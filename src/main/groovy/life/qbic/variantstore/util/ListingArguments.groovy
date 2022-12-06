package life.qbic.variantstore.util

import io.micronaut.http.uri.UriBuilder
import io.micronaut.core.annotation.Nullable
import javax.validation.constraints.Pattern
import javax.validation.constraints.Positive

/**
 * A class to hold variables for request arguments.
 *
 * Might extract these arguments at some point and use this class for sorting, pagination etc.
 *
 * @since: 1.0.0
 */
class ListingArguments {
    @Nullable
    @Pattern(regexp = "[1-22]|X|Y")
    private String chromosome

    @Nullable
    @Positive
    private BigInteger startPosition

    @Nullable
    @Positive
    private BigInteger endPosition

    @Nullable
    private String cancerEntity

    @Nullable
    @Pattern(regexp = '(Q[A-Z\\d]{4}\\d{3}[A-Z][A-Z\\d]$)|(Q[A-Z\\d]{4}ENTITY-\\d*$)')
    private String sampleId

    @Nullable
    private String geneId

    @Nullable
    private String gene

    @Nullable
    private String consequenceType

    @Nullable
    @Positive
    private Integer ensemblVersion

    @Nullable
    @Pattern(regexp = "vcf|VCF")
    private String format

    ListingArguments() {

    }

    Optional<String> getChromosome() {
        if (chromosome == null) {
            return Optional.empty()
        }
        return Optional.of(chromosome)
    }

    Optional<BigInteger> getStartPosition() {
        if (startPosition == null) {
            return Optional.empty()
        }
        return Optional.of(startPosition)
    }

    Optional<BigInteger> getEndPosition() {
        if (endPosition == null) {
            return Optional.empty()
        }
        return Optional.of(endPosition)
    }

    Optional<String> getCancerEntity() {
        if (cancerEntity == null) {
            return Optional.empty()
        }
        return Optional.of(cancerEntity)
    }

    Optional<String> getSampleId() {
        if (sampleId == null) {
            return Optional.empty()
        }
        return Optional.of(sampleId)
    }

    Optional<String> getGeneId() {
        if (geneId == null) {
            return Optional.empty()
        }
        return Optional.of(geneId)
    }

    Optional<String> getGene() {
        if (gene == null) {
            return Optional.empty()
        }
        return Optional.of(gene)
    }

    Optional<String> getConsequenceType() {
        if (consequenceType == null) {
            return Optional.empty()
        }
        return Optional.of(consequenceType)
    }

    Optional<Integer> getEnsemblVersion() {
        if (ensemblVersion == null) {
            return Optional.empty()
        }
        return Optional.of(ensemblVersion)
    }

    Optional<String> getFormat() {
        if (format == null) {
            return Optional.empty()
        }
        return Optional.of(format)
    }

    void setChromosome(@Nullable String chromosome) {
        this.chromosome = chromosome
    }

    void setStartPosition(@Nullable BigInteger startPosition) {
        this.startPosition = startPosition
    }

    void setEndPosition(@Nullable BigInteger endPosition) {
        this.endPosition = endPosition
    }

    void setCancerEntity(@Nullable String cancerEntity) {
        this.cancerEntity = cancerEntity
    }

    void setSampleId(@Nullable String sampleId) {
        this.sampleId = sampleId
    }

    void setGeneId(@Nullable String geneId) {
        this.geneId = geneId
    }

    void setGene(@Nullable String gene) {
        this.gene = gene
    }

    void setConsequenceType(@Nullable String consequenceType) {
        this.consequenceType = consequenceType
    }

    void setEnsemblVersion(@Nullable Integer ensemblVersion) {
        this.ensemblVersion = ensemblVersion
    }

    void setFormat(@Nullable String format) {
        this.format = format
    }

    static Builder builder() {
        return new Builder()
    }

    URI of(UriBuilder uriBuilder) {
        if (chromosome != null) {
            uriBuilder.queryParam("chromosome", chromosome)
        }

        if (startPosition != null) {
            uriBuilder.queryParam("startPosition", startPosition)
        }

        if (endPosition != null) {
            uriBuilder.queryParam("endPosition", startPosition)
        }

        if (cancerEntity != null) {
            uriBuilder.queryParam("cancerEntity", cancerEntity)
        }

        if (sampleId != null) {
            uriBuilder.queryParam("sampleId", sampleId)
        }

        if (geneId != null) {
            uriBuilder.queryParam("geneId", geneId)
        }

        if (gene != null) {
            uriBuilder.queryParam("gene", gene)
        }

        if (consequenceType != null) {
            uriBuilder.queryParam("consequenceType", consequenceType)
        }

        if (ensemblVersion != null) {
            uriBuilder.queryParam("ensemblVersion", ensemblVersion)
        }

        if (format != null) {
            uriBuilder.queryParam("format", format)
        }

        return uriBuilder.build()
    }

    static final class Builder {
        private ListingArguments args = new ListingArguments()

        private Builder() {

        }

        Builder chromosome(String chromosome) {
            args.setChromosome(chromosome)
            return this
        }

        Builder startPosition(BigInteger startPosition) {
            args.setStartPosition(startPosition)
            return this
        }

        Builder endPosition(BigInteger endPosition) {
            args.setEndPosition(endPosition)
            return this
        }

        Builder cancerEntity(String cancerEntity) {
            args.setCancerEntity(cancerEntity)
            return this
        }

        Builder sampleId(String sampleId) {
            args.setSampleId(sampleId)
            return this
        }

        Builder geneId(String geneId) {
            args.setGeneId(geneId)
            return this
        }

        Builder gene(String gene) {
            args.setGene(gene)
            return this
        }

        Builder consequenceType(String consequenceType) {
            args.setConsequenceType(consequenceType)
            return this
        }

        Builder ensemblVersion(Integer ensemblVersion) {
            args.setEnsemblVersion(ensemblVersion)
            return this
        }

        Builder format(String format) {
            args.setFormat(format)
            return this
        }

        ListingArguments build() {
            return this.args
        }
    }
}
