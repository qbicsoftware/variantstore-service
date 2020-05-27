package life.qbic.oncostore.database

import groovy.sql.BatchingPreparedStatementWrapper
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.time.TimeCategory
import groovy.time.TimeDuration
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
    String insertVariantConsequenceJunction = "INSERT INTO variant_has_consequence (variant_id, consequence_id) " +
            "VALUES (?, ?) ON DUPLICATE KEY UPDATE variant_id=variant_id"
    String insertVariantVariantCallerJunction = "INSERT INTO variant_has_variantcaller (variantcaller_id, variant_id)" + "" + " VALUES (?, ?) ON DUPLICATE KEY UPDATE variantcaller_id=variantcaller_id"
    String insertAnnotationSoftwareConsequenceJunction = "INSERT INTO annotationsoftware_has_consequence " + "" + "(annotationsoftware_id, consequence_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE annotationsoftware_id=annotationsoftware_id"
    String insertReferenceGenomeVariantJunction = "INSERT INTO variant_has_referencegenome (referencegenome_id, " +
            "variant_id) VALUES (?,?) ON DUPLICATE KEY UPDATE referencegenome_id=referencegenome_id"
    //String insertSampleVariantJunction = "INSERT INTO sample_has_variant (sample_identifier, variant_id) VALUES (?,
    // ?) ON DUPLICATE KEY UPDATE sample_identifier=sample_identifier"
    String insertSampleVariantJunction = "INSERT INTO sample_has_variant (sample_id, variant_id, vcfinfo_id, genotype_id) " + "" + "VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE sample_id=sample_id"

    String insertEnsemblGeneJunction = "INSERT INTO ensembl_has_gene (ensembl_id, gene_id) VALUES (?,?) ON DUPLICATE " + "" + "KEY UPDATE ensembl_id=ensembl_id"
    String insertConsequenceGeneJunction = "INSERT INTO consequence_has_gene (consequence_id, gene_id) VALUES (?,?) "+ "ON DUPLICATE KEY UPDATE consequence_id=consequence_id"

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
        } catch (Exception e) {
            throw new VariantstoreStorageException("Beacon something? $e.", e.fillInStackTrace())
        } finally {
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
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch case with identifier $id.", e.fillInStackTrace())
        } finally {
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
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch sample with identifier $id.", e.fillInStackTrace())
        } finally {
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
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch variant with identifier $id.", e.printStackTrace())
        } finally {
            sql.close()
        }
    }

    @Override
    /**
     * Find gene in store for given gene identifier (ENSEMBL)
     * @param id gene identifier
     * @return List<gene>    containing the found gene for specified identifier
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
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch gene with identifier $id.", e.printStackTrace())
        } finally {
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

            if (args.getChromosome().isPresent() && args.getStartPosition().isPresent() && args.getEndPosition()
                    .isPresent()) {
                return fetchCasesByChromosomeAndPositionRange(args.getChromosome().get(), args.getStartPosition().get
                        (), args.getEndPosition().get())
            }

            if (args.getChromosome().isPresent()) {
                return fetchCasesByChromosome(args.getChromosome().get())
            }
            return fetchCases()
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch cases.", e.printStackTrace())
        } finally {
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
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch samples.", e.fillInStackTrace())
        } finally {
            sql.close()
        }
    }

    @Override
    List<Variant> findVariants(@NotNull ListingArguments args) {
        sql = new Sql(dataSource.connection)
        try {
            if (args.getChromosome().isPresent() && args.getStartPosition().isPresent()) {
                return fetchVariantsByChromosomeAndStartPosition(args.getChromosome().get(), args.getStartPosition()
                        .get())
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
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch variants.", e.fillInStackTrace())
        } finally {
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
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch genes.", e.fillInStackTrace())
        } finally {
            sql.close()
        }
    }

    private Integer fetchEnsemblVersion() {
        def result =
                sql.firstRow("SELECT MAX(version) AS version FROM ensembl")
        return result.version
    }

    @Override
    void storeCaseInStore(Case patient) throws VariantstoreStorageException {
        this.sql = new Sql(dataSource.connection)
        try {
            tryToStoreCase(patient)
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not store case in store: $patient", e)
        } finally {
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
        } finally {
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
        } finally {
            sql.close()
        }
    }

    @Override
    void storeVariantCallerInStore(VariantCaller variantCaller) throws VariantstoreStorageException {
        this.sql = new Sql(dataSource.connection)
        try {
            tryToStoreVariantCaller(variantCaller)
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not store variant calling software in store: " +
                    "$variantCaller", e)
        } finally {
            sql.close()
        }
    }

    @Override
    void storeAnnotationSoftwareInStore(Annotation annotationSoftware) throws VariantstoreStorageException {
        this.sql = new Sql(dataSource.connection)
        try {
            tryToStoreAnnotationSoftware(annotationSoftware)
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not store annotation software in store: " +
                    "$annotationSoftware", e)
        } finally {
            sql.close()
        }
    }

    @Override
    void storeVariantsInStoreWithMetadata(MetadataContext metadata, List<SimpleVariantContext> variants) throws
            VariantstoreStorageException {
        this.sql = new Sql(dataSource.connection)

        try {
            // get samples from genotypes for registration
            def samplesIds = []
            variants.get(0).genotypes.each {genotype ->
                samplesIds.add(genotype.sampleName)
            }

            def cId = tryToStoreCase(metadata.getCase())
            def samples = samplesIds.collect {identifier -> new Sample(identifier.toString())}

            if (!samples.empty) tryToStoreSamplesWithCase(samples, cId)

            //def sId = tryToStoreSampleWithCase(metadata.getSample(), cId)

            tryToStoreVariantCaller(metadata.getVariantCalling())
            tryToStoreAnnotationSoftware(metadata.getVariantAnnotation())
            tryToStoreReferenceGenome(metadata.getReferenceGenome())

            def timeStart = new Date()
            def vcId = tryToFindVariantCaller(metadata.getVariantCalling())
            def asId = tryToFindAnnotationSoftware(metadata.getVariantAnnotation())
            def rgId = tryToFindReferenceGenome(metadata.getReferenceGenome())
            def sIds = tryToFindSamples(samples, cId)
            def timeStop = new Date()
            TimeDuration duration = TimeCategory.minus(timeStop, timeStart)
            println("Metadata insertion took..." + duration)

            sql.connection.autoCommit = false
            sql.setCacheStatements(true)

            timeStart = new Date()
            /* INSERT variants and save consequences for import */
            def consequencesToInsert = !variants.isEmpty() ? tryToStoreVariantsBatch(variants) : []
            tryToStoreVariantInfo(variants)
            tryToStoreVariantGenotypes(variants)
            timeStop = new Date()
            duration = TimeCategory.minus(timeStop, timeStart)
            println("Variant insertion took..." + duration)

            timeStart = new Date()
            /* INSERT consequences */
            def consGeneMap = !consequencesToInsert.isEmpty() ? tryToStoreConsequencesBatch(consequencesToInsert as
                    List<Consequence>) : [:]
            timeStop = new Date()
            duration = TimeCategory.minus(timeStop, timeStart)
            println("Consequence insertion took..." + duration)

            timeStart = new Date()
            /* INSERT genes */
            if (!consGeneMap.values().isEmpty()) tryToStoreGenes(consGeneMap.values().toList().flatten() as
                    List<String>)
            timeStop = new Date()
            duration = TimeCategory.minus(timeStop, timeStart)
            println("Gene insertion took..." + duration)

            timeStart = new Date()
            /* GET ids of genes */
            def geneIdMap = !consGeneMap.isEmpty() ? tryToFindGenesByConsequence(consGeneMap as HashMap<Consequence,
                    List<String>>) : [:]
            timeStop = new Date()
            duration = TimeCategory.minus(timeStop, timeStart)
            println("Gene lookup took..." + duration)

            timeStart = new Date()
            /* GET ids of variants */
            def variantInsert = tryToFindVariants(variants)
            def variantIdMap = variantInsert.first
            def infoIdMap = variantInsert.second

            println("Variant lookup 1 took..." + duration)

            //TODO optimization potential
            /* GET ids of genotypes */
            def genotypeMap = tryToFindGenotypes(variants)
            timeStop = new Date()
            duration = TimeCategory.minus(timeStop, timeStart)
            println("Variant lookup 2 took..." + duration)

            timeStart = new Date()
            /* GET ids of consequences */
            def findConsequenceMaps = !variants.isEmpty() ? tryToFindConsequences(variants) : new Tuple2<HashMap,
                    HashMap>()
            def variantConsequenceIdMap = !findConsequenceMaps.isEmpty() ? findConsequenceMaps.first : [:]
            /* consequence to consequence DB id map */
            def consIdMap = !findConsequenceMaps.isEmpty() ? findConsequenceMaps.second : [:]
            timeStop = new Date()
            duration = TimeCategory.minus(timeStop, timeStart)
            println("Consequence lookup took..." + duration)

            timeStart = new Date()
            /* INSERT variant and consequence junction */
            if (!variantConsequenceIdMap.values().flatten().isEmpty())
                tryToStoreJunctionBatchFromMap(variantConsequenceIdMap as HashMap, variantIdMap, insertVariantConsequenceJunction)
            timeStop = new Date()
            duration = TimeCategory.minus(timeStop, timeStart)
            println("Variant Consequence Batch insertion took..." + duration)

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
            sql.close()
        }
    }

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

    Tuple2<HashMap, HashMap> tryToFindVariants(List<SimpleVariantContext> variants) {
        def ids = [:]
        def infoIds = [:]

        variants.each { var ->
            if (ids[var]) {
                return
            } else {
                def result =
                        sql.firstRow("SELECT id FROM variant WHERE variant.chr=? and variant.start=? and variant" + "" + ".end=? and variant.ref=? and variant.obs=? and variant.issomatic=?",
                                [var.chromosome, var.startPosition, var.endPosition, var.referenceAllele, var
                                        .observedAllele, var.isSomatic])
                ids[var] = result.id

                def props = VcfInfo.declaredFields.findAll { !it.synthetic }.collect { it }
                def infoValues = []
                props.each { prop ->
                    if (prop.type == List) {
                        def value = var.vcfInfo.properties.get(prop.name)
                        def valDb = value ? infoValues.toString() : "[]"
                        infoValues.add(valDb)
                    } else {
                        infoValues.add(var.vcfInfo.properties.get(prop.name))
                    }
                }

                def result_vcfinfo = sql.firstRow("SELECT id FROM vcfinfo WHERE vcfinfo.ancestralallele=? and " + "vcfinfo" + ".allelecount=? and vcfinfo.allelefreq=? and vcfinfo.numberalleles=? and vcfinfo.basequality=? and vcfinfo.cigar=? and vcfinfo.dbsnp=? and vcfinfo.hapmaptwo=? and vcfinfo.hapmapthree=? and vcfinfo.thousandgenomes=? and vcfinfo.combineddepth=? and vcfinfo.endpos=? and vcfinfo.rms=? and vcfinfo.mqzero=? and vcfinfo.strandbias=? and vcfinfo.numbersamples=? and vcfinfo.somatic=? and vcfinfo.validated=?",
                        infoValues)

                infoIds[var] = result_vcfinfo.id
            }
        }
        return new Tuple2(ids, infoIds)
    }

    HashMap tryToFindGenotypes(List<SimpleVariantContext> variants) {
        def ids = [:].withDefault {[:]}
        def genotype_ids = [:]

        variants.each { var ->
            def props = Genotype.declaredFields.findAll { !it.synthetic && it.name != "sampleName"}.collect { it }
            var.genotypes.each { genotype ->

                if(genotype_ids[genotype]) {
                    ids[var][genotype.sampleName] = genotype_ids[genotype]
                }
                else {
                    def genotypeValues = []
                    props.each { prop -> genotypeValues.add(genotype.properties.get(prop.name)) }
                    def result = sql.firstRow("""SELECT id FROM genotype WHERE genotype
.genotype=? and genotype.readdepth=? and genotype.filter=? and genotype.likelihoods=? and genotype
.genotypelikelihoods=? and genotype.genotypelikelihoodshet=? and genotype.posteriorprobs=? and genotype
.genotypequality=? and genotype.haplotypequalities=? and genotype.phaseset=? and genotype.phasingQuality=? and genotype.alternateallelecounts=? and 
genotype.mappingquality=?""",
                            genotypeValues)

                    genotype_ids[genotype] = result.id
                    ids[var][genotype.sampleName] = result.id
                }
            }
        }
        return ids
    }

    Tuple2<HashMap, HashMap> tryToFindConsequences(List<SimpleVariantContext> variants) {
        def ids = [:]
        def consIdMap = [:]

        variants.each { var ->
            def consIds = []
            var.getConsequences().each { cons ->
                if (consIdMap[cons]) {
                    consIds.add(consIdMap[cons])
                } else {
                    def result = sql.firstRow("SELECT id FROM consequence WHERE consequence.allele=? and consequence"
                            + ".codingchange=? and consequence.transcriptid=? and consequence.transcriptversion=? and" + " consequence.type=? and consequence.biotype=? and consequence.canonical=? and consequence.aachange=? and consequence.cdnaposition=? and consequence.cdsposition=? and consequence.proteinposition=? and consequence.proteinlength=? and consequence.cdnalength=? and consequence.cdslength=? and consequence.impact=? and consequence.exon=? and consequence.intron=? and consequence.strand=? and consequence.genesymbol=? and consequence.featuretype=? and consequence.distance=? and consequence.warnings=?",

                            [cons.allele, cons.codingChange, cons.transcriptId, cons.transcriptVersion, cons.type,
                             cons.bioType, cons.canonical, cons.aaChange, cons.cdnaPosition, cons.cdsPosition, cons
                                     .proteinPosition, cons.proteinLength, cons.cdnaLength, cons.cdsLength, cons
                                     .impact, cons.exon, cons.intron, cons.strand, cons.geneSymbol, cons.featureType,
                             cons.distance, cons.warnings])
                    consIds.add(result.id)
                    consIdMap[cons] = result.id
                }
            }
            ids[var] = consIds
        }
        return new Tuple2(ids, consIdMap)
    }

    HashMap tryToFindGenes(List<Gene> genes) {
        def ids = [:]

        genes.each { gene ->
            if (ids[gene]) {
                return
            } else {
                def result =
                        sql.firstRow("SELECT id FROM gene WHERE gene.symbol=? and gene.name=? and gene.biotype=? and " + "" + "gene.chr=? and gene.start=? and gene.end=? and gene.synonyms=? and gene.geneid=? and gene.description=? and gene.strand=? and gene.version=?",

                                [gene.symbol, gene.name, gene.bioType, gene.chromosome, gene.geneStart, gene.geneEnd,
                                 gene.synonyms[0], gene.geneId, gene.description, gene.strand, gene.version])
                ids[gene] = result.id
            }
        }
        return ids
    }

    HashMap tryToFindSamples(List<Sample> samples, String cId) {
        def ids = [:]

        samples.each { sample ->
            if (ids[sample]) {
                return
            } else {
                def result =
                        sql.firstRow("SELECT id FROM sample WHERE sample.identifier=? and sample.entity_id=? and sample.cancerentity=?",

                                [sample.identifier, cId, sample.cancerEntity])
                ids[sample] = result.id
            }
        }
        return ids
    }

    private HashMap<Consequence, List<String>> tryToFindGenesByConsequence(HashMap<Consequence, List<String>>
                                                                                   consequenceToGeneIds) {
        def ids = [:]
        def foundIds = [:]

        consequenceToGeneIds.each { cons, geneIds ->
            def geneDBids = []
            geneIds.each { geneId ->
                if (foundIds[geneId]) {
                    geneDBids.add(foundIds[geneId])
                } else {
                    def result = sql.firstRow("SELECT id FROM gene WHERE gene.geneid=?", [geneId])
                    geneDBids.add(result.id)
                    foundIds[geneId] = result.id
                }
            }

            ids[cons] = geneDBids
        }
        return ids as HashMap<Consequence, List<String>>
    }

    private List<Case> fetchCaseForId(String id) {
        def result = sql.rows("""SELECT distinct entity.id, project_id FROM entity WHERE entity.id=$id;""")
        List<Case> cases = result.collect { convertRowResultToCase(it) }
        return cases
    }

    private List<Sample> fetchSampleForId(String id) {
        def result = sql.rows("""SELECT * FROM sample WHERE sample.identifier=$id;""")
        List<Sample> sample = result.collect { convertRowResultToSample(it) }
        return sample
    }

    private List<Variant> fetchVariantForId(String id) {
        def result = sql.rows("""SELECT variant.id as varid, variant.chr as varchr, variant.start as varstart, 
variant.end as varend, variant.ref as varref, variant.obs as varobs, variant.issomatic as varsomatic, variant.uuid as
 varuuid, consequence.*, gene.id as geneindex, gene.geneid as geneid FROM variant INNER JOIN variant_has_consequence 
 ON variant.id = variant_has_consequence.variant_id INNER JOIN consequence on variant_has_consequence.consequence_id 
 = consequence.id INNER JOIN consequence_has_gene on consequence_has_gene.consequence_id = consequence.id INNER JOIN 
 gene on gene.id=consequence_has_gene.gene_id WHERE variant.uuid=$id;""")
        return parseVariantQueryResult(result, true)
    }

    private List<Gene> fetchGeneForId(String id) {
        def result = sql.rows("""SELECT distinct * FROM gene WHERE gene.geneid=$id;""")
        List<Gene> genes = result.collect { convertRowResultTogene(it) }
        return genes
    }

    private List<Gene> fetchGeneForIdWithEnsemblVersion(String id, Integer ensemblVersion) {
        def result = sql.rows("""SELECT distinct * FROM gene INNER JOIN ensembl_has_gene ON gene.id = 
Ensembl_has_gene.gene_id INNER JOIN ensembl ON ensembl_has_gene.ensembl_id = ensembl.id WHERE gene.geneid=$id and 
ensembl.version=$ensemblVersion;""")
        List<Gene> genes = result.collect { convertRowResultTogene(it) }
        return genes
    }

    private List<Case> fetchCases() {
        def result = sql.rows("""SELECT * FROM entity;""")
        List<Case> cases = result.collect { convertRowResultToCase(it) }
        return cases
    }

    private List<Sample> fetchSamples() {
        def result = sql.rows("""SELECT * FROM sample;""")
        List<Sample> samples = result.collect { convertRowResultToSample(it) }
        return samples
    }

    private List<Variant> fetchVariants() {
        def result = sql.rows("""SELECT variant.id as varid, variant.chr as varchr, variant.start as varstart, 
variant.end as varend, variant.ref as varref, variant.obs as varobs, variant.issomatic as varsomatic, variant.uuid as
 varuuid FROM variant;""")
        return parseVariantQueryResult(result, false)
    }

    private List<Gene> fetchGenes() {
        def result = sql.rows("""SELECT * FROM gene;""")
        List<Gene> genes = result.collect { convertRowResultTogene(it) }
        return genes
    }

    private List<Case> fetchCasesByConsequenceType(String consequenceType) {
        def result = sql.rows("""select distinct entity.id, project_id from entity INNER JOIN sample ON entity.id = 
sample.entity_id INNER JOIN sample_has_variant ON sample.identifier = sample_has_variant.sample_identifier INNER JOIN
 variant ON variant.id = sample_has_variant.variant_id INNER JOIN variant_has_consequence ON variant_has_consequence
 .variant_id = variant.id INNER JOIN consequence on variant_has_consequence.consequence_id = consequence.id where 
 consequence.type = $consequenceType""")
        List<Case> cases = result.collect { convertRowResultToCase(it) }
        return cases
    }

    private List<Case> fetchCasesByChromosome(String chromosome) {
        def result = sql.rows("""select distinct entity.id, project_id from entity INNER JOIN sample ON entity.id = 
sample.entity_id INNER JOIN sample_has_variant ON sample.identifier = sample_has_variant.sample_identifier INNER JOIN
 variant ON variant.id = sample_has_variant.variant_id where variant.chr = $chromosome;""")
        List<Case> cases = result.collect { convertRowResultToCase(it) }
        return cases
    }

    private List<Case> fetchCasesByChromosomeAndPositionRange(String chromosome, BigInteger startPosition, BigInteger
            endPosition) {
        def result = sql.rows("""select distinct entity.id, project_id from entity INNER JOIN sample ON entity.id = 
sample.entity_id INNER JOIN sample_has_variant ON sample.identifier = sample_has_variant.sample_identifier INNER JOIN
 variant ON variant.id = sample_has_variant.variant_id where variant.chr = $chromosome AND variant.start >= 
$startPosition AND variant.end <= $endPosition;""")
        List<Case> cases = result.collect { convertRowResultToCase(it) }
        return cases
    }

    private List<Sample> fetchSamplesByCancerEntity(String entity) {
        def result = sql.rows("""SELECT * FROM sample WHERE sample.cancerentity=$entity;""")
        List<Sample> samples = result.collect { convertRowResultToSample(it) }
        return samples
    }

    private List<Variant> fetchVariantsByChromosomeAndStartPosition(String chromosome, BigInteger start) {
        def result = sql.rows("""SELECT variant.id as varid, variant.chr as varchr, variant.start as varstart, 
variant.end as varend, variant.ref as varref, variant.obs as varobs, variant.issomatic as varsomatic, variant.uuid as
 varuuid, consequence.*, gene.id as geneindex, gene.geneid as geneid FROM variant INNER JOIN variant_has_consequence 
 ON variant.id = variant_has_consequence.variant_id INNER JOIN consequence on variant_has_consequence.consequence_id 
 = consequence.id INNER JOIN consequence_has_gene on consequence_has_gene.consequence_id = consequence.id INNER JOIN 
 gene on gene.id=consequence_has_gene.gene_id WHERE variant.chr=$chromosome AND variant.start=$start;""")
        return parseVariantQueryResult(result)
    }

    private List<Variant> fetchVariantsByChromosome(String chromosome) {
        def result = sql.rows("""SELECT variant.id as varid, variant.chr as varchr, variant.start as varstart, 
variant.end as varend, variant.ref as varref, variant.obs as varobs, variant.issomatic as varsomatic, variant.uuid as
 varuuid, consequence.*, gene.id as geneIndex, gene.geneid as geneid FROM variant INNER JOIN variant_has_consequence 
 ON variant.id = variant_has_consequence.variant_id INNER JOIN consequence on variant_has_consequence.consequence_id 
 = consequence.id INNER JOIN consequence_has_gene on consequence_has_gene.consequence_id = consequence.id INNER JOIN 
 gene on gene.id=consequence_has_gene.gene_id WHERE variant.chr=$chromosome""")
        return parseVariantQueryResult(result)
    }

    private List<Variant> fetchVariantsByStartPosition(BigInteger start) {
        def result = sql.rows("""SELECT variant.id as varid, variant.chr as varchr, variant.start as varstart, 
variant.end as varend, variant.ref as varref, variant.obs as varobs, variant.issomatic as varsomatic, variant.uuid as
 varuuid, consequence.*, gene.id as geneIndex, gene.geneid as geneid FROM variant INNER JOIN variant_has_consequence 
 ON variant.id = variant_has_consequence.variant_id INNER JOIN consequence on variant_has_consequence.consequence_id 
 = consequence.id INNER JOIN consequence_has_gene on consequence_has_gene.consequence_id = consequence.id INNER JOIN 
 gene on gene.id=consequence_has_gene.gene_id WHERE variant.start=$start;""")
        return parseVariantQueryResult(result)
    }

    private List<Variant> fetchVariantsBySample(String sampleId) {
        def result = sql.rows("""SELECT variant.id as varid, variant.chr as varchr, variant.start as varstart, 
variant.end as varend, variant.ref as varref, variant.obs as varobs, variant.issomatic as varsomatic, variant.uuid as
 varuuid, consequence.*, gene.id as geneIndex, gene.geneid as geneid FROM variant INNER JOIN Sample_has_variant ON 
 variant.id = Sample_has_variant.variant_id INNER JOIN variant_has_consequence ON variant.id = 
 variant_has_consequence.variant_id INNER JOIN consequence on variant_has_consequence.consequence_id = consequence.id
  INNER JOIN consequence_has_gene on consequence_has_gene.consequence_id = consequence.id INNER JOIN gene on gene
  .id=consequence_has_gene.gene_id WHERE Sample_identifier=$sampleId;""")
        return parseVariantQueryResult(result)
    }

    private List<Variant> fetchVariantsBySampleAndGeneId(String sampleId, String geneId) {
        def result = sql.rows("""SELECT distinct variant.id as varid, variant.chr as varchr, variant.start as 
varstart, variant.end as varend, variant.ref as varref, variant.obs as varobs, variant.issomatic as varsomatic, 
variant.uuid as varuuid, consequence.*, gene.id as geneIndex, gene.geneid as geneid FROM variant INNER JOIN 
variant_has_consequence ON variant.id = variant_has_consequence.variant_id INNER JOIN consequence on 
variant_has_consequence.consequence_id = consequence.id INNER JOIN Sample_has_variant ON Sample_has_variant
.variant_id = variant_has_consequence.variant_id INNER JOIN consequence_has_gene on consequence_has_gene
.consequence_id = consequence.id INNER JOIN gene on gene.id=consequence_has_gene.gene_id where Sample_identifier = 
$sampleId AND geneid=$geneId;""")
        return parseVariantQueryResult(result)
    }

    private List<Variant> fetchVariantsByGeneId(String geneId) {
        def result = sql.rows("""SELECT variant.id as varid, variant.chr as varchr, variant.start as varstart, 
variant.end as varend, variant.ref as varref, variant.obs as varobs, variant.issomatic as varsomatic, variant.uuid as
 varuuid, consequence.*, gene.id as geneIndex, gene.geneid as geneid FROM variant INNER JOIN variant_has_consequence 
 ON variant.id = variant_has_consequence.variant_id INNER JOIN consequence on variant_has_consequence.consequence_id 
 = consequence.id INNER JOIN consequence_has_gene on consequence_has_gene.consequence_id = consequence.id INNER JOIN 
 gene on gene.id=consequence_has_gene.gene_id WHERE geneid=$geneId;""")
        return parseVariantQueryResult(result)
    }

    private List<Variant> fetchVariantsForBeaconResponse(String chromosome, BigInteger start,
                                                         String reference, String observed, String assemblyId) {
        def result = sql.rows("""SELECT variant.id as varid, variant.chr as varchr, variant.start as varstart, 
variant.end as varend, variant.ref as varref, variant.obs as varobs, variant.issomatic as varsomatic, variant.uuid as
 varuuid FROM variant INNER JOIN variant_has_referencegenome ON variant.id = variant_has_referencegenome.variant_id 
 INNER JOIN referencegenome on variant_has_referencegenome.referencegenome_id = referencegenome.id where 
 referencegenome.build=$assemblyId and variant.chr=$chromosome and variant.start=$start and variant.ref=$reference 
and variant.obs=$observed;""")
        return parseVariantQueryResult(result, false)
    }

    private List<Gene> fetchGenesBySample(String sampleId) {
        def result = sql.rows(""" SELECT gene.*, Sample_has_variant.* FROM gene INNER JOIN consequence_has_gene ON 
gene.id = consequence_has_gene.gene_id INNER JOIN consequence on consequence_has_gene.consequence_id = consequence.id
 INNER JOIN variant_has_consequence on variant_has_consequence.consequence_id = consequence.id INNER JOIN variant ON 
 variant_has_consequence.variant_id = variant.id INNER JOIN sample_has_variant ON sample_has_variant.variant_id = 
 variant.id WHERE sample_identifier=$sampleId;""")
        List<Gene> genes = result.collect { convertRowResultTogene(it) }
        return genes
    }

    void tryToStoreJunctionBatch(Object id, List ids, String insertStatement) {
        sql.withBatch(insertStatement) { ps ->
            ids.each { key2 ->
                if (id instanceof String) id = (String) id else id = (Integer) id

                if (key2 instanceof String) key2 = (String) key2 else key2 = (Integer) key2
                ps.addBatch([id, key2] as List<Object>)
            }
        }
        sql.commit()
    }

    void tryToStoreJunctionBatchFromMap(HashMap ids, HashMap connectorMap, String insertStatement) {
        sql.withBatch(insertStatement) { ps ->
            ids.each { entry ->
                entry.value.each { cons -> ps.addBatch([connectorMap.get(entry.key), cons] as List<Object>)
                }
            }
        }
        sql.commit()
    }

    void tryToStoreJunctionBatchForSamplesAndVariants(HashMap<Sample, Integer> samples,
                                                      HashMap<SimpleVariant, Integer> connectorMap,
                                                      HashMap<SimpleVariant, Integer> infoMap, HashMap<SimpleVariantContext, HashMap<String, Integer>> genotypeMap, String insertStatement) {
        sql.withBatch(insertStatement) { ps ->
            genotypeMap.each { entry ->
                samples.each { sampleEntry ->
                    // sample_id, variant_id, vcfinfo_id, genotype_id

                    ps.addBatch([sampleEntry.value, connectorMap[entry.key], infoMap[entry.key], genotypeMap[entry.key][sampleEntry.key.identifier]])
                }
            }
        }
        sql.commit()
    }

    private List tryToStoreVariantsBatch(List<SimpleVariantContext> variants) {
        def consequences = []

        sql.withBatch("INSERT INTO variant (uuid, chr, start, end, ref, obs, issomatic, databaseidentifier) values " + "(?,?,?,?,?,?,?,?) ON " + "DUPLICATE KEY UPDATE id=id") { ps ->
            variants.each { v ->
                ps.addBatch([UUID.randomUUID().toString(), v.getChromosome(), v.getStartPosition(), v.getEndPosition
                        (), v.getReferenceAllele(), v.getObservedAllele(), v.getIsSomatic(), v.getDatabaseId()])
                v.getConsequences().each { cons -> consequences.add(cons)
                }
            }
        }

        sql.commit()
        return consequences
    }

    private void tryToStoreVariantInfo(List<SimpleVariantContext> variants) {
        def props = VcfInfo.declaredFields.findAll { !it.synthetic }.collect { it }

        sql.withBatch("INSERT INTO vcfinfo (ancestralallele,allelecount,allelefreq,numberalleles,basequality,cigar,"
                + "dbsnp,hapmaptwo,hapmapthree,thousandgenomes,combineddepth,endpos,rms,mqzero,strandbias," + "numbersamples,somatic,validated) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE id=id") { ps ->
            variants.each { var ->
                def infoValues = []
                props.each { prop ->
                    if (prop.type == List) {
                        def value = var.vcfInfo.properties.get(prop.name)
                        def valDb = value ? infoValues.toString() : "[]"
                        infoValues.add(valDb)
                    } else {
                        infoValues.add(var.vcfInfo.properties.get(prop.name))
                    }
                }
                ps.addBatch(infoValues)
            }
        }
        sql.commit()
    }

    private void tryToStoreVariantGenotypes(List<SimpleVariantContext> variants) {
        def props = Genotype.declaredFields.findAll { !it.synthetic && it.name != "sampleName" }.collect { it }
        def genotypes = variants.collect { variant -> variant.genotypes }.flatten()

        if (genotypes.empty) return

        sql.withBatch(10000, "INSERT INTO genotype (genotype,readdepth,filter,likelihoods,genotypelikelihoods," + "genotypelikelihoodshet,posteriorprobs,genotypequality,haplotypequalities,phaseset,phasingquality,alternateallelecounts,mappingquality) values (?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE id=id") { ps ->
            genotypes.toSet().each { genotype ->
                def genotypeValues = []
                props.each { prop -> genotypeValues.add(genotype.properties.get(prop.name)) }
                ps.addBatch(genotypeValues)
            }
        }
        sql.commit()
    }

    private HashMap tryToStoreConsequencesBatch(List<Consequence> consequences) {
        def consGeneMap = [:]

        sql.withBatch("INSERT INTO consequence (allele , codingchange , transcriptid , transcriptversion , type , " +
                "biotype , canonical , aachange , cdnaposition , cdsposition , proteinposition , proteinlength , " + "cdnalength , cdslength , impact, exon, intron, strand, genesymbol , featuretype , distance , warnings) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE id=id") { ps ->
            consequences.each { cons ->
                ps.addBatch([cons.allele, cons.codingChange, cons.transcriptId, cons.transcriptVersion, cons.type,
                             cons.bioType, cons.canonical, cons.aaChange, cons.cdnaPosition, cons.cdsPosition, cons
                                     .proteinPosition, cons.proteinLength, cons.cdnaLength, cons.cdsLength, cons
                                     .impact, cons.exon, cons.intron, cons.strand, cons.geneSymbol, cons.featureType,
                             cons.distance, cons.warnings])
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
        def result = sql.executeInsert("""INSERT INTO entity (id) values \
        (${patient.identifier})
     ON DUPLICATE KEY UPDATE id=id;""")
        return patient.identifier
    }

    private void tryToStoreVariantCaller(VariantCaller variantCaller) {
        def result = sql.executeInsert("""INSERT INTO variantcaller (name, version, doi) values \
        (${variantCaller.name},
        ${variantCaller.version},
        ${variantCaller.doi})
     ON DUPLICATE KEY UPDATE id=LAST_INSERT_ID(id);""")
    }

    private String tryToStoreSample(Sample sample) {
        def result = sql.executeInsert("""INSERT INTO sample (identifier, cancerentity) values \
        (${sample.identifier},
        ${sample.cancerEntity})
     ON DUPLICATE KEY UPDATE identifier=identifier;""")
        return sample.identifier
    }

    private String tryToStoreSampleWithCase(Sample sample, String caseId) {
        def result = sql.executeInsert("""INSERT INTO sample (identifier, entity_id, cancerentity) values \
        (${sample.identifier},
        ${caseId},
        ${sample.cancerEntity})
     ON DUPLICATE KEY UPDATE identifier=identifier;""")
        return sample.identifier
    }

    private void tryToStoreSamplesWithCase(List<Sample> samples, String caseId) {

        sql.withBatch("INSERT INTO sample (identifier, entity_id, cancerentity) values (?,?,?) ON DUPLICATE KEY UPDATE identifier=identifier") {
            BatchingPreparedStatementWrapper ps ->
                samples.each { sample -> ps.addBatch([sample.identifier, caseId, sample.cancerEntity])
                }
        }

        sql.commit()
    }

    private void tryToStoreAnnotationSoftware(Annotation annotationSoftware) {
        def result = sql.executeInsert("""INSERT INTO annotationsoftware (name, version, doi) values \
        (${annotationSoftware.name},
        ${annotationSoftware.version},
        ${annotationSoftware.doi})
     ON DUPLICATE KEY UPDATE id=LAST_INSERT_ID(id);""")
    }

    private void tryToStoreReferenceGenome(ReferenceGenome referenceGenome) {
        def result = sql.executeInsert("""INSERT INTO referencegenome (source, build, version) values \
        (${referenceGenome.source},
        ${referenceGenome.build},
        ${referenceGenome.version})
     ON DUPLICATE KEY UPDATE id=id;""")
    }

    private Integer tryToFindReferenceGenome(ReferenceGenome referenceGenome) {
        def result =
                sql.firstRow("SELECT id FROM referencegenome WHERE referencegenome.source=? and referencegenome" + "" + ".build=? and referencegenome.version=?",
                        [referenceGenome.source, referenceGenome.build, referenceGenome.version])
        return result.id
    }

    private List<String> tryToStoreGenes(List<String> genes) {
        sql.withBatch("insert INTO gene (geneid) values (?) ON DUPLICATE KEY UPDATE id=id") {
            BatchingPreparedStatementWrapper ps ->
            genes.each { identifier -> ps.addBatch([identifier])
            }
        }

        sql.commit()
        return genes
    }

    private List<Gene> tryToStoreGeneObjects(List<Gene> genes) {
        sql.withBatch("insert INTO gene (symbol, name, biotype, chr, start, end, synonyms, geneid, description, " +
                "strand, version) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE id=id") {
            BatchingPreparedStatementWrapper ps ->
            genes.each { gene ->
                ps.addBatch([gene.symbol, gene.name, gene.bioType, gene.chromosome, gene.geneStart,
                             gene.geneEnd, gene.synonyms[0], gene.geneId, gene.description, gene
                                     .strand, gene.version])
            }
        }

        sql.commit()
        return genes
    }

    private void tryToStoreEnsemblDB(Integer version, String date, Integer referenceGenomeId) {
        def result = sql.executeInsert("""INSERT INTO ensembl (version, date, referencegenome_id) values \
        ($version,
        $date,
        $referenceGenomeId)
     ON DUPLICATE KEY UPDATE id=id;""")
    }

    private Integer tryToFindEnsemblDB(Integer version, String date, Integer rgId) {
        def result =
                sql.firstRow("SELECT id FROM ensembl WHERE ensembl.version=? and ensembl.date=? and ensembl" + "" + ".referencegenome_id=?",
                        [version, date, rgId])
        return result.id
    }

    private Integer tryToFindVariantCaller(VariantCaller variantCaller) {
        def result =
                sql.firstRow("SELECT id FROM variantcaller WHERE variantcaller.name=? and variantcaller.version=? " + "and" + " variantcaller.doi=?",
                        [variantCaller.name, variantCaller.version, variantCaller.doi])
        return result.id
    }

    private Integer tryToFindAnnotationSoftware(Annotation annotation) {
        def result =
                sql.firstRow("SELECT id FROM annotationsoftware WHERE annotationsoftware.name=? and " +
                        "annotationsoftware.version=? and annotationsoftware.doi=?",
                        [annotation.name, annotation.version, annotation.doi])
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
    private static Gene convertRowResultTogene(GroovyRowResult row) {
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

    private static Variant convertRowResultToVariant(GroovyRowResult row, Boolean withConsequence) {
        def variant = new Variant()
        variant.setIdentifier(row.get("varuuid") as String)
        variant.setChromosome(row.get("varchr") as String)
        variant.setStartPosition(row.get("varstart") as BigInteger)
        variant.setEndPosition(row.get("varend") as BigInteger)
        variant.setReferenceAllele(row.get("varref") as String)
        variant.setObservedAllele(row.get("varobs") as String)
        variant.setIsSomatic(row.get("varsomatic") as Boolean)
        variant.setDatabaseIdentifier(row.get("databaseidentifier") as String)
        if (withConsequence) {
            variant.setConsequences([convertRowResultToConsequence(row)])
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

    private static List<Variant> parseVariantQueryResult(List<GroovyRowResult> rows, Boolean withConsequence = true) {
        Map<String, List<Variant>> variantsIdMap = rows.collect { convertRowResultToVariant(it, withConsequence) }
                .groupBy { it.identifier }
        List<Variant> variants = []

        if (!withConsequence) {
            return variantsIdMap.values().toList() as List<Variant>
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
