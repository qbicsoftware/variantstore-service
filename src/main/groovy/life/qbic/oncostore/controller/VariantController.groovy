package life.qbic.oncostore.controller

import groovy.util.logging.Log4j2
import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import life.qbic.oncostore.model.Variant
import life.qbic.oncostore.service.OncostoreService
import life.qbic.oncostore.util.ListingArguments

import javax.annotation.Nullable
import javax.inject.Inject
import javax.validation.Valid
import javax.validation.constraints.NotNull

@Log4j2
@Controller("/variants")
class VariantController {

    private final OncostoreService service

    @Inject
    VariantController(OncostoreService service) {
        this.service = service
    }

    @Get(uri = "/{id}", produces = MediaType.APPLICATION_JSON)
    HttpResponse getVariant(@Parameter('id') String identifier) {
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


    @Get(uri = "{?args*}", produces = MediaType.APPLICATION_JSON)
    HttpResponse getVariants(@Valid ListingArguments args) {
        try {
            List<Variant> variants = service.getVariantForVariantId(identifier)
            return variants ? HttpResponse.ok(variants.get(0)) : HttpResponse.notFound("No variants found matching provided attributes..")
        }
        catch (Exception e) {
            log.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.")
        }
    }

    @Post(uri = "/upload", consumes = MediaType.TEXT_PLAIN)
    HttpResponse storeVariants(@QueryValue("url") @NotNull String url) {
        println(url)
        try {
            service.storeVariantsInStore(url)
            return HttpResponse.ok()
        }
        catch (Exception e) {
            log.error(e)
        }
    }
}