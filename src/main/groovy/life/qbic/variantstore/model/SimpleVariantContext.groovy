package life.qbic.variantstore.model

/**
 * A variant object with additional information
 *
 * @since: 1.0.0
 */
interface SimpleVariantContext {

    String getChromosome()

    BigInteger getStartPosition()

    BigInteger getEndPosition()

    String getReferenceAllele()

    String getObservedAllele()

    Set<Consequence> getConsequences()

    List<Genotype> getGenotypes()

    Set<VariantCaller> getVariantCaller()

    Set<SampleVariant> getSampleVariants()

    Set<ReferenceGenome> getReferenceGenomes()

    String getDatabaseIdentifier()

    boolean isSomatic()

    String getIdentifier()

    VcfInfo getVcfInfo()

    void setIdentifier(String id)

    void setSomatic(boolean somatic)

    void setConsequences(Set<Consequence> consequences)
}