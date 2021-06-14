package life.qbic.variantstore.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import io.micronaut.core.annotation.Creator
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.MappedProperty
import io.micronaut.data.annotation.Relation
import io.micronaut.data.model.naming.NamingStrategies
import io.swagger.v3.oas.annotations.media.Schema


/**
 * A sample with associated metadata
 *
 * @since: 1.0.0
 */
@MappedEntity(namingStrategy = NamingStrategies.UnderScoreSeparatedLowerCase)
@EqualsAndHashCode
@Schema(name="Sample", description="A biological sample")
class Sample {

    @GeneratedValue
    @Id
    private Long id
    /**
     * The identifier of a given sample
     */
    private final String identifier
    /**
     * The annotated cancer entity of a given sample
     */
    @MappedProperty("cancerentity")
    private final String cancerEntity
    /**
     * The associated case (patient) identifier of a given sample
     */
    //private final String caseId

    @Relation(value = Relation.Kind.MANY_TO_ONE, mappedBy = "entity_id")
    private Case entity

    @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "sample")
    Set<SampleVariant> sampleVariants


    /*
    Sample(String identifier, String cancerEntity, String caseId) {
        this.identifier = identifier
        this.cancerEntity = cancerEntity
        this.caseId = caseId
    }


     */
    @Creator
    Sample(String identifier, String cancerEntity, Case entity) {
        this.identifier = identifier
        this.cancerEntity = cancerEntity
        this.entity = entity
    }

    Sample(String identifier, String cancerEntity) {
        this.identifier = identifier
        this.cancerEntity = cancerEntity
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

    @Schema(description="The sample identifier")
    @JsonProperty("sampleIdentifier")
    String getIdentifier() {
        return identifier
    }

    @Schema(description="The associated cancer entity")
    @JsonProperty("cancerEntity")
    String getCancerEntity() {
        return cancerEntity
    }

    /*
    @Schema(description="The associated case identifier")
    @JsonProperty("caseID")
    String getCaseId() {
        return caseId
    }

     */

    Case getEntity() {
        return entity
    }
}