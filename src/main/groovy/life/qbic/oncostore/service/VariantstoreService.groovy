package life.qbic.oncostore.service

import life.qbic.oncostore.model.*
import life.qbic.oncostore.parser.EnsemblParser
import life.qbic.oncostore.util.ListingArguments
import javax.inject.Singleton

/**
 * The Variantstore service interface.
 *
 * @since: 1.0.0
 */
@Singleton
interface VariantstoreService {

    /**
     * Retrieves cases for specified case identifier.
     * @param identifier the case identifier
     * @return list of found cases
     */
    List<Case> getCaseForCaseId(String identifier)
    /**
     * Retrieves variants for specified variant identifier.
     * @param identifier the variant identifier
     * @return list of found variants
     */
    List<SimpleVariantContext> getVariantForVariantId(String identifier)
    /**
     * Retrieves genes for specified gene identifier and optionally arguments to e.g. specify the Ensembl version.
     * @param identifier the gene identifier
     * @param args optional arguments to specify Ensembl version
     * @return list of found genes
     */
    List<Gene> getGeneForGeneId(String identifier, ListingArguments args)
    /**
     * Retrieves samples for specified variant identifier.
     * @param identifier the variant identifier
     * @return list of found variants
     */
    List<Sample> getSampleForSampleId(String identifier)
    /**
     * Retrieves cases for specified filtering options.
     * @param args the provided filtering options
     * @return list of cases genes
     */
    List<Case> getCasesForSpecifiedProperties(ListingArguments args)
    /**
     * Retrieves samples for specified filtering options.
     * @param args the provided filtering options
     * @return list of found samples
     */
    List<Sample> getSamplesForSpecifiedProperties(ListingArguments args)
    /**
     * Retrieves variants for specified (filtering) options.
     * @param args the filtering options
     * @param referenceGenome the associated reference genome
     * @param withConsequences true if connected consequenes should be returned
     * @param annotationSoftware the associated annotation software
     * @param withVcfInfo true if connected VCF INFO should be returned
     * @param withGenotypes true if connected genotype information should be returned
     * @return list of found variants
     */
    List<SimpleVariantContext> getVariantsForSpecifiedProperties(ListingArguments args, String referenceGenome, Boolean
            withConsequences, String annotationSoftware, Boolean withVcfInfo, Boolean withGenotypes)
    /**
     * Retrieves genes for specified filtering options.
     * @param args the provided filtering options
     * @return list of found genes
     */
    List<Gene> getGenesForSpecifiedProperties(ListingArguments args)
    /**
     * Generates the Beacon allele response for given information.
     * @param chromosome the chromosome
     * @param start the start position
     * @param reference the reference allele
     * @param observed the observed allele
     * @param assemblyId the identifier (build) of the reference genome
     * @return the response issued by the Beacon
     */
    BeaconAlleleResponse getBeaconAlleleResponse(String chromosome, BigInteger start,
                                                 String reference, String observed, String assemblyId)
    /**
     * Generates content in Variant Call Format (VCF) for given set of variants.
     * @param variants the provided variants
     * @param withConsequences true if consequences should be included in VCF
     * @param withGenotypes true if genotype information should be included in VCF
     * @param referenceGenome the reference genome
     * @param annotationSoftware the annotation software
     * @param annotationSoftwareVersion the annotation software version
     * @param version the VCF version
     * @return the variant content in Variant Call Format
     */
    String getVcfContentForVariants(List<SimpleVariantContext> variants, Boolean withConsequences, Boolean withGenotypes, String
            referenceGenome, String annotationSoftware, String annotationSoftwareVersion, String version)
    /**
     * Generates content in FHIR format for given set of variants.
     * @param variants the provided variants
     * @param withConsequences true if consequences should be included in VCF
     * @param withGenotypes true if genotype information should be included in VCF
     * @param referenceGenome the reference genome
     * @param annotationSoftware the annotation software
     * @return the variant content in FHIR format
     */
    String getFhirContentForVariants(List<SimpleVariantContext> variants, Boolean withConsequences, String referenceGenome)
    /**
     * Stores variants given in VCF file and accompanied metadata in the store.
     * @param metadata JSON string holding metadata
     * @param inputStream input stream of variant content
     */
    void storeVariantsInStore(String metadata, InputStream inputStream, TransactionStatusRepository repository, TransactionStatus transactionStatus)
    /**
     * Stores gene information provided in a GFF3 file (Ensembl) in the store.
     * @param ensemblParser parser to extract information from GFF3 file
     */
    void storeGeneInformationInStore(EnsemblParser ensemblParser)
}
