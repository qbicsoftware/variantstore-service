package life.qbic.oncostore.controller

import groovy.util.logging.Log4j2
import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import life.qbic.oncostore.model.Case
import life.qbic.oncostore.service.OncostoreService
import life.qbic.oncostore.util.ListingArguments

import javax.inject.Inject
import javax.validation.Valid

@Log4j2
@Controller("/cases")
class CaseController {
    private final OncostoreService service

    @Inject
    CaseController(OncostoreService service) {
        this.service = service
    }

    @Get(uri = "/{id}", produces = MediaType.APPLICATION_JSON)
    HttpResponse getCase(@Parameter('id') String identifier) {
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


    @Get(uri = "{?args*}", produces = MediaType.APPLICATION_JSON)
    HttpResponse getCases(@Valid ListingArguments args) {
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
