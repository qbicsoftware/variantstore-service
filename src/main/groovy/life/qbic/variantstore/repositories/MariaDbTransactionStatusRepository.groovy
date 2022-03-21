package life.qbic.variantstore.repositories

import io.micronaut.context.annotation.Replaces
import io.micronaut.context.annotation.Requires
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect

/**
 * A MariaDB-based repository holding transaction status information
 *
 * @since: 1.1.0
 */
@Requires(property = "database.specifier", value = "variantstore-mariadb")
@Replaces(TransactionStatusRepository.class)
@Repository('transactions-mariadb')
@JdbcRepository(dialect = Dialect.MYSQL)
interface MariaDbTransactionStatusRepository extends TransactionStatusRepository {

}
