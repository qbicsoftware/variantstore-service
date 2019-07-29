package life.qbic.oncostore.database

import groovy.sql.BatchingPreparedStatementWrapper
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import life.qbic.micronaututils.QBiCDataSource
import life.qbic.oncostore.model.*
import life.qbic.oncostore.parser.MetadataContext
import life.qbic.oncostore.util.IdValidator
import life.qbic.oncostore.util.ListingArguments

import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.constraints.NotNull

@Singleton
class MariaDBOncostoreStorage implements OncostoreStorage{

    private QBiCDataSource dataSource
    private Sql sql

    /* Predefined queries for inserting db entries in junction tables */
    String insertVariantConsequenceJunction = "INSERT INTO Variant_has_Consequence (Variant_id, Consequence_id) VALUES(?, ?) ON DUPLICATE KEY UPDATE Variant_id=Variant_id"
    String insertVariantVariantCallerJunction = "INSERT INTO Variant_has_VariantCaller (VariantCaller_id, Variant_id) VALUES(?, ?) ON DUPLICATE KEY UPDATE VariantCaller_id=VariantCaller_id"
    String insertGeneReferenceGenomeJunction = "INSERT INTO Gene_has_ReferenceGenome (ReferenceGenome_id, Gene_id) VALUES(?, ?) ON DUPLICATE KEY UPDATE ReferenceGenome_id=ReferenceGenome_id"
    String insertAnnotationSoftwareConsequenceJunction = "INSERT INTO AnnotationSoftware_has_Consequence (AnnotationSoftware_id, Consequence_id) VALUE(?, ?) ON DUPLICATE KEY UPDATE AnnotationSoftware_id=AnnotationSoftware_id"
    String insertReferenceGenomeVariantJunction = "INSERT INTO Variant_has_ReferenceGenome (ReferenceGenome_id, Variant_id) VALUE(?,?) ON DUPLICATE KEY UPDATE ReferenceGenome_id=ReferenceGenome_id"
    String insertSampleVariantJunction = "INSERT INTO Sample_has_Variant (Sample_qbicID, Variant_id) VALUE(?,?) ON DUPLICATE KEY UPDATE Sample_qbicID=Sample_qbicID"

    @Inject MariaDBOncostoreStorage(QBiCDataSource dataSource) {
        this.dataSource = dataSource
    }

    @Override
    List<Variant> findVariantsForBeaconResponse(String chromosome, BigInteger start,
                                         String reference, String observed, String assemblyId, ListingArguments args) {
        sql = new Sql(dataSource.connection)
        try {
            def variant = fetchVariantsForBeaconResponse(chromosome, start, reference, observed, assemblyId, args)
            return variant
        }
        catch (Exception e) {
            throw new OncostoreStorageException("Beacon something? $start.", e)
        }
    }

    @Override
    List<Case> findCaseById(String id) {
        //TODO should we check for a specific type of identifier?
        if (id?.trim()) {
            throw new IllegalArgumentException("Invalid case identifier supplied.")
        }
        sql = new Sql(dataSource.connection)
        try {
            def cases = fetchCaseForId(id)
            return cases
        }
        catch (Exception e) {
            throw new OncostoreStorageException("Could not fetch case with identifier $id.", e)
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
            throw new OncostoreStorageException("Could not fetch sample with identifier $id.", e)
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
            throw new OncostoreStorageException("Could not fetch variant with identifier $id.", e)
        }
    }

    @Override
    /**
     * Find gene in store for given gene identifier (ENSEMBL)
     * @param id Gene identifier
     * @return List<Gene> containing the found gene for specified identifier
     */
    List<Gene> findGeneById(String id) {
        // TODO check for valid gene identifier?

        sql = new Sql(dataSource.connection)
        try {
            def genes = fetchGeneForId(id)
            return genes
        }
        catch (Exception e) {
            throw new OncostoreStorageException("Could not fetch gene with identifier $id.", e)
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
            return fetchCases()
        }
        catch (Exception e) {
            sql.close()
            throw new OncostoreStorageException("Could not fetch cases.", e)
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
            throw new OncostoreStorageException("Could not fetch samples.", e)
        }
    }

    @Override
    List<Variant> findVariants(@NotNull ListingArguments args) {
        sql = new Sql(dataSource.connection)
        try {
            if (!args.getChromosome().isPresent() && args.getStartPosition().isPresent()) {
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

            return fetchVariants()
        }
        catch (Exception e) {
            println(e)
            throw new OncostoreStorageException("Could not fetch variants.", e)
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
            throw new OncostoreStorageException("Could not fetch genes.", e)
        }
    }

    private List<Case> fetchCaseForId(String id) {
        def result = sql.rows("""SELECT * FROM Entity WHERE Entity.id=$id;""")
        List<Case> cases = result.collect{ convertRowResultToCase(it)}
        return cases
    }

    private List<Sample> fetchSampleForId(String id) {
        def result = sql.rows("""SELECT * FROM Sample WHERE Sample.qbicID=$id;""")
        List<Sample> sample = result.collect{ convertRowResultToSample(it)}
        return sample
    }

    private List<Variant> fetchVariantForId(String id) {
        def result = sql.rows("""SELECT * FROM Sample WHERE Variant.id=$id;""")
        //Variant variant = convertRowResultToVariant(result)
        //return variant
        return parseVariantQueryResult(result)
    }

    private List<Gene> fetchGeneForId(String id) {
        def result = sql.rows("""SELECT * FROM Gene WHERE Gene.id=$id;""")
        List<Gene> genes = result.collect{ convertRowResultToGene(it)}
        return genes
    }

    private List<Case> fetchCases() {
        def result = sql.rows("""SELECT * FROM Case;""")
        List<Case> cases = result.collect( convertRowResultToCase(it))
        return cases
    }

    private List<Sample> fetchSamples() {
        def result = sql.rows("""SELECT * FROM Sample;""")
        List<Sample> samples = result.collect{ convertRowResultToSample(it) }
        return samples
    }

    private List<Variant> fetchVariants() {
        def result = sql.rows("""select * from Variant INNER JOIN Variant_has_Consequence ON Variant.id = Variant_has_Consequence.Variant_id INNER JOIN Consequence on Variant_has_Consequence.Consequence_id = Consequence.id;""")
        return parseVariantQueryResult(result)
    }

    private List<Gene> fetchGenes() {
        def result = sql.rows("""SELECT * FROM Gene;""")
        List<Gene> genes = result.collect{ convertRowResultToGene(it) }
        return genes
    }

    private List<Case> fetchCasesByConsequenceType(String consequenceType) {
        def result = sql.rows("""select disinct * from Entity INNER JOIN Sample ON Entity.id = Sample.Entity_id INNER JOIN Sample_has_Variant ON Sample.qbicID = Sample_has_Variant.Sample_qbicId INNER JOIN Variant ON Variant.id = Sample_has_Variant.Variant_Id INNER JOIN Variant_has_Consequence ON Variant_has_Consequence.Variant_id = Variant.id INNER JOIN Consequence on Variant_has_Consequence.Consequence_id = Consequence.id where Consequence.type = $consequenceType""")
        List<Case> cases = result.collect{ convertRowResultToCase(it) }
        return cases
    }

    private List<Case> fetchCasesByChromosomeAndPositionRange(String chromosome, BigInteger startPosition, BigInteger endPosition) {
        def result = sql.rows("""select distinct Entity.id from Entity INNER JOIN Sample ON Entity.id = Sample.Entity_id INNER JOIN Sample_has_Variant ON Sample.qbicID = Sample_has_Variant.Sample_qbicId INNER JOIN Variant ON Variant.id = Sample_has_Variant.Variant_Id where Variant.chr = $chromosome AND Variant.start >= $startPosition AND Variant.end <= $endPosition;""")
        List<Case> cases = result.collect{ convertRowResultToCase(it) }
        return cases
    }

    private List<Sample> fetchSamplesByCancerEntity(String entity) {
        def result = sql.rows("""SELECT * FROM Sample WHERE Sample.cancerEntity=$entity;""")
        List<Sample> samples = result.collect{ convertRowResultToSample(it) }
        return samples
    }

    private List<Variant> fetchVariantsByChromosome(String chromosome) {
        println(chromosome)
        println(chromosome.getClass())
        def result = sql.rows("""select * from Variant INNER JOIN Variant_has_Consequence ON Variant.id = Variant_has_Consequence.Variant_id INNER JOIN Consequence on Variant_has_Consequence.Consequence_id = Consequence.id where Variant.chr=$chromosome;""")
        //List<Variant> variants = result.collect{ convertRowResultToVariant(it) }
        //return variants
        return parseVariantQueryResult(result)
    }

    private List<Variant> fetchVariantsByStartPosition(BigInteger start) {
        def result = sql.rows("""SELECT * FROM Variant INNER JOIN Variant_has_Consequence ON Variant.id = Variant_has_Consequence.Variant_id INNER JOIN Consequence on Variant_has_Consequence.Consequence_id = Consequence.id WHERE Variant.start=$start;""")
        //List<Variant> variants = result.collect{ convertRowResultToVariant(it) }
        //return variants
        return parseVariantQueryResult(result)
    }

    private List<Variant> fetchVariantsBySample(String sampleId) {
        def result = sql.rows("""select * from Variant INNER JOIN Sample_has_Variant ON Variant.id = Sample_has_Variant.Variant_id INNER JOIN Variant_has_Consequence ON Variant.id = Variant_has_Consequence.Variant_id where Sample_qbicID=$sampleId;""")
        //List<Variant> variants = result.collect{ convertRowResultToVariant(it) }
        //return variants
        return parseVariantQueryResult(result)
    }

    private List<Variant> fetchVariantsBySampleAndGeneId(String sampleId, String geneId) {
        def result = sql.rows("""select distinct * from Variant INNER JOIN Variant_has_Consequence ON Variant.id = Variant_has_Consequence.Variant_id INNER JOIN Sample_has_Variant ON Sample_has_Variant.Variant_id = Variant_has_Consequence.Variant_id INNER JOIN Consequence ON Variant_has_Consequence.Consequence_id = Consequence.id where Sample_qbicID = $sampleId AND Gene_id=$geneId;""")
        return parseVariantQueryResult(result)
    }

    private List<Variant> fetchVariantsForBeaconResponse(String chromosome, BigInteger start,
                                                         String reference, String observed, String assemblyId, ListingArguments args) {
        def result = sql.rows("""select * from Variant INNER JOIN Variant_has_ReferenceGenome ON Variant.id = Variant_has_ReferenceGenome.Variant_id INNER JOIN ReferenceGenome on Variant_has_ReferenceGenome.ReferenceGenome_id = ReferenceGenome.id where ReferenceGenome.build=$assemblyId and Variant.chr=$chromosome and Variant.start=$start and Variant.ref=$reference and Variant.obs=$observed;""")
        List<Variant> variants = result.collect{ convertRowResultToVariant(it) }
        return variants
    }

    private List<Gene> fetchGenesBySample(String sampleId) {
        def result = sql.rows("""select distinct Gene_id from Consequence INNER JOIN Variant_has_Consequence ON Consequence.id = Variant_has_Consequence.Consequence_id INNER JOIN Sample_has_Variant ON Sample_has_Variant.Variant_id = Variant_has_Consequence.Variant_id where Sample_qbicID=$sampleId;""")
        List<Gene> genes = result.collect{ convertRowResultToGene(it) }
        return genes
    }

    @Override
    void storeCaseInStore(Case patient) throws OncostoreStorageException {
        this.sql = new Sql(dataSource.connection)
        try {
            tryToStoreCase(patient)
            sql.close()
        } catch (Exception e) {
            sql.close()
            throw new OncostoreStorageException("Could not store case in store: $patient", e)
        }
    }

    @Override
    void storeSampleInStore(Sample sample) throws OncostoreStorageException {
        this.sql = new Sql(dataSource.connection)
        try {
            tryToStoreSample(sample)
            sql.close()
        } catch (Exception e) {
            sql.close()
            throw new OncostoreStorageException("Could not store sample in store: $sample", e)
        }
    }

    @Override
    void storeReferenceGenomeInStore(ReferenceGenome referenceGenome) throws OncostoreStorageException {
        this.sql = new Sql(dataSource.connection)
        try {
            tryToStoreReferenceGenome(referenceGenome)
            sql.close()
        } catch (Exception e) {
            sql.close()
            throw new OncostoreStorageException("Could not store reference genome in store: $referenceGenome", e)
        }
    }

    @Override
    void storeVariantCallerInStore(VariantCaller variantCaller) throws OncostoreStorageException {
        this.sql = new Sql(dataSource.connection)
        try {
            tryToStoreVariantCaller(variantCaller)
            sql.close()
        } catch (Exception e) {
            sql.close()
            throw new OncostoreStorageException("Could not store variant calling software in store: $variantCaller", e)
        }
    }

    @Override
    void storeAnnotationSoftwareInStore(Annotation annotationSoftware) throws OncostoreStorageException {
        this.sql = new Sql(dataSource.connection)
        try {
            tryToStoreAnnotationSoftware(annotationSoftware)
            sql.close()
        } catch (Exception e) {
            sql.close()
            throw new OncostoreStorageException("Could not store annotation software in store: $annotationSoftware", e)
        }
    }

    @Override
    void storeVariantsInStoreWithMetadata(MetadataContext metadata, List<SimpleVariantContext> variants) throws OncostoreStorageException {
        this.sql = new Sql(dataSource.connection)
        try {
            def genesToInsert = []
            def cId = tryToStoreCase(metadata.getCase())
            def sId = tryToStoreSampleWithCase(metadata.getSample(), cId)
            def vcId = tryToStoreVariantCaller(metadata.getVariantCalling())
            def asId = tryToStoreAnnotationSoftware(metadata.getVariantAnnotation())
            println("Connection")
            println(this.sql.connection)
            def rgId = tryToStoreReferenceGenome(metadata.getReferenceGenome())

            def variantsToInsert = []
            def allConsequencesToInsert = []
            def geneIds = []

            variants.each { variant ->
                def consequencesToInsert = []
                variant.getConsequences().each { consequence ->
                    genesToInsert.add(consequence.getGeneID())
                }

                geneIds.addAll(tryToStoreGenes(genesToInsert))

                variant.getConsequences().each { consequence ->
                    consequencesToInsert.add(tryToStoreConsequence(consequence))
                }

                allConsequencesToInsert.addAll(consequencesToInsert)
                def varId = tryToStoreVariant(variant)
                tryToStoreJunctionBatch(varId, consequencesToInsert, insertVariantConsequenceJunction)

                variantsToInsert.add(varId)
            }

            /* INSERT consequences and annotation software junction */
            tryToStoreJunctionBatch(asId, allConsequencesToInsert, insertAnnotationSoftwareConsequenceJunction)

            /* INSERT sample and variants junction */
            tryToStoreJunctionBatch(sId, variantsToInsert, insertSampleVariantJunction)

            /* INSERT reference genome and genes junction */
            tryToStoreJunctionBatch(rgId, geneIds, insertGeneReferenceGenomeJunction)

            /* INSERT variants and variant caller in junction table */
            tryToStoreJunctionBatch(vcId, variantsToInsert, insertVariantVariantCallerJunction)

            /* INSERT variants and reference genome in junction table */
            tryToStoreJunctionBatch(rgId, variantsToInsert, insertReferenceGenomeVariantJunction)

            sql.close()
        } catch (Exception e) {
            sql.close()
            println(e)
            throw new OncostoreStorageException("Could not store variants with metadata in store: $metadata", e)
        }
    }

    void tryToStoreJunctionBatch(Object id, List ids, String insertStatement){
            def result =
                            sql.withBatch(500, insertStatement)
                                {  ps ->
                                    [[id], ids].combinations().each { key1, key2 ->
                                        if (key1 instanceof String)
                                            key1 = (String) key1
                                        else
                                            key1 = (Integer) key1

                                        if (key2 instanceof String)
                                            key2 = (String) key2
                                        else
                                            key2 = (Integer) key2
                                        ps.addBatch([key1, key2] as List<Object>)
                            }
                        }

    }

    private Integer tryToStoreVariant(SimpleVariantContext variant) {

         def result = sql.executeInsert("""INSERT INTO Variant (uuid, chr, start, end, ref, obs, isSomatic) values \
        (${UUID.randomUUID().toString()},
        ${variant.getChromosome()},
        ${variant.getStartPosition()},
        ${variant.getEndPosition()},
        ${variant.getReferenceAllele()},
        ${variant.getObservedAllele()},
        ${variant.getIsSomatic()})
        ON DUPLICATE KEY UPDATE id=LAST_INSERT_ID(id);""")
        return result.get(0).get(0) as Integer
        /*def result = sql.withBatch(500, 'INSERT INTO Variant (uuid, chr, start, end, ref, obs, isSomatic) OUTPUT INSERTED.id values (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE id=id;')
                { ps ->
            variants.each { v ->
                ps.addBatch(UUID.randomUUID().toString(), v.getChromosome(), v.getStartPosition(), v.getEndPosition(), v.getReferenceAllele(), v.getObservedAllele(), v.getIsSomatic())
            }
        }*/

        //println(result)
    }

    private Integer tryToStoreConsequence(Consequence cons) {
        def result = sql.executeInsert("""INSERT INTO Consequence (codingChange, aaChange, aaStart, aaEnd, type, impact, strand, transcriptID, transcriptVersion, canonical, bioType, refSeqID, Gene_id) values \
        (${cons.codingChange},
        ${cons.aaChange},
        ${cons.aaStart},
        ${cons.aaEnd},
        ${cons.consequenceType},
        ${cons.impact},
        ${cons.strand},
        ${cons.transcriptID},
        ${cons.transcriptVersion},
        ${cons.canonical},
        ${cons.bioType},
        ${cons.refSeqID},
        ${cons.geneID})
        ON DUPLICATE KEY UPDATE id=LAST_INSERT_ID(id);""")
        return result.get(0).get(0) as Integer
    }

    private String tryToStoreCase(Case patient) {
        def result = sql.executeInsert("""INSERT INTO Entity (id) values \
        (${patient.identifier})
     ON DUPLICATE KEY UPDATE id=id;""")
        return patient.identifier
    }

    private Integer tryToStoreVariantCaller(VariantCaller variantCaller) {
        def result = sql.executeInsert("""INSERT INTO VariantCaller (name, version, doi) values \
        (${variantCaller.name},
        ${variantCaller.version},
        ${variantCaller.doi})
     ON DUPLICATE KEY UPDATE id=LAST_INSERT_ID(id);""")
        return result.get(0).get(0) as Integer
    }

    private String tryToStoreSample(Sample sample) {
        def result = sql.executeInsert("""INSERT INTO Sample (qbicID, cancerEntity) values \
        (${sample.identifier},
        ${sample.cancerEntity})
     ON DUPLICATE KEY UPDATE qbicID=qbicID;""")
        return sample.identifier
    }

    private String tryToStoreSampleWithCase(Sample sample, String caseId) {
        def result = sql.executeInsert("""INSERT INTO Sample (qbicID, Entity_id, cancerEntity) values \
        (${sample.identifier},
        ${caseId},
        ${sample.cancerEntity})
     ON DUPLICATE KEY UPDATE qbicID=qbicID;""")
        return sample.identifier
    }

    private Integer tryToStoreAnnotationSoftware(Annotation annotationSoftware) {
        def result = sql.executeInsert("""INSERT INTO AnnotationSoftware (name, version, doi) values \
        (${annotationSoftware.name},
        ${annotationSoftware.version},
        ${annotationSoftware.doi})
     ON DUPLICATE KEY UPDATE id=LAST_INSERT_ID(id);""")
        return result.get(0).get(0) as Integer
    }

    private Integer tryToStoreReferenceGenome(ReferenceGenome referenceGenome) {
        def result = sql.executeInsert("""INSERT INTO ReferenceGenome (source, build, version) values \
        (${referenceGenome.source},
        ${referenceGenome.build},
        ${referenceGenome.version})
     ON DUPLICATE KEY UPDATE id=LAST_INSERT_ID(id);""")
        return result.get(0).get(0) as Integer
    }

    private List<String> tryToStoreGenes(List<String> genes) {
        def result = sql.withBatch(500, "insert INTO Gene (id) values (?) ON DUPLICATE KEY UPDATE id=id")
                { BatchingPreparedStatementWrapper ps ->
                    genes.each { identifier ->
                        ps.addBatch([identifier])
                    }
                }
        return genes
    }

    private static Case convertRowResultToCase(GroovyRowResult row) {
        def entity = new Case()
        entity.setIdentifier(row.get("id") as String)
        entity.setProjectId(row.get("Project_id") as String)
        return entity
    }

    private static Sample convertRowResultToSample(GroovyRowResult row) {
        def sample = new Sample()
        sample.setIdentifier(row.get("qbicID") as String)
        sample.setCancerEntity(row.get("Entity_id") as String)
        sample.setCaseID(row.get("cancerEntity") as String)
        return sample
    }

    private static Gene convertRowResultToGene(GroovyRowResult row) {
        def gene = new Gene()
        gene.setBioType(row.get("bioType") as String)
        //TODO check if string or int?
        gene.setChromosome(row.get("chr") as String)
        gene.setGeneEnd(row.get("end") as BigInteger)
        gene.setGeneID(row.get("id") as String)
        gene.setGeneStart(row.get("start") as BigInteger)
        gene.setName(row.get("name") as String)
        gene.setSymbol(row.get("symbol") as String)
        //TODO list of synonyms ?
        gene.setSynonyms([row.get("synonyms") as String])
        return gene
    }

    private static Variant convertRowResultToVariant(GroovyRowResult row) {
        def variant = new Variant()
        variant.setIdentifier(row.get("uuid") as String)
        variant.setChromosome(row.get("chr") as String)
        variant.setStartPosition(row.get("start") as BigInteger)
        variant.setEndPosition(row.get("end") as BigInteger)
        variant.setReferenceAllele(row.get("ref") as String)
        variant.setObservedAllele(row.get("obs") as String)
        variant.setIsSomatic(row.get("isSomatic") as Boolean)
        println(row)
        variant.setConsequences([convertRowResultToConsequence(row)])
        println("yes")
        return variant
    }

    private static Consequence convertRowResultToConsequence(GroovyRowResult row) {
        def consequence = new Consequence()
        consequence.setCodingChange(row.get("codingChange") as String)
        consequence.setTranscriptID(row.get("transcriptID") as String)
        consequence.setTranscriptVersion(row.get("transcriptVersion") as Integer)
        consequence.setRefSeqID(row.get("refSeqID") as String)
        consequence.setConsequenceType(row.get("type") as String)
        consequence.setBioType(row.get("bioType") as String)
        consequence.setCanonical(row.get("canonical") as Boolean)
        consequence.setAaChange(row.get("aaChange") as String)
        consequence.setAaStart(row.get("aaStart") as Integer)
        consequence.setAaEnd(row.get("aaEnd") as Integer)
        consequence.setImpact(row.get("impact") as String)
        consequence.setGeneID(row.get("Gene_id") as String)
        consequence.setStrand(row.get("strand") as Integer)
        return consequence
    }

    private static List<Variant> parseVariantQueryResult(List<GroovyRowResult> rows) {
        Map<String, List<Variant>> variantsIdMap = rows.collect{ convertRowResultToVariant(it) }.groupBy{ it.identifier }
        List<Variant> variants = []
        variantsIdMap.each { key, value ->
            //def consequences = value*.getConsequences().collectMany {[it]}
            //value[0].consequences = consequences
            //TODO check that
            variants.add(value[0])
        }
        return variants
    }
}

/*
@Requires(env="test")
@Requires(property="database.schema-uri")
@Singleton
class DatabaseInit implements BeanCreatedEventListener<OncostoreStorage> {

    String schemaUri

    DatabaseInit(@Property(name='database.schema-uri') schemaUri) {
        this.schemaUri = schemaUri
    }

    OncostoreStorage onCreated(BeanCreatedEvent<OncostoreStorage> event) {
        def sqlStatement = new File(schemaUri).text
        MariaDBOncostoreStorage storage = event.bean as MariaDBOncostoreStorage
        setupDatabase(storage.dataSource.connection, sqlStatement)
        return event.bean
    }

    private static setupDatabase(Connection connection, String sqlStatement) {
        Sql sql = new Sql(connection)
        sql.execute(sqlStatement)
    }
}
*/
