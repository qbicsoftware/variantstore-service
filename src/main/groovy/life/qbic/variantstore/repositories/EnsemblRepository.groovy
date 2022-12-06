package life.qbic.variantstore.repositories

import io.micronaut.data.annotation.Join
import io.micronaut.data.annotation.NamingStrategy
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.naming.NamingStrategies
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import life.qbic.variantstore.model.Ensembl
import life.qbic.variantstore.model.ReferenceGenome


/**
 * The Ensembl repository
 *
 * @since: 1.1.0
 */
@Repository("variantstore-postgres")
@JdbcRepository(dialect = Dialect.POSTGRES)
@NamingStrategy(NamingStrategies.LowerCase.class)
interface EnsemblRepository extends CrudRepository<Ensembl, Long> {

    @Override
    Optional<Ensembl> findById(Long id)

    @Join(value = "genes", type = Join.Type.LEFT_FETCH, alias = "genes")
    Optional<Ensembl> find(Integer version, String date, ReferenceGenome referenceGenome)

    /**
     * Get the highest Ensembl version available in the Store
     * @return the highest version
     */
    @Query("SELECT MAX(version) FROM ensembl")
    Integer fetchMaxVersion()

}
