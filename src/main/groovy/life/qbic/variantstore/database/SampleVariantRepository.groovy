package life.qbic.variantstore.database

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import life.qbic.variantstore.model.SampleVariant

@Repository("variantstore-postgres")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface SampleVariantRepository extends CrudRepository<SampleVariant, Long> {

    SampleVariant findById(String id)

}