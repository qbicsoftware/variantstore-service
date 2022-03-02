package life.qbic.variantstore.repositories

import io.micronaut.data.annotation.Join
import life.qbic.variantstore.model.Consequence
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.jdbc.runtime.JdbcOperations
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import jakarta.inject.*

/**
 *
 *
 * @since: 1.1.0
 */
@Repository("variantstore-postgres")
@JdbcRepository(dialect = Dialect.POSTGRES)
abstract class ConsequenceRepository implements CrudRepository<Consequence, Long>{

    @Inject
    @Named("variantstore-postgres")
    JdbcOperations jdbcOperations

    @Override
    abstract Optional<Consequence> findById(Long id)

    abstract List<Consequence> saveAll(Set<Consequence> consequences)

    @Join(value = "genes", type = Join.Type.LEFT_FETCH)
    abstract List<Consequence> list()

    abstract Optional<Consequence> find(String allele, String codingChange, String transcriptId, Integer transcriptVersion,
                                        String type, String bioType, boolean canonical, String aaChange, String cdnaPosition,
                                        String cdsPosition, String proteinPosition, Integer proteinLength, Integer cdnaLength,
                                        Integer cdsLength, String impact, String exon, String intron, Integer strand,
                                        String geneSymbol, String featureType, Integer distance, String warnings)

    @Join(value = "annotations", type = Join.Type.LEFT_FETCH)
    abstract Optional<Consequence> retrieve(String allele, String codingChange, String transcriptId, Integer transcriptVersion,
                                            String type, String bioType, boolean canonical, String aaChange, String cdnaPosition,
                                            String cdsPosition, String proteinPosition, Integer proteinLength, Integer cdnaLength,
                                            Integer cdsLength, String impact, String exon, String intron, Integer strand,
                                            String geneSymbol, String featureType, Integer distance, String warnings)

    void insertMany(List<Consequence> consequences) {
        def sqlStatement = """INSERT INTO consequence (allele, codingchange, transcriptid, 
transcriptversion, type, biotype, canonical, aachange, cdnaposition, cdsposition, proteinposition, proteinlength, 
cdnalength, cdslength, impact, exon, intron, strand, genesymbol, featuretype, distance, warnings) VALUES (?,
 ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING"""
        jdbcOperations.prepareStatement(sqlStatement,  { statement ->
            consequences.each {
                statement.setString(1, it.allele)
                statement.setString(2, it.codingChange)
                statement.setString(3, it.transcriptId)
                statement.setInt(4, it.transcriptVersion)
                statement.setString(5, it.type)
                statement.setString(6, it.bioType)
                statement.setBoolean(7, it.canonical)
                statement.setString(8, it.aaChange)
                statement.setString(9, it.cdnaPosition)
                statement.setString(10, it.cdsPosition)
                statement.setString(11, it.proteinPosition)
                statement.setInt(12, it.proteinLength)
                statement.setInt(13, it.cdnaLength)
                statement.setInt(14, it.cdsLength)
                statement.setString(15, it.impact)
                statement.setString(16, it.exon)
                statement.setString(17, it.intron)
                statement.setInt(18, it.strand)
                statement.setString(19, it.geneSymbol)
                statement.setString(20, it.featureType)
                statement.setInt(21, it.distance)
                statement.setString(22, it.warnings)
                statement.addBatch()
            }
            statement.executeBatch()
        })
    }

}
