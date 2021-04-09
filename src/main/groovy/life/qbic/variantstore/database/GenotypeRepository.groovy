package life.qbic.variantstore.database

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import life.qbic.variantstore.model.Genotype

@Repository("variantstore-postgres")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface GenotypeRepository extends CrudRepository<Genotype, BigInteger>{

    Genotype findById(String id)

}