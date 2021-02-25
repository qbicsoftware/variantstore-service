package life.qbic.variantstore.model

import groovy.transform.EqualsAndHashCode
import io.swagger.v3.oas.annotations.media.Schema

/**
 * A variant annotation software
 *
 * @since: 1.0.0
 *
 */
@EqualsAndHashCode
@Schema(name="Variant Annotation", description="A variant annotation software")
class Annotation implements Software{

    /**
     * The name of a given annotation software
     */
    final String name
    /**
     * The version of a given annotation software
     */
    final String version
    /**
     * The Digital Object Identifier (DOI) of a given annotation software
     */
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
