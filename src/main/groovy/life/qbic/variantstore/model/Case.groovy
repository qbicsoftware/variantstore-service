package life.qbic.variantstore.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.micronaut.data.model.naming.NamingStrategies
import io.swagger.v3.oas.annotations.media.Schema

/**
 * A case (patient)
 *
 * @since: 1.1.0
 */
@MappedEntity(value = "entity" , namingStrategy = NamingStrategies.UnderScoreSeparatedLowerCase.class)
@EqualsAndHashCode
@Schema(name = "Case", description = "A case")
class Case {

    /**
     * The identifier of a case
     */
    @Id
    private final String id
    /**
     * The project associated with a case
     */
    @Relation(value = Relation.Kind.MANY_TO_ONE, mappedBy = "project_id")
    private Project project

    /**
     * The project associated with a case
     */
    @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "entity")
    private final List<Sample> sample

    Case(String id, Project project) {
        this.id = id
        this.project = project
    }

    @Schema(description = "The case identifier")
    @JsonProperty("id")
    String getId() {
        return id
    }

    /*
    @Schema(description = "The associated project identifier")
    @JsonProperty("projectId")
    String getProjectId() {
        return projectId
    }
    */


    Project getProject() {
        return project
    }

    void setProject(Project project) {
        this.project = project
    }
}
