package life.qbic.variantstore.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.swagger.v3.oas.annotations.media.Schema

@MappedEntity("project")
@EqualsAndHashCode(includeFields=true, excludes = ["id"])
@Schema(name="Project", description="A project")
class Project {

    /**
     * The database id
     */
    @GeneratedValue
    @Id
    Long id

    /**
     * The identifier of a project
     */
    String identifier

    /**
     * The associated cases of a project
     */
    @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "project")
    Set<Case> cases

    Project() {
    }

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    @Schema(description = "The associated cases")
    @JsonProperty("cases")
    Set<Case> getCases() {
        return cases
    }

    void setCases(Set<Case> cases) {
        this.cases = cases
    }

    @Schema(description = "The identifier")
    @JsonProperty("identifier")
    String getIdentifier() {
        return identifier
    }

    void setIdentifier(String identifier) {
        this.identifier = identifier
    }
}
