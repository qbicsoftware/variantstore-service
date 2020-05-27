package life.qbic.oncostore.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name="Variant", description="A genomic variant")
class Variant implements SimpleVariantContext, Comparable{

    String identifier
    String databaseIdentifier
    String chromosome
    BigInteger startPosition
    BigInteger endPosition
    String referenceAllele
    String observedAllele
    List<Consequence> consequences
    ReferenceGenome referenceGenome
    Boolean isSomatic
    VcfInfo vcfInfo
    List<Genotype> genotypes

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

    void setVCF(VcfInfo vcfInfo) {
        this.vcfInfo = vcfInfo
    }

    void setDatabaseIdentifier(String databaseIdentifier) {
        this.databaseIdentifier = databaseIdentifier
    }

    void setVcfInfo(VcfInfo vcfInfo) {
        this.vcfInfo = vcfInfo
    }

    void setGenotypes(List<Genotype> genotypes) {
        this.genotypes = genotypes
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

    @Schema(description="The database identifier of this variant")
    @JsonProperty("databaseIdentifier")
    @Override
    String getDatabaseId() {
        return databaseIdentifier
    }

    @Schema(description="Is it a somatic variant?")
    @JsonProperty("isSomatic")
    @Override
    Boolean getIsSomatic() {
        return isSomatic
    }

    @Schema(description="The variant identifier")
    @JsonProperty("identifier")
    @Override
    String getId() {
        return identifier
    }

    @Schema(description="The information given in the INFO column of a VCF file")
    @JsonProperty("info")
    @Override
    VcfInfo getVcfInfo() {
        return vcfInfo
    }

    @Schema(description="The database identifier of this variant (if available)")
    @JsonProperty("databaseIdentifier")
    String getDatabaseIdentifier() {
        return databaseIdentifier
    }

    @Schema(description="The genotypes associated with this variant")
    @JsonProperty("genotypes")
    List<Genotype> getGenotypes() {
        return genotypes
    }

    String toVcfFormat() {
        return new StringBuilder().append(chromosome + "\t").append(startPosition + "\t").append(referenceAllele + "\t").append(observedAllele + "\t")
    }

}
