package life.qbic.variantstore.model

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
    final String allele
    /**
     * The coding change in HGVS notation of a consequence
     */
    final String codingChange
    /**
     * The transcript identifier of a consequence
     */
    final String transcriptId
    /**
     * The transcript version of a consequence
     */
    final Integer transcriptVersion
    /**
     * The type of a consequence
     */
    final String type
    /**
     * The biological type of a consequence
     */
    final String bioType
    /**
     * Indicating if the associated transcript is denoted as canonical
     */
    final Boolean canonical
    /**
     * The amino acid change of a consequence
     */
    final String aaChange
    // the following three positions can be ranges (including -) therefore we will just use (for now) the string as
    // annotated
    /**
     * The cdna position of a consequence
     */
    final String cdnaPosition
    /**
     * The cds position of a consequence
     */
    final String cdsPosition
    /**
     * The associated protein position of a consequence
     */
    final String proteinPosition
    /**
     * The length of the associated protein of a consequence
     */
    final Integer proteinLength
    /**
     * The length of the associated cDNA sequence
     */
    final Integer cdnaLength
    /**
     * The length of the associated cds sequence
     */
    final Integer cdsLength
    /**
     * The impact of a consequence
     */
    final String impact
    /**
     * The associated exon of a consequence
     */
    final String exon
    /**
     * The associated intron of a consequence
     */
    final String intron
    /**
     * The associated strand of a consequence
     */
    final Integer strand
    /**
     * The symbol of the associated gene of a consequence
     */
    final String geneSymbol
    /**
     * The identifier of the associated gene of a consequence
     */
    final String geneId
    /**
     * The feature type of a consequence
     */
    final String featureType
    /**
     * The shortest distance from variant to transcript
     */
    final Integer distance
    /**
     * The warnings associated with a consequence
     */
    final String warnings

    Consequence(String allele, String codingChange, String transcriptId, Integer transcriptVersion, String type,
                String bioType, Boolean canonical, String aaChange, String cdnaPosition, String cdsPosition, String
                        proteinPosition, Integer proteinLength, Integer cdnaLength, Integer cdsLength, String impact,
                String exon, String intron, Integer strand, String geneSymbol, String geneId, String featureType,
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
        this.geneId = geneId
        this.featureType = featureType
        this.distance = distance
        this.warnings = warnings
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
}
