package life.qbic.oncostore.oncoloader

import htsjdk.samtools.util.CloseableIterator
import life.qbic.oncostore.DataBase
import life.qbic.oncostore.model.*

import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

/**
 * A database loader object that expects a list
 * of variants and metadata and writes them into
 * a configured target data base.
 * 
 * @authors Christopher Mohr, Sven Fillinger
 */

class MysqlLoader implements Loader{

    final DataBase db
    final CloseableIterator variants
    final MetadataContext metadataContext

    /* We might have to use this for batch inserts in case of many variants */
    final int batchSize = 50000

    /* Predefined queries for existence checks of db entries */
    String searchGene = "SELECT * FROM Gene where id = ?"
    String searchAnnotationSoftware = "SELECT * from AnnotationSoftware where name=? and version=?"
    String searchVariantCaller = "SELECT * from VariantCaller where name=? and version=?"
    String searchReferenceGenome = "SELECT * from ReferenceGenome where source=? and build=? and version=?"
    String searchConsequence = "SELECT * from Consequence where transcriptID=? and codingChange=? and aaChange=? and type=?"
    String searchVariant = "SELECT * from Variant where start=? and end=? and ref=? and obs=?"
    String searchSample = "SELECT * from Sample where qbicID=?"

    /* Predefined queries for existence checks of db entries in junction tables */
    String searchGeneReferenceGenome = "SELECT * from Gene_has_ReferenceGenome where ReferenceGenome_id=? and Gene_id=?"
    String searchAnnotationSoftwareConsequence = "SELECT * from AnnotationSoftware_has_Consequence where AnnotationSoftware_id=? and Consequence_id=?"
    String searchVariantVariantCaller = "SELECT * from Variant_has_VariantCaller where VariantCaller_id=? and Variant_id=?"
    String searchVariantConsequence = "SELECT * from Variant_has_Consequence where Variant_id=? and Consequence_id=?"
    String searchReferenceGenomeVariant = "SELECT * from Variant_has_ReferenceGenome where ReferenceGenome_id=? and Variant_id=?"
    String searchSampleVariant = "SELECT * from Sample_has_Variant where Sample_qbicID=? and Variant_id=?"

    /* Predefined queries for inserting db entries */
    String insertGene = "INSERT INTO Gene (id, symbol, name, bioType, chr, start, end, synonyms) VALUES(?, ?, ?, ?, ?, ?, ?, ?)"
    String insertConsequence = "INSERT INTO Consequence (codingChange, aaChange, aaStart, aaEnd, type, impact, strand, transcriptID, transcriptVersion, canonical, bioType, refSeqID, Gene_id) VALUE(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
    String insertVariantCaller = "INSERT INTO VariantCaller (name, version, doi) VALUE(?, ?, ?)"
    String insertVariantAnnotation = "INSERT INTO AnnotationSoftware (name, version, doi) VALUE(?, ?, ?)"
    String insertReferenceGenome = "INSERT INTO ReferenceGenome (source, build, version) VALUE(?, ?, ?)"
    String insertVariant = "INSERT INTO Variant (id, chr, start, end, ref, obs, isSomatic) VALUE(?, ?, ?, ?, ?, ?, ?)"
    String insertSample = "INSERT INTO Sample (qbicID) VALUE(?)"

    /* Predefined queries for inserting db entries in junction tables */
    String insertVariantConsequenceJunction = "INSERT INTO Variant_has_Consequence (Variant_id, Consequence_id) VALUES(?, ?)"
    String insertVariantVariantCallerJunction = "INSERT INTO Variant_has_VariantCaller (VariantCaller_id, Variant_id) VALUES(?, ?)"
    String insertGeneReferenceGenomeJunction = "INSERT INTO Gene_has_ReferenceGenome (ReferenceGenome_id, Gene_id) VALUES(?, ?)"
    String insertAnnotationSoftwareConsequenceJunction = "INSERT INTO AnnotationSoftware_has_Consequence (AnnotationSoftware_id, Consequence_id) VALUE(?, ?)"
    String insertReferenceGenomeVariantJunction = "INSERT INTO Variant_has_ReferenceGenome (ReferenceGenome_id, Variant_id) VALUE(?,?)"
    String insertSampleVariantJunction = "INSERT INTO Sample_has_Variant (Sample_qbicID, Variant_id) VALUE(?,?)"

    MysqlLoader(CloseableIterator variants, MetadataContext metadataContext, DataBase db) {
        this.variants = variants
        this.metadataContext = metadataContext
        this.db = db
    }

    @Override
    public void load() {
        def variantCallerID = insertSoftware(this.metadataContext.getVariantCalling(), this.db)
        def variantAnnotationID = insertSoftware(this.metadataContext.getVariantAnnotation(), this.db)
        def referenceGenomeID = insertReferenceGenome(this.metadataContext.getReferenceGenome(), this.db)

        /* Store gene DB identifiers for association with reference genome in junction table */
        def geneIDs = []

        /* Store variant DB identifiers for association with variant caller in junction table */
        def variantIDs = []

        variants.each {variant ->
            /* UUID */
            def variantID = UUID.randomUUID().toString()

            variant = life.qbic.oncostore.util.AnnotationHandler.addAnnotationsToVariant(variant, metadataContext.getVariantAnnotation())

            /* Store consequence DB identifiers for association with variant in junction table */
            def consequenceDBids = []
            variant.getConsequences().each {consequence ->
                geneIDs.add(insertGene(consequence.getGene(), this.db))
                consequenceDBids.add(insertConsequence(consequence, this.db))
            }
            /* INSERT variant */
            def variantdbId = insertVariant(variant, variantID, this.metadataContext.getIsSomatic(), this.db)

            /* INSERT consequences and annotation software junction */
            insertJunction(variantAnnotationID, consequenceDBids.unique(), searchAnnotationSoftwareConsequence, insertAnnotationSoftwareConsequenceJunction, this.db)

            /* INSERT consequences and variant junction */
            insertJunction(variantdbId, consequenceDBids.unique(), searchVariantConsequence, insertVariantConsequenceJunction, this.db)

            /* Store variant IDs for later inserts */
            variantIDs.add(variantdbId)
        }

        /* INSERT sample */
        insertSample(this.metadataContext.getSampleID(), this.db)

        /* INSERT sample and variants junction */
        insertJunction(this.metadataContext.getSampleID(), variantIDs, searchSampleVariant, insertSampleVariantJunction, this.db)

        /* INSERT reference genome and genes junction */
        insertJunction(referenceGenomeID, geneIDs, searchGeneReferenceGenome, insertGeneReferenceGenomeJunction, this.db)

        /* INSERT variants and variant caller in junction table */
        insertJunction(variantCallerID, variantIDs, searchVariantVariantCaller, insertVariantVariantCallerJunction, this.db)

        /* INSERT variants and reference genome in junction table */
        insertJunction(referenceGenomeID, variantIDs, searchReferenceGenomeVariant, insertReferenceGenomeVariantJunction, this.db)
    }

    @Override
    List<Integer> insertJunction(Object id, List ids, String searchStatement, String insertStatement, DataBase db) throws SQLException {
        def conn = db.getConnection()
        def insert = conn.prepareStatement(insertStatement, Statement.RETURN_GENERATED_KEYS)
        def indices = []

        // Check if entry exists in database
        ids.each {identifier->
            def exists = conn.prepareStatement(searchStatement)
            (id.getClass() == String) ? exists.setString(1, (String) id): exists.setInt(1, (Integer) id)
            (identifier.getClass() == String) ? exists.setString(2, (String) identifier): exists.setInt(2, (Integer) identifier)
            ResultSet re = exists.executeQuery()

            if (!re.isBeforeFirst()) {
                (id.getClass() == String) ? insert.setString(1, (String) id): insert.setInt(1, (Integer) id)
                (identifier.getClass() == String) ? insert.setString(2, (String) identifier): insert.setInt(2, (Integer) identifier)
                insert.addBatch()
            }
        }

        insert.executeBatch()
        insert.close()

        while (insert.generatedKeys.next()) {
            indices.add(insert.getGeneratedKeys().getInt(1))
        }

        return indices
    }

    @Override
    String insertVariant(SimpleVariantContext variant, String id, Boolean isSomatic, DataBase db) {
        def conn = db.getConnection()
        def newVariant = false

        // Check if software entry exists in database
        def exists = conn.prepareStatement(searchVariant)

        exists.setLong(1, Long.parseLong(String.valueOf(variant.getStartPosition())))
        exists.setLong(2, Long.parseLong(String.valueOf(variant.getEndPosition())))
        exists.setString(3, variant.getReferenceAllele())
        exists.setString(4, variant.getObservedAllele())
        ResultSet re = exists.executeQuery()

        // if not, insert in database
        if (!re.isBeforeFirst()) {
            def insert = conn.prepareStatement(insertVariant)
            insert.setString(1, id)
            insert.setString(2, variant.getChromosome())
            insert.setLong(3, Long.parseLong(String.valueOf(variant.getStartPosition())))
            insert.setLong(4, Long.parseLong(String.valueOf(variant.getEndPosition())))
            /* contains a * in variantContext */
            insert.setString(5, variant.getReferenceAllele().toString().replace("*", ""))
            insert.setString(6, variant.getObservedAllele())
            insert.setBoolean(7, isSomatic)

            newVariant = (insert.executeUpdate() > 0)
        }

        if (!newVariant) {
            re.next()
            id = re.getString(1)
        }

        // return existing or newly generated index
        return id
    }

    @Override
    String insertGene(Gene gene, DataBase db) {
        def conn = db.getConnection()

        // Check if software entry exists in database
        def exists = conn.prepareStatement(searchGene)
        exists.setString(1, gene.getGeneID())
        ResultSet re = exists.executeQuery()

        // if not, insert in database
        if (!re.isBeforeFirst()) {
            def insert = conn.prepareStatement(insertGene)
            insert.setString(1, gene.getGeneID())
            insert.setString(2, "")
            insert.setString(3, "")
            insert.setString(4, "")
            insert.setInt(5, 0)
            insert.setInt(6, 0)
            insert.setInt(7, 0)
            insert.setString(8, "")
            insert.execute()
        }
        // return existing or newly generated index
        return gene.geneID
    }

    @Override
    Integer insertConsequence(Consequence cons, DataBase db) {
        def conn = db.getConnection()

        // Check if software entry exists in database
        def exists = conn.prepareStatement(searchConsequence)
        exists.setString(1, cons.getTranscriptID())
        exists.setString(2, cons.getCodingChange())
        exists.setString(3, cons.getAaChange())
        exists.setString(4, cons.getConsequenceType())
        ResultSet re = exists.executeQuery()

        // if not, insert in database
        if (!re.isBeforeFirst()) {
            def insert = conn.prepareStatement(insertConsequence, Statement.RETURN_GENERATED_KEYS)
            insert.setString(1, cons.getCodingChange())
            insert.setString(2, cons.getAaChange())
            insert.setInt(3, cons.getAaStart() ?: -1)
            insert.setInt(4, cons.getAaEnd() ?: -1)
            insert.setString(5, cons.getConsequenceType())
            insert.setString(6, cons.getImpact())
            insert.setInt(7, cons.getStrand() ?: -1)
            insert.setString(8, cons.getTranscriptID())
            insert.setInt(9, cons.getTranscriptVersion())
            insert.setBoolean(10, cons.getCanonical().asBoolean())
            insert.setString(11, cons.getBioType())
            insert.setString(12, cons.getRefSeqID())
            insert.setString(13, cons.getGene().getGeneID())
            insert.execute()
            re = insert.getGeneratedKeys()
        }

        re.next()
        // return existing or newly generated index
        return re.getInt(1)
    }


    /* Batch INSERT for consequences for efficiency */
    List<Integer> insertConsequencesBatch(List<Consequence> cons, DataBase db) {
        def conn = db.getConnection()
        def insert = conn.prepareStatement(insertConsequence, Statement.RETURN_GENERATED_KEYS)
        def indices = []

        cons.each { annotation ->
            /* Check if software entry exists in database */
            def exists = conn.prepareStatement(searchConsequence)
            exists.setString(1, annotation.getTranscriptID())
            exists.setString(2, annotation.getAaChange())
            exists.execute()
            ResultSet re = exists.getGeneratedKeys()

            /* If not, add batch for execution */
            if (!re.next()) {
                insert.setString(1, cons.getTranscriptID())
                insert.setString(2, cons.getTranscriptVersion())
                insert.setString(3, cons.getRefSeqID())
                insert.setString(4, cons.getConsequenceType())
                insert.setString(5, cons.getBioType())
                insert.setBoolean(6, cons.getCanonical())
                insert.setString(7, cons.getAaChange())
                insert.setInt(8, cons.getAaStart())
                insert.setInt(9, cons.getAaEnd())
                insert.setString(10, cons.getImpact())
                insert.addBatch()
            }
            else {
                indices.add(re.getInt(1))
            }
        }
        insert.executeBatch()
        insert.close()

        while (insert.getGeneratedKeys().next()) {
            indices.add(insert.getGeneratedKeys().getInt(1))
        }

        // return list of existing and/or newly generated indices
        return indices
    }

    @Override
    String insertSample(String id, DataBase db) {
        def conn = db.getConnection()
        def newSample = false

        // Check if software entry exists in database
        def exists = conn.prepareStatement(searchSample)
        exists.setString(1, id)
        ResultSet re = exists.executeQuery()

        // if not, insert in database
        if (!re.isBeforeFirst()) {
            def insert = conn.prepareStatement(insertSample)
            insert.setString(1, id)
            newSample = (insert.executeUpdate() > 0)
        }

        if (!newSample) {
            re.next()
            id = re.getString(1)
        }

        // return existing or newly generated index
        return id
    }

    @Override
    Integer insertSoftware(Object software, DataBase db) {
        def conn = db.getConnection()

        // Check if software entry exists in database
        def exists = (software.getClass() == Annotation) ? conn.prepareStatement(searchAnnotationSoftware): conn.prepareStatement(searchVariantCaller)
        exists.setString(1, software.getName())
        exists.setString(2, software.getVersion())
        ResultSet re = exists.executeQuery()

        // if not, insert in database
        if (!re.isBeforeFirst()) {
            def insert = (software.getClass() == Annotation) ? conn.prepareStatement(insertVariantAnnotation, Statement.RETURN_GENERATED_KEYS): conn.prepareStatement(insertVariantCaller, Statement.RETURN_GENERATED_KEYS)
            insert.setString(1, software.getName())
            insert.setString(2, software.getVersion())
            insert.setString(3, software.getDoi())
            insert.execute()
            re = insert.getGeneratedKeys()
        }
        re.next()
        // return existing or newly generated index
        return re.getInt(1)
    }

    @Override
    Integer insertReferenceGenome(ReferenceGenome genome, DataBase db) {
        def conn = db.getConnection()

        // Check if software entry exists in database
        def exists = conn.prepareStatement(searchReferenceGenome)
        exists.setString(1, genome.getSource())
        exists.setString(2, genome.getBuild())
        exists.setString(3, genome.getVersion())
        ResultSet re = exists.executeQuery()

        // if not, insert in database
        if (!re.isBeforeFirst()) {
            def insert = conn.prepareStatement(insertReferenceGenome, Statement.RETURN_GENERATED_KEYS)
            insert.setString(1, genome.getSource())
            insert.setString(2, genome.getBuild())
            insert.setString(3, genome.getVersion())
            insert.execute()
            re = insert.getGeneratedKeys()
        }
        re.next()
        // return existing or newly generated index
        return re.getInt(1)
    }

}