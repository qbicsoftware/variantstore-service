/*
Manual conversion of MariaDB database scheme of Version 
-- Wed Nov  6 09:03:31 2019
-- Model: New Model    Version: 1.0
*/
--
/*
Creation of this model:
-- Thu Dec 11
-- Version: 0.1
*/

/*
The database should be created using:
WITH ENCODING 'UTF8'

we should not use 'name' as a field name -> https://stackoverflow.com/questions/8416017/is-name-a-special-keyword-in-postgresql
we should not use 'end' as a field name -> reserved keyword -> best to avoid them -> https://stackoverflow.com/questions/10891368/postgres-table-column-name-restrictions

There is no TINYTEXT in postgresql -> we replace it with VARCHAR(255)
There is no TINYINT (1 byte) in postgresql -> we place it with SMALLINT (2 bytes)
    It is however possible to use the extension pguint (https://github.com/petere/pguint), which provides
        int1 (signed 8-bit integer)
        uint1 (unsigned 8-bit integer)
*/


-- -----------------------------------------------------
-- Schema oncostore
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS oncostore;


-- -----------------------------------------------------
-- Schema oncostore
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS oncostore;
-- We likely don't need access to the public scheme anymore, so we drop it
SET search_path TO oncostore;

-- -----------------------------------------------------
-- Table `oncostore`.`Gene`
-- -----------------------------------------------------
DROP TABLE IF EXISTS oncostore.Gene;

CREATE TABLE IF NOT EXISTS oncostore.Gene (
    id SERIAL PRIMARY KEY,
    symbol VARCHAR(25),
    geneName VARCHAR(45),
    biotype VARCHAR(45),
    chr VARCHAR(15),
    geneStart BIGINT,
    geneEnd BIGINT,
    synonyms VARCHAR(45),
    geneId VARCHAR(45),
    description VARCHAR(255),
    strand VARCHAR(1),
    version INTEGER
);
CREATE UNIQUE INDEX gene_idx_UNIQUE ON oncostore.Gene (
    symbol ASC,
    geneName ASC,
    biotype ASC,  
    chr ASC,
    geneStart ASC,
    geneEnd ASC,
    synonyms ASC,
    geneId ASC,
    description ASC,
    strand ASC,
    version ASC
);


-- -----------------------------------------------------
-- Table `oncostore`.`Variant`
-- -----------------------------------------------------
DROP TABLE IF EXISTS oncostore.Variant;

CREATE TABLE IF NOT EXISTS oncostore.Variant (
    id SERIAL PRIMARY KEY,
    chr VARCHAR(15) NOT NULL,
    variantStart BIGINT NOT NULL,
    variantEnd BIGINT NOT NULL,
    ref VARCHAR(255) NOT NULL,
    obs VARCHAR(255) NOT NULL,
    isSomatic SMALLINT NOT NULL,
    uuid VARCHAR(36) NOT NULL
);
CREATE UNIQUE INDEX variant_idx_UNIQUE ON oncostore.Variant (
    chr ASC,
    variantStart ASC,
    variantEnd ASC,
    ref ASC,
    obs ASC,
    isSomatic ASC
);

-- -----------------------------------------------------
-- Table `oncostore`.`Consequence`
-- -----------------------------------------------------
DROP TABLE IF EXISTS oncostore.Consequence;

CREATE TABLE IF NOT EXISTS oncostore.Consequence (
    id SERIAL PRIMARY KEY,
    codingChange VARCHAR(128),
    aaChange VARCHAR(60),
    aaStart INTEGER,
    aaEND INTEGER,
    type VARCHAR(128) NOT NULL,
    impact VARCHAR(25) NOT NULL,
    strand SMALLINT,
    transcriptID VARCHAR(128) NOT NULL,
    transcriptVersion INTEGER,
    canonical SMALLINT,
    bioType VARCHAR(45) NOT NULL,
    refSeqID VARCHAR(45)
);
CREATE UNIQUE INDEX consequence_idx_UNIQUE ON oncostore.Consequence (
    codingChange ASC,
    aaChange ASC,
    aaStart ASC,
    aaEnd ASC,
    type ASC,
    impact ASC,
    strand ASC,
    transcriptID ASC,
    transcriptVersion ASC,
    canonical ASC,
    biotype ASC,
    refSeqID ASC
);

-- -----------------------------------------------------
-- Table `oncostore`.`ReferenceGenome`
-- -----------------------------------------------------
DROP TABLE IF EXISTS oncostore.ReferenceGenome;

CREATE TABLE IF NOT EXISTS oncostore.ReferenceGenome (
    id SERIAL PRIMARY KEY,
    source VARCHAR(45),
    build VARCHAR(45),
    version VARCHAR(45)
);
CREATE UNIQUE INDEX reference_genome_idx_UNIQUE ON oncostore.ReferenceGenome (
    source ASC,
    build ASC,
    version ASC
);

-- -----------------------------------------------------
-- Table `oncostore`.`AnnotationSoftware`
-- -----------------------------------------------------
DROP TABLE IF EXISTS oncostore.AnnotationSoftware;

CREATE TABLE IF NOT EXISTS oncostore.AnnotationSoftware (
    id SERIAL PRIMARY KEY,
    annotationSoftwareName VARCHAR(45) NOT NULL,
    version VARCHAR(15) NOT NULL,
    DOI VARCHAR(30) NOT NULL
);
CREATE UNIQUE INDEX annotation_software_idx_UNIQUE ON oncostore.AnnotationSoftware (
    annotationSoftwareName ASC,
    version ASC,
    DOI ASC
);

-- -----------------------------------------------------
-- Table `oncostore`.`Project`
-- -----------------------------------------------------
DROP TABLE IF EXISTS oncostore.Project;

CREATE TABLE IF NOT EXISTS oncostore.Project (
    id VARCHAR(15) NOT NULL PRIMARY KEY
);
CREATE UNIQUE INDEX project_id_UNIQUE ON oncostore.Project (
    id ASC
);

-- -----------------------------------------------------
-- Table `oncostore`.`Entity`
-- -----------------------------------------------------
DROP TABLE IF EXISTS oncostore.Entity;

CREATE TABLE IF NOT EXISTS oncostore.Entity (
    id VARCHAR(15) NOT NULL PRIMARY KEY,
    Project_id VARCHAR(15),
    CONSTRAINT fk_Entity_Project
        FOREIGN KEY (Project_id)
        REFERENCES oncostore.Project (id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
);
CREATE INDEX entity_project_id ON oncostore.Entity (
    Project_id ASC
);
CREATE UNIQUE INDEX entity_id_UNIQUE ON oncostore.Entity (
    id ASC
);

-- -----------------------------------------------------
-- Table `oncostore`.`VariantCaller`
-- -----------------------------------------------------
DROP TABLE IF EXISTS oncostore.VariantCaller;

CREATE TABLE IF NOT EXISTS oncostore.VariantCaller (
    id SERIAL PRIMARY KEY,
    variantCallerName VARCHAR(45) NOT NULL,
    version VARCHAR(15) NOT NULL,
    DOI VARCHAR(30) NOT NULL
);
CREATE UNIQUE INDEX variant_caller_idx_UNIQUE ON oncostore.VariantCaller (
    variantCallerName ASC,
    version ASC,
    DOI ASC
);

-- -----------------------------------------------------
-- Table `oncostore`.`Sample`
-- -----------------------------------------------------
DROP TABLE IF EXISTS oncostore.Sample;

CREATE TABLE IF NOT EXISTS oncostore.Sample (
    qbicID VARCHAR(15) NOT NULL PRIMARY KEY,
    Entity_id VARCHAR(15),
    cancerEntity VARCHAR(45),
    CONSTRAINT fk_Sample_Entity
        FOREIGN KEY (Entity_id)
        REFERENCES oncostore.Entity (id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
);
CREATE INDEX fk_Sample_Entity_idx ON oncostore.Sample (
    Entity_id ASC
);
CREATE UNIQUE INDEX fk_Sample_Entity_UNIQUE ON oncostore.Sample (
    qbicID ASC,
    Entity_id ASC,
    cancerEntity ASC
);
CREATE UNIQUE INDEX qbicID_UNIQUE ON oncostore.Sample (
    qbicID ASC
);

-- -----------------------------------------------------
-- Table `oncostore`.`AnnotationSoftware_has_Consequence`
-- -----------------------------------------------------
DROP TABLE IF EXISTS oncostore.AnnotationSoftware_has_Consequence;

CREATE TABLE IF NOT EXISTS oncostore.AnnotationSoftware_has_Consequence (
    AnnotationSoftware_id INTEGER NOT NULL,
    Consequence_id INTEGER NOT NULL,
    PRIMARY KEY (AnnotationSoftware_id, Consequence_id),
    CONSTRAINT fk_AnnotationSoftware_has_Consequence_AnnotationSoftware
        FOREIGN KEY (AnnotationSoftware_id)
        REFERENCES oncostore.AnnotationSoftware (id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION,
    CONSTRAINT fk_AnnotationSoftware_has_Consequence_Consequence
        FOREIGN KEY (Consequence_id)
        REFERENCES oncostore.Consequence (id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
);
CREATE INDEX fk_AnnotationSoftware_has_Consequence_Consequence_idx ON oncostore.AnnotationSoftware_has_Consequence (
    Consequence_id ASC
);
CREATE INDEX fk_AnnotationSoftware_has_Consequence_AnnotationSoftware_idx ON oncostore.AnnotationSoftware_has_Consequence (
    AnnotationSoftware_id ASC
);
CREATE UNIQUE INDEX fk_AnnotationSoftware_has_Consequence_Consequence_UNIQUE ON oncostore.AnnotationSoftware_has_Consequence (
    AnnotationSoftware_id ASC,
    Consequence_id ASC
);

-- -----------------------------------------------------
-- Table `oncostore`.`Variant_has_ReferenceGenome`
-- -----------------------------------------------------
DROP TABLE IF EXISTS oncostore.Variant_has_ReferenceGenome;

CREATE TABLE IF NOT EXISTS oncostore.Variant_has_ReferenceGenome (
    Variant_id INTEGER NOT NULL,
    ReferenceGenome_id INTEGER NOT NULL,
    PRIMARY KEY (Variant_id, ReferenceGenome_id),
    CONSTRAINT fk_Variant_has_ReferenceGenome_Variant
        FOREIGN KEY (Variant_id)
        REFERENCES oncostore.Variant(id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION,
    CONSTRAINT fk_Variant_has_ReferenceGenome_ReferenceGenome
        FOREIGN KEY (ReferenceGenome_id)
        REFERENCES oncostore.ReferenceGenome (id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
);
CREATE INDEX fk_Variant_has_ReferenceGenome_ReferenceGenome_idx ON oncostore.Variant_has_ReferenceGenome (
    ReferenceGenome_id ASC
);
CREATE INDEX fk_Variant_has_ReferenceGenome_Variant_idx ON oncostore.Variant_has_ReferenceGenome (
    Variant_id ASC
);
CREATE UNIQUE INDEX fk_Variant_has_ReferenceGenome_ReferenceGenome_Variant_idx_UNIQUE ON oncostore.Variant_has_ReferenceGenome (
    Variant_id ASC,
    ReferenceGenome_id ASC
);

-- -----------------------------------------------------
-- Table `oncostore`.`Variant_has_Consequence`
-- -----------------------------------------------------
DROP TABLE IF EXISTS oncostore.Variant_has_Consequence;

CREATE TABLE IF NOT EXISTS oncostore.Variant_has_Consequence (
    Variant_id INTEGER NOT NULL,
    Consequence_id INTEGER NOT NULL,
    PRIMARY KEY (Variant_id, Consequence_id),
    CONSTRAINT fk_Variant_has_Consequence_Variant
        FOREIGN KEY (Variant_id)
        REFERENCES oncostore.Variant (id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION,
    CONSTRAINT fk_Variant_has_Consequence_Consequence 
        FOREIGN KEY (Consequence_id)
        REFERENCES oncostore.Consequence (id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
);
CREATE INDEX fk_Variant_has_Consequence_Consequence_idx ON oncostore.Variant_has_Consequence (
    Consequence_id ASC
);
CREATE INDEX fk_Variant_has_Consequence_Variant ON oncostore.Variant_has_Consequence (
    Variant_id ASC
);
CREATE UNIQUE INDEX fk_Variant_has_Consequence_Consequence_Variant_Consequence_idx_UNIQUE ON oncostore.Variant_has_Consequence (
    Variant_id ASC,
    Consequence_id ASC
);

-- -----------------------------------------------------
-- Table `oncostore`.`Variant_has_VariantCaller`
-- -----------------------------------------------------
DROP TABLE IF EXISTS oncostore.Variant_has_VariantCaller;

CREATE TABLE IF NOT EXISTS oncostore.Variant_has_VariantCaller (
    Variant_id INTEGER NOT NULL,
    VariantCaller_id INTEGER NOT NULL,
    PRIMARY KEY (Variant_id, VariantCaller_id),
    CONSTRAINT fk_Variant_has_VariantCaller_Variant
        FOREIGN KEY (Variant_id)
        REFERENCES oncostore.Variant (id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION,
    CONSTRAINT fk_Variant_has_VariantCaller_VariantCaller
        FOREIGN KEY (VariantCaller_id)
        REFERENCES oncostore.VariantCaller (id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
);
CREATE INDEX fk_Variant_has_VariantCaller_VariantCaller_idx ON oncostore.Variant_has_VariantCaller (
    VariantCaller_id ASC
);
CREATE INDEX fk_Variant_has_VariantCaller_Variant_idx ON oncostore.Variant_has_VariantCaller (
    Variant_id ASC
);
CREATE UNIQUE INDEX fk_Variant_has_VariantCaller_Variant_VariantCaller_idx_UNIQUE ON oncostore.Variant_has_VariantCaller (
    Variant_id ASC,
    VariantCaller_id ASC
);

-- -----------------------------------------------------
-- Table `oncostore`.`Sample_has_Variant`
-- -----------------------------------------------------
DROP TABLE IF EXISTS oncostore.Sample_has_Variant;

CREATE TABLE IF NOT EXISTS oncostore.Sample_has_Variant (
    Sample_qbicID VARCHAR(15) NOT NULL,
    Variant_id INTEGER NOT NULL,
    PRIMARY KEY (Sample_qbicID, Variant_id),
    CONSTRAINT fk_Sample_has_Variant_Sample
        FOREIGN KEY (Sample_qbicID)
        REFERENCES oncostore.Sample (qbicID)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION,
    CONSTRAINT fk_Sample_has_Variant_Variant
        FOREIGN KEY (Variant_id)
        REFERENCES oncostore.Variant (id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
);
CREATE INDEX fk_Sample_has_Variant_Variant_idx ON oncostore.Sample_has_Variant(
    Variant_id ASC
);
CREATE INDEX fk_Sample_has_Variant_Sample_idx ON oncostore.Sample_has_Variant (
    Sample_qbicID ASC
);
CREATE UNIQUE INDEX fk_Sample_has_Variant_Sample_Variant ON oncostore.Sample_has_Variant (
    Sample_qbicID ASC,
    Variant_id ASC
);

-- -----------------------------------------------------
-- Table `oncostore`.`Ensembl`
-- -----------------------------------------------------
DROP TABLE IF EXISTS oncostore.Ensembl;

CREATE TABLE IF NOT EXISTS oncostore.Ensembl (
    id SERIAL PRIMARY KEY,
    version INTEGER NOT NULL,
    date VARCHAR(45),
    ReferenceGenome_id INTEGER NOT NULL,
    CONSTRAINT fk_Ensembl_ReferenceGenome 
        FOREIGN KEY (ReferenceGenome_id)
        REFERENCES oncostore.ReferenceGenome (id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
);
CREATE INDEX fk_Ensembl_ReferenceGenome_idx ON oncostore.Ensembl (
    ReferenceGenome_id ASC
);
CREATE UNIQUE INDEX fk_Ensembl_ReferenceGenome_idx_UNIQUE ON oncostore.Ensembl (
    version ASC,
    date ASC
);

-- -----------------------------------------------------
-- Table `oncostore`.`Consequence_has_Gene`
-- -----------------------------------------------------
DROP TABLE IF EXISTS oncostore.Consequence_has_Gene;

CREATE TABLE IF NOT EXISTS oncostore.Consequence_has_Gene (
    Consequence_id INTEGER NOT NULL,
    Gene_id INTEGER NOT NULL,
    PRIMARY KEY (Consequence_id, Gene_id),
    CONSTRAINT fk_Consequence_has_Gene_Consequence 
        FOREIGN KEY (Consequence_id)
        REFERENCES oncostore.Consequence (id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION,
    CONSTRAINT fk_Consequence_has_Gene_Gene 
        FOREIGN KEY (Gene_id)
        REFERENCES oncostore.Gene (id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
);
CREATE INDEX fk_Consequence_has_Gene_Gene ON oncostore.Consequence_has_Gene (
    Gene_id ASC
);
CREATE INDEX fk_Consequence_has_Gene_Consequence ON oncostore.Consequence_has_Gene (
    Consequence_id ASC
);

-- -----------------------------------------------------
-- Table `oncostore`.`Ensembl_has_Gene`
-- -----------------------------------------------------
DROP TABLE IF EXISTS oncostore.Ensembl_has_Gene;

CREATE TABLE IF NOT EXISTS oncostore.Ensembl_has_Gene (
    Ensembl_id INTEGER NOT NULL,
    Gene_id INTEGER NOT NULL,
    PRIMARY KEY (Ensembl_id, Gene_id),
    CONSTRAINT fk_Ensembl_has_Gene_Ensembl 
        FOREIGN KEY (Ensembl_id)
        REFERENCES oncostore.Ensembl (id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION,
    CONSTRAINT fk_Ensembl_has_Gene_Gene 
        FOREIGN KEY (Gene_id)
        REFERENCES oncostore.Gene (id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
);
CREATE INDEX fk_Ensembl_has_Gene_Gene_idx ON oncostore.Ensembl_has_Gene (
    Gene_id ASC
);
CREATE INDEX fk_Ensembl_has_Gene_Ensembl_idx ON oncostore.Ensembl_has_Gene (
    Ensembl_id ASC
);
