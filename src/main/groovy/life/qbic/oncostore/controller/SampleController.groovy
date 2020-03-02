package life.qbic.oncostore.controller

import groovy.util.logging.Log4j2
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import life.qbic.oncostore.model.Sample
import life.qbic.oncostore.service.VariantstoreService
import life.qbic.oncostore.util.ListingArguments

import javax.inject.Inject

@Log4j2
@Controller("/samples")
@Secured(SecurityRule.IS_AUTHENTICATED)
class SampleController {

    private final VariantstoreService service

    @Inject SampleController(VariantstoreService service) {
        this.service = service
    }

    /**
     *
     * @param identifier The sample identifier
     * @return The found sample
     */
    @Get(uri = "/{id}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Request a sample",
            description = "The sample with the specified identifier is returned.",
            tags = "Sample")
    @ApiResponse(
            responseCode = "200", description = "Returns a sample", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Sample.class)))
    @ApiResponse(responseCode = "400", description = "Invalid sample identifier supplied")
    @ApiResponse(responseCode = "404", description = "Sample not found")
    HttpResponse getSample(@PathVariable(name="id") String identifier) {
        log.info("Resource request for sample: $identifier")
        try {
            List<Sample> samples = service.getSampleForSampleId(identifier)
            return samples ? HttpResponse.ok(samples.get(0)) : HttpResponse.notFound("Sample not found.")
        }
        catch (IllegalArgumentException e) {
            log.error(e)
            return HttpResponse.badRequest("Invalid sample identifier supplied.")
        }
        catch (Exception e) {
            log.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.")
        }
    }

    /**
     *
     * @param args The filter arguments
     * @return The found samples
     */
    @Get(uri = "{?args*}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Request a set of samples",
            description = "The samples matching the supplied properties are returned.",
            tags = "Sample")
    @ApiResponse(responseCode = "200", description = "Returns a set of samples", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Sample.class)))
    @ApiResponse(responseCode = "400", description = "Invalid sample identifier supplied")
    @ApiResponse(responseCode = "404", description = "No samples found matching provided attributes")
    HttpResponse getSamples(ListingArguments args){
        log.info("Resource request for samples with filtering options.")
        try {
            List<Sample> samples = service.getSamplesForSpecifiedProperties(args)
            return samples ? HttpResponse.ok(samples) : HttpResponse.notFound("No samples found matching provided attributes.")
        }
        catch (Exception e) {
            log.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.")
        }
    }
}