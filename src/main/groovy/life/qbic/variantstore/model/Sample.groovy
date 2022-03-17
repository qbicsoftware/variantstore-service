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
import io.swagger.v3.oas.annotations.media.Schema

/**
 * A sample with associated metadata
 *
 * @since: 1.0.0
 */
@MappedEntity
@EqualsAndHashCode(includeFields=true, excludes = ["id"])
@Schema(name="Sample", description="A biological sample")
@Builder
class Sample {

    /**
     * The database id
     */
    @GeneratedValue
    @Id
    private Long id
    /**
     * The identifier of a given sample
     */
    String identifier
    /**
     * The annotated cancer entity of a given sample
     */
    @MappedProperty("cancerentity")
    String cancerEntity
    /**
     * The associated case (patient) identifier of a given sample
     */
    @Relation(value = Relation.Kind.MANY_TO_ONE)
    Case entity
    /**
     * The association between sample, variant, vcfinfo, and genotypes
     */
    @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "sample")
    Set<SampleVariant> sampleVariants

    @Creator
    Sample() { }

    Sample(String identifier, String cancerEntity) {
        this.identifier = identifier
        this.cancerEntity = cancerEntity
    }

    Sample(String identifier, String cancerEntity, String entityIdentifier) {
        this.identifier = identifier
        this.cancerEntity = cancerEntity
        this.entity = new Case().setIdentifier(entityIdentifier)
    }

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    void setEntity(Case entity) {
        this.entity = entity
    }

    void setIdentifier(String identifier) {
        this.identifier = identifier
    }

    void setCancerEntity(String cancerEntity) {
        this.cancerEntity = cancerEntity
    }

    @Schema(description="The sample identifier")
    @JsonProperty("identifier")
    String getIdentifier() {
        return identifier
    }

    @Schema(description="The associated cancer entity")
    @JsonProperty("cancerEntity")
    String getCancerEntity() {
        return cancerEntity
    }

    Case getEntity() {
        return entity
    }

    Set<SampleVariant> getSampleVariants() {
        return sampleVariants
    }

    void setSampleVariants(Set<SampleVariant> sampleVariants) {
        this.sampleVariants = sampleVariants
    }
}
