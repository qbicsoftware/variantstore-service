package life.qbic.variantstore.repositories

import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import life.qbic.variantstore.model.VariantCaller

/**
 * The VariantCaller repository
 *
 * @since: 1.1.0
 */
@Repository("variantstore-postgres")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface VariantCallerRepository extends CrudRepository<VariantCaller, Long> {

    Optional<VariantCaller> findById(Long id)

    List<VariantCaller> list()

    Optional<VariantCaller> find(String name, String version, String doi)

    /**
     * Insert variant calling software to database
     * @param variantCaller the variant calling software to insert
     */
    @Query("INSERT INTO variantcaller(name, version, doi) VALUES (:name, :version, :doi) ON CONFLICT DO NOTHING")
    void insertIgnore(VariantCaller variantCaller)

}
