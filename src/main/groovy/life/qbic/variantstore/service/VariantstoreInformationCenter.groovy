package life.qbic.variantstore.service

import groovy.util.logging.Log4j2
import io.micronaut.context.annotation.Value
import life.qbic.variantstore.model.*
import life.qbic.variantstore.parser.MetadataContext
import life.qbic.variantstore.parser.MetadataReader
import life.qbic.variantstore.parser.SimpleVCFReader
import life.qbic.variantstore.repositories.TransactionStatusRepository
import life.qbic.variantstore.util.AnnotationHandler
import life.qbic.variantstore.util.ListingArguments
import life.qbic.variantstore.util.VariantExporter
import jakarta.inject.*
import io.micronaut.core.annotation.NonNull
import life.qbic.variantstore.util.VcfConstants

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
     * The maximum allele length that is considered for further processing.
     * Variants with a larger allele length are filtered.
     */
    static final int MAX_ALLELE_LENGTH = 255
    /**
     * The maximum number of variants that are processed and forwarded to the storage interface per batch.
     * This has effects on run time and memory consumption.
     */
    @Value('${max-variants-per-batch}')
    static final int MAX_NUMBER_OF_VARIANTS_PER_BATCH
    /**
     * The Variantstore storage
     */
    private VariantstoreStorage storage

    @Inject
    VariantstoreInformationCenter(VariantstoreStorage storage) {
        this.storage = storage
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Optional<Project> getProjectForProjectId(String projectId) {
        return storage.findProjectById(projectId)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<Case> getCaseForCaseId(String caseId) {
        return storage.findCaseById(caseId)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Set<SimpleVariantContext> getVariantForVariantId(String variantId) {
        return storage.findVariantById(variantId)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Set<Gene> getGeneForGeneId(String geneId, @NonNull ListingArguments args) {
        return storage.findGeneById(geneId, args)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    BeaconAlleleResponse getBeaconAlleleResponse(String chromosome, BigInteger start,
                                        String reference, String observed, String assemblyId) {

        Set<Variant> variants = storage.findVariantsForBeaconResponse(chromosome, start, reference, observed, assemblyId)
        BeaconAlleleRequest request = new BeaconAlleleRequest(chromosome, start, reference, observed, assemblyId)
        BeaconAlleleResponse response = new BeaconAlleleResponse(request, !variants.empty)
        return response
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<Sample> getSampleForSampleId(String sampleId) {
        return storage.findSampleById(sampleId)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<Project> getProjectsForSpecifiedProperties(@NonNull ListingArguments args) {
        return storage.findProjects(args)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<Case> getCasesForSpecifiedProperties(@NonNull ListingArguments args) {
        return storage.findCases(args)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<Sample> getSamplesForSpecifiedProperties(@NonNull ListingArguments args) {
        return storage.findSamples(args)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Set<Gene> getGenesForSpecifiedProperties(@NonNull ListingArguments args) {
        return storage.findGenes(args)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    String getVcfContentForVariants(Set<SimpleVariantContext> variants, Boolean withConsequences, Boolean withGenotypes,
                                    String referenceGenome, String annotationSoftware, String annotationSoftwareVersion,
                                    String version) {
        // order variants by position in order to get valid VCF file
        return VariantExporter.exportVariantsToVCF(variants.sort { a, b ->
            (a.chromosome?.isInteger() ? a.chromosome.toInteger() : a.chromosome) <=> (b.chromosome?.isInteger() ?
                    b.chromosome.toInteger() : b.chromosome) ?: a.startPosition <=> b.startPosition} as
                List<SimpleVariantContext>, withConsequences, withGenotypes, referenceGenome, annotationSoftware,
                annotationSoftwareVersion, version)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    String getFhirContentForVariants(Set<SimpleVariantContext> variants, Boolean withConsequences, String referenceGenome) {
        return VariantExporter.exportVariantsToFHIR(variants.sort { a, b ->
            (a.chromosome?.isInteger() ? a.chromosome.toInteger() : a.chromosome) <=> (b.chromosome?.isInteger() ?
                    b.chromosome.toInteger() : b.chromosome) ?: a.startPosition <=> b.startPosition }, withConsequences, referenceGenome) }

    /**
     * {@inheritDoc}
     */
    @Override
    Set<SimpleVariantContext> getVariantsForSpecifiedProperties(ListingArguments args, String referenceGenome,
                                                                 Boolean withConsequences, String annotationSoftware,
                                                                 Boolean withVcfInfo, Boolean withGenotypes) {
        def variants = storage.findVariants(args, referenceGenome, withConsequences, annotationSoftware,
                withVcfInfo, withGenotypes)
        return variants
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void storeVariantsInStore(String metadata, InputStream inputStream, TransactionStatusRepository repository, TransactionStatus transactionStatus) {
        MetadataReader meta = new MetadataReader(metadata)
        MetadataContext metadataContext = meta.getMetadataContext()

        Annotation annotationSoftware =  metadataContext.getVariantAnnotation()
        AnnotationHandler.AnnotationTools annotationTool = annotationSoftware.getName().toUpperCase()  as AnnotationHandler.AnnotationTools
        SimpleVCFReader reader = new SimpleVCFReader(inputStream, annotationTool.getTag())

        SimpleVariantContext variant
        HashMap<String, Sample> sampleGenotypeMapping = new HashMap<String, Sample>()
        def idx = 0

        ArrayList<SimpleVariantContext> variantsToInsert = null

        log.info("Storing provided metadata and variants in the store")
        try {
            while (reader.iterator().hasNext()) {
                variant = reader.iterator().next()

                AnnotationHandler.addAnnotationsToVariant(variant, annotationSoftware)
                variant.setIdentifier(UUID.randomUUID().toString())
                variant.setSomatic(metadataContext.getIsSomatic())

                if (idx == 0) {
                    sampleGenotypeMapping = determineGenotypeMapping(variant, metadataContext)
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
                    storage.storeVariantsInStoreWithMetadata(metadataContext, sampleGenotypeMapping,variantsToInsert)
                    variantsToInsert.clear()
                    idx = 0
                }
            }
            storage.storeVariantsInStoreWithMetadata(metadataContext, sampleGenotypeMapping, variantsToInsert)
            variantsToInsert.clear()
            repository.updateStatus(transactionStatus.getId(), Status.finished.toString())
            log.info("...done.")
        }
        catch (Exception e) {
            e.printStackTrace()
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
    void storeGeneInformationInStore(Ensembl ensembl) {
        log.info("Storing provided gene information in store")
        storage.storeGenesWithMetadata(ensembl)
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
                        if (sample.cancerEntity && genotypesIdentifiers.findIndexOf { VcfConstants.TUMOR } > -1) {
                            sampleGenotypeMapping[VcfConstants.NORMAL] = sample
                        } else if (!sample.cancerEntity && genotypesIdentifiers.findIndexOf { VcfConstants.NORMAL } > -1) {
                            sampleGenotypeMapping[VcfConstants.TUMOR] = sample
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
