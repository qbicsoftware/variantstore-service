package life.qbic.oncostore.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Allele request as interpreted by the beacon.
 */
class BeaconAlleleRequest {

    String referenceName
    BigInteger start
    String referenceBases
    String alternateBases
    String assemblyId

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
