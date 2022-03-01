package life.qbic.variantstore.repositories

import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.jdbc.runtime.JdbcOperations
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import life.qbic.variantstore.model.Sample
import jakarta.inject.*

/**
 *
 *
 * @since: 1.1.0
 */
@Repository("variantstore-postgres")
@JdbcRepository(dialect = Dialect.POSTGRES)
abstract class SampleRepository implements CrudRepository<Sample, Long> {

    @Inject
    @Named("variantstore-postgres")
    JdbcOperations jdbcOperations

    @NonNull
    @Override
    abstract List<Sample> findAll()

    abstract Optional<Sample> findById(Long id)

    @NonNull
    abstract List<Sample> findByIdentifier(String identifier)

    abstract List<Sample> findByCancerEntity(String cancerEntity)

    void insertMany(List<Sample> samples) {
        def sqlStatement = """INSERT INTO sample (identifier, cancerEntity, entity_id) VALUES (?, ?, ?) ON CONFLICT (identifier, cancerEntity) DO NOTHING RETURNING id"""
        jdbcOperations.prepareStatement(sqlStatement) { statement ->
            samples.each {
                statement.setString(1, it.identifier)
                statement.setString(2, it.cancerEntity)
                statement.setLong(3, it.entity.id)
                statement.addBatch()
            }
            statement.executeBatch()
        }
    }
}
