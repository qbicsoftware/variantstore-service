package life.qbic.oncostore.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name="Reference Genome", description="A reference genome")
class ReferenceGenome {

    final String source
    final String build
    final String version

    ReferenceGenome(String source, String build, String version) {
        this.source = source
        this.build = build
        this.version = version
    }

    @Schema(description="The source")
    String getSource() {
        return source
    }

    @Schema(description="The build")
    String getBuild() {
        return build
    }

    @Schema(description="The version")
    String getVersion() {
        return version
    }
}
