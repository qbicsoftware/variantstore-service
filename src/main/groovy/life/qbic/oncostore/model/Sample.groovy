package life.qbic.oncostore.model

import com.fasterxml.jackson.annotation.JsonProperty

class Sample {

    final String identifier
    final String cancerEntity

    Sample(String identifier, String cancerEntity) {
        this.identifier = identifier
        this.cancerEntity = cancerEntity
    }

    @JsonProperty("sampleIdentifier")
    String getIdentifier() {
        return identifier
    }

    @JsonProperty("cancerEntity")
    String getCancerEntity() {
        return cancerEntity
    }
}