package life.qbic.variantstore.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.builder.Builder
import io.micronaut.core.annotation.Creator
import io.micronaut.core.annotation.Nullable
import io.micronaut.data.annotation.*
import io.micronaut.data.model.DataType
import io.micronaut.data.model.naming.NamingStrategies
import life.qbic.variantstore.util.IntegerListStringConverter
import life.qbic.variantstore.util.VcfConstants

/**
 * A genotype with associated information and measurements
 *
 * @since: 1.0.0
 */
@MappedEntity(namingStrategy = NamingStrategies.LowerCase.class)
@EqualsAndHashCode(excludes =  ["sampleName", "sampleVariants"])
@Builder
class Genotype {

    /**
     * The identifier of a gene
     */
    @GeneratedValue
    @Id
    private Long id
    /**
     * The sample name associated with a genotype
     */
    @Transient
    String sampleName
    /**
     * The genotype, encoded as allele values separated by / or |
     */
    @Nullable
    String genotype // GT
    /**
     * The measured read depth of a genotype
     */
    @Nullable
    Integer readDepth // DP
    /**
     * The filter indicating if this genotype was “called”
     */
    @Nullable
    String filter // FT
    /**
     * The phred-scaled genotype likelihoods (PL)
     */
    @Nullable
    String likelihoods
    /**
     * The genotype likelihoods (GL)
     */
    @Nullable
    String genotypeLikelihoods
    /**
     * The genotype likelihoods of heterogeneous ploidy (GLE)
     */
    @Nullable
    String genotypeLikelihoodsHet
    /**
     * The phred-scaled genotype posterior probabilities (GP)
     */
    @Nullable
    String posteriorProbs
    /**
     * The quality (error probability) of a genotype (GQ)
     */
    @Nullable
    Integer genotypeQuality
    /**
     * The haplotype qualities (HQ)
     */
    @Nullable
    String haplotypeQualities
    /**
     * A phase set is defined as a set of phased genotypes to which this genotype belongs (PS)
     */
    @Nullable
    String phaseSet
    /**
     * The phred-scaled probability (PQ)
     */
    @Nullable
    Integer phasingQuality
    /**
     * The alternate allele counts of a genotype
     */
    @Nullable
    @TypeDef(type = DataType.INTEGER_ARRAY)
    @MappedProperty(type = DataType.STRING, converter = IntegerListStringConverter.class)
    List<Integer> alternateAlleleCounts
    /**
     * The RMS mapping quality (MQ)
     */
    @Nullable
    Integer mappingQuality // MQ RMS mapping quality
    /**
     * The association between sample, variant, vcfinfo, and genotypes
     */
    @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "genotype")
    Set<SampleVariant> sampleVariants

    @Creator
    Genotype() {}

    /**
    * Consturctor for creating Genotype from htsjdk genotype object
     */
    Genotype(htsjdk.variant.variantcontext.Genotype genotypeContext) {
        sampleName = genotypeContext.sampleName
        genotype = genotypeContext.type.toString()
        readDepth = genotypeContext.DP
        filter = genotypeContext.filters ? genotypeContext.filters : ""
        likelihoods = genotypeContext.likelihoodsString
        genotypeLikelihoods = genotypeContext.getExtendedAttribute(VcfConstants.VcfGenotypeAbbreviations
                .GENOTYPELIKELIHOODS.tag, "") as String
        genotypeLikelihoodsHet = genotypeContext.getExtendedAttribute(VcfConstants.VcfGenotypeAbbreviations
                .GENOTYPELIKELIHOODSHET.tag, "") as String
        posteriorProbs = genotypeContext.getExtendedAttribute(VcfConstants.VcfGenotypeAbbreviations.POSTERIORPROBS
                .tag, "") as String
        genotypeQuality = genotypeContext.GQ as Integer
        haplotypeQualities = genotypeContext.getExtendedAttribute(VcfConstants.VcfGenotypeAbbreviations
                .HAPLOTYPEQUALITIES
                .tag, "") as String
        phaseSet = genotypeContext.getExtendedAttribute(VcfConstants.VcfGenotypeAbbreviations.PHASESET.tag, "") as String
        phasingQuality = genotypeContext.getExtendedAttribute(VcfConstants.VcfGenotypeAbbreviations.PHASINGQUALITY
                .tag, -1) as Integer
        alternateAlleleCounts = genotypeContext.getExtendedAttribute(VcfConstants.VcfGenotypeAbbreviations
                .ALTERNATEALLELECOUNTS.tag, [])
        mappingQuality = genotypeContext.getExtendedAttribute(VcfConstants.VcfGenotypeAbbreviations.MAPPINGQUALITY
                .tag, -1) as Integer
    }

    /**
     * Consturctor for creating Genotype that includes sample name
     */
    Genotype(sampleName, String genotype,readDepth, filter, likelihoods, genotypeLikelihoods,
             genotypeLikelihoodsHet, posteriorProbs, genotypeQuality, haplotypeQualities,
             phaseSet, phasingQuality, alternateAlleleCounts, mappingQuality) {
        this.sampleName = sampleName
        this.genotype = genotype
        this.readDepth = readDepth
        this.filter = filter
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

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
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

    List<Integer> getAlternateAlleleCounts() {
        return alternateAlleleCounts
    }

    Integer getMappingQuality() {
        return mappingQuality
    }

    void setSampleName(String sampleName) {
        this.sampleName = sampleName
    }

    void setGenotype(String genotype) {
        this.genotype = genotype
    }

    void setReadDepth(Integer readDepth) {
        this.readDepth = readDepth
    }

    void setFilter(String filter) {
        this.filter = filter
    }

    void setLikelihoods(String likelihoods) {
        this.likelihoods = likelihoods
    }

    void setGenotypeLikelihoods(String genotypeLikelihoods) {
        this.genotypeLikelihoods = genotypeLikelihoods
    }

    void setGenotypeLikelihoodsHet(String genotypeLikelihoodsHet) {
        this.genotypeLikelihoodsHet = genotypeLikelihoodsHet
    }

    void setPosteriorProbs(String posteriorProbs) {
        this.posteriorProbs = posteriorProbs
    }

    void setGenotypeQuality(Integer genotypeQuality) {
        this.genotypeQuality = genotypeQuality
    }

    void setHaplotypeQualities(String haplotypeQualities) {
        this.haplotypeQualities = haplotypeQualities
    }

    void setPhaseSet(String phaseSet) {
        this.phaseSet = phaseSet
    }

    void setPhasingQuality(Integer phasingQuality) {
        this.phasingQuality = phasingQuality
    }

    void setAlternateAlleleCounts(List<Integer> alternateAlleleCounts) {
        this.alternateAlleleCounts = alternateAlleleCounts
    }

    void setMappingQuality(Integer mappingQuality) {
        this.mappingQuality = mappingQuality
    }

    Set<SampleVariant> getSampleVariants() {
        return sampleVariants
    }

    void setSampleVariants(Set<SampleVariant> sampleVariants) {
        this.sampleVariants = sampleVariants
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
