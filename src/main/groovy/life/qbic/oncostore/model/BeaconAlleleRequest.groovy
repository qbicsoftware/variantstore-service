package life.qbic.oncostore.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import io.swagger.v3.oas.annotations.media.Schema

/**
 * An allele request as interpreted by the beacon.
 *
 * @since: 1.0.0
 *
 */
@Schema(name="BeaconAlleleRequest", description="A request to the beacon")
class BeaconAlleleRequest {

    /**
     * The chromosome specified within a request
     */
    private final String referenceName
    /**
     * The start position specified within a request
     */
    private final BigInteger start
    /**
     * The reference base(s) specified within a request
     */
    private final String referenceBases
    /**
     * The observed (alternate) base(s) specified within a request
     */
    private final String alternateBases
    /**
     * The reference genome identifier specified within a request
     */
    private final String assemblyId

    BeaconAlleleRequest(String referenceName, BigInteger start, String referenceBases, String alternateBases, String assemblyId) {
        this.referenceName = referenceName
        this.start = start
        this.referenceBases = referenceBases
        this.alternateBases = alternateBases
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
}
