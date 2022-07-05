package life.qbic.variantstore.controller

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
import io.micronaut.transaction.annotation.TransactionalAdvice
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.inject.Inject
import life.qbic.variantstore.model.Gene
import life.qbic.variantstore.parser.EnsemblParser
import life.qbic.variantstore.service.VariantstoreService
import life.qbic.variantstore.util.ListingArguments

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Controller for gene requests
 *
 * This handles requests that try to retrieve information on genes from the store.
 *
 * @since: 1.0.0
 */
@Log4j2
@Controller("/genes")
@Secured(SecurityRule.IS_AUTHENTICATED)
class GeneController {

    /**
     * The variantstore service
     */
    private final VariantstoreService service

    @Inject
    GeneController(VariantstoreService service) {
        this.service = service
    }

    /**
     * Retrieve gene by identifier
     * @param identifier the gene identifier
     * @return The found gene or 404 Not Found
     */
    @TransactionalAdvice('${database.specifier}')
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
            Set<Gene> genes = service.getGeneForGeneId(identifier, args)
            return genes ? HttpResponse.ok(genes[0]) : HttpResponse.notFound("Gene not found.").body("")
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
     * Retrieve gene based on filtering options
     * @param args the filter arguments
     * @return the found genes or 404 Not Found
     */
    @TransactionalAdvice('${database.specifier}')
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
            Set<Gene> genes = service.getGenesForSpecifiedProperties(args)
            return genes ? HttpResponse.ok(genes) : HttpResponse.notFound("No genes found matching provided attributes.")
        }
        catch (Exception e) {
            log.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.")
        }
    }

    /**
     * Upload gene information provided as GFF3 file to the store
     * @param args the filter arguments
     * @return 200 OK or 400 Bad Request
     */
    @TransactionalAdvice('${database.specifier}')
    @Operation(summary = "Upload gene information",
            description = "Upload Ensembl GFF3 file to add gene information to the store.",
            tags = "Gene")
    @Post(uri = "/", consumes = MediaType.MULTIPART_FORM_DATA)
    HttpResponse storeGenes(CompletedFileUpload files) {
        try {
            log.info("Request for storing gene information.")
            File tempFile = File.createTempFile(files.getFilename(), "temp")
            Path path = Paths.get(tempFile.getAbsolutePath())
            Files.write(path, files.getBytes())
            EnsemblParser ensembl = new EnsemblParser(tempFile)
            service.storeGeneInformationInStore(ensembl.ensemblContext)
            return HttpResponse.ok("Upload of gene information successful.")
        } catch (IOException exception) {
            log.error(exception)
            return HttpResponse.badRequest("Upload of gene information failed.")
        }
    }
}
