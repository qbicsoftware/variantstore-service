package life.qbic.oncostore.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import io.swagger.v3.oas.annotations.media.Schema

/**
 * A consequence of a genomic variant
 *
 * @since: 1.1.0
 */
@EqualsAndHashCode
@CompileStatic
@Schema(name = "Consequence", description = "A variant consequence")
class Consequence implements Comparable {

    /**
     * The genomic allele of a consequence
     */
    String allele
    /**
     * The coding change in HGVS notation of a consequence
     */
    String codingChange
    /**
     * The transcript identifier of a consequence
     */
    String transcriptId
    /**
     * The transcript version of a consequence
     */
    Integer transcriptVersion
    /**
     * The type of a consequence
     */
    String type
    /**
     * The biological type of a consequence
     */
    String bioType
    /**
     * Indicating if the associated transcript is denoted as canonical
     */
    Boolean canonical
    /**
     * The amino acid change of a consequence
     */
    String aaChange
    // the following three positions can be ranges (including -) therefore we will just use (for now) the string as
    // annotated
    /**
     * The cdna position of a consequence
     */
    String cdnaPosition
    /**
     * The cds position of a consequence
     */
    String cdsPosition
    /**
     * The associated protein position of a consequence
     */
    String proteinPosition
    /**
     * The length of the associated protein of a consequence
     */
    Integer proteinLength
    /**
     * The length of the associated cDNA sequence
     */
    Integer cdnaLength
    /**
     * The length of the associated cds sequence
     */
    Integer cdsLength
    /**
     * The impact of a consequence
     */
    String impact
    /**
     * The associated exon of a consequence
     */
    String exon
    /**
     * The associated intron of a consequence
     */
    String intron
    /**
     * The associated strand of a consequence
     */
    Integer strand
    /**
     * The symbol of the associated gene of a consequence
     */
    String geneSymbol
    /**
     * The identifier of the associated gene of a consequence
     */
    String geneId
    /**
     * The feature type of a consequence
     */
    String featureType
    /**
     * The shortest distance from variant to transcript
     */
    Integer distance
    /**
     * The warnings associated with a consequence
     */
    String warnings

    Consequence(String allele, String codingChange, String transcriptId, Integer transcriptVersion, String type,
                String bioType, Boolean canonical, String aaChange, String cdnaPosition, String cdsPosition, String
                        proteinPosition, Integer proteinLength, Integer cdnaLength, Integer cdsLength, String impact,
                String exon, String intron, Integer strand, String geneSymbol, String featureType,
                Integer distance, String warnings) {
        this.allele = allele
        this.codingChange = codingChange
        this.transcriptId = transcriptId
        this.transcriptVersion = transcriptVersion
        this.type = type
        this.bioType = bioType
        this.canonical = canonical
        this.aaChange = aaChange
        this.cdnaPosition = cdnaPosition
        this.cdsPosition = cdsPosition
        this.proteinPosition = proteinPosition
        this.proteinLength = proteinLength
        this.cdnaLength = cdnaLength
        this.cdsLength = cdsLength
        this.impact = impact
        this.exon = exon
        this.intron = intron
        this.strand = strand
        this.geneSymbol = geneSymbol
        this.featureType = featureType
        this.distance = distance
        this.warnings = warnings
    }

    Consequence() {}

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
