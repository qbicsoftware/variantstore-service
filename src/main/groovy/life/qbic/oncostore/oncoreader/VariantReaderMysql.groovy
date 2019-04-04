package life.qbic.oncostore.oncoreader

import life.qbic.oncostore.DataBase
import life.qbic.oncostore.model.Consequence
import life.qbic.oncostore.model.Variant
import life.qbic.oncostore.util.ListingArguments

import javax.inject.Inject
import javax.validation.constraints.NotNull
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class VariantReaderMysql implements VariantReader{

    private final DataBase dataBase

    String searchAllVariants = "select * from Variant INNER JOIN Variant_has_Consequence ON Variant.id = Variant_has_Consequence.Variant_id INNER JOIN Consequence on Variant_has_Consequence.Consequence_id = Consequence.id ;"
    String searchVariantByChromosome = "where Variant.chr=?;"
    String searchVariantByPosition = "where Variant.start=?;"
    String searchVariantById = "where Variant.id=?;"
    String searchVariantByRef = "where Variant.ref=?;"
    String searchVariantByObs = "where Variant.obs=?;"
    String searchVariantBeacon = "select * from Variant INNER JOIN Variant_has_ReferenceGenome ON Variant.id = Variant_has_ReferenceGenome.Variant_id INNER JOIN ReferenceGenome on Variant_has_ReferenceGenome.ReferenceGenome_id = ReferenceGenome.id where ReferenceGenome.build=? and Variant.chr=? and Variant.start=? and Variant.ref=? and Variant.obs=?;"



    @Inject
    VariantReaderMysql(DataBase dataBase) {
        this.dataBase = dataBase
    }


    @Override
    List<Variant> searchVariants(@NotNull ListingArguments args) {
        if (args.getChromosome().isPresent()) {
            return findAllByChromosome(searchAllVariants.replace(";", searchVariantByChromosome), args.getChromosome().get())
        }

        if (!args.getChromosome().isPresent() && args.getStartPosition().isPresent()) {
            return findAllByStartPosition(searchAllVariants.replace(";", searchVariantByPosition), args.getStartPosition().get())
        }

        if (args.getSampleId().isPresent()) {

        }

        return findAll(searchAllVariants)
    }

/**
     * Retrieve all variants in the store
     * @return List<Variant>
     */
    @Override
    public List<Variant> findAll(String sqlStatement) {
        def variants = []
        def consequences = [:].withDefault{key -> return []}

        try {
            dataBase.connection.prepareStatement(sqlStatement).withCloseable { PreparedStatement statement ->
                statement.executeQuery().withCloseable { ResultSet rs ->
                    while(rs.next()) {
                        def var = createVariant(rs)
                        def consequence = createConsequence(rs)

                        consequences.get(var.getIdentifier()).add(consequence)
                        variants.add(var)
                    }
                }
            }
        }

        catch (SQLException e) {
            e.printStackTrace()
        }

        variants.unique().each { Variant v ->
            v.setConsequences(consequences.get(v.getIdentifier()))
        }

        return variants
    }

    @Override
    public List<Variant> findAllByChromosome(String sqlStatement, String chromosome) {
        def searchVariant = dataBase.connection.prepareStatement(sqlStatement)
        searchVariant.setString(1, chromosome)

        return findAllFiltered(searchVariant)
    }

    @Override
    public List<Variant> findAllByStartPosition(String sqlStatement, BigInteger startPosition) {
        def searchVariant = dataBase.connection.prepareStatement(sqlStatement)
        searchVariant.setLong(1, Long.parseLong(String.valueOf(startPosition)))

        return findAllFiltered(searchVariant)
    }

    public List<Variant> findAllFiltered(PreparedStatement sqlStatement) {
        def variants = []
        def consequences = [:].withDefault{key -> return []}

        try {
            sqlStatement.withCloseable { PreparedStatement statement ->
                statement.executeQuery().withCloseable { ResultSet rs ->
                    while(rs.next()) {
                        def var = createVariant(rs)
                        def consequence = createConsequence(rs)

                        consequences.get(var.getIdentifier()).add(consequence)
                        variants.add(var)
                    }
                }
            }
        }

        catch (SQLException e) {
            e.printStackTrace()
        }

        variants.unique().each { Variant v ->
            v.setConsequences(consequences.get(v.getIdentifier()))
        }

        return variants
    }

    public List<Variant> findForBeacon(PreparedStatement sqlStatement) {
        def variants = []
        def consequences = [:].withDefault{key -> return []}

        try {
            sqlStatement.withCloseable { PreparedStatement statement ->
                statement.executeQuery().withCloseable { ResultSet rs ->
                    while(rs.next()) {
                        def var = createVariant(rs)
                        variants.add(var)
                    }
                }
            }
        }

        catch (SQLException e) {
            e.printStackTrace()
        }

        variants.unique().each { Variant v ->
            v.setConsequences(consequences.get(v.getIdentifier()))
        }

        return variants
    }

    /**
     * Search in store for a variant with specific UUID
     * @param identifier
     * @return Variant
     */
    @Override
    public Variant searchVariant(String identifier) {
        def var = null
        def consequences = [:].withDefault{key -> return []}
        String searchStatement = searchAllVariants.replace(";", searchVariantById)

        try {
            dataBase.connection.prepareStatement(searchStatement).withCloseable { PreparedStatement statement ->
                statement.setString(1, identifier)
                statement.executeQuery().withCloseable { ResultSet rs ->
                    while (rs.next()) {
                        var = createVariant(rs)
                        def consequence = createConsequence(rs)

                        consequences.get(var.getIdentifier()).add(consequence)
                    }
                }
            }
        }

        catch (SQLException e) {
            e.printStackTrace()
        }

        if (var != null) {
            var.setConsequences(consequences.get(var.getIdentifier()))
        }

        return var
    }

    Variant createVariant(ResultSet rs) {
        def var = new Variant()

        String identifier = rs.getString("Variant_id")
        Integer chromosome = rs.getInt("chr")
        BigInteger start = rs.getInt("start")
        BigInteger end = rs.getInt("end")
        String ref = rs.getString("ref")
        String obs = rs.getString("obs")
        Boolean isSomatic = rs.getBoolean("isSomatic")

        var.setIdentifier(identifier)
        var.setChromosome(chromosome)
        var.setStartPosition(start)
        var.setEndPosition(end)
        var.setReferenceAllele(ref)
        var.setObservedAllele(obs)
        var.setIsSomatic(isSomatic)

        return var
    }

    Consequence createConsequence(ResultSet rs) {
        def consequence = new Consequence()

        consequence.setCodingChange(rs.getString("codingChange"))
        consequence.setTranscriptID(rs.getString("transcriptID"))
        consequence.setTranscriptVersion(rs.getInt("transcriptVersion"))
        consequence.setRefSeqID(rs.getString("refSeqID"))
        consequence.setConsequenceType(rs.getString("type"))
        consequence.setBioType(rs.getString("bioType"))
        consequence.setCanonical(rs.getBoolean("canonical"))
        consequence.setAaChange(rs.getString("aaChange"))
        consequence.setAaStart(rs.getInt("aaStart"))
        consequence.setAaEnd(rs.getInt("aaEnd"))
        consequence.setImpact(rs.getString("impact"))
        consequence.setGeneID(rs.getString("Gene_id"))
        consequence.setStrand(rs.getInt("strand"))

        return consequence
    }

    @Override
    List<Variant> searchVariantForBeaconReponse(String chromosome, BigInteger startPosition,
                                               String reference, String observed, String assemblyId, ListingArguments args) {
        def searchVariant = dataBase.connection.prepareStatement(searchVariantBeacon)
        searchVariant.setString(1, assemblyId)
        searchVariant.setString(2, chromosome)
        searchVariant.setLong(3, Long.parseLong(String.valueOf(startPosition)))
        searchVariant.setString(4, reference)
        searchVariant.setString(5, observed)

        return findForBeacon(searchVariant)
    }
}
