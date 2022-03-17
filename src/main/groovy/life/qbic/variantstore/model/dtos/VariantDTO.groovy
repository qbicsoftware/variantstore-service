package life.qbic.variantstore.model.dtos

import io.micronaut.core.annotation.Introspected;

/**
 * A Variant Data Transfer Object.
 *
 * @since: 1.1.0
 */
@Introspected
class VariantDTO {

    private String chromosome
    private BigInteger startPosition

    String getChromosome() {
        return chromosome
    }

    void setChromosome(String chromosome) {
        this.chromosome = chromosome
    }

    BigInteger getStartPosition() {
        return startPosition
    }

    void setStartPosition(BigInteger startPosition) {
        this.startPosition = startPosition
    }
}
