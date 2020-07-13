package life.qbic.oncostore.service

import groovy.util.logging.Log4j2
import life.qbic.oncostore.model.*
import life.qbic.oncostore.parser.EnsemblParser
import life.qbic.oncostore.parser.MetadataReader
import life.qbic.oncostore.util.AnnotationHandler
import life.qbic.oncostore.util.ListingArguments
import life.qbic.oncostore.util.VariantExporter

import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.constraints.NotNull

@Log4j2
@Singleton
class VariantstoreInformationCenter implements VariantstoreService{

    private final VariantstoreStorage storage

    @Inject
    VariantstoreInformationCenter(VariantstoreStorage storage) {
        this.storage = storage
    }

    @Override
    @Transactional
    List<Case> getCaseForCaseId(String caseId) {
        return storage.findCaseById(caseId)
    }

    @Override
    @Transactional
    List<Variant> getVariantForVariantId(String variantId) {
        return storage.findVariantById(variantId)
    }

    @Override
    @Transactional
    List<Gene> getGeneForGeneId(String geneId, @NotNull ListingArguments args) {
        return storage.findGeneById(geneId, args)
    }

    @Override
    @Transactional
    BeaconAlleleResponse getBeaconAlleleResponse(String chromosome, BigInteger start,
                                        String reference, String observed, String assemblyId) {

        List<Variant> variants = storage.findVariantsForBeaconResponse(chromosome, start, reference, observed, assemblyId)

        BeaconAlleleRequest request = new BeaconAlleleRequest()
        request.setAlternateBases(observed)
        request.setAssemblyId(assemblyId)
        request.setReferenceBases(reference)
        request.setReferenceName(chromosome)
        request.setStart(start)

        BeaconAlleleResponse response = new BeaconAlleleResponse()
        response.setAlleleRequest(request)
        response.setExists(!variants.empty)

        return response
    }

    @Override
    @Transactional
    List<Sample> getSampleForSampleId(String sampleId) {
        return storage.findSampleById(sampleId)
    }

    @Override
    @Transactional
    List<Case> getCasesForSpecifiedProperties(@NotNull ListingArguments args) {
        return storage.findCases(args)
    }

    @Override
    @Transactional
    List<Sample> getSamplesForSpecifiedProperties(@NotNull ListingArguments args) {
        return storage.findSamples(args)
    }


    @Override
    @Transactional
    List<Variant> getVariantsForSpecifiedProperties(@NotNull ListingArguments args, Boolean withConsequences) {
        return storage.findVariants(args, withConsequences)
    }

    @Override
    @Transactional
    List<Gene> getGenesForSpecifiedProperties(@NotNull ListingArguments args) {
        return storage.findGenes(args)
    }

    @Override
    @Transactional
    String getVcfContentForVariants(List<Variant> variants) {
        // order variants by contig and position in order to get valid VCF file
        return VariantExporter.exportVariantsToVCF(variants.sort { it.startPosition })
    }

    /**
     * Stores variants given in VCF file in the store.
     * @param url path to the VCF file
     */
    @Override
    @Transactional
    void storeVariantsInStore(String metadata, List<SimpleVariantContext> variants) {
        MetadataReader meta = new MetadataReader(metadata)
        def variantsToInsert = []

        variants.each { variant ->
            AnnotationHandler.addAnnotationsToVariant(variant, meta.getMetadataContext().getVariantAnnotation())
            variant.setIsSomatic(meta.getMetadataContext().getIsSomatic())
            variantsToInsert.add(variant)
        }

        log.info("Storing provided metadata and variants in the store")
        storage.storeVariantsInStoreWithMetadata(meta.getMetadataContext(), variantsToInsert)
        log.info("...done.")
    }

    /**
     * Stores gene information provided in a GFF3 file in the store.
     * @param url path to the GFF3 file
     */
    @Override
    @Transactional
    void storeGeneInformationInStore(EnsemblParser ensembl) {
        log.info("Storing provided gene information in store")
        storage.storeGenesWithMetadata(ensembl.version, ensembl.date, ensembl.referenceGenome, ensembl.genes)
        log.info("...done.")
    }
}

