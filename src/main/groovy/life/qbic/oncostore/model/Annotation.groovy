package life.qbic.oncostore.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name="Variant Annotation", description="A variant annotation software")
class Annotation implements Software{

    final String name
    final String version
    final String doi

    Annotation(String name, String version, String doi) {
        this.name = name
        this.version = version
        this.doi = doi
    }

    @Override
    String getName() {
        return name
    }

    @Override
    String getVersion() {
        return version
    }

    @Override
    String getDoi() {
        return doi
    }
}
