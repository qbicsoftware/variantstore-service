-- MySQL Script generated by MySQL Workbench
-- Wed Nov  6 09:03:31 2019
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema oncostore
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `oncostore` ;

-- -----------------------------------------------------
-- Schema oncostore
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `oncostore` DEFAULT CHARACTER SET utf8 ;
USE `oncostore` ;

-- -----------------------------------------------------
-- Table `oncostore`.`Gene`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `oncostore`.`Gene` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`Gene` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `symbol` VARCHAR(25) NULL,
  `name` VARCHAR(45) NULL,
  `bioType` VARCHAR(45) NULL,
  `chr` VARCHAR(15) NULL,
  `start` BIGINT NULL,
  `end` BIGINT NULL,
  `synonyms` VARCHAR(45) NULL,
  `geneID` VARCHAR(45) NULL,
  `description` VARCHAR(255) NULL,
  `strand` VARCHAR(1) NULL,
  `version` INT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `gene_idx` (`symbol` ASC, `name` ASC, `bioType` ASC, `chr` ASC, `start` ASC, `end` ASC, `synonyms` ASC, `geneID` ASC, `description` ASC, `strand` ASC, `version` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `oncostore`.`Variant`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `oncostore`.`Variant` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`Variant` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `chr` VARCHAR(15) NOT NULL,
  `start` BIGINT NOT NULL,
  `end` BIGINT NOT NULL,
  `ref` TINYTEXT NOT NULL,
  `obs` TINYTEXT NOT NULL,
  `isSomatic` TINYINT NOT NULL,
  `uuid` VARCHAR(36) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `variant_idx` (`chr` ASC, `start` ASC, `end` ASC, `ref`(255) ASC, `obs`(255) ASC, `isSomatic` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `oncostore`.`Consequence`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `oncostore`.`Consequence` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`Consequence` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `codingChange` VARCHAR(128) NULL,
  `aaChange` VARCHAR(60) NULL,
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
  PRIMARY KEY (`id`),
  UNIQUE INDEX `fk_idx` (`codingChange` ASC, `aaChange` ASC, `aaStart` ASC, `aaEnd` ASC, `type` ASC, `impact` ASC, `strand` ASC, `transcriptID` ASC, `transcriptVersion` ASC, `canonical` ASC, `bioType` ASC, `refSeqID` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `oncostore`.`ReferenceGenome`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `oncostore`.`ReferenceGenome` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`ReferenceGenome` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `source` VARCHAR(45) NULL,
  `build` VARCHAR(45) NULL,
  `version` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `rf_idx` (`source` ASC, `build` ASC, `version` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `oncostore`.`AnnotationSoftware`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `oncostore`.`AnnotationSoftware` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`AnnotationSoftware` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `version` VARCHAR(15) NOT NULL,
  `DOI` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `fk_idx` (`name` ASC, `version` ASC, `DOI` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `oncostore`.`Project`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `oncostore`.`Project` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`Project` (
  `id` VARCHAR(15) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `oncostore`.`Entity`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `oncostore`.`Entity` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`Entity` (
  `id` VARCHAR(15) NOT NULL,
  `Project_id` VARCHAR(15) NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Case_Project1_idx` (`Project_id` ASC),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  CONSTRAINT `fk_Case_Project1`
    FOREIGN KEY (`Project_id`)
    REFERENCES `oncostore`.`Project` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `oncostore`.`VariantCaller`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `oncostore`.`VariantCaller` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`VariantCaller` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `version` VARCHAR(15) NOT NULL,
  `DOI` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `fk_idx` (`name` ASC, `version` ASC, `DOI` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `oncostore`.`Sample`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `oncostore`.`Sample` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`Sample` (
  `qbicID` VARCHAR(15) NOT NULL,
  `Entity_id` VARCHAR(15) NULL,
  `cancerEntity` VARCHAR(45) NULL,
  INDEX `fk_Sample_Case1_idx` (`Entity_id` ASC),
  PRIMARY KEY (`qbicID`),
  UNIQUE INDEX `fk_idx` (`qbicID` ASC, `Entity_id` ASC, `cancerEntity` ASC),
  UNIQUE INDEX `qbicID_UNIQUE` (`qbicID` ASC),
  CONSTRAINT `fk_Sample_Case1`
    FOREIGN KEY (`Entity_id`)
    REFERENCES `oncostore`.`Entity` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `oncostore`.`AnnotationSoftware_has_Consequence`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `oncostore`.`AnnotationSoftware_has_Consequence` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`AnnotationSoftware_has_Consequence` (
  `AnnotationSoftware_id` INT NOT NULL,
  `Consequence_id` INT NOT NULL,
  PRIMARY KEY (`AnnotationSoftware_id`, `Consequence_id`),
  INDEX `fk_AnnotationSoftware_has_Consequence_Consequence1_idx` (`Consequence_id` ASC),
  INDEX `fk_AnnotationSoftware_has_Consequence_AnnotationSoftware1_idx` (`AnnotationSoftware_id` ASC),
  UNIQUE INDEX `fk_idx` (`AnnotationSoftware_id` ASC, `Consequence_id` ASC),
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


-- -----------------------------------------------------
-- Table `oncostore`.`Variant_has_ReferenceGenome`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `oncostore`.`Variant_has_ReferenceGenome` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`Variant_has_ReferenceGenome` (
  `Variant_id` INT NOT NULL,
  `ReferenceGenome_id` INT NOT NULL,
  PRIMARY KEY (`Variant_id`, `ReferenceGenome_id`),
  INDEX `fk_Variant_has_ReferenceGenome_ReferenceGenome1_idx` (`ReferenceGenome_id` ASC),
  INDEX `fk_Variant_has_ReferenceGenome_Variant1_idx` (`Variant_id` ASC),
  UNIQUE INDEX `fk_idx` (`Variant_id` ASC, `ReferenceGenome_id` ASC),
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


-- -----------------------------------------------------
-- Table `oncostore`.`Variant_has_Consequence`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `oncostore`.`Variant_has_Consequence` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`Variant_has_Consequence` (
  `Variant_id` INT NOT NULL,
  `Consequence_id` INT NOT NULL,
  PRIMARY KEY (`Variant_id`, `Consequence_id`),
  INDEX `fk_Variant_has_Consequence_Consequence1_idx` (`Consequence_id` ASC),
  INDEX `fk_Variant_has_Consequence_Variant1_idx` (`Variant_id` ASC),
  UNIQUE INDEX `fk_idx` (`Variant_id` ASC, `Consequence_id` ASC),
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


-- -----------------------------------------------------
-- Table `oncostore`.`Variant_has_VariantCaller`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `oncostore`.`Variant_has_VariantCaller` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`Variant_has_VariantCaller` (
  `Variant_id` INT NOT NULL,
  `VariantCaller_id` INT NOT NULL,
  PRIMARY KEY (`Variant_id`, `VariantCaller_id`),
  INDEX `fk_Variant_has_VariantCaller_VariantCaller1_idx` (`VariantCaller_id` ASC),
  INDEX `fk_Variant_has_VariantCaller_Variant1_idx` (`Variant_id` ASC),
  UNIQUE INDEX `fk_idx` (`Variant_id` ASC, `VariantCaller_id` ASC),
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


-- -----------------------------------------------------
-- Table `oncostore`.`Sample_has_Variant`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `oncostore`.`Sample_has_Variant` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`Sample_has_Variant` (
  `Sample_qbicID` VARCHAR(15) NOT NULL,
  `Variant_id` INT NOT NULL,
  PRIMARY KEY (`Sample_qbicID`, `Variant_id`),
  INDEX `fk_Sample_has_Variant_Variant1_idx` (`Variant_id` ASC),
  INDEX `fk_Sample_has_Variant_Sample1_idx` (`Sample_qbicID` ASC),
  UNIQUE INDEX `fk_idx` (`Sample_qbicID` ASC, `Variant_id` ASC),
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


-- -----------------------------------------------------
-- Table `oncostore`.`Ensembl`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `oncostore`.`Ensembl` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`Ensembl` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `version` INT NULL,
  `date` VARCHAR(45) NULL,
  `ReferenceGenome_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Ensembl_ReferenceGenome1_idx` (`ReferenceGenome_id` ASC),
  UNIQUE INDEX `ensembl_index` (`version` ASC, `date` ASC),
  CONSTRAINT `fk_Ensembl_ReferenceGenome1`
    FOREIGN KEY (`ReferenceGenome_id`)
    REFERENCES `oncostore`.`ReferenceGenome` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `oncostore`.`Consequence_has_Gene`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `oncostore`.`Consequence_has_Gene` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`Consequence_has_Gene` (
  `Consequence_id` INT NOT NULL,
  `Gene_id` INT NOT NULL,
  PRIMARY KEY (`Consequence_id`, `Gene_id`),
  INDEX `fk_Consequence_has_Gene_Gene1_idx` (`Gene_id` ASC),
  INDEX `fk_Consequence_has_Gene_Consequence1_idx` (`Consequence_id` ASC),
  CONSTRAINT `fk_Consequence_has_Gene_Consequence1`
    FOREIGN KEY (`Consequence_id`)
    REFERENCES `oncostore`.`Consequence` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Consequence_has_Gene_Gene1`
    FOREIGN KEY (`Gene_id`)
    REFERENCES `oncostore`.`Gene` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `oncostore`.`Ensembl_has_Gene`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `oncostore`.`Ensembl_has_Gene` ;

CREATE TABLE IF NOT EXISTS `oncostore`.`Ensembl_has_Gene` (
  `Ensembl_id` INT NOT NULL,
  `Gene_id` INT NOT NULL,
  PRIMARY KEY (`Ensembl_id`, `Gene_id`),
  INDEX `fk_Ensembl_has_Gene_Gene1_idx` (`Gene_id` ASC),
  INDEX `fk_Ensembl_has_Gene_Ensembl1_idx` (`Ensembl_id` ASC),
  CONSTRAINT `fk_Ensembl_has_Gene_Ensembl1`
    FOREIGN KEY (`Ensembl_id`)
    REFERENCES `oncostore`.`Ensembl` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Ensembl_has_Gene_Gene1`
    FOREIGN KEY (`Gene_id`)
    REFERENCES `oncostore`.`Gene` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
