package life.qbic.oncostore.parser

import life.qbic.oncostore.model.Annotation
import life.qbic.oncostore.model.ReferenceGenome
import life.qbic.oncostore.model.VariantCaller

class MetadataContext {

    final boolean isSomatic
    final VariantCaller variantCalling
    final Annotation variantAnnotation
    final ReferenceGenome referenceGenome
    final String sampleID
    final List<String> vcfFiles

    MetadataContext(boolean isSomatic, VariantCaller variantCalling, Annotation variantAnnotation, ReferenceGenome referenceGenome, String sampleID, List<String> vcfFiles) {
        this.isSomatic = isSomatic
        this.variantCalling = variantCalling
        this.variantAnnotation = variantAnnotation
        this.referenceGenome = referenceGenome
        this.sampleID = sampleID
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

    String getSampleID() {
        return sampleID
    }

    List<String> getVcfFiles() {
        return vcfFiles
    }
}
