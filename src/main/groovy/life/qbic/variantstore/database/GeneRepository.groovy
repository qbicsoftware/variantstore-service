package life.qbic.variantstore.database

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import life.qbic.variantstore.model.Gene

@Repository("variantstore-postgres")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface GeneRepository extends CrudRepository<Gene, BigInteger>{

    Gene findById(String id)

}