package life.qbic.oncostore.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import io.swagger.v3.oas.annotations.media.Schema

/**
 * A sample with associated metadata
 *
 * @since: 1.0.0
 */
@EqualsAndHashCode
@Schema(name="Sample", description="A biological sample")
class Sample {

    /**
     * The identifier of a given sample
     */
    String identifier
    /**
     * The annotated cancer entity of a given sample
     */
    String cancerEntity
    /**
     * The associated case (patient) identifier of a given sample
     */
    String caseId

    Sample(String identifier, String cancerEntity) {
        this.identifier = identifier
        this.cancerEntity = cancerEntity
    }

    Sample(String identifier) {
        this.identifier = identifier
    }

    Sample() {
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

    @Schema(description="The associated case identifier")
    @JsonProperty("caseID")
    String getCaseId() {
        return caseId
    }

    void setIdentifier(String identifier) {
        this.identifier = identifier
    }

    void setCancerEntity(String cancerEntity) {
        this.cancerEntity = cancerEntity
    }

    void setCaseId(String caseId) {
        this.caseId = caseId
    }
}