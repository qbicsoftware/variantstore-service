package life.qbic.variantstore.repositories

import io.micronaut.data.annotation.Join
import io.micronaut.data.annotation.NamingStrategy
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.annotation.Where
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.naming.NamingStrategies
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import life.qbic.variantstore.model.Variant

/**
 * The Variant repository
 *
 * @since: 1.1.0
 */
@Repository("variantstore-postgres")
@JdbcRepository(dialect = Dialect.POSTGRES)
@NamingStrategy(NamingStrategies.LowerCase.class)
interface VariantRepository extends CrudRepository<Variant, Long> {

    @Override
    List<Variant> findAll()

    Set<Variant> findByIdentifier(String identifier)

    List<Variant> list()

    /**
     * Find variant based on properties and return with consequence associations
     * @param chromosome the chromosome
     * @param startPosition the genomic start position
     * @param databaseIdentifier the database identifier
     * @param endPosition the genomic end position
     * @param referenceAllele the reference allele
     * @param observedAllele the observed allele
     * @param somatic information whether the variant is somatic
     * @return the found variant or empty Optional
     */
    @Join(value = "consequences", type = Join.Type.LEFT_FETCH, alias = "consequence")
    Optional<Variant> find(String chromosome, BigInteger startPosition, String databaseIdentifier,
                           BigInteger endPosition, String referenceAllele, String observedAllele, boolean somatic)

    /**
     * Find variant based on properties for Beacon request
     * @param chromosome the chromosome
     * @param startPosition the genomic start position
     * @param referenceAllele the reference allele
     * @param observedAllele the observed allele
     * @param build the reference genome
     * @return a set of found variants
     */
    @Query("SELECT * FROM variant WHERE chr = :chromosome and start = :startPosition and ref LIKE :referenceAllele and obs LIKE :observedAllele")
    Set<Variant> findForBeacon(String chromosome, BigInteger startPosition, String referenceAllele,
                                String observedAllele, String build)

    /**
     * Search variant based on properties and return with reference genome associations
     * @param databaseIdentifier the database identifier
     * @param chromosome the chromosome
     * @param startPosition the genomic start position
     * @param endPosition the genomic end position
     * @param referenceAllele the reference allele
     * @param observedAllele the observerd allele
     * @param somatic information whether variant is somatic
     * @return the found Variant or empty Optional
     */
    @Join(value = "referenceGenomes", type = Join.Type.LEFT_FETCH, alias = "referencegenome")
    Optional<Variant> search(String databaseIdentifier, String chromosome, BigInteger startPosition,
                             BigInteger endPosition, String referenceAllele, String observedAllele, boolean somatic)

    // withConsequences and genotypes and vcfinfo
    @Query("SELECT variant.*, consequence.*, vcfinfo.*, genotype.* FROM variant INNER JOIN sample_variant ON variant.id = sample_variant.variant_id INNER JOIN vcfinfo ON vcfinfo.id=sample_variant.vcfinfo_id INNER JOIN referencegenome_variant ON variant.id=referencegenome_variant.variant_id INNER JOIN referencegenome ON referencegenome.id=referencegenome_variant.referencegenome_id INNER JOIN variant_consequence ON variant.id = variant_consequence.variant_id INNER JOIN consequence on variant_consequence.consequence_id = consequence.id INNER JOIN annotationsoftware_consequence ON annotationsoftware_consequence.consequence_id= consequence.id INNER JOIN annotationsoftware ON annotationsoftware.id = annotationsoftware_consequence.annotationsoftware_id INNER JOIN genotype ON genotype.id=sample_variant.genotype_id INNER JOIN sample ON sample_variant.sample_id = sample.id WHERE referencegenome.build = :referenceGenome AND annotationsoftware.name = :annotationSoftware")
    List<Variant> getAllWithConsequencesAndGenos(String annotationSoftware, String referenceGenome)

    //withConsequences and vcfInfo
    @Query("SELECT variant.*, consequence.*, vcfinfo.* FROM variant INNER JOIN sample_variant ON variant.id = sample_variant.variant_id INNER JOIN vcfinfo ON vcfinfo.id=sample_variant.vcfinfo_id INNER JOIN referencegenome_variant ON variant.id=referencegenome_variant.variant_id INNER JOIN referencegenome ON referencegenome.id=referencegenome_variant.referencegenome_id INNER JOIN variant_consequence ON variant.id = variant_consequence.variant_id INNER JOIN consequence on variant_consequence.consequence_id = consequence.id INNER JOIN annotationsoftware_consequence ON annotationsoftware_consequence.consequence_id= consequence.id INNER JOIN annotationsoftware ON annotationsoftware.id = annotationsoftware_consequence.annotationsoftware_id WHERE referencegenome.build = :referenceGenome AND annotationsoftware.name = :annotationSoftware")
    List<Variant> getAllWithConsequencesAndVcfInfo(String annotationSoftware, String referenceGenome)

    // withConsequences and no vcfinfo
    @Query("SELECT variant.*, consequence.* FROM variant INNER JOIN sample_variant ON variant.id = sample_variant.variant_id INNER JOIN referencegenome_variant ON variant.id=referencegenome_variant.variant_id INNER JOIN referencegenome ON referencegenome.id=referencegenome_variant.referencegenome_id INNER JOIN variant_consequence ON variant.id = variant_consequence.variant_id INNER JOIN consequence on variant_consequence.consequence_id = consequence.id INNER JOIN annotationsoftware_consequence ON annotationsoftware_consequence.consequence_id= consequence.id INNER JOIN annotationsoftware ON annotationsoftware.id = annotationsoftware_consequence.annotationsoftware_id WHERE referencegenome.build = :referenceGenome AND annotationsoftware.name = :annotationSoftware")
    List<Variant> getAllWithConsequencesAndNoVcfInfo(String annotationSoftware, String referenceGenome)

    // withVcInfo
    @Query("SELECT variant.*, vcfinfo.* FROM variant INNER JOIN sample_variant ON variant.id = sample_variant.variant_id INNER JOIN vcfinfo ON vcfinfo.id=sample_variant.vcfinfo_id INNER JOIN referencegenome_variant ON variant.id=referencegenome_variant.variant_id INNER JOIN referencegenome ON referencegenome.id=referencegenome_variant.referencegenome_id WHERE referencegenome.build = :referenceGenome")
    List<Variant> getAllWithVcfInfo(String referenceGenome)

    // only based on refGenome
    @Join(value = "referenceGenomes", type = Join.Type.INNER, alias = "referencegenome")
    @Where("referencegenome.build = :referenceGenome")
    List<Variant> findByChromosomeAndStartPosition(String chromosome, BigInteger startPosition, String referenceGenome)

    // withConsequences and genotypes and vcfinfo
    @Join(value = "sampleVariants", type = Join.Type.LEFT_FETCH)
    @Join(value = "sampleVariants.vcfinfo", type = Join.Type.LEFT_FETCH)
    @Join(value = "sampleVariants.genotype", type = Join.Type.LEFT_FETCH)
    @Join(value = "referenceGenomes", type = Join.Type.INNER, alias = "referencegenome")
    @Where("referencegenome.build = :referenceGenome")
    @Join(value = "consequences", type = Join.Type.INNER, alias = "consequence")
    @Join(value = "consequences.annotations", type = Join.Type.INNER, alias = "annotationsoftware")
    @Where("annotationsoftware.name = :annotationSoftware")
    List<Variant> searchByChromosomeAndStartPosition(String chromosome, BigInteger startPosition, String annotationSoftware, String referenceGenome)

    //withConsequences and vcfInfo
    @Join(value = "sampleVariants", type = Join.Type.LEFT_FETCH)
    @Join(value = "sampleVariants.vcfinfo", type = Join.Type.LEFT_FETCH)
    @Join(value = "referenceGenomes", type = Join.Type.INNER, alias = "referencegenome")
    @Where("referencegenome.build = :referenceGenome")
    @Join(value = "consequences", type = Join.Type.INNER, alias = "consequence")
    @Join(value = "consequences.annotations", type = Join.Type.INNER, alias = "annotationsoftware")
    @Where("annotationsoftware.name = :annotationSoftware")
    List<Variant> queryByChromosomeAndStartPosition(String chromosome, BigInteger startPosition, String annotationSoftware, String referenceGenome)

    // withConsequences and no vcfinfo
    @Join(value = "referenceGenomes", type = Join.Type.INNER, alias = "referencegenome")
    @Where("referencegenome.build = :referenceGenome")
    @Join(value = "consequences", type = Join.Type.INNER, alias = "consequence")
    @Join(value = "consequences.annotations", type = Join.Type.INNER, alias = "annotationsoftware")
    @Where("annotationsoftware.name = :annotationSoftware")
    List<Variant> getByChromosomeAndStartPosition(String chromosome, BigInteger startPosition, String annotationSoftware, String referenceGenome)

    // withVcInfo
    @Join(value = "sampleVariants", type = Join.Type.LEFT_FETCH)
    @Join(value = "sampleVariants.vcfinfo", type = Join.Type.LEFT_FETCH)
    @Join(value = "referenceGenomes", type = Join.Type.INNER, alias = "referencegenome")
    @Where("referencegenome.build = :referenceGenome")
    List<Variant> retrieveByChromosomeAndStartPosition(String chromosome, BigInteger startPosition, String referenceGenome)

    // only based on refGenome
    @Join(value = "referenceGenomes", type = Join.Type.INNER, alias = "referencegenome")
    @Where("referencegenome.build = :referenceGenome")
    List<Variant> findByChromosome(String chromosome, String referenceGenome)

    // withConsequences and genotypes and vcfinfo
    @Join(value = "sampleVariants", type = Join.Type.LEFT_FETCH)
    @Join(value = "sampleVariants.vcfinfo", type = Join.Type.LEFT_FETCH)
    @Join(value = "sampleVariants.genotype", type = Join.Type.LEFT_FETCH)
    @Join(value = "referenceGenomes", type = Join.Type.INNER, alias = "referencegenome")
    @Where("referencegenome.build = :referenceGenome")
    @Join(value = "consequences", type = Join.Type.INNER, alias = "consequence")
    @Join(value = "consequences.annotations", type = Join.Type.INNER, alias = "annotationsoftware")
    @Where("annotationsoftware.name = :annotationSoftware")
    List<Variant> searchByChromosome(String chromosome, String annotationSoftware, String referenceGenome)

    //withConsequences and vcfInfo
    @Join(value = "sampleVariants", type = Join.Type.LEFT_FETCH)
    @Join(value = "sampleVariants.vcfinfo", type = Join.Type.LEFT_FETCH)
    @Join(value = "referenceGenomes", type = Join.Type.INNER, alias = "referencegenome")
    @Where("referencegenome.build = :referenceGenome")
    @Join(value = "consequences", type = Join.Type.INNER, alias = "consequence")
    @Join(value = "consequences.annotations", type = Join.Type.INNER, alias = "annotationsoftware")
    @Where("annotationsoftware.name = :annotationSoftware")
    List<Variant> queryByChromosome(String chromosome, String annotationSoftware, String referenceGenome)

    // withConsequences and no vcfinfo
    @Join(value = "referenceGenomes", type = Join.Type.INNER, alias = "referencegenome")
    @Where("referencegenome.build = :referenceGenome")
    @Join(value = "consequences", type = Join.Type.INNER, alias = "consequence")
    @Join(value = "consequences.annotations", type = Join.Type.INNER, alias = "annotationsoftware")
    @Where("annotationsoftware.name = :annotationSoftware")
    List<Variant> getByChromosome(String chromosome, String annotationSoftware, String referenceGenome)

    // withVcInfo
    @Join(value = "sampleVariants", type = Join.Type.LEFT_FETCH)
    @Join(value = "sampleVariants.vcfinfo", type = Join.Type.LEFT_FETCH)
    @Join(value = "referenceGenomes", type = Join.Type.INNER, alias = "referencegenome")
    @Where("referencegenome.build = :referenceGenome")
    List<Variant> retrieveByChromosome(String chromosome, String referenceGenome)

    // only based on refGenome
    @Join(value = "referenceGenomes", type = Join.Type.INNER, alias = "referencegenome")
    @Where("referencegenome.build = :referenceGenome")
    List<Variant> findByStartPosition(BigInteger startPosition, String referenceGenome)

    // withConsequences and genotypes and vcfinfo
    @Join(value = "sampleVariants", type = Join.Type.LEFT_FETCH)
    @Join(value = "sampleVariants.vcfinfo", type = Join.Type.LEFT_FETCH)
    @Join(value = "sampleVariants.genotype", type = Join.Type.LEFT_FETCH)
    @Join(value = "referenceGenomes", type = Join.Type.INNER, alias = "referencegenome")
    @Where("referencegenome.build = :referenceGenome")
    @Join(value = "consequences", type = Join.Type.INNER, alias = "consequence")
    @Join(value = "consequences.annotations", type = Join.Type.INNER, alias = "annotationsoftware")
    @Where("annotationsoftware.name = :annotationSoftware")
    List<Variant> searchByStartPosition(BigInteger startPosition, String annotationSoftware, String referenceGenome)

    //withConsequences and vcfInfo
    @Join(value = "sampleVariants", type = Join.Type.LEFT_FETCH)
    @Join(value = "sampleVariants.vcfinfo", type = Join.Type.LEFT_FETCH)
    @Join(value = "referenceGenomes", type = Join.Type.INNER, alias = "referencegenome")
    @Where("referencegenome.build = :referenceGenome")
    @Join(value = "consequences", type = Join.Type.INNER, alias = "consequence")
    @Join(value = "consequences.annotations", type = Join.Type.INNER, alias = "annotationsoftware")
    @Where("annotationsoftware.name = :annotationSoftware")
    List<Variant> queryByStartPosition(BigInteger startPosition, String annotationSoftware, String referenceGenome)

    // withConsequences and no vcfinfo
    @Join(value = "referenceGenomes", type = Join.Type.INNER, alias = "referencegenome")
    @Where("referencegenome.build = :referenceGenome")
    @Join(value = "consequences", type = Join.Type.INNER, alias = "consequence")
    @Join(value = "consequences.annotations", type = Join.Type.INNER, alias = "annotationsoftware")
    @Where("annotationsoftware.name = :annotationSoftware")
    List<Variant> getByStartPosition(BigInteger startPosition, String annotationSoftware, String referenceGenome)

    // withVcInfo
    @Join(value = "sampleVariants", type = Join.Type.LEFT_FETCH)
    @Join(value = "sampleVariants.vcfinfo", type = Join.Type.LEFT_FETCH)
    @Join(value = "referenceGenomes", type = Join.Type.INNER, alias = "referencegenome")
    @Where("referencegenome.build = :referenceGenome")
    List<Variant> retrieveByStartPosition(BigInteger startPosition, String referenceGenome)

    // only based on refGenome
    @Query("SELECT variant.*, sample.identifier FROM variant INNER JOIN referencegenome_variant ON variant.id=referencegenome_variant.variant_id INNER JOIN referencegenome ON referencegenome.id=referencegenome_variant.referencegenome_id INNER JOIN sample_variant ON variant.id = sample_variant.variant_id INNER JOIN sample ON sample_variant.sample_id = sample.id WHERE referencegenome.build = :referenceGenome AND sample.identifier = :sampleIdentifier")
    Set<Variant> findUsingSampleId(String sampleIdentifier, String referenceGenome)

    // withConsequences and genotypes and vcfinfo
    @Query("SELECT variant.*, consequence.*, vcfinfo.*, genotype.*, sample.identifier FROM variant INNER JOIN sample_variant ON variant.id = sample_variant.variant_id INNER JOIN vcfinfo ON vcfinfo.id=sample_variant.vcfinfo_id INNER JOIN referencegenome_variant ON variant.id=referencegenome_variant.variant_id INNER JOIN referencegenome ON referencegenome.id=referencegenome_variant.referencegenome_id INNER JOIN variant_consequence ON variant.id = variant_consequence.variant_id INNER JOIN consequence on variant_consequence.consequence_id = consequence.id INNER JOIN annotationsoftware_consequence ON annotationsoftware_consequence.consequence_id= consequence.id INNER JOIN annotationsoftware ON annotationsoftware.id = annotationsoftware_consequence.annotationsoftware_id INNER JOIN genotype ON genotype.id=sample_variant.genotype_id INNER JOIN sample ON sample_variant.sample_id = sample.id WHERE referencegenome.build = :referenceGenome AND sample.identifier = :sampleIdentifier AND annotationsoftware.name = :annotationSoftware")
    Set<Variant> findUsingSampleIdWithConsAndGeno(String sampleIdentifier, String referenceGenome, String annotationSoftware)

    // withConsequences and vcfinfo
    @Query("SELECT variant.*, consequence.*, vcfinfo.*, sample.identifier FROM variant INNER JOIN sample_variant ON variant.id = sample_variant.variant_id INNER JOIN vcfinfo ON vcfinfo.id=sample_variant.vcfinfo_id INNER JOIN referencegenome_variant ON variant.id=referencegenome_variant.variant_id INNER JOIN referencegenome ON referencegenome.id=referencegenome_variant.referencegenome_id INNER JOIN variant_consequence ON variant.id = variant_consequence.variant_id INNER JOIN consequence on variant_consequence.consequence_id = consequence.id INNER JOIN annotationsoftware_consequence ON annotationsoftware_consequence.consequence_id= consequence.id INNER JOIN annotationsoftware ON annotationsoftware.id = annotationsoftware_consequence.annotationsoftware_id INNER JOIN sample ON sample_variant.sample_id = sample.id WHERE referencegenome.build = :referenceGenome AND sample.identifier = :sampleIdentifier AND annotationsoftware.name = :annotationSoftware")
    Set<Variant> findUsingSampleIdWithConsAndInfo(String sampleIdentifier, String referenceGenome, String annotationSoftware)

    //withConsequences and no vcfInfo
    @Query("SELECT variant.*, consequence.*, sample.identifier FROM variant INNER JOIN sample_variant ON variant.id = sample_variant.variant_id INNER JOIN referencegenome_variant ON variant.id=referencegenome_variant.variant_id INNER JOIN referencegenome ON referencegenome.id=referencegenome_variant.referencegenome_id INNER JOIN variant_consequence ON variant.id = variant_consequence.variant_id INNER JOIN consequence on variant_consequence.consequence_id = consequence.id INNER JOIN annotationsoftware_consequence ON annotationsoftware_consequence.consequence_id= consequence.id INNER JOIN annotationsoftware ON annotationsoftware.id = annotationsoftware_consequence.annotationsoftware_id INNER JOIN sample ON sample_variant.sample_id = sample.id WHERE referencegenome.build = :referenceGenome AND sample.identifier = :sampleIdentifier AND annotationsoftware.name = :annotationSoftware")
    Set<Variant> findUsingSampleIdWithCons(String sampleIdentifier, String referenceGenome, String annotationSoftware)

    // withVcInfo
    @Query("SELECT variant.*, vcfinfo.* , sample.identifier FROM variant INNER JOIN sample_variant ON variant.id = sample_variant.variant_id INNER JOIN vcfinfo ON vcfinfo.id=sample_variant.vcfinfo_id INNER JOIN referencegenome_variant ON variant.id=referencegenome_variant.variant_id INNER JOIN referencegenome ON referencegenome.id=referencegenome_variant.referencegenome_id INNER JOIN sample ON sample_variant.sample_id = sample.id  WHERE referencegenome.build = :referenceGenome AND sample.identifier = :sampleIdentifier")
    Set<Variant> findUsingSampleIdWithInfo(String sampleIdentifier, String referenceGenome)

    // withConsequences and genotypes and vcfinfo
    @Query("SELECT variant.*, consequence.*, genotype.* FROM variant INNER JOIN referencegenome_variant ON variant.id=referencegenome_variant.variant_id INNER JOIN referencegenome ON referencegenome.id=referencegenome_variant.referencegenome_id INNER JOIN sample_variant ON variant.id = sample_variant.variant_id INNER JOIN genotype ON genotype.id=sample_variant.genotype_id INNER JOIN variant_consequence ON variant.id = variant_consequence.variant_id INNER JOIN consequence on variant_consequence.consequence_id = consequence.id INNER JOIN gene_consequence on gene_consequence.consequence_id = consequence.id INNER JOIN annotationsoftware_consequence ON annotationsoftware_consequence.consequence_id= consequence.id INNER JOIN annotationsoftware ON annotationsoftware.id = annotationsoftware_consequence.annotationsoftware_id INNER JOIN gene on gene.id=gene_consequence.gene_id WHERE referencegenome.build = :referenceGenome AND consequence.genesymbol = :geneName AND annotationsoftware.name = :annotationSoftware")
    Set<Variant> findUsingGeneNameWithConsAndGeno(String geneName, String referenceGenome, String annotationSoftware)

    //withConsequences and no vcfInfo
    @Query("SELECT variant.*, consequence.*, vcfinfo.* FROM variant INNER JOIN referencegenome_variant ON variant.id=referencegenome_variant.variant_id INNER JOIN referencegenome ON referencegenome.id=referencegenome_variant.referencegenome_id INNER JOIN sample_variant ON variant.id = sample_variant.variant_id INNER JOIN vcfinfo ON vcfinfo.id=sample_variant.vcfinfo_id INNER JOIN variant_consequence ON variant.id = variant_consequence.variant_id INNER JOIN consequence on variant_consequence.consequence_id = consequence.id INNER JOIN gene_consequence on gene_consequence.consequence_id = consequence.id INNER JOIN annotationsoftware_consequence ON annotationsoftware_consequence.consequence_id= consequence.id INNER JOIN annotationsoftware ON annotationsoftware.id = annotationsoftware_consequence.annotationsoftware_id INNER JOIN gene on gene.id=gene_consequence.gene_id WHERE referencegenome.build = :referenceGenome AND consequence.genesymbol = :geneName AND annotationsoftware.name = :annotationSoftware")
    Set<Variant> findUsingGeneNameWithConsAndInfo(String geneName, String referenceGenome, String annotationSoftware)

    //withConsequences and no vcfInfo
    @Query("SELECT variant.*, consequence.* FROM variant INNER JOIN referencegenome_variant ON variant.id=referencegenome_variant.variant_id INNER JOIN referencegenome ON referencegenome.id=referencegenome_variant.referencegenome_id INNER JOIN variant_consequence ON variant.id = variant_consequence.variant_id INNER JOIN consequence on variant_consequence.consequence_id = consequence.id INNER JOIN gene_consequence on gene_consequence.consequence_id = consequence.id INNER JOIN annotationsoftware_consequence ON annotationsoftware_consequence.consequence_id= consequence.id INNER JOIN gene on gene.id=gene_consequence.gene_id  INNER JOIN annotationsoftware ON annotationsoftware.id = annotationsoftware_consequence.annotationsoftware_id WHERE referencegenome.build = :referenceGenome AND consequence.genesymbol = :geneName AND annotationsoftware.name = :annotationSoftware")
    Set<Variant> findUsingGeneNameWithCons(String geneName, String referenceGenome, String annotationSoftware)

    // withVcInfo
    @Query("SELECT variant.*, vcfinfo.* FROM variant INNER JOIN referencegenome_variant ON variant.id=referencegenome_variant.variant_id INNER JOIN referencegenome ON referencegenome.id=referencegenome_variant.referencegenome_id INNER JOIN sample_variant ON variant.id = sample_variant.variant_id INNER JOIN vcfinfo ON vcfinfo.id=sample_variant.vcfinfo_id INNER JOIN variant_consequence ON variant.id = variant_consequence.variant_id INNER JOIN consequence on variant_consequence.consequence_id = consequence.id INNER JOIN gene_consequence on gene_consequence.consequence_id = consequence.id INNER JOIN gene on gene.id=gene_consequence.gene_id WHERE referencegenome.build = :referenceGenome AND consequence.genesymbol = :geneName")
    Set<Variant> findUsingGeneNameWithInfo(String geneName, String referenceGenome)

    // only based on refGenome
    @Query("SELECT variant.* FROM variant INNER JOIN referencegenome_variant ON variant.id=referencegenome_variant.variant_id INNER JOIN referencegenome ON referencegenome.id=referencegenome_variant.referencegenome_id INNER JOIN variant_consequence ON variant.id = variant_consequence.variant_id INNER JOIN consequence on variant_consequence.consequence_id = consequence.id INNER JOIN gene_consequence on gene_consequence.consequence_id = consequence.id INNER JOIN gene on gene.id=gene_consequence.gene_id WHERE referencegenome.build = :referenceGenome AND consequence.genesymbol = :geneName")
    Set<Variant> findUsingGeneName(String geneName, String referenceGenome)

    // withConsequences and genotypes and vcfinfo
    @Query("SELECT variant.*, consequence.*, genotype.* FROM variant INNER JOIN referencegenome_variant ON variant.id=referencegenome_variant.variant_id INNER JOIN referencegenome ON referencegenome.id=referencegenome_variant.referencegenome_id INNER JOIN sample_variant ON variant.id = sample_variant.variant_id INNER JOIN genotype ON genotype.id=sample_variant.genotype_id INNER JOIN variant_consequence ON variant.id = variant_consequence.variant_id INNER JOIN consequence on variant_consequence.consequence_id = consequence.id INNER JOIN gene_consequence on gene_consequence.consequence_id = consequence.id INNER JOIN annotationsoftware_consequence ON annotationsoftware_consequence.consequence_id= consequence.id INNER JOIN annotationsoftware ON annotationsoftware.id = annotationsoftware_consequence.annotationsoftware_id INNER JOIN gene on gene.id=gene_consequence.gene_id WHERE referencegenome.build = :referenceGenome AND gene.geneid = :geneId AND annotationsoftware.name = :annotationSoftware")
    Set<Variant> findUsingGeneIdWithConsAndGeno(String geneId, String referenceGenome, String annotationSoftware)

    //withConsequences and no vcfInfo
    @Query("SELECT variant.*, consequence.*, vcfinfo.* FROM variant INNER JOIN referencegenome_variant ON variant.id=referencegenome_variant.variant_id INNER JOIN referencegenome ON referencegenome.id=referencegenome_variant.referencegenome_id INNER JOIN sample_variant ON variant.id = sample_variant.variant_id INNER JOIN vcfinfo ON vcfinfo.id=sample_variant.vcfinfo_id INNER JOIN variant_consequence ON variant.id = variant_consequence.variant_id INNER JOIN consequence on variant_consequence.consequence_id = consequence.id INNER JOIN gene_consequence on gene_consequence.consequence_id = consequence.id INNER JOIN annotationsoftware_consequence ON annotationsoftware_consequence.consequence_id= consequence.id INNER JOIN annotationsoftware ON annotationsoftware.id = annotationsoftware_consequence.annotationsoftware_id INNER JOIN gene on gene.id=gene_consequence.gene_id WHERE referencegenome.build = :referenceGenome AND gene.geneid = :geneId AND annotationsoftware.name = :annotationSoftware")
    Set<Variant> findUsingGeneIdWithConsAndInfo(String geneId, String referenceGenome, String annotationSoftware)

    //withConsequences and no vcfInfo
    @Query("SELECT variant.*, consequence.* FROM variant INNER JOIN referencegenome_variant ON variant.id=referencegenome_variant.variant_id INNER JOIN referencegenome ON referencegenome.id=referencegenome_variant.referencegenome_id INNER JOIN variant_consequence ON variant.id = variant_consequence.variant_id INNER JOIN consequence on variant_consequence.consequence_id = consequence.id INNER JOIN gene_consequence on gene_consequence.consequence_id = consequence.id INNER JOIN annotationsoftware_consequence ON annotationsoftware_consequence.consequence_id= consequence.id INNER JOIN gene on gene.id=gene_consequence.gene_id  INNER JOIN annotationsoftware ON annotationsoftware.id = annotationsoftware_consequence.annotationsoftware_id WHERE referencegenome.build = :referenceGenome AND gene.geneid = :geneId AND annotationsoftware.name = :annotationSoftware")
    Set<Variant> findUsingGeneIdWithCons(String geneId, String referenceGenome, String annotationSoftware)

    // withVcInfo
    @Query("SELECT variant.*, vcfinfo.* FROM variant INNER JOIN referencegenome_variant ON variant.id=referencegenome_variant.variant_id INNER JOIN referencegenome ON referencegenome.id=referencegenome_variant.referencegenome_id INNER JOIN sample_variant ON variant.id = sample_variant.variant_id INNER JOIN vcfinfo ON vcfinfo.id=sample_variant.vcfinfo_id INNER JOIN variant_consequence ON variant.id = variant_consequence.variant_id INNER JOIN consequence on variant_consequence.consequence_id = consequence.id INNER JOIN gene_consequence on gene_consequence.consequence_id = consequence.id INNER JOIN gene on gene.id=gene_consequence.gene_id WHERE referencegenome.build = :referenceGenome AND gene.geneid = :geneId")
    Set<Variant> findUsingGeneIdWithInfo(String geneId, String referenceGenome)

    // only based on refGenome
    @Query("SELECT variant.* FROM variant INNER JOIN referencegenome_variant ON variant.id=referencegenome_variant.variant_id INNER JOIN referencegenome ON referencegenome.id=referencegenome_variant.referencegenome_id INNER JOIN variant_consequence ON variant.id = variant_consequence.variant_id INNER JOIN consequence on variant_consequence.consequence_id = consequence.id INNER JOIN gene_consequence on gene_consequence.consequence_id = consequence.id INNER JOIN gene on gene.id=gene_consequence.gene_id WHERE referencegenome.build = :referenceGenome AND gene.geneid = :geneId")
    Set<Variant> findUsingGeneId(String geneId, String referenceGenome)

    // withConsequences and genotypes and vcfinfo
    @Query("SELECT variant.*, consequence.*, genotype.* FROM variant INNER JOIN referencegenome_variant ON variant.id=referencegenome_variant.variant_id INNER JOIN referencegenome ON referencegenome.id=referencegenome_variant.referencegenome_id INNER JOIN sample_variant ON variant.id = sample_variant.variant_id INNER JOIN sample ON sample_variant.sample_id = sample.id INNER JOIN genotype ON genotype.id=sample_variant.genotype_id INNER JOIN variant_consequence ON variant.id = variant_consequence.variant_id INNER JOIN consequence on variant_consequence.consequence_id = consequence.id INNER JOIN gene_consequence on gene_consequence.consequence_id = consequence.id INNER JOIN annotationsoftware_consequence ON annotationsoftware_consequence.consequence_id= consequence.id INNER JOIN annotationsoftware ON annotationsoftware.id = annotationsoftware_consequence.annotationsoftware_id INNER JOIN gene on gene.id=gene_consequence.gene_id WHERE referencegenome.build = :referenceGenome AND gene.geneid = :geneId AND annotationsoftware.name = :annotationSoftware AND sample.identifier = :sampleIdentifier")
    Set<Variant> findUsingSampleIdAndGeneIdWithConsAndGeno(String sampleIdentifier, String geneId, String referenceGenome, String annotationSoftware)

    //withConsequences and no vcfInfo
    @Query("SELECT variant.*, consequence.*, vcfinfo.* FROM variant INNER JOIN referencegenome_variant ON variant.id=referencegenome_variant.variant_id INNER JOIN referencegenome ON referencegenome.id=referencegenome_variant.referencegenome_id INNER JOIN sample_variant ON variant.id = sample_variant.variant_id INNER JOIN sample ON sample_variant.sample_id = sample.id INNER JOIN vcfinfo ON vcfinfo.id=sample_variant.vcfinfo_id INNER JOIN variant_consequence ON variant.id = variant_consequence.variant_id INNER JOIN consequence on variant_consequence.consequence_id = consequence.id INNER JOIN gene_consequence on gene_consequence.consequence_id = consequence.id INNER JOIN annotationsoftware_consequence ON annotationsoftware_consequence.consequence_id= consequence.id INNER JOIN annotationsoftware ON annotationsoftware.id = annotationsoftware_consequence.annotationsoftware_id INNER JOIN gene on gene.id=gene_consequence.gene_id WHERE referencegenome.build = :referenceGenome AND gene.geneid = :geneId AND annotationsoftware.name = :annotationSoftware AND sample.identifier = :sampleIdentifier")
    Set<Variant> findUsingSampleIdAndGeneIdWithConsAndInfo(String sampleIdentifier, String geneId, String referenceGenome, String annotationSoftware)

    //withConsequences and no vcfInfo
    @Query("SELECT variant.*, consequence.* FROM variant INNER JOIN referencegenome_variant ON variant.id=referencegenome_variant.variant_id INNER JOIN referencegenome ON referencegenome.id=referencegenome_variant.referencegenome_id INNER JOIN sample_variant ON variant.id = sample_variant.variant_id INNER JOIN sample ON sample_variant.sample_id = sample.id INNER JOIN variant_consequence ON variant.id = variant_consequence.variant_id INNER JOIN consequence on variant_consequence.consequence_id = consequence.id INNER JOIN gene_consequence on gene_consequence.consequence_id = consequence.id INNER JOIN annotationsoftware_consequence ON annotationsoftware_consequence.consequence_id= consequence.id INNER JOIN gene on gene.id=gene_consequence.gene_id  INNER JOIN annotationsoftware ON annotationsoftware.id = annotationsoftware_consequence.annotationsoftware_id WHERE referencegenome.build = :referenceGenome AND gene.geneid = :geneId AND annotationsoftware.name = :annotationSoftware AND sample.identifier = :sampleIdentifier")
    Set<Variant> findUsingSampleIdAndGeneIdWithCons(String sampleIdentifier, String geneId, String referenceGenome, String annotationSoftware)

    // withVcInfo
    @Query("SELECT variant.*, vcfinfo.* FROM variant INNER JOIN referencegenome_variant ON variant.id=referencegenome_variant.variant_id INNER JOIN referencegenome ON referencegenome.id=referencegenome_variant.referencegenome_id INNER JOIN sample_variant ON variant.id = sample_variant.variant_id  INNER JOIN sample_variant ON variant.id = sample_variant.variant_id INNER JOIN sample ON sample_variant.sample_id = sample.id INNER JOIN vcfinfo ON vcfinfo.id=sample_variant.vcfinfo_id INNER JOIN variant_consequence ON variant.id = variant_consequence.variant_id INNER JOIN consequence on variant_consequence.consequence_id = consequence.id INNER JOIN gene_consequence on gene_consequence.consequence_id = consequence.id INNER JOIN gene on gene.id=gene_consequence.gene_id WHERE referencegenome.build = :referenceGenome AND gene.geneid = :geneId AND sample.identifier = :sampleIdentifier")
    Set<Variant> findUsingSampleIdAndGeneIdWithInfo(String sampleIdentifier, String geneId, String referenceGenome)

    // only based on refGenome
    @Query("SELECT variant.* FROM variant INNER JOIN referencegenome_variant ON variant.id=referencegenome_variant.variant_id INNER JOIN referencegenome ON referencegenome.id=referencegenome_variant.referencegenome_id INNER JOIN sample_variant ON variant.id = sample_variant.variant_id INNER JOIN sample ON sample_variant.sample_id = sample.id INNER JOIN variant_consequence ON variant.id = variant_consequence.variant_id INNER JOIN consequence on variant_consequence.consequence_id = consequence.id INNER JOIN gene_consequence on gene_consequence.consequence_id = consequence.id INNER JOIN gene on gene.id=gene_consequence.gene_id WHERE referencegenome.build = :referenceGenome AND gene.geneid = :geneId AND sample.identifier = :sampleIdentifier")
    Set<Variant> findUsingSampleIdAndGeneId(String sampleIdentifier, String geneId, String referenceGenome)
}
