package life.qbic.variantstore.database

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import io.reactivex.annotations.NonNull
import life.qbic.variantstore.model.ReferenceGenome

@Repository("variantstore-postgres")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface ReferenceGenomeRepository extends CrudRepository<ReferenceGenome, Long> {

    @NonNull
    @Override
    List<ReferenceGenome> findAll()

    List<ReferenceGenome> findByBuild(String build)

}