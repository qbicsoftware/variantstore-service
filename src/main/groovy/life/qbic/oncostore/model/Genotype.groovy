package life.qbic.oncostore.model

import groovy.transform.EqualsAndHashCode
import htsjdk.variant.vcf.VCFConstants
import life.qbic.oncostore.util.VcfConstants

@EqualsAndHashCode(includes = 'genotype, readDepth, filter, likelihoods, genotypeLikelihoods, genotypeLikelihoodsHet, posteriorProbs, genotypeQuality, haplotypeQualities, phaseSet, phasingQuality, alternateAlleleCounts, mappingQuality')
class Genotype{

    String sampleName
    String genotype // GT
    Integer readDepth // DP
    String filter // FT
    String likelihoods // PL (phred-scaled genotype likelihoods)
    String genotypeLikelihoods // GL genotype likelihoods
    String genotypeLikelihoodsHet // GLE genotype likelihoods of heterogeneous ploidy
    String posteriorProbs // GP phred-scaled genotype posterior probabilities
    Integer genotypeQuality // GQ
    String haplotypeQualities // HQ
    String phaseSet // PS A phase set is defined as a set of phased genotypes to which this genotype belongs
    Integer phasingQuality // PQ phred-scaled probability
    String alternateAlleleCounts
    Integer mappingQuality // MQ RMS mapping quality

    Genotype(htsjdk.variant.variantcontext.Genotype genotype) {
        setSampleName(genotype.sampleName)
        setGenotype(genotype.type.toString())
        setReadDepth(genotype.DP)
        genotype.filters ? setFilter(genotype.filters) : setFilter("")
        setLikelihoods(genotype.likelihoodsString)
        //TODO use constants
        setGenotypeLikelihoods(genotype.getExtendedAttribute("GL", "") as String)
        setGenotypeLikelihoodsHet(genotype.getExtendedAttribute("GLE", "") as String)
        setPosteriorProbs(genotype.getExtendedAttribute(VCFConstants.GENOTYPE_POSTERIORS_KEY, "") as String)
        setGenotypeQuality(genotype.GQ)
        setHaplotypeQualities(genotype.getExtendedAttribute(VCFConstants.HAPLOTYPE_QUALITY_KEY, "") as String)
        setPhaseSet(genotype.getExtendedAttribute(VCFConstants.PHASE_SET_KEY, "") as String)
        setPhasingQuality(genotype.getExtendedAttribute(VCFConstants.PHASE_QUALITY_KEY, -1) as Integer)
        setAlternateAlleleCounts(genotype.getExtendedAttribute(VCFConstants.EXPECTED_ALLELE_COUNT_KEY, "") as String)
        setMappingQuality(genotype.getExtendedAttribute(VCFConstants.RMS_MAPPING_QUALITY_KEY, -1) as Integer)
    }

    Genotype() {

    }

    String getSampleName() {
        return sampleName
    }

    void setSampleName(String sampleName) {
        this.sampleName = sampleName
    }

    String getGenotype() {
        return genotype
    }

    void setGenotype(String genotype) {
        this.genotype = genotype
    }

    Integer getReadDepth() {
        return readDepth
    }

    void setReadDepth(Integer readDepth) {
        this.readDepth = readDepth
    }

    String getFilter() {
        return filter
    }

    void setFilter(String filter) {
        this.filter = filter
    }

    String getLikelihoods() {
        return likelihoods
    }

    void setLikelihoods(String likelihoods) {
        this.likelihoods = likelihoods
    }

    String getGenotypeLikelihoods() {
        return genotypeLikelihoods
    }

    void setGenotypeLikelihoods(String genotypeLikelihoods) {
        this.genotypeLikelihoods = genotypeLikelihoods
    }

    String getGenotypeLikelihoodsHet() {
        return genotypeLikelihoodsHet
    }

    void setGenotypeLikelihoodsHet(String genotypeLikeliehoodsHet) {
        this.genotypeLikelihoodsHet = genotypeLikeliehoodsHet
    }

    String getPosteriorProbs() {
        return posteriorProbs
    }

    void setPosteriorProbs(String posteriorProbs) {
        this.posteriorProbs = posteriorProbs
    }

    Integer getGenotypeQuality() {
        return genotypeQuality
    }

    void setGenotypeQuality(Integer genotypeQuality) {
        this.genotypeQuality = genotypeQuality
    }

    String getHaplotypeQualities() {
        return haplotypeQualities
    }

    void setHaplotypeQualities(String haplotypeQualities) {
        this.haplotypeQualities = haplotypeQualities
    }

    String getPhaseSet() {
        return phaseSet
    }

    void setPhaseSet(String phaseSet) {
        this.phaseSet = phaseSet
    }

    Integer getPhasingQuality() {
        return phasingQuality
    }

    void setPhasingQuality(Integer phasingQuality) {
        this.phasingQuality = phasingQuality
    }

    String getAlternateAlleleCounts() {
        return alternateAlleleCounts
    }

    void setAlternateAlleleCounts(String alternateAlleleCounts) {
        this.alternateAlleleCounts = alternateAlleleCounts
    }

    Integer getMappingQuality() {
        return mappingQuality
    }

    void setMappingQuality(Integer mappingQuality) {
        this.mappingQuality = mappingQuality
    }

    List<String> toVcfFormat() {
        def formatString = new StringJoiner(VcfConstants.GENOTYPE_DELIMITER)
        def genotypeString = new StringJoiner(VcfConstants.GENOTYPE_DELIMITER)

        this.properties.each { it ->
            if (it.key != "class" & it.key != "sampleName" & it.value != -1 & it.value != false & it.value != "") {
                def name = it.key.toString().toUpperCase() as VcfConstants.VcfGenotypeAbbreviations
                if (it.value != null) {
                    formatString.add(name.getTag())
                    genotypeString.add(it.value.toString())
                }
            }
        }
        return [formatString.toString(), genotypeString.toString()]
    }
}
