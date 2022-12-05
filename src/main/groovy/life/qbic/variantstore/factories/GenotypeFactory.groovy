package life.qbic.variantstore.factories

import net.datafaker.Faker
import life.qbic.variantstore.model.Genotype

/**
 * Genotype factory for generating random Genotype objects.
 *
 * @since: 1.1.0
 */
class GenotypeFactory {

    private static Faker faker

    GenotypeFactory() {
        faker = new Faker()
    }

    Genotype createGenotype() {
        return Genotype.builder()
                .sampleName(faker.regexify("TUMOR|NORMAL|([A-B1-9]{3,7})"))
                .genotype(faker.regexify("[CGAT][/|][CGAT]"))
                .readDepth(faker.number().randomDigit())
                .filter(faker.options().nextElement(["", "FILTERED"]))
                .likelihoods(faker.regexify("[0-999],[0-999],[0-999]"))
                .genotypeLikelihoods(faker.regexify('-\\d*\\.?\\d+,-\\d*\\.?\\d+,-\\d*\\.?\\d+'))
                .genotypeLikelihoodsHet(faker.options().nextElement(["", "0:-75.22,1:-223.42,0/0:-323.03,1/0:-99.29,1/1:-802.53"]))
                .posteriorProbs(faker.regexify("[0.0-1.0],[0.0-1.0],[0.0-1.0]"))
                .genotypeQuality(faker.number().randomDigit())
                .haplotypeQualities(faker.regexify("\\d+,\\d+"))
                // not really defined what to put here
                .phaseSet("")
                .phasingQuality(faker.options().nextElement([-1, faker.number().randomNumber() as Integer]))
                .alternateAlleleCounts((1..3).collect { faker.number().randomDigit() })
                .mappingQuality(faker.number().randomDigit())
                .build()
    }
}
