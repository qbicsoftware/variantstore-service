package life.qbic.variantstore.database

import groovy.util.logging.Log4j2
import life.qbic.variantstore.model.*
import life.qbic.variantstore.parser.MetadataContext
import life.qbic.variantstore.service.VariantstoreStorage
import life.qbic.variantstore.util.ListingArguments

import javax.inject.Inject
import javax.inject.Singleton

@Log4j2
@Singleton
class PostgresSqlVariantstoreStorage implements VariantstoreStorage {


    @Inject ProjectRepository projectRepository
    @Inject CaseRepository caseRepository
    @Inject SampleRepository sampleRepository
    @Inject GeneRepository geneRepository
    @Inject ReferenceGenomeRepository referenceGenomeRepository
    @Inject VariantCallerRepository variantCallerRepository

    @Override
    List<Variant> findVariantsForBeaconResponse(String chromosome, BigInteger start, String reference,
                                                       String observed, String assemblyId) {
        return null;
    }

    @Override
    List<Case> findCaseById(String identifier) {
        return null;
    }

    @Override
    List<Sample> findSampleById(String identifier) {
        return null;
    }

    @Override
    List<Variant> findVariantById(String identifier) {
        return null;
    }

    @Override
    List<Gene> findGeneById(String identifier, ListingArguments args) {
        return null;
    }

    @Override
    List<Case> findCases(ListingArguments args) {
        return null;
    }

    @Override
    List<Sample> findSamples(ListingArguments args) {
        return null;
    }

    @Override
    List<Variant> findVariants(ListingArguments args, String referenceGenome, Boolean withConsequences,
                                      String annotationSoftware, Boolean withVcfInfo, Boolean withGenotypes) {
        return null;
    }

    @Override
    Annotation findAnnotationSoftwareByConsequence(Consequence consequence) {
        return null;
    }

    @Override
    ReferenceGenome findReferenceGenomeByVariant(Variant variant) {
        return null;
    }

    @Override
    List<Gene> findGenes(ListingArguments args) {
        return null;
    }

    @Override
    void storeCaseInStore(Case patient) throws VariantstoreStorageException {
        caseRepository.save(patient)
    }

    @Override
    void storeSampleInStore(Sample sample) throws VariantstoreStorageException {
        sampleRepository.save(sample)
    }

    @Override
    void storeReferenceGenomeInStore(ReferenceGenome referenceGenome) throws VariantstoreStorageException {
        referenceGenomeRepository.save(referenceGenome)
    }

    @Override
    void storeVariantCallerInStore(VariantCaller variantCaller) throws VariantstoreStorageException {
        variantCallerRepository.save(variantCaller)
    }

    @Override
    void storeAnnotationSoftwareInStore(Annotation annotationSoftware) throws VariantstoreStorageException {

    }

    @Override
    void storeVariantsInStoreWithMetadata(MetadataContext metadata, Map sampleIdentifiers,
                                                 ArrayList<SimpleVariantContext> variantContext) throws VariantstoreStorageException {
        try {
            def caseId = caseRepository.save(metadata.getCase())
            if (!sampleIdentifiers.isEmpty()) sampleRepository.saveAll(sampleIdentifiers.values().collect{sample -> sample}))

            variantCallerRepository.save(metadata.getVariantCalling())

            tryToStoreAnnotationSoftware(metadata.getVariantAnnotation())
            referenceGenomeRepository.save(metadata.getReferenceGenome())
        }
        catch (Exception) {

        }

    }

    @Override
    void storeGenesWithMetadata(Integer version, String date, ReferenceGenome referenceGenome, List<Gene> genes) throws VariantstoreStorageException {

    }
}
