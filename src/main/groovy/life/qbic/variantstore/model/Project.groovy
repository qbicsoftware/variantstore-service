package life.qbic.variantstore.model

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
     * The identifier of a project
     */
    @GeneratedValue
    @Id
    Long id

    String identifier

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

    Set<Case> getCases() {
        return cases
    }

    void setCases(Set<Case> cases) {
        this.cases = cases
    }

    String getIdentifier() {
        return identifier
    }

    void setIdentifier(String identifier) {
        this.identifier = identifier
    }
}
