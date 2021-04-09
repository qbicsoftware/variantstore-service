package life.qbic.variantstore.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import htsjdk.variant.variantcontext.VariantContext
import io.micronaut.core.annotation.Creator
import io.micronaut.data.annotation.*
import io.micronaut.data.model.naming.NamingStrategies
import io.swagger.v3.oas.annotations.media.Schema

/**
 * A variant with all accompanied information
 *
 * @since: 1.0.0
 */
@MappedEntity(namingStrategy = NamingStrategies.LowerCase)
@EqualsAndHashCode
@Schema(name = "Variant", description = "A genomic variant")
class Variant implements SimpleVariantContext, Comparable {

    /**
     * The id of a variant
     */
    @GeneratedValue
    @Id
    private Long id
    /**
     * The identifier (UUID) of a given variant
     */
    String identifier
    /**
     * The database identifier (i.e. DBSNP) of a given variant
     */
    @MappedProperty("databaseidentifier")
    String databaseIdentifier
    /**
     * The chromosome of a given variant
     */
    @MappedProperty("chr")
    String chromosome
    /**
     * The start position of a given variant
     */
    @MappedProperty("start")
    BigInteger startPosition
    /**
     * The end position of a given variant
     */
    @MappedProperty("end")
    BigInteger endPosition
    /**
     * The reference allele of a given variant
     */
    @MappedProperty("ref")
    String referenceAllele
    /**
     * The observed allele of a given variant
     */
    @MappedProperty("obs")
    String observedAllele
    /**
     * The consequences of a given variant
     */
    @Transient
    @Relation(value = Relation.Kind.MANY_TO_MANY, mappedBy = "variants")
    //ArrayList consequences
    private Set<Consequence> consequences = new HashSet<>()
    /**
     * Describes whether a given variant is somatic
     */
    @MappedProperty("issomatic")
    Boolean isSomatic
    /**
     * The information given in a VCF file for a given variant
     */
    @Transient
    VcfInfo vcfInfo
    /**
     * The genotype information for a given variant
     */
    @Transient
    List<Genotype> genotypes

    @Relation(value = Relation.Kind.MANY_TO_MANY, mappedBy = "variants")
    private Set<ReferenceGenome> referenceGenomes = new HashSet<>()

    @Creator
    Variant() {}

    // create variant object from htsjdk variant context, given annotation type
    Variant(VariantContext context, String annotationType) {
        chromosome = context.contig
        startPosition = context.start
        endPosition = context.end
        referenceAllele = context.reference.toString().replace("*", "")
        observedAllele = context.alternateAlleles.join(',')
        vcfInfo = new VcfInfo(context.getCommonInfo())
        databaseIdentifier = context.getID()
        List<Genotype> genotypes = []
        consequences = annotationType ? context.getAttributeAsList(annotationType) : null
        context.getGenotypes().each { genotype -> genotypes.add(new Genotype(genotype))
        }

        if (genotypes.empty) genotypes.add(new Genotype())
        this.genotypes = genotypes
    }



    void setId(Long id) {
        this.id = id
    }

    Long getId() {
        return id
    }

    @Override
    int compareTo(Object other) {
        Variant v = (Variant) other
        return identifier <=> v.identifier
    }

    @Override
    void setIdentifier(String identifier) {
        this.identifier = identifier
    }

    void setChromosome(String chromosome) {
        this.chromosome = chromosome
    }

    void setStartPosition(BigInteger startPosition) {
        this.startPosition = startPosition
    }

    void setEndPosition(BigInteger endPosition) {
        this.endPosition = endPosition
    }

    void setReferenceAllele(String referenceAllele) {
        this.referenceAllele = referenceAllele
    }

    void setObservedAllele(String observedAllele) {
        this.observedAllele = observedAllele
    }

    void setIsSomatic(Boolean isSomatic) {
        this.isSomatic = isSomatic
    }

    void setConsequences(ArrayList consequences) {
        this.consequences = consequences
    }

    void setVCF(VcfInfo vcfInfo) {
        this.vcfInfo = vcfInfo
    }

    void setDatabaseIdentifier(String databaseIdentifier) {
        this.databaseIdentifier = databaseIdentifier
    }

    void setVcfInfo(VcfInfo vcfInfo) {
        this.vcfInfo = vcfInfo
    }

    void setGenotypes(List<Genotype> genotypes) {
        this.genotypes = genotypes
    }

    Set<ReferenceGenome> getReferenceGenomes() {
        return referenceGenomes
    }

    void setReferenceGenomes(Set<ReferenceGenome> referenceGenomes) {
        this.referenceGenomes = referenceGenomes
    }

    @Schema(description = "The chromosome")
    @JsonProperty("chromosome")
    @Override
    String getChromosome() {
        return chromosome
    }

    @Schema(description = "The genomic start position")
    @JsonProperty("startPosition")
    @Override
    BigInteger getStartPosition() {
        return startPosition
    }

    @Schema(description = "The genomic end position")
    @JsonProperty("endPosition")
    @Override
    BigInteger getEndPosition() {
        return endPosition
    }

    @Schema(description = "The reference allele")
    @JsonProperty("referenceAllele")
    @Override
    String getReferenceAllele() {
        return referenceAllele
    }

    @Schema(description = "The observed allele")
    @JsonProperty("observedAllele")
    @Override
    String getObservedAllele() {
        return observedAllele
    }

    @Schema(description = "The consequences")
    @JsonProperty("consequences")
    @Override
    ArrayList<Consequence> getConsequences() {
        return consequences
    }

    @Schema(description = "Is it a somatic variant?")
    @JsonProperty("isSomatic")
    @Override
    Boolean getIsSomatic() {
        return isSomatic
    }

    @Schema(description = "The variant identifier")
    @JsonProperty("identifier")
    @Override
    String getIdentifier() {
        return identifier
    }

    @Schema(description = "The information given in the INFO column of a VCF file")
    @JsonProperty("info")
    @Override
    VcfInfo getVcfInfo() {
        return vcfInfo
    }

    @Schema(description = "The database identifier of this variant (if available)")
    @JsonProperty("databaseIdentifier")
    String getDatabaseIdentifier() {
        return databaseIdentifier
    }

    @Schema(description = "The genotypes associated with this variant")
    @JsonProperty("genotypes")
    List<Genotype> getGenotypes() {
        return genotypes
    }



    /**
     * Generate variant content in Variant Call Format.
     * @return variant information in Variant Call Format
     */
    String toVcfFormat() {
        def vcfInfo = vcfInfo.toVcfFormat() ?: '.'
        return new StringBuilder().append(chromosome + "\t").append(startPosition + "\t").append(databaseIdentifier +
                "\t").append(referenceAllele + "\t").append(observedAllele + "\t").append("." + "\t").append("." +
                "\t").append(vcfInfo)
    }

}
