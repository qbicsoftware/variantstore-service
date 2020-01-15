package life.qbic.oncostore.service

import life.qbic.oncostore.database.VariantstoreStorageException
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
interface VariantstoreStorage {

    List<Variant> findVariantsForBeaconResponse(String chromosome, BigInteger start,
                                          String reference, String observed, String assemblyId)

    List<Case> findCaseById(String id)

    List<Sample> findSampleById(String id)

    List<Variant> findVariantById(String id)

    List<Gene> findGeneById(String id, @NotNull ListingArguments args)

    List<Case> findCases(@NotNull ListingArguments args)

    List<Sample> findSamples(@NotNull ListingArguments args)

    List<Variant> findVariants(@NotNull ListingArguments args)

    List<Gene> findGenes(@NotNull ListingArguments args)

    void storeCaseInStore(Case patient) throws VariantstoreStorageException

    void storeSampleInStore(Sample sample) throws VariantstoreStorageException

    void storeReferenceGenomeInStore(ReferenceGenome referenceGenome) throws VariantstoreStorageException

    void storeVariantCallerInStore(VariantCaller variantCaller) throws VariantstoreStorageException

    void storeAnnotationSoftwareInStore(Annotation annotationSoftware) throws VariantstoreStorageException

    void storeVariantsInStoreWithMetadata(MetadataContext metadata, List<SimpleVariantContext> variantContext) throws VariantstoreStorageException

    void storeGenesWithMetadata(Integer version, String date, ReferenceGenome referenceGenome, List<Gene> genes) throws VariantstoreStorageException
}
