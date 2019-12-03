package life.qbic.oncostore.controller

import groovy.util.logging.Log4j2
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameters
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import life.qbic.oncostore.model.Variant
import life.qbic.oncostore.service.OncostoreService
import life.qbic.oncostore.util.ListingArguments

import javax.annotation.Nullable
import javax.inject.Inject
import javax.validation.Valid
import javax.validation.constraints.NotNull

@Log4j2
@Controller("/variants")
@Secured(SecurityRule.IS_ANONYMOUS)
class VariantController {

    private final OncostoreService service

    @Inject
    VariantController(OncostoreService service) {
        this.service = service
        //this.executor = Executors.newFixedThreadPool(1);
    }

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Get(uri = "/{id}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Request a variant",
            description = "The variant with the specified identifier is returned.",
            tags = "Variant")
    @ApiResponse(
            responseCode = "200", description = "Returns a variant", content = [@Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Variant.class))])
    @ApiResponse(responseCode = "400", description = "Invalid variant identifier supplied")
    @ApiResponse(responseCode = "404", description = "Variant not found")
    HttpResponse<Variant> getVariant(@PathVariable(name="id") String identifier) {
        log.info("Resource request for variant: $identifier")
        try {
            List<Variant> variants = service.getVariantForVariantId(identifier)
            return variants ? HttpResponse.ok(variants.get(0)) : HttpResponse.notFound("Variant not found.")
        }
        catch (IllegalArgumentException e) {
            log.error(e)
            return HttpResponse.badRequest("Invalid variant identifier supplied.")
        }
        catch (Exception e) {
            log.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.")
        }
    }

    @Get(uri = "{?args*}", produces = [MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN])
    @Operation(summary = "Request a set of variants",
            description = "The variants matching the supplied properties are returned.",
            tags = "Variant")
    @ApiResponse(responseCode = "200", description = "Returns a set of variants", content = [@Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Variant.class, type = "object"))])
    @ApiResponse(responseCode = "400", description = "Invalid variant identifier supplied")
    @ApiResponse(responseCode = "404", description = "Variant not found")
    HttpResponse<List<Variant>> getVariants(@Nullable @Valid ListingArguments args) {
        log.info("Resource request for variants with filtering options.")
        try {
            List<Variant> variants = service.getVariantsForSpecifiedProperties(args)
            //@TODO provide option to get output in VCF format
            //if (args.format.get() & args.format.get() == "VCF") {
            //    return  variants ? HttpResponse.ok(service.getVcfContentForVariants(variants)).header("Content-Disposition", "attachment; filename=test.jpg").contentType(MediaType.TEXT_PLAIN_TYPE) : HttpResponse.notFound("No variants found matching provided attributes.")
            //}

            return variants ? HttpResponse.ok(variants) : HttpResponse.notFound("No variants found matching provided attributes.")
        }
        catch (Exception e) {
            log.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.")
        }
    }

    @Post(uri = "/upload", consumes = MediaType.TEXT_PLAIN)
    @Operation(summary = "Add variants to the store",
            description = "Upload an annotated VCF file and store the contained variants.",
            tags = "Variant")
    HttpResponse storeVariants(@QueryValue("url") @NotNull String url) {
        try {
            log.info("Request for storing variant information.")
            service.storeVariantsInStore(url)
            //executor.submit(new MyRunnable(service, url));

            /*
            if (executor instanceof ThreadPoolExecutor) {
                System.out.println(
                        "Pool size is now " +
                                ((ThreadPoolExecutor) executor).getActiveCount()
                );
            }
            */

            //return HttpResponse.accepted()

            return HttpResponse.ok()
        }
        catch (Exception e) {
            log.error(e)
        }
    }
}
/*
public class MyRunnable implements Runnable {
    private final String url;
    private final OncostoreService service

    MyRunnable(OncostoreService service, String url) {
        this.service = service;
        this.url = url;
    }

    @Override
    public void run() {
        service.storeVariantsInStore(url)
    }
}
*/