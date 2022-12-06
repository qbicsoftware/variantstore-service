package life.qbic.variantstore.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.MappedProperty
import io.micronaut.data.annotation.Relation
import io.micronaut.data.jdbc.annotation.JoinColumn
import io.micronaut.data.jdbc.annotation.JoinTable
import io.swagger.v3.oas.annotations.media.Schema

/**
 * An Ensembl (database instance) object
 *
 * @since: 1.1.0
 */
@MappedEntity
@EqualsAndHashCode
class Ensembl {

    /**
     * The identifier of a Ensembl DB instance
     */
    @GeneratedValue
    @Id
    Long id

    /**
     * The version of a Ensembl DB instance
     */
    Integer version

    /**
     * The date of a Ensembl DB instance
     */
    String date

    /**
     * The reference genome associated with a Ensembl DB instance
     */
    @Relation(value = Relation.Kind.MANY_TO_ONE)
    @MappedProperty(value = "referencegenome_id")
    ReferenceGenome referenceGenome

    /**
     * The genes associated with a Ensembl DB instance
     */
    @JoinTable(name = "ensembl_gene",
            joinColumns = @JoinColumn(name = "ensembl_id"),
            inverseJoinColumns = @JoinColumn(name = "gene_id")
    )
    @Relation(value = Relation.Kind.MANY_TO_MANY, cascade = [Relation.Cascade.PERSIST, Relation.Cascade.UPDATE])
    Set<Gene> genes

    Ensembl() { }

    Ensembl(Integer version, String date) {
        this.version = version
        this.date = date
    }

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    @Schema(description = "The version")
    @JsonProperty("version")
    Integer getVersion() {
        return version
    }

    @Schema(description = "The date")
    @JsonProperty("date")
    String getDate() {
        return date
    }

    ReferenceGenome getReferenceGenome() {
        return referenceGenome
    }

    void setReferenceGenome(ReferenceGenome referenceGenome) {
        this.referenceGenome = referenceGenome
    }

    Set<Gene> getGenes() {
        return genes
    }

    void setGenes(Set<Gene> genes) {
        this.genes = genes
    }
}
