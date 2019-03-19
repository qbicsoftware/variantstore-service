package life.qbic.oncostore.model

class Consequence {

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
    Gene gene

    Consequence() {

    }

    String getCodingChange() {
        return codingChange
    }

    String getTranscriptID() {
        return transcriptID
    }

    Integer getTranscriptVersion() {
        return transcriptVersion
    }

    String getBioType() {
        return bioType
    }

    String getRefSeqID() {
        return refSeqID
    }

    String getConsequenceType() {
        return consequenceType
    }

    Boolean getCanonical() {
        return canonical
    }

    String getAaChange() {
        return aaChange
    }

    Integer getAaStart() {
        return aaStart
    }

    Integer getAaEnd() {
        return aaEnd
    }

    String getImpact() {
        return impact
    }

    Gene getGene() {
        return gene
    }

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

    void setGene(Gene gene) {
        this.gene = gene
    }

    void setStrand(Integer strand) {
        this.strand = strand
    }
}
