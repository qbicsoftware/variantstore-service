package life.qbic.oncostore.controller

import groovy.util.logging.Log4j2
import io.micronaut.context.annotation.Parameter
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
import life.qbic.oncostore.model.Case
import life.qbic.oncostore.model.Gene
import life.qbic.oncostore.model.Sample
import life.qbic.oncostore.service.OncostoreService
import life.qbic.oncostore.util.ListingArguments

import javax.inject.Inject
import javax.validation.Valid

@Log4j2
@Controller("/cases")
@Secured(SecurityRule.IS_ANONYMOUS)
class CaseController {
    private final OncostoreService service

    @Inject
    CaseController(OncostoreService service) {
        this.service = service
    }

    /**
     *
     * @param identifier The case identifier
     * @return The found case
     */
    @Get(uri = "/{id}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Request a case",
            description = "The case with the specified identifier is returned.",
            tags = "Case")
    @ApiResponse(
            responseCode = "200", description = "Returns a case", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Sample.class)))
    @ApiResponse(responseCode = "400", description = "Invalid case identifier supplied")
    @ApiResponse(responseCode = "404", description = "Case not found")
    HttpResponse getCase(@PathVariable(name="id") String identifier) {
        log.info("Resource request for case: $identifier")
        try {
            List<Case> cases = service.getCaseForCaseId(identifier)
            return cases ? HttpResponse.ok(cases.get(0)) : HttpResponse.notFound("Case not found.")
        }
        catch (IllegalArgumentException e) {
            log.error(e)
            return HttpResponse.badRequest("Invalid case identifier supplied.")
        }
        catch (Exception e) {
            log.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.")
        }
    }


    /**
     *
     * @param args The filter arguments
     * @return The found cases
     */
    @Get(uri = "{?args*}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Request a set of cases",
            description = "The cases matching the supplied properties are returned.",
            tags = "Case")
    @ApiResponse(responseCode = "200", description = "Returns a set of cases", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Case.class)))
    @ApiResponse(responseCode = "404", description = "No cases found matching provided attributes")
    HttpResponse getCases(ListingArguments args) {
        log.info("Resource request for cases with filtering options.")
        try {
            List<Case> cases = service.getCasesForSpecifiedProperties(args)
            return cases ? HttpResponse.ok(cases) : HttpResponse.notFound("No cases found matching provided attributes.")
        }
        catch (Exception e) {
            log.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.")
        }
    }
}
