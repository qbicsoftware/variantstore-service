package life.qbic.oncostore.controller

import groovy.util.logging.Log4j2
import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import life.qbic.oncostore.model.Case
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
    HttpResponse getCase(@Parameter('id') String identifier) {
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
    HttpResponse getCases(@Valid ListingArguments args) {
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
