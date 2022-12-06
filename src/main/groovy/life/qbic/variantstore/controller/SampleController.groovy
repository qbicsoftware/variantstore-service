package life.qbic.variantstore.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.hateoas.JsonError
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.transaction.annotation.TransactionalAdvice
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import life.qbic.variantstore.model.Sample
import life.qbic.variantstore.service.VariantstoreService
import life.qbic.variantstore.util.ListingArguments
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Controller for samples requests
 *
 * This handles requests that try to retrieve information on samples from the store.
 *
 * @since: 1.0.0
 */
@Controller("/samples")
@Secured(SecurityRule.IS_AUTHENTICATED)
class SampleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleController.class);

    /**
     * The variantstore service
     */
    private final VariantstoreService service

    @Inject
    SampleController(VariantstoreService service) {
        this.service = service
    }

    /**
     * Retrieve sample by identifier
     * @param identifier the sample identifier
     * @return the found sample or 404 Not Found
     */
    @TransactionalAdvice('${database.specifier}')
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
        LOGGER.info("Resource request for sample: $identifier")
        try {
            List<Sample> samples = service.getSampleForSampleId(identifier)
            JsonError error = new JsonError("No sample found with provided identifier $identifier.")
            return samples ? HttpResponse.ok(samples.get(0)) : HttpResponse.notFound(error)
        }
        catch (IllegalArgumentException e) {
            LOGGER.error(e)
            return HttpResponse.badRequest("Invalid sample identifier supplied.")
        }
        catch (Exception e) {
            LOGGER.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.")
        }
    }

    /**
     * Retrieve samples based on filtering criteria
     * @param args the filter arguments
     * @return the found samples or 404 Not Found
     */
    @TransactionalAdvice('${database.specifier}')
    @Get(uri = "{?args*}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Request a set of samples",
            description = "The samples matching the supplied properties are returned.",
            tags = "Sample")
    @ApiResponse(responseCode = "200", description = "Returns a set of samples", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Sample.class)))
    @ApiResponse(responseCode = "404", description = "No samples found matching provided attributes")
    HttpResponse getSamples(ListingArguments args){
        LOGGER.info("Resource request for samples with filtering options.")
        try {
            List<Sample> samples = service.getSamplesForSpecifiedProperties(args)
            JsonError error = new JsonError("No samples found matching provided attributes.")
            return samples ? HttpResponse.ok(samples) : HttpResponse.notFound(error)
        }
        catch (Exception e) {
            LOGGER.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.")
        }
    }
}
