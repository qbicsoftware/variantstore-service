package life.qbic.oncostore.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name="Consequence", description="A variant consequence")
class Consequence implements Comparable{

    String codingChange
    String transcriptID
    Integer transcriptVersion
    String refSeqID
    String consequenceType
    String bioType
    Boolean canonical
    String aaChange
    Integer aaStart
    Integer aaEnd
    String impact
    Integer strand
    String geneID

    Consequence() {

    }

    @Override
    int compareTo(Object other) {
        Consequence c = (Consequence) other
        int byCoding = this.codingChange <=> c.codingChange
        return byCoding ?: this.transcriptID <=> c.transcriptID
    }

    @JsonProperty("codingChange")
    String getCodingChange() {
        return codingChange
    }

    @JsonProperty("transcriptID")
    String getTranscriptID() {
        return transcriptID
    }

    @JsonProperty("transcriptVersion")
    Integer getTranscriptVersion() {
        return transcriptVersion
    }

    @JsonProperty("bioType")
    String getBioType() {
        return bioType
    }

    @JsonProperty("refseqID")
    String getRefSeqID() {
        return refSeqID
    }

    @JsonProperty("consequenceType")
    String getConsequenceType() {
        return consequenceType
    }

    @JsonProperty("canonical")
    Boolean getCanonical() {
        return canonical
    }

    @JsonProperty("aaChange")
    String getAaChange() {
        return aaChange
    }

    @JsonProperty("aaStart")
    Integer getAaStart() {
        return aaStart
    }

    @JsonProperty("aaEnd")
    Integer getAaEnd() {
        return aaEnd
    }

    @JsonProperty("impact")
    String getImpact() {
        return impact
    }

    @JsonProperty("geneID")
    String getGeneID() {
        return geneID
    }

    @JsonProperty("strand")
    Integer getStrand() {
        return strand
    }

    void setCodingChange(String codingChange) {
        this.codingChange = codingChange
    }

    void setTranscriptID(String transcriptID) {
        this.transcriptID = transcriptID
    }

    void setTranscriptVersion(Integer transcriptVersion) {
        this.transcriptVersion = transcriptVersion
    }

    void setRefSeqID(String refSeqID) {
        this.refSeqID = refSeqID
    }

    void setConsequenceType(String consequenceType) {
        this.consequenceType = consequenceType
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

    void setAaStart(Integer aaStart) {
        this.aaStart = aaStart
    }

    void setAaEnd(Integer aaEnd) {
        this.aaEnd = aaEnd
    }

    void setImpact(String impact) {
        this.impact = impact
    }

    void setGeneID(String geneID) {
        this.geneID = geneID
    }

    void setStrand(Integer strand) {
        this.strand = strand
    }
}
