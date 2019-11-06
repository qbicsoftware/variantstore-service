package life.qbic.oncostore.service

import life.qbic.oncostore.model.Case
import life.qbic.oncostore.model.Gene
import life.qbic.oncostore.model.Variant
import life.qbic.oncostore.model.Sample
import life.qbic.oncostore.model.BeaconAlleleResponse

import life.qbic.oncostore.util.ListingArguments

import javax.inject.Singleton

@Singleton
interface OncostoreService {

    List<Case> getCaseForCaseId(String identifier)

    List<Variant> getVariantForVariantId(String variantId)

    List<Gene> getGeneForGeneId(String geneId, ListingArguments args)

    List<Sample> getSampleForSampleId(String sampleId)

    List<Case> getCasesForSpecifiedProperties(ListingArguments args)

    List<Sample> getSamplesForSpecifiedProperties(ListingArguments args)

    List<Variant> getVariantsForSpecifiedProperties(ListingArguments args)

    List<Gene> getGenesForSpecifiedProperties(ListingArguments args)

    BeaconAlleleResponse getBeaconAlleleResponse(String chromosome, BigInteger start,
                                                 String reference, String observed, String assemblyId, ListingArguments args)

    void storeVariantsInStore(String url)

    void storeGeneInformationInStore(String url)
}
