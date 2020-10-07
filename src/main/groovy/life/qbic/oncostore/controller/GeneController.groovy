package life.qbic.oncostore.controller

import groovy.util.logging.Log4j2
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.multipart.CompletedFileUpload
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import life.qbic.oncostore.model.Gene
import life.qbic.oncostore.parser.EnsemblParser
import life.qbic.oncostore.service.VariantstoreService
import life.qbic.oncostore.util.ListingArguments

import javax.inject.Inject
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Log4j2
@Controller("/genes")
@Secured(SecurityRule.IS_AUTHENTICATED)
class GeneController {

    private final VariantstoreService service

    @Inject
    GeneController(VariantstoreService service) {
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
            description = "Upload Ensembl GFF3 file to add gene information to the store.",
            tags = "Gene")
    @Post(uri = "/", consumes = MediaType.MULTIPART_FORM_DATA)
    HttpResponse storeGenes(CompletedFileUpload files) {
        try {
            log.info("Request for storing gene information.")

            File tempFile = File.createTempFile(files.getFilename(), "temp");
            Path path = Paths.get(tempFile.getAbsolutePath());
            Files.write(path, files.getBytes());
            //TODO ensembl POJO
            EnsemblParser ensembl = new EnsemblParser(tempFile)
            service.storeGeneInformationInStore(ensembl)

            return HttpResponse.ok("Upload of gene information successful.")
        } catch (IOException exception) {
            log.error(exception)
            return HttpResponse.badRequest("Upload of gene information failed.");
        }
    }
}
