package life.qbic.oncostore.model

import com.fasterxml.jackson.annotation.JsonProperty

class Gene {

    String chromosome
    String symbol
    String name
    String bioType
    BigInteger geneStart
    BigInteger geneEnd
    String geneID
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

    String getChromosome() {
        return chromosome
    }

    String getSymbol() {
        return symbol
    }

    String getName() {
        return name
    }

    String getBioType() {
        return bioType
    }

    BigInteger getGeneStart() {
        return geneStart
    }

    BigInteger getGeneEnd() {
        return geneEnd
    }

    @JsonProperty("geneId")
    String getGeneID() {
        return geneID
    }

    List<String> getSynonyms() {
        return synonyms
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
