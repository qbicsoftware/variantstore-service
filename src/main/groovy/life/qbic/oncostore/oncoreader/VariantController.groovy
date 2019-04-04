package life.qbic.oncostore.oncoreader

import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import life.qbic.oncostore.model.Variant
import life.qbic.oncostore.util.IdValidator
import life.qbic.oncostore.util.ListingArguments

import javax.inject.Inject
import javax.validation.Valid

@Controller("/variants")
class VariantController {

    private final VariantReader variantReader

    @Inject
    VariantController(VariantReader variantReader) {
        this.variantReader = variantReader
    }

    @Get(uri = "/{id}", produces = MediaType.APPLICATION_JSON)
    HttpResponse getVariant(@Parameter('id') String identifier) {
        if (!IdValidator.isValidUUID(identifier)) {
            return HttpResponse.badRequest("Invalid variant identifier supplied.")
        } else {
            Variant s = variantReader.searchVariant(identifier)
            if (s != null) {
                return HttpResponse.ok(s)
            } else {
                return HttpResponse.notFound("Variant not found.")
            }
        }
    }

    @Get(uri = "{?args*}", produces = MediaType.APPLICATION_JSON)
    HttpResponse getVariants(@Valid ListingArguments args){
        List<Variant> s = variantReader.searchVariants(args)
        if(!s.empty) {
            return HttpResponse.ok(s)
        } else {
            return HttpResponse.notFound("No variants found.")
        }
    }
}