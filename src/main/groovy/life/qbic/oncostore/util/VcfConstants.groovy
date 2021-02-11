package life.qbic.oncostore.util

/**
 * Holding constants used in the context of Variant Call Format files
 *
 * @since: 1.1.0
 */
class VcfConstants {

    /**
     * A delimiter for property values
     */
    static final String PROPERTY_DELIMITER = ";"
    /**
     * Assigns values to a property
     */
    static final String PROPERTY_DEFINITION_STRING = "="
    /**
     * A delimiter for genotype properties
     */
    static final String GENOTYPE_DELIMITER = ":"

    /**
     * Valid tags for properties used in the INFO column of a VCF file
     *
     * @since: 1.1.0
     */
    enum VcfInfoAbbreviations{
        ANCESTRALALLELE("AA"),
        ALLELECOUNT("AC"),
        ALLELEFREQUENCY("AF"),
        NUMBERALLELES("AN"),
        BASEQUALITY("BQ"),
        CIGAR("CIGAR"),
        DBSNP("DB"),
        COMBINEDDEPTH("DP"),
        HAPMAPTWO("H2"),
        HAPMAPTHREE("H3"),
        THOUSANDGENOMES("1000G"),
        ENDPOS("END"),
        RMS("MQ"),
        MQZERO("MQ0"),
        STRANDBIAS("SB"),
        NUMBERSAMPLES("NS"),
        SOMATIC("SOMATIC"),
        VALIDATED("VALIDATED")

        VcfInfoAbbreviations(String tag) {
            this.tag = tag
        }

        private final String tag

        String getTag() {
            tag
        }
    }

    /**
     * Valid tags for genotype properties in a VCF file
     *
     * @since: 1.1.0
     */
    enum VcfGenotypeAbbreviations{
        GENOTYPE("GT"),
        READDEPTH("DP"),
        FILTER("FT"),
        LIKELIHOODS("PL"),
        GENOTYPELIKELIHOODS("GL"),
        GENOTYPELIKELIHOODSHET("GLE"),
        POSTERIORPROBS("GP"),
        GENOTYPEQUALITY("GQ"),
        HAPLOTYPEQUALITIES("HQ"),
        PHASESET("PS"),
        PHASINGQUALITY("PQ"),
        ALTERNATEALLELECOUNTS("EC"),
        MAPPINGQUALITY("MQ")

        VcfGenotypeAbbreviations(String tag) {
            this.tag = tag
        }
        private final String tag

        String getTag() {
            tag
        }
    }
}
