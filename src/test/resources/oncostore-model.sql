
DROP SCHEMA IF EXISTS `oncostore` ;

CREATE SCHEMA IF NOT EXISTS `oncostore`;
USE `oncostore` ;

DROP TABLE IF EXISTS `oncostore`.`Gene` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`Gene` (
  `id` VARCHAR(45) NOT NULL,
  `symbol` VARCHAR(25) NULL,
  `name` VARCHAR(45) NULL,
  `bioType` VARCHAR(45) NULL,
  `chr` INT NULL,
  `start` BIGINT NULL,
  `end` BIGINT NULL,
  `synonyms` VARCHAR(45) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


DROP TABLE IF EXISTS `oncostore`.`Variant` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`Variant` (
  `id` VARCHAR(36) NOT NULL,
  `chr` VARCHAR(15) NOT NULL,
  `start` BIGINT NOT NULL,
  `end` BIGINT NOT NULL,
  `ref` TINYTEXT NOT NULL,
  `obs` TINYTEXT NOT NULL,
  `isSomatic` TINYINT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


DROP TABLE IF EXISTS `oncostore`.`Consequence` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`Consequence` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `codingChange` VARCHAR(128) NULL,
  `aaChange` VARCHAR(25) NULL,
  `aaStart` INT NULL,
  `aaEnd` INT NULL,
  `type` VARCHAR(128) NOT NULL,
  `impact` VARCHAR(25) NOT NULL,
  `strand` TINYINT NULL,
  `transcriptID` VARCHAR(128) NOT NULL,
  `transcriptVersion` INT NULL,
  `canonical` TINYINT NULL,
  `bioType` VARCHAR(45) NOT NULL,
  `refSeqID` VARCHAR(45) NULL,
  `Gene_id` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Consequence_Gene1_idx` (`Gene_id` ASC),
  CONSTRAINT `fk_Consequence_Gene1`
    FOREIGN KEY (`Gene_id`)
    REFERENCES `oncostore`.`Gene` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


DROP TABLE IF EXISTS `oncostore`.`ReferenceGenome` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`ReferenceGenome` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `source` VARCHAR(45) NULL,
  `build` VARCHAR(45) NULL,
  `version` VARCHAR(45) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


DROP TABLE IF EXISTS `oncostore`.`AnnotationSoftware` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`AnnotationSoftware` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `version` VARCHAR(15) NOT NULL,
  `DOI` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


DROP TABLE IF EXISTS `oncostore`.`Project` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`Project` (
  `id` INT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


DROP TABLE IF EXISTS `oncostore`.`Case` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`Case` (
  `id` INT NOT NULL,
  `Project_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Case_Project1_idx` (`Project_id` ASC),
  CONSTRAINT `fk_Case_Project1`
    FOREIGN KEY (`Project_id`)
    REFERENCES `oncostore`.`Project` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


DROP TABLE IF EXISTS `oncostore`.`VariantCaller` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`VariantCaller` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `version` VARCHAR(15) NOT NULL,
  `DOI` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


DROP TABLE IF EXISTS `oncostore`.`Sample` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`Sample` (
  `qbicID` VARCHAR(15) NOT NULL,
  `Case_id` INT NULL,
  `cancerEntity` VARCHAR(45) NULL,
  INDEX `fk_Sample_Case1_idx` (`Case_id` ASC),
  PRIMARY KEY (`qbicID`),
  CONSTRAINT `fk_Sample_Case1`
    FOREIGN KEY (`Case_id`)
    REFERENCES `oncostore`.`Case` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


DROP TABLE IF EXISTS `oncostore`.`Gene_has_ReferenceGenome` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`Gene_has_ReferenceGenome` (
  `Gene_id` VARCHAR(45) NOT NULL,
  `ReferenceGenome_id` INT NOT NULL,
  PRIMARY KEY (`Gene_id`, `ReferenceGenome_id`),
  INDEX `fk_Gene_has_ReferenceGenome_ReferenceGenome1_idx` (`ReferenceGenome_id` ASC),
  INDEX `fk_Gene_has_ReferenceGenome_Gene1_idx` (`Gene_id` ASC),
  CONSTRAINT `fk_Gene_has_ReferenceGenome_Gene1`
    FOREIGN KEY (`Gene_id`)
    REFERENCES `oncostore`.`Gene` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Gene_has_ReferenceGenome_ReferenceGenome1`
    FOREIGN KEY (`ReferenceGenome_id`)
    REFERENCES `oncostore`.`ReferenceGenome` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


DROP TABLE IF EXISTS `oncostore`.`AnnotationSoftware_has_Consequence` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`AnnotationSoftware_has_Consequence` (
  `AnnotationSoftware_id` INT NOT NULL,
  `Consequence_id` INT NOT NULL,
  PRIMARY KEY (`AnnotationSoftware_id`, `Consequence_id`),
  INDEX `fk_AnnotationSoftware_has_Consequence_Consequence1_idx` (`Consequence_id` ASC),
  INDEX `fk_AnnotationSoftware_has_Consequence_AnnotationSoftware1_idx` (`AnnotationSoftware_id` ASC),
  CONSTRAINT `fk_AnnotationSoftware_has_Consequence_AnnotationSoftware1`
    FOREIGN KEY (`AnnotationSoftware_id`)
    REFERENCES `oncostore`.`AnnotationSoftware` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_AnnotationSoftware_has_Consequence_Consequence1`
    FOREIGN KEY (`Consequence_id`)
    REFERENCES `oncostore`.`Consequence` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


DROP TABLE IF EXISTS `oncostore`.`Variant_has_ReferenceGenome` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`Variant_has_ReferenceGenome` (
  `Variant_id` VARCHAR(36) NOT NULL,
  `ReferenceGenome_id` INT NOT NULL,
  PRIMARY KEY (`Variant_id`, `ReferenceGenome_id`),
  INDEX `fk_Variant_has_ReferenceGenome_ReferenceGenome1_idx` (`ReferenceGenome_id` ASC),
  INDEX `fk_Variant_has_ReferenceGenome_Variant1_idx` (`Variant_id` ASC),
  CONSTRAINT `fk_Variant_has_ReferenceGenome_Variant1`
    FOREIGN KEY (`Variant_id`)
    REFERENCES `oncostore`.`Variant` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Variant_has_ReferenceGenome_ReferenceGenome1`
    FOREIGN KEY (`ReferenceGenome_id`)
    REFERENCES `oncostore`.`ReferenceGenome` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


DROP TABLE IF EXISTS `oncostore`.`Variant_has_Consequence` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`Variant_has_Consequence` (
  `Variant_id` VARCHAR(36) NOT NULL,
  `Consequence_id` INT NOT NULL,
  PRIMARY KEY (`Variant_id`, `Consequence_id`),
  INDEX `fk_Variant_has_Consequence_Consequence1_idx` (`Consequence_id` ASC),
  INDEX `fk_Variant_has_Consequence_Variant1_idx` (`Variant_id` ASC),
  CONSTRAINT `fk_Variant_has_Consequence_Variant1`
    FOREIGN KEY (`Variant_id`)
    REFERENCES `oncostore`.`Variant` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Variant_has_Consequence_Consequence1`
    FOREIGN KEY (`Consequence_id`)
    REFERENCES `oncostore`.`Consequence` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


DROP TABLE IF EXISTS `oncostore`.`Variant_has_VariantCaller` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`Variant_has_VariantCaller` (
  `Variant_id` VARCHAR(36) NOT NULL,
  `VariantCaller_id` INT NOT NULL,
  PRIMARY KEY (`Variant_id`, `VariantCaller_id`),
  INDEX `fk_Variant_has_VariantCaller_VariantCaller1_idx` (`VariantCaller_id` ASC),
  INDEX `fk_Variant_has_VariantCaller_Variant1_idx` (`Variant_id` ASC),
  CONSTRAINT `fk_Variant_has_VariantCaller_Variant1`
    FOREIGN KEY (`Variant_id`)
    REFERENCES `oncostore`.`Variant` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Variant_has_VariantCaller_VariantCaller1`
    FOREIGN KEY (`VariantCaller_id`)
    REFERENCES `oncostore`.`VariantCaller` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


DROP TABLE IF EXISTS `oncostore`.`Sample_has_Variant` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`Sample_has_Variant` (
  `Sample_qbicID` VARCHAR(15) NOT NULL,
  `Variant_id` VARCHAR(36) NOT NULL,
  PRIMARY KEY (`Sample_qbicID`, `Variant_id`),
  INDEX `fk_Sample_has_Variant_Variant1_idx` (`Variant_id` ASC),
  INDEX `fk_Sample_has_Variant_Sample1_idx` (`Sample_qbicID` ASC),
  CONSTRAINT `fk_Sample_has_Variant_Sample1`
    FOREIGN KEY (`Sample_qbicID`)
    REFERENCES `oncostore`.`Sample` (`qbicID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Sample_has_Variant_Variant1`
    FOREIGN KEY (`Variant_id`)
    REFERENCES `oncostore`.`Variant` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;