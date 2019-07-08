package life.qbic.oncostore.parser

import life.qbic.oncostore.model.Annotation
import life.qbic.oncostore.model.Case
import life.qbic.oncostore.model.ReferenceGenome
import life.qbic.oncostore.model.VariantCaller
import life.qbic.oncostore.model.Sample

class MetadataContext {

    final boolean isSomatic
    final VariantCaller variantCalling
    final Annotation variantAnnotation
    final ReferenceGenome referenceGenome
    final Case patient
    final Sample sample
    final List<String> vcfFiles

    MetadataContext(boolean isSomatic, VariantCaller variantCalling, Annotation variantAnnotation, ReferenceGenome referenceGenome, Case patient, Sample sample, List<String> vcfFiles) {
        this.isSomatic = isSomatic
        this.variantCalling = variantCalling
        this.variantAnnotation = variantAnnotation
        this.referenceGenome = referenceGenome
        this.sample = sample
        this.patient = patient
        this.vcfFiles = vcfFiles
    }

    boolean getIsSomatic() {
        return isSomatic
    }

    VariantCaller getVariantCalling() {
        return variantCalling
    }

    Annotation getVariantAnnotation() {
        return variantAnnotation
    }

    ReferenceGenome getReferenceGenome() {
        return referenceGenome
    }

    Case getCase() {
        return patient
    }

    Sample getSample() {
        return sample
    }

    List<String> getVcfFiles() {
        return vcfFiles
    }
}
