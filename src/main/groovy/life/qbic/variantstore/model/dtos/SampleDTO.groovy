package life.qbic.variantstore.model.dtos

import io.micronaut.core.annotation.Introspected
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id

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
