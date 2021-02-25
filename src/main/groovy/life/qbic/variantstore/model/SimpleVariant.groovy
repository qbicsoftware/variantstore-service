package life.qbic.variantstore.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import groovy.util.logging.Log4j2
import htsjdk.variant.variantcontext.VariantContext

@EqualsAndHashCode
@Log4j2
@Deprecated
class SimpleVariant implements SimpleVariantContext{

    //VariantContext context
    String chromosome
    BigInteger startPosition
    BigInteger endPosition
    String referenceAllele
    String observedAllele
    VcfInfo vcfInfo
    List<Genotype> genotypes
    String id
    Boolean isSomatic
    ArrayList consequences
    String databaseId

    SimpleVariant(VariantContext context) {
        chromosome = context.contig
        startPosition = context.start
        endPosition = context.end
        referenceAllele = context.reference.toString().replace("*", "")
        observedAllele = context.alternateAlleles.join(',')
        vcfInfo = new VcfInfo(context.getCommonInfo())
        databaseId = context.getID()
        List<Genotype> genotypes = []
        consequences = null

        context.getGenotypes().each { genotype ->
            genotypes.add(new Genotype(genotype))
        }

        if (genotypes.empty) genotypes.add(new Genotype())
        this.genotypes = genotypes
    }

    SimpleVariant() {

    }

    public addVariantContextAnnotation(VariantContext context, String annotationToolKeyword) {
        consequences = context.getAttributeAsList(annotationToolKeyword)
    }

    @JsonProperty("chromosome")
    @Override
    String getChromosome() {
        //context.contig
        return chromosome
    }

    @JsonProperty("startPosition")
    @Override
    BigInteger getStartPosition() {
        //context.start
        return startPosition
    }

    @JsonProperty("endPosition")
    @Override
    BigInteger getEndPosition() {
        //context.end
        return endPosition
    }

    @JsonProperty("referenceAllele")
    @Override
    String getReferenceAllele() {
        //context.reference.toString().replace("*", "")
        return referenceAllele
    }

    @JsonProperty("databaseId")
    @Override
    String getDatabaseId() {
        return databaseId
        //context.getID()
    }

    @JsonProperty("observedAllele")
    @Override
    String getObservedAllele() {
        //@TODO check that for release!
        // this is a list, usually we would expect one observed allele, but it`s allowed to have multiple
        //context.alternateAlleles.join(',')
        return observedAllele
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
    Boolean getIsSomatic() {
        return isSomatic
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

    @Override
    void setConsequences(ArrayList consequences) {
        this.consequences = consequences
    }
}