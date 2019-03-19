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

    Boolean isSomatic()

    String getAttribute(String key)

}