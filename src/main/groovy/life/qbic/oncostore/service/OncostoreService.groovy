package life.qbic.oncostore.service

import life.qbic.oncostore.model.Variant
import life.qbic.oncostore.model.Sample
import life.qbic.oncostore.model.BeaconAlleleResponse

import life.qbic.oncostore.util.ListingArguments

import javax.inject.Singleton

@Singleton
interface OncostoreService {

    List<Variant> getVariantForVariantId(String variantId)

    BeaconAlleleResponse getBeaconAlleleResponse(String chromosome, BigInteger start,
                                          String reference, String observed, String assemblyId, ListingArguments args)

    List<Sample> getSampleForSampleId(String sampleId)

    List<Sample> getSamplesForSpecifiedProperties(ListingArguments args)

    List<Sample> getVariantsForSpecifiedProperties(ListingArguments args)

    void storeVariantsInStore(String url)

}
