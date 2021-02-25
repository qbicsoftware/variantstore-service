package life.qbic.variantstore.controller

import groovy.util.logging.Log4j2
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import life.qbic.variantstore.model.BeaconAlleleResponse
import life.qbic.variantstore.service.VariantstoreService
import javax.inject.Inject
import javax.validation.constraints.Pattern
import javax.validation.constraints.PositiveOrZero

/**
 * Controller for Beacon requests
 *
 * Answers the question: "Have you observed this genotype?"
 * A GA4GH Beacon based on the v0.4 specification. Given a position in a chromosome and an alllele, the beacon looks
 * for matching mutations at that location and returns a response accordingly.
 *
 * @since: 1.0.0
 */
@Log4j2
@Controller("/beacon")
@Secured(SecurityRule.IS_ANONYMOUS)
class BeaconController {

    /**
     * The variantstore service
     */
    private final VariantstoreService service

    @Inject
    BeaconController(VariantstoreService service) {
        this.service = service
    }

    @Get(uri = "/query", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Query the Beacon",
            description = "Answers the question: \"Have you observed this genotype?\"",
            tags = "Beacon")
    @ApiResponse(responseCode = "200", description = "Returns the answer to the specified question", content =
            @Content(schema = @Schema(implementation = BeaconAlleleResponse.class)))
    HttpResponse<BeaconAlleleResponse> checkVariant(@Pattern(regexp = '[1-9]|1[0-9]|2[0-3]|X|Y') @QueryValue String
                                                            chromosome, @PositiveOrZero @QueryValue BigInteger
            startPosition,
                                                    @Pattern(regexp = '[ACTG]+') @QueryValue String reference,
                                                    @Pattern(regexp = '[ACTG]+') @QueryValue String observed,
                                                    @QueryValue String assemblyId) {
        log.info("Beacon request for specified variant.")
        try {
            BeaconAlleleResponse response = service.getBeaconAlleleResponse(chromosome, startPosition, reference,
                    observed, assemblyId)
            return HttpResponse.ok(response)
        }
        catch (Exception e) {
            //@TODO check for 400 ? (bad request), missing mandatory parameters
            log.error(e)
            return HttpResponse.serverError()
        }
    }
}