package life.qbic.oncostore.service

import groovy.util.logging.Log4j2
import life.qbic.oncostore.model.*
import life.qbic.oncostore.parser.EnsemblParser
import life.qbic.oncostore.parser.MetadataContext
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
    List<Gene> getGenesForSpecifiedProperties(@NotNull ListingArguments args) {
        return storage.findGenes(args)
    }

    @Override
    @Transactional
    String getVcfContentForVariants(List<Variant> variants, Boolean withConsequences, Boolean withGenotypes, String referenceGenome, String annotationSoftware) {
        // order variants by position in order to get valid VCF file
        return VariantExporter.exportVariantsToVCF(variants.sort { a, b -> (a.chromosome?.isInteger() ? a.chromosome
                .toInteger() : a.chromosome) <=> (b.chromosome?.isInteger() ? b.chromosome.toInteger() : b
                .chromosome) ?: a.startPosition <=> b.startPosition }, withConsequences, withGenotypes, referenceGenome, annotationSoftware) }

    @Override
    @Transactional
    String getFhirContentForVariants(List<Variant> variants, Boolean withConsequences, String referenceGenome) {
        return VariantExporter.exportVariantsToFHIR(variants.sort { a, b -> (a.chromosome?.isInteger() ? a.chromosome
                .toInteger() : a.chromosome) <=> (b.chromosome?.isInteger() ? b.chromosome.toInteger() : b
                .chromosome) ?: a.startPosition <=> b.startPosition }, withConsequences, referenceGenome) }

    @Override
    @Transactional
    List<Variant> getVariantsForSpecifiedProperties(ListingArguments args, String referenceGenome, Boolean
            withConsequences, String annotationSoftware, Boolean withVcfInfo, Boolean withGenotypes) {
        def variants = storage.findVariants(args, referenceGenome, withConsequences, annotationSoftware, withVcfInfo, withGenotypes)
        return variants
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

            if ((variant.referenceAllele.length() > 255) || (variant.observedAllele.length() > 255)) {
                log.warn("Skipping variant ${variant.startPosition}:${variant.referenceAllele}>${variant.observedAllele} because the reference or observed allele is exceeding the maximum " + "length")
            }
            else {
                variantsToInsert.add(variant)
            }
        }

        // get sample identifiers provided in VCF file and compare them with the samples provided in metadata JSON
        def genotypesIdentifiers = []
        def sampleGenotypeMapping = [:]
        variants.get(0).genotypes.each {genotype ->
            if (genotype.sampleName) genotypesIdentifiers.add(genotype.sampleName)
        }

        if (genotypesIdentifiers.isEmpty()) {
            log.info("No sample/genotype information provided in VCF, using information given in metadata file.")
            meta.getMetadataContext().samples.each { sample ->
                sampleGenotypeMapping[sample.identifier] = sample
            }
        }
        else {
            assert genotypesIdentifiers.size() == meta.getMetadataContext().samples.size()
            // in case of one identifier, the mapping is straightforward
            if(genotypesIdentifiers.size() == 1) {
                sampleGenotypeMapping[genotypesIdentifiers.get(0)] = meta.getMetadataContext().samples.get(0)
            }
            else {
                meta.getMetadataContext().samples.each { sample ->
                    def searchIndex = genotypesIdentifiers.indexOf(sample.identifier)
                    if (searchIndex > -1) {
                        sampleGenotypeMapping[genotypesIdentifiers[searchIndex]] = sample
                    } else {
                        log.info("Genotype identifier does not match any sample identifiers. Trying to map identifiers...")
                        if (sample.cancerEntity && genotypesIdentifiers.findIndexOf{"TUMOR"} > -1) {
                            sampleGenotypeMapping["NORMAL"] = sample
                        } else if (!sample.cancerEntity && genotypesIdentifiers.findIndexOf{"NORMAL"} > -1) {
                            sampleGenotypeMapping["TUMOR"] = sample
                        } else {
                            log.error("Could not map genotype information to provided sample information.")
                        }
                    }
                }
            }
        }
        log.info("Storing provided metadata and variants in the store")
        storage.storeVariantsInStoreWithMetadata(meta.getMetadataContext(), sampleGenotypeMapping, variantsToInsert)
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

