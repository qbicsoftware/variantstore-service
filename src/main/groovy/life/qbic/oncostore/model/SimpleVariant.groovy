package life.qbic.oncostore.model

import com.fasterxml.jackson.annotation.JsonProperty
import htsjdk.variant.variantcontext.VariantContext

class SimpleVariant implements SimpleVariantContext{

    final VariantContext context
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
        context.reference
    }

    @JsonProperty("observedAllele")
    @Override
    String getObservedAllele() {
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
    Boolean isSomatic() {
        return null
    }

    @Override
    String getAttribute(String key) {
        this.context.getAttribute(key) ?: ""
    }
}