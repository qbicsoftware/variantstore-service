package life.qbic.oncostore.model

import com.fasterxml.jackson.annotation.JsonProperty

class Variant implements SimpleVariantContext, Comparable{

    String identifier
    String chromosome
    BigInteger startPosition
    BigInteger endPosition
    String referenceAllele
    String observedAllele
    List<Consequence> consequences
    ReferenceGenome referenceGenome
    Boolean isSomatic

    Variant() {
    }

    int compareTo(Object other) {
        identifier <=> other.identifier
    }

    void setIdentifier(String identifier) {
        this.identifier = identifier
    }

    void setChromosome(String chromosome) {
        this.chromosome = chromosome
    }

    void setStartPosition(BigInteger startPosition) {
        this.startPosition = startPosition
    }

    void setEndPosition(BigInteger endPosition) {
        this.endPosition = endPosition
    }

    void setReferenceAllele(String referenceAllele) {
        this.referenceAllele = referenceAllele
    }

    void setObservedAllele(String observedAllele) {
        this.observedAllele = observedAllele
    }

    void setIsSomatic(Boolean isSomatic) {
        this.isSomatic = isSomatic
    }

    void setConsequences(List<Consequence> consequences) {
        this.consequences = consequences
    }

    @JsonProperty("chromosome")
    @Override
    String getChromosome() {
        return chromosome
    }

    @JsonProperty("startPosition")
    @Override
    BigInteger getStartPosition() {
        return startPosition
    }

    @JsonProperty("endPosition")
    @Override
    BigInteger getEndPosition() {
        return endPosition
    }

    @JsonProperty("referenceAllele")
    @Override
    String getReferenceAllele() {
        return referenceAllele
    }

    @JsonProperty("observedAllele")
    @Override
    String getObservedAllele() {
        return observedAllele
    }

    @JsonProperty("consequences")
    @Override
    List<Consequence> getConsequences() {
        return consequences
    }

    @JsonProperty("referenceGenome")
    @Override
    ReferenceGenome getReferenceGenome() {
        return referenceGenome
    }

    @JsonProperty("isSomatic")
    @Override
    Boolean getIsSomatic() {
        return isSomatic
    }

    @JsonProperty("identifier")
    String getIdentifier() {
        return identifier
    }

    String getAttribute(String key) {
        return null
    }

    @Override
    @JsonProperty("id")
    String getId() {
        return identifier
    }

    @Override
    void setId(String identifier) {
        this.identifier = identifier
    }
}
