package life.qbic.variantstore.database

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import io.reactivex.annotations.NonNull
import life.qbic.variantstore.model.Variant

//@Repository("variantstore-postgres")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface VariantRepository extends CrudRepository<Variant, Long> {


    @NonNull
    @Override
    List<Variant> findAll()

    List<Variant> findByIdentifier(String identifier)

}