package life.qbic.variantstore.repositories

import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.Join
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.jdbc.runtime.JdbcOperations
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import life.qbic.variantstore.model.Annotation
import jakarta.inject.Inject

/**
 * The VariantAnnotation repository
 *
 * @since: 1.1.0
 */
@Repository("variantstore-postgres")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface VariantAnnotationRepository extends CrudRepository<Annotation, Long> {

    @Inject
    JdbcOperations jdbcOperations

    @NonNull
    @Override
    List<Annotation> findAll()

    Optional<Annotation> find(String name, String version, String doi)

    /**
     * Retrieve all Annotation objects wth Consequence associations
     * @return A list of Annotation  objects
     */
    @Join(value = "consequences", type = Join.Type.FETCH)
    List<Annotation> list()

    /**
     * Insert annotation software object to database while ignoring conflicts
     * @param annotation the annotation software object to insert
     */
    @Query("INSERT INTO annotationsoftware(name, version, doi) VALUES (:name, :version, :doi) ON CONFLICT DO NOTHING")
    void insertIgnore(Annotation annotation)

}
