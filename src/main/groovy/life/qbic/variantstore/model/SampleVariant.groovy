package life.qbic.variantstore.model

import io.micronaut.core.annotation.Nullable
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.MappedProperty
import io.micronaut.data.annotation.Relation
import io.micronaut.data.model.naming.NamingStrategies

/**
 *
 *
 * @since: 1.1.0
 */
@MappedEntity(value = "sample_variant", namingStrategy = NamingStrategies.LowerCase.class)
class SampleVariant {

    @GeneratedValue
    @Id
    private Long id

    @MappedProperty(value = "sample_id")
    @Relation(value = Relation.Kind.MANY_TO_ONE)
    Sample sample

    @MappedProperty(value = "variant_id")
    @Relation(value = Relation.Kind.MANY_TO_ONE)
    Variant variant

    @Nullable
    @MappedProperty(value = "vcfinfo_id")
    @Relation(value = Relation.Kind.MANY_TO_ONE)
    VcfInfo vcfinfo

    @Nullable
    @MappedProperty(value = "genotype_id")
    @Relation(value = Relation.Kind.MANY_TO_ONE)
    Genotype genotype

    SampleVariant(Sample sample, Variant variant, VcfInfo vcfinfo, Genotype genotype) {
        this.sample = sample
        this.variant = variant
        this.vcfinfo = vcfinfo
        this.genotype = genotype
    }

    Long getId() {
        return id
    }

    void setSample(Sample sample) {
        this.sample = sample
    }

    void setVariant(Variant variant) {
        this.variant = variant
    }

    void setVcfinfo(VcfInfo vcfinfo) {
        this.vcfinfo = vcfinfo
    }

    void setGenotype(Genotype genotype) {
        this.genotype = genotype
    }

    void setId(Long id) {
        this.id = id
    }

    Sample getSample() {
        return sample
    }

    Variant getVariant() {
        return variant
    }

    VcfInfo getVcfinfo() {
        return vcfinfo
    }

    Genotype getGenotype() {
        return genotype
    }
}
