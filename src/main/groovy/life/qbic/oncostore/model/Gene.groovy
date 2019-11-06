package life.qbic.oncostore.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name="Gene", description="A Gene")
class Gene {

    String chromosome
    String symbol
    String name
    String bioType
    String description
    BigInteger geneStart
    BigInteger geneEnd
    String geneID
    String strand
    Integer version
    List<String> synonyms

    Gene() {
    }

    Gene(String geneID) {
        this.geneID = geneID
    }

    Gene(String chromosome, String symbol, String name, String bioType, BigInteger geneStart, BigInteger geneEnd, String geneID, List<String> synonyms) {
        this.chromosome = chromosome
        this.symbol = symbol
        this.name = name
        this.bioType = bioType
        this.geneStart = geneStart
        this.geneEnd = geneEnd
        this.geneID = geneID
        this.synonyms = synonyms
    }

    @Schema(description="The chromosome")
    @JsonProperty("chromosome")
    String getChromosome() {
        return chromosome
    }

    @Schema(description="The gene symbol (HGNC Symbol)")
    @JsonProperty("symbol")
    String getSymbol() {
        return symbol
    }

    @Schema(description="The gene name (HGNC Symbol)")
    @JsonProperty("name")
    String getName() {
        return name
    }

    @Schema(description="The biological gene type")
    @JsonProperty("bioType")
    String getBioType() {
        return bioType
    }

    @Schema(description="The genomic start position")
    @JsonProperty("geneStart")
    BigInteger getGeneStart() {
        return geneStart
    }

    @Schema(description="The genomic end position")
    @JsonProperty("geneEnd")
    BigInteger getGeneEnd() {
        return geneEnd
    }

    @Schema(description="The gene identifier")
    @JsonProperty("geneId")
    String getGeneID() {
        return geneID
    }

    @Schema(description="The synonyms")
    @JsonProperty("synonyms")
    List<String> getSynonyms() {
        return synonyms
    }

    @Schema(description="The description")
    @JsonProperty("description")
    String getDescription() {
        return description
    }

    @Schema(description="The gene orientation", allowableValues = ["+, -"])
    @JsonProperty("strand")
    String getStrand() {
        return strand
    }

    @Schema(description="The gene version")
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

    void setGeneID(String geneID) {
        this.geneID = geneID
    }

    void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms
    }
}
