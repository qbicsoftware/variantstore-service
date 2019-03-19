package life.qbic.oncostore.oncoloader

import groovy.json.JsonSlurper
import life.qbic.oncostore.model.Annotation
import life.qbic.oncostore.model.ReferenceGenome
import life.qbic.oncostore.model.VariantCaller

class MetadataReader {

    final MetadataContext metadataContext

    MetadataReader(File file) {
        def slurper = new JsonSlurper()
        def jsonContent = slurper.parse(file)
        this.metadataContext = new MetadataContext(jsonContent.is_somatic, parseCallingSoftware(jsonContent), parseAnnotationSoftware(jsonContent), parseReferenceGenome(jsonContent), jsonContent.sample_id, jsonContent.vcf_files)
    }

    MetadataContext getMetadataContext() {
        return metadataContext
    }

    static Annotation parseAnnotationSoftware(jsonContent) {
        return new Annotation(jsonContent.variant_annotation.name, jsonContent.variant_annotation.version, jsonContent.variant_annotation.doi)
    }

    static VariantCaller parseCallingSoftware(jsonContent) {
        return new VariantCaller(jsonContent.variant_calling.name, jsonContent.variant_calling.version, jsonContent.variant_calling.doi)
    }

    static ReferenceGenome parseReferenceGenome(jsonContent) {
        return new ReferenceGenome(jsonContent.reference_genome.source, jsonContent.reference_genome.build, jsonContent.reference_genome.version)
    }
}