package life.qbic.oncostore.controller

import groovy.util.logging.Log4j2
import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import life.qbic.oncostore.model.Gene
import life.qbic.oncostore.model.Variant
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
    @Get(uri = "/{id}{?args*}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Request a gene",
            description = "The gene with the specified identifier is returned.",
            tags = "Gene")
    @ApiResponse(
            responseCode = "200", description = "Returns a gene", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Gene.class)))
    @ApiResponse(responseCode = "400", description = "Invalid gene identifier supplied")
    @ApiResponse(responseCode = "404", description = "Gene not found")
    HttpResponse getGene(@PathVariable(name="id") String identifier, ListingArguments args) {
        log.info("Resource request for gene: $identifier")
        try {
            List<Gene> genes = service.getGeneForGeneId(identifier, args)
            return genes ? HttpResponse.ok(genes.get(0)) : HttpResponse.notFound("Gene not found.")
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
    @Operation(summary = "Request a set of genes",
            description = "The genes matching the supplied properties are returned.",
            tags = "Gene")
    @ApiResponse(responseCode = "200", description = "Returns a set of genes", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Gene.class)))
    @ApiResponse(responseCode = "404", description = "No genes found matching provided attributes")
    @Get(uri = "{?args*}", produces = MediaType.APPLICATION_JSON)
    HttpResponse getGenes(ListingArguments args) {
        log.info("Resource request for genes with filtering options.")
        try {
            List<Gene> genes = service.getGenesForSpecifiedProperties(args)
            return genes ? HttpResponse.ok(genes) : HttpResponse.notFound("No genes found matching provided attributes.")
        }
        catch (Exception e) {
            log.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.")
        }
    }

    @Operation(summary = "Upload gene information",
            description = "Uploa Ensembl GFF3 file to add gene information to the store.",
            tags = "Gene")
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
