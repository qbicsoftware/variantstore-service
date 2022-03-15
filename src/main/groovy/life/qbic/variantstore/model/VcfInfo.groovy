package life.qbic.variantstore.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.builder.Builder
import htsjdk.variant.variantcontext.CommonInfo
import io.micronaut.core.annotation.Creator
import io.micronaut.core.annotation.Nullable
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.MappedProperty
import io.micronaut.data.annotation.Relation
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import io.micronaut.data.model.naming.NamingStrategies
import life.qbic.variantstore.util.FloatListStringConverter
import life.qbic.variantstore.util.IntegerListStringConverter
import life.qbic.variantstore.util.VcfConstants

/**
 * A class to store information as given in the INFO column as defined in the Variant Call Format specification
 *
 * @since: 1.0.0
 */
@MappedEntity(namingStrategy = NamingStrategies.LowerCase.class)
@EqualsAndHashCode
@Builder
class VcfInfo {

    // possible reserved sub-fields of the INFO column as defined in the VCF specification
    // http://samtools.github.io/hts-specs/ (VCF 4.1 and 4.2)
    //TODO deal with future releases?
    /**
     * The identifier of a gene
     */
    @GeneratedValue
    @Id
    private Long id
    /**
     * The ancestral allele
     **/
    String ancestralAllele
    /**
     * The allele count
     **/
    @TypeDef(type = DataType.INTEGER_ARRAY)
    @MappedProperty(type = DataType.STRING, converter = IntegerListStringConverter.class)
    List<Integer> alleleCount
    /**
     * The allele frequency
     **/
    @TypeDef(type = DataType.FLOAT_ARRAY)
    @MappedProperty(type = DataType.STRING, converter = FloatListStringConverter.class)
    List<Float> alleleFrequency
    /**
     * The number of alleles
     **/
    Integer numberAlleles
    /**
     * The RMS base quality
     **/
    Integer baseQuality
    /**
     * The cigar string describing how to align an alternate allele to the reference allele
     **/
    String cigar
    /**
     * Membership in dbSNP ?
     **/
    boolean dbSnp
    /**
     * Membership in hapmap2 ?
     **/
    boolean hapmapTwo
    /**
     * Membership in hapmap3 ?
     **/
    boolean hapmapThree
    /**
     * Membership in 1000 Genomes ?
     **/
    boolean thousandGenomes
    /**
     * The combined depth (DP) across samples
     **/
    Integer combinedDepth
    /**
     * The end position of the described variant
     **/
    Integer endPos
    /**
     * The RMS mapping quality (MQ)
     **/
    Float rms
    /**
     * The number of MAPQ==0 reads covering this record (MQ0)
     **/
    Integer mqZero
    /**
     * The strand bias at this position
     **/
    Integer strandBias
    /**
     * The number of samples with data
     **/
    Integer numberSamples
    /**
     * Indicates that the record is a somatic mutation
     **/
    boolean somatic
    /**
     * Validated by follow-up experiment
     **/
    boolean validated
    /**
     * The association between sample, variant, vcfinfo, and genotypes
     */
    @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "vcfinfo")
    Set<SampleVariant> sampleVariants

    @Creator
    VcfInfo() {}

    VcfInfo(CommonInfo commonInfo) {
        this.ancestralAllele = commonInfo.getAttributeAsString(VcfConstants.VcfInfoAbbreviations.ANCESTRALALLELE.tag,
                "")
        this.alleleCount = commonInfo.getAttributeAsList(VcfConstants.VcfInfoAbbreviations.ALLELECOUNT.tag) as List<Integer> ?: []
        this.alleleFrequency = commonInfo.getAttributeAsList(VcfConstants.VcfInfoAbbreviations.ALLELEFREQUENCY.tag) as List<Float> ?: []
        this.numberAlleles = commonInfo.getAttributeAsInt(VcfConstants.VcfInfoAbbreviations.NUMBERALLELES.tag, -1)
        this.baseQuality = commonInfo.getAttributeAsInt(VcfConstants.VcfInfoAbbreviations.BASEQUALITY.tag, -1 )
        this.cigar = commonInfo.getAttributeAsString(VcfConstants.VcfInfoAbbreviations.CIGAR.tag, '')
        this.dbSnp = commonInfo.getAttributeAsBoolean(VcfConstants.VcfInfoAbbreviations.DBSNP.tag, false)
        this.hapmapTwo = commonInfo.getAttributeAsBoolean(VcfConstants.VcfInfoAbbreviations.HAPMAPTWO.tag, false)
        this.hapmapThree = commonInfo.getAttributeAsBoolean(VcfConstants.VcfInfoAbbreviations.HAPMAPTHREE.tag, false)
        this.thousandGenomes = commonInfo.getAttributeAsBoolean(VcfConstants.VcfInfoAbbreviations.THOUSANDGENOMES
                .tag, false)
        this.combinedDepth = commonInfo.getAttributeAsInt(VcfConstants.VcfInfoAbbreviations.COMBINEDDEPTH.tag, -1)
        this.endPos = commonInfo.getAttributeAsInt(VcfConstants.VcfInfoAbbreviations.ENDPOS.tag, -1)
        this.rms = commonInfo.getAttributeAsDouble(VcfConstants.VcfInfoAbbreviations.RMS.tag, -1) as Float
        this.mqZero = commonInfo.getAttributeAsInt(VcfConstants.VcfInfoAbbreviations.MQZERO.tag, -1)
        this.strandBias = commonInfo.getAttributeAsInt(VcfConstants.VcfInfoAbbreviations.STRANDBIAS.tag, -1)
        this.numberSamples = commonInfo.getAttributeAsInt(VcfConstants.VcfInfoAbbreviations.NUMBERSAMPLES.tag, -1)
        this.somatic = commonInfo.getAttributeAsBoolean(VcfConstants.VcfInfoAbbreviations.SOMATIC.tag, false)
        this.validated = commonInfo.getAttributeAsBoolean(VcfConstants.VcfInfoAbbreviations.VALIDATED.tag, false)
    }

    VcfInfo(String ancestralAllele, List<Integer> alleleCount, List<Float> alleleFrequency, Integer numberAlleles,
            Integer baseQuality, String cigar, boolean dbSnp, boolean hapmapTwo, boolean hapmapThree, boolean thousandGenomes,
            Integer combinedDepth, Integer endPos, Float rms, Integer mqZero, Integer strandBias, Integer numberSamples,
            boolean somatic, boolean validated) {
        this.ancestralAllele = ancestralAllele
        this.alleleCount = alleleCount
        this.alleleFrequency = alleleFrequency
        this.numberAlleles = numberAlleles
        this.baseQuality = baseQuality
        this.cigar = cigar
        this.dbSnp = dbSnp
        this.hapmapTwo = hapmapTwo
        this.hapmapThree = hapmapThree
        this.thousandGenomes = thousandGenomes
        this.combinedDepth = combinedDepth
        this.endPos = endPos
        this.rms = rms
        this.mqZero = mqZero
        this.strandBias = strandBias
        this.numberSamples = numberSamples
        this.somatic = somatic
        this.validated = validated
    }

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    String getAncestralAllele() {
        return ancestralAllele
    }

    List<Integer> getAlleleCount() {
        return alleleCount
    }

    List<Float> getAlleleFrequency() {
        return alleleFrequency
    }

    Integer getNumberAlleles() {
        return numberAlleles
    }

    Integer getBaseQuality() {
        return baseQuality
    }

    String getCigar() {
        return cigar
    }

    boolean isDbSnp() {
        return dbSnp
    }

    boolean isHapmapTwo() {
        return hapmapTwo
    }

    boolean isHapmapThree() {
        return hapmapThree
    }

    boolean isThousandGenomes() {
        return thousandGenomes
    }

    Integer getCombinedDepth() {
        return combinedDepth
    }

    Integer getEndPos() {
        return endPos
    }

    Float getRms() {
        return rms
    }

    Integer getMqZero() {
        return mqZero
    }

    Integer getStrandBias() {
        return strandBias
    }

    Integer getNumberSamples() {
        return numberSamples
    }

    boolean isSomatic() {
        return somatic
    }

    boolean isValidated() {
        return validated
    }

    void setAncestralAllele(@Nullable String ancestralAllele) {
        this.ancestralAllele = ancestralAllele
    }

    void setCigar(@Nullable String cigar) {
        this.cigar = cigar
    }

    void setAlleleCount(List<Integer> alleleCount) {
        this.alleleCount = alleleCount
    }

    void setAlleleFrequency(List<Float> alleleFrequency) {
        this.alleleFrequency = alleleFrequency
    }
    /**
     * Generate representation in INFO column format.
     *
     * @return the content of an INFO column as specified in a Variant Call Format file
     */
    String toVcfFormat() {
        def vcfInfoString = new StringJoiner(VcfConstants.PROPERTY_DELIMITER)
        this.properties.each { it ->
            if (it.key != "class" & it.value != null & it.value != -1 & it.value != false & it.value != [] & it.value
                    != "") {
                def name = it.key.toString().toUpperCase() as VcfConstants.VcfInfoAbbreviations
                vcfInfoString.add("${name.getTag()}${VcfConstants.PROPERTY_DEFINITION_STRING}${it.value.toString()}")
            }
        }
        return vcfInfoString
    }
}
