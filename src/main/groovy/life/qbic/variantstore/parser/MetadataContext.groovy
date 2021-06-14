package life.qbic.variantstore.parser

import life.qbic.variantstore.model.Annotation
import life.qbic.variantstore.model.Case
import life.qbic.variantstore.model.Project
import life.qbic.variantstore.model.ReferenceGenome
import life.qbic.variantstore.model.VariantCaller
import life.qbic.variantstore.model.Sample

/**
 * A DTO for metadata
 *
 * @since: 1.0.0
 */
class MetadataContext {

    /**
     * Indicates if the provided variants are somatic
     */
    final boolean isSomatic
    /**
     * The applied variant calling software
     */
    final VariantCaller variantCalling
    /**
     * The applied variant annotation software
     */
    final Annotation variantAnnotation
    /**
     * The reference genome
     */
    final ReferenceGenome referenceGenome
    /**
     * The associated case (patient)
     */
    final Case patient
    /**
     * The associated samples
     */
    final List<Sample> samples
    /**
     * The associated project
     */
    final Project project

    MetadataContext(boolean isSomatic, VariantCaller variantCalling, Annotation variantAnnotation, ReferenceGenome referenceGenome, Case patient, List<Sample> samples, Project project) {
        this.isSomatic = isSomatic
        this.variantCalling = variantCalling
        this.variantAnnotation = variantAnnotation
        this.referenceGenome = referenceGenome
        this.samples = samples
        this.patient = patient
        this.project = project
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

    List<Sample> getSamples() {
        return samples
    }

    Project getProject() {
        return project
    }
}
