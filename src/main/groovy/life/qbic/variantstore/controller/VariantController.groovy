package life.qbic.variantstore.controller

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
import io.micronaut.core.annotation.Nullable
import io.micronaut.transaction.annotation.TransactionalAdvice
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import life.qbic.variantstore.model.TransactionStatus
import life.qbic.variantstore.repositories.TransactionStatusRepository
import life.qbic.variantstore.model.Variant
import life.qbic.variantstore.service.VariantstoreService
import life.qbic.variantstore.util.IdValidator
import life.qbic.variantstore.util.ListingArguments
import jakarta.inject.Inject
import jakarta.inject.Named
import java.util.concurrent.ExecutorService

/**
 * Controller for variant requests.
 *
 * This handles requests that try to retrieve information on variants from the store.
 *
 * @since: 1.0.0
 */
@Log4j2
@Controller("/variants")
@Secured(SecurityRule.IS_AUTHENTICATED)
class VariantController {

    /**
     * The Variantstore service instance
     */
    final VariantstoreService service
    /**
     * The executor service instance
     */
    @Inject
    @Named(TaskExecutors.IO)
    ExecutorService ioExecutorService

    /**
     * The embedded server instance
     */
    @Inject
    EmbeddedServer server
    /**
     * The repository instance to track the transaction status during variant import
     */
    @Inject
    TransactionStatusRepository repository

    @Inject
    VariantController(VariantstoreService service) {
        this.service = service
    }

    /**
     * Retrieve variant by identifier
     * @param identifier the variant identifier
     * @return the found variant or 404 Not Found
     */
    @TransactionalAdvice('${database.specifier}')
    @Get(uri = "/{id}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Request a variant",
            description = "The variant with the specified identifier is returned.",
            tags = "Variant")
    @ApiResponse(responseCode = "200", description = "Returns a variant", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Variant.class)))
    @ApiResponse(responseCode = "400", description = "Invalid variant identifier supplied")
    @ApiResponse(responseCode = "404", description = "Variant not found")
    HttpResponse getVariant(@PathVariable(name = "id") String identifier) {
        log.info("Resource request for variant: $identifier")
        try {
            Set<Variant> variants = service.getVariantForVariantId(identifier) as Set<Variant>
            return variants ? HttpResponse.ok(variants[0]) : HttpResponse.notFound("No Variant found for given "
                    + "identifier.").body("")
        } catch (IllegalArgumentException e) {
            log.error(e)
            return HttpResponse.badRequest("Invalid variant identifier supplied.")
        } catch (Exception e) {
            log.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.")
        }
    }

    /**
     * Retrieve variants by providing filtering options
     * @param args the filtering arguments
     * @param format the format in which variants should be returned (JSON (default)/VCF/FHIR)
     * @param referenceGenome the reference genome
     * @param withConsequences true if variants should be returned with consequences
     * @param annotationSoftware the annotation software
     * @param withGenotypes true if variants should be returned with connected genotype information
     * @param vcfVersion the Variant Call Format version
     * @return the found variants or 404 Not Found
     */
    @TransactionalAdvice('${database.specifier}')
    @Get(uri = "{?args*}{?format,referenceGenome,withConsequences,annotationSoftware,withGenotypes,vcfVersion}", produces = [MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN])
    @Operation(summary = "Request a set of variats",
            description = "The variants matching the supplied properties are returned.",
            tags = "Variant")
    @ApiResponse(responseCode = "200", description = "Returns a set of variants", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Variant.class, type = "object")))
    @ApiResponse(responseCode = "400", description = "Invalid variant identifier supplied")
    @ApiResponse(responseCode = "404", description = "Variant not found")
    HttpResponse<Set<Variant>> getVariants(@Nullable ListingArguments args, @QueryValue @Nullable String format,
                                           @QueryValue(defaultValue = "GRCh37") @Nullable String referenceGenome,
                                           @QueryValue(defaultValue = "false") @Nullable Boolean withConsequences,
                                           @QueryValue(defaultValue = "snpeff") @Nullable String annotationSoftware,
                                           @QueryValue(defaultValue = "4.3t") @Nullable String annotationSoftwareVersion,
                                           @QueryValue(defaultValue = "false") @Nullable Boolean withVcfInfo,
                                           @QueryValue(defaultValue = "false") @Nullable Boolean withGenotypes,
                                           @QueryValue(defaultValue = "4.1") @Nullable String vcfVersion) {
        log.info("Resource request for variants with filtering options.")
        try {
            def variants
            if (format) {
                if (!IdValidator.isSupportedVariantFormat(format)) {
                    return HttpResponse.badRequest("Invalid export format specified.") as HttpResponse<Set<Variant>>
                }
                variants = service.getVariantsForSpecifiedProperties(args, referenceGenome, withConsequences,
                        annotationSoftware, withVcfInfo, withGenotypes)
                def time = new Date().format("yyyy-MM-dd_HH-mm")

                if (format.toUpperCase() == IdValidator.VariantFormats.VCF.toString()) {
                    return variants ? HttpResponse.ok(service.getVcfContentForVariants(variants, withConsequences, withGenotypes,
                            referenceGenome, annotationSoftware, annotationSoftwareVersion, vcfVersion))
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

    /**
     * Store provided variants in the store.
     * @param metadata the provided metadata
     * @param files the provided VCF (or VCF.gz) files containing variants
     * @return 202 Accepted
     */
    @TransactionalAdvice('${database.specifier}')
    @Operation(summary = "Add variants to the store",
            description = "Upload annotated VCF file(s) and store the contained variants.",
            tags = "Variant")
    @Post(uri = "/", consumes = MediaType.MULTIPART_FORM_DATA)
    HttpResponse storeVariants(String metadata, Flowable<CompletedFileUpload> files) {
        try {
            log.info("Request for storing variant information.")
            def statusId = UUID.randomUUID().toString()

            // build location for response
            def uri = UriBuilder.of("/variants/upload/status/${statusId}").build()

            files.subscribeOn(Schedulers.from(ioExecutorService)).doOnError { throwable -> log.error("Upload of variants failed.") }
                    .subscribe() { file ->
                        log.info("Processing file ${file.filename}")

                        def newStatus = new TransactionStatus().tap {
                            identifier = statusId
                            fileName = file.filename
                            fileSize = file.size
                            status = life.qbic.variantstore.model.Status.processing
                        }

                        TransactionStatus transactionStatus = repository.save(newStatus)
                        service.storeVariantsInStore(metadata, file.inputStream, repository, transactionStatus)
                    }
            return HttpResponse.accepted(uri)
        } catch (IOException exception) {
            log.error(exception)
            return HttpResponse.badRequest("Upload of variants failed.");
        }
        finally {
            Schedulers.shutdown()
            Runtime.getRuntime().gc()
        }
    }

    /**
     * Retrieve transaction status by identifier.
     * @param identifier the transaction identifier
     * @return the transaction status
     */
    @TransactionalAdvice('${database.specifier}')
    @Get(uri = "/upload/status/{id}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Request the variant upload status",
            description = "The status for the requested variant upload is shown.",
            tags = "Variant")
    @ApiResponse(responseCode = "200", description = "Returns a status", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Invalid upload identifier supplied")
    @ApiResponse(responseCode = "404", description = "Upload not found")
    HttpResponse getUploadStatus(@PathVariable(name = "id") String identifier) {
        try {
            def searchResult= repository.findByIdentifier(identifier)
            return searchResult.present ? HttpResponse.ok(searchResult.get()) : HttpResponse.notFound("No transaction found for given "
                    + "uuid.").body("")
        } catch (IllegalArgumentException e) {
            log.error(e)
            return HttpResponse.badRequest("Invalid upload identifier supplied.")
        } catch (Exception e) {
            log.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.")
        }
    }

    @Get(uri = "/test", produces = MediaType.APPLICATION_JSON)
    HttpResponse getTest() {
        try {
            repository.save(new TransactionStatus())
            return HttpResponse.ok("Just a test")
        } catch (IllegalArgumentException e) {
            log.error(e)
            return HttpResponse.badRequest("Invalid variant identifier supplied.")
        } catch (Exception e) {
            log.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.")
        }
    }
}
