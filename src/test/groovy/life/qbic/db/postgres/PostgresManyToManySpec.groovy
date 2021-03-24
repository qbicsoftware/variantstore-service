package life.qbic.db.postgres


import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
import life.qbic.variantstore.database.*
import life.qbic.variantstore.model.ReferenceGenome
import life.qbic.variantstore.model.Variant
import spock.lang.Specification

import javax.inject.Inject

@MicronautTest(transactional = false)
class PostgresManyToManySpec extends Specification {

    @Inject private ApplicationContext applicationContext

    @Inject
    EmbeddedServer embeddedServer

    @Inject ProjectRepository projectRepository
    @Inject CaseRepository caseRepository
    @Inject SampleRepository sampleRepository
    @Inject VariantRepository variantRepository
    @Inject ReferenceGenomeRepository referenceGenomeRepository


    void "test variant and referencegenome connection"() {

        when:
        ReferenceGenome referenceGenome = new ReferenceGenome("bla", "blub", "blob")

        // create a variant
        Variant variant = new Variant()
        variant.setIdentifier("ASDASDsaD")
        variant.setDatabaseIdentifier("DB1")
        variant.setChromosome("chr1")
        variant.setStartPosition(123123 as BigInteger)
        variant.setEndPosition(1487456 as BigInteger)
        variant.setReferenceAllele("A")
        variant.setObservedAllele("G")
        variant.setIsSomatic(true)

        Variant variant2 = new Variant()
        variant2.setIdentifier("ASDASDsaD")
        variant2.setDatabaseIdentifier("DB2")
        variant2.setChromosome("chr1")
        variant2.setStartPosition(12312377 as BigInteger)
        variant2.setEndPosition(148745699 as BigInteger)
        variant2.setReferenceAllele("A")
        variant2.setObservedAllele("G")
        variant2.setIsSomatic(true)

        Variant variant3 = new Variant()
        variant3.setIdentifier("ASDASDsaD")
        variant3.setDatabaseIdentifier("DB3")
        variant3.setChromosome("chr1")
        variant3.setStartPosition(12312355 as BigInteger)
        variant3.setEndPosition(148745677 as BigInteger)
        variant3.setReferenceAllele("A")
        variant3.setObservedAllele("G")
        variant3.setIsSomatic(true)

        def vars = Arrays.asList(variant, variant2, variant3)
        referenceGenome.variants.addAll(vars)

        def rg = referenceGenomeRepository.save(referenceGenome)

        //def lis =  variantRepository.saveAll(vars)

        then:
        rg.id
        //lis.size() == 3
    }

}
