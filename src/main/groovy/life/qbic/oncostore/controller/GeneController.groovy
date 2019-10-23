package life.qbic.oncostore.controller

import groovy.util.logging.Log4j2
import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import life.qbic.oncostore.model.Gene
import life.qbic.oncostore.service.OncostoreService
import life.qbic.oncostore.util.ListingArguments

import javax.inject.Inject
import javax.validation.Valid
import javax.validation.constraints.NotNull

@Log4j2
@Controller("/genes")
@Secured(SecurityRule.IS_ANONYMOUS)
class GeneController {

    private final OncostoreService service

    @Inject
    GeneController(OncostoreService service) {
        this.service = service
    }

    /**
     *
     * @param identifier The gene identifier
     * @return The found genes
     */
    @Get(uri = "/{id}", produces = MediaType.APPLICATION_JSON)
    HttpResponse getGene(@Parameter('id') String identifier) {
        log.info("Resource request for gene: $identifier")
        try {
            List<Gene> genes = service.getGeneForGeneId(identifier)
            return genes ? HttpResponse.ok(genes) : HttpResponse.notFound("Gene not found.")
        }
        catch (IllegalArgumentException e) {
            log.error(e)
            return HttpResponse.badRequest("Invalid gene identifier supplied.")
        }
        catch (Exception e) {
            log.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.")
        }
    }


    /**
     *
     * @param args The filter arguments
     * @return The found genes
     */
    @Get(uri = "{?args*}", produces = MediaType.APPLICATION_JSON)
    HttpResponse getGenes(@Valid ListingArguments args) {
        log.info("Resource request for genes with filtering options.")
        try {
            List<Gene> genes = service.getGenesForSpecifiedProperties(args)
            return genes ? HttpResponse.ok(genes) : HttpResponse.notFound("No genes found matching provided attributes..")
        }
        catch (Exception e) {
            log.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.")
        }
    }

    @Post(uri = "/upload", consumes = MediaType.TEXT_PLAIN)
    HttpResponse storeGeneInformation(@QueryValue("url") @NotNull String url) {
        try {
            log.info("Request for storing gene information.")
            service.storeGeneInformationInStore(url)

            return HttpResponse.ok()
        }
        catch (Exception e) {
            log.error(e)
        }
    }
}
