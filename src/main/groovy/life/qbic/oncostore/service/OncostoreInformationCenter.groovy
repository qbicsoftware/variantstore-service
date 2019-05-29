package life.qbic.oncostore.service

import htsjdk.variant.vcf.VCFFileReader
import life.qbic.oncostore.database.OncostoreStorage
import life.qbic.oncostore.model.BeaconAlleleRequest
import life.qbic.oncostore.model.BeaconAlleleResponse
import life.qbic.oncostore.model.Sample
import life.qbic.oncostore.model.Variant
import life.qbic.oncostore.parser.MetadataReader
import life.qbic.oncostore.parser.SimpleVCFReader
import life.qbic.oncostore.util.ListingArguments

import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.constraints.NotNull

@Singleton
class OncostoreInformationCenter implements OncostoreService{

    private final OncostoreStorage storage

    @Inject OncostoreInformationCenter(OncostoreStorage storage) {
        this.storage = storage
    }

    @Override
    List<Variant> getVariantForVariantId(String variantId) {
        return storage.findVariantById(variantId)
    }

    @Override
    BeaconAlleleResponse getBeaconAlleleResponse(String chromosome, BigInteger start,
                                        String reference, String observed, String assemblyId, ListingArguments args) {

        List<Variant> variants = storage.findVariantsForBeaconResponse(chromosome, start, reference, observed, assemblyId, args)

        BeaconAlleleRequest request = new BeaconAlleleRequest()
        request.setAlternateBases(observed)
        request.setAssemblyId(assemblyId)
        request.setReferenceBases(reference)
        request.setReferenceName(chromosome)
        request.setStart(startPosition)

        BeaconAlleleResponse response = new BeaconAlleleResponse()
        response.setAlleleRequest(request)
        response.setExists(!variants.empty)

        return response
    }

    @Override
    List<Sample> getSampleForSampleId(String sampleId) {
        return storage.findSampleById(sampleId)
    }

    @Override
    List<Sample> getSamplesForSpecifiedProperties(@NotNull ListingArguments args) {
        return storage.findSamples(args)
    }

    @Override
    List<Sample> getVariantsForSpecifiedProperties(@NotNull ListingArguments args) {
        return storage.findVariants(args)
    }

    @Override
    void storeVariantsInStore(String url) {
        MetadataReader meta = new MetadataReader(new File(url))

        def vcfFiles = meta.getMetadataContext().getVcfFiles()

        storage.storeVariantCallerInStore(meta.getMetadataContext().getVariantCalling())
        println(meta.getMetadataContext().getVariantCalling().getName())

        //this.isSomatic = isSomatic
        //this.variantCalling = variantCalling
        //this.variantAnnotation = variantAnnotation
        //this.referenceGenome = referenceGenome
        //this.sampleID = sampleID
        //this.vcfFiles = vcfFiles

        //TODO
        // write metadata information to store

        // write variant information to store for each vcf file
        vcfFiles.each { filePath ->
            SimpleVCFReader reader = new SimpleVCFReader((new VCFFileReader(new File(filePath), false)))
            println(filePath)
        }
    }

}

