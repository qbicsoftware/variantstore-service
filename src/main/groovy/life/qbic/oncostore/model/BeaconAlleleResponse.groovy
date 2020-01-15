package life.qbic.oncostore.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name="BeaconAlleleResponse", description="A response to a beacon request")
class BeaconAlleleResponse {

    private String beaconId
    private String apiVersion
    private Boolean exists
    private BeaconAlleleRequest alleleRequest

    public BeaconAlleleResponse() {

    }

    @JsonProperty("beaconId")
    String getBeaconId() {
        return beaconId
    }

    @JsonProperty("apiVersion")
    String getApiVersion() {
        return apiVersion
    }

    @JsonProperty("alleleRequest")
    BeaconAlleleRequest getBeaconAlleleRequest() {
        return alleleRequest
    }

    @JsonProperty("exists")
    Boolean getExists() {
        return exists
    }

    void setBeaconId(String beaconId) {
        this.beaconId = beaconId
    }

    void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion
    }

    void setExists(Boolean exists) {
        this.exists = exists
    }

    void setAlleleRequest(BeaconAlleleRequest alleleRequest) {
        this.alleleRequest = alleleRequest
    }
}
