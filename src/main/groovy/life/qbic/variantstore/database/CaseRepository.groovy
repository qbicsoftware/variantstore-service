package life.qbic.variantstore.database

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import life.qbic.variantstore.model.Case
import life.qbic.variantstore.model.TransactionStatus


@Repository("variantstore-postgres")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface CaseRepository extends CrudRepository<Case, Long> {

    Case findById(String id)

}