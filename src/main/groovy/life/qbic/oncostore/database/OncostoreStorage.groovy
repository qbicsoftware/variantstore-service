package life.qbic.oncostore.database

import life.qbic.oncostore.model.Case
import life.qbic.oncostore.model.Gene
import life.qbic.oncostore.model.ReferenceGenome
import life.qbic.oncostore.model.Sample
import life.qbic.oncostore.model.SimpleVariantContext
import life.qbic.oncostore.model.Variant
import life.qbic.oncostore.model.VariantCaller
import life.qbic.oncostore.model.Annotation
import life.qbic.oncostore.parser.MetadataContext
import life.qbic.oncostore.util.ListingArguments

import javax.inject.Singleton
import javax.validation.constraints.NotNull

@Singleton
interface OncostoreStorage {

    List<Variant> findVariantsForBeaconResponse(String chromosome, BigInteger start,
                                          String reference, String observed, String assemblyId, ListingArguments args)

    List<Case> findCaseById(String id)

    List<Sample> findSampleById(String id)

    List<Variant> findVariantById(String id)

    List<Gene> findGeneById(String id)

    List<Case> findCases(@NotNull ListingArguments args)

    List<Sample> findSamples(@NotNull ListingArguments args)

    List<Variant> findVariants(@NotNull ListingArguments args)

    List<Gene> findGenes(@NotNull ListingArguments args)

    void storeCaseInStore(Case patient) throws OncostoreStorageException

    void storeSampleInStore(Sample sample) throws OncostoreStorageException

    void storeReferenceGenomeInStore(ReferenceGenome referenceGenome) throws OncostoreStorageException

    void storeVariantCallerInStore(VariantCaller variantCaller) throws OncostoreStorageException

    void storeAnnotationSoftwareInStore(Annotation annotationSoftware) throws OncostoreStorageException

    void storeVariantsInStoreWithMetadata(MetadataContext metadata, List<SimpleVariantContext> variantContext) throws OncostoreStorageException

}
