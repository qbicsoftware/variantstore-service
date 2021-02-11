package life.qbic.oncostore.model

import groovy.transform.EqualsAndHashCode
import life.qbic.oncostore.util.VcfConstants

/**
 * A genotype with associated information and measurements
 *
 * @since: 1.0.0
 */
@EqualsAndHashCode(includes = 'genotype, readDepth, filter, likelihoods, genotypeLikelihoods, genotypeLikelihoodsHet, posteriorProbs, genotypeQuality, haplotypeQualities, phaseSet, phasingQuality, alternateAlleleCounts, mappingQuality')
class Genotype {

    /**
     * The sample name associated with a genotype
     */
    String sampleName
    /**
     * The genotype, encoded as allele values separated by / or |
     */
    String genotype // GT
    /**
     * The measured read depth of a genotype
     */
    Integer readDepth // DP
    /**
     * The filter indicating if this genotype was “called”
     */
    String filter // FT
    /**
     * The phred-scaled genotype likelihoods (PL)
     */
    String likelihoods
    /**
     * The genotype likelihoods (GL)
     */
    String genotypeLikelihoods
    /**
     * The genotype likelihoods of heterogeneous ploidy (GLE)
     */
    String genotypeLikelihoodsHet
    /**
     * The phred-scaled genotype posterior probabilities (GP)
     */
    String posteriorProbs
    /**
     * The quality (error probability) of a genotype (GQ)
     */
    Integer genotypeQuality
    /**
     * The haplotype qualities (HQ)
     */
    String haplotypeQualities
    /**
     * A phase set is defined as a set of phased genotypes to which this genotype belongs (PS)
     */
    String phaseSet
    /**
     * The phred-scaled probability (PQ)
     */
    Integer phasingQuality
    /**
     * The alternate allele counts of a genotype
     */
    String alternateAlleleCounts
    /**
     * The RMS mapping quality (MQ)
     */
    Integer mappingQuality // MQ RMS mapping quality

    Genotype(htsjdk.variant.variantcontext.Genotype genotype) {
        setSampleName(genotype.sampleName)
        setGenotype(genotype.type.toString())
        setReadDepth(genotype.DP)
        genotype.filters ? setFilter(genotype.filters) : setFilter("")
        setLikelihoods(genotype.likelihoodsString)
        setGenotypeLikelihoods(genotype.getExtendedAttribute(VcfConstants.VcfGenotypeAbbreviations
                .GENOTYPELIKELIHOODS.tag, "".intern()) as String)
        setGenotypeLikelihoodsHet(genotype.getExtendedAttribute(VcfConstants.VcfGenotypeAbbreviations
                .GENOTYPELIKELIHOODSHET.tag, "".intern()) as String)
        setPosteriorProbs(genotype.getExtendedAttribute(VcfConstants.VcfGenotypeAbbreviations.POSTERIORPROBS.tag, ""
                .intern()) as String)
        setGenotypeQuality(genotype.GQ as Integer)
        setHaplotypeQualities(genotype.getExtendedAttribute(VcfConstants.VcfGenotypeAbbreviations.HAPLOTYPEQUALITIES
                .tag, "".intern()) as String)
        setPhaseSet(genotype.getExtendedAttribute(VcfConstants.VcfGenotypeAbbreviations.PHASESET.tag, "".intern()) as
                String)
        setPhasingQuality(genotype.getExtendedAttribute(VcfConstants.VcfGenotypeAbbreviations.PHASINGQUALITY.tag, -1)
                as Integer)
        setAlternateAlleleCounts(genotype.getExtendedAttribute(VcfConstants.VcfGenotypeAbbreviations
                .ALTERNATEALLELECOUNTS.tag, "".intern())
                as String)
        setMappingQuality(genotype.getExtendedAttribute(VcfConstants.VcfGenotypeAbbreviations.MAPPINGQUALITY.tag, -1)
                as Integer)
    }

    Genotype() { }

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

    /**
     * Generates content in Variant Call Format (VCF) for a genotype.
     * @return the format string (defining the contained information) and genotype content in Variant Call Format
     */
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
