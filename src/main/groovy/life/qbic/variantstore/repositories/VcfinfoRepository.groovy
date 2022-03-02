package life.qbic.variantstore.repositories

import io.micronaut.core.annotation.Nullable
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import life.qbic.variantstore.model.VcfInfo

/**
 *
 *
 * @since: 1.1.0
 */
@Repository("variantstore-postgres")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface VcfinfoRepository extends CrudRepository<VcfInfo, Long>{

    @Override
    Optional<VcfInfo> findById(Long id)

    Optional<VcfInfo> find(@Nullable String ancestralAllele, List<Integer> alleleCount, List<Float> alleleFrequency,
                           Integer numberAlleles, Integer baseQuality, @Nullable String cigar, boolean dbSnp,
                           boolean hapmapTwo, boolean hapmapThree, boolean thousandGenomes, Integer combinedDepth,
                           Integer endPos, Float rms, Integer mqZero, Integer strandBias, Integer numberSamples,
                           boolean somatic, boolean validated)

    Optional<VcfInfo> search(Float rms)

    Optional<VcfInfo> findByRms(float rms)

}
