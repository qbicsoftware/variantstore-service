package life.qbic.variantstore.database


import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import io.reactivex.annotations.NonNull
import life.qbic.variantstore.model.Sample

@Repository("variantstore-postgres")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface SampleRepository extends CrudRepository<Sample, Long> {

    @NonNull
    @Override
    List<Sample> findAll()

    @NonNull
    Sample findByIdentifier(String identifier)

}
