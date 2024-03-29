package life.qbic.variantstore.factories

import net.datafaker.Faker
import life.qbic.variantstore.model.Sample

/**
 * Sample factory for generating random Sample objects.
 *
 * @since: 1.1.0
 */
class SampleFactory {

    private static Faker faker

    SampleFactory() {
        faker = new Faker()
    }

    Sample createSample() {
        return Sample.builder()
                .identifier(faker.regexify('(Q[A-Z0-9]{4}[0-9]{3}[A-Z][A-Z0-9]$)'))
                .cancerEntity(faker.options().nextElement(["HCC", "ALL", "AML", "CHOL", "BRCA"]))
                .build()
    }
}
