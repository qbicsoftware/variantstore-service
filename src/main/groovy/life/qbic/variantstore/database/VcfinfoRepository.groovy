package life.qbic.variantstore.database

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import life.qbic.variantstore.model.VcfInfo

@Repository("variantstore-postgres")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface VcfinfoRepository extends CrudRepository<VcfInfo, Long>{

    VcfInfo findById(String id)

}