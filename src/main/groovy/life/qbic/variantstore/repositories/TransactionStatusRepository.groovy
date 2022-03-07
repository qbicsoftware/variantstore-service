package life.qbic.variantstore.repositories

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import life.qbic.variantstore.model.TransactionStatus


/**
 * A repository holding transcation status information
 *
 * @since: 1.0.0
 */
@Repository('transactions')
@JdbcRepository(dialect = Dialect.POSTGRES)
interface TransactionStatusRepository extends CrudRepository<TransactionStatus, Long> {

    @Override
    List<TransactionStatus> findAll()

    Optional<TransactionStatus> findByIdentifier(String identifier)

    void updateStatus(@Id Integer id, String status);
}
