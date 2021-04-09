package life.qbic.variantstore.model

import groovy.transform.EqualsAndHashCode
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.micronaut.data.model.naming.NamingStrategies
import io.swagger.v3.oas.annotations.media.Schema

/**
 * A variant annotation software
 *
 * @since: 1.0.0
 *
 */
@MappedEntity(value = "annotationsoftware", namingStrategy = NamingStrategies.LowerCase)
@EqualsAndHashCode
@Schema(name="Variant Annotation", description="A variant annotation software")
class Annotation implements Software{

    /**
     * The identifier of a Variant Caller
     */
    @GeneratedValue
    @Id
    private Long id
    /**
     * The name of a given annotation software
     */
    final String name
    /**
     * The version of a given annotation software
     */
    final String version
    /**
     * The Digital Object Identifier (DOI) of a given annotation software
     */
    final String doi

    @Relation(value = Relation.Kind.MANY_TO_MANY, cascade = Relation.Cascade.PERSIST)
    Set<Consequence> consequences = new HashSet<>()

    Annotation(String name, String version, String doi) {
        this.name = name
        this.version = version
        this.doi = doi
    }

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    @Override
    String getName() {
        return name
    }

    @Override
    String getVersion() {
        return version
    }

    @Override
    String getDoi() {
        return doi
    }

    Set<Consequence> getConsequences() {
        return consequences
    }
}
