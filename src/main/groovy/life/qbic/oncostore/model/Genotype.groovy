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
    final String sampleName
    /**
     * The genotype, encoded as allele values separated by / or |
     */
    final String genotype // GT
    /**
     * The measured read depth of a genotype
     */
    final Integer readDepth // DP
    /**
     * The filter indicating if this genotype was “called”
     */
    final String filter // FT
    /**
     * The phred-scaled genotype likelihoods (PL)
     */
    final String likelihoods
    /**
     * The genotype likelihoods (GL)
     */
    final String genotypeLikelihoods
    /**
     * The genotype likelihoods of heterogeneous ploidy (GLE)
     */
    final String genotypeLikelihoodsHet
    /**
     * The phred-scaled genotype posterior probabilities (GP)
     */
    final String posteriorProbs
    /**
     * The quality (error probability) of a genotype (GQ)
     */
    final Integer genotypeQuality
    /**
     * The haplotype qualities (HQ)
     */
    final String haplotypeQualities
    /**
     * A phase set is defined as a set of phased genotypes to which this genotype belongs (PS)
     */
    final String phaseSet
    /**
     * The phred-scaled probability (PQ)
     */
    final Integer phasingQuality
    /**
     * The alternate allele counts of a genotype
     */
    final String alternateAlleleCounts
    /**
     * The RMS mapping quality (MQ)
     */
    final Integer mappingQuality // MQ RMS mapping quality

    Genotype(htsjdk.variant.variantcontext.Genotype genotypeContext) {
        sampleName = genotypeContext.sampleName
        genotype = genotypeContext.type.toString()
        readDepth = genotypeContext.DP
        filter = genotypeContext.filters ? genotypeContext.filters : ""
        likelihoods = genotypeContext.likelihoodsString
        genotypeLikelihoods = genotypeContext.getExtendedAttribute(VcfConstants.VcfGenotypeAbbreviations
                .GENOTYPELIKELIHOODS.tag, "".intern()) as String
        genotypeLikelihoodsHet = genotypeContext.getExtendedAttribute(VcfConstants.VcfGenotypeAbbreviations
                .GENOTYPELIKELIHOODSHET.tag, "".intern()) as String
        posteriorProbs = genotypeContext.getExtendedAttribute(VcfConstants.VcfGenotypeAbbreviations.POSTERIORPROBS
                .tag, ""
                .intern()) as String
        genotypeQuality = genotypeContext.GQ as Integer
        haplotypeQualities = genotypeContext.getExtendedAttribute(VcfConstants.VcfGenotypeAbbreviations
                .HAPLOTYPEQUALITIES
                .tag, "".intern()) as String
        phaseSet = genotypeContext.getExtendedAttribute(VcfConstants.VcfGenotypeAbbreviations.PHASESET.tag, "".intern
                ()) as String
        phasingQuality = genotypeContext.getExtendedAttribute(VcfConstants.VcfGenotypeAbbreviations.PHASINGQUALITY
                .tag, -1) as Integer
        alternateAlleleCounts = genotypeContext.getExtendedAttribute(VcfConstants.VcfGenotypeAbbreviations
                .ALTERNATEALLELECOUNTS.tag, "".intern()) as String
        mappingQuality = genotypeContext.getExtendedAttribute(VcfConstants.VcfGenotypeAbbreviations.MAPPINGQUALITY
                .tag, -1) as Integer
    }

    Genotype(sample, genotype, depth, filters, likelihoods, genotypeLikelihoods,
             genotypeLikelihoodsHet, posteriorProbs, genotypeQuality, haplotypeQualities,
             phaseSet, phasingQuality, alternateAlleleCounts, mappingQuality) {
        this.sampleName = sample
        this.genotype = genotype
        this.readDepth = depth
        this.filter = filters
        this.likelihoods = likelihoods
        this.genotypeLikelihoods = genotypeLikelihoods
        this.genotypeLikelihoodsHet = genotypeLikelihoodsHet
        this.posteriorProbs = posteriorProbs
        this.genotypeQuality = genotypeQuality
        this.haplotypeQualities = haplotypeQualities
        this.phaseSet = phaseSet
        this.phasingQuality = phasingQuality
        this.alternateAlleleCounts = alternateAlleleCounts
        this.mappingQuality = mappingQuality
    }

    String getSampleName() {
        return sampleName
    }

    String getGenotype() {
        return genotype
    }

    Integer getReadDepth() {
        return readDepth
    }

    String getFilter() {
        return filter
    }

    String getLikelihoods() {
        return likelihoods
    }

    String getGenotypeLikelihoods() {
        return genotypeLikelihoods
    }

    String getGenotypeLikelihoodsHet() {
        return genotypeLikelihoodsHet
    }

    String getPosteriorProbs() {
        return posteriorProbs
    }

    Integer getGenotypeQuality() {
        return genotypeQuality
    }

    String getHaplotypeQualities() {
        return haplotypeQualities
    }

    String getPhaseSet() {
        return phaseSet
    }

    Integer getPhasingQuality() {
        return phasingQuality
    }

    String getAlternateAlleleCounts() {
        return alternateAlleleCounts
    }

    Integer getMappingQuality() {
        return mappingQuality
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
