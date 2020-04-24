CREATE TABLE IF NOT EXISTS "gene" (
  "id" int(11) NOT NULL,
  "symbol" varchar(25) DEFAULT NULL,
  "name" varchar(45) DEFAULT NULL,
  "biotype" varchar(45) DEFAULT NULL,
  "chr" varchar(15) DEFAULT NULL,
  "start" bigint(20) DEFAULT NULL,
  "end" bigint(20) DEFAULT NULL,
  "synonyms" varchar(45) DEFAULT NULL,
  "geneid" varchar(45) DEFAULT NULL,
  "description" varchar(255) DEFAULT NULL,
  "strand" varchar(1) DEFAULT NULL,
  "version" int(11) DEFAULT NULL,
  PRIMARY KEY ("id"),
  UNIQUE KEY "gene_idx" ("symbol","name","biotype","chr","start","end","synonyms","geneid","description","strand","version")
);


CREATE TABLE IF NOT EXISTS "variant" (
  "id" int(11) NOT NULL,
  "chr" varchar(15) NOT NULL,
  "start" bigint(20) NOT NULL,
  "end" bigint(20) NOT NULL,
  "ref" tinytext NOT NULL,
  "obs" tinytext NOT NULL,
  "issomatic" tinyint(4) NOT NULL,
  "uuid" varchar(36) NOT NULL,
  PRIMARY KEY ("id"),

);


CREATE TABLE IF NOT EXISTS "consequence" (
  "id" int(11) NOT NULL,
  "allele" varchar(45) DEFAULT NULL,
  "codingchange" varchar(128) DEFAULT NULL,
  "transcriptid" varchar(128) NOT NULL,
  "transcriptversion" int(11) DEFAULT NULL,
  "type" varchar(128) NOT NULL,
  "biotype" varchar(45) NOT NULL,
  "canonical" tinyint(4) DEFAULT NULL,
  "aachange" varchar(60) DEFAULT NULL,
  "cdnaposition" varchar(45) DEFAULT NULL,
  "cdsposition" varchar(45) DEFAULT NULL,
  "proteinposition" varchar(45) DEFAULT NULL,
  "proteinlength" int(11) DEFAULT NULL,
  "cdnalength" int(11) DEFAULT NULL,
  "cdslength" int(11) DEFAULT NULL,
  "impact" varchar(25) NOT NULL,
  "exon" varchar(45) DEFAULT NULL,
  "intron" varchar(45) DEFAULT NULL,
  "strand" int(11) DEFAULT NULL,
  "genesymbol" varchar(45) DEFAULT NULL,
  "featuretype" varchar(128) DEFAULT NULL,
  "distance" int(11) DEFAULT NULL,
  "warnings" varchar(255) DEFAULT NULL,
  PRIMARY KEY ("id"),
  UNIQUE KEY "fk_idx1" ("codingchange","aachange","proteinposition","proteinlength","type","impact","strand","transcriptid","transcriptversion","canonical","biotype","cdnaposition","cdsposition","cdnalength","cdslength","genesymbol","featuretype","distance","allele","exon","intron")
);


CREATE TABLE IF NOT EXISTS "referencegenome" (
  "id" int(11) NOT NULL,
  "source" varchar(45) DEFAULT NULL,
  "build" varchar(45) DEFAULT NULL,
  "version" varchar(45) DEFAULT NULL,
  PRIMARY KEY ("id"),
  UNIQUE KEY "rf_idx" ("source","build","version")
);


CREATE TABLE IF NOT EXISTS "annotationsoftware" (
  "id" int(11) NOT NULL,
  "name" varchar(45) NOT NULL,
  "version" varchar(15) NOT NULL,
  "doi" varchar(30) NOT NULL,
  PRIMARY KEY ("id"),
  UNIQUE KEY "fk_idx" ("name","version","doi")
);


CREATE TABLE IF NOT EXISTS "project" (
  "id" varchar(15) NOT NULL,
  PRIMARY KEY ("id"),
  UNIQUE KEY "project_index" ("id")
);


CREATE TABLE IF NOT EXISTS "entity" (
  "id" varchar(15) NOT NULL,
  "project_id" varchar(15) DEFAULT NULL,
  PRIMARY KEY ("id"),
  UNIQUE KEY "entity_index" ("id"),
  KEY "fk_vase_project_idx" ("project_id"),
  CONSTRAINT "fk_Case_Project1" FOREIGN KEY ("project_id") REFERENCES "project" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
);


CREATE TABLE IF NOT EXISTS "variantcaller" (
  "id" int(11) NOT NULL,
  "name" varchar(45) NOT NULL,
  "version" varchar(15) NOT NULL,
  "doi" varchar(30) NOT NULL,
  PRIMARY KEY ("id"),
  UNIQUE KEY "fk_idx6" ("name","version","doi")
);


CREATE TABLE IF NOT EXISTS "sample" (
  "identifier" varchar(15) NOT NULL,
  "entity_id" varchar(15) DEFAULT NULL,
  "cancerentity" varchar(45) DEFAULT NULL,
  PRIMARY KEY ("identifier"),
  UNIQUE KEY "sample_index" ("identifier"),
  UNIQUE KEY "fk_idx2" ("identifier","entity_id","cancerentity"),
  KEY "fk_sample_case_idx" ("entity_id"),
  CONSTRAINT "fk_Sample_Case1" FOREIGN KEY ("entity_id") REFERENCES "entity" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
);


CREATE TABLE IF NOT EXISTS "ensembl" (
  "id" int(11) NOT NULL,
  "version" int(11) DEFAULT NULL,
  "date" varchar(45) DEFAULT NULL,
  "referencegenome_id" int(11) NOT NULL,
  PRIMARY KEY ("id"),
  UNIQUE KEY "ensembl_index" ("version","date"),
  KEY "fk_ensembl_referencegenome_idx" ("referencegenome_id"),
  CONSTRAINT "fk_Ensembl_ReferenceGenome1" FOREIGN KEY ("referencegenome_id") REFERENCES "referencegenome" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
);


CREATE TABLE IF NOT EXISTS "annotationsoftware_has_consequence" (
  "annotationsoftware_id" int(11) NOT NULL,
  "consequence_id" int(11) NOT NULL,
  PRIMARY KEY ("annotationsoftware_id","consequence_id"),
  UNIQUE KEY "fk_idx0" ("annotationsoftware_id","consequence_id"),
  KEY "fk_annotationsoftware_has_consequence_consequence_idx" ("consequence_id"),
  KEY "fk_annotationsoftware_has_consequence_annotationsoftware_idx" ("annotationsoftware_id"),
  CONSTRAINT "fk_AnnotationSoftware_has_Consequence_AnnotationSoftware1" FOREIGN KEY ("annotationsoftware_id") REFERENCES "annotationsoftware" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT "fk_AnnotationSoftware_has_Consequence_Consequence1" FOREIGN KEY ("consequence_id") REFERENCES "consequence" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
);


CREATE TABLE IF NOT EXISTS "variant_has_referencegenome" (
  "variant_id" int(11) NOT NULL,
  "referencegenome_id" int(11) NOT NULL,
  PRIMARY KEY ("variant_id","referencegenome_id"),
  UNIQUE KEY "fk_idx4" ("variant_id","referencegenome_id"),
  KEY "fk_Variant_has_ReferenceGenome_ReferenceGenome1_idx" ("referencegenome_id"),
  KEY "fk_Variant_has_ReferenceGenome_Variant1_idx" ("variant_id"),
  CONSTRAINT "fk_Variant_has_ReferenceGenome_ReferenceGenome1" FOREIGN KEY ("referencegenome_id") REFERENCES "referencegenome" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT "fk_Variant_has_ReferenceGenome_Variant1" FOREIGN KEY ("variant_id") REFERENCES "variant" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
);


CREATE TABLE IF NOT EXISTS "variant_has_consequence" (
  "variant_id" int(11) NOT NULL,
  "consequence_id" int(11) NOT NULL,
  PRIMARY KEY ("variant_id","consequence_id"),
  UNIQUE KEY "fk_idx3" ("variant_id","consequence_id"),
  KEY "fk_variant_has_consequence_consequence_idx" ("consequence_id"),
  KEY "fk_variant_has_consequence_variant_idx" ("variant_id"),
  CONSTRAINT "fk_Variant_has_Consequence_Consequence1" FOREIGN KEY ("consequence_id") REFERENCES "consequence" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT "fk_Variant_has_Consequence_Variant1" FOREIGN KEY ("variant_id") REFERENCES "variant" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
);


CREATE TABLE IF NOT EXISTS "variant_has_variantcaller" (
  "variant_id" int(11) NOT NULL,
  "variantcaller_id" int(11) NOT NULL,
  PRIMARY KEY ("variant_id","variantcaller_id"),
  UNIQUE KEY "fk_idx5" ("variant_id","variantcaller_id"),
  KEY "fk_variant_has_variantcaller_variantcaller_idx" ("variantcaller_id"),
  KEY "fk_variant_has_variantcaller_variant_idx" ("variant_id"),
  CONSTRAINT "fk_Variant_has_VariantCaller_Variant1" FOREIGN KEY ("variant_id") REFERENCES "variant" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT "fk_Variant_has_VariantCaller_VariantCaller1" FOREIGN KEY ("variantcaller_id") REFERENCES "variantcaller" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
);


CREATE TABLE IF NOT EXISTS "consequence_has_gene" (
  "consequence_id" int(11) NOT NULL,
  "gene_id" int(11) NOT NULL,
  PRIMARY KEY ("consequence_id","gene_id"),
  KEY "fk_consequence_has_gene_gene_idx" ("gene_id"),
  KEY "fk_consequence_has_gene_consequence_idx" ("consequence_id"),
  CONSTRAINT "fk_Consequence_has_Gene_Consequence1" FOREIGN KEY ("consequence_id") REFERENCES "consequence" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT "fk_Consequence_has_Gene_Gene1" FOREIGN KEY ("gene_id") REFERENCES "gene" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
);


CREATE TABLE IF NOT EXISTS "ensembl_has_gene" (
  "ensembl_id" int(11) NOT NULL,
  "gene_id" int(11) NOT NULL,
  PRIMARY KEY ("ensembl_id","gene_id"),
  KEY "fk_ensembl_has_gene_gene_idx" ("gene_id"),
  KEY "fk_ensembl_has_gene_ensembl_idx" ("ensembl_id"),
  CONSTRAINT "fk_Ensembl_has_Gene_Ensembl1" FOREIGN KEY ("ensembl_id") REFERENCES "ensembl" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT "fk_Ensembl_has_Gene_Gene1" FOREIGN KEY ("gene_id") REFERENCES "gene" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
);


CREATE TABLE IF NOT EXISTS "sample_has_variant" (
  "sample_identifier" varchar(15) NOT NULL,
  "variant_id" int(11) NOT NULL,
  PRIMARY KEY ("sample_identifier","variant_id"),
  KEY "fk_sample_has_variant_variant_idx" ("variant_id"),
  KEY "fk_sample_has_variant_sample_idx" ("sample_identifier"),
  CONSTRAINT "fk_Sample_has_Variant_Sample1" FOREIGN KEY ("sample_identifier") REFERENCES "sample" ("identifier") ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT "fk_Sample_has_Variant_Variant1" FOREIGN KEY ("variant_id") REFERENCES "variant" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
);



