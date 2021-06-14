package life.qbic.variantstore.model

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.micronaut.data.model.naming.NamingStrategies

@MappedEntity(namingStrategy = NamingStrategies.UnderScoreSeparatedLowerCase)
class SampleVariant {

    @GeneratedValue
    @Id
    private Long id

    @Relation(value = Relation.Kind.MANY_TO_ONE, mappedBy = "sample_id")
    private Sample sample

    @Relation(value = Relation.Kind.MANY_TO_ONE, mappedBy = "variant_id")
    private Variant variant

    @Relation(value = Relation.Kind.MANY_TO_ONE, mappedBy = "vcfinfo_id")
    private VcfInfo vcfinfo

    @Relation(value = Relation.Kind.MANY_TO_ONE, mappedBy = "genotype_id")
    private Genotype genotype

    SampleVariant(Sample sample, Variant variant, VcfInfo vcfinfo, Genotype genotype) {
        this.sample = sample
        this.variant = variant
        this.vcfinfo = vcfinfo
        this.genotype = genotype
    }

    Long getId() {
        return id
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
