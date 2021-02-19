package life.qbic.oncostore.model

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

    String getDatabaseId()

    Boolean getIsSomatic()

    String getId()

    VcfInfo getVcfInfo()

    void setId(String id)

    void setIsSomatic(Boolean somatic)

    void setConsequences(ArrayList consequences)
}