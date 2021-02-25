package life.qbic.variantstore.util

/**
 * This enum defines possible consequences types of variants.
 *
 * Note: It might be necessary to extend this list.
 *
 * @since: 1.0.0
 */
enum ConsequenceTypes {

    /**
     * The variant types
     */
    TRANSCRIPT_ABLATION("transcript_ablation"), SPLICE_ACCEPTOR_VARIANT("splice_acceptor_variant"),
    SPLICE_DONOR_VARIANT("splice_donor_variant"), STOP_GAINED("stop_gained"),
    FRAMESHIFT_VARIANT("frameshift_variant"), STOP_LOST("stop_lost"), START_LOST("start_lost"),
    TRANSCRIPT_AMPLIFICATION("transcript_amplification"), INFRAME_INSERTION("inframe_insertion"),
    INFRAME_DELETION("inframe_deletion"), MISSENSE_VARIANT("missense_variant"),
    PROTEIN_ALTERING_VARIANT("protein_altering_variant"), SPLICE_REGION_VARINAT("splice_region_variant"),
    INCOMPLETE_TERMINAL_CODON_VARIANT("incomplete_terminal_codon_variant"), START_RETAINED_VARIANT("start_retained_variant"),
    STOP_RETAINED_VARIANT("stop_retained_variant"), SYNONYMOUS_VARIANT("synonymous_variant"),
    CODING_SEQUENCE_VARINAT("coding_sequence_variant"), MATURE_MIRNA_VARIANT("mature_miRNA_variant"),
    FIVE_PRIME_UTR_VARIANT("5_prime_UTR_variant"), THREE_PRIME_UTR_VARIANT("3_prime_UTR_variant"),
    NON_CODING_TRANSCRIPT_EXON_VARIANT("non_coding_transcript_exon_variant"), INTRON_VARIANT("intron_variant"),
    NMD_TRANSCRIPT_VARIANT("nmd_transcript_variant"), NON_CODING_TRANSCRIPT_VARIANT("non_coding_transcript_variant"),
    UPSTREAM_GENE_VARIANT("upstream_gene_variant"), DOWNSTREAM_GENE_VARIANT("downstream_gene_variant"),
    TFBS_ABLATION("tfbs_ablation"), TFBS_AMPLIFICATION("tfbs_amplification"),
    TF_BINDING_SITE_VARIANT("tf_binding_site_variant"), REGULATORY_REGION_ABLATION("regulatory_region_ablation"),
    REGULATORY_REGION_AMPLIFICATION("regulatory_region_amplification"), FEATURE_ELONGATION("feature_elongation"),
    REGULATORY_REGION_VARIANT("regulatory_region_variant"), FEATURE_TRUNCATION("feature_truncation"),
    INTERGENIC_VARIANT("intergenic_variant")

    private final String tag

    ConsequenceTypes(String tag) {
        this.tag = tag
    }

    String getTag() {
        tag
    }

    /**
     * Perform consequence type to LOINC mapping
     */
    static ConsequenceTypesLoinc getLoincMapping(String term) {
        switch (term) {
            case TRANSCRIPT_ABLATION.tag:
                return ""
            case SPLICE_ACCEPTOR_VARIANT.tag:
                return ""
            case SPLICE_DONOR_VARIANT.tag:
                return ""
            case STOP_GAINED.tag:
                return ConsequenceTypesLoinc.STOP_CODON_MUTATION
            case FRAMESHIFT_VARIANT.tag:
                return ConsequenceTypesLoinc.FRAMESHIFT
            case STOP_LOST.tag:
                return ConsequenceTypesLoinc.STOP_CODON_MUTATION
            case START_LOST.tag:
                return ConsequenceTypesLoinc.STOP_CODON_MUTATION
            case TRANSCRIPT_AMPLIFICATION.tag:
                return ""
            case INFRAME_INSERTION.tag:
                return ConsequenceTypesLoinc.INSERTION
            case INFRAME_DELETION.tag:
                return ConsequenceTypesLoinc.DELETION
            case MISSENSE_VARIANT.tag:
                return ConsequenceTypesLoinc.MISSENSE
            case PROTEIN_ALTERING_VARIANT.tag:
                return ""
            case SPLICE_REGION_VARINAT.tag:
                return ""
            case INCOMPLETE_TERMINAL_CODON_VARIANT.tag:
                return ""
            case START_RETAINED_VARIANT.tag:
                return ""
            case STOP_RETAINED_VARIANT.tag:
                return ""
            case SYNONYMOUS_VARIANT.tag:
                return ""
            case CODING_SEQUENCE_VARINAT.tag:
                return ""
            case MATURE_MIRNA_VARIANT.tag:
                return ""
            case FIVE_PRIME_UTR_VARIANT.tag:
                return ""
            case THREE_PRIME_UTR_VARIANT.tag:
                return ""
            case NON_CODING_TRANSCRIPT_EXON_VARIANT.tag:
                return ""
            case INTRON_VARIANT.tag:
                return ""
            case NMD_TRANSCRIPT_VARIANT.tag:
                return ""
            case NON_CODING_TRANSCRIPT_VARIANT.tag:
                return ""
            case UPSTREAM_GENE_VARIANT.tag:
                return ConsequenceTypesLoinc.SILENT
            case DOWNSTREAM_GENE_VARIANT.tag:
                return ConsequenceTypesLoinc.SILENT
            case TFBS_ABLATION.tag:
                return ""
            case TFBS_AMPLIFICATION.tag:
                return ""
            case TF_BINDING_SITE_VARIANT.tag:
                return ""
            case REGULATORY_REGION_ABLATION.tag:
                return ""
            case REGULATORY_REGION_AMPLIFICATION.tag:
                return ""
            case FEATURE_ELONGATION.tag:
                return ""
            case REGULATORY_REGION_VARIANT.tag:
                return ""
            case FEATURE_TRUNCATION.tag:
                return ""
            case INTERGENIC_VARIANT.tag:
                return ""
        }
    }

}