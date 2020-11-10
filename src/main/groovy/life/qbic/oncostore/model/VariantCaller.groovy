package life.qbic.oncostore.model

import groovy.transform.EqualsAndHashCode
import io.swagger.v3.oas.annotations.media.Schema

@EqualsAndHashCode
@Schema(name="Variant Caller", description="A variant calling software")
class VariantCaller implements Software {

    final String name
    final String version
    final String doi

    VariantCaller(String name, String version, String doi) {
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
