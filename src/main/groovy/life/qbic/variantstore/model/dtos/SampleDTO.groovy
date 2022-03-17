package life.qbic.variantstore.model.dtos

import io.micronaut.core.annotation.Introspected

/**
 * A Sample Data Transfer Object.
 *
 * @since: 1.1.0
 */
@Introspected
class SampleDTO {

    private final String identifier

    SampleDTO(String identifier) {
        this.identifier = identifier
    }

    String getIdentifier() {
        return identifier
    }
}
