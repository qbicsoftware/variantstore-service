package life.qbic.oncostore.database

import groovy.sql.GroovyRowResult
import groovy.sql.Sql

import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.context.event.BeanCreatedEvent
import io.micronaut.context.event.BeanCreatedEventListener
import life.qbic.micronaututils.QBiCDataSource
import life.qbic.oncostore.controller.VariantController
import life.qbic.oncostore.model.Annotation
import life.qbic.oncostore.model.Consequence
import life.qbic.oncostore.model.ReferenceGenome
import life.qbic.oncostore.model.Sample
import life.qbic.oncostore.model.SimpleVariantContext
import life.qbic.oncostore.model.Variant
import life.qbic.oncostore.model.VariantCaller
import life.qbic.oncostore.util.IdValidator
import life.qbic.oncostore.util.ListingArguments
import sun.java2d.pipe.SpanShapeRenderer.Simple

import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.constraints.NotNull

import java.sql.Connection

@Singleton
class MariaDBOncostoreStorage implements OncostoreStorage{

    private QBiCDataSource dataSource
    private Sql sql

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
            throw new OncostoreStorageException("Beacon something? $id.", e)
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
            throw new OncostoreStorageException("Could not fetch sample with id $id.", e)
        }
    }

    @Override
    List<Variant> findVariantById(String id) {
        if (!IdValidator.isValidUUID(identifier)) {
            throw new IllegalArgumentException("Invalid variant identifier supplied.")
        }
        sql = new Sql(dataSource.connection)
        try {
            def variant = fetchVariantForId(id)
            return variant
        }
        catch (Exception e) {
            throw new OncostoreStorageException("Could not fetch variant with id $id.", e)
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
            if (args.getChromosome().isPresent()) {
                return fetchVariantsByChromosome(args.getChromosome().get())
            }

            if (!args.getChromosome().isPresent() && args.getStartPosition().isPresent()) {
                return fetchVariantsByStartPosition(args.getStartPosition().get())
            }

            if (args.getSampleId().isPresent()) {
                return fetchVariantsBySample(args.getSampleId().get())
            }

            return fetchVariants()
        }
        catch (Exception e) {
            throw new OncostoreStorageException("Could not fetch variants.", e)
        }
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

    private List<Sample> fetchSamples() {
        def result = sql.rows("""SELECT * FROM Sample;""")
        System.out.println(result)

        List<Sample> samples = result.collect{ convertRowResultToSample(it) }
        return samples
    }

    private List<Variant> fetchVariants() {
        def result = sql.rows("""SELECT * FROM Variant;""")
        return parseVariantQueryResult(result)
    }

    private List<Sample> fetchSamplesByCancerEntity(String entity) {
        def result = sql.rows("""SELECT * FROM Sample WHERE Sample.cancerEntity=$entity;""")
        List<Sample> samples = result.collect{ convertRowResultToSample(it) }
        return samples
    }

    private List<Variant> fetchVariantsByChromosome(Integer chromosome) {
        def result = sql.rows(""""select * from Variant INNER JOIN Variant_has_Consequence ON Variant.id = Variant_has_Consequence.Variant_id INNER JOIN Consequence on Variant_has_Consequence.Consequence_id = Consequence.id where Variant.chr=$chromosome;""")
        //List<Variant> variants = result.collect{ convertRowResultToVariant(it) }
        //return variants
        return parseVariantQueryResult(result)
    }

    private List<Variant> fetchVariantsByStartPosition(BigInteger start) {
        def result = sql.rows(""""select * from Variant INNER JOIN Variant_has_Consequence ON Variant.id = Variant_has_Consequence.Variant_id INNER JOIN Consequence on Variant_has_Consequence.Consequence_id = Consequence.id where Variant.start$start;""")
        //List<Variant> variants = result.collect{ convertRowResultToVariant(it) }
        //return variants
        return parseVariantQueryResult(result)
    }

    private List<Variant> fetchVariantsBySample(String sampleId) {
        def result = sql.rows(""""select * from Variant INNER JOIN Variant_has_Consequence ON Variant.id = Variant_has_Consequence.Variant_id INNER JOIN Consequence on Variant_has_Consequence.Consequence_id = Consequence.id where Variant.start$start;""")
        //List<Variant> variants = result.collect{ convertRowResultToVariant(it) }
        //return variants
        return parseVariantQueryResult(result)
    }

    private List<Variant> fetchVariantsForBeaconResponse(String chromosome, BigInteger start,
                                                         String reference, String observed, String assemblyId, ListingArguments args) {
        def result = sql.rows(""""select * from Variant INNER JOIN Variant_has_ReferenceGenome ON Variant.id = Variant_has_ReferenceGenome.Variant_id INNER JOIN ReferenceGenome on Variant_has_ReferenceGenome.ReferenceGenome_id = ReferenceGenome.id where ReferenceGenome.build=$assemblyId and Variant.chr=$chromosome and Variant.start=$start and Variant.ref=$reference and Variant.obs=$observed;""")
        List<Variant> variants = result.collect{ convertRowResultToVariant(it) }
        return variants
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
            tryToStoreSample(sample)
            sql.close()
        } catch (Exception e) {
            sql.close()
            throw new OncostoreStorageException("Could not store sample in store: $sample", e)
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
            throw new OncostoreStorageException("Could not store sample in store: $sample", e)
        }
    }

    @Override
    void storeAnnotationSoftwareInStore(Annotation annotationSoftware) throws OncostoreStorageException {
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
    void storeVariantsInStore(SimpleVariantContext variantContext) throws OncostoreStorageException {
        this.sql = new Sql(dataSource.connection)
        try {
            tryToStoreVariants(variantContext)
            sql.close()
        } catch (Exception e) {
            sql.close()
            throw new OncostoreStorageException("Could not store sample in store: $sample", e)
        }
    }

    private void tryToStoreVariants(SimpleVariantContext variantContext) {

    }

    private void tryToStoreVariantCaller(VariantCaller variantCaller) {
        def result = sql.executeInsert("""INSERT INTO VariantCaller (name, version, doi) values \
        (${variantCaller.name},
        ${variantCaller.version},
        ${variantCaller.doi})
     ON DUPLICATE KEY UPDATE id=LAST_INSERT_ID(id);""")

        println(result)

        // return result
    }

    private static Sample convertRowResultToSample(GroovyRowResult row) {
        def sample = new Sample()
        sample.setIdentifier(row.get("qbicID") as String)
        sample.setCancerEntity(row.get("Case_id") as String)
        sample.setCaseID(row.get("cancerEntity") as String)
        return sample
    }

    private static Variant convertRowResultToVariant(GroovyRowResult row) {
        def variant = new Variant()
        variant.setIdentifier(row.get("Variant_id") as String)
        variant.setChromosome(row.get("chr") as Integer)
        variant.setStartPosition(row.get("start") as BigInteger)
        variant.setEndPosition(row.get("end") as BigInteger)
        variant.setReferenceAllele(row.get("ref") as String)
        variant.setObservedAllele(row.get("obs") as String)
        variant.setIsSomatic(row.get("isSomatic") as Boolean)
        variant.setConsequences([convertRowResultToConsequence(row)])
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
            def consequences = value*.getConsequences().collectMany {it}
            variants.add(value[0].consequences = consequences)
        }
        return variants
    }
}

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
