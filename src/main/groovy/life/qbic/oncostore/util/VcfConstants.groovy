package life.qbic.oncostore.util

class VcfConstants {

    static final String PROPERTY_DELIMITER = ";"
    static final String PROPERTY_DEFINITION_STRING = "="
    static final String GENOTYPE_DELIMITER = ":"

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
