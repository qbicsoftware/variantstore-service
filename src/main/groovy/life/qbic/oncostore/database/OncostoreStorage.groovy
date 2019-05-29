package life.qbic.oncostore.database

import life.qbic.oncostore.model.ReferenceGenome
import life.qbic.oncostore.model.Sample
import life.qbic.oncostore.model.SimpleVariant
import life.qbic.oncostore.model.SimpleVariantContext
import life.qbic.oncostore.model.Variant
import life.qbic.oncostore.model.VariantCaller
import life.qbic.oncostore.model.Annotation
import life.qbic.oncostore.util.ListingArguments

import javax.inject.Singleton
import javax.validation.constraints.NotNull

@Singleton
interface OncostoreStorage {

    List<Variant> findVariantsForBeaconResponse(String chromosome, BigInteger start,
                                          String reference, String observed, String assemblyId, ListingArguments args)

    List<Sample> findSampleById(String id)

    List<Variant> findVariantById(String id)

    List<Sample> findSamples(@NotNull ListingArguments args)

    List<Sample> findVariants(@NotNull ListingArguments args)

    void storeSampleInStore(Sample sample) throws OncostoreStorageException

    void storeReferenceGenomeInStore(ReferenceGenome referenceGenome) throws OncostoreStorageException

    void storeVariantCallerInStore(VariantCaller variantCaller) throws OncostoreStorageException

    void storeAnnotationSoftwareInStore(Annotation annotationSoftware) throws OncostoreStorageException

    void storeVariantsInStore(SimpleVariantContext variantContext) throws OncostoreStorageException

}
