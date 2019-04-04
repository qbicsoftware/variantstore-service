package life.qbic.oncostore.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name="Sample", description="A biological sample")
class Sample {

    String identifier
    String cancerEntity
    String caseID

    Sample(String identifier, String cancerEntity) {
        this.identifier = identifier
        this.cancerEntity = cancerEntity
    }

    Sample() {

    }

    @JsonProperty("sampleIdentifier")
    String getIdentifier() {
        return identifier
    }

    @JsonProperty("cancerEntity")
    String getCancerEntity() {
        return cancerEntity
    }

    @JsonProperty("caseID")
    String getCaseID() {
        return caseID
    }

    void setIdentifier(String identifier) {
        this.identifier = identifier
    }

    void setCancerEntity(String cancerEntity) {
        this.cancerEntity = cancerEntity
    }

    void setCaseID(String caseID) {
        this.caseID = caseID
    }
}