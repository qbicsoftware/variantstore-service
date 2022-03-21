package life.qbic.variantstore.repositories

import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.NamingStrategy
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.naming.NamingStrategies
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import life.qbic.variantstore.model.Project

/**
 * The Project repository
 *
 * @since: 1.1.0
 */
@Repository("variantstore-postgres")
@JdbcRepository(dialect = Dialect.POSTGRES)
@NamingStrategy(NamingStrategies.LowerCase.class)
interface ProjectRepository extends CrudRepository<Project, Long> {

    @NonNull
    @Override
    List<Project> findAll()

    @Override
    Optional<Project> findById(Long id)

    Optional<Project> findByIdentifier(String identifier)

    List<Project> list()

    /**
     * Insert project to database with ignoring conflicts
     * @param project the project to insert
     */
    @Query("INSERT INTO PROJECT(identifier) VALUES (identifier) ON CONFLICT DO NOTHING")
    void insertIgnore(Project project)

}
