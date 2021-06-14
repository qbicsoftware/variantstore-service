package life.qbic.db.postgres

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.multipart.MultipartBody
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
import life.qbic.variantstore.database.CaseRepository
import life.qbic.variantstore.database.GenotypeRepository
import life.qbic.variantstore.database.ProjectRepository
import life.qbic.variantstore.database.ReferenceGenomeRepository
import life.qbic.variantstore.database.SampleRepository
import life.qbic.variantstore.database.SampleVariantRepository
import life.qbic.variantstore.database.VariantAnnotationRepository
import life.qbic.variantstore.database.VariantCallerRepository
import life.qbic.variantstore.database.VariantRepository
import life.qbic.variantstore.database.VcfinfoRepository
import life.qbic.variantstore.model.Annotation
import life.qbic.variantstore.model.ReferenceGenome
import life.qbic.variantstore.model.Sample
import life.qbic.variantstore.model.SimpleVariantContext
import life.qbic.variantstore.model.Variant
import life.qbic.variantstore.model.VariantCaller
import life.qbic.variantstore.parser.MetadataContext
import life.qbic.variantstore.parser.MetadataReader
import life.qbic.variantstore.parser.SimpleVCFReader
import life.qbic.variantstore.util.AnnotationHandler
import spock.lang.Specification


import javax.inject.Inject

@MicronautTest(transactional = false)
class ImportVariantsToPostgresqlSpec extends Specification {

    @Inject
    private ApplicationContext applicationContext

    @Inject
    EmbeddedServer embeddedServer

    @Inject
    @Client('/')
    RxHttpClient httpClient

    @Inject ProjectRepository projectRepository
    @Inject CaseRepository caseRepository
    @Inject SampleRepository sampleRepository
    @Inject VariantRepository variantRepository
    @Inject ReferenceGenomeRepository referenceGenomeRepository
    @Inject SampleVariantRepository sampleVariantRepository
    @Inject VcfinfoRepository vcfInfoRepository
    @Inject GenotypeRepository genotypeRepository
    @Inject VariantCallerRepository variantCallerRepository
    @Inject VariantAnnotationRepository variantAnnotationRepository

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
                        if (sample.cancerEntity && genotypesIdentifiers.findIndexOf { "TUMOR" } > -1) {
                            sampleGenotypeMapping["NORMAL"] = sample
                        } else if (!sample.cancerEntity && genotypesIdentifiers.findIndexOf { "NORMAL" } > -1) {
                            sampleGenotypeMapping["TUMOR"] = sample
                        }
                    }
                }
            }
        }
        genotypesIdentifiers.clear()
        return sampleGenotypeMapping
    }

    void "should import variants from VCF file"() {
        when:
        def metadata = '{"project": {"identifier": "QTEST"}, "case": {"identifier": "do1234"}, "variant_annotation": {"version": "bioconda::4.3.1t", "name": "snpeff", "doi": "10.4161/fly.19695"}, "is_somatic": "true", "samples": [{"identifier": "S123456", "cancerEntity": "HCC"}, {"identifier": "S341", "cancerEntity": ""}], "reference_genome": {"source": "GATK", "version": "unknown", "build": "hg38"}, "variant_calling": {"version": "bioconda::2.9.10", "name": "Strelka", "doi": "10.1038/s41592-018-0051-x"}}'

        MetadataReader meta = new MetadataReader(metadata)
        MetadataContext metadataContext = meta.getMetadataContext()

        String path = "src/test/resources/Strelka_do22836T_vs_do22836N_somatic_indels_snpEff.ann.vcf"
        File file = new File(path)

        Annotation annotationSoftware =  metadataContext.getVariantAnnotation()
        AnnotationHandler.AnnotationTools annotationTool = annotationSoftware.getName().toUpperCase()  as AnnotationHandler.AnnotationTools
        SimpleVCFReader reader = new SimpleVCFReader(file.newInputStream(), annotationTool.getTag())

        ReferenceGenome refGen = metadataContext.getReferenceGenome()
        ReferenceGenome ref2 = new ReferenceGenome("bla", "blub", "blu")

        VariantCaller varCaller = metadataContext.getVariantCalling()

        SimpleVariantContext variant = null
        def sampleGenotypeMapping = [:]
        def idx = 0

        ArrayList<SimpleVariantContext> variantsToInsert = null

        while (reader.iterator().hasNext()) {
            variant = reader.iterator().next()

            AnnotationHandler.addAnnotationsToVariant(variant, annotationSoftware)
            variant.setIdentifier(UUID.randomUUID().toString())
            variant.setIsSomatic(metadataContext.getIsSomatic())
            //variant.referenceGenomes.add(refGen)
            //variant.referenceGenomes.add(ref2)
            variant.variantCaller.add(varCaller)

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

        def projId = projectRepository.save(metadataContext.getProject())
        def caseId = caseRepository.save(metadataContext.getCase())
        def samples = sampleRepository.saveAll(sampleGenotypeMapping.values())

        //ArrayList<Variant> vars = variantRepository.saveAll(variantsToInsert)
        //println(vars)

        Variant variant2 = new Variant()
        variant2.setIdentifier("ASDASDsaD")
        variant2.setDatabaseIdentifier("DB1")
        variant2.setChromosome("chr1")
        variant2.setStartPosition(123123 as BigInteger)
        variant2.setEndPosition(1487456 as BigInteger)
        variant2.setReferenceAllele("A")
        variant2.setObservedAllele("G")
        variant2.setIsSomatic(true)

        //def registeredVars = variantsToInsert.collect {var -> variantRepository.findById(var.id).get()}
        refGen.variants.addAll(variantsToInsert)
        //referenceGenomeRepository.save(refGen)

        ref2.variants.addAll(variantsToInsert)
        //varCaller.variants.addAll(registeredVars)
        //def ref2registered = referenceGenomeRepository.save(ref2)

        referenceGenomeRepository.saveAll([ref2, refGen])

        //registeredVars.each {println(it.id)}

        //def referenceGenome = referenceGenomeRepository.save(refGen)
        //def registeredVars = vars.collect {var -> variantRepository.findById(var.id).get()}

        //println(registeredVars)
        //varCaller.variants.addAll(variantsToInsert)
        //def variantCaller = variantCallerRepository.save(varCaller)

        //def registeredVars = variantsToInsert.collect { Variant var -> variantRepository.findById(var.id).get()}

        //refGen.variants.addAll(registeredVars)
        //def referenceGenome = referenceGenomeRepository.save(refGen)

        //def variantAnnotation = variantAnnotationRepository.save(metadataContext.getVariantAnnotation())

        //storage.storeVariantsInStoreWithMetadata(metadataContext, sampleGenotypeMapping,variantsToInsert)
        variantsToInsert.clear()

        /*
        HttpResponse response = httpClient.toBlocking().exchange(HttpRequest.POST("/variants",
                MultipartBody.builder()
                .addPart("metadata", '{\"project\": {\"identifier\": \"QTEST\"}, \"case\": {\"identifier\": \"do1234\"}, \"variant_annotation\": {\"version\": \"bioconda::4.3.1t\", \"name\": \"snpeff\", \"doi\": \"10.4161/fly.19695\"}, \"is_somatic\": \"true\", \"samples\": [{\"identifier\": \"S123456\", \"cancerEntity\": \"HCC\"}], \"reference_genome\": {\"source\": \"GATK\", \"version\": \"unknown\", \"build\": \"hg38\"}, \"variant_calling\": {\"version\": \"bioconda::2.9.10\", \"name\": \"Strelka\", \"doi\": \"10.1038/s41592-018-0051-x\"}}')
                .addPart("files", "@${path}")
                .build())
                .contentType(MediaType.MULTIPART_FORM_DATA_TYPE))

         */
        then:
        metadataContext
    }
}
