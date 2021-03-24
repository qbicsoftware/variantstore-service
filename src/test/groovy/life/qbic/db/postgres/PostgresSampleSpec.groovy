package life.qbic.db.postgres


import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
import life.qbic.db.PostgresTestContainerSpecification
import life.qbic.variantstore.database.CaseRepository
import life.qbic.variantstore.database.ProjectRepository
import life.qbic.variantstore.database.ReferenceGenomeRepository
import life.qbic.variantstore.database.SampleRepository
import life.qbic.variantstore.database.VariantRepository
import life.qbic.variantstore.model.Case
import life.qbic.variantstore.model.Project
import life.qbic.variantstore.model.ReferenceGenome
import life.qbic.variantstore.model.Sample
import life.qbic.variantstore.model.Variant

import javax.inject.Inject

@MicronautTest(transactional = false)
class PostgresSampleSpec extends PostgresTestContainerSpecification {

    @Inject
    private ApplicationContext applicationContext

    @Inject
    EmbeddedServer embeddedServer

    @Inject ProjectRepository projectRepository
    @Inject CaseRepository caseRepository
    @Inject SampleRepository sampleRepository
    @Inject VariantRepository variantRepository
    @Inject ReferenceGenomeRepository referenceGenomeRepository

    void "test sequence generation with default nextval"() {
        when:
        Project projectToInsert = new Project("project1")
        Project projectInserted = projectRepository.save(projectToInsert)

        Case caesToInsert = new Case("blablabla", projectInserted)
        Case caseInserted = caseRepository.save(caesToInsert)
        Case caseFound = caseRepository.findById(caseInserted.id)

        Sample sample = new Sample("SAMPLE1234", "HCC", caseFound)
        Sample sampleInserted = sampleRepository.save(sample)


        //Sample foundSample = sampleRepository.findByIdentifier("1213")

        then:
        caseFound.id
        println(caseFound.id)
        println(caseInserted.id)
        println(sampleInserted.id)
        //sample.id
        //sample.id > 0
        //foundSample.identifier == "1213"
    }

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
        def lis =  variantRepository.saveAll(vars)

        referenceGenome.variants.addAll(vars)
        referenceGenomeRepository.save(referenceGenome)

        //ReferenceGenome rg = referenceGenomeRepository.save(referenceGenome)
        //println(rg.id)

        //referenceGenome.setVariants([variant, variant2, variant3].toSet())

        //ReferenceGenome rg2 = referenceGenomeRepository.save(referenceGenome)




        // create three courses
        //Course course1 = new Course("Machine Learning", "ML", 12, 1500);
        //Course course2 = new Course("Database Systems", "DS", 8, 800);
        //Course course3 = new Course("Web Basics", "WB", 10, 0);

        // save courses
        //courseRepository.saveAll(Arrays.asList(course1, course2, course3));

        // add courses to the student
        //student.getCourses().addAll(Arrays.asList(course1, course2, course3));

        // update the student
        //studentRepository.save(student);
        then:
        lis.size() == 3
    }
}