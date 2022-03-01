package life.qbic.variantstore.model

import groovy.transform.EqualsAndHashCode
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.micronaut.data.model.naming.NamingStrategies
import io.swagger.v3.oas.annotations.media.Schema

/**
 * A sample with associated metadata
 *
 * @since: 1.0.0
 *
 */
/*
@Data
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
@NoArgsConstructor
@MappedEntity
 */
@MappedEntity(namingStrategy = NamingStrategies.LowerCase.class)
@EqualsAndHashCode
@Schema(name="ReferenceGenome", description="A reference genome")
class ReferenceGenome {

    @GeneratedValue
    @Id
    private Long id

    String source
    String build
    String version

    @Relation(value = Relation.Kind.MANY_TO_MANY, mappedBy = "referenceGenomes")
    Set<Variant> variants

    @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "referencegenome")
    private Set<Ensembl> ensemblInstances

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

    Set<Ensembl> getEnsemblInstances() {
        return ensemblInstances
    }

    void setEnsemblInstances(Set<Ensembl> ensemblInstances) {
        this.ensemblInstances = ensemblInstances
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

    void addVariant(Variant variant){
        if(variants==null) variants = [].toSet() as Set<Variant>
        variants.add(variant)
    }
}
