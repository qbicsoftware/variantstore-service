package life.qbic.variantstore.factories

import com.github.javafaker.Faker
import life.qbic.variantstore.model.VcfInfo

/**
 *
 *
 * @since: 1.1.0
 */
class VcfInfoFactory {

    private static Faker faker
    private static final Long MAX_VALID_GENOMIC_POSITION = Double.parseDouble("3.3e9").longValue()

    VcfInfoFactory() {
        faker = new Faker()
    }

    VcfInfo createVcfInfo() {

        def numberSamples = faker.number().randomDigit()

        return VcfInfo.builder()
        .ancestralAllele(faker.regexify("[CGAT]+"))
        .alleleCount((1..numberSamples).collect { faker.number().randomDigit() })
        .alleleFrequency((1..numberSamples).collect { faker.number().numberBetween(0, 1).toFloat() })
        .numberAlleles(faker.number().randomDigit())
        .baseQuality(faker.number().randomDigit())
        .cigar(faker.regexify("[MNDI][0-10]+"))
        .dbSnp(faker.bool().bool())
        .hapmapTwo(faker.bool().bool())
        .hapmapThree(faker.bool().bool())
        .thousandGenomes(faker.bool().bool())
        .combinedDepth(numberSamples * faker.random().nextInt(0, 250))
        .endPos(faker.number().numberBetween(0, MAX_VALID_GENOMIC_POSITION).toInteger())
        .rms(faker.number().randomNumber())
        .mqZero(faker.number().randomDigit())
        .strandBias(faker.number().randomDigit())
        .numberSamples(numberSamples)
        .somatic(faker.bool().bool())
        .validated(faker.bool().bool())
        .build()
    }

}
