package life.qbic.oncostore.oncoreader

import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import life.qbic.oncostore.model.BeaconAlleleRequest
import life.qbic.oncostore.model.BeaconAlleleResponse
import life.qbic.oncostore.model.Variant
import life.qbic.oncostore.util.IdValidator
import life.qbic.oncostore.util.ListingArguments

import javax.annotation.Nullable
import javax.inject.Inject
import javax.validation.Valid
import javax.validation.constraints.Pattern
import javax.validation.constraints.NotNull
import javax.validation.constraints.PositiveOrZero

/*
Answers the question: "Have you observed this genotype?"
A GA4GH Beacon based on the v0.4 specification. Given a position in a chromosome and an alllele, the beacon looks for matching mutations at that location and returns a response accordingly.
 */
@Controller("/beacon")
class BeaconController {

    private final VariantReader variantReader

    @Inject
    BeaconController(VariantReader variantReader) {
        this.variantReader = variantReader
    }

    @Get(uri = "/query{?args}", produces = MediaType.APPLICATION_JSON)
    HttpResponse checkVariant(@NotNull @Pattern(regexp = "[1-22]|X|Y") String chromosome, @NotNull @PositiveOrZero Integer startPosition,
                             @NotNull @Pattern(regexp = "[ACTG]+") String reference, @NotNull @Pattern(regexp = "[ACTG]+") String observed, @NotNull String assemblyId, @Nullable ListingArguments args){

        List<Variant> s = variantReader.searchVariantForBeaconReponse(chromosome, startPosition, reference, observed, assemblyId, args)

        def exists = !s.empty
        BeaconAlleleRequest request = new BeaconAlleleRequest()
        request.setAlternateBases(observed)
        request.setAssemblyId(assemblyId)
        request.setReferenceBases(reference)
        request.setReferenceName(chromosome)
        request.setStart(startPosition)

        BeaconAlleleResponse response = new BeaconAlleleResponse()
        response.setAlleleRequest(request)
        response.setExists(exists)
        return HttpResponse.ok(response)

        // check for 400 ?
        // we have to implement 401 and 403 here as well when we're dealing with permissions
    }
}
