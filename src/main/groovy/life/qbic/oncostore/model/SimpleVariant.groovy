package life.qbic.oncostore.model

import com.fasterxml.jackson.annotation.JsonProperty
import htsjdk.variant.variantcontext.VariantContext

class SimpleVariant implements SimpleVariantContext{

    final VariantContext context
    final VcfInfo vcfInfo
    final List<Genotype> genotypes
    String id
    Boolean isSomatic
    List<Consequence> consequences = []

    SimpleVariant(VariantContext context) {
        this.context = context
        this.vcfInfo = new VcfInfo(context.getCommonInfo())
        List<Genotype> genotypes = []
        context.getGenotypes().each { genotype ->
            genotypes.add(new Genotype(genotype))
        }

        if (genotypes.empty) genotypes.add(new Genotype())
        this.genotypes = genotypes
    }

    @JsonProperty("chromosome")
    @Override
    String getChromosome() {
        context.contig
    }

    @JsonProperty("startPosition")
    @Override
    BigInteger getStartPosition() {
        context.start
    }

    @JsonProperty("endPosition")
    @Override
    BigInteger getEndPosition() {
        context.end
    }

    @JsonProperty("referenceAllele")
    @Override
    String getReferenceAllele() {
        context.reference.toString().replace("*", "")
    }

    @JsonProperty("databaseId")
    @Override
    String getDatabaseId() {
        context.getID()
    }

    @JsonProperty("observedAllele")
    @Override
    String getObservedAllele() {
        //@TODO check that for release!
        // this is a list, usually we would expect one observed allele, but it`s allowed to have multiple
        context.alternateAlleles.join(',')
    }

    @JsonProperty("consequences")
    @Override
    List<Consequence> getConsequences() {
        return consequences
    }

    @JsonProperty("info")
    @Override
    VcfInfo getVcfInfo() {
        return vcfInfo
    }

    @Override
    ReferenceGenome getReferenceGenome() {
        return null
    }

    @Override
    Boolean getIsSomatic() {
        return isSomatic
    }

    List<Object> getAttribute(String key) {
        this.context.getAttributeAsList(key)
    }

    @Override
    String getId() {
        return id
    }

    VariantContext getContext() {
        return context
    }

    List<Genotype> getGenotypes() {
        return genotypes
    }

    @Override
    void setId(String id) {
        this.id = id
    }

    @Override
    void setIsSomatic(Boolean isSomatic) {
        this.isSomatic = isSomatic
    }
}