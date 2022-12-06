package life.qbic.variantstore.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.hateoas.JsonError
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.transaction.annotation.TransactionalAdvice
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.inject.Inject
import life.qbic.variantstore.model.Project
import life.qbic.variantstore.service.VariantstoreService
import life.qbic.variantstore.util.ListingArguments
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Controller for Beacon requests
 *
 * Answers the question: "Have you observed this genotype?"
 * A GA4GH Beacon based on the v0.4 specification. Given a position in a chromosome and an allele, the beacon looks
 * for matching mutations at that location and returns a response accordingly.
 *
 * @since: 1.0.0
 */
@Controller("/projects")
@Secured(SecurityRule.IS_ANONYMOUS)
class ProjectController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);

    /**
     * The variantstore service
     */
    private final VariantstoreService service

    @Inject
    ProjectController(VariantstoreService service) {
        this.service = service
    }

    /**
     * Retrieve project by identifier
     * @param identifier the project identifier
     * @return the found variant or 404 Not Found
     */
    @TransactionalAdvice('${database.specifier}')
    @Get(uri = "/{id}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Request a project",
            description = "The project with the specified identifier is returned.",
            tags = "Project")
    @ApiResponse(responseCode = "200", description = "Returns a project", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = Project.class)))
    @ApiResponse(responseCode = "400", description = "Invalid project identifier supplied")
    @ApiResponse(responseCode = "404", description = "Project not found")
    HttpResponse getProject(@PathVariable(name = "id") String identifier) {
        LOGGER.info("Resource request for project: $identifier")
        try {
            Optional<Project> project = service.getProjectForProjectId(identifier)
            JsonError error = new JsonError("Project $identifier not found.")
            return project.present ? HttpResponse.ok(project.get()) : HttpResponse.notFound("No Project found for given "
                    + "identifier.").body(error)
        } catch (IllegalArgumentException e) {
            LOGGER.error(e)
            return HttpResponse.badRequest("Invalid project identifier supplied.")
        } catch (Exception e) {
            LOGGER.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.")
        }
    }

    /**
     * Retrieve projects based on filtering criteria
     * @param args the filter arguments
     * @return the found projects or 404 Not Found
     */
    @TransactionalAdvice('${database.specifier}')
    @Get(uri = "{?args*}", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Request a set of projects",
            description = "The samples matching the supplied properties are returned.",
            tags = "Project")
    @ApiResponse(responseCode = "200", description = "Returns a set of projects", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Project.class)))
    @ApiResponse(responseCode = "404", description = "No samples found matching provided attributes")
    HttpResponse getProjects(ListingArguments args){
        LOGGER.info("Resource request for projects with filtering options.")
        try {
            List<Project> projects = service.getProjectsForSpecifiedProperties(args)
            return projects ? HttpResponse.ok(projects) : HttpResponse.notFound("No projects found matching provided attributes.")
        }
        catch (Exception e) {
            LOGGER.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.")
        }
    }
}
