package life.qbic.variantstore.factories

import net.datafaker.Faker
import life.qbic.variantstore.model.Variant

/**
 * Variant factory for generating random Variant objects.
 *
 * @since: 1.1.0
 */
class VariantFactory {

    private static Faker faker
    private static final Long MIN_VALID_GENOMIC_POSITION = Long.valueOf("0")
    private static final Long MAX_VALID_GENOMIC_POSITION = Double.parseDouble("3.3e9").longValue()

    VariantFactory() {
        faker = new Faker()
    }

    Variant createVariant() {
        def variantStart = returnVariantStart()

        return Variant.builder()
                .identifier(faker.internet().uuid())
                .databaseIdentifier(faker.lorem().characters(5, 10,
                        true, true))
                .chromosome(faker.regexify("(?:chr)?[1-9]|1[0-9]|2[0-3]|X|Y"))
                .startPosition(variantStart as BigInteger)
                .endPosition(returnVariantEnd(variantStart))
                .referenceAllele(faker.regexify("[CGAT]+"))
                .observedAllele(faker.regexify("[CGAT]+"))
                .somatic(faker.bool().bool())
                .build()
    }

    private ArrayList returnSynonyms() {
        return faker.options().nextElement([], faker.lorem().words())
    }

    private Long returnVariantStart() {
        return faker.number().numberBetween(MIN_VALID_GENOMIC_POSITION, MAX_VALID_GENOMIC_POSITION)
    }

    private BigInteger returnVariantEnd(Long variantStart) {
        return faker.number().numberBetween(variantStart, MAX_VALID_GENOMIC_POSITION) as BigInteger
    }

}
