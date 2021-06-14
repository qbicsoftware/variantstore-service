package life.qbic.variantstore.database

import groovy.sql.Sql
import groovy.util.logging.Log4j2
import life.qbic.variantstore.model.*
import life.qbic.variantstore.parser.MetadataContext
import life.qbic.variantstore.service.VariantstoreStorage
import life.qbic.variantstore.util.ListingArguments

import javax.inject.Inject
import javax.inject.Singleton
import java.sql.SQLException

@Log4j2
@Singleton
class PostgresSqlVariantstoreStorage implements VariantstoreStorage {


    @Inject ProjectRepository projectRepository
    @Inject CaseRepository caseRepository
    @Inject SampleRepository sampleRepository
    @Inject ProjectRepository variantRepository
    @Inject GeneRepository geneRepository
    @Inject ReferenceGenomeRepository referenceGenomeRepository
    @Inject VariantCallerRepository variantCallerRepository
    @Inject VariantAnnotationRepository variantAnnotationRepository
    @Inject VcfinfoRepository vcfinfoRepository
    @Inject GenotypeRepository genotypeRepository
    @Inject SampleVariantRepository sampleVariantRepository

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
    void storeVariantsInStoreWithMetadata(MetadataContext metadata, Map sampleIdentifiers, ArrayList variants) throws
            VariantstoreStorageException  {
        try {
            def projId = projectRepository.save(metadata.getProject())
            def caseId = caseRepository.save(metadata.getCase())

            if (!sampleIdentifiers.isEmpty()) {
                List<Sample> samples = sampleRepository.saveAll(sampleIdentifiers.values())
            }

            def variantCaller = variantCallerRepository.save(metadata.getVariantCalling())
            def variantAnnotation = variantAnnotationRepository.save(metadata.getVariantAnnotation())
            def referenceGenome = referenceGenomeRepository.save(metadata.getReferenceGenome())

            /* INSERT variants, this should also add all connected information */

            !variants.isEmpty() ? tryToStoreVariantsBatch(variants, samples) : []
            // def consequencesToInsert = !variants.isEmpty() ? tryToStoreVariantsBatch(variants) : []
            //tryToStoreVariantInfo(variants)
            //tryToStoreVariantGenotypes(variants)

            /* INSERT consequences */
            //def consGeneMap = !consequencesToInsert.isEmpty() ? tryToStoreConsequencesBatch(consequencesToInsert as
            //        List<Consequence>) : [:]

            /* INSERT genes */
            //if (!consGeneMap.values().isEmpty()) tryToStoreGenes(consGeneMap.values().toList().flatten() as
            //        List<String>)

            /* GET ids of genes */
            //def geneIdMap = !consGeneMap.isEmpty() ? tryToFindGenesByConsequence(consGeneMap as HashMap<Consequence,
            //        List<String>>) : [:]
            //consGeneMap.clear()
        }

        catch (Exception) {

        }

    }

    @Override
    void storeGenesWithMetadata(Integer version, String date, ReferenceGenome referenceGenome, List<Gene> genes) throws VariantstoreStorageException {

    }

    /**
     * Store variants in batch in the store
     * @param variants a list of variants
     * @param list of consequences of the provded variants
     */
    private List tryToStoreVariantsBatch(ArrayList<SimpleVariantContext> variants, ArrayList<Sample> samples) {

        ArrayList<SimpleVariantContext> insertedVars = []
        try {
            // maybe add batching here
            insertedVars = variantRepository.saveAll(variants)
            //vcfinfoRepository.saveAll(variants.collect{variant -> variant.getVcfInfo()})
            //genotypeRepository.saveAll(variants.collect{ variant -> variant.getGenotypes()}.flatten())
        }
        catch (Exception e) {
            e.printStackTrace()
        }
        finally {
        }

        return insertedVars
    }
}
