package life.qbic.oncostore.parser

import groovy.json.JsonSlurper
import life.qbic.oncostore.model.Annotation
import life.qbic.oncostore.model.Case
import life.qbic.oncostore.model.ReferenceGenome
import life.qbic.oncostore.model.VariantCaller
import life.qbic.oncostore.model.Sample
import life.qbic.oncostore.util.AnnotationHandler


class MetadataReader {

    final MetadataContext metadataContext

    MetadataReader(File file) {
        def slurper = new JsonSlurper()
        def jsonContent = slurper.parse(file)
        this.metadataContext = new MetadataContext(parseIsSomatic(jsonContent), parseCallingSoftware(jsonContent), parseAnnotationSoftware(jsonContent), parseReferenceGenome(jsonContent), parseCase(jsonContent), parseSample(jsonContent))
    }

    MetadataReader(String content) {
        def slurper = new JsonSlurper()
        def jsonContent = slurper.parseText(content)
        this.metadataContext = new MetadataContext(parseIsSomatic(jsonContent), parseCallingSoftware(jsonContent), parseAnnotationSoftware(jsonContent), parseReferenceGenome(jsonContent), parseCase(jsonContent), parseSample(jsonContent))
    }

    MetadataContext getMetadataContext() {
        return metadataContext
    }

    static boolean parseIsSomatic(jsonContent) {
        return jsonContent.is_somatic
    }

    static Annotation parseAnnotationSoftware(jsonContent) {
        def name = jsonContent.variant_annotation.name as String
        def version = jsonContent.variant_annotation.version
        def doi = jsonContent.variant_annotation.doi

        assert name.toUpperCase() in AnnotationHandler.AnnotationTools.values().collect{value -> value.name()}
        return new Annotation(name, version, doi)
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
        def cancerEntity = jsonContent.sample.cancerEntity ?: ''
        return new Sample(jsonContent.sample.identifier, cancerEntity)
    }
}