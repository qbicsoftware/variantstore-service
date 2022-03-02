package life.qbic.variantstore.factories

import com.github.javafaker.Faker
import life.qbic.variantstore.model.Gene

/**
 *
 *
 * @since: 1.1.0
 */
class GeneFactory {

    private static Faker faker
    private static final Long MIN_VALID_GENOMIC_POSITION = Long.valueOf("0");
    private static final Long MAX_VALID_GENOMIC_POSITION = Double.parseDouble("3.3e9").longValue()

    GeneFactory() {
        faker = new Faker()
    }

    Gene createGene() {
        def geneStart = returnGeneStart()
        return Gene.builder()
                .chromosome(faker.regexify("(?:chr)?[1-9]|1[0-9]|2[0-3]|X|Y"))
                .geneStart(geneStart as BigInteger)
                .geneEnd(returnGeneEnd(geneStart))
                .symbol(faker.regexify("[A-B1-9]{3,4}"))
                .name(faker.lorem().word())
                .description(faker.lorem().sentence())
                .geneId(faker.regexify("ENSG[1-9]{11}"))
                .version(faker.options().nextElement([1,2,3]))
                .strand(faker.options().nextElement(["-1", "1"]))
                .synonyms(returnSynonyms())
                .build()
    }

    private ArrayList returnSynonyms() {
        return faker.options().nextElement([], faker.lorem().words())
    }

    private Long returnGeneStart() {
        return faker.number().numberBetween(MIN_VALID_GENOMIC_POSITION, MAX_VALID_GENOMIC_POSITION)
    }

    private BigInteger returnGeneEnd(Long geneStart) {
        return faker.number().numberBetween(geneStart, MAX_VALID_GENOMIC_POSITION) as BigInteger
    }

}
