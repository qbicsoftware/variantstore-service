package life.qbic.variantstore.model

import groovy.transform.EqualsAndHashCode
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.MappedProperty
import io.micronaut.data.annotation.Relation
import io.micronaut.data.model.naming.NamingStrategies
import io.swagger.v3.oas.annotations.media.Schema

import javax.persistence.CascadeType
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table

/**
 * A sample with associated metadata
 *
 * @since: 1.0.0
 *
 */
@MappedEntity(value = "referencegenome", namingStrategy = NamingStrategies.UnderScoreSeparatedLowerCase)
@EqualsAndHashCode
@Schema(name="ReferenceGenome", description="A reference genome")
class ReferenceGenome {

    @GeneratedValue
    @Id
    private Long id

    final String source
    final String build
    final String version

    /*@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "referencegenomevariant", joinColumns = @JoinColumn(name = "referencegenomeid",
            referencedColumnName = "id", nullable = false, updatable = false), inverseJoinColumns = @JoinColumn(name = "variantid",
            referencedColumnName = "id", nullable = false, updatable = false))
    */
    @Relation(value = Relation.Kind.MANY_TO_MANY, cascade = Relation.Cascade.PERSIST)
    Set<Variant> variants = new HashSet<>()

    ReferenceGenome(String source, String build, String version) {
        this.source = source
        this.build = build
        this.version = version
    }

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    @Schema(description="The genome source")
    String getSource() {
        return source
    }

    @Schema(description="The genome build")
    String getBuild() {
        return build
    }

    @Schema(description="The genome version")
    String getVersion() {
        return version
    }

    @Override
    String toString() {
        return "${build}.${version}"
    }

    Set<Variant> getVariants() {
        return variants
    }

    void setVariants(Set<Variant> variants) {
        this.variants = variants
    }
}
