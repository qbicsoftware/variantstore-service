package life.qbic.oncostore.model

import htsjdk.variant.variantcontext.CommonInfo
import htsjdk.variant.vcf.VCFConstants
import life.qbic.oncostore.util.VcfConstants

class VcfInfo {

    // possible reserved sub-fields of the INFO column as defined in the VCF specification
    // http://samtools.github.io/hts-specs/ (VCF 4.1 and 4.2)
    String ancestralAllele
    List<Integer> alleleCount
    List<Float> alleleFrequency
    Integer numberAlleles
    Integer baseQuality // RMS base quality
    String cigar
    Boolean dbSnp
    Boolean hapmapTwo
    Boolean hapmapThree
    Boolean thousandGenomes
    Integer combinedDepth
    Integer endPos
    Integer rms // RMS mapping quality
    Integer mqZero // Number of MAPQ == 0 reads covering this record
    Integer strandBias
    Integer numberSamples //  Number of samples with data
    Boolean somatic
    Boolean validated

    VcfInfo() {}

    VcfInfo(CommonInfo commonInfo) {
        //TODO check if that isn`t AA (amino acid) annotation?
        this.ancestralAllele = commonInfo.getAttributeAsString(VCFConstants.ANCESTRAL_ALLELE_KEY,"")
        this.alleleCount = commonInfo.getAttributeAsIntList(VCFConstants.ALLELE_COUNT_KEY, -1)
        this.alleleFrequency = commonInfo.getAttribute(VCFConstants.ALLELE_FREQUENCY_KEY) as List<Float>
        this.numberAlleles = commonInfo.getAttributeAsInt(VCFConstants.ALLELE_NUMBER_KEY, -1)
        this.baseQuality = commonInfo.getAttributeAsInt(VCFConstants.RMS_BASE_QUALITY_KEY, -1)
        this.cigar = commonInfo.getAttributeAsString(VCFConstants.CIGAR_KEY, "")
        this.dbSnp = commonInfo.getAttributeAsBoolean(VCFConstants.DBSNP_KEY, false)
        this.hapmapTwo = commonInfo.getAttributeAsBoolean(VCFConstants.HAPMAP2_KEY, false)
        this.hapmapThree = commonInfo.getAttributeAsBoolean(VCFConstants.HAPMAP3_KEY, false)
        this.thousandGenomes = commonInfo.getAttributeAsBoolean(VCFConstants.THOUSAND_GENOMES_KEY, false)
        this.combinedDepth = commonInfo.getAttributeAsInt(VCFConstants.DEPTH_KEY, -1)
        this.endPos = commonInfo.getAttributeAsInt(VCFConstants.END_KEY, -1)
        this.rms = commonInfo.getAttributeAsInt(VCFConstants.RMS_MAPPING_QUALITY_KEY, -1)
        this.mqZero = commonInfo.getAttributeAsInt(VCFConstants.MAPPING_QUALITY_ZERO_KEY, -1)
        this.strandBias = commonInfo.getAttributeAsInt(VCFConstants.STRAND_BIAS_KEY, -1)
        this.numberSamples = commonInfo.getAttributeAsInt(VCFConstants.SAMPLE_NUMBER_KEY, -1)
        this.somatic = commonInfo.getAttributeAsBoolean(VCFConstants.SOMATIC_KEY, false)
        this.validated = commonInfo.getAttributeAsBoolean(VCFConstants.VALIDATED_KEY, false)
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

    Boolean getDbSnp() {
        return dbSnp
    }

    Boolean getHapmapTwo() {
        return hapmapTwo
    }

    Boolean getHapmapThree() {
        return hapmapThree
    }

    Boolean getThousandGenomes() {
        return thousandGenomes
    }

    Integer getCombinedDepth() {
        return combinedDepth
    }

    Integer getEndPos() {
        return endPos
    }

    Integer getRms() {
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

    Boolean getSomatic() {
        return somatic
    }

    Boolean getValidated() {
        return validated
    }

    String toVcfFormat() {
        def vcfInfoString = new StringJoiner(VcfConstants.PROPERTY_DELIMITER)
        this.properties.each { it ->
            if (it.key != "class" & it.value != null) {
                def name = it.key.toString().toUpperCase() as VcfConstants.VcfInfoAbbreviations
                vcfInfoString.add("${name.getTag()}${VcfConstants.PROPERTY_DEFINITION_STRING}${it.value.toString()}")
            }
        }
        return vcfInfoString
    }

}
