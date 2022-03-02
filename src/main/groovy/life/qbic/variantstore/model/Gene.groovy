package life.qbic.variantstore.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import groovy.transform.builder.Builder
import io.micronaut.core.annotation.Creator
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.MappedProperty
import io.micronaut.data.annotation.Relation
import io.micronaut.data.jdbc.annotation.JoinColumn
import io.micronaut.data.jdbc.annotation.JoinTable
import io.micronaut.data.model.DataType
import io.micronaut.data.model.naming.NamingStrategies
import io.swagger.v3.oas.annotations.media.Schema

/**
 * A gene with detailed information
 *
 * @since: 1.1.0
 */
@MappedEntity(namingStrategy = NamingStrategies.LowerCase.class)
@EqualsAndHashCode
@Schema(name = "Gene", description = "A Gene")
@Builder
class Gene {

    /**
     * The identifier of a gene
     */
    @GeneratedValue
    @Id
    private Long id
    /**
     * The chromosome of a gene
     */
    @MappedProperty("chr")
    String chromosome
    /**
     * The symbol of a gene
     */
    String symbol
    /**
     * The name of a gene
     */
    String name
    /**
     * The biological classification (such as pseudogene and protein coding) of a gene
     */
    String bioType
    /**
     * The description available for a gene
     */
    String description
    /**
     * The start position of a gene
     */
    @MappedProperty("start")
    BigInteger geneStart
    /**
     * The end position of a gene
     */
    @MappedProperty("end")
    BigInteger geneEnd
    /**
     * The identifier of a gene
     */
    String geneId
    /**
     * The strand of a gene
     */
    String strand
    /**
     * The version of a gene
     */
    Integer version
    /**
     * The associated synonyms of a gene
     */
    @MappedProperty(type = DataType.STRING_ARRAY)
    List<String> synonyms

    @Relation(value = Relation.Kind.MANY_TO_MANY, mappedBy = "genes")
    Set<Consequence> consequences

    @JoinTable(name = "ensembl_gene",
            joinColumns = @JoinColumn(name = "gene_id"),
            inverseJoinColumns = @JoinColumn(name = "ensembl_id")
    )
    @Relation(value = Relation.Kind.MANY_TO_MANY, cascade = Relation.Cascade.UPDATE)
    Set<Ensembl> ensembles = new HashSet<>()

    @Creator
    Gene() {}

    Gene(String bioType, String chromosome, String symbol, String name, BigInteger geneStart, BigInteger geneEnd,
         String geneId, String description, String strand, Integer version, List<String> synonyms) {
        this.bioType = bioType
        this.chromosome = chromosome
        this.symbol = symbol
        this.name = name
        this.geneStart = geneStart
        this.geneEnd = geneEnd
        this.geneId = geneId
        this.description = description
        this.strand = strand
        this.version = version
        this.synonyms = synonyms
    }

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    @Schema(description = "The chromosome")
    @JsonProperty("chromosome")
    String getChromosome() {
        return chromosome
    }

    @Schema(description = "The gene symbol (HGNC Symbol)")
    @JsonProperty("symbol")
    String getSymbol() {
        return symbol
    }

    @Schema(description = "The gene name (HGNC Symbol)")
    @JsonProperty("name")
    String getName() {
        return name
    }

    @Schema(description = "The biological gene type")
    @JsonProperty("bioType")
    String getBioType() {
        return bioType
    }

    @Schema(description = "The genomic start position")
    @JsonProperty("geneStart")
    BigInteger getGeneStart() {
        return geneStart
    }

    @Schema(description = "The genomic end position")
    @JsonProperty("geneEnd")
    BigInteger getGeneEnd() {
        return geneEnd
    }

    @Schema(description = "The gene identifier")
    @JsonProperty("geneID")
    String getGeneId() {
        return geneId
    }

    @Schema(description = "The synonyms")
    @JsonProperty("synonyms")
    List<String> getSynonyms() {
        return synonyms
    }

    @Schema(description = "The description")
    @JsonProperty("description")
    String getDescription() {
        return description
    }

    @Schema(description = "The gene orientation", allowableValues = ["+, -"])
    @JsonProperty("strand")
    String getStrand() {
        return strand
    }

    @Schema(description = "The gene version")
    @JsonProperty("version")
    Integer getVersion() {
        return version
    }

    Set<Consequence> getConsequences() {
        return consequences
    }

    void setConsequences(Set<Consequence> consequences) {
        this.consequences = consequences
    }

    Set<Ensembl> getEnsembles() {
        return ensembles
    }

    void setEnsembles(Set<Ensembl> ensembles) {
        this.ensembles = ensembles
    }

    void setChromosome(String chromosome) {
        this.chromosome = chromosome
    }

    void setSymbol(String symbol) {
        this.symbol = symbol
    }

    void setName(String name) {
        this.name = name
    }

    void setBioType(String bioType) {
        this.bioType = bioType
    }

    void setDescription(String description) {
        this.description = description
    }

    void setGeneStart(BigInteger geneStart) {
        this.geneStart = geneStart
    }

    void setGeneEnd(BigInteger geneEnd) {
        this.geneEnd = geneEnd
    }

    void setGeneId(String geneId) {
        this.geneId = geneId
    }

    void setStrand(String strand) {
        this.strand = strand
    }

    void setVersion(Integer version) {
        this.version = version
    }

    void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms
    }
}
