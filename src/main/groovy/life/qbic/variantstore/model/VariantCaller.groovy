package life.qbic.variantstore.model

import groovy.transform.EqualsAndHashCode
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.micronaut.data.model.naming.NamingStrategies
import io.swagger.v3.oas.annotations.media.Schema

/**
 * A variant calling software
 *
 * @since: 1.0.0
 */
@MappedEntity(namingStrategy = NamingStrategies.LowerCase)
@EqualsAndHashCode
@Schema(name="Variant Caller", description="A variant calling software")
class VariantCaller implements Software {

    /**
     * The database id
     */
    @GeneratedValue
    @Id
    private Long id
    /**
     * The name of a variant calling software
     */
    final String name
    /**
     * The version of a variant calling software
     */
    final String version
    /**
     * The Digital Object Identifier (DOI) of a variant calling software
     */
    final String doi

    @Relation(value = Relation.Kind.MANY_TO_MANY, mappedBy = "variantCaller")
    Set<Variant> variants

    VariantCaller(String name, String version, String doi) {
        this.name = name
        this.version = version
        this.doi = doi
    }

    Long getId() {
        return id
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

    Set<Variant> getVariants() {
        return variants
    }

    void setId(Long id) {
        this.id = id
    }

    void setVariants(Set<Variant> variants) {
        this.variants = variants
    }

    void addVariant(Variant variant){
        if(variants==null) variants = [].toSet() as Set<Variant>
        variants.add(variant)
    }
}
