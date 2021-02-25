package life.qbic.variantstore.service

import groovy.util.logging.Log4j2
import life.qbic.variantstore.model.*
import life.qbic.variantstore.parser.EnsemblParser
import life.qbic.variantstore.parser.MetadataContext
import life.qbic.variantstore.parser.MetadataReader
import life.qbic.variantstore.parser.SimpleVCFReader
import life.qbic.variantstore.util.AnnotationHandler
import life.qbic.variantstore.util.ListingArguments
import life.qbic.variantstore.util.VariantExporter
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.constraints.NotNull

/**
 * A VariantstoreService implementation.
 *
 * This provides a service layer between controllers and VariantstoreStorage.
 *
 * @since: 1.0.0
 */
@Log4j2
@Singleton
class VariantstoreInformationCenter implements VariantstoreService{

    /**
     * The maximum allele length that is consideres for further processing.
     * Variants with a larger allele length are filtered.
     */
    static final Integer MAX_ALLELE_LENGTH = 255
    /**
     * The maximum number of variants that are processed and forwarded to the storage interface per batch.
     * This has effects on run time and memory consumption.
     */
    static final Integer MAX_NUMBER_OF_VARIANTS_PER_BATCH = 250000
    /**
     * The string used in a VCF file to represent a tumor genotype
     */
    static final String TUMOR_ENTITY = "TUMOR"
    /**
     * The string used in VCF to represent a benign genotype
     */
    static final String NORMAL_ENTITY = "NORMAL"
    /**
     * The Variantstore storage
     */
    private final VariantstoreStorage storage

    @Inject
    VariantstoreInformationCenter(VariantstoreStorage storage) {
        this.storage = storage
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    List<Case> getCaseForCaseId(String caseId) {
        return storage.findCaseById(caseId)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    List<SimpleVariantContext> getVariantForVariantId(String variantId) {
        return storage.findVariantById(variantId)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    List<Gene> getGeneForGeneId(String geneId, @NotNull ListingArguments args) {
        return storage.findGeneById(geneId, args)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    BeaconAlleleResponse getBeaconAlleleResponse(String chromosome, BigInteger start,
                                        String reference, String observed, String assemblyId) {

        List<Variant> variants = storage.findVariantsForBeaconResponse(chromosome, start, reference, observed, assemblyId)
        BeaconAlleleRequest request = new BeaconAlleleRequest(chromosome, start, reference, observed, assemblyId)
        BeaconAlleleResponse response = new BeaconAlleleResponse(request, !variants.empty)
        return response
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    List<Sample> getSampleForSampleId(String sampleId) {
        return storage.findSampleById(sampleId)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    List<Case> getCasesForSpecifiedProperties(@NotNull ListingArguments args) {
        return storage.findCases(args)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    List<Sample> getSamplesForSpecifiedProperties(@NotNull ListingArguments args) {
        return storage.findSamples(args)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    List<Gene> getGenesForSpecifiedProperties(@NotNull ListingArguments args) {
        return storage.findGenes(args)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    String getVcfContentForVariants(List<SimpleVariantContext> variants, Boolean withConsequences, Boolean
            withGenotypes,
                                    String referenceGenome, String annotationSoftware, String
                                            annotationSoftwareVersion, String version) {
        // order variants by position in order to get valid VCF file
        return VariantExporter.exportVariantsToVCF(variants.sort { a, b ->
            (a.chromosome?.isInteger() ? a.chromosome
                    .toInteger() : a.chromosome) <=> (b.chromosome?.isInteger() ? b.chromosome.toInteger() : b
                    .chromosome) ?: a.startPosition <=> b.startPosition
        } as List<SimpleVariantContext>, withConsequences, withGenotypes, referenceGenome, annotationSoftware,
                annotationSoftwareVersion, version)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    String getFhirContentForVariants(List<SimpleVariantContext> variants, Boolean withConsequences, String referenceGenome) {
        return VariantExporter.exportVariantsToFHIR(variants.sort { a, b -> (a.chromosome?.isInteger() ? a.chromosome
                .toInteger() : a.chromosome) <=> (b.chromosome?.isInteger() ? b.chromosome.toInteger() : b
                .chromosome) ?: a.startPosition <=> b.startPosition }, withConsequences, referenceGenome) }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    List<SimpleVariantContext> getVariantsForSpecifiedProperties(ListingArguments args, String referenceGenome, Boolean
            withConsequences, String annotationSoftware, Boolean withVcfInfo, Boolean withGenotypes) {
        def variants = storage.findVariants(args, referenceGenome, withConsequences, annotationSoftware, withVcfInfo, withGenotypes)
        return variants
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    void storeVariantsInStore(String metadata, InputStream inputStream, TransactionStatusRepository repository, TransactionStatus transactionStatus) {
        MetadataReader meta = new MetadataReader(metadata)
        Annotation annotationSoftware =  meta.getMetadataContext().getVariantAnnotation()
        AnnotationHandler.AnnotationTools annotationTool = annotationSoftware.getName().toUpperCase()  as AnnotationHandler.AnnotationTools
        SimpleVCFReader reader = new SimpleVCFReader(inputStream, annotationTool.getTag())
        SimpleVariantContext variant = null
        ArrayList<SimpleVariantContext> variantsToInsert = null
        def sampleGenotypeMapping = [:]
        def idx = 0

        log.info("Storing provided metadata and variants in the store")
        try {
            while (reader.iterator().hasNext()) {
                variant = reader.iterator().next()

                AnnotationHandler.addAnnotationsToVariant(variant, annotationSoftware)
                variant.setIsSomatic(meta.getMetadataContext().getIsSomatic())

                if (idx == 0) {
                    sampleGenotypeMapping = determineGenotypeMapping(variant, meta.getMetadataContext())
                }

                if ((variant.referenceAllele.length() > MAX_ALLELE_LENGTH) || (variant.observedAllele.length() > MAX_ALLELE_LENGTH)) {
                    def warning = new StringBuilder("Skipping variant").append(variant.startPosition).append(":")
                            .append(variant.referenceAllele).append(">").append(" because the reference or observed "
                            + "allele is exceeding the maximum length\"")
                    log.warn(warning)
                }  else {
                    if (variantsToInsert == null) variantsToInsert = new ArrayList<>(250000)
                    variantsToInsert.add(variant)
                    idx += 1
                }

                if (idx == MAX_NUMBER_OF_VARIANTS_PER_BATCH) {
                    storage.storeVariantsInStoreWithMetadata(meta.getMetadataContext(), sampleGenotypeMapping,variantsToInsert)
                    variantsToInsert.clear()
                    idx = 0
                }
            }
            storage.storeVariantsInStoreWithMetadata(meta.getMetadataContext(), sampleGenotypeMapping, variantsToInsert)
            variantsToInsert.clear()
            repository.updateStatus(transactionStatus.getId(), Status.finished.toString())
            log.info("...done.")
        }
        finally {
            reader.iterator().close()
            inputStream.close()
            Runtime.getRuntime().gc()
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    void storeGeneInformationInStore(EnsemblParser ensembl) {
        log.info("Storing provided gene information in store")
        storage.storeGenesWithMetadata(ensembl.version, ensembl.date, ensembl.referenceGenome, ensembl.genes)
        log.info("...done.")
    }

    /**
     * Maps given genotype information to provided metadata on samples.
     * @param variant a variant
     * @param meta provided metadata
     * @return map with connected genotype and sample information
     */
    HashMap determineGenotypeMapping(SimpleVariantContext variant, MetadataContext meta) {
        // get sample identifiers provided in VCF file and compare them with the samples provided in metadata JSON
        def genotypesIdentifiers = new ArrayList<String>(variant.genotypes.size())
        def sampleGenotypeMapping = [:]

        variant.genotypes.each { genotype ->
            if (genotype.sampleName) genotypesIdentifiers.add(genotype.sampleName)
        }

        if (genotypesIdentifiers.isEmpty()) {
            log.info("No sample/genotype information provided in VCF, using information given in metadata file.")
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
                        log.info("Genotype identifier does not match any sample identifiers. Trying to map identifiers...")
                        if (sample.cancerEntity && genotypesIdentifiers.findIndexOf { TUMOR_ENTITY } > -1) {
                            sampleGenotypeMapping[NORMAL_ENTITY] = sample
                        } else if (!sample.cancerEntity && genotypesIdentifiers.findIndexOf { NORMAL_ENTITY } > -1) {
                            sampleGenotypeMapping[TUMOR_ENTITY] = sample
                        } else {
                            log.error("Could not map genotype information to provided sample information.")
                        }
                    }
                }
            }
        }
        genotypesIdentifiers.clear()
        return sampleGenotypeMapping
    }
}


