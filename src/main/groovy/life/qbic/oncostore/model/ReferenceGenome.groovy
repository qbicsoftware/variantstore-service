package life.qbic.oncostore.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name="ReferenceGenome", description="A reference genome")
class ReferenceGenome {

    final String source
    final String build
    final String version

    ReferenceGenome(String source, String build, String version) {
        this.source = source
        this.build = build
        this.version = version
    }

    @Schema(description="The genome source")
    String getSource() {
        return source
    }

    @Schema(description="The genome build")
    String getBuild() {
        return build
    }

    @Schema(description="The genome version")
    String getVersion() {
        return version
    }
}
