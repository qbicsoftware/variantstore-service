package life.qbic.oncostore.model

import com.fasterxml.jackson.annotation.JsonProperty

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

    @JsonProperty("chromosome")
    String getChromosome() {
        return chromosome
    }

    @JsonProperty("symbol")
    String getSymbol() {
        return symbol
    }

    @JsonProperty("name")
    String getName() {
        return name
    }

    @JsonProperty("bioType")
    String getBioType() {
        return bioType
    }

    @JsonProperty("geneStart")
    BigInteger getGeneStart() {
        return geneStart
    }

    @JsonProperty("geneEnd")
    BigInteger getGeneEnd() {
        return geneEnd
    }

    @JsonProperty("geneId")
    String getGeneID() {
        return geneID
    }

    @JsonProperty("synonyms")
    List<String> getSynonyms() {
        return synonyms
    }

    @JsonProperty("description")
    String getDescription() {
        return description
    }

    @JsonProperty("strand")
    String getStrand() {
        return strand
    }

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
