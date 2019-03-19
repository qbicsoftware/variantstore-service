package life.qbic.oncostore.oncoloader

import life.qbic.oncostore.DataBase
import life.qbic.oncostore.model.Consequence
import life.qbic.oncostore.model.Gene
import life.qbic.oncostore.model.ReferenceGenome
import life.qbic.oncostore.model.SimpleVariantContext

interface Loader {

    void load()

    List<Integer> insertJunction(Object id, List ids, String searchStatement, String insertStatement, DataBase db)

    String insertVariant(SimpleVariantContext variant, String id, Boolean isSomatic, DataBase db)

    String insertGene(Gene gene, DataBase db)

    String insertSample(String id, DataBase db)

    Integer insertConsequence(Consequence cons, DataBase db)

    Integer insertSoftware(Object software, DataBase db)

    Integer insertReferenceGenome(ReferenceGenome genome, DataBase db)
}