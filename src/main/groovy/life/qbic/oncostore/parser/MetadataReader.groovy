package life.qbic.oncostore.parser

import groovy.json.JsonSlurper
import life.qbic.oncostore.model.Annotation
import life.qbic.oncostore.model.Case
import life.qbic.oncostore.model.ReferenceGenome
import life.qbic.oncostore.model.VariantCaller
import life.qbic.oncostore.model.Sample


class MetadataReader {

    final MetadataContext metadataContext

    MetadataReader(File file) {
        def slurper = new JsonSlurper()
        def jsonContent = slurper.parse(file)
        this.metadataContext = new MetadataContext(parseIsSomatic(jsonContent), parseCallingSoftware(jsonContent), parseAnnotationSoftware(jsonContent), parseReferenceGenome(jsonContent), parseCase(jsonContent), parseSample(jsonContent), parseVcfFiles(jsonContent))
    }

    MetadataReader(String content) {
        def slurper = new JsonSlurper()
        def jsonContent = slurper.parseText(content)
        this.metadataContext = new MetadataContext(parseIsSomatic(jsonContent), parseCallingSoftware(jsonContent), parseAnnotationSoftware(jsonContent), parseReferenceGenome(jsonContent), parseCase(jsonContent), parseSample(jsonContent), parseVcfFiles(jsonContent))
    }

    MetadataContext getMetadataContext() {
        return metadataContext
    }

    static boolean parseIsSomatic(jsonContent) {
        return jsonContent.is_somatic
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

    static Case parseCase(jsonContent) {
        return new Case(jsonContent.case.identifier)
    }

    static Sample parseSample(jsonContent) {
        return new Sample(jsonContent.sample.identifier, jsonContent.sample.cancerEntity)
    }

    static List<String> parseVcfFiles(jsonContent) {
        return jsonContent.vcf_files
    }
}