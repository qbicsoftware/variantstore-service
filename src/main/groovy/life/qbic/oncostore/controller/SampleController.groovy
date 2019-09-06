package life.qbic.oncostore.controller

import groovy.util.logging.Log4j2
import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import life.qbic.oncostore.model.Sample
import life.qbic.oncostore.service.OncostoreService
import life.qbic.oncostore.util.ListingArguments

import javax.inject.Inject
import javax.validation.Valid

@Log4j2
@Controller("/samples")
class SampleController {

    private final OncostoreService service

    @Inject SampleController(OncostoreService service) {
        this.service = service
    }

    /**
     *
     * @param identifier The sample identifier
     * @return The found sample
     */
    @Get(uri = "/{id}", produces = MediaType.APPLICATION_JSON)
    HttpResponse getSample(@Parameter('id') String identifier) {
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
    HttpResponse getSamples(@Valid ListingArguments args){
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