package life.qbic.oncostore.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

/**
 * Allele request as interpreted by the beacon.
 */
@Schema(name="BeaconAlleleRequest", description="A request to the beacon")
class BeaconAlleleRequest {

    private String referenceName
    private BigInteger start
    private String referenceBases
    private String alternateBases
    private String assemblyId

    public BeaconAlleleRequest() {

    }

    void setReferenceName(String referenceName) {
        this.referenceName = referenceName
    }

    void setStart(BigInteger start) {
        this.start = start
    }

    void setReferenceBases(String referenceBases) {
        this.referenceBases = referenceBases
    }

    void setAlternateBases(String alternateBases) {
        this.alternateBases = alternateBases
    }

    void setAssemblyId(String assemblyId) {
        this.assemblyId = assemblyId
    }

    @JsonProperty("referenceName")
    String getReferenceName() {
        return referenceName
    }

    @JsonProperty("start")
    BigInteger getStart() {
        return start
    }

    @JsonProperty("referenceBases")
    String getReferenceBases() {
        return referenceBases
    }

    @JsonProperty("alternateBases")
    String getAlternateBases() {
        return alternateBases
    }

    @JsonProperty("assemblyId")
    String getAssemblyId() {
        return assemblyId
    }

//- startMin
    //- startMax
    //- endMin
    //- endMax
}
