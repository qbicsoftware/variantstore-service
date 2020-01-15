package life.qbic.oncostore.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name="Consequence", description="A variant consequence")
class Consequence implements Comparable{


    String allele
    String codingChange
    String transcriptId
    Integer transcriptVersion
    String type
    String bioType
    Boolean canonical
    String aaChange
    // the following three positions can be ranges (including -) therefore we will just use (for now) the string as annotated
    String cdnaPosition
    String cdsPosition
    String proteinPosition
    Integer proteinLength
    Integer cdnaLength
    Integer cdsLength
    String impact
    String exon
    String intron
    Integer strand
    String geneSymbol
    String geneId
    String featureType
    Integer distance
    String warnings

    Consequence() {

    }

    @Override
    int compareTo(Object other) {
        Consequence c = (Consequence) other
        int byCoding = this.codingChange <=> c.codingChange
        return byCoding ?: this.transcriptId <=> c.transcriptId
    }

    @JsonProperty("allele")
    String getAllele() {
        return allele
    }

    @JsonProperty("codingChange")
    String getCodingChange() {
        return codingChange
    }

    @JsonProperty("transcriptId")
    String getTranscriptId() {
        return transcriptId
    }

    @JsonProperty("transcriptVersion")
    Integer getTranscriptVersion() {
        return transcriptVersion
    }

    @JsonProperty("bioType")
    String getBioType() {
        return bioType
    }


    @JsonProperty("consequenceType")
    String getType() {
        return type
    }

    @JsonProperty("canonical")
    Boolean getCanonical() {
        return canonical
    }

    @JsonProperty("aaChange")
    String getAaChange() {
        return aaChange
    }

    @JsonProperty("proteinPosition")
    String getProteinPosition() {
        return proteinPosition
    }

    @JsonProperty("impact")
    String getImpact() {
        return impact
    }

    @JsonProperty("geneId")
    String getGeneId() {
        return geneId
    }

    @JsonProperty("strand")
    Integer getStrand() {
        return strand
    }

    @JsonProperty("proteinLength")
    Integer getProteinLength() {
        return proteinLength
    }

    @JsonProperty("cdnaPos")
    String getCdnaPosition() {
        return cdnaPosition
    }

    @JsonProperty("cdnaLength")
    Integer getCdnaLength() {
        return cdnaLength
    }

    @JsonProperty("cdsPos")
    String getCdsPosition() {
        return cdsPosition
    }

    @JsonProperty("cdsLength")
    Integer getCdsLength() {
        return cdsLength
    }

    @JsonProperty("geneSymbol")
    String getGeneSymbol() {
        return geneSymbol
    }

    @JsonProperty("featureType")
    String getFeatureType() {
        return featureType
    }

    @JsonProperty("distance")
    Integer getDistance() {
        return distance
    }

    @JsonProperty("warnings")
    String getWarnings() {
        return warnings
    }

    @JsonProperty("exon")
    String getExon() {
        return exon
    }

    @JsonProperty("intron")
    String getIntron() {
        return intron
    }

    void setCdsLength(Integer cdsLength) {
        this.cdsLength = cdsLength
    }

    void setAllele(String allele) {
        this.allele = allele
    }

    void setCodingChange(String codingChange) {
        this.codingChange = codingChange
    }

    void setTranscriptId(String transcriptId) {
        this.transcriptId = transcriptId
    }

    void setTranscriptVersion(Integer transcriptVersion) {
        this.transcriptVersion = transcriptVersion
    }

    void setType(String consequenceType) {
        this.type = consequenceType
    }

    void setBioType(String bioType) {
        this.bioType = bioType
    }

    void setCanonical(Boolean canonical) {
        this.canonical = canonical
    }

    void setAaChange(String aaChange) {
        this.aaChange = aaChange
    }

    void setProteinPosition(String protPosition) {
        this.proteinPosition = protPosition
    }

    void setImpact(String impact) {
        this.impact = impact
    }

    void setGeneId(String geneId) {
        this.geneId = geneId
    }

    void setStrand(Integer strand) {
        this.strand = strand
    }

    void setProteinLength(Integer protLength) {
        this.proteinLength = protLength
    }

    void setCdnaPosition(String cdnaPos) {
        this.cdnaPosition = cdnaPos
    }

    void setCdsPosition(String cdsPos) {
        this.cdsPosition = cdsPos
    }

    void setCdnaLength(Integer cdnaLength) {
        this.cdnaLength = cdnaLength
    }

    void setGeneSymbol(String geneSymbol) {
        this.geneSymbol = geneSymbol
    }

    void setFeatureType(String featureType) {
        this.featureType = featureType
    }

    void setDistance(Integer distance) {
        this.distance = distance
    }

    void setWarnings(String warnings) {
        this.warnings = warnings
    }

    void setExon(String exon) {
        this.exon = exon
    }

    void setIntron(String intron) {
        this.intron = intron
    }
}
