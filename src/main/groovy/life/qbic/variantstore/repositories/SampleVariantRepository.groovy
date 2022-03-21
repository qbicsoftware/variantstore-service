package life.qbic.variantstore.repositories

import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.Join
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import life.qbic.variantstore.model.Genotype
import life.qbic.variantstore.model.Sample
import life.qbic.variantstore.model.SampleVariant
import life.qbic.variantstore.model.Variant
import life.qbic.variantstore.model.VcfInfo

/**
 * The SampleVariant repository to store associations (many-to-many) between Sample, Variant, Genotype, and VcfInfo
 *
 * @since: 1.1.0
 */
@Repository("variantstore-postgres")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface SampleVariantRepository extends CrudRepository<SampleVariant, Long> {

    @Override
    Optional<SampleVariant> findById(Long id)

    @NonNull
    @Override
    List<SampleVariant> findAll()

    /**
     * Retrieve all SampleVariant entities with Genotype information
     * @return List of SampleVariant objects
     */
    @Join(value = "genotype", type = Join.Type.FETCH)
    List<SampleVariant> list()

    Optional<SampleVariant> findBySampleAndVariantAndVcfinfoAndGenotype(Sample sample, Variant variant, VcfInfo vcfInfo,
                                                                        Genotype genotype)

}
