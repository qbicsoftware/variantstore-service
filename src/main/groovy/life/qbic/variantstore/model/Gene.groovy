package life.qbic.variantstore.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.swagger.v3.oas.annotations.media.Schema

/**
 * A gene with detailed information
 *
 * @since: 1.1.0
 */
@MappedEntity
@EqualsAndHashCode
@Schema(name = "Gene", description = "A Gene")
class Gene {

    /**
     * The identifier of a gene
     */
    @GeneratedValue
    @Id
    private String id
    /**
     * The chromosome of a gene
     */
    final String chromosome
    /**
     * The symbol of a gene
     */
    final String symbol
    /**
     * The name of a gene
     */
    final String name
    /**
     * The biological classification (such as pseudogene and protein coding) of a gene
     */
    final String bioType
    /**
     * The description available for a gene
     */
    final String description
    /**
     * The start position of a gene
     */
    final BigInteger geneStart
    /**
     * The end position of a gene
     */
    final BigInteger geneEnd
    /**
     * The identifier of a gene
     */
    final String geneId
    /**
     * The strand of a gene
     */
    final String strand
    /**
     * The version of a gene
     */
    final Integer version
    /**
     * The associated synonyms of a gene
     */
    final List<String> synonyms

    @Relation(value = Relation.Kind.MANY_TO_MANY, cascade = Relation.Cascade.PERSIST)
    Set<Consequence> consequences = new HashSet<>()

    @Relation(value = Relation.Kind.MANY_TO_MANY, cascade = Relation.Cascade.PERSIST)
    Set<Ensembl> ensembles = new HashSet<>()

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

    String getId() {
        return id
    }

    void setId(String id) {
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
}
