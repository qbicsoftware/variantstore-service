package life.qbic.db.postgres

import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import life.qbic.variantstore.database.*
import life.qbic.variantstore.model.*
import life.qbic.variantstore.parser.MetadataContext
import life.qbic.variantstore.parser.MetadataReader
import life.qbic.variantstore.parser.SimpleVCFReader
import life.qbic.variantstore.repositories.CaseRepository
import life.qbic.variantstore.repositories.ConsequenceRepository
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
import life.qbic.variantstore.util.AnnotationHandler
import life.qbic.variantstore.util.VcfConstants
import spock.lang.Specification


@MicronautTest(transactional = false)
class ImportVariantsToPostgresqlSpec extends Specification {

    @Inject EmbeddedApplication<?> application
    @Inject ProjectRepository projectRepository
    @Inject CaseRepository caseRepository
    @Inject ConsequenceRepository consequenceRepository
    @Inject SampleRepository sampleRepository
    @Inject VariantRepository variantRepository
    @Inject ReferenceGenomeRepository referenceGenomeRepository
    @Inject SampleVariantRepository sampleVariantRepository
    @Inject VcfinfoRepository vcfInfoRepository
    @Inject GeneRepository geneRepository
    @Inject GenotypeRepository genotypeRepository
    @Inject VariantCallerRepository variantCallerRepository
    @Inject VariantAnnotationRepository variantAnnotationRepository
    @Inject PostgresSqlVariantstoreStorage storage

    HashMap determineGenotypeMapping(SimpleVariantContext variant, MetadataContext meta) {
        // get sample identifiers provided in VCF file and compare them with the samples provided in metadata JSON
        def genotypesIdentifiers = new ArrayList<String>(variant.genotypes.size())
        def sampleGenotypeMapping = [:]

        variant.genotypes.each { genotype ->
            if (genotype.sampleName) genotypesIdentifiers.add(genotype.sampleName)
        }

        if (genotypesIdentifiers.isEmpty()) {
            meta.samples.each { sample ->
                sampleGenotypeMapping.put(sample.identifier, sample)
            }
        } else {
            assert genotypesIdentifiers.size() == meta.samples.size()
            // in case of one identifier, the mapping is straightforward
            if (genotypesIdentifiers.size() == 1) {
                sampleGenotypeMapping[genotypesIdentifiers.get(0)] = meta.samples
                        .get(0)
            } else {
                meta.samples.each { sample ->
                    def searchIndex = genotypesIdentifiers.indexOf(sample.identifier)
                    if (searchIndex > -1) {
                        sampleGenotypeMapping[genotypesIdentifiers[searchIndex]] = sample
                    } else {
                        if (sample.cancerEntity && genotypesIdentifiers.findIndexOf { VcfConstants.TUMOR } > -1) {
                            sampleGenotypeMapping[VcfConstants.NORMAL] = sample
                        } else if (!sample.cancerEntity && genotypesIdentifiers.findIndexOf { VcfConstants.NORMAL } > -1) {
                            sampleGenotypeMapping[VcfConstants.TUMOR] = sample
                        }
                    }
                }
            }
        }
        genotypesIdentifiers.clear()
        return sampleGenotypeMapping
    }

    void "should import variants from VCF file with metadata and duplicates should be handled properly"() {
        when:
        def metadata = '{"project": {"identifier": "QTEST"}, "case": {"identifier": "do1234"}, "variant_annotation": {"version": "bioconda::4.3.1t", "name": "snpeff", "doi": "10.4161/fly.19695"}, "is_somatic": "true", "samples": [{"identifier": "S123456", "cancerEntity": "HCC"}, {"identifier": "S341", "cancerEntity": ""}], "reference_genome": {"source": "GATK", "version": "unknown", "build": "hg38"}, "variant_calling": {"version": "bioconda::2.9.10", "name": "Strelka", "doi": "10.1038/s41592-018-0051-x"}}'

        MetadataReader meta = new MetadataReader(metadata)
        MetadataContext metadataContext = meta.getMetadataContext()

        def projectsAvailable = projectRepository.findAll().size()
        def casesAvailable = caseRepository.findAll().size()
        def samplesAvailable = sampleRepository.findAll().size()
        def variantAvailable =  variantRepository.findAll().size()
        def refGenomeAvailable = referenceGenomeRepository.findAll().size()
        def variantCallerAvailable = variantCallerRepository.findAll().size()
        def variantAnnotationAvailable = variantAnnotationRepository.findAll().size()
        def consequenceAvailable = consequenceRepository.findAll().size()
        def geneAvailable = geneRepository.findAll().size()
        def sampleVariantAvailable = sampleVariantRepository.findAll().size()

        String path = "src/test/resources/data/Strelka_somatic_indels_snpEff.ann.vcf"
        File file = new File(path)

        Annotation annotationSoftware =  metadataContext.getVariantAnnotation()
        AnnotationHandler.AnnotationTools annotationTool = annotationSoftware.getName().toUpperCase()  as AnnotationHandler.AnnotationTools
        SimpleVCFReader reader = new SimpleVCFReader(file.newInputStream(), annotationTool.getTag())

        SimpleVariantContext variant = null
        HashMap<String, Sample> sampleGenotypeMapping = new HashMap<String, Sample>()
        def idx = 0

        ArrayList<SimpleVariantContext> variantsToInsert = null

        while (reader.iterator().hasNext()) {
            variant = (Variant) reader.iterator().next()

            AnnotationHandler.addAnnotationsToVariant(variant, annotationSoftware)
            variant.setIdentifier(UUID.randomUUID().toString())
            variant.setSomatic(metadataContext.getIsSomatic())

            if (idx == 0) {
                sampleGenotypeMapping = determineGenotypeMapping(variant, metadataContext)
            }

            if ((variant.referenceAllele.length() > 255) || (variant.observedAllele.length() > 255)) {
                def warning = new StringBuilder("Skipping variant").append(variant.startPosition).append(":")
                        .append(variant.referenceAllele).append(">").append(" because the reference or observed " + "allele is exceeding the maximum length\"")
            } else {
                if (variantsToInsert == null) variantsToInsert = new ArrayList<>(250000)
                variantsToInsert.add(variant)
                idx += 1
            }
        }

        storage.storeVariantsInStoreWithMetadata(metadataContext, sampleGenotypeMapping, variantsToInsert)
        storage.storeVariantsInStoreWithMetadata(metadataContext, sampleGenotypeMapping, variantsToInsert)

        def availableConsequences = []
        variantsToInsert.each {availableConsequences.addAll(it.consequences.flatten())}
        def numberConsequences = availableConsequences.size()

        then:
        projectRepository.list().size() == projectsAvailable + 1
        caseRepository.list().size() == casesAvailable + 1
        sampleRepository.findAll().size() == samplesAvailable + metadataContext.samples.size()
        variantRepository.list().size() == variantAvailable + variantsToInsert.size()
        referenceGenomeRepository.list().size() == refGenomeAvailable + 1
        variantCallerRepository.list().size() == variantCallerAvailable + 1
        variantAnnotationRepository.findAll().size() == variantAnnotationAvailable + 1
        consequenceRepository.list().size() == consequenceAvailable + numberConsequences
        geneRepository.findAll().size() > geneAvailable
        sampleVariantRepository.findAll().size() == sampleVariantAvailable + 4
    }
}
