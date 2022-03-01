package life.qbic.variantstore.repositories

import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import life.qbic.variantstore.model.Ensembl


/**
 * A DTO representing the status of a transaction
 *
 * @since: 1.1.0
 */
@Repository("variantstore-postgres")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface EnsemblRepository extends CrudRepository<Ensembl, Long> {

    @Override
    Optional<Ensembl> findById(Long id)

    @Query("SELECT MAX(version) FROM ensembl")
    Integer fetchMaxVersion()

}
