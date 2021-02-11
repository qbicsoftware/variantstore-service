package life.qbic.oncostore.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import io.swagger.v3.oas.annotations.media.Schema

/**
 * A gene with detailed information
 *
 * @since: 1.1.0
 */
@EqualsAndHashCode
@Schema(name = "Gene", description = "A Gene")
class Gene {

    /**
     * The chromosome of a gene
     */
    String chromosome
    /**
     * The symbol of a gene
     */
    String symbol
    /**
     * The name of a gene
     */
    String name
    /**
     * The biological classification (such as pseudogene and protein coding) of a gene
     */
    String bioType
    /**
     * The description available for a gene
     */
    String description
    /**
     * The start position of a gene
     */
    BigInteger geneStart
    /**
     * The end position of a gene
     */
    BigInteger geneEnd
    /**
     * The identifier of a gene
     */
    String geneId
    /**
     * The strand of a gene
     */
    String strand
    /**
     * The version of a gene
     */
    Integer version
    /**
     * The associated synonyms of a gene
     */
    List<String> synonyms

    Gene() {}

    Gene(String geneId) {
        this.geneId = geneId
    }

    Gene(String chromosome, String symbol, String name, String bioType, BigInteger geneStart, BigInteger geneEnd,
         String geneId, List<String> synonyms) {
        this.chromosome = chromosome
        this.symbol = symbol
        this.name = name
        this.bioType = bioType
        this.geneStart = geneStart
        this.geneEnd = geneEnd
        this.geneId = geneId
        this.synonyms = synonyms
    }

    @Schema(description = "The chromosome")
    @JsonProperty("chromosome")
    String getChromosome() {
        return chromosome
    }

    @Schema(description = "The gene symbol (HGNC Symbol)")
    @JsonProperty("symbol")
    String getSymbol() {
        return symbol
    }

    @Schema(description = "The gene name (HGNC Symbol)")
    @JsonProperty("name")
    String getName() {
        return name
    }

    @Schema(description = "The biological gene type")
    @JsonProperty("bioType")
    String getBioType() {
        return bioType
    }

    @Schema(description = "The genomic start position")
    @JsonProperty("geneStart")
    BigInteger getGeneStart() {
        return geneStart
    }

    @Schema(description = "The genomic end position")
    @JsonProperty("geneEnd")
    BigInteger getGeneEnd() {
        return geneEnd
    }

    @Schema(description = "The gene identifier")
    @JsonProperty("geneID")
    String getGeneId() {
        return geneId
    }

    @Schema(description = "The synonyms")
    @JsonProperty("synonyms")
    List<String> getSynonyms() {
        return synonyms
    }

    @Schema(description = "The description")
    @JsonProperty("description")
    String getDescription() {
        return description
    }

    @Schema(description = "The gene orientation", allowableValues = ["+, -"])
    @JsonProperty("strand")
    String getStrand() {
        return strand
    }

    @Schema(description = "The gene version")
    @JsonProperty("version")
    Integer getVersion() {
        return version
    }

    void setStrand(String strand) {
        this.strand = strand
    }

    void setVersion(Integer version) {
        this.version = version
    }

    void setDescription(String description) {
        this.description = description
    }

    void setChromosome(String chromosome) {
        this.chromosome = chromosome
    }

    void setSymbol(String geneSymbol) {
        this.symbol = geneSymbol
    }

    void setName(String name) {
        this.name = name
    }

    void setBioType(String bioType) {
        this.bioType = bioType
    }

    void setGeneStart(BigInteger geneStart) {
        this.geneStart = geneStart
    }

    void setGeneEnd(BigInteger geneEnd) {
        this.geneEnd = geneEnd
    }

    void setGeneId(String geneID) {
        this.geneId = geneID
    }

    void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms
    }
}
