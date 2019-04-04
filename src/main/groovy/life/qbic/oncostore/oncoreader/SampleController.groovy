package life.qbic.oncostore.oncoreader

import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import life.qbic.oncostore.model.Sample
import life.qbic.oncostore.util.IdValidator
import life.qbic.oncostore.util.ListingArguments

import javax.inject.Inject
import javax.validation.Valid

@Controller("/samples")
class SampleController {

    private final SampleReader sampleReader
    //private final VariantReader variantReader

    /*
    @Inject SampleController(SampleReader sampleReader, VariantReader variantReader) {
        this.sampleReader = sampleReader
        this.variantReader = variantReader
    }
    */

    @Inject SampleController(SampleReader sampleReader) {
        this.sampleReader = sampleReader
    }

    /**
     *
     * @param identifier The sample identifier
     * @return The found sample
     */
    @Get(uri = "/{id}", produces = MediaType.APPLICATION_JSON)
    HttpResponse getSample(@Parameter('id') String identifier){
        if(!IdValidator.isValidSampleCode(identifier))
        {
            return HttpResponse.badRequest("Invalid sample identifier supplied.")
        } else {
            Sample s = sampleReader.searchSample(identifier)
            if(s!=null) {
                return HttpResponse.ok(s)
            } else {
                return HttpResponse.notFound("Sample not found.")
            }
        }
    }

    /**
     *
     * @param args The filter arguments
     * @return The found samples
     */
    @Get(uri = "{?args*}", produces = MediaType.APPLICATION_JSON)
    HttpResponse getSamples(@Valid ListingArguments args){
        List<Sample> s = sampleReader.searchSamples(args)
        if(!s.empty) {
            return HttpResponse.ok(s)
        } else {
            return HttpResponse.notFound("No samples found.")
        }
    }

    /*
    @Get(uri = "{id}/variants", produces = MediaType.APPLICATION_JSON)
    HttpResponse getVariantsOfSamples(@Parameter('id') List<String> identifiers) {
        if (identifiers.any { id -> !IdValidator.isValidSampleCode(identifier) }) {
            return HttpResponse.badRequest("Invalid sample identifier supplied.")
        } else {
            Sample s = sampleReader.searchSample(identifier)
            if (!s.empty) {
                return HttpResponse.ok(s)
            } else {
                return HttpResponse.notFound("No variants found for supplied sample identifiers.")
            }
        }
    }
    */
}