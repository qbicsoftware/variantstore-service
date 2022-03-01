package life.qbic.variantstore.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import groovy.transform.builder.Builder
import htsjdk.variant.variantcontext.VariantContext
import io.micronaut.core.annotation.Creator
import io.micronaut.data.annotation.*
import io.micronaut.data.jdbc.annotation.JoinColumn
import io.micronaut.data.jdbc.annotation.JoinTable
import io.micronaut.data.model.naming.NamingStrategies
import io.swagger.v3.oas.annotations.media.Schema

/**
 * A variant with all accompanied information
 *
 * @since: 1.0.0
 */
@MappedEntity(namingStrategy = NamingStrategies.LowerCase.class)
@EqualsAndHashCode(excludes = ["vcfInfo", "genotypes"])
@Schema(name = "Variant", description = "A genomic variant")
@Builder
class Variant implements SimpleVariantContext, Comparable {

    /**
     * The id of a variant
     */
    // @TODO However, using @GeneratedValue(strategy = GenerationType.SEQUENCE) on a PostgreSQL has obtained a better
    //  result than the other two & runs 3x times slower than providing an explicit ID & without @GenerateValue.
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
     * Describes whether a given variant is somatic
     */
    @MappedProperty("somatic")
    boolean somatic
    /**
     * The consequences of a given variant
     */
    @JoinTable(name = "variant_consequence",
            joinColumns = @JoinColumn(name = "variant_id"),
            inverseJoinColumns = @JoinColumn(name = "consequence_id")
    )
    @Relation(value = Relation.Kind.MANY_TO_MANY, cascade = Relation.Cascade.UPDATE)
    private Set<Consequence> consequences
    /**
     * TODO
     */
    // TODO changed the assignment of join and inverseJoinColumn here, check if insert still works properly!
    @JoinTable(name = "referencegenome_variant",
            joinColumns = @JoinColumn(name = "variant_id"),
            inverseJoinColumns = @JoinColumn(name = "referencegenome_id")
    )
    @Relation(value = Relation.Kind.MANY_TO_MANY, cascade = Relation.Cascade.UPDATE)
    private Set<ReferenceGenome> referenceGenomes
    /**
     * TODO
     */
    @JoinTable(name = "variantcaller_variant",
            joinColumns = @JoinColumn(name = "variant_id"),
            inverseJoinColumns = @JoinColumn(name = "variantcaller_id")
    )
    @Relation(value = Relation.Kind.MANY_TO_MANY, cascade = Relation.Cascade.UPDATE)
    private Set<VariantCaller> variantCaller
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
    /**
     * TODO
     */
    @JsonIgnore
    @Relation(value = Relation.Kind.ONE_TO_MANY, cascade = Relation.Cascade.ALL, mappedBy = "variant")
    Set<SampleVariant> sampleVariants

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

    void setSomatic(boolean somatic) {
        this.somatic = somatic
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

    @Override
    Set<ReferenceGenome> getReferenceGenomes() {
        return referenceGenomes
    }

    @Override
    Set<VariantCaller> getVariantCaller() {
        return variantCaller
    }

    @Override
    Set<SampleVariant> getSampleVariants() {
        return sampleVariants
    }

    void setReferenceGenomes(Set<ReferenceGenome> referenceGenomes) {
        this.referenceGenomes = referenceGenomes
    }

    void setConsequences(Set<Consequence> consequences) {
        this.consequences = consequences
    }

    void setVariantCaller(Set<VariantCaller> variantCaller) {
        this.variantCaller = variantCaller
    }

    void setSampleVariants(Set<SampleVariant> sampleVariants) {
        this.sampleVariants = sampleVariants
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
    Set<Consequence> getConsequences() {
        return consequences
    }

    @Schema(description = "Is it a somatic variant?")
    @JsonProperty("somatic")
    @Override
    boolean isSomatic() {
        return somatic
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

    void addVariantCaller(VariantCaller callingSoftware){
        if(variantCaller==null) variantCaller = [] as Set<VariantCaller>
        variantCaller.add(callingSoftware)
    }

    void addReferenceGenome(ReferenceGenome refGenome){
        if(referenceGenomes==null) referenceGenomes = [] as Set<ReferenceGenome>
        referenceGenomes.add(refGenome)
    }

}
