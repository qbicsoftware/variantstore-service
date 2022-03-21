package life.qbic.variantstore.repositories

import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import io.micronaut.core.annotation.NonNull
import life.qbic.variantstore.model.ReferenceGenome

/**
 * The ReferenceGenome repository
 *
 * @since: 1.1.0
 */
@Repository("variantstore-postgres")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface ReferenceGenomeRepository extends CrudRepository<ReferenceGenome, Long> {


    @Override
    Optional<ReferenceGenome> findById(Long id)

    @NonNull
    @Override
    List<ReferenceGenome> findAll()

    List<ReferenceGenome> list()

    Optional<ReferenceGenome> find(String source, String build, String version)

    /**
     * Insert reference genome to the database and ignore conflicts
     * @param referenceGenome the reference genome to insert
     */
    @Query("INSERT INTO referencegenome(source, build, version) VALUES (:source, :build, :version) ON CONFLICT DO NOTHING")
    void insertIgnore(ReferenceGenome referenceGenome)

}
