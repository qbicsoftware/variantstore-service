package life.qbic.variantstore.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import io.swagger.v3.oas.annotations.media.Schema

/**
 * A case (patient)
 *
 * @since: 1.1.0
 */
@EqualsAndHashCode
@Schema(name = "Case", description = "A case")
class Case {

    /**
     * The identifier of a case
     */
    final String identifier
    /**
     * The project identifier associated with a case
     */
    final String projectId

    Case(String identifier, String projectId) {
        this.identifier = identifier
        this.projectId = projectId
    }

    Case(String identifier) {
        this.identifier = identifier
        this.projectId = ""
    }

    @Schema(description = "The case identifier")
    @JsonProperty("id")
    String getIdentifier() {
        return identifier
    }

    @Schema(description = "The associated project identifier")
    @JsonProperty("projectId")
    String getProjectId() {
        return projectId
    }
}
