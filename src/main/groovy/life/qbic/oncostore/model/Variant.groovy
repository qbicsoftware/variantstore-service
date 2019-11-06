package life.qbic.oncostore.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name="Variant", description="A genomic variant")
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

    @Override
    int compareTo(Object other) {
        Variant v = (Variant) other
        return identifier <=> v.identifier
    }

    @Override
    void setId(String identifier) {
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

    @Schema(description="The chromosome")
    @JsonProperty("chromosome")
    @Override
    String getChromosome() {
        return chromosome
    }

    @Schema(description="The genomic start position")
    @JsonProperty("startPosition")
    @Override
    BigInteger getStartPosition() {
        return startPosition
    }

    @Schema(description="The genomic end position")
    @JsonProperty("endPosition")
    @Override
    BigInteger getEndPosition() {
        return endPosition
    }

    @Schema(description="The reference allele")
    @JsonProperty("referenceAllele")
    @Override
    String getReferenceAllele() {
        return referenceAllele
    }

    @Schema(description="The observed allele")
    @JsonProperty("observedAllele")
    @Override
    String getObservedAllele() {
        return observedAllele
    }

    @Schema(description="The consequences")
    @JsonProperty("consequences")
    @Override
    List<Consequence> getConsequences() {
        return consequences
    }

    @Schema(description="The reference genome")
    @JsonProperty("referenceGenome")
    @Override
    ReferenceGenome getReferenceGenome() {
        return referenceGenome
    }

    @Schema(description="Is it a somatic variant?")
    @JsonProperty("isSomatic")
    @Override
    Boolean getIsSomatic() {
        return isSomatic
    }

    String getAttribute(String key) {
        return null
    }

    @Schema(description="The variant identifier")
    @JsonProperty("identifier")
    @Override
    String getId() {
        return identifier
    }

}
