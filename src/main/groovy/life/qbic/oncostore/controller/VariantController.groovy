package life.qbic.oncostore.controller

import groovy.util.logging.Log4j2
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.multipart.CompletedFileUpload
import io.micronaut.http.uri.UriBuilder
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import life.qbic.oncostore.model.*
import life.qbic.oncostore.parser.SimpleVCFReader
import life.qbic.oncostore.service.VariantstoreService
import life.qbic.oncostore.util.IdValidator
import life.qbic.oncostore.util.ListingArguments

import javax.annotation.Nullable
import javax.inject.Inject
import javax.inject.Named
import java.util.concurrent.ExecutorService

@Log4j2
@Controller("/variants")
@Secured(SecurityRule.IS_AUTHENTICATED)
class VariantController {

    private final VariantstoreService service

    @Inject
    VariantController(VariantstoreService service) {
        this.service = service
    }

    @Inject
    @Named(TaskExecutors.IO)
    ExecutorService ioExecutorService

    @Inject
    EmbeddedServer server

    @Inject
    TransactionStatusRepository repository

    @Get(uri = "/{id}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Request a variant",
            description = "The variant with the specified identifier is returned.",
            tags = "Variant")
    @ApiResponse(responseCode = "200", description = "Returns a variant", content = @Content(mediaType =
            "application/json",

            schema = @Schema(implementation = Variant.class)))
    @ApiResponse(responseCode = "400", description = "Invalid variant identifier supplied")
    @ApiResponse(responseCode = "404", description = "Variant not found")
    HttpResponse getVariant(@PathVariable(name = "id") String identifier) {
        log.info("Resource request for variant: $identifier")
        try {
            List<Variant> variants = service.getVariantForVariantId(identifier)
            return variants ? HttpResponse.ok(variants.get(0)) : HttpResponse.notFound("No Variant found for given "
                    + "identifier.")
        } catch (IllegalArgumentException e) {
            log.error(e)
            return HttpResponse.badRequest("Invalid variant identifier supplied.")
        } catch (Exception e) {
            log.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.")
        }
    }

    @Get(uri = "{?args*}", produces = [MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN])
    @Operation(summary = "Request a set of variants",
            description = "The variants matching the supplied properties are returned.",
            tags = "Variant")
    @ApiResponse(responseCode = "200", description = "Returns a set of variants", content = @Content(mediaType =
            "application/json",
            schema = @Schema(implementation = Variant.class, type = "object")))
    @ApiResponse(responseCode = "400", description = "Invalid variant identifier supplied")
    @ApiResponse(responseCode = "404", description = "Variant not found")
    HttpResponse<List<Variant>> getVariants(@Nullable ListingArguments args, @Nullable String format, @QueryValue
            (defaultValue = "GRCh37") @Nullable String referenceGenome, @QueryValue(defaultValue = "false") @Nullable
            Boolean withConsequences, @QueryValue(defaultValue = "snpeff") @Nullable String annotationSoftware,
                                            @QueryValue(defaultValue = "false") @Nullable Boolean withGenotypes) {
        log.info("Resource request for variants with filtering options.")
        try {
            //@TODO add option to get genotype information in exported VCF file
            def variants
            if (format) {
                if (!IdValidator.isSupportedVariantFormat(format)) {
                    return HttpResponse.badRequest("Invalid export format specified.") as HttpResponse<List<Variant>>
                }
                variants = service.getVariantsForSpecifiedProperties(args, referenceGenome,
                        withConsequences, annotationSoftware, true, withGenotypes)
                def time = new Date().format("yyyy-MM-dd_HH-mm")

                if (format == IdValidator.VariantFormats.VCF.toString()) {
                    return variants ? HttpResponse.ok(service.getVcfContentForVariants(variants, withConsequences,
                            referenceGenome, annotationSoftware))
                            .header("Content-Disposition", "attachment; filename=variantstore_export_${time}.vcf")
                            .contentType(MediaType.TEXT_PLAIN_TYPE) : HttpResponse.notFound("No variants found " +
                            "matching " + "provided attributes.") as HttpResponse<List<Variant>>
                } else {
                    return variants ? HttpResponse.ok(service.getFhirContentForVariants(variants, withConsequences,
                            referenceGenome))
                            .header("Content-Disposition", "attachment; filename=variantstore_export_${time}.json")
                            .contentType(MediaType.TEXT_PLAIN_TYPE) : HttpResponse.notFound("No variants found " +
                            "matching " + "provided attributes.") as HttpResponse<List<Variant>>
                }
            }

            variants = service.getVariantsForSpecifiedProperties(args, referenceGenome,
                    withConsequences, annotationSoftware, false, withGenotypes)
            return variants ? HttpResponse.ok(variants) : HttpResponse.notFound("No variants found matching provided " + "" + "" + "" + "attributes.") as HttpResponse<List<Variant>>
        }

        catch (Exception e) {
            log.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.") as
                    HttpResponse<List<Variant>>
        }
    }

    @Operation(summary = "Add variants to the store",
            description = "Upload annotated VCF file(s) and store the contained variants.",
            tags = "Variant")
    @Post(uri = "/", consumes = MediaType.MULTIPART_FORM_DATA)
    HttpResponse storeVariants(String metadata, Flowable<CompletedFileUpload> files) {
        try {
            log.info("Request for storing variant information.")
            def statusId = UUID.randomUUID().toString()

            // build location for response
            def uri = UriBuilder.of("${server.getURI()}/variants/upload/status/${statusId}").build()

            List<SimpleVariantContext> variantsToAdd = []
            files.subscribeOn(Schedulers.from(ioExecutorService))
                    .subscribe { file ->
                        variantsToAdd = []
                        SimpleVCFReader reader = new SimpleVCFReader(file.inputStream)
                        reader.iterator().each { variant -> variantsToAdd.add(variant)
                        }

                        def newStatus = new TransactionStatus().tap {
                            uuid = statusId
                            fileName = file.filename
                            fileSize = file.size
                            status = life.qbic.oncostore.model.Status.processing
                        }
                        def test = repository.save(newStatus)

                        service.storeVariantsInStore(metadata, variantsToAdd)
                        repository.updateStatus(test.getId(), life.qbic.oncostore.model.Status.finished.toString())
                    }
            return HttpResponse.accepted(uri)
        } catch (IOException exception) {
            log.error(exception)
            return HttpResponse.badRequest("Upload of variants failed.");
        }
    }

    @Get(uri = "/upload/status/{id}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Request the variant upload status",
            description = "The status for the requested variant upload is shown.",
            tags = "Variant")
    @ApiResponse(responseCode = "200", description = "Returns a status", content = @Content(mediaType =
            "application/json"))
    @ApiResponse(responseCode = "400", description = "Invalid upload identifier supplied")
    @ApiResponse(responseCode = "404", description = "Upload not found")
    HttpResponse getUploadStatus(@PathVariable(name = "id") String identifier) {
        try {
            return HttpResponse.ok(repository.findAllByUuid(identifier))
        } catch (IllegalArgumentException e) {
            log.error(e)
            return HttpResponse.badRequest("Invalid upload identifier supplied.")
        } catch (Exception e) {
            log.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.")
        }
    }
}
