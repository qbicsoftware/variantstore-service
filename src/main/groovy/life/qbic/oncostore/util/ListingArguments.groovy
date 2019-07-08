package life.qbic.oncostore.util

import io.micronaut.http.uri.UriBuilder;

import javax.annotation.Nullable
import javax.validation.constraints.Pattern
import javax.validation.constraints.Positive


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
    @Pattern(regexp = '(Q[A-Z0-9]{4}[0-9]{3}[A-Z][A-Z0-9]$)|(Q[A-Z0-9]{4}ENTITY-[0-9]*$)')
    private String sampleId

    @Nullable
    //TODO pattern?
    private String geneId

    @Nullable
    private String consequenceType

    public ListingArguments() {

    }

    public Optional<String> getChromosome() {
        if (chromosome == null) {
            return Optional.empty()
        }
        return Optional.of(chromosome)
    }

    public Optional<BigInteger> getStartPosition() {
        if (startPosition == null) {
            return Optional.empty()
        }
        return Optional.of(startPosition)
    }

    public Optional<BigInteger> getEndPosition() {
        if (endPosition == null) {
            return Optional.empty()
        }
        return Optional.of(endPosition)
    }

    public Optional<String> getCancerEntity() {
        if (cancerEntity == null) {
            return Optional.empty()
        }
        return Optional.of(cancerEntity)
    }

    public Optional<String> getSampleId() {
        if (sampleId == null) {
            return Optional.empty()
        }
        return Optional.of(sampleId)
    }

    public Optional<String> getGeneId() {
        if (geneId == null) {
            return Optional.empty()
        }
        return Optional.of(geneId)
    }

    public Optional<String> getConsequenceType() {
        if (consequenceType == null) {
            return Optional.empty()
        }
        return Optional.of(consequenceType)
    }

    public void setChromosome(@Nullable String chromosome) {
        this.chromosome = chromosome
    }

    public void setStartPosition(@Nullable BigInteger startPosition) {
        this.startPosition = startPosition
    }

    public void setEndPosition(@Nullable BigInteger endPosition) {
        this.endPosition = endPosition
    }

    public void setCancerEntity(@Nullable String cancerEntity) {
        this.cancerEntity = cancerEntity
    }

    public void setSampleId(@Nullable String sampleId) {
        this.sampleId = sampleId
    }

    public void setGeneId(@Nullable String geneId) {
        this.geneId = geneId
    }

    public void setConsequenceType(@Nullable String consequenceType) {
        this.consequenceType = consequenceType
    }

    public static Builder builder() {
        return new Builder()
    }

    public URI of(UriBuilder uriBuilder) {
        if (chromosome != null) {
            uriBuilder.queryParam("chromosome", chromosome);
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

        if (consequenceType != null) {
            uriBuilder.queryParam("consequenceType", consequenceType)
        }

        return uriBuilder.build();
    }

    public static final class Builder {
        private ListingArguments args = new ListingArguments()

        private Builder() {

        }

        public Builder chromosome(String chromosome) {
            args.setChromosome(chromosome)
            return this
        }

        public Builder startPosition(BigInteger startPosition) {
            args.setStartPosition(startPosition)
            return this
        }

        public Builder endPosition(BigInteger endPosition) {
            args.setEndPosition(endPosition)
            return this
        }

        public Builder cancerEntity(String cancerEntity) {
            args.setCancerEntity(cancerEntity)
            return this
        }

        public Builder sampleId(String sampleId) {
            args.setSampleId(sampleId)
            return this
        }

        public Builder geneId(String geneId) {
            args.setGeneId(geneId)
            return this
        }

        public Builder consequenceType(String consequenceType) {
            args.setConsequenceType(consequenceType)
            return this
        }

        public ListingArguments build() {
            return this.args
        }
    }
}
