package life.qbic.variantstore.model

import groovy.transform.EqualsAndHashCode
import io.swagger.v3.oas.annotations.media.Schema

/**
 * A variant calling software
 *
 * @since: 1.0.0
 */
@EqualsAndHashCode
@Schema(name="Variant Caller", description="A variant calling software")
class VariantCaller implements Software {

    /**
     * The name of a variant calling software
     */
    final String name
    /**
     * The version of a variant calling software
     */
    final String version
    /**
     * The Digital Object Identifier (DOI) of a variant calling software
     */
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
