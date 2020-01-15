package life.qbic.oncostore.model

import com.fasterxml.jackson.annotation.JsonProperty
import htsjdk.variant.variantcontext.VariantContext

class SimpleVariant implements SimpleVariantContext{

    final VariantContext context
    String id
    Boolean isSomatic
    List<Consequence> consequences = []

    SimpleVariant(VariantContext context) {
        this.context = context
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

    @JsonProperty("observedAllele")
    @Override
    String getObservedAllele() {
        //@TODO check that for release!
        // this is a list, usually we would expect one observed allele, so we will just take the first one for now
        context.alternateAlleles.get(0)
    }

    @JsonProperty("consequences")
    @Override
    List<Consequence> getConsequences() {
        return consequences
    }

    @Override
    ReferenceGenome getReferenceGenome() {
        return null
    }

    @Override
    Boolean getIsSomatic() {
        return isSomatic
    }

    @Override
    List<Object> getAttribute(String key) {
        this.context.getAttributeAsList(key)
    }

    @Override
    String getId() {
        return id
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