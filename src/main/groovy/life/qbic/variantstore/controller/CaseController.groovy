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
import life.qbic.variantstore.model.Case
import life.qbic.variantstore.model.Sample
import life.qbic.variantstore.service.VariantstoreService
import life.qbic.variantstore.util.ListingArguments
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Controller for case (patient) requests
 *
 * This handles requests that try to retrieve information on patients from the store. Requests can thereby be made by
 * providing patient identifiers or by providing filtering criteria such as variant information. Therefore, questions
 * could be answered such as "Which patient has variant X on gene Y ?".
 *
 * @since: 1.0.0
 */
@Controller("/cases")
@Secured(SecurityRule.IS_AUTHENTICATED)
class CaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaseController.class);

    /**
     * The variantstore service
     */
    private final VariantstoreService service

    @Inject
    CaseController(VariantstoreService service) {
        this.service = service
    }

    /**
     * Retrieve case by identifier
     * @param identifier the case identifier
     * @return the found case or 404 Not Found
     */
    @TransactionalAdvice('${database.specifier}')
    @Get(uri = "/{id}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Request a case",
            description = "The case with the specified identifier is returned.",
            tags = "Case")
    @ApiResponse(responseCode = "200", description = "Returns a case", content = @Content(
            mediaType = "application/json", schema = @Schema(implementation = Sample.class)))
    @ApiResponse(responseCode = "400", description = "Invalid case identifier supplied")
    @ApiResponse(responseCode = "404", description = "Case not found")
    HttpResponse getCase(@PathVariable(name = "id") String identifier) {
        LOGGER.info("Resource request for case: $identifier")
        try {
            List<Case> cases = service.getCaseForCaseId(identifier)
            JsonError error = new JsonError("No case found for given identifier $identifier.")

            return cases ? HttpResponse.ok(cases) : HttpResponse.notFound(error)
        }

        catch (IllegalArgumentException e) {
            LOGGER.error(e)
            return HttpResponse.badRequest("Invalid case identifier supplied.")
        }

        catch (Exception e) {
            LOGGER.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.")
        }
    }


    /**
     * Retrieve case based on filtering options
     * @param args the filter arguments
     * @return The found cases or 404 Not Found
     */
    @TransactionalAdvice('${database.specifier}')
    @Get(uri = "{?args*}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Request a set of cases",
            description = "The cases matching the supplied properties are returned.",
            tags = "Case")
    @ApiResponse(responseCode = "200", description = "Returns a set of cases", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Case.class)))
    @ApiResponse(responseCode = "404", description = "No cases found matching provided attributes")
    HttpResponse getCases(ListingArguments args) {
        LOGGER.info("Resource request for cases with filtering options.")
        try {
            List<Case> cases = service.getCasesForSpecifiedProperties(args)
            JsonError error = new JsonError("No cases found for provided attributes.")
            return cases ? HttpResponse.ok(cases) : HttpResponse.notFound(error)
        } catch (Exception e) {
            LOGGER.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.")
        }
    }
}
