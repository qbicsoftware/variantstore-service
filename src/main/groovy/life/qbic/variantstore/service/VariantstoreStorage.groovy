package life.qbic.variantstore.service

import life.qbic.variantstore.database.VariantstoreStorageException
import life.qbic.variantstore.model.Case
import life.qbic.variantstore.model.Consequence
import life.qbic.variantstore.model.Ensembl
import life.qbic.variantstore.model.Gene
import life.qbic.variantstore.model.ReferenceGenome
import life.qbic.variantstore.model.Sample
import life.qbic.variantstore.model.SimpleVariantContext
import life.qbic.variantstore.model.Variant
import life.qbic.variantstore.model.VariantCaller
import life.qbic.variantstore.model.Annotation

import life.qbic.variantstore.parser.MetadataContext
import life.qbic.variantstore.util.ListingArguments
import jakarta.inject.Singleton
import io.micronaut.core.annotation.NonNull

/**
 * The Variantstore storage interface.
 *
 * @since: 1.0.0
 */
@Singleton
interface VariantstoreStorage {

    /**
     * Find variants in store for issued Beacon request.
     * @param chromosome the chromosome
     * @param start the start position
     * @param reference the reference allele
     * @param observed the observed allele
     * @param assemblyId the identifier (build) of the reference genome
     * @return list of found variants
     */
    Set<Variant> findVariantsForBeaconResponse(String chromosome, BigInteger start,
                                          String reference, String observed, String assemblyId)

    /**
     * Find case in store by identifier.
     * @param identifier the case identifier
     * @return list of found cases
     */
    List<Case> findCaseById(String identifier)

    /**
     * Find samples in store by identifier.
     * @param identifier the sample identifier
     * @return list of found samples
     */
    List<Sample> findSampleById(String identifier)

    /**
     * Find variant in store by identifier.
     * @param identifier the variant identifier
     * @return list of found variants
     */
    Set<Variant> findVariantById(String identifier)

    /**
     * Find gene in store by identifier.
     * @param identifier the gene identifier
     * @param args further optional arguments to specify Ensembl version e.g.
     * @return list of found genes
     */
    Set<Gene> findGeneById(String identifier, @NonNull ListingArguments args)

    /**
     * Find cases for specified filtering options.
     * @param args the provided filtering options
     * @return list of found cases
     */
    List<Case> findCases(@NonNull ListingArguments args)

    /**
     * Find samples for specified filtering options.
     * @param args the provided filtering options
     * @return list of found samples
     */
    List<Sample> findSamples(@NonNull ListingArguments args)

    /**
     * Find variants for specified (filtering) options.
     * @param args the filtering options
     * @param referenceGenome the associated reference genome
     * @param withConsequences true if connected consequenes should be returned
     * @param annotationSoftware the associated annotation software
     * @param withVcfInfo true if connected VCF INFO should be returned
     * @param withGenotypes true if connected genotype information should be returned
     * @return list of found variants
     */
    Set<Variant> findVariants(@NonNull ListingArguments args, String referenceGenome, Boolean withConsequences,
                               String annotationSoftware, Boolean withVcfInfo, Boolean withGenotypes)

    /**
     * Find annotation software used to annotate the given consequence.
     * @param consequence the provided consequence
     * @return the found annotation software
     */
    Set<Annotation> findAnnotationSoftwareByConsequence(Consequence consequence)

    /**
     * Find reference genomes of given variant.
     * @param variant the provided variant
     * @return the found reference genomes
     */
    Set<ReferenceGenome> findReferenceGenomeByVariant(Variant variant)

    /**
     * Find genes for specified filtering options.
     * @param args the provided filtering options
     * @return list of found genes
     */
    Set<Gene> findGenes(@NonNull ListingArguments args)

    /**
     * Store case in the store.
     * @param patient the case to store
     */
    void storeCaseInStore(Case patient) throws VariantstoreStorageException

    /**
     * Store sample in the store.
     * @param sample the sample to store
     */
    void storeSampleInStore(Sample sample) throws VariantstoreStorageException

    /**
     * Store reference genome in the store.
     * @param referenceGenome the reference genome to store
     */
    void storeReferenceGenomeInStore(ReferenceGenome referenceGenome) throws VariantstoreStorageException

    /**
     * Store variant caller in the store.
     * @param variantCaller the variant caller to store
     */
    void storeVariantCallerInStore(VariantCaller variantCaller) throws VariantstoreStorageException

    /**
     * Store annotation software in the store.
     * @param annotationSoftware the annotation software to store
     */
    void storeAnnotationSoftwareInStore(Annotation annotationSoftware) throws VariantstoreStorageException

    /**
     * Store variants with provided metadata in the store.
     * @param metadata the provided metadata
     * @param sampleIdentifiers the provided sample identifiers (mapped to genotypes)
     * @param variantContext the provided variants
     */
    void storeVariantsInStoreWithMetadata(MetadataContext metadata, Map<String, Sample> sampleIdentifiers, ArrayList<SimpleVariantContext> variantContext) throws VariantstoreStorageException

    /**
     * Store genes with provided metadata in the store.
     * @param ensemblContext the ensemblContext
     */
    void storeGenesWithMetadata(Ensembl ensemblContext) throws VariantstoreStorageException
}
