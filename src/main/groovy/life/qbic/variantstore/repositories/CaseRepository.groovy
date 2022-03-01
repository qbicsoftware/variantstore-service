package life.qbic.variantstore.repositories

import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.Join
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import life.qbic.variantstore.model.Case

/**
 *
 *
 * @since: 1.1.0
 */
@Repository("variantstore-postgres")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface CaseRepository extends CrudRepository<Case, Long> {

    @NonNull
    @Override
    List<Case> findAll()

    Optional<Case> findById(Long id)

    List<Case> list()

    List<Case> findByIdentifier(String identifier)

    @Query("""select distinct entity.id, project_id from entity INNER JOIN sample ON entity
             .id = sample.entity_id INNER JOIN sample_variant ON sample.id = sample_variant.sample_id INNER
             JOIN variant ON variant.id = sample_variant.variant_id INNER JOIN variant_consequence ON
             variant_consequence.variant_id = variant.id INNER JOIN consequence on variant_consequence
             .consequence_id = consequence.id WHERE consequence.type=:consequenceType AND consequence
             .genesymbol=:geneSymbol""")
    @Join(value = "sample", type = Join.Type.INNER)
    @Join(value = "sample_variant", type = Join.Type.INNER)
    @Join(value = "variant", type = Join.Type.INNER)
    @Join(value = "variant_consequence", type = Join.Type.INNER)
    @Join(value = "consequence", type = Join.Type.INNER)
    List<Case> searchByGeneSymbolAndConsequenceType(String geneSymbol, String consequenceType)


    @Query("""select distinct entity.id, project_id from entity INNER JOIN sample ON entity.id = sample.entity_id 
INNER JOIN sample_variant ON sample.id = sample_variant.sample_id INNER JOIN variant ON variant.id = 
sample_variant.variant_id INNER JOIN variant_consequence ON variant_consequence.variant_id = variant.id 
INNER JOIN consequence on variant_consequence.consequence_id = consequence.id WHERE consequence.genesymbol= 
:geneSymbol""")
    @Join(value = "sample", type = Join.Type.INNER)
    @Join(value = "sample_variant", type = Join.Type.INNER)
    @Join(value = "variant", type = Join.Type.INNER)
    @Join(value = "variant_consequence", type = Join.Type.INNER)
    @Join(value = "consequence", type = Join.Type.INNER)
    List<Case> searchByGeneSymbol(String geneSymbol)

    @Query("""select distinct entity.id, project_id from entity INNER JOIN sample ON entity.id = 
sample.entity_id INNER JOIN sample_variant ON sample.id = sample_variant.sample_id INNER JOIN
 variant ON variant.id = sample_variant.variant_id where variant.chr= :chromosome AND variant.start>= :startPosition AND variant.end<= :endPosition""")
    @Join(value = "sample", type = Join.Type.INNER)
    @Join(value = "sample_variant", type = Join.Type.INNER)
    @Join(value = "variant", type = Join.Type.INNER)
    List<Case> searchByChromosomeAndStartPositionRange(String chromosome, BigInteger startPosition, BigInteger endPosition)

    @Query("""select distinct entity.id, project_id, type from entity INNER JOIN sample ON entity.id = sample.entity_id INNER JOIN sample_variant ON sample.id = sample_variant.sample_id INNER JOIN variant ON variant.id = sample_variant.variant_id INNER JOIN variant_consequence ON variant_consequence.variant_id = variant.id INNER JOIN consequence on variant_consequence.consequence_id = consequence.id where type= :consequenceType""")
    @Join(value = "sample", type = Join.Type.INNER)
    @Join(value = "sample_variant", type = Join.Type.INNER)
    @Join(value = "variant", type = Join.Type.INNER)
    @Join(value = "variant_consequence", type = Join.Type.INNER)
    @Join(value = "consequence", type = Join.Type.INNER)
    List<Case> searchByConsequenceType(String consequenceType)

    @Query("""select distinct * from entity INNER JOIN sample ON entity.id =
    sample.entity_id INNER JOIN sample_variant ON sample.id = sample_variant.sample_id INNER JOIN
            variant ON variant.id = sample_variant.variant_id where variant.chr= :chromosome""")
    @Join(value = "sample", type = Join.Type.INNER)
    @Join(value = "sample_variant", type = Join.Type.INNER)
    @Join(value = "variant", type = Join.Type.INNER)
    List<Case> searchByChromosome(String chromosome)

    @Query("INSERT INTO entity(identifier, project_id) VALUES (:identifier, :project) ON CONFLICT DO NOTHING")
    void insertIgnore(Case entity)

}
