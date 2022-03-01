package life.qbic.variantstore.repositories

import io.micronaut.data.annotation.Join
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.annotation.Where
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.jdbc.runtime.JdbcOperations
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import life.qbic.variantstore.model.Gene
import jakarta.inject.*

/**
 *
 *
 * @since: 1.1.0
 */
@Repository("variantstore-postgres")
@JdbcRepository(dialect = Dialect.POSTGRES)
abstract class GeneRepository implements CrudRepository<Gene, Long>{

    @Inject
    @Named("variantstore-postgres")
    JdbcOperations jdbcOperations

    abstract Set<Gene> findAll()

    abstract Optional<Gene> findById(Long id)

    abstract Optional<Gene> findByGeneId(String geneId)

    abstract Optional<Gene> find(String bioType, String chromosome, String symbol, String name, BigInteger geneStart, BigInteger geneEnd,
                                 String geneId, String description, String strand, Integer version, List<String> synonyms)

    void insertMany(List<Gene> genes) {
        def sqlStatement = """WITH maybe_new AS (INSERT INTO gene (bioType, chromosome, symbol, name, genestart, geneend, geneid, description, strand, version, synonyms) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING"""
        jdbcOperations.prepareStatement(sqlStatement) { statement ->
            genes.each {
                statement.setString(1, it.bioType)
                statement.setString(2, it.chromosome)
                statement.setString(3, it.symbol)
                statement.setString(4, it.name)
                statement.setBigDecimal(5, it.geneStart as BigDecimal)
                statement.setBigDecimal(6, it.geneEnd as BigDecimal)
                statement.setString(7, it.geneId)
                statement.setString(8, it.description)
                statement.setString(9, it.strand)
                statement.setInt(10, it.version)
                statement.setString(11, it.synonyms)
                statement.addBatch()
            }
            statement.executeBatch()
        }
    }

    @Join(value = "ensembles", type = Join.Type.INNER, alias = "ensembl")
    @Where("ensembl.version = :ensemblVersion")
    abstract Set<Gene> searchByGeneId(String geneId, int ensemblVersion)


    @Query("SELECT gene.* FROM gene INNER JOIN gene_consequence ON gene.id = gene_consequence.gene_id INNER JOIN consequence on gene_consequence.consequence_id = consequence.id INNER JOIN variant_consequence on variant_consequence.consequence_id = consequence.id INNER JOIN variant ON variant_consequence.variant_id = variant.id INNER JOIN sample_variant ON sample_variant.variant_id = variant.id INNER JOIN sample ON sample_variant.sample_id = sample.id WHERE sample.identifier= :sampleIdentifier")
    abstract Set<Gene> getForSampleIdentifier(String sampleIdentifier)
}
