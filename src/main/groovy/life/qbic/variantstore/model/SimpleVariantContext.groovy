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

    List<Consequence> getConsequences()

    List<Genotype> getGenotypes()

    String getDatabaseIdentifier()

    Boolean getIsSomatic()

    String getIdentifier()

    VcfInfo getVcfInfo()

    void setIdentifier(String id)

    void setIsSomatic(Boolean somatic)

    void setConsequences(ArrayList consequences)
}