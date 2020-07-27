package life.qbic.oncostore.service

import life.qbic.oncostore.model.*
import life.qbic.oncostore.parser.EnsemblParser
import life.qbic.oncostore.parser.MetadataContext
import life.qbic.oncostore.util.ListingArguments

import javax.inject.Singleton

@Singleton
interface VariantstoreService {

    List<Case> getCaseForCaseId(String identifier)

    List<Variant> getVariantForVariantId(String variantId)

    List<Gene> getGeneForGeneId(String geneId, ListingArguments args)

    List<Sample> getSampleForSampleId(String sampleId)

    List<Case> getCasesForSpecifiedProperties(ListingArguments args)

    List<Sample> getSamplesForSpecifiedProperties(ListingArguments args)

    List<Variant> getVariantsForSpecifiedProperties(ListingArguments args, String referenceGenome, Boolean
            withConsequences, String annotationSoftware, Boolean withVcfInfo, Boolean withGenotypes)

    List<Gene> getGenesForSpecifiedProperties(ListingArguments args)

    BeaconAlleleResponse getBeaconAlleleResponse(String chromosome, BigInteger start,
                                                 String reference, String observed, String assemblyId)

    String getVcfContentForVariants(List<Variant> variants, Boolean withConsequences, String referenceGenome, String annotationSoftware)

    void storeVariantsInStore(String metadata, List<SimpleVariantContext> variants)

    void storeGeneInformationInStore(EnsemblParser ensemblParser)
}
