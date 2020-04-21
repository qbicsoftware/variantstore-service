package life.qbic.oncostore.database

import groovy.sql.BatchingPreparedStatementWrapper
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.util.logging.Log4j2
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.context.event.BeanCreatedEvent
import io.micronaut.context.event.BeanCreatedEventListener
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

@Log4j2
@Singleton
class MariaDBVariantstoreStorage implements VariantstoreStorage {

    QBiCDataSource dataSource
    Sql sql

    /* Predefined queries for inserting db entries in junction tables */
    String insertVariantConsequenceJunction = "INSERT INTO Variant_has_Consequence (Variant_id, Consequence_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE Variant_id=Variant_id"
    String insertVariantVariantCallerJunction = "INSERT INTO Variant_has_VariantCaller (VariantCaller_id, Variant_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE VariantCaller_id=VariantCaller_id"
    String insertAnnotationSoftwareConsequenceJunction = "INSERT INTO AnnotationSoftware_has_Consequence (AnnotationSoftware_id, Consequence_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE AnnotationSoftware_id=AnnotationSoftware_id"
    String insertReferenceGenomeVariantJunction = "INSERT INTO Variant_has_ReferenceGenome (ReferenceGenome_id, Variant_id) VALUES (?,?) ON DUPLICATE KEY UPDATE ReferenceGenome_id=ReferenceGenome_id"
    String insertSampleVariantJunction = "INSERT INTO Sample_has_Variant (Sample_identifier, Variant_id) VALUES (?,?) ON DUPLICATE KEY UPDATE Sample_identifier=Sample_identifier"
    String insertEnsemblGeneJunction = "INSERT INTO Ensembl_has_Gene (Ensembl_id, Gene_id) VALUES (?,?) ON DUPLICATE KEY UPDATE Ensembl_id=Ensembl_id"
    String insertConsequenceGeneJunction = "INSERT INTO Consequence_has_Gene (Consequence_id, Gene_id) VALUES (?,?) ON DUPLICATE KEY UPDATE Consequence_id=Consequence_id"

    @Inject
    MariaDBVariantstoreStorage(QBiCDataSource dataSource) {
        this.dataSource = dataSource
    }

    @Override
    List<Variant> findVariantsForBeaconResponse(String chromosome, BigInteger start,
                                                String reference, String observed, String assemblyId) {
        sql = new Sql(dataSource.connection)
        try {
            def variant = fetchVariantsForBeaconResponse(chromosome, start, reference, observed, assemblyId)
            return variant
        }
        catch (Exception e) {
            throw new VariantstoreStorageException("Beacon something? $e.", e.fillInStackTrace())
        }
        finally {
            sql.close()
        }
    }

    @Override
    List<Case> findCaseById(String id) {
        //TODO should we check for a specific type of identifier?
        //if (id?.trim()) {
        //    throw new IllegalArgumentException("Invalid case identifier supplied.")
        //}
        sql = new Sql(dataSource.connection)
        try {
            def cases = fetchCaseForId(id)
            return cases
        }
        catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch case with identifier $id.", e.fillInStackTrace())
        }
        finally {
            sql.close()
        }
    }

    @Override
    List<Sample> findSampleById(String id) {
        if (!IdValidator.isValidSampleCode(id)) {
            throw new IllegalArgumentException("Invalid sample identifier supplied.")
        }
        sql = new Sql(dataSource.connection)
        try {
            def samples = fetchSampleForId(id)
            return samples
        }
        catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch sample with identifier $id.", e.fillInStackTrace())
        }
        finally {
            sql.close()
        }
    }

    @Override
    List<Variant> findVariantById(String id) {
        if (!IdValidator.isValidUUID(id)) {
            throw new IllegalArgumentException("Invalid variant identifier supplied.")
        }
        sql = new Sql(dataSource.connection)
        try {
            def variants = fetchVariantForId(id)
            return variants
        }
        catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch variant with identifier $id.", e.printStackTrace())
        }
        finally {
            sql.close()
        }
    }

    @Override
    /**
     * Find gene in store for given gene identifier (ENSEMBL)
     * @param id Gene identifier
     * @return List<Gene>  containing the found gene for specified identifier
     */
    List<Gene> findGeneById(String id, ListingArguments args) {
        sql = new Sql(dataSource.connection)
        try {
            if (args.getEnsemblVersion().isPresent()) {
                return fetchGeneForIdWithEnsemblVersion(id, args.getEnsemblVersion().get())
            }
            def ensemblversion = fetchEnsemblVersion()
            if (ensemblversion) {
                return fetchGeneForIdWithEnsemblVersion(id, ensemblversion)
            }
            // fall back solution, if there is not ensembl version in the variantstore instance
            return fetchGeneForId(id)
        }
        catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch gene with identifier $id.", e.printStackTrace())
        }
        finally {
            sql.close()
        }
    }

    @Override
    List<Case> findCases(@NotNull ListingArguments args) {
        sql = new Sql(dataSource.connection)
        try {
            if (args.getConsequenceType().isPresent()) {
                return fetchCasesByConsequenceType(args.getConsequenceType().get())
            }

            if (args.getChromosome().isPresent() && args.getStartPosition().isPresent() && args.getEndPosition().isPresent()) {
                return fetchCasesByChromosomeAndPositionRange(args.getChromosome().get(), args.getStartPosition().get(), args.getEndPosition().get())
            }

            if (args.getChromosome().isPresent()) {
                return fetchCasesByChromosome(args.getChromosome().get())
            }
            return fetchCases()
        }
        catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch cases.", e.printStackTrace())
        }
        finally {
            sql.close()
        }
    }

    @Override
    List<Sample> findSamples(@NotNull ListingArguments args) {
        sql = new Sql(dataSource.connection)
        try {
            if (args.getCancerEntity().isPresent()) {
                return fetchSamplesByCancerEntity(args.getCancerEntity().get())
            }
            return fetchSamples()
        }
        catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch samples.", e.fillInStackTrace())
        }
        finally {
            sql.close()
        }
    }

    @Override
    List<Variant> findVariants(@NotNull ListingArguments args) {
        sql = new Sql(dataSource.connection)
        try {
            if (args.getChromosome().isPresent() && args.getStartPosition().isPresent()) {
                return fetchVariantsByChromosomeAndStartPosition(args.getChromosome().get(), args.getStartPosition().get())
            }

            if (args.getStartPosition().isPresent()) {
                return fetchVariantsByStartPosition(args.getStartPosition().get())
            }

            if (args.getChromosome().isPresent()) {
                return fetchVariantsByChromosome(args.getChromosome().get())
            }

            if (args.getSampleId().isPresent() && args.getGeneId().isPresent()) {
                return fetchVariantsBySampleAndGeneId(args.getSampleId().get(), args.getGeneId().get())
            }

            if (args.getSampleId().isPresent()) {
                return fetchVariantsBySample(args.getSampleId().get())
            }

            if (args.getGeneId().isPresent()) {
                return fetchVariantsByGeneId(args.getGeneId().get())
            }
            return fetchVariants()
        }
        catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch variants.", e.fillInStackTrace())
        }
        finally {
            sql.close()
        }
    }

    @Override
    List<Gene> findGenes(@NotNull ListingArguments args) {
        sql = new Sql(dataSource.connection)
        try {
            if (args.getSampleId()) {
                if (!IdValidator.isValidSampleCode(args.getSampleId().get())) {
                    throw new IllegalArgumentException("Invalid sample identifier supplied.")
                }
                return fetchGenesBySample(args.getSampleId().get())
            }

            return fetchGenes()
        }
        catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch genes.", e.fillInStackTrace())
        }
        finally {
            sql.close()
        }
    }

    private Integer fetchEnsemblVersion() {
        def result =
                sql.firstRow("SELECT MAX(version) AS version FROM Ensembl")
        return result.version
    }

    @Override
    void storeCaseInStore(Case patient) throws VariantstoreStorageException {
        this.sql = new Sql(dataSource.connection)
        try {
            tryToStoreCase(patient)
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not store case in store: $patient", e)
        }
        finally {
            sql.close()
        }
    }

    @Override
    void storeSampleInStore(Sample sample) throws VariantstoreStorageException {
        this.sql = new Sql(dataSource.connection)
        try {
            tryToStoreSample(sample)
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not store sample in store: $sample", e)
        }
        finally {
            sql.close()
        }
    }

    @Override
    void storeReferenceGenomeInStore(ReferenceGenome referenceGenome) throws VariantstoreStorageException {
        this.sql = new Sql(dataSource.connection)
        try {
            tryToStoreReferenceGenome(referenceGenome)
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not store reference genome in store: $referenceGenome", e)
        }
        finally {
            sql.close()
        }
    }

    @Override
    void storeVariantCallerInStore(VariantCaller variantCaller) throws VariantstoreStorageException {
        this.sql = new Sql(dataSource.connection)
        try {
            tryToStoreVariantCaller(variantCaller)
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not store variant calling software in store: $variantCaller", e)
        }
        finally {
            sql.close()
        }
    }

    @Override
    void storeAnnotationSoftwareInStore(Annotation annotationSoftware) throws VariantstoreStorageException {
        this.sql = new Sql(dataSource.connection)
        try {
            tryToStoreAnnotationSoftware(annotationSoftware)
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not store annotation software in store: $annotationSoftware", e)
        }
        finally {
            sql.close()
        }
    }

    @Override
    void storeVariantsInStoreWithMetadata(MetadataContext metadata, List<SimpleVariantContext> variants) throws VariantstoreStorageException {
        this.sql = new Sql(dataSource.connection)

        try {
            def cId = tryToStoreCase(metadata.getCase())
            def sId = tryToStoreSampleWithCase(metadata.getSample(), cId)
            tryToStoreVariantCaller(metadata.getVariantCalling())
            tryToStoreAnnotationSoftware(metadata.getVariantAnnotation())
            tryToStoreReferenceGenome(metadata.getReferenceGenome())

            def vcId = tryToFindVariantCaller(metadata.getVariantCalling())
            def asId = tryToFindAnnotationSoftware(metadata.getVariantAnnotation())
            def rgId = tryToFindReferenceGenome(metadata.getReferenceGenome())

            sql.connection.autoCommit = false
            sql.setCacheStatements(true)

            /* INSERT variants and save consequences for import */
            def consequencesToInsert = tryToStoreVariantsBatch(variants)

            /* INSERT consequences */
            def consGeneMap = tryToStoreConsequencesBatch(consequencesToInsert)

            /* INSERT genes */
            tryToStoreGenes(consGeneMap.values().toList().flatten() as List<String>)

            /* GET ids of genes */
            def geneIdMap = tryToFindGenesByConsequence(consGeneMap as HashMap<Consequence, List<String>>)

            /* GET ids of variants */
            def variantIdMap = tryToFindVariants(variants)

            /* GET ids of consequences */
            def findConsequenceMaps = tryToFindConsequences(variants)
            def variantConsequenceIdMap = findConsequenceMaps.first

            /* consequence to consequence DB id map */
            def consIdMap = findConsequenceMaps.second

            /* INSERT variant and consequence junction */
            tryToStoreJunctionBatchFromMap(variantConsequenceIdMap, variantIdMap, insertVariantConsequenceJunction)

            /* INSERT consequence and gene junction */
            tryToStoreJunctionBatchFromMap(geneIdMap, consIdMap, insertConsequenceGeneJunction)

            /* INSERT consequences and annotation software junction */
            tryToStoreJunctionBatch(asId, variantConsequenceIdMap.values().toList().flatten(), insertAnnotationSoftwareConsequenceJunction)

            /* INSERT sample and variants junction */
            tryToStoreJunctionBatch(sId, variantIdMap.values().asList(), insertSampleVariantJunction)

            /* INSERT variants and variant caller in junction table */
            tryToStoreJunctionBatch(vcId, variantIdMap.values().asList(), insertVariantVariantCallerJunction)

            /* INSERT variants and reference genome in junction table */
            tryToStoreJunctionBatch(rgId, variantIdMap.values().asList(), insertReferenceGenomeVariantJunction)

        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not store variants with metadata in store: $metadata", e.printStackTrace())
        }
        finally {
            sql.close()
        }
    }

    @Override
    void storeGenesWithMetadata(Integer version, String date, ReferenceGenome referenceGenome, List<Gene> genes) throws VariantstoreStorageException {
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
            throw new VariantstoreStorageException("Could not store genes in store: ", e)
        }
        finally {
            sql.close()
        }
    }

    HashMap tryToFindVariants(List<SimpleVariantContext> variants) {
        def ids = [:]

        variants.each { var ->
            def result =
                    sql.firstRow("SELECT id FROM Variant WHERE Variant.chr=? and Variant.start=? and Variant.end=? and Variant.ref=? and Variant.obs=? and Variant.isSomatic=?",
                            [var.chromosome, var.startPosition, var.endPosition, var.referenceAllele, var.observedAllele, var.isSomatic])
            ids[var] = result.id
        }
        return ids
    }

    Tuple2<HashMap, HashMap> tryToFindConsequences(List<SimpleVariantContext> variants) {
        def ids = [:]
        def consIdMap = [:]

        variants.each { var ->
            def consIds = []
            var.getConsequences().each { cons ->
                def result = sql.firstRow("SELECT id FROM Consequence WHERE Consequence.allele=? and Consequence.codingChange=? and Consequence.transcriptId=? and Consequence.transcriptVersion=? and Consequence.type=? and Consequence.bioType=? and Consequence.canonical=? and Consequence.aaChange=? and Consequence.cdnaPosition=? and Consequence.cdsPosition=? and Consequence.proteinPosition=? and Consequence.proteinLength=? and Consequence.cdnaLength=? and Consequence.cdsLength=? and Consequence.impact=? and Consequence.exon=? and Consequence.intron=? and Consequence.strand=? and Consequence.geneSymbol=? and Consequence.featureType=? and Consequence.distance=? and Consequence.warnings=?",
                        [cons.allele, cons.codingChange, cons.transcriptId, cons.transcriptVersion, cons.type, cons.bioType, cons.canonical, cons.aaChange, cons.cdnaPosition, cons.cdsPosition, cons.proteinPosition, cons.proteinLength, cons.cdnaLength, cons.cdsLength, cons.impact, cons.exon, cons.intron, cons.strand, cons.geneSymbol, cons.featureType, cons.distance, cons.warnings])
                consIds.add(result.id)
                consIdMap[cons] = result.id
            }
            ids[var] = consIds
        }
        return new Tuple2(ids, consIdMap)
    }

    HashMap tryToFindGenes(List<Gene> genes) {
        def ids = [:]

        genes.each { gene ->
            def result =
                    sql.firstRow("SELECT id FROM Gene WHERE Gene.symbol=? and Gene.name=? and Gene.bioType=? and Gene.chr=? and Gene.start=? and Gene.end=? and Gene.synonyms=? and Gene.geneId=? and Gene.description=? and Gene.strand=? and Gene.version=?",
                            [gene.symbol, gene.name, gene.bioType, gene.chromosome, gene.geneStart, gene.geneEnd, gene.synonyms[0], gene.geneId, gene.description, gene.strand, gene.version])
            ids[gene] = result.id
        }
        return ids
    }

    private HashMap<Consequence, List<String>> tryToFindGenesByConsequence(HashMap<Consequence, List<String>> consequenceToGeneIds) {
        def ids = [:]

        consequenceToGeneIds.each { cons, geneIDs ->
            def geneDBids = []
            geneIDs.each { geneId ->
                def result = sql.firstRow("SELECT id FROM Gene WHERE Gene.geneId=?", [geneId])
                geneDBids.add(result.id)
            }

            ids[cons] = geneDBids
        }
        return ids as HashMap<Consequence, List<String>>
    }

    private List<Case> fetchCaseForId(String id) {
        def result = sql.rows("""SELECT distinct Entity.id, Project_id FROM Entity WHERE Entity.id=$id;""")
        List<Case> cases = result.collect { convertRowResultToCase(it) }
        return cases
    }

    private List<Sample> fetchSampleForId(String id) {
        def result = sql.rows("""SELECT * FROM Sample WHERE Sample.identifier=$id;""")
        List<Sample> sample = result.collect { convertRowResultToSample(it) }
        return sample
    }

    private List<Variant> fetchVariantForId(String id) {
        def result = sql.rows("""SELECT Variant.id as varid, Variant.chr as varchr, Variant.start as varstart, Variant.end as varend, Variant.ref as varref, Variant.obs as varobs, Variant.issomatic as varsomatic, Variant.uuid as varuuid, Consequence.*, Gene.id as geneIndex, Gene.geneId as geneId FROM Variant INNER JOIN Variant_has_Consequence ON Variant.id = Variant_has_Consequence.Variant_id INNER JOIN Consequence on Variant_has_Consequence.Consequence_id = Consequence.id INNER JOIN Consequence_has_Gene on Consequence_has_Gene.Consequence_id = Consequence.id INNER JOIN Gene on Gene.id=Consequence_has_Gene.Gene_id WHERE Variant.uuid=$id;""")
        return parseVariantQueryResult(result, true)
    }

    private List<Gene> fetchGeneForId(String id) {
        def result = sql.rows("""SELECT distinct * FROM Gene WHERE Gene.geneId=$id;""")
        List<Gene> genes = result.collect { convertRowResultToGene(it) }
        return genes
    }

    private List<Gene> fetchGeneForIdWithEnsemblVersion(String id, Integer ensemblVersion) {
        def result = sql.rows("""SELECT distinct * FROM Gene INNER JOIN Ensembl_has_Gene ON Gene.id = Ensembl_has_Gene.Gene_id INNER JOIN Ensembl ON Ensembl_has_Gene.Ensembl_id = Ensembl.id WHERE Gene.geneId=$id and Ensembl.version=$ensemblVersion;""")
        List<Gene> genes = result.collect { convertRowResultToGene(it) }
        return genes
    }

    private List<Case> fetchCases() {
        def result = sql.rows("""SELECT * FROM Entity;""")
        List<Case> cases = result.collect { convertRowResultToCase(it) }
        return cases
    }

    private List<Sample> fetchSamples() {
        def result = sql.rows("""SELECT * FROM Sample;""")
        List<Sample> samples = result.collect { convertRowResultToSample(it) }
        return samples
    }

    private List<Variant> fetchVariants() {
        def result = sql.rows("""SELECT Variant.id as varid, Variant.chr as varchr, Variant.start as varstart, Variant.end as varend, Variant.ref as varref, Variant.obs as varobs, Variant.issomatic as varsomatic, Variant.uuid as varuuid FROM Variant;""")
        return parseVariantQueryResult(result, false)
    }

    private List<Gene> fetchGenes() {
        def result = sql.rows("""SELECT * FROM Gene;""")
        List<Gene> genes = result.collect { convertRowResultToGene(it) }
        return genes
    }

    private List<Case> fetchCasesByConsequenceType(String consequenceType) {
        def result = sql.rows("""select distinct Entity.id, Project_id from Entity INNER JOIN Sample ON Entity.id = Sample.Entity_id INNER JOIN Sample_has_Variant ON Sample.identifier = Sample_has_Variant.Sample_identifier INNER JOIN Variant ON Variant.id = Sample_has_Variant.Variant_id INNER JOIN Variant_has_Consequence ON Variant_has_Consequence.Variant_id = Variant.id INNER JOIN Consequence on Variant_has_Consequence.Consequence_id = Consequence.id where Consequence.type = $consequenceType""")
        List<Case> cases = result.collect { convertRowResultToCase(it) }
        return cases
    }

    private List<Case> fetchCasesByChromosome(String chromosome) {
        def result = sql.rows("""select distinct Entity.id, Project_id from Entity INNER JOIN Sample ON Entity.id = Sample.Entity_id INNER JOIN Sample_has_Variant ON Sample.identifier = Sample_has_Variant.Sample_identifier INNER JOIN Variant ON Variant.id = Sample_has_Variant.Variant_id where Variant.chr = $chromosome;""")
        List<Case> cases = result.collect { convertRowResultToCase(it) }
        return cases
    }

    private List<Case> fetchCasesByChromosomeAndPositionRange(String chromosome, BigInteger startPosition, BigInteger endPosition) {
        def result = sql.rows("""select distinct Entity.id, Project_id from Entity INNER JOIN Sample ON Entity.id = Sample.Entity_id INNER JOIN Sample_has_Variant ON Sample.identifier = Sample_has_Variant.Sample_identifier INNER JOIN Variant ON Variant.id = Sample_has_Variant.Variant_id where Variant.chr = $chromosome AND Variant.start >= $startPosition AND Variant.end <= $endPosition;""")
        List<Case> cases = result.collect { convertRowResultToCase(it) }
        return cases
    }

    private List<Sample> fetchSamplesByCancerEntity(String entity) {
        def result = sql.rows("""SELECT * FROM Sample WHERE Sample.cancerEntity=$entity;""")
        List<Sample> samples = result.collect { convertRowResultToSample(it) }
        return samples
    }

    private List<Variant> fetchVariantsByChromosomeAndStartPosition(String chromosome, BigInteger start) {
        def result = sql.rows("""SELECT Variant.id as varid, Variant.chr as varchr, Variant.start as varstart, Variant.end as varend, Variant.ref as varref, Variant.obs as varobs, Variant.issomatic as varsomatic, Variant.uuid as varuuid, Consequence.*, Gene.id as geneIndex, Gene.geneId as geneId FROM Variant INNER JOIN Variant_has_Consequence ON Variant.id = Variant_has_Consequence.Variant_id INNER JOIN Consequence on Variant_has_Consequence.Consequence_id = Consequence.id INNER JOIN Consequence_has_Gene on Consequence_has_Gene.Consequence_id = Consequence.id INNER JOIN Gene on Gene.id=Consequence_has_Gene.Gene_id WHERE Variant.chr=$chromosome AND Variant.start=$start;""")
        return parseVariantQueryResult(result)
    }

    private List<Variant> fetchVariantsByChromosome(String chromosome) {
        def result = sql.rows("""SELECT Variant.id as varid, Variant.chr as varchr, Variant.start as varstart, Variant.end as varend, Variant.ref as varref, Variant.obs as varobs, Variant.issomatic as varsomatic, Variant.uuid as varuuid, Consequence.*, Gene.id as geneIndex, Gene.geneId as geneId FROM Variant INNER JOIN Variant_has_Consequence ON Variant.id = Variant_has_Consequence.Variant_id INNER JOIN Consequence on Variant_has_Consequence.Consequence_id = Consequence.id INNER JOIN Consequence_has_Gene on Consequence_has_Gene.Consequence_id = Consequence.id INNER JOIN Gene on Gene.id=Consequence_has_Gene.Gene_id WHERE Variant.chr=$chromosome""")
        return parseVariantQueryResult(result)
    }

    private List<Variant> fetchVariantsByStartPosition(BigInteger start) {
        def result = sql.rows("""SELECT Variant.id as varid, Variant.chr as varchr, Variant.start as varstart, Variant.end as varend, Variant.ref as varref, Variant.obs as varobs, Variant.issomatic as varsomatic, Variant.uuid as varuuid, Consequence.*, Gene.id as geneIndex, Gene.geneId as geneId FROM Variant INNER JOIN Variant_has_Consequence ON Variant.id = Variant_has_Consequence.Variant_id INNER JOIN Consequence on Variant_has_Consequence.Consequence_id = Consequence.id INNER JOIN Consequence_has_Gene on Consequence_has_Gene.Consequence_id = Consequence.id INNER JOIN Gene on Gene.id=Consequence_has_Gene.Gene_id WHERE Variant.start=$start;""")
        return parseVariantQueryResult(result)
    }

    private List<Variant> fetchVariantsBySample(String sampleId) {
        def result = sql.rows("""SELECT Variant.id as varid, Variant.chr as varchr, Variant.start as varstart, Variant.end as varend, Variant.ref as varref, Variant.obs as varobs, Variant.issomatic as varsomatic, Variant.uuid as varuuid, Consequence.*, Gene.id as geneIndex, Gene.geneId as geneId FROM Variant INNER JOIN Sample_has_Variant ON Variant.id = Sample_has_Variant.Variant_id INNER JOIN Variant_has_Consequence ON Variant.id = Variant_has_Consequence.Variant_id INNER JOIN Consequence on Variant_has_Consequence.Consequence_id = Consequence.id INNER JOIN Consequence_has_Gene on Consequence_has_Gene.Consequence_id = Consequence.id INNER JOIN Gene on Gene.id=Consequence_has_Gene.Gene_id WHERE Sample_identifier=$sampleId;""")
        return parseVariantQueryResult(result)
    }

    private List<Variant> fetchVariantsBySampleAndGeneId(String sampleId, String geneId) {
        def result = sql.rows("""SELECT distinct Variant.id as varid, Variant.chr as varchr, Variant.start as varstart, Variant.end as varend, Variant.ref as varref, Variant.obs as varobs, Variant.issomatic as varsomatic, Variant.uuid as varuuid, Consequence.*, Gene.id as geneIndex, Gene.geneId as geneId FROM Variant INNER JOIN Variant_has_Consequence ON Variant.id = Variant_has_Consequence.Variant_id INNER JOIN Consequence on Variant_has_Consequence.Consequence_id = Consequence.id INNER JOIN Sample_has_Variant ON Sample_has_Variant.Variant_id = Variant_has_Consequence.Variant_id INNER JOIN Consequence_has_Gene on Consequence_has_Gene.Consequence_id = Consequence.id INNER JOIN Gene on Gene.id=Consequence_has_Gene.Gene_id where Sample_identifier = $sampleId AND geneId=$geneId;""")
        return parseVariantQueryResult(result)
    }

    private List<Variant> fetchVariantsByGeneId(String geneId) {
        def result = sql.rows("""SELECT Variant.id as varid, Variant.chr as varchr, Variant.start as varstart, Variant.end as varend, Variant.ref as varref, Variant.obs as varobs, Variant.issomatic as varsomatic, Variant.uuid as varuuid, Consequence.*, Gene.id as geneIndex, Gene.geneId as geneId FROM Variant INNER JOIN Variant_has_Consequence ON Variant.id = Variant_has_Consequence.Variant_id INNER JOIN Consequence on Variant_has_Consequence.Consequence_id = Consequence.id INNER JOIN Consequence_has_Gene on Consequence_has_Gene.Consequence_id = Consequence.id INNER JOIN Gene on Gene.id=Consequence_has_Gene.Gene_id WHERE geneId=$geneId;""")
        return parseVariantQueryResult(result)
    }

    private List<Variant> fetchVariantsForBeaconResponse(String chromosome, BigInteger start,
                                                         String reference, String observed, String assemblyId) {
        def result = sql.rows("""SELECT Variant.id as varid, Variant.chr as varchr, Variant.start as varstart, Variant.end as varend, Variant.ref as varref, Variant.obs as varobs, Variant.issomatic as varsomatic, Variant.uuid as varuuid FROM Variant INNER JOIN Variant_has_ReferenceGenome ON Variant.id = Variant_has_ReferenceGenome.Variant_id INNER JOIN ReferenceGenome on Variant_has_ReferenceGenome.ReferenceGenome_id = ReferenceGenome.id where ReferenceGenome.build=$assemblyId and Variant.chr=$chromosome and Variant.start=$start and Variant.ref=$reference and Variant.obs=$observed;""")
        return parseVariantQueryResult(result, false)
    }

    private List<Gene> fetchGenesBySample(String sampleId) {
        def result = sql.rows(""" SELECT Gene.*, Sample_has_Variant.* FROM Gene INNER JOIN Consequence_has_Gene ON Gene.id = Consequence_has_Gene.Gene_id INNER JOIN Consequence on Consequence_has_Gene.Consequence_id = Consequence.id INNER JOIN Variant_has_Consequence on Variant_has_Consequence.Consequence_id = Consequence.id INNER JOIN Variant ON Variant_has_Consequence.Variant_id = Variant.id INNER JOIN Sample_has_Variant ON Sample_has_Variant.Variant_id = Variant.id WHERE Sample_identifier=$sampleId;""")
        List<Gene> genes = result.collect { convertRowResultToGene(it) }
        return genes
    }

    void tryToStoreJunctionBatch(Object id, List ids, String insertStatement) {
        sql.withBatch(insertStatement)
                { ps ->
                    ids.each { key2 ->
                        if (id instanceof String)
                            id = (String) id
                        else
                            id = (Integer) id

                        if (key2 instanceof String)
                            key2 = (String) key2
                        else
                            key2 = (Integer) key2
                        ps.addBatch([id, key2] as List<Object>)
                    }
                }
        sql.commit()
    }

    void tryToStoreJunctionBatchFromMap(HashMap ids, HashMap connectorMap, String insertStatement) {
        sql.withBatch(insertStatement)
                { ps ->
                    ids.each { entry ->
                        entry.value.each { cons ->
                            ps.addBatch([connectorMap.get(entry.key), cons] as List<Object>)
                        }
                    }
                }
        sql.commit()
    }

    private List tryToStoreVariantsBatch(List<SimpleVariantContext> variants) {
        def consequences = []

        sql.withBatch("INSERT INTO Variant (uuid, chr, start, end, ref, obs, isSomatic) values (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE id=id")
                { ps ->
                    variants.each { v ->
                        ps.addBatch([UUID.randomUUID().toString(), v.getChromosome(), v.getStartPosition(), v.getEndPosition(), v.getReferenceAllele(), v.getObservedAllele(), v.getIsSomatic()])
                        v.getConsequences().each { cons ->
                            consequences.add(cons)
                        }
                    }
                }

        sql.commit()
        return consequences
    }

    private HashMap tryToStoreConsequencesBatch(List<Consequence> consequences) {
        def consGeneMap = [:]


        sql.withBatch("INSERT INTO Consequence (allele , codingChange , transcriptID , transcriptVersion , type , bioType , canonical , aaChange , cdnaPosition , cdsPosition , proteinPosition , proteinLength , cdnaLength , cdsLength , impact, exon, intron, strand, geneSymbol , featureType , distance , warnings) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE id=id")
                { ps ->
                    consequences.each { cons ->
                        ps.addBatch([cons.allele, cons.codingChange, cons.transcriptId, cons.transcriptVersion, cons.type, cons.bioType, cons.canonical, cons.aaChange, cons.cdnaPosition, cons.cdsPosition, cons.proteinPosition, cons.proteinLength, cons.cdnaLength, cons.cdsLength, cons.impact, cons.exon, cons.intron, cons.strand, cons.geneSymbol, cons.featureType, cons.distance, cons.warnings])
                        if (cons.geneId.contains("-")) {
                            consGeneMap[cons] = cons.geneId.split("-")
                        } else {
                            consGeneMap[cons] = [cons.geneId]
                        }
                    }
                }

        sql.commit()
        return consGeneMap
    }

    private String tryToStoreCase(Case patient) {
        def result = sql.executeInsert("""INSERT INTO Entity (id) values \
        (${patient.identifier})
     ON DUPLICATE KEY UPDATE id=id;""")
        return patient.identifier
    }

    private void tryToStoreVariantCaller(VariantCaller variantCaller) {
        def result = sql.executeInsert("""INSERT INTO VariantCaller (name, version, doi) values \
        (${variantCaller.name},
        ${variantCaller.version},
        ${variantCaller.doi})
     ON DUPLICATE KEY UPDATE id=LAST_INSERT_ID(id);""")
    }

    private String tryToStoreSample(Sample sample) {
        def result = sql.executeInsert("""INSERT INTO Sample (identifier, cancerEntity) values \
        (${sample.identifier},
        ${sample.cancerEntity})
     ON DUPLICATE KEY UPDATE identifier=identifier;""")
        return sample.identifier
    }

    private String tryToStoreSampleWithCase(Sample sample, String caseId) {
        def result = sql.executeInsert("""INSERT INTO Sample (identifier, Entity_id, cancerEntity) values \
        (${sample.identifier},
        ${caseId},
        ${sample.cancerEntity})
     ON DUPLICATE KEY UPDATE identifier=identifier;""")
        return sample.identifier
    }

    private void tryToStoreAnnotationSoftware(Annotation annotationSoftware) {
        def result = sql.executeInsert("""INSERT INTO AnnotationSoftware (name, version, doi) values \
        (${annotationSoftware.name},
        ${annotationSoftware.version},
        ${annotationSoftware.doi})
     ON DUPLICATE KEY UPDATE id=LAST_INSERT_ID(id);""")
    }

    private void tryToStoreReferenceGenome(ReferenceGenome referenceGenome) {
        def result = sql.executeInsert("""INSERT INTO ReferenceGenome (source, build, version) values \
        (${referenceGenome.source},
        ${referenceGenome.build},
        ${referenceGenome.version})
     ON DUPLICATE KEY UPDATE id=id;""")
    }

    private Integer tryToFindReferenceGenome(ReferenceGenome referenceGenome) {
        def result =
                sql.firstRow("SELECT id FROM ReferenceGenome WHERE ReferenceGenome.source=? and ReferenceGenome.build=? and ReferenceGenome.version=?",
                        [referenceGenome.source, referenceGenome.build, referenceGenome.version])
        return result.id
    }

    private List<String> tryToStoreGenes(List<String> genes) {
        sql.withBatch("insert INTO Gene (geneId) values (?) ON DUPLICATE KEY UPDATE id=id")
                { BatchingPreparedStatementWrapper ps ->
                    genes.each { identifier ->
                        ps.addBatch([identifier])
                    }
                }

        sql.commit()
        return genes
    }

    private List<Gene> tryToStoreGeneObjects(List<Gene> genes) {
        sql.withBatch("insert INTO Gene (symbol, name, bioType, chr, start, end, synonyms, geneId, description, strand, version) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE id=id")
                { BatchingPreparedStatementWrapper ps ->
                    genes.each { gene ->
                        log.info(gene.toString())
                        ps.addBatch([gene.symbol, gene.name, gene.bioType, gene.chromosome, gene.geneStart, gene.geneEnd, gene.synonyms[0], gene.geneId, gene.description, gene.strand, gene.version])
                    }
                }

        sql.commit()
        return genes
    }

    private void tryToStoreEnsemblDB(Integer version, String date, Integer referenceGenomeId) {
        def result = sql.executeInsert("""INSERT INTO Ensembl (version, date, ReferenceGenome_id) values \
        ($version,
        $date,
        $referenceGenomeId)
     ON DUPLICATE KEY UPDATE id=id;""")
    }

    private Integer tryToFindEnsemblDB(Integer version, String date, Integer rgId) {
        def result =
                sql.firstRow("SELECT id FROM Ensembl WHERE Ensembl.version=? and Ensembl.date=? and Ensembl.ReferenceGenome_id=?",
                        [version, date, rgId])
        return result.id
    }

    private Integer tryToFindVariantCaller(VariantCaller variantCaller) {
        def result =
                sql.firstRow("SELECT id FROM VariantCaller WHERE VariantCaller.name=? and VariantCaller.version=? and VariantCaller.DOI=?",
                        [variantCaller.name, variantCaller.version, variantCaller.doi])
        return result.id
    }

    private Integer tryToFindAnnotationSoftware(Annotation annotation) {
        def result =
                sql.firstRow("SELECT id FROM AnnotationSoftware WHERE AnnotationSoftware.name=? and AnnotationSoftware.version=? and AnnotationSoftware.DOI=?",
                        [annotation.name, annotation.version, annotation.doi])
        return result.id
    }

    private static Case convertRowResultToCase(GroovyRowResult row) {
        def entity = new Case()
        entity.setIdentifier(row.get("id") as String)
        entity.setProjectId(row.get("Project_id") as String)
        return entity
    }

    private static Sample convertRowResultToSample(GroovyRowResult row) {
        def sample = new Sample()
        sample.setIdentifier(row.get("identifier") as String)
        sample.setCancerEntity(row.get("cancerEntity") as String)
        sample.setCaseId(row.get("Entity_id") as String)
        return sample
    }

    //TODO ADAPT
    private static Gene convertRowResultToGene(GroovyRowResult row) {
        def gene = new Gene()
        gene.setBioType(row.get("bioType") as String)
        gene.setChromosome(row.get("chr") as String)
        gene.setGeneEnd(row.get("end") as BigInteger)
        gene.setGeneId(row.get("geneId") as String)
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

    private static Variant convertRowResultToVariant(GroovyRowResult row, Boolean withConsequence) {
        def variant = new Variant()
        variant.setIdentifier(row.get("varuuid") as String)
        variant.setChromosome(row.get("varchr") as String)
        variant.setStartPosition(row.get("varstart") as BigInteger)
        variant.setEndPosition(row.get("varend") as BigInteger)
        variant.setReferenceAllele(row.get("varref") as String)
        variant.setObservedAllele(row.get("varobs") as String)
        variant.setIsSomatic(row.get("varSomatic") as Boolean)
        if (withConsequence) {
            variant.setConsequences([convertRowResultToConsequence(row)])
        }
        return variant
    }

    private static Consequence convertRowResultToConsequence(GroovyRowResult row) {
        def consequence = new Consequence()
        consequence.allele = row.get("allele") as String
        consequence.codingChange = row.get("codingChange") as String
        consequence.transcriptId = row.get("transcriptId") as String
        consequence.transcriptVersion = row.get("transcriptVersion") as Integer
        consequence.type = row.get("type") as String
        consequence.bioType = row.get("bioType") as String
        consequence.canonical = row.get("canonical") as Boolean
        consequence.aaChange = row.get("aaChange") as String
        consequence.cdnaPosition = row.get("cdnaPosition") as String
        consequence.cdsPosition = row.get("cdsPosition") as String
        consequence.proteinPosition = row.get("proteinPosition") as String
        consequence.proteinLength = row.get("proteinLength") as Integer
        consequence.cdnaLength = row.get("cdnaLength") as Integer
        consequence.cdsLength = row.get("cdsLength") as Integer
        consequence.impact = row.get("impact") as String
        consequence.exon = row.get("exon") as String
        consequence.intron = row.get("intron") as String
        consequence.strand = row.get("strand") as Integer
        consequence.geneSymbol = row.get("geneSymbol") as String
        consequence.geneId = row.get("geneId") as String
        consequence.featureType = row.get("featureType") as String
        consequence.distance = row.get("distance") as Integer
        consequence.warnings = row.get("warnings") as String
        return consequence
    }

    private static List<Variant> parseVariantQueryResult(List<GroovyRowResult> rows, Boolean withConsequence = true) {
        Map<String, List<Variant>> variantsIdMap = rows.collect { convertRowResultToVariant(it, withConsequence) }.groupBy { it.identifier }
        List<Variant> variants = []

        if (!withConsequence) {
            return variantsIdMap.values().toList() as List<Variant>
        }

        variantsIdMap.each { key, value ->
            def consequences = value*.getConsequences().collectMany { [it] }.flatten()

            // in case of e.g. intergenic consequences, we have to join the corresponding identifiers of affected genes and report it as one consequence
            // @TODO investigate alternative to just set the annotated transcript ID as geneID in such cases...
            def groupedConsequences = consequences.groupBy({ it.codingChange }, { it.transcriptId })
            def joinedConsequences = []
            groupedConsequences.each { coding, values ->
                values.each {
                    transcript, cons ->
                        if (cons.size > 1) {
                            Consequence c = (Consequence) cons[0]
                            joinedConsequences.add(c)
                        } else {
                            joinedConsequences.addAll(cons)
                        }
                }
            }
            value[0].consequences = joinedConsequences
            variants.add(value[0])
        }
        return variants
    }
}


@Requires(env = "test")
@Requires(property = "database.schema-uri")
@Singleton
class DatabaseInit implements BeanCreatedEventListener<VariantstoreStorage> {

    String schemaUri
    String dataUri

    DatabaseInit(@Property(name = 'database.schema-uri') schemaUri, @Property(name = 'database.data-uri') dataUri) {
        this.schemaUri = schemaUri
        this.dataUri = dataUri
    }

    VariantstoreStorage onCreated(BeanCreatedEvent<VariantstoreStorage> event) {
        def sqlStatement = new File(schemaUri).text
        def insertStatements = new File(dataUri).text
        MariaDBVariantstoreStorage storage = event.bean as MariaDBVariantstoreStorage
        setupDatabase(storage.dataSource.connection, sqlStatement)
        setupDatabase(storage.dataSource.connection, insertStatements)
        return event.bean
    }

    private static setupDatabase(Connection connection, String sqlStatement) {
        Sql sql = new Sql(connection)
        sql.execute(sqlStatement)
    }
}
