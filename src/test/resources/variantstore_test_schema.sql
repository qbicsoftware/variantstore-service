CREATE TABLE IF NOT EXISTS "Gene" (
  "id" int(11) NOT NULL,
  "symbol" varchar(25) DEFAULT NULL,
  "name" varchar(45) DEFAULT NULL,
  "bioType" varchar(45) DEFAULT NULL,
  "chr" varchar(15) DEFAULT NULL,
  "start" bigint(20) DEFAULT NULL,
  "end" bigint(20) DEFAULT NULL,
  "synonyms" varchar(45) DEFAULT NULL,
  "geneId" varchar(45) DEFAULT NULL,
  "description" varchar(255) DEFAULT NULL,
  "strand" varchar(1) DEFAULT NULL,
  "version" int(11) DEFAULT NULL,
  PRIMARY KEY ("id"),
  UNIQUE KEY "gene_idx" ("symbol","name","bioType","chr","start","end","synonyms","geneId","description","strand","version")
);


CREATE TABLE IF NOT EXISTS "Variant" (
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


CREATE TABLE IF NOT EXISTS "Consequence" (
  "id" int(11) NOT NULL,
  "allele" varchar(45) DEFAULT NULL,
  "codingChange" varchar(128) DEFAULT NULL,
  "transcriptId" varchar(128) NOT NULL,
  "transcriptVersion" int(11) DEFAULT NULL,
  "type" varchar(128) NOT NULL,
  "bioType" varchar(45) NOT NULL,
  "canonical" tinyint(4) DEFAULT NULL,
  "aaChange" varchar(60) DEFAULT NULL,
  "cdnaPosition" varchar(45) DEFAULT NULL,
  "cdsPosition" varchar(45) DEFAULT NULL,
  "proteinPosition" varchar(45) DEFAULT NULL,
  "proteinLength" int(11) DEFAULT NULL,
  "cdnaLength" int(11) DEFAULT NULL,
  "cdsLength" int(11) DEFAULT NULL,
  "impact" varchar(25) NOT NULL,
  "exon" varchar(45) DEFAULT NULL,
  "intron" varchar(45) DEFAULT NULL,
  "strand" int(11) DEFAULT NULL,
  "geneSymbol" varchar(45) DEFAULT NULL,
  "featureType" varchar(128) DEFAULT NULL,
  "distance" int(11) DEFAULT NULL,
  "warnings" varchar(255) DEFAULT NULL,
  PRIMARY KEY ("id"),
  UNIQUE KEY "fk_idx1" ("codingChange","aaChange","proteinPosition","proteinLength","type","impact","strand","transcriptId","transcriptVersion","canonical","bioType","cdnaPosition","cdsPosition","cdnaLength","cdsLength","geneSymbol","featureType","distance","allele","exon","intron")
);


CREATE TABLE IF NOT EXISTS "ReferenceGenome" (
  "id" int(11) NOT NULL,
  "source" varchar(45) DEFAULT NULL,
  "build" varchar(45) DEFAULT NULL,
  "version" varchar(45) DEFAULT NULL,
  PRIMARY KEY ("id"),
  UNIQUE KEY "rf_idx" ("source","build","version")
);


CREATE TABLE IF NOT EXISTS "AnnotationSoftware" (
  "id" int(11) NOT NULL,
  "name" varchar(45) NOT NULL,
  "version" varchar(15) NOT NULL,
  "DOI" varchar(30) NOT NULL,
  PRIMARY KEY ("id"),
  UNIQUE KEY "fk_idx" ("name","version","DOI")
);


CREATE TABLE IF NOT EXISTS "Project" (
  "id" varchar(15) NOT NULL,
  PRIMARY KEY ("id"),
  UNIQUE KEY "id_UNIQUE2" ("id")
);


CREATE TABLE IF NOT EXISTS "Entity" (
  "id" varchar(15) NOT NULL,
  "Project_id" varchar(15) DEFAULT NULL,
  PRIMARY KEY ("id"),
  UNIQUE KEY "id_UNIQUE" ("id"),
  KEY "fk_Case_Project1_idx" ("Project_id"),
  CONSTRAINT "fk_Case_Project1" FOREIGN KEY ("Project_id") REFERENCES "Project" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
);


CREATE TABLE IF NOT EXISTS "VariantCaller" (
  "id" int(11) NOT NULL,
  "name" varchar(45) NOT NULL,
  "version" varchar(15) NOT NULL,
  "DOI" varchar(30) NOT NULL,
  PRIMARY KEY ("id"),
  UNIQUE KEY "fk_idx4" ("name","version","DOI")
);


CREATE TABLE IF NOT EXISTS "Sample" (
  "identifier" varchar(15) NOT NULL,
  "Entity_id" varchar(15) DEFAULT NULL,
  "cancerEntity" varchar(45) DEFAULT NULL,
  PRIMARY KEY ("identifier"),
  UNIQUE KEY "qbicID_UNIQUE" ("identifier"),
  UNIQUE KEY "fk_idx3" ("identifier","Entity_id","cancerEntity"),
  KEY "fk_Sample_Case1_idx" ("Entity_id"),
  CONSTRAINT "fk_Sample_Case1" FOREIGN KEY ("Entity_id") REFERENCES "Entity" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
);


CREATE TABLE IF NOT EXISTS "AnnotationSoftware_has_Consequence" (
  "AnnotationSoftware_id" int(11) NOT NULL,
  "Consequence_id" int(11) NOT NULL,
  PRIMARY KEY ("AnnotationSoftware_id","Consequence_id"),
  UNIQUE KEY "fk_idx0" ("AnnotationSoftware_id","Consequence_id"),
  KEY "fk_AnnotationSoftware_has_Consequence_Consequence1_idx" ("Consequence_id"),
  KEY "fk_AnnotationSoftware_has_Consequence_AnnotationSoftware1_idx" ("AnnotationSoftware_id"),
  CONSTRAINT "fk_AnnotationSoftware_has_Consequence_AnnotationSoftware1" FOREIGN KEY ("AnnotationSoftware_id") REFERENCES "AnnotationSoftware" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT "fk_AnnotationSoftware_has_Consequence_Consequence1" FOREIGN KEY ("Consequence_id") REFERENCES "Consequence" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
);


CREATE TABLE IF NOT EXISTS "Variant_has_ReferenceGenome" (
  "Variant_id" int(11) NOT NULL,
  "ReferenceGenome_id" int(11) NOT NULL,
  PRIMARY KEY ("Variant_id","ReferenceGenome_id"),
  UNIQUE KEY "fk_idx6" ("Variant_id","ReferenceGenome_id"),
  KEY "fk_Variant_has_ReferenceGenome_ReferenceGenome1_idx" ("ReferenceGenome_id"),
  KEY "fk_Variant_has_ReferenceGenome_Variant1_idx" ("Variant_id"),
  CONSTRAINT "fk_Variant_has_ReferenceGenome_ReferenceGenome1" FOREIGN KEY ("ReferenceGenome_id") REFERENCES "ReferenceGenome" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT "fk_Variant_has_ReferenceGenome_Variant1" FOREIGN KEY ("Variant_id") REFERENCES "Variant" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
);


CREATE TABLE IF NOT EXISTS "Variant_has_Consequence" (
  "Variant_id" int(11) NOT NULL,
  "Consequence_id" int(11) NOT NULL,
  PRIMARY KEY ("Variant_id","Consequence_id"),
  UNIQUE KEY "fk_idx5" ("Variant_id","Consequence_id"),
  KEY "fk_Variant_has_Consequence_Consequence1_idx" ("Consequence_id"),
  KEY "fk_Variant_has_Consequence_Variant1_idx" ("Variant_id"),
  CONSTRAINT "fk_Variant_has_Consequence_Consequence1" FOREIGN KEY ("Consequence_id") REFERENCES "Consequence" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT "fk_Variant_has_Consequence_Variant1" FOREIGN KEY ("Variant_id") REFERENCES "Variant" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
);


CREATE TABLE IF NOT EXISTS "Variant_has_VariantCaller" (
  "Variant_id" int(11) NOT NULL,
  "VariantCaller_id" int(11) NOT NULL,
  PRIMARY KEY ("Variant_id","VariantCaller_id"),
  UNIQUE KEY "fk_idx7" ("Variant_id","VariantCaller_id"),
  KEY "fk_Variant_has_VariantCaller_VariantCaller1_idx" ("VariantCaller_id"),
  KEY "fk_Variant_has_VariantCaller_Variant1_idx" ("Variant_id"),
  CONSTRAINT "fk_Variant_has_VariantCaller_Variant1" FOREIGN KEY ("Variant_id") REFERENCES "Variant" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT "fk_Variant_has_VariantCaller_VariantCaller1" FOREIGN KEY ("VariantCaller_id") REFERENCES "VariantCaller" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
);


CREATE TABLE IF NOT EXISTS "Ensembl" (
  "id" int(11) NOT NULL,
  "version" int(11) DEFAULT NULL,
  "date" varchar(45) DEFAULT NULL,
  "ReferenceGenome_id" int(11) NOT NULL,
  PRIMARY KEY ("id"),
  UNIQUE KEY "ensembl_index" ("version","date"),
  KEY "fk_Ensembl_ReferenceGenome1_idx" ("ReferenceGenome_id"),
  CONSTRAINT "fk_Ensembl_ReferenceGenome1" FOREIGN KEY ("ReferenceGenome_id") REFERENCES "ReferenceGenome" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
);


CREATE TABLE IF NOT EXISTS "Consequence_has_Gene" (
  "Consequence_id" int(11) NOT NULL,
  "Gene_id" int(11) NOT NULL,
  PRIMARY KEY ("Consequence_id","Gene_id"),
  KEY "fk_Consequence_has_Gene_Gene1_idx" ("Gene_id"),
  KEY "fk_Consequence_has_Gene_Consequence1_idx" ("Consequence_id"),
  CONSTRAINT "fk_Consequence_has_Gene_Consequence1" FOREIGN KEY ("Consequence_id") REFERENCES "Consequence" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT "fk_Consequence_has_Gene_Gene1" FOREIGN KEY ("Gene_id") REFERENCES "Gene" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
);


CREATE TABLE IF NOT EXISTS "Ensembl_has_Gene" (
  "Ensembl_id" int(11) NOT NULL,
  "Gene_id" int(11) NOT NULL,
  PRIMARY KEY ("Ensembl_id","Gene_id"),
  KEY "fk_Ensembl_has_Gene_Gene1_idx" ("Gene_id"),
  KEY "fk_Ensembl_has_Gene_Ensembl1_idx" ("Ensembl_id"),
  CONSTRAINT "fk_Ensembl_has_Gene_Ensembl1" FOREIGN KEY ("Ensembl_id") REFERENCES "Ensembl" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT "fk_Ensembl_has_Gene_Gene1" FOREIGN KEY ("Gene_id") REFERENCES "Gene" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
);


CREATE TABLE IF NOT EXISTS "Sample_has_Variant" (
  "Sample_identifier" varchar(15) NOT NULL,
  "Variant_id" int(11) NOT NULL,
  PRIMARY KEY ("Sample_identifier","Variant_id"),
  KEY "fk_Sample_has_Variant_Variant1_idx" ("Variant_id"),
  KEY "fk_Sample_has_Variant_Sample1_idx" ("Sample_identifier"),
  CONSTRAINT "fk_Sample_has_Variant_Sample1" FOREIGN KEY ("Sample_identifier") REFERENCES "Sample" ("identifier") ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT "fk_Sample_has_Variant_Variant1" FOREIGN KEY ("Variant_id") REFERENCES "Variant" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
);













