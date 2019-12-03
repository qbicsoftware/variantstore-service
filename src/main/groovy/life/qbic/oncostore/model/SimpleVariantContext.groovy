package life.qbic.oncostore.model

import life.qbic.oncostore.model.Consequence
import life.qbic.oncostore.model.ReferenceGenome

interface SimpleVariantContext {

    String getChromosome()

    BigInteger getStartPosition()

    BigInteger getEndPosition()

    String getReferenceAllele()

    String getObservedAllele()

    List<Consequence> getConsequences()

    ReferenceGenome getReferenceGenome()

    Boolean getIsSomatic()

    List<Object> getAttribute(String key)

    String getId()

    void setId(String id)

    void setIsSomatic(Boolean somatic)

    void setConsequences(List<Consequence> consequences)
}