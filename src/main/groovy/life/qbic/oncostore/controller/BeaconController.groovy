package life.qbic.oncostore.controller

import groovy.util.logging.Log4j2
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import life.qbic.oncostore.model.BeaconAlleleResponse
import life.qbic.oncostore.service.OncostoreService
import life.qbic.oncostore.util.ListingArguments

import javax.annotation.Nullable
import javax.inject.Inject
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.PositiveOrZero

/*
Answers the question: "Have you observed this genotype?"
A GA4GH Beacon based on the v0.4 specification. Given a position in a chromosome and an alllele, the beacon looks for matching mutations at that location and returns a response accordingly.

 */

@Log4j2
@Controller("/beacon")
@Secured(SecurityRule.IS_ANONYMOUS)
class BeaconController {

    private final OncostoreService service

    @Inject
    BeaconController(OncostoreService service) {
        this.service = service
    }

    @Get(uri = "/query{?args}", produces = MediaType.APPLICATION_JSON)
    HttpResponse checkVariant(@NotNull @Pattern(regexp = "[1-22]|X|Y") String chromosome, @NotNull @PositiveOrZero BigInteger startPosition,
                             @NotNull @Pattern(regexp = "[ACTG]+") String reference, @NotNull @Pattern(regexp = "[ACTG]+") String observed, @NotNull String assemblyId, @Nullable ListingArguments args){
        log.info("Beacon request for variant.")
        try {
            BeaconAlleleResponse response = service.getBeaconAlleleResponse(chromosome, startPosition, reference, observed, assemblyId, args)
            return HttpResponse.ok(response)
        }

        catch (Exception e) {
            log.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.")
        }

        // check for 400 ? (bad request), missing mandatory parameters
        // we have to implement 401 and 403 here as well when we're dealing with permissions
    }
}
