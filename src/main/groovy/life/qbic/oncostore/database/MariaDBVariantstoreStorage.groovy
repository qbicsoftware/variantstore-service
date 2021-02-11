package life.qbic.oncostore.database

import groovy.json.JsonSlurper
import groovy.sql.BatchingPreparedStatementWrapper
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.util.logging.Log4j2
import life.qbic.micronaututils.QBiCDataSource
import life.qbic.oncostore.model.*
import life.qbic.oncostore.parser.MetadataContext
import life.qbic.oncostore.service.VariantstoreStorage
import life.qbic.oncostore.util.IdValidator
import life.qbic.oncostore.util.ListingArguments
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.constraints.NotNull
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException


/**
 * A VariantstoreStorage implementation.
 *
 * This provides an interface to a MariaDB database storing variant information.
 *
 * @since: 1.0.0
 */
@Log4j2
@Singleton
class MariaDBVariantstoreStorage implements VariantstoreStorage {

    /**
     * The data source instance
     */
    QBiCDataSource dataSource

    /**
     * Predefined queries for inserting db entries in junction tables.
     */
    static final String insertVariantConsequenceJunction = "INSERT INTO variant_has_consequence (variant_id, consequence_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE id=id"
    static final insertVariantVariantCallerJunction = "INSERT INTO variant_has_variantcaller (variantcaller_id, variant_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE variantcaller_id=variantcaller_id"
    static final insertAnnotationSoftwareConsequenceJunction = "INSERT INTO annotationsoftware_has_consequence (annotationsoftware_id, consequence_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE id=id"
    static final insertReferenceGenomeVariantJunction = "INSERT INTO variant_has_referencegenome (referencegenome_id, variant_id) VALUES (?,?) ON DUPLICATE KEY UPDATE referencegenome_id=referencegenome_id"
    static final insertSampleVariantJunction = "INSERT INTO sample_has_variant (sample_id, variant_id, vcfinfo_id, genotype_id) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE id=id"
    static final insertEnsemblGeneJunction = "INSERT INTO ensembl_has_gene (ensembl_id, gene_id) VALUES (?,?) ON DUPLICATE KEY UPDATE ensembl_id=ensembl_id"
    static final insertConsequenceGeneJunction = "INSERT INTO consequence_has_gene (consequence_id, gene_id) VALUES (?,?) ON DUPLICATE KEY UPDATE id=id"


    /**
     * Predefined queries for selecting entities from the database.
     */
    static final selectVariantsWithConsequencesAndGenotypes = """SELECT variant.id as varid, variant.chr as varchr, variant
.start as varstart, 
variant.end as varend, variant.ref as varref, variant.obs as varobs, variant.issomatic as varsomatic, variant.uuid as
 varuuid, variant.databaseidentifier as vardbid, consequence.*, sample.identifier, genotype.*, vcfinfo.*, gene.id as geneindex, gene.geneid as geneid
  FROM 
 variant INNER JOIN sample_has_variant ON variant_id = variant.id INNER JOIN sample ON sample_has_variant.sample_id = sample.id INNER JOIN vcfinfo ON vcfinfo.id=sample_has_variant
 .vcfinfo_id INNER JOIN variant_has_referencegenome ON variant.id = 
 variant_has_referencegenome.variant_id INNER JOIN referencegenome ON referencegenome.id =
   variant_has_referencegenome.referencegenome_id INNER JOIN variant_has_consequence 
 ON variant.id = variant_has_consequence.variant_id INNER JOIN consequence on variant_has_consequence.consequence_id 
 = consequence.id INNER JOIN annotationsoftware_has_consequence ON 
 annotationsoftware_has_consequence.consequence_id = consequence.id INNER JOIN annotationsoftware ON 
 annotationsoftware.id = annotationsoftware_has_consequence.annotationsoftware_id INNER JOIN genotype ON genotype
 .id=sample_has_variant.genotype_id INNER JOIN consequence_has_gene on consequence_has_gene.consequence_id = consequence.id INNER JOIN 
 gene on gene.id=consequence_has_gene.gene_id;"""
    static final selectVariantsWithConsequencesAndVcfInfo = """SELECT variant.id as varid, variant.chr as varchr, variant
.start as varstart, 
variant.end as varend, variant.ref as varref, variant.obs as varobs, variant.issomatic as varsomatic, variant.uuid as
 varuuid, variant.databaseidentifier as vardbid, consequence.*, vcfinfo.*, gene.id as geneindex, gene.geneid as 
 geneid FROM 
 variant INNER JOIN sample_has_variant ON variant_id = variant.id INNER JOIN vcfinfo ON vcfinfo.id=sample_has_variant
 .vcfinfo_id INNER JOIN variant_has_referencegenome ON variant.id = 
 variant_has_referencegenome.variant_id INNER JOIN referencegenome ON referencegenome.id =
   variant_has_referencegenome.referencegenome_id INNER JOIN variant_has_consequence 
 ON variant.id = variant_has_consequence.variant_id INNER JOIN consequence on variant_has_consequence.consequence_id 
 = consequence.id INNER JOIN annotationsoftware_has_consequence ON annotationsoftware_has_consequence.consequence_id 
 = consequence.id INNER JOIN annotationsoftware ON annotationsoftware.id = annotationsoftware_has_consequence
 .annotationsoftware_id INNER JOIN  consequence_has_gene on consequence_has_gene.consequence_id = consequence.id 
 INNER JOIN 
 gene on gene.id=consequence_has_gene.gene_id;"""
    static final selectVariantsWithConsequences = """SELECT variant.id as varid, variant.chr as varchr, variant.start as 
varstart, 
variant.end as varend, variant.ref as varref, variant.obs as varobs, variant.issomatic as varsomatic, variant.uuid as
 varuuid, variant.databaseidentifier as vardbid, consequence.*, gene.id as geneindex, gene.geneid as geneid FROM 
 variant INNER JOIN variant_has_referencegenome ON variant.id = 
 variant_has_referencegenome.variant_id INNER JOIN referencegenome ON referencegenome.id =
   variant_has_referencegenome.referencegenome_id INNER JOIN variant_has_consequence 
 ON variant.id = variant_has_consequence.variant_id INNER JOIN consequence on variant_has_consequence.consequence_id 
 = consequence.id INNER JOIN annotationsoftware_has_consequence ON annotationsoftware_has_consequence.consequence_id 
 = consequence.id INNER JOIN annotationsoftware ON annotationsoftware.id = annotationsoftware_has_consequence
 .annotationsoftware_id INNER JOIN  consequence_has_gene on consequence_has_gene.consequence_id = consequence.id 
 INNER JOIN 
 gene on gene.id=consequence_has_gene.gene_id;"""
    static final selectVariants = """SELECT variant.id as varid, variant.chr as varchr, variant.start as varstart, 
variant.end as varend, variant.ref as varref, variant.obs as varobs, variant.issomatic as varsomatic, variant.uuid as
 varuuid, variant.databaseidentifier as vardbid FROM variant INNER JOIN variant_has_referencegenome ON variant.id = 
 variant_has_referencegenome.variant_id INNER JOIN referencegenome ON referencegenome.id =
   variant_has_referencegenome.referencegenome_id;"""
    static final selectVariantsWithVcfInfo = """SELECT variant.id as varid, variant.chr as varchr, variant.start as varstart, 
variant.end as varend, variant.ref as varref, variant.obs as varobs, variant.issomatic as varsomatic, variant.uuid as
 varuuid, variant.databaseidentifier as vardbid, vcfinfo.* FROM variant INNER JOIN sample_has_variant ON variant_id = variant.id INNER JOIN vcfinfo ON vcfinfo.id=sample_has_variant
 .vcfinfo_id INNER JOIN variant_has_referencegenome ON variant.id = 
 variant_has_referencegenome.variant_id INNER JOIN referencegenome ON referencegenome.id =
   variant_has_referencegenome.referencegenome_id;"""

    @Inject
    MariaDBVariantstoreStorage(QBiCDataSource dataSource) {
        this.dataSource = dataSource
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<Variant> findVariantsForBeaconResponse(String chromosome, BigInteger start,
                                                String reference, String observed, String assemblyId) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        try {
            def variant = fetchVariantsForBeaconResponse(chromosome, start, reference, observed, assemblyId, sql)
            return variant
        } catch (Exception e) {
            throw new VariantstoreStorageException("Beacon something? $e.", e.printStackTrace())
        } finally {
            sql.close()
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<Case> findCaseById(String id) {
        //TODO should we check for a specific type of identifier?
        //if (id?.trim()) {
        //    throw new IllegalArgumentException("Invalid case identifier supplied.")
        //}
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        try {
            def cases = fetchCaseForId(id, sql)
            return cases
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch case with identifier $id.", e.fillInStackTrace())
        } finally {
            sql.close()
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<Sample> findSampleById(String id) {
        if (!IdValidator.isValidSampleCode(id)) {
            throw new IllegalArgumentException("Invalid sample identifier supplied.")
        }
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        try {
            def samples = fetchSampleForId(id, sql)
            return samples
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch sample with identifier $id.", e.fillInStackTrace())
        } finally {
            sql.close()
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<Variant> findVariantById(String id) {
        if (!IdValidator.isValidUUID(id)) {
            throw new IllegalArgumentException("Invalid variant identifier supplied.")
        }
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        try {
            def variants = fetchVariantForId(id, sql)
            return variants
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch variant with identifier $id.", e.printStackTrace())
        } finally {
            sql.close()
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<Gene> findGeneById(String id, ListingArguments args) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        try {
            if (args.getEnsemblVersion().isPresent()) {
                return fetchGeneForIdWithEnsemblVersion(id, args.getEnsemblVersion().get(), sql)
            }
            def ensemblversion = fetchEnsemblVersion(sql)
            if (ensemblversion) {
                return fetchGeneForIdWithEnsemblVersion(id, ensemblversion, sql)
            }
            // fall back solution, if there is not ensembl version in the variantstore instance
            return fetchGeneForId(id, sql)
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch gene with identifier $id.", e.printStackTrace())
        } finally {
            sql.close()
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<Case> findCases(@NotNull ListingArguments args) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        try {
            if (args.getGene().isPresent()) {
                if (args.getConsequenceType().isPresent()) {
                    return fetchCasesByGeneAndConsequenceType(args.getGene().get(), args.getConsequenceType().get(), sql)
                }
                else {
                    return fetchCasesByGene(args.getGene().get(), sql)
                }
            }
            if (args.getConsequenceType().isPresent()) {
                return fetchCasesByConsequenceType(args.getConsequenceType().get(), sql)
            }

            if (args.getChromosome().isPresent() && args.getStartPosition().isPresent() && args.getEndPosition()
                    .isPresent()) {
                return fetchCasesByChromosomeAndPositionRange(args.getChromosome().get(), args.getStartPosition().get
                        (), args.getEndPosition().get(), sql)
            }

            if (args.getChromosome().isPresent()) {
                return fetchCasesByChromosome(args.getChromosome().get(), sql)
            }

            return fetchCases(sql)
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch cases.", e.printStackTrace())
        } finally {
            sql.close()
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<Sample> findSamples(@NotNull ListingArguments args) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        try {
            if (args.getCancerEntity().isPresent()) {
                return fetchSamplesByCancerEntity(args.getCancerEntity().get(), sql)
            }
            return fetchSamples(sql)
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch samples.", e.fillInStackTrace())
        } finally {
            sql.close()
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<Variant> findVariants(@NotNull ListingArguments args, String referenceGenome, Boolean
            withConsequences, String annotationSoftware, Boolean withVcfInfo, Boolean withGenotypes) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        try {
            if (args.getChromosome().isPresent() && args.getStartPosition().isPresent()) {
                return fetchVariantsByChromosomeAndStartPosition(args.getChromosome().get(), args.getStartPosition()
                        .get(), referenceGenome, withConsequences, annotationSoftware, withVcfInfo, withGenotypes, sql)
            }

            if (args.getStartPosition().isPresent()) {
                return fetchVariantsByStartPosition(args.getStartPosition().get(), referenceGenome, withConsequences,
                        annotationSoftware, withVcfInfo,
                        withGenotypes, sql)
            }

            if (args.getChromosome().isPresent()) {
                return fetchVariantsByChromosome(args.getChromosome().get(), referenceGenome, withConsequences,
                        annotationSoftware, withVcfInfo,
                        withGenotypes, sql)
            }

            if (args.getSampleId().isPresent() && args.getGeneId().isPresent()) {
                return fetchVariantsBySampleAndGeneId(args.getSampleId().get(), args.getGeneId().get(), referenceGenome,
                        withConsequences, annotationSoftware, withVcfInfo, withGenotypes, sql)
            }

            if (args.getSampleId().isPresent()) {
                return fetchVariantsBySample(args.getSampleId().get(), referenceGenome, withConsequences,
                        annotationSoftware, withVcfInfo, withGenotypes, sql)
            }

            if (args.getGeneId().isPresent()) {
                return fetchVariantsByGeneId(args.getGeneId().get(), referenceGenome, withConsequences,
                        annotationSoftware, withVcfInfo, withGenotypes, sql)
            }
            return fetchVariants(referenceGenome, withConsequences, annotationSoftware, withVcfInfo, withGenotypes, sql)
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch variants.", e.printStackTrace())
        } finally {
            sql.close()
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    ReferenceGenome findReferenceGenomeByVariant(Variant variant) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        try {

            def result =
                    sql.firstRow("SELECT id FROM variant WHERE variant.chr=? and variant.start=? and variant.end=? and variant.ref=? and variant.obs=? and variant.issomatic=?",
                            [variant.chromosome, variant.startPosition, variant.endPosition, variant.referenceAllele, variant
                                    .observedAllele, variant.isSomatic])

            def resultReference = sql.firstRow("SELECT * FROM referencegenome INNER JOIN variant_has_referencegenome WHERE variant_has_referencegenome.variant_id = ${result.id}")
            def referenceGenomeSource = resultReference.get("source") as String
            def referenceGenomeBuild = resultReference.get("build") as String
            def referenceGenomeVersion = resultReference.get("version") as String
            def referenceGenome = new ReferenceGenome(referenceGenomeSource, referenceGenomeBuild, referenceGenomeVersion)
            return referenceGenome
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch reference genome.", e.fillInStackTrace())
        } finally {
            sql.close()
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Annotation findAnnotationSoftwareByConsequence(Consequence consequence) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        try {

            def result = sql.firstRow("SELECT id FROM consequence WHERE consequence.allele=? and consequence.codingchange=? and consequence.transcriptid=? and consequence.transcriptversion=? and consequence.type=? and consequence.biotype=? and consequence.canonical=? and consequence.aachange=? and consequence.cdnaposition=? and consequence.cdsposition=? and consequence.proteinposition=? and consequence.proteinlength=? and consequence.cdnalength=? and consequence.cdslength=? and consequence.impact=? and consequence.exon=? and consequence.intron=? and consequence.strand=? and consequence.genesymbol=? and consequence.featuretype=? and consequence.distance=? and consequence.warnings=?",

                    [consequence.allele, consequence.codingChange, consequence.transcriptId, consequence
                            .transcriptVersion, consequence.type,
                     consequence.bioType, consequence.canonical, consequence.aaChange, consequence.cdnaPosition,
                     consequence.cdsPosition, consequence
                             .proteinPosition, consequence.proteinLength, consequence.cdnaLength, consequence
                             .cdsLength, consequence
                             .impact, consequence.exon, consequence.intron, consequence.strand, consequence
                             .geneSymbol, consequence.featureType,
                     consequence.distance, consequence.warnings])

            def resultAnnotation = sql.firstRow("SELECT * FROM annotationsoftware INNER JOIN annotationsoftware_has_consequence WHERE annotationsoftware_has_consequence.consequence_id = ${result.id}")
            def annotationSoftwareName = resultAnnotation.get("name") as String
            def annotationSoftwareVersion = resultAnnotation.get("version") as String
            def annotationSoftwareDoi = resultAnnotation.get("doi") as String
            def annotationSoftware = new Annotation(annotationSoftwareName, annotationSoftwareVersion, annotationSoftwareDoi)
            return annotationSoftware
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch annotation software.", e.fillInStackTrace())
        } finally {
            sql.close()
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<Gene> findGenes(@NotNull ListingArguments args) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        try {
            if (args.getSampleId()) {
                if (!IdValidator.isValidSampleCode(args.getSampleId().get())) {
                    throw new IllegalArgumentException("Invalid sample identifier supplied.")
                }
                return fetchGenesBySample(args.getSampleId().get(), sql)
            }

            return fetchGenes(sql)
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch genes.", e.fillInStackTrace())
        } finally {
            sql.close()
        }
    }

    private Integer fetchEnsemblVersion(Sql sql) {
        def result =
                sql.firstRow("SELECT MAX(version) AS version FROM ensembl")
        return result.version
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void storeCaseInStore(Case patient) throws VariantstoreStorageException {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        try {
            tryToStoreCase(patient)
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not store case in store: $patient", e)
        } finally {
            sql.close()
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void storeSampleInStore(Sample sample) throws VariantstoreStorageException {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        try {
            tryToStoreSample(sample)
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not store sample in store: $sample", e)
        } finally {
            sql.close()
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void storeReferenceGenomeInStore(ReferenceGenome referenceGenome) throws VariantstoreStorageException {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        try {
            tryToStoreReferenceGenome(referenceGenome)
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not store reference genome in store: $referenceGenome", e)
        } finally {
            sql.close()
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void storeVariantCallerInStore(VariantCaller variantCaller) throws VariantstoreStorageException {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        try {
            tryToStoreVariantCaller(variantCaller)
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not store variant calling software in store: " +
                    "$variantCaller", e)
        } finally {
            sql.close()
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void storeAnnotationSoftwareInStore(Annotation annotationSoftware) throws VariantstoreStorageException {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        try {
            tryToStoreAnnotationSoftware(annotationSoftware)
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not store annotation software in store: " +
                    "$annotationSoftware", e)
        } finally {
            sql.close()
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void storeVariantsInStoreWithMetadata(MetadataContext metadata, Map sampleIdentifiers, ArrayList variants) throws
            VariantstoreStorageException {

        try {
            def cId = tryToStoreCase(metadata.getCase())
            if (!sampleIdentifiers.isEmpty()) tryToStoreSamplesWithCase(sampleIdentifiers, cId)

            tryToStoreVariantCaller(metadata.getVariantCalling())
            tryToStoreAnnotationSoftware(metadata.getVariantAnnotation())
            tryToStoreReferenceGenome(metadata.getReferenceGenome())

            def vcId = tryToFindVariantCaller(metadata.getVariantCalling())
            def asId = tryToFindAnnotationSoftware(metadata.getVariantAnnotation())
            def rgId = tryToFindReferenceGenome(metadata.getReferenceGenome())
            def sIds = tryToFindSamples(sampleIdentifiers, cId)

            //sql.setCacheStatements(true)

            /* INSERT variants and save consequences for import */
            def consequencesToInsert = !variants.isEmpty() ? tryToStoreVariantsBatch(variants) : []
            tryToStoreVariantInfo(variants)
            tryToStoreVariantGenotypes(variants)

            /* INSERT consequences */
            def consGeneMap = !consequencesToInsert.isEmpty() ? tryToStoreConsequencesBatch(consequencesToInsert as
                    List<Consequence>) : [:]

            /* INSERT genes */
            if (!consGeneMap.values().isEmpty()) tryToStoreGenes(consGeneMap.values().toList().flatten() as
                    List<String>)

            /* GET ids of genes */
            def geneIdMap = !consGeneMap.isEmpty() ? tryToFindGenesByConsequence(consGeneMap as HashMap<Consequence,
                    List<String>>) : [:]
            consGeneMap.clear()

            /* GET ids of variants */
            def variantInsert = tryToFindVariants(variants)
            def variantIdMap = variantInsert.first
            def infoIdMap = variantInsert.second

            /* GET ids of consequences */
            def findConsequenceMaps = !variants.isEmpty() ? tryToFindConsequences(variants) : new Tuple2<HashMap,
                    HashMap>()
            def variantConsequenceIdMap = !findConsequenceMaps.isEmpty() ? findConsequenceMaps.first : [:]
            /* consequence to consequence DB id map */
            def consIdMap = !findConsequenceMaps.isEmpty() ? findConsequenceMaps.second : [:]

            //TODO optimization potential
            /* GET ids of genotypes */
            def genotypeMap = tryToFindGenotypes(variants, sampleIdentifiers)
            variants.clear()

            /* INSERT variant and consequence junction */
            if (!variantConsequenceIdMap.values().flatten().isEmpty())
                tryToStoreJunctionBatchFromMap(variantConsequenceIdMap as HashMap, variantIdMap, insertVariantConsequenceJunction)

            /* INSERT consequence and gene junction */
            if (!geneIdMap.values().isEmpty()) tryToStoreJunctionBatchFromMap(geneIdMap as HashMap, consIdMap as
                    HashMap, insertConsequenceGeneJunction)

            /* INSERT consequences and annotation software junction */
            if (!variantConsequenceIdMap.values().toList().flatten().isEmpty()) tryToStoreJunctionBatch(asId,
                    variantConsequenceIdMap.values().toList().flatten(), insertAnnotationSoftwareConsequenceJunction)

            /* INSERT sample and variants junction */
            if (!sIds.isEmpty() && !variantIdMap.values().asList().isEmpty() && !infoIdMap.values().asList().isEmpty())
                tryToStoreJunctionBatchForSamplesAndVariants(sIds, variantIdMap, infoIdMap, genotypeMap, insertSampleVariantJunction)

            if (!variantIdMap.values().asList().isEmpty()) {
                /* INSERT variants and variant caller in junction table */
                tryToStoreJunctionBatch(vcId, variantIdMap.values().asList(), insertVariantVariantCallerJunction)

                /* INSERT variants and reference genome in junction table */
                tryToStoreJunctionBatch(rgId, variantIdMap.values().asList(), insertReferenceGenomeVariantJunction)
            }
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not store variants with metadata in store: $metadata", e
                    .printStackTrace())
        } finally {
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void storeGenesWithMetadata(Integer version, String date, ReferenceGenome referenceGenome, List<Gene> genes)
            throws VariantstoreStorageException {
        this.sql = new Sql(dataSource.connection)
        try {
            tryToStoreReferenceGenome(referenceGenome)
            def rgId = tryToFindReferenceGenome(referenceGenome)

            tryToStoreEnsemblDB(version, date, rgId)
            def enId = tryToFindEnsemblDB(version, date, rgId)

            sql.connection.autoCommit = false
            sql.setCacheStatements(true)

            tryToStoreGeneObjects(genes)

            /* GET ids of genes */
            def geneIdMap = tryToFindGenes(genes)

            /* INSERT genes and ensembl version in junction table */
            tryToStoreJunctionBatch(enId, geneIdMap.values().asList(), insertEnsemblGeneJunction)


        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not store genes in store: ", e.printStackTrace())
        } finally {
            sql.close()
        }
    }

    Tuple2<HashMap, HashMap> tryToFindVariants2(ArrayList<SimpleVariantContext> variants) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        def ids = [:]
        def infoIds = [:]
        def infosFound = [:]
        def BATCH_SIZE = 5000

        def varsToFind = new HashSet()
        def varsIdMap = [:]
        def infoValues = []
        def propsToUse = ["ancestralAllele", "alleleCount", "alleleFrequency"]
        def props = VcfInfo.declaredFields.findAll { !it.synthetic && propsToUse.contains(it.name) }.collect { it }

        def allVcfInfos = variants.collect{it -> it.getVcfInfo()}.flatten()

        ResultSet resultSet = null

        StringBuilder sqlSelectInfo = new StringBuilder("SELECT * FROM vcfinfo WHERE ")
        PreparedStatement pstmt = null
        try {

            props.eachWithIndex { prop, int i ->
                if (i == 0) {
                    sqlSelectInfo.append(prop.name.toLowerCase())
                    sqlSelectInfo.append(" IN ")
                    sqlSelectInfo.append(" (")
                    sqlSelectInfo.append(String.join(",", Collections.nCopies(allVcfInfos.size(), "?")))
                    sqlSelectInfo.append(") ")

                } else {
                    sqlSelectInfo.append("and ")
                    sqlSelectInfo.append(prop.name.toLowerCase())
                    sqlSelectInfo.append(" IN ")
                    sqlSelectInfo.append(" (")
                    sqlSelectInfo.append(String.join(",", Collections.nCopies(allVcfInfos.size(), "?")))
                    sqlSelectInfo.append(") ")
                }
            }

            pstmt = sql.connection.prepareStatement(sqlSelectInfo.toString(),
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY)

            def propPosition = 1
            props.each { prop ->
                allVcfInfos.each { vcfInfo ->
                    def val = null
                    /*
                    if (vcfInfo[prop.name] instanceof Boolean) {
                        val = vcfInfo[prop.name] ?: "''"
                        pstmt.setBoolean(propPosition++, val as Boolean)
                    } else if (vcfInfo[prop.name] instanceof Float) {
                        val = vcfInfo[prop.name] ?: "''"
                        pstmt.setFloat(propPosition++, val as Float)
                    } else if (vcfInfo[prop.name] instanceof Integer) {
                        val = vcfInfo[prop.name] ?: "''"
                        pstmt.setInt(propPosition++, val as Integer)
                    } else if (vcfInfo[prop.name] instanceof Double) {
                        val = vcfInfo[prop.name] ?: "''"
                        pstmt.setDouble(propPosition++, val as Double)
                    } else {
                     */
                    val = vcfInfo[prop.name] ?: ""
                    pstmt.setString(propPosition++, val as String)

                }

            }

            pstmt.setFetchSize(500)
            resultSet = pstmt.executeQuery()

            while (resultSet.next()) {
                def newVcfInfo = new VcfInfo()
                newVcfInfo.ancestralAllele = resultSet.getString("ancestralallele") != "" ? resultSet.getString("ancestralallele") : null
                newVcfInfo.alleleCount = new JsonSlurper().parseText(resultSet.getString("allelecount"))
                newVcfInfo.alleleFrequency = new JsonSlurper().parseText(resultSet.getString("allelefrequency"))

                def newKey = new StringBuilder("")

                props.each { prop -> newKey.append(newVcfInfo.getProperty(prop.name))
                }
                infosFound[newKey.toString()] = resultSet.getInt(1)
            }
        }
        catch (SQLException e) {
            e.printStackTrace()
        }
        finally {
            pstmt.clearBatch()
            pstmt.close()
            sql.close()
        }
        connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        sql = new Sql(connection)
        sql.connection.autoCommit = false

        variants.eachWithIndex { var, int idx ->
            if (ids[var]) {return} else {
                varsToFind.add(var)
                if (varsToFind.size() == BATCH_SIZE || (idx == variants.size() - 1)) {
                    StringBuilder sqlSelect = new StringBuilder("SELECT * FROM variant WHERE variant.chr in (")
                    sqlSelect.append(varsToFind.collect { "'$it.chromosome'" }.join(','))
                    sqlSelect.append(") and variant.start in (")
                    sqlSelect.append(varsToFind.collect { "$it.startPosition" }.join(','))
                    sqlSelect.append(") and variant.end in (")
                    sqlSelect.append(varsToFind.collect { "$it.endPosition" }.join(','))
                    sqlSelect.append(") and variant.ref in (")
                    sqlSelect.append(varsToFind.collect { "'$it.referenceAllele'" }.join(','))
                    sqlSelect.append(") and variant.obs in (")
                    sqlSelect.append(varsToFind.collect { "'$it.observedAllele'" }.join(','))
                    sqlSelect.append(") and variant.issomatic in (")
                    sqlSelect.append(varsToFind.collect { "$it.isSomatic" }.join(','))
                    sqlSelect.append(")")

                    try {
                        pstmt = sql.connection.prepareStatement(
                                sqlSelect.toString(),
                                ResultSet.TYPE_FORWARD_ONLY,
                                ResultSet.CONCUR_READ_ONLY)

                        pstmt.setFetchSize(100)

                        pstmt.execute()
                        resultSet = pstmt.getResultSet()

                        while (resultSet.next()) {

                            def chr = resultSet.getString("chr")
                            def start = resultSet.getString("start")
                            def end = resultSet.getString("end")
                            def ref = resultSet.getString("ref")
                            def obs = resultSet.getString("obs")
                            def issomatic = resultSet.getBoolean("issomatic")
                            varsIdMap["$chr$start$end$ref$obs$issomatic"] = resultSet.getInt("id")
                        }
                    }
                    catch (SQLException e) {
                        e.printStackTrace()
                    }
                    finally {
                        pstmt.close();
                        resultSet.close()
                        sql.close()
                        sqlSelect = null
                    }

                    /*
                    sql.execute(sqlSelect.toString(), { isResultSet, result ->
                        result.each { entry ->
                            varsIdMap["$entry.chr$entry.start$entry.end$entry.ref$entry.obs${entry.issomatic as boolean}"] = entry.id
                        }
                    })
                    */

                    varsToFind.clear()
                }
            }
        }

        variants.each { var ->
            ids[var] = varsIdMap["$var.chromosome$var.startPosition$var.endPosition$var.referenceAllele$var.observedAllele$var.isSomatic"]
            def newKey = new StringBuilder("")
            props.each { prop ->
                newKey.append(var.vcfInfo.getProperty(prop.name))
            }
            infoIds[var] = infosFound[newKey.toString()]
        }
        return new Tuple2(ids, infoIds)
    }


    Tuple2<HashMap, HashMap> tryToFindVariants(ArrayList<SimpleVariantContext> variants) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        def ids = [:]
        def infoIds = [:]
        def infosFound = [:]
        def BATCH_SIZE = 5000

        def varsToFind = new HashSet()
        def varsIdMap = [:]
        def infoValues = []
        def propsToUse = ["ancestralAllele", "alleleCount", "alleleFrequency"]
        def props = VcfInfo.declaredFields.findAll { !it.synthetic && propsToUse.contains(it.name) }.collect { it }

        def allVcfInfos = variants.collect{it -> it.getVcfInfo()}.flatten()
        PreparedStatement pstmt = null
        ResultSet resultSet = null

        allVcfInfos.eachWithIndex { vcfInfo, int idx ->
            if (infosFound[vcfInfo]) {
                return
            } else {
                infoValues.add(vcfInfo)
                if (idx == BATCH_SIZE || (idx == allVcfInfos.size() - 1)) {
                    StringBuilder sqlSelectInfo = new StringBuilder("SELECT * FROM vcfinfo WHERE ")

                    props.eachWithIndex { prop, int i ->
                        if (i == 0) {
                            sqlSelectInfo.append(buildMultiSelectString("vcfinfo.${prop.name.toLowerCase()}", infoValues, prop.name))

                        } else {
                            sqlSelectInfo.append("and ")
                            sqlSelectInfo.append(buildMultiSelectString("vcfinfo.${prop.name.toLowerCase()}", infoValues, prop.name))
                        }
                    }

                    infoValues = []


                    try {
                        pstmt = sql.connection.prepareStatement(
                                sqlSelectInfo.toString(),
                                ResultSet.TYPE_FORWARD_ONLY,
                                ResultSet.CONCUR_READ_ONLY)

                        pstmt.setFetchSize(1000)
                        pstmt.execute()
                        resultSet = pstmt.getResultSet()

                        while (resultSet.next()) {
                            def newVcfInfo = new VcfInfo()
                            newVcfInfo.ancestralAllele = resultSet.getString("ancestralallele") != "" ? resultSet.getString("ancestralallele") : null
                            newVcfInfo.alleleCount = new JsonSlurper().parseText(resultSet.getString("allelecount"))
                            newVcfInfo.alleleFrequency = new JsonSlurper().parseText(resultSet.getString("allelefrequency"))

                            def newKey = new StringBuilder("")

                            props.each {
                                prop -> newKey.append(newVcfInfo.getProperty(prop.name))
                            }
                            infosFound[newKey.toString()] = resultSet.getInt(1)
                        }
                        sqlSelectInfo = null
                    }
                    catch (SQLException e) {
                        e.printStackTrace()
                    }
                    finally {
                        pstmt.close();
                        resultSet.close()
                        sql.close()
                    }
                }
            }
        }

        connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        sql = new Sql(connection)
        sql.connection.autoCommit = false

        variants.eachWithIndex { var, int idx ->
            if (ids[var]) {return} else {
                varsToFind.add(var)
                if (varsToFind.size() == BATCH_SIZE || (idx == variants.size() - 1)) {
                    StringBuilder sqlSelect = new StringBuilder("SELECT * FROM variant WHERE variant.chr in (")
                    sqlSelect.append(varsToFind.collect { "'$it.chromosome'" }.join(','))
                    sqlSelect.append(") and variant.start in (")
                    sqlSelect.append(varsToFind.collect { "$it.startPosition" }.join(','))
                    sqlSelect.append(") and variant.end in (")
                    sqlSelect.append(varsToFind.collect { "$it.endPosition" }.join(','))
                    sqlSelect.append(") and variant.ref in (")
                    sqlSelect.append(varsToFind.collect { "'$it.referenceAllele'" }.join(','))
                    sqlSelect.append(") and variant.obs in (")
                    sqlSelect.append(varsToFind.collect { "'$it.observedAllele'" }.join(','))
                    sqlSelect.append(") and variant.issomatic in (")
                    sqlSelect.append(varsToFind.collect { "$it.isSomatic" }.join(','))
                    sqlSelect.append(")")

                    try {
                        pstmt = sql.connection.prepareStatement(
                                sqlSelect.toString(),
                                ResultSet.TYPE_FORWARD_ONLY,
                                ResultSet.CONCUR_READ_ONLY)

                        pstmt.setFetchSize(1000)

                        pstmt.execute()
                        resultSet = pstmt.getResultSet()

                        while (resultSet.next()) {

                            def chr = resultSet.getString("chr")
                            def start = resultSet.getString("start")
                            def end = resultSet.getString("end")
                            def ref = resultSet.getString("ref")
                            def obs = resultSet.getString("obs")
                            def issomatic = resultSet.getBoolean("issomatic")
                            varsIdMap["$chr$start$end$ref$obs$issomatic"] = resultSet.getInt("id")
                        }
                    }
                    catch (SQLException e) {
                        e.printStackTrace()
                    }
                    finally {
                        pstmt.close();
                        resultSet.close()
                        sql.close()
                        sqlSelect = null
                    }

                    /*
                    sql.execute(sqlSelect.toString(), { isResultSet, result ->
                        result.each { entry ->
                            varsIdMap["$entry.chr$entry.start$entry.end$entry.ref$entry.obs${entry.issomatic as boolean}"] = entry.id
                        }
                    })
                    */

                    varsToFind.clear()
                }
            }
        }

        variants.each { var ->
            ids[var] = varsIdMap["$var.chromosome$var.startPosition$var.endPosition$var.referenceAllele$var.observedAllele$var.isSomatic"]
            def newKey = new StringBuilder("")
            props.each { prop ->
                newKey.append(var.vcfInfo.getProperty(prop.name))
            }
            infoIds[var] = infosFound[newKey.toString()]
        }
        return new Tuple2(ids, infoIds)
    }

    HashMap tryToFindGenotypes(ArrayList<SimpleVariantContext> variants, Map sampleGenotypeMapping) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        def ids = [:].withDefault { [:] }
        def genotype_ids = [:]

        variants.each { var ->
            def props = Genotype.declaredFields.findAll { !it.synthetic && it.name != "sampleName" }.collect { it }
            // iterate over given genotypes of variant
            var.genotypes.each { genotype ->
                def genotypeValues = []
                props.each { prop -> genotypeValues.add(genotype.properties.get(prop.name)) }

                def result = null

                if (!genotype.sampleName) {
                    sampleGenotypeMapping.keySet().each { sampleIdent ->
                        // no genotype information is available
                        if (genotypeValues.every { it == null }) {
                            // we have seen the same genotype in this registration process
                            if (genotype_ids[genotype]) {
                                ids[var][sampleIdent] = genotype_ids[genotype]
                            } else {
                                // search for genotype in database
                                result = sql.firstRow("""SELECT id FROM genotype WHERE genotype
.genotype IS NULL and genotype.readdepth IS NULL and genotype.filter IS NULL and genotype.likelihoods IS NULL and 
genotype.genotypelikelihoods IS NULL and genotype.genotypelikelihoodshet IS NULL and genotype.posteriorprobs IS NULL and 
genotype.genotypequality IS NULL and genotype.haplotypequalities IS NULL and genotype.phaseset IS NULL and genotype.phasingquality IS NULL and genotype.alternateallelecounts IS NULL and 
genotype.mappingquality IS NULL""")
                                ids[var][sampleIdent] = result.id
                                genotype_ids[genotype] = result.id
                            }
                        }
                        else {
                            log.error("Incompatible sample/genotype information provided.")
                        }
                    }

                } else {
                    def sample = sampleGenotypeMapping[genotype.sampleName] as Sample
                    def sampleIdent = sample ? sample.identifier : ""

                    // no genotype information is available
                    if (genotypeValues.every { it == null }) {
                        // we have seen the same genotype in this registration process
                        if (genotype_ids[genotype]) {
                            ids[var][sampleIdent] = genotype_ids[genotype]
                        } else {
                            // search for genotype in database
                            result = sql.firstRow("""SELECT id FROM genotype WHERE genotype
.genotype IS NULL and genotype.readdepth IS NULL and genotype.filter IS NULL and genotype.likelihoods IS NULL and 
genotype.genotypelikelihoods IS NULL and genotype.genotypelikelihoodshet IS NULL and genotype.posteriorprobs IS NULL and 
genotype.genotypequality IS NULL and genotype.haplotypequalities IS NULL and genotype.phaseset IS NULL and genotype.phasingquality IS NULL and genotype.alternateallelecounts IS NULL and 
genotype.mappingquality IS NULL""")
                            ids[var][sampleIdent] = result.id
                            genotype_ids[genotype] = result.id
                        }
                    }

                    // genotype information available
                    else {
                        // we have seen the same genotype in this registration process
                        if (genotype_ids[genotype]) {
                            ids[var][sampleIdent] = genotype_ids[genotype]
                        } else {
                            // search for genotype in database
                            result = sql.firstRow("""SELECT id FROM genotype WHERE genotype
.genotype=? and genotype.readdepth=? and genotype.filter=? and genotype.likelihoods=? and genotype
.genotypelikelihoods=? and genotype.genotypelikelihoodshet=? and genotype.posteriorprobs=? and genotype
.genotypequality=? and genotype.haplotypequalities=? and genotype.phaseset=? and genotype.phasingQuality=? and 
genotype.alternateallelecounts=? and 
genotype.mappingquality=?""",
                                    genotypeValues)
                            ids[var][sampleIdent] = result.id
                            genotype_ids[genotype] = result.id
                        }
                    }
                }
            }
        }
        sql.close()
        return ids
    }

    Tuple2<HashMap, HashMap> tryToFindConsequences(ArrayList<SimpleVariantContext> variants) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        def ids = [:]
        def consIdMap = [:]
        def BATCH_SIZE = 5000
        def consToFind = new ArrayList<>()

        ArrayList<Consequence> allConsequences = variants.collect{it -> it.getConsequences()}.flatten()

        PreparedStatement pstmt = null
        try {
            def selectString = new StringBuilder("SELECT id, allele, codingchange, aachange, transcriptid, transcriptversion FROM consequence WHERE consequence.allele in (")
            selectString.append(String.join(",", Collections.nCopies(allConsequences.size(), "?")) + ")")
            selectString.append(" and consequence.codingchange in (")
            selectString.append(String.join(",", Collections.nCopies(allConsequences.size(), "?")) + ")")
            selectString.append(" and consequence.aachange in (")
            selectString.append(String.join(",", Collections.nCopies(allConsequences.size(), "?")) + ")")
            selectString.append(" and consequence.transcriptid in (")
            selectString.append(String.join(",", Collections.nCopies(allConsequences.size(), "?")) + ")")

            pstmt = sql.connection.prepareStatement(selectString.toString())
            def length = allConsequences.size()
            allConsequences.eachWithIndex{ consequence, int i ->
                pstmt.setString(i+1, consequence.allele)
                pstmt.setString(i+1+length, consequence.codingChange)
                pstmt.setString(i+1+length+length, consequence.aaChange)
                pstmt.setString(i+1+length+length+length, consequence.transcriptId)
            }

            ResultSet rs = pstmt.executeQuery()
            def resultString = null
            while(rs.next()) {
                resultString = new StringBuilder()
                resultString.append(rs.getString("allele"))
                resultString.append(rs.getString("codingchange"))
                resultString.append(rs.getString("aachange"))
                resultString.append(rs.getString("transcriptid"))
                resultString.append(rs.getInt("transcriptversion"))
                consIdMap[resultString.toString()] = rs.getInt("id")
            }
        }
        catch (SQLException e) {
            e.printStackTrace()
        }
        finally {
            //pstmt.clearBatch()
            pstmt.close()
            sql.close()
        }

        /*
        allConsequences.eachWithIndex { consequence, int idx ->
            if (consIdMap[consequence]) {
                return
            } else {
                consToFind.add(consequence)
                if (consToFind.size() == BATCH_SIZE) {
                    StringBuilder sqlSelect = new StringBuilder("SELECT id, allele, codingchange, aachange, transcriptid, transcriptversion FROM consequence WHERE consequence.allele in (")
                    sqlSelect.append(consToFind.collect { "'$it.allele'" }.join(','))
                    sqlSelect.append(") and consequence.codingchange in (")
                    sqlSelect.append(consToFind.collect { "'$it.codingChange'" }.join(','))
                    sqlSelect.append(") and consequence.aachange in (")
                    sqlSelect.append(consToFind.collect { "'$it.aaChange'" }.join(','))
                    sqlSelect.append(") and consequence.transcriptid in (")
                    sqlSelect.append(consToFind.collect { "'$it.transcriptId'" }.join(','))
                    sqlSelect.append(")")

                    sql.execute(sqlSelect.toString(), { isResultSet, result ->
                        result.each { entry ->
                            consIdMap["$entry.allele$entry.codingchange$entry.aachange$entry.transcriptid$entry.transcriptversion"] = entry.id
                        }
                    })
                    consToFind.clear()
                } else if (idx == allConsequences.size() - 1) {
                    StringBuilder sqlSelect = new StringBuilder("SELECT id, allele, codingchange, aachange, transcriptid, transcriptversion FROM consequence WHERE consequence.allele in (")
                    sqlSelect.append(consToFind.collect { "'$it.allele'" }.join(','))
                    sqlSelect.append(") and consequence.codingchange in (")
                    sqlSelect.append(consToFind.collect { "'$it.codingChange'" }.join(','))
                    sqlSelect.append(") and consequence.aachange in (")
                    sqlSelect.append(consToFind.collect { "'$it.aaChange'" }.join(','))
                    sqlSelect.append(") and consequence.transcriptid in (")
                    sqlSelect.append(consToFind.collect { "'$it.transcriptId'" }.join(','))
                    sqlSelect.append(")")

                    sql.execute(sqlSelect.toString(), { isResultSet, result ->
                        result.each { entry ->

                            consIdMap["$entry.allele$entry.codingchange$entry.aachange$entry.transcriptid$entry.transcriptversion"] = entry.id
                        }
                    })
                    consToFind.clear()
                }
            }
        }
        */

        variants.each { var ->
            ids[var] = var.getConsequences().collect {it ->
                def queryString = new StringBuilder()
                queryString.append(it.allele)
                queryString.append(it.codingChange)
                queryString.append(it.aaChange)
                queryString.append(it.transcriptId)
                queryString.append(it.transcriptVersion)

                //consIdMap["$it.allele$it.codingChange$it.aaChange$it.transcriptId$it.transcriptVersion"]}
                consIdMap[queryString.toString()]}

            }
        consToFind.clear()
        return new Tuple2(ids, consIdMap)
    }

    Tuple2<HashMap, HashMap> tryToFindConsequences2(ArrayList<SimpleVariantContext> variants) {
        def ids = [:]
        def consIdMap = [:]
        def BATCH_SIZE = 5000
        def consToFind = new ArrayList<>()

        def allConsequences = variants.collect{it -> it.getConsequences()}.flatten()

        allConsequences.eachWithIndex { consequence, int idx ->
            if (consIdMap[consequence]) {
                return
            } else {
                consToFind.add(consequence)
                if (consToFind.size() == BATCH_SIZE) {
                    StringBuilder sqlSelect = new StringBuilder("SELECT id, allele, codingchange, aachange, transcriptid, transcriptversion FROM consequence WHERE consequence.allele in (")
                    sqlSelect.append(consToFind.collect { "'$it.allele'" }.join(','))
                    sqlSelect.append(") and consequence.codingchange in (")
                    sqlSelect.append(consToFind.collect { "'$it.codingChange'" }.join(','))
                    sqlSelect.append(") and consequence.aachange in (")
                    sqlSelect.append(consToFind.collect { "'$it.aaChange'" }.join(','))
                    sqlSelect.append(") and consequence.transcriptid in (")
                    sqlSelect.append(consToFind.collect { "'$it.transcriptId'" }.join(','))
                    sqlSelect.append(")")

                    sql.execute(sqlSelect.toString(), { isResultSet, result ->
                        result.each { entry ->
                            consIdMap["$entry.allele$entry.codingchange$entry.aachange$entry.transcriptid$entry.transcriptversion"] = entry.id
                        }
                    })
                    consToFind.clear()
                } else if (idx == allConsequences.size() - 1) {
                    StringBuilder sqlSelect = new StringBuilder("SELECT id, allele, codingchange, aachange, transcriptid, transcriptversion FROM consequence WHERE consequence.allele in (")
                    sqlSelect.append(consToFind.collect { "'$it.allele'" }.join(','))
                    sqlSelect.append(") and consequence.codingchange in (")
                    sqlSelect.append(consToFind.collect { "'$it.codingChange'" }.join(','))
                    sqlSelect.append(") and consequence.aachange in (")
                    sqlSelect.append(consToFind.collect { "'$it.aaChange'" }.join(','))
                    sqlSelect.append(") and consequence.transcriptid in (")
                    sqlSelect.append(consToFind.collect { "'$it.transcriptId'" }.join(','))
                    sqlSelect.append(")")

                    sql.execute(sqlSelect.toString(), { isResultSet, result ->
                        result.each { entry ->
                            /*
                            def newCons = new Consequence(entry.allele, entry.codingchange, entry.transcriptid, entry.transcriptversion,
                             entry.type, entry.biotype, entry.canonical as boolean, entry.aachange, entry.cdnaposition, entry.cdsposition,
                                    entry.proteinposition, entry.proteinlength, entry.cdnalength, entry.cdslength, entry.impact,
                                    entry.exon, entry.intron, entry.strand, entry.genesymbol, entry.featuretype, entry.distance, entry.warnings)
                             */

                            consIdMap["$entry.allele$entry.codingchange$entry.aachange$entry.transcriptid$entry.transcriptversion"] = entry.id
                        }
                    })
                    consToFind.clear()
                }
            }
        }

        variants.each { var ->
            ids[var] = var.getConsequences().collect {it ->
                consIdMap["$it.allele$it.codingChange$it.aaChange$it.transcriptId$it.transcriptVersion"]}
        }
        consToFind.clear()
        return new Tuple2(ids, consIdMap)
    }

    HashMap tryToFindGenes(List<Gene> genes) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        def ids = [:]

        genes.each { gene ->
            if (ids[gene]) {
                return
            } else {
                def result =
                        sql.firstRow("SELECT id FROM gene WHERE gene.symbol=? and gene.name=? and gene.biotype=? and gene.chr=? and gene.start=? and gene.end=? and gene.synonyms=? and gene.geneid=? and gene.description=? and gene.strand=? and gene.version=?",

                                [gene.symbol, gene.name, gene.bioType, gene.chromosome, gene.geneStart, gene.geneEnd,
                                 gene.synonyms[0], gene.geneId, gene.description, gene.strand, gene.version])
                ids[gene] = result.id
            }
        }
        sql.close()
        return ids
    }

    HashMap tryToFindSamples(Map samples, String cId) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        def ids = [:].withDefault { [:] }

        samples.each { entry ->
            def sample = entry.value
                if (ids[sample]) {
                    return
                } else {

                    def result =
                            sql.firstRow("SELECT id FROM sample WHERE sample.identifier=? and sample.entity_id=? and sample.cancerentity=?",

                                    [sample.identifier, cId, sample.cancerEntity])
                    ids[sample] = result.id
                }
        }
        sql.close()
        return ids
    }

    private HashMap<Consequence, List<String>> tryToFindGenesByConsequence2(HashMap<Consequence, List<String>>
                                                                                   consequenceToGeneIds) {
        def ids = [:]
        def foundIds = [:]
        def result = null

        consequenceToGeneIds.each { cons, geneIds ->
            def geneDBids = []
            geneIds.each { geneId ->
                if (foundIds[geneId]) {
                    geneDBids.add(foundIds[geneId])
                } else {
                    result = sql.firstRow("SELECT id FROM gene WHERE gene.geneid=?", [geneId])
                    geneDBids.add(result.id)
                    foundIds[geneId] = result.id
                }
            }

            ids[cons] = geneDBids
        }
        return ids as HashMap<Consequence, List<String>>
    }

    private HashMap<Consequence, List<String>> tryToFindGenesByConsequence(HashMap<Consequence, List<String>>
                                                                                   consequenceToGeneIds) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        def ids = [:]
        def foundIds = [:]
        def BATCH_SIZE = 50000
        def genesToFind = new HashSet()

        def allGeneIds = new HashSet<String>()
        allGeneIds.addAll(consequenceToGeneIds.values().collect().flatten())

/*

        allGeneIds.eachWithIndex{ geneId, int idx ->
            if (foundIds[geneId]) {
                return
            }
            else {
                genesToFind.add(geneId)
                if (genesToFind.size() == BATCH_SIZE || (idx ==  allGeneIds.size()-1)) {
                    StringBuilder sqlSelect = new StringBuilder("select id, geneid FROM gene where gene.geneid in (")
                    sqlSelect.append(genesToFind.collect { "'$it'" }.join( ',' ))
                    sqlSelect.append(")")
                    sql.execute(sqlSelect.toString(), { isResultSet, result ->
                        result.each { entry ->
                            foundIds[entry.geneId] = entry.id
                        }
                    })
                    genesToFind = new HashSet()
                }
            }
        }
        */


        PreparedStatement pstmt = null
        try {
            pstmt = sql.connection.prepareStatement("select id, geneid FROM gene where gene.geneid in (" + String.join(",", Collections.nCopies(allGeneIds.size(), "?")) + ")")

            allGeneIds.eachWithIndex{ identifier, int i ->
                pstmt.setString(i+1 , identifier)
            }

            ResultSet rs = pstmt.executeQuery()
            while(rs.next()) {
                rs.getString("geneid")
                foundIds[rs.getString("geneid")] = rs.getInt("id")
            }
        }
        catch (SQLException e) {
            e.printStackTrace()
        }
        finally {
            pstmt.clearBatch()
            pstmt.close()
            sql.close()
        }

        consequenceToGeneIds.each { cons, geneIds ->
            ids["$cons.allele$cons.codingChange$cons.aaChange$cons.transcriptId$cons.transcriptVersion"] = geneIds.collect {it ->
                foundIds[it]}
        }
        return ids as HashMap<Consequence, List<String>>
    }


    private List<Case> fetchCaseForId(String id, Sql sql) {
        def result = sql.rows("""SELECT distinct entity.id, project_id FROM entity WHERE entity.id=$id;""")
        List<Case> cases = result.collect { convertRowResultToCase(it) }
        return cases
    }

    private List<Sample> fetchSampleForId(String id, Sql sql) {
        def result = sql.rows("""SELECT * FROM sample WHERE sample.identifier=$id;""")
        List<Sample> sample = result.collect { convertRowResultToSample(it) }
        return sample
    }

    private List<Variant> fetchVariantForId(String id, Sql sql) {
        def result = sql.rows("""SELECT variant.id as varid, variant.chr as varchr, variant.start as varstart, 
variant.end as varend, variant.ref as varref, variant.obs as varobs, variant.issomatic as varsomatic, variant.uuid as
 varuuid, variant.databaseidentifier as vardbid, consequence.*, gene.id as geneindex, gene.geneid as geneid FROM variant INNER JOIN variant_has_consequence 
 ON variant.id = variant_has_consequence.variant_id INNER JOIN consequence on variant_has_consequence.consequence_id 
 = consequence.id INNER JOIN consequence_has_gene on consequence_has_gene.consequence_id = consequence.id INNER JOIN 
 gene on gene.id=consequence_has_gene.gene_id WHERE variant.uuid=$id;""")
        return parseVariantQueryResult(result, true)
    }

    private List<Gene> fetchGeneForId(String id, Sql sql) {
        def result = sql.rows("""SELECT distinct * FROM gene WHERE gene.geneid=$id;""")
        List<Gene> genes = result.collect { convertRowResultToGene(it) }
        return genes
    }

    private List<Gene> fetchGeneForIdWithEnsemblVersion(String id, Integer ensemblVersion, Sql sql) {
        def result = sql.rows("""SELECT distinct * FROM gene INNER JOIN ensembl_has_gene ON gene.id = 
Ensembl_has_gene.gene_id INNER JOIN ensembl ON ensembl_has_gene.ensembl_id = ensembl.id WHERE gene.geneid=$id and 
ensembl.version=$ensemblVersion;""")
        List<Gene> genes = result.collect { convertRowResultToGene(it) }
        return genes
    }

    private List<Case> fetchCases(Sql sql) {
        def result = sql.rows("SELECT * FROM entity;")
        List<Case> cases = result.collect { convertRowResultToCase(it) }
        return cases
    }

    private List<Sample> fetchSamples(Sql sql) {
        def result = sql.rows("SELECT * FROM sample;")
        List<Sample> samples = result.collect { convertRowResultToSample(it) }
        return samples
    }

    private List<Variant> fetchVariants(referenceGenome,withConsequences, annotationSoftware, withVcInfo, withGenotypes, Sql sql) {
        def result

        if (withConsequences & withGenotypes) {
            // we will fetch VcfInfo information as well since this case is always VCF output format
            result = sql.rows(selectVariantsWithConsequencesAndGenotypes.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND annotationsoftware.name='${annotationSoftware}';"))
        } else if (withConsequences) {
            if (withVcInfo) {
                result = sql.rows(selectVariantsWithConsequencesAndVcfInfo.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND annotationsoftware.name='${annotationSoftware}';"))
            } else {
                result = sql.rows(selectVariantsWithConsequences.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND annotationsoftware.name='${annotationSoftware}';"))
            }
        } else {
            if (withVcInfo) {
                result = sql.rows(selectVariantsWithVcfInfo.replace(";", " WHERE referencegenome.build='${referenceGenome}';"))
            } else {
                result = sql.rows(selectVariants.replace(";", " WHERE referencegenome.build='${referenceGenome}';"))
            }
        }
        return parseVariantQueryResult(result, withConsequences, withVcInfo, withGenotypes)
    }

    private List<Gene> fetchGenes(Sql sql) {
        def result = sql.rows("SELECT * FROM gene;")
        List<Gene> genes = result.collect { convertRowResultToGene(it) }
        return genes
    }

    private List<Case> fetchCasesByGene(String gene, Sql sql) {
        def result = sql.rows("""select distinct entity.id, project_id,genesymbol from entity INNER JOIN sample ON entity.id = sample.entity_id INNER JOIN sample_has_variant ON sample.id = sample_has_variant.sample_id INNER JOIN variant ON variant.id = sample_has_variant.variant_id INNER JOIN variant_has_consequence ON variant_has_consequence.variant_id = variant.id INNER JOIN consequence on variant_has_consequence.consequence_id = consequence.id WHERE genesymbol=$gene;""")
        List<Case> cases = result.collect { convertRowResultToCase(it) }
        return cases
    }

    private List<Case> fetchCasesByGeneAndConsequenceType(String gene, String consequenceType, Sql sql) {
        def result = sql.rows("""select distinct entity.id, project_id,genesymbol, type from entity INNER JOIN sample ON entity.id = sample.entity_id INNER JOIN sample_has_variant ON sample.id = sample_has_variant.sample_id INNER JOIN variant ON variant.id = sample_has_variant.variant_id INNER JOIN variant_has_consequence ON variant_has_consequence.variant_id = variant.id INNER JOIN consequence on variant_has_consequence.consequence_id = consequence.id WHERE type=$consequenceType AND genesymbol=$gene;""")
        List<Case> cases = result.collect { convertRowResultToCase(it) }
        return cases
    }

    private List<Case> fetchCasesByConsequenceType(String consequenceType, Sql sql) {
        def result = sql.rows("""select distinct entity.id, project_id, type from entity INNER JOIN sample ON entity.id = sample.entity_id INNER JOIN sample_has_variant ON sample.id = sample_has_variant.sample_id INNER JOIN variant ON variant.id = sample_has_variant.variant_id INNER JOIN variant_has_consequence ON variant_has_consequence.variant_id = variant.id INNER JOIN consequence on variant_has_consequence.consequence_id = consequence.id where type==$consequenceType;""")
        List<Case> cases = result.collect { convertRowResultToCase(it) }
        return cases
    }

    private List<Case> fetchCasesByChromosome(String chromosome, Sql sql) {
        def result = sql.rows("""select distinct entity.id, project_id from entity INNER JOIN sample ON entity.id = 
sample.entity_id INNER JOIN sample_has_variant ON sample.id = sample_has_variant.sample_id INNER JOIN
 variant ON variant.id = sample_has_variant.variant_id where variant.chr=$chromosome;""")
        List<Case> cases = result.collect { convertRowResultToCase(it) }
        return cases
    }

    private List<Case> fetchCasesByChromosomeAndPositionRange(String chromosome, BigInteger startPosition, BigInteger
            endPosition, Sql sql) {
        def result = sql.rows("""select distinct entity.id, project_id from entity INNER JOIN sample ON entity.id = 
sample.entity_id INNER JOIN sample_has_variant ON sample.id = sample_has_variant.sample_id INNER JOIN
 variant ON variant.id = sample_has_variant.variant_id where variant.chr=$chromosome AND variant.start>=$startPosition AND variant.end<=$endPosition;""")
        List<Case> cases = result.collect { convertRowResultToCase(it) }
        return cases
    }

    private List<Sample> fetchSamplesByCancerEntity(String entity, Sql sql) {
        def result = sql.rows("""SELECT * FROM sample WHERE sample.cancerentity=$entity;""")
        List<Sample> samples = result.collect { convertRowResultToSample(it) }
        return samples
    }

    private List<Variant> fetchVariantsByChromosomeAndStartPosition(String chromosome, BigInteger start, String
            referenceGenome, Boolean withConsequences, String annotationSoftware, Boolean withVcInfo, Boolean
            withGenotypes, Sql sql) {
        def result

        if (withConsequences & withGenotypes) {
            // we will fetch VcfInfo information as well since this case is always VCF output format
            result = sql.rows(selectVariantsWithConsequencesAndGenotypes.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND annotationsoftware.name='${annotationSoftware}' AND variant.chr='${chromosome}' AND variant.start=$start;"))
        } else if (withConsequences) {
            if (withVcInfo) {
                result = sql.rows(selectVariantsWithConsequencesAndVcfInfo.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND annotationsoftware.name='${annotationSoftware}' AND variant.chr='${chromosome}' AND variant.start=$start;"))
            } else {
                result = sql.rows(selectVariantsWithConsequences.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND annotationsoftware.name='${annotationSoftware}' AND variant.chr='${chromosome}' AND variant.start=$start;"))
            }
        } else {
            if (withVcInfo) {
                result = sql.rows(selectVariantsWithVcfInfo.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND annotationsoftware.name='${annotationSoftware}' AND variant.chr='${chromosome}' AND variant.start=$start;"))
            }
            else {
                result = sql.rows(selectVariants.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND variant.chr='${chromosome}' AND variant.start=$start;"))
            }
        }
        return parseVariantQueryResult(result, withConsequences, withVcInfo, withGenotypes)
    }

    private List<Variant> fetchVariantsByChromosome(String chromosome, String referenceGenome, Boolean
            withConsequences, String annotationSoftware, Boolean withVcInfo, Boolean withGenotypes, Sql sql) {
        def result
        if (withConsequences & withGenotypes) {
            // we will fetch VcfInfo information as well since this case is always VCF output format
            result = sql.rows(selectVariantsWithConsequencesAndGenotypes.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND annotationsoftware.name='${annotationSoftware}' AND variant.chr='${chromosome}';"))
        } else if (withConsequences) {
            if (withVcInfo) {
                result = sql.rows(selectVariantsWithConsequencesAndVcfInfo.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND annotationsoftware.name='${annotationSoftware}' AND variant.chr='${chromosome}';"))
            } else {
                result = sql.rows(selectVariantsWithConsequences.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND annotationsoftware.name='${annotationSoftware}' AND variant.chr='${chromosome}';"))
            }
        } else {
            if (withVcInfo) {
                result = sql.rows(selectVariantsWithVcfInfo.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND variant.chr='${chromosome}';"))
            } else {
                result = sql.rows(selectVariants.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND variant.chr='${chromosome}';"))
            }
        }
        return parseVariantQueryResult(result, withConsequences, withVcInfo, withGenotypes)
    }

    private List<Variant> fetchVariantsByStartPosition(BigInteger start, String referenceGenome, Boolean
            withConsequences, String annotationSoftware, Boolean withVcInfo, Boolean withGenotypes, Sql sql) {
        def result

        if (withConsequences & withGenotypes) {
            // we will fetch VcfInfo information as well since this case is always VCF output format
            result = sql.rows(selectVariantsWithConsequencesAndGenotypes.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND annotationsoftware.name='${annotationSoftware}' AND variant.start=$start;"))
        } else if (withConsequences) {
            if (withVcInfo) {
                result = sql.rows(selectVariantsWithConsequencesAndVcfInfo.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND annotationsoftware.name='${annotationSoftware}' AND variant.start=$start;"))
            } else {
                result = sql.rows(selectVariantsWithConsequences.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND annotationsoftware.name='${annotationSoftware}' AND variant.start=$start;"))
            }
        } else {
            if (withVcInfo) {
                result = sql.rows(selectVariantsWithVcfInfo.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND annotationsoftware.name='${annotationSoftware}' AND variant.start=$start;"))
            }
            else {
                result = sql.rows(selectVariants.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND variant.start=$start;"))
            }
        }
        return parseVariantQueryResult(result, withConsequences, withVcInfo, withGenotypes)
    }

    private List<Variant> fetchVariantsBySample(String sampleId, String referenceGenome, Boolean withConsequences,
                                                String annotationSoftware, Boolean withVcInfo, Boolean withGenotypes, Sql sql) {
        def result
        if (withConsequences & withGenotypes) {
            // we will fetch VcfInfo information as well since this case is always VCF output format
            result = sql.rows(selectVariantsWithConsequencesAndGenotypes.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND annotationsoftware.name='${annotationSoftware}' AND Sample_identifier='${sampleId}';"))
        } else if (withConsequences) {
            if (withVcInfo) {
                result = sql.rows(selectVariantsWithConsequencesAndVcfInfo.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND annotationsoftware.name='${annotationSoftware}' AND Sample_identifier='${sampleId}';"))
            } else {
                result = sql.rows(selectVariantsWithConsequences.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND annotationsoftware.name='${annotationSoftware}' AND Sample_identifier='${sampleId}';"))
            }
        } else {
            if (withVcInfo) {
                result = sql.rows(selectVariantsWithVcfInfo.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND annotationsoftware.name='${annotationSoftware}' AND Sample_identifier='${sampleId}';"))

            }
            else {
                result = sql.rows(selectVariants.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND Sample_identifier='${sampleId}';"))
            }
        }
        return parseVariantQueryResult(result, withConsequences, withVcInfo, withGenotypes)
    }

    private List<Variant> fetchVariantsBySampleAndGeneId(String sampleId, String geneId, String referenceGenome,
                                                         Boolean withConsequences, String annotationSoftware, Boolean
                                                                 withVcInfo, Boolean withGenotypes, Sql sql) {
        def result
        if (withConsequences & withGenotypes) {
            // we will fetch VcfInfo information as well since this case is always VCF output format
            result = sql.rows(selectVariantsWithConsequencesAndGenotypes.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND annotationsoftware.name='${annotationSoftware}' AND Sample_identifier = '${sampleId}' AND geneid='${geneId}';"))
        } else if (withConsequences) {
            if (withVcInfo) {
                result = sql.rows(selectVariantsWithConsequencesAndVcfInfo.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND annotationsoftware.name='${annotationSoftware}' AND Sample_identifier = '${sampleId}' AND geneid='${geneId}';"))
            } else {
                result = sql.rows(selectVariantsWithConsequences.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND annotationsoftware.name='${annotationSoftware}' AND Sample_identifier = '${sampleId}' AND geneid='${geneId}';"))
            }
        } else {
            if (withVcInfo) {
                result = sql.rows(selectVariantsWithVcfInfo.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND annotationsoftware.name='${annotationSoftware}' AND Sample_identifier = '${sampleId}' AND geneid='${geneId}';"))
            }
            else {
                result = sql.rows(selectVariants.replace(";", " WHERE referencegenome.build='${referenceGenome}' AND Sample_identifier = '${sampleId}' AND geneid='${geneId}';"))
            }
        }
        return parseVariantQueryResult(result, withConsequences, withVcInfo, withGenotypes)
    }

    private List<Variant> fetchVariantsByGeneId(String geneId, String referenceGenome, Boolean withConsequences,
                                                String annotationSoftware, Boolean withVcInfo, Boolean withGenotypes, Sql sql) {
        def result

        if (withConsequences & withGenotypes) {
            // we will fetch VcfInfo information as well since this case is always VCF output format
            result = sql.rows(selectVariantsWithConsequencesAndGenotypes.replace(";", """ WHERE referencegenome
.build='${referenceGenome}' AND annotationsoftware.name='${annotationSoftware}' AND geneid='${geneId}';"""))
        } else if (withConsequences) {
            if (withVcInfo) {
                result = sql.rows(selectVariantsWithConsequencesAndVcfInfo.replace(";", """ WHERE referencegenome
.build='${referenceGenome}' AND annotationsoftware.name='${annotationSoftware}' AND geneid='${geneId}';"""))
            } else {
                result = sql.rows(selectVariantsWithConsequences.replace(";", """ WHERE referencegenome
.build='${referenceGenome}' AND annotationsoftware.name='${annotationSoftware}' AND geneid='${geneId}';"""))
            }
        } else {
            if (withVcInfo) {
                result = sql.rows(selectVariantsWithVcfInfo.replace(";", """ WHERE referencegenome.build='${referenceGenome}' AND 
annotationsoftware.name='${annotationSoftware}' AND geneid='${geneId}';"""))
            }
            else {
                result = sql.rows(selectVariants.replace(";", """ WHERE referencegenome.build='${referenceGenome}' AND geneid='${geneId}';"""))
            }
        }
        return parseVariantQueryResult(result, withConsequences, withVcInfo, withGenotypes)
    }

    private List<Variant> fetchVariantsForBeaconResponse(String chromosome, BigInteger start,
                                                         String reference, String observed, String assemblyId, Sql sql) {

        //TODO check if we might get unprecise results due to like
        def obs = "%" + observed + "%"
        def ref = "%" + reference + "%"

        def result = sql.rows("""SELECT variant.id as varid, variant.chr as varchr, variant.start as varstart, 
variant.end as varend, variant.ref as varref, variant.obs as varobs, variant.issomatic as varsomatic, variant.uuid as
 varuuid, variant.databaseidentifier as vardbid FROM variant INNER JOIN variant_has_referencegenome ON variant.id = variant_has_referencegenome.variant_id 
 INNER JOIN referencegenome on variant_has_referencegenome.referencegenome_id = referencegenome.id where 
 referencegenome.build=$assemblyId and variant.chr=$chromosome and variant.start=$start and variant.ref LIKE ${ref} 
and variant.obs LIKE ${obs};""")
        return parseVariantQueryResult(result, false)
    }

    private List<Gene> fetchGenesBySample(String sampleId, Sql sql) {
        def result = sql.rows(""" SELECT gene.*, Sample_has_variant.* FROM gene INNER JOIN consequence_has_gene ON 
gene.id = consequence_has_gene.gene_id INNER JOIN consequence on consequence_has_gene.consequence_id = consequence.id
 INNER JOIN variant_has_consequence on variant_has_consequence.consequence_id = consequence.id INNER JOIN variant ON 
 variant_has_consequence.variant_id = variant.id INNER JOIN sample_has_variant ON sample_has_variant.variant_id = 
 variant.id WHERE sample_id=$sampleId;""")
        List<Gene> genes = result.collect { convertRowResultToGene(it) }
        return genes
    }

    void tryToStoreJunctionBatch(Object id, List ids, String insertStatement) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        sql.withBatch( insertStatement) { ps ->
            ids.each { key2 ->
                if (id instanceof String) id = (String) id else id = (Integer) id

                if (key2 instanceof String) key2 = (String) key2 else key2 = (Integer) key2
                ps.addBatch([id, key2] as List<Object>)
            }
        }
        sql.close()
    }

    void tryToStoreJunctionBatchFromMap(HashMap ids, HashMap connectorMap, String insertStatement) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
            sql.withBatch(insertStatement) { ps ->
                ids.each { entry ->
                    entry.value.each { cons -> ps.addBatch([connectorMap.get(entry.key), cons] as List<Object>)
                    }
                }
            }
        sql.commit()
        sql.close()
    }

    void tryToStoreJunctionBatchForSamplesAndVariants(HashMap<Sample, Integer> samples,
                                                      HashMap<SimpleVariantContext, Integer> connectorMap,
                                                      HashMap<SimpleVariantContext, Integer> infoMap, HashMap<SimpleVariantContext, HashMap<String, Integer>> genotypeMap, String insertStatement) {

        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        // sample_id, variant_id, vcfinfo_id, genotype_id
            sql.withBatch(insertStatement) { ps ->
                genotypeMap.each { entry ->
                    samples.each { sampleEntry ->
                        ps.addBatch([sampleEntry.value, connectorMap[entry.key], infoMap[entry.key], genotypeMap[entry.key][sampleEntry.key.identifier]] as List<Object>)
                    }
                }
            }
        sql.commit()
        sql.close()
    }

    private List tryToStoreVariantsBatch(ArrayList<SimpleVariantContext> variants) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        def consequences = []

        /*
        sql.withTransaction {
            sql.withBatch("INSERT INTO variant (uuid, chr, start, end, ref, obs, issomatic, databaseidentifier) values (?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE id=id") { ps ->

                variants.each { v ->
                    ps.addBatch([UUID.randomUUID().toString(), v.getChromosome(), v.getStartPosition(), v.getEndPosition(), v.getReferenceAllele(), v.getObservedAllele(), v.getIsSomatic(), v.getDatabaseId()] as List<Object>)
                    v.getConsequences().each { cons -> consequences.add(cons)}
                }
            }
        }
        sql.commit()

         */

        PreparedStatement pstmt = null

        try {
            def sqlSelect = "INSERT INTO variant (uuid, chr, start, end, ref, obs, issomatic, databaseidentifier) values (?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE id=id"
            pstmt = sql.connection.prepareStatement(
                    sqlSelect)

            variants.each{ variant ->
                variant.getConsequences().each { cons -> consequences.add(cons)}
                pstmt.setString(1, UUID.randomUUID().toString())
                pstmt.setString(2, variant.getChromosome())
                pstmt.setInt(3, variant.getStartPosition().toInteger())
                pstmt.setInt(4, variant.getEndPosition().toInteger())
                pstmt.setString(5, variant.getReferenceAllele())
                pstmt.setString(6, variant.getObservedAllele())
                pstmt.setBoolean(7, variant.getIsSomatic())
                pstmt.setString(8,  variant.getDatabaseId())
                pstmt.addBatch()
            }
            pstmt.executeBatch()
        }
        catch (SQLException e) {
            e.printStackTrace()
        }
        finally {
            pstmt.close();
            sql.close()
        }

        return consequences
    }

    private void tryToStoreVariantInfo2(List<SimpleVariantContext> variants) {
        def props = VcfInfo.declaredFields.findAll { !it.synthetic }.collect { it }
        def infosInserted = []

        sql.withTransaction {
            sql.withBatch("INSERT INTO vcfinfo (ancestralallele,allelecount,allelefrequency,numberalleles,basequality,cigar,dbsnp,hapmaptwo,hapmapthree,thousandgenomes,combineddepth,endpos,rms,mqzero,strandbias,numbersamples,somatic,validated) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE id=id") { ps ->

                variants.each { var ->
                    def vcfInfoProps = var.vcfInfo.properties
                    if (vcfInfoProps.toString() in infosInserted) {
                        return
                    } else {
                        def infoValues = []
                        props.each { prop ->
                            if (prop.type == List) {
                                def value = var.vcfInfo.properties.get(prop.name)
                                def valDb = value ? infoValues.toString() : "[]"
                                infoValues.add(valDb)
                            } else {
                                infoValues.add(var.vcfInfo.properties.get(prop.name) ?: "")
                            }
                            infosInserted.add(vcfInfoProps.toString())
                        }
                        ps.addBatch(infoValues as List<Object>)
                    }
                }
            }
        }
        sql.commit()
    }

    private void tryToStoreVariantInfo(ArrayList variants) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        def props = VcfInfo.declaredFields.findAll { !it.synthetic }.collect { it }
        HashSet<String> vcfInfos = new HashSet<>()
        for (var in variants) {
            vcfInfos.add(var.vcfInfo)
        }
        def infoValues = []

        sql.withTransaction {
            sql.withBatch(50000, "INSERT INTO vcfinfo (ancestralallele,allelecount,allelefrequency,numberalleles,basequality,cigar,dbsnp,hapmaptwo,hapmapthree,thousandgenomes,combineddepth,endpos,rms,mqzero,strandbias,numbersamples,somatic,validated) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE id=id") { ps ->

                vcfInfos.each { vcfInfo ->
                    infoValues.clear()
                    props.each { prop ->
                        if (prop.type == List) {
                            def value = vcfInfo.properties.get(prop.name)
                            def valDb = value ? infoValues.toString() : "[]"
                            infoValues.add(valDb)
                        } else {
                            def valDb2
                            if (vcfInfo.properties.get(prop.name) == null) {
                                valDb2 = ''
                            } else {
                                valDb2 = vcfInfo.properties.get(prop.name)
                            }
                            infoValues.add(valDb2)
                        }
                    }
                    ps.addBatch(infoValues)
                }
            }
        }
        sql.commit()
        sql.close()
    }

    private void tryToStoreVariantGenotypes(ArrayList variants) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        def props = Genotype.declaredFields.findAll { !it.synthetic && it.name != "sampleName" }.collect { it }
        def genotypes = variants.collect { variant -> variant.genotypes }.flatten()

        sql.withTransaction {
            sql.withBatch(25000, "INSERT INTO genotype (genotype, readdepth, filter,likelihoods,genotypelikelihoods,genotypelikelihoodshet,posteriorprobs,genotypequality,haplotypequalities,phaseset,phasingquality,alternateallelecounts,mappingquality) values (?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE id=id") { ps ->

                genotypes.toSet().each { genotype ->
                    def genotypeValues = []
                    props.each { prop -> genotypeValues.add(genotype.properties.get(prop.name)) }
                    ps.addBatch(genotypeValues)
                }
            }
        }
        sql.commit()
        sql.close()
    }

    private HashMap tryToStoreConsequencesBatch(List<Consequence> consequences) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        def consGeneMap = [:]
        PreparedStatement pstmt = null
        def batchSize = 100000

        try {
            def sqlSelect = "INSERT INTO consequence (allele , codingchange , transcriptid , transcriptversion , type , biotype , canonical , aachange , cdnaposition , cdsposition , proteinposition , proteinlength , cdnalength , cdslength , impact, exon, intron, strand, genesymbol , featuretype , distance , warnings) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE id=id"
            pstmt = sql.connection.prepareStatement(sqlSelect)

            consequences.eachWithIndex {cons, index ->

                pstmt.setString(1, cons.allele)
                pstmt.setString(2, cons.codingChange)
                pstmt.setString(3, cons.transcriptId)
                pstmt.setInt(4, cons.transcriptVersion)
                pstmt.setString(5, cons.type)
                pstmt.setString(6, cons.bioType)
                pstmt.setBoolean(7, cons.canonical)
                pstmt.setString(8, cons.aaChange)
                pstmt.setString(9, cons.cdnaPosition)
                pstmt.setString(10, cons.cdsPosition)
                pstmt.setString(11, cons.proteinPosition)
                pstmt.setInt(12, cons.proteinLength)
                pstmt.setInt(13, cons.cdnaLength)
                pstmt.setInt(14, cons.cdsLength)
                pstmt.setString(15, cons.impact)
                pstmt.setString(16, cons.exon)
                pstmt.setString(17, cons.intron)
                pstmt.setInt(18, cons.strand)
                pstmt.setString(19, cons.geneSymbol)
                pstmt.setString(20, cons.featureType)
                pstmt.setInt(21, cons.distance)
                pstmt.setString(22, cons.warnings)

                pstmt.addBatch()

                if (cons.geneId.contains("-")) {
                    consGeneMap[cons] = cons.geneId.split("-")
                } else {
                    consGeneMap[cons] = [cons.geneId]
                }
                if (index % batchSize == 0) {
                    pstmt.executeBatch();
                }
            }
        pstmt.executeBatch()
        }
        catch (SQLException e) {
            e.printStackTrace()
        }
        finally {
            pstmt.close()
            sql.close()
        }

        return consGeneMap
    }

    private String tryToStoreCase(Case patient) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        sql.withTransaction {
            sql.executeInsert("INSERT INTO entity (id) values (?) ON DUPLICATE KEY UPDATE id=id;", [patient.identifier])
        }
        sql.close()
        return patient.identifier
    }

    private void tryToStoreVariantCaller(VariantCaller variantCaller) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        def result = sql.executeInsert("""INSERT INTO variantcaller (name, version, doi) values \
        (${variantCaller.name},
        ${variantCaller.version},
        ${variantCaller.doi})
     ON DUPLICATE KEY UPDATE id=LAST_INSERT_ID(id);""")
        sql.close()
    }

    private String tryToStoreSample(Sample sample) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        def result = sql.executeInsert("""INSERT INTO sample (identifier, cancerentity) values \
        (${sample.identifier},
        ${sample.cancerEntity})
     ON DUPLICATE KEY UPDATE identifier=identifier;""")
        sql.close()
        return sample.identifier
    }

    private String tryToStoreSampleWithCase(Sample sample, String caseId) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        def result = sql.executeInsert("""INSERT INTO sample (identifier, entity_id, cancerentity) values \
        (${sample.identifier},
        ${caseId},
        ${sample.cancerEntity})
     ON DUPLICATE KEY UPDATE identifier=identifier;""")
        sql.close()
        return sample.identifier
    }

    private void tryToStoreSamplesWithCase(Map samples, String caseId) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        sql.withTransaction {
            sql.withBatch("INSERT INTO sample (identifier, entity_id, cancerentity) values (?,?,?) ON DUPLICATE KEY UPDATE identifier=identifier") { BatchingPreparedStatementWrapper ps ->
                samples.values().each { sample -> ps.addBatch([sample.identifier, caseId, sample.cancerEntity])
                }
            }
        }

        sql.commit()
        sql.close()
    }

    private void tryToStoreAnnotationSoftware(Annotation annotationSoftware) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        def result = sql.executeInsert("""INSERT INTO annotationsoftware (name, version, doi) values \
        (${annotationSoftware.name},
        ${annotationSoftware.version},
        ${annotationSoftware.doi})
     ON DUPLICATE KEY UPDATE id=LAST_INSERT_ID(id);""")
        sql.close()
    }

    private void tryToStoreReferenceGenome(ReferenceGenome referenceGenome) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        def result = sql.executeInsert("""INSERT INTO referencegenome (source, build, version) values \
        (${referenceGenome.source},
        ${referenceGenome.build},
        ${referenceGenome.version})
     ON DUPLICATE KEY UPDATE id=id;""")
        sql.close()
    }

    private Integer tryToFindReferenceGenome(ReferenceGenome referenceGenome) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        def result =
                sql.firstRow("SELECT id FROM referencegenome WHERE referencegenome.source=? and referencegenome.build=? and referencegenome.version=?",
                        [referenceGenome.source, referenceGenome.build, referenceGenome.version])
        sql.close()
        return result.id
    }

    private List<String> tryToStoreGenes(List<String> genes) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        def genesToInsert = new HashSet<String>()
        genes.each { gene ->
            if (gene != "") {
                genesToInsert.add(gene)
            }
        }

        PreparedStatement pstmt = null

        try {
            def sqlSelect = "INSERT INTO gene (geneid) values (?) ON DUPLICATE KEY UPDATE id=id"
            pstmt = sql.connection.prepareStatement(
                    sqlSelect)

            genesToInsert.each{ identifier ->
                pstmt.setString(1, identifier)
                pstmt.addBatch()
            }
            pstmt.executeBatch()
        }
        catch (SQLException e) {
            e.printStackTrace()
        }
        finally {
            pstmt.clearBatch()
            pstmt.close()
            sql.close()
        }

        /*
            sql.withBatch('insert INTO gene (geneid) values (?) ON DUPLICATE KEY UPDATE id=id') { ps ->
                genesToInsert.each { identifier -> ps.addBatch([identifier])
                }
            }

        sql.commit()

         */


        return genes
    }

    private List<Gene> tryToStoreGeneObjects(List<Gene> genes) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        sql.withBatch("insert INTO gene (symbol, name, biotype, chr, start, end, synonyms, geneid, description, strand, version) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE id=id") {
            BatchingPreparedStatementWrapper ps ->
            genes.each { gene ->
                ps.addBatch([gene.symbol, gene.name, gene.bioType, gene.chromosome, gene.geneStart,
                             gene.geneEnd, gene.synonyms[0], gene.geneId, gene.description, gene
                                     .strand, gene.version])
            }
        }

        sql.commit()
        sql.close()
        return genes
    }

    private void tryToStoreEnsemblDB(Integer version, String date, Integer referenceGenomeId) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        sql.executeInsert("""INSERT INTO ensembl (version, date, referencegenome_id) values \
        ($version,
        $date,
        $referenceGenomeId)
     ON DUPLICATE KEY UPDATE id=id;""")
        sql.close()
    }

    private Integer tryToFindEnsemblDB(Integer version, String date, Integer rgId) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        def result =
                sql.firstRow("SELECT id FROM ensembl WHERE ensembl.version=? and ensembl.date=? and ensembl.referencegenome_id=?",
                        [version, date, rgId])
        sql.close()
        return result.id
    }

    private Integer tryToFindVariantCaller(VariantCaller variantCaller) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        def result =
                sql.firstRow("SELECT id FROM variantcaller WHERE variantcaller.name=? and variantcaller.version=? and variantcaller.doi=?",
                        [variantCaller.name, variantCaller.version, variantCaller.doi])
        sql.close()
        return result.id
    }

    private Integer tryToFindAnnotationSoftware(Annotation annotation) {
        Connection connection = Objects.requireNonNull(dataSource.connection, "Connection must not be null.")
        Sql sql = new Sql(connection)
        sql.connection.autoCommit = false
        def result =
                sql.firstRow("SELECT id FROM annotationsoftware WHERE annotationsoftware.name=? and annotationsoftware.version=? and annotationsoftware.doi=?",
                        [annotation.name, annotation.version, annotation.doi])
        sql.close()
        return result.id
    }

    private static Case convertRowResultToCase(GroovyRowResult row) {
        def entity = new Case()
        entity.setIdentifier(row.get("id") as String)
        entity.setProjectId(row.get("project_id") as String)
        return entity
    }

    private static Sample convertRowResultToSample(GroovyRowResult row) {
        def sample = new Sample()
        sample.setIdentifier(row.get("identifier") as String)
        sample.setCancerEntity(row.get("cancerentity") as String)
        sample.setCaseId(row.get("entity_id") as String)
        return sample
    }

    //TODO ADAPT
    private static Gene convertRowResultToGene(GroovyRowResult row) {
        def gene = new Gene()
        gene.setBioType(row.get("biotype") as String)
        gene.setChromosome(row.get("chr") as String)
        gene.setGeneEnd(row.get("end") as BigInteger)
        gene.setGeneId(row.get("geneid") as String)
        gene.setGeneStart(row.get("start") as BigInteger)
        gene.setName(row.get("name") as String)
        gene.setSymbol(row.get("symbol") as String)
        gene.setDescription(row.get("description") as String)
        gene.setStrand(row.get("strand") as String)
        gene.setVersion(row.get("version") as Integer)
        //TODO list of synonyms ?
        gene.setSynonyms([row.get("synonyms") as String])
        return gene
    }

    private static Variant convertRowResultToVariant(GroovyRowResult row, Boolean withConsequences, Boolean
            withVcfInfo, Boolean withGenotypes) {
        def variant = new Variant()
        variant.setIdentifier(row.get("varuuid") as String)
        variant.setChromosome(row.get("varchr") as String)
        variant.setStartPosition(row.get("varstart") as BigInteger)
        variant.setEndPosition(row.get("varend") as BigInteger)
        variant.setReferenceAllele(row.get("varref") as String)
        variant.setObservedAllele(row.get("varobs") as String)
        variant.setIsSomatic(row.get("varsomatic") as Boolean)
        variant.setDatabaseIdentifier(row.get("vardbid") as String)

        if (withConsequences) {
            variant.setConsequences([convertRowResultToConsequence(row)])
        }
        if (withVcfInfo) {
            variant.setVcfInfo(convertRowResultToVcfInfo(row))
        }
        if (withGenotypes) {
            variant.setGenotypes([convertRowResultToGenotype(row)])
        }
        return variant
    }

    private static Consequence convertRowResultToConsequence(GroovyRowResult row) {
        def consequence = new Consequence()
        consequence.allele = row.get("allele") as String
        consequence.codingChange = row.get("codingchange") as String
        consequence.transcriptId = row.get("transcriptid") as String
        consequence.transcriptVersion = row.get("transcriptversion") as Integer
        consequence.type = row.get("type") as String
        consequence.bioType = row.get("biotype") as String
        consequence.canonical = row.get("canonical") as Boolean
        consequence.aaChange = row.get("aachange") as String
        consequence.cdnaPosition = row.get("cdnaposition") as String
        consequence.cdsPosition = row.get("cdsposition") as String
        consequence.proteinPosition = row.get("proteinposition") as String
        consequence.proteinLength = row.get("proteinlength") as Integer
        consequence.cdnaLength = row.get("cdnalength") as Integer
        consequence.cdsLength = row.get("cdslength") as Integer
        consequence.impact = row.get("impact") as String
        consequence.exon = row.get("exon") as String
        consequence.intron = row.get("intron") as String
        consequence.strand = row.get("strand") as Integer
        consequence.geneSymbol = row.get("genesymbol") as String
        consequence.geneId = row.get("geneid") as String
        consequence.featureType = row.get("featuretype") as String
        consequence.distance = row.get("distance") as Integer
        consequence.warnings = row.get("warnings") as String
        return consequence
    }

    private static VcfInfo convertRowResultToVcfInfo(GroovyRowResult row) {
        def vcfInfo = new VcfInfo()
        def aAllele = row.get("ancestralallele") as String
        def cigar =  row.get("cigar") as String
        vcfInfo.ancestralAllele = aAllele != "" ? aAllele : null
        vcfInfo.alleleCount = new JsonSlurper().parseText(row.get("allelecount"))
        vcfInfo.alleleFrequency = new JsonSlurper().parseText(row.get("allelefrequency"))
        vcfInfo.numberAlleles = row.get("numberalleles") as Integer
        vcfInfo.baseQuality = row.get("basequality") as Integer
        vcfInfo.cigar = cigar != "" ? cigar: null
        vcfInfo.dbSnp = row.get("dbsnp") as Boolean
        vcfInfo.hapmapTwo = row.get("hapmaptwo") as Boolean
        vcfInfo.hapmapThree = row.get("hapmapthree") as Boolean
        vcfInfo.thousandGenomes = row.get("thousandgenomes") as Boolean
        vcfInfo.combinedDepth = row.get("combineddepth") as Integer
        vcfInfo.endPos = row.get("endpos") as Integer
        vcfInfo.rms = row.get("rms") as Integer
        vcfInfo.mqZero = row.get("mqzero") as Integer
        vcfInfo.strandBias = row.get("strandbias") as Integer
        vcfInfo.numberSamples = row.get("numbersamples") as Integer
        vcfInfo.somatic = row.get("somatic") as Boolean
        vcfInfo.validated = row.get("validated") as Boolean
        return vcfInfo
    }

    private static Genotype convertRowResultToGenotype(GroovyRowResult row) {
        def genotype = new Genotype()
        genotype.sampleName = row.get("identifier") as String
        genotype.genotype = row.get("genotype") as String
        genotype.readDepth = row.get("readdepth") as Integer
        genotype.filter = row.get("filter") as String
        genotype.likelihoods = row.get("likelihoods") as String
        genotype.genotypeLikelihoods = row.get("genotypelikelihoods") as String
        genotype.genotypeLikelihoodsHet = row.get("genotypelikelihoodshet") as String
        genotype.posteriorProbs = row.get("posteriorprobs") as String
        genotype.genotypeQuality = row.get("genotypequality") as Integer
        genotype.haplotypeQualities = row.get("haplotypequalities") as String
        genotype.phaseSet = row.get("phaseset") as String
        genotype.phasingQuality = row.get("phasingquality") as Integer
        genotype.alternateAlleleCounts = row.get("alternatealleleCounts") as String
        genotype.mappingQuality = row.get("mappingquality") as Integer
        return genotype
    }

    private static List<Variant> parseVariantQueryResult(List<GroovyRowResult> rows, Boolean withConsequence = true, withVcfInfo = false, Boolean withGenotypes = false) {
        Map<String, List<Variant>> variantsIdMap = rows.collect { convertRowResultToVariant(it, withConsequence, withVcfInfo, withGenotypes) }
                .groupBy { it.identifier }
        List<Variant> variants = []

        if (!withConsequence) {
            return variantsIdMap.values().toList().flatten() as List<Variant>
        }

        variantsIdMap.each { key, value ->
            def consequences = value*.getConsequences().collectMany { [it] }.flatten()

            // in case of e.g. intergenic consequences, we have to join the corresponding identifiers of affected
            // genes and report it as one consequence
            // @TODO investigate alternative to just set the annotated transcript ID as geneid in such cases...
            def groupedConsequences = consequences.groupBy({ it.codingChange }, { it.transcriptId })
            def joinedConsequences = []
            groupedConsequences.each { coding, values ->
                values.each { transcript, cons ->
                    if (cons.size > 1) {
                        Consequence c = (Consequence) cons[0]
                        joinedConsequences.add(c)
                    } else {
                        joinedConsequences.addAll(cons)
                    }
                }
            }

            if (withGenotypes) {
                def genotypes = value*.getGenotypes().collectMany{ [it]}.flatten().unique()
                value[0].genotypes = genotypes
            }

            value[0].consequences = joinedConsequences
            variants.add(value[0])
        }
        return variants.flatten() as List<Variant>
    }

    private static String buildMultiSelectString(String field, List values, String name) {
        def formattedValues = []
        def classType = values[0][name]
         if (classType instanceof Boolean) {
             formattedValues = values.collect { it[name] != null ? it[name] as boolean : "''"}
         }
        else if (classType instanceof Float) {
             formattedValues = values.collect { it[name] != null ? it[name] as float : "''"}
         }
        else if (classType instanceof Integer) {
             formattedValues = values.collect { it[name] != null ? it[name] as int : "''"}
         }
        else if (classType instanceof Double) {
             formattedValues = values.collect { it[name] != null ? it[name] as double : "''"}
         }
        else {
                formattedValues = values.collect { it[name] != null ? "'${it[name]}'" : "''"}
        }

        def query = new StringBuilder(field)
        query.append(" in (".intern())
        query.append(formattedValues.join(',').intern())
        query.append(") ".intern())
        return query.toString()
    }

}



