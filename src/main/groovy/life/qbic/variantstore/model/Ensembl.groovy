package life.qbic.variantstore.model

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation


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
    @Relation(value = Relation.Kind.MANY_TO_ONE, mappedBy = "referencegenome_id")
    private ReferenceGenome referenceGenome

    @Relation(value = Relation.Kind.MANY_TO_MANY, mappedBy = "gene_id")
    private Set<Gene> genes = new HashSet<>();

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
