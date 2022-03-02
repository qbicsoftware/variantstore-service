package life.qbic.variantstore.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import io.micronaut.core.annotation.Creator
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.micronaut.data.jdbc.annotation.JoinColumn
import io.micronaut.data.model.naming.NamingStrategies
import io.swagger.v3.oas.annotations.media.Schema

/**
 * A case (patient)
 *
 * @since: 1.1.0
 */
@MappedEntity(value = "entity", namingStrategy = NamingStrategies.UnderScoreSeparatedLowerCase.class)
@EqualsAndHashCode(includeFields=true, excludes = ["id", "samples"])
@Schema(name = "Case", description = "A case")
class Case {

    /**
     * The identifier of a case
     */
    @GeneratedValue
    @Id
    Long id

    String identifier

    /**
     * The project associated with a case
     */
    @Relation(value = Relation.Kind.MANY_TO_ONE)
    @JoinColumn(name="project_id")
    Project project

    /**
     * The samples associated with a case
     */
    @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "entity", cascade = Relation.Cascade.ALL)
    Set<Sample> samples

    @Creator
    Case() { }

    Case(String identifier, String projectId) {
        this.identifier = identifier
        this.project = new Project().setIdentifier(projectId)
    }

    @Schema(description = "The case identifier")
    @JsonProperty("id")
    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    void setIdentifier(String identifier) {
        this.identifier = identifier
    }

    void setProject(Project project) {
        this.project = project
    }

    void setSamples(Set<Sample> samples) {
        this.samples = samples
    }

    String getIdentifier() {
        return identifier
    }

    Project getProject() {
        return project
    }

    Set<Sample> getSamples() {
       return samples
   }

}
