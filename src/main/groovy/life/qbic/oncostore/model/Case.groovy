package life.qbic.oncostore.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name="Case", description="A case")
class Case {

        String identifier
        String projectId

        Case(String identifier, String projectId) {
            this.identifier = identifier
            this.projectId = projectId
        }

        Case(String identifier) {
            this.identifier = identifier
        }

        Case() {
        }

        @JsonProperty("id")
        String getIdentifier() {
            return identifier
        }

        @JsonProperty("projectId")
        String getProjectId() {
            return projectId
        }

        void setIdentifier(String identifier) {
            this.identifier = identifier
        }

        void setProjectId(String projectId) {
            this.projectId = projectId
        }
}
