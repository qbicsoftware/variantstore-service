package life.qbic.db.postgres

import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import life.qbic.variantstore.database.PostgresSqlVariantstoreStorage
import life.qbic.variantstore.factories.SampleFactory
import life.qbic.variantstore.repositories.CaseRepository
import life.qbic.variantstore.repositories.ConsequenceRepository
import life.qbic.variantstore.repositories.EnsemblRepository
import life.qbic.variantstore.repositories.GeneRepository
import life.qbic.variantstore.repositories.GenotypeRepository
import life.qbic.variantstore.repositories.ProjectRepository
import life.qbic.variantstore.repositories.ReferenceGenomeRepository
import life.qbic.variantstore.repositories.SampleRepository
import life.qbic.variantstore.repositories.SampleVariantRepository
import life.qbic.variantstore.repositories.VariantAnnotationRepository
import life.qbic.variantstore.repositories.VariantCallerRepository
import life.qbic.variantstore.repositories.VariantRepository
import life.qbic.variantstore.repositories.VcfinfoRepository
import life.qbic.variantstore.model.Annotation
import life.qbic.variantstore.model.Case
import life.qbic.variantstore.model.Consequence
import life.qbic.variantstore.model.Ensembl
import life.qbic.variantstore.model.Gene
import life.qbic.variantstore.model.Genotype
import life.qbic.variantstore.model.Project
import life.qbic.variantstore.model.ReferenceGenome
import life.qbic.variantstore.model.Sample
import life.qbic.variantstore.model.SampleVariant
import life.qbic.variantstore.model.Variant
import life.qbic.variantstore.model.VariantCaller
import life.qbic.variantstore.model.VcfInfo
import life.qbic.variantstore.factories.GeneFactory
import life.qbic.variantstore.factories.GenotypeFactory
import life.qbic.variantstore.factories.VariantFactory
import life.qbic.variantstore.factories.VcfInfoFactory
import spock.lang.Specification


@MicronautTest(transactional = false)
class RepositorySpec extends Specification {

    @Inject
    EmbeddedApplication<?> application

    @Inject
    CaseRepository caseRepository

    @Inject
    ConsequenceRepository consequenceRepository

    @Inject
    EnsemblRepository ensemblRepository

    @Inject
    GeneRepository geneRepository

    @Inject
    GenotypeRepository genotypeRepository

    @Inject
    ProjectRepository projectRepository

    @Inject
    ReferenceGenomeRepository referenceGenomeRepository

    @Inject
    SampleRepository sampleRepository

    @Inject
    SampleVariantRepository sampleVariantRepository

    @Inject
    VariantAnnotationRepository variantAnnotationRepository

    @Inject
    VariantCallerRepository variantCallerRepository

    @Inject
    VariantRepository variantRepository

    @Inject
    VcfinfoRepository vcfinfoRepository

    @Inject
    PostgresSqlVariantstoreStorage postgresSqlVariantstoreStorage

    void 'test application is running'() {
        expect:
        application.running
    }

    void 'test insert works for project and case'() {
        when:
        Project project = new Project()
        project.setIdentifier("QTEST2")
        Case entity = new Case()
        entity.setIdentifier("QENTITY2")
        projectRepository.save(project)
        entity.setProject(project)

        def entityInserted = caseRepository.save(entity)
        def found = caseRepository.findById(entityInserted.id).get()
        then:
        found.id == entity.id
    }

    void 'test insert works for consequence'() {
        when:
        Consequence consequence = new Consequence()
        def consequenceInserted = consequenceRepository.save(consequence)
        def found = consequenceRepository.findById(consequenceInserted.id).get()
        then:
        found.id
    }

    void 'test insert works for ensembl'() {
        when:
        Ensembl ensembl = new Ensembl(1, "01/2021")
        def ensemblInserted = ensemblRepository.save(ensembl)
        def found = ensemblRepository.findById(ensemblInserted.id).get()
        then:
        found.version == ensembl.version
    }

    void 'test insert works for gene'() {
        when:
        Gene newGene = new GeneFactory().createGene()
        Gene geneInserted = geneRepository.save(newGene)
        Gene found = geneRepository.findById(geneInserted.id).get()
        then:
        found.geneId == newGene.geneId
    }

    void 'test insert works for genotype'() {
        when:
        def newGenotype = new GenotypeFactory().createGenotype()
        def genotypeInserted = genotypeRepository.save(newGenotype)
        def found = genotypeRepository.findById(genotypeInserted.id).get()
        then:
        found.readDepth == newGenotype.readDepth
    }

    void 'test insert works for project'() {
        when:
        Project project = new Project()
        project.setIdentifier("QTEST")
        def projectInserted = projectRepository.save(project)
        def found = projectRepository.findById(projectInserted.id).get()
        then:
        found.id == project.id
    }

    void 'test insert works for reference genome'() {
        when:
        ReferenceGenome referenceGenome = new ReferenceGenome("unknown", "2t", "1.0")
        def refInserted = referenceGenomeRepository.save(referenceGenome)
        def found = referenceGenomeRepository.findById(refInserted.id).get()
        then:
        found.id == refInserted.id
    }

    void 'test insert works for sample'() {
        when:
        Sample sample = new SampleFactory().createSample()
        def sampleInserted = sampleRepository.save(sample)
        def found = sampleRepository.findById(sampleInserted.id).get()
        then:
        found.identifier == sample.identifier
    }

    void 'test insert works for sample variant'() {
        when:
        Sample sample = new SampleFactory().createSample()
        Variant variant = new VariantFactory().createVariant()
        VcfInfo vcfInfo = new VcfInfoFactory().createVcfInfo()
        Genotype genotype = new GenotypeFactory().createGenotype()

        def sampleInserted = sampleRepository.save(sample)
        def variantInserted = variantRepository.save(variant)
        def vcfInfoInserted = vcfinfoRepository.save(vcfInfo)
        def genotypeInserted = genotypeRepository.save(genotype)

        SampleVariant sampleVariant = new SampleVariant(sampleInserted, variantInserted, vcfInfoInserted, genotypeInserted)
        def sampleVariantInserted = sampleVariantRepository.save(sampleVariant)
        def foundVariant = variantRepository.findById(variantInserted.id).get()
        then:
        sampleVariantInserted.variant.id == foundVariant.id
    }

    void 'test insert works for variant annotation software'() {
        when:
        Annotation annotation = new Annotation("a software", "1.0", "doi1234")
        def annotationInserted = variantAnnotationRepository.save(annotation)
        def found = variantAnnotationRepository.findById(annotationInserted.id).get()
        then:
        found.doi == annotation.doi
    }

    void 'test insert works for variant calling software'() {
        when:
        VariantCaller variantCaller = new VariantCaller("a software", "1.0", "doi1234")
        def variantCallerInserted = variantCallerRepository.save(variantCaller)
        def found = variantCallerRepository.findById(variantCallerInserted.id).get()
        then:
        found.doi == variantCaller.doi
    }

    void 'test insert works for variant'() {
        when:
        Variant newVariant = new VariantFactory().createVariant()
        def variantInserted = variantRepository.save(newVariant)
        def found = variantRepository.findById(variantInserted.id).get()
        then:
        found.chromosome == newVariant.chromosome
    }

    void 'test insert works for vcf info'() {
        when:

        VcfInfo newVcfInfo = new VcfInfoFactory().createVcfInfo()
        def vcfInfoInserted = vcfinfoRepository.save(newVcfInfo)
        def found = vcfinfoRepository.findById(vcfInfoInserted.id).get()
        then:
        found.combinedDepth == newVcfInfo.combinedDepth
    }
}
