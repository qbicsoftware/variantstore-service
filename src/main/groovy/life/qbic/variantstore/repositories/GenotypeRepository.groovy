package life.qbic.variantstore.repositories

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import life.qbic.variantstore.model.Genotype

/**
 *
 *
 * @since: 1.1.0
 */
@Repository("variantstore-postgres")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface GenotypeRepository extends CrudRepository<Genotype, Long>{

    @Override
    Optional<Genotype> findById(Long id)

    Optional<Genotype> find(String genotype, Integer readDepth, String filter, String likelihoods, String genotypeLikelihoods, String genotypeLikelihoodsHet,
                            String posteriorProbs, Integer genotypeQuality, String haplotypeQualities, String phaseSet, Integer phasingQuality,
                            List<Integer> alternateAlleleCounts, Integer mappingQuality)

    List<Genotype> list()

}
