package life.qbic.variantstore.model

import groovy.transform.EqualsAndHashCode
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.Join
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.micronaut.data.model.naming.NamingStrategies
import io.swagger.v3.oas.annotations.media.Schema

@MappedEntity(namingStrategy = NamingStrategies.LowerCase.class)
@EqualsAndHashCode
@Schema(name="Project", description="A project")
class Project {

    /**
     * The identifier of a project
     */
    @Id
    private String id

    //@Join(name = "project_id")
    @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "project")
    private List<Case> cases

    Project(String id) {
        this.id = id
    }

    String getId() {
        return id
    }
}
