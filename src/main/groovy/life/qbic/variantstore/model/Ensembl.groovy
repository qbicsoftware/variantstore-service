package life.qbic.variantstore.model

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.MappedProperty
import io.micronaut.data.annotation.Relation
import io.micronaut.data.jdbc.annotation.JoinColumn
import io.micronaut.data.jdbc.annotation.JoinTable

/**
 *
 *
 * @since: 1.1.0
 */
@MappedEntity
class Ensembl {

    /**
     * The identifier of a Ensembl DB instance
     */
    @GeneratedValue
    @Id
    private Long id

    private final Integer version

    private final String date

    /**
     * The reference genome associated with a Ensembl DB instance
     */
    @Relation(value = Relation.Kind.MANY_TO_ONE)
    @MappedProperty(value = "referencegenome_id")
    ReferenceGenome referenceGenome

    @JoinTable(name = "ensembl_gene",
            joinColumns = @JoinColumn(name = "ensembl_id"),
            inverseJoinColumns = @JoinColumn(name = "gene_id")
    )
    @Relation(value = Relation.Kind.MANY_TO_MANY, cascade = Relation.Cascade.PERSIST)
    private Set<Gene> genes = new HashSet<>()

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

    Integer getVersion() {
        return version
    }

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
