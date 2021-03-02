package life.qbic.variantstore.model

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
    final String identifier
    /**
     * The annotated cancer entity of a given sample
     */
    final String cancerEntity
    /**
     * The associated case (patient) identifier of a given sample
     */
    final String caseId

    Sample(String identifier, String cancerEntity, String caseId) {
        this.identifier = identifier
        this.cancerEntity = cancerEntity
        this.caseId = caseId
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
}