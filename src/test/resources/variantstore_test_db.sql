-- MariaDB dump 10.17  Distrib 10.4.13-MariaDB, for Linux (x86_64)
--
-- Host: localhost    Database: oncostore
-- ------------------------------------------------------
-- Server version	10.4.13-MariaDB-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET FOREIGN_KEY_CHECKS=0;
--
-- Current Database: `oncostore`
--

USE `oncostore`;

--
-- Table structure for table `annotationsoftware`
--

DROP TABLE IF EXISTS `annotationsoftware`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `annotationsoftware` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `version` varchar(15) NOT NULL,
  `doi` varchar(30) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `fk_idx` (`name`,`version`,`doi`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `annotationsoftware`
--

LOCK TABLES `annotationsoftware` WRITE;
/*!40000 ALTER TABLE `annotationsoftware` DISABLE KEYS */;
INSERT INTO `annotationsoftware` VALUES (1,'snpeff','4.3t','10.4161/fly.19695');
/*!40000 ALTER TABLE `annotationsoftware` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `annotationsoftware_has_consequence`
--

DROP TABLE IF EXISTS `annotationsoftware_has_consequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `annotationsoftware_has_consequence` (
  `annotationsoftware_id` int(11) NOT NULL,
  `consequence_id` int(11) NOT NULL,
  PRIMARY KEY (`annotationsoftware_id`,`consequence_id`),
  UNIQUE KEY `fk_idx` (`annotationsoftware_id`,`consequence_id`),
  KEY `fk_annotationsoftware_has_consequence_consequence_idx` (`consequence_id`),
  KEY `fk_annotationsoftware_has_consequence_annotationsoftware_idx` (`annotationsoftware_id`),
  CONSTRAINT `fk_AnnotationSoftware_has_Consequence_AnnotationSoftware1` FOREIGN KEY (`annotationsoftware_id`) REFERENCES `annotationsoftware` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_AnnotationSoftware_has_Consequence_Consequence1` FOREIGN KEY (`consequence_id`) REFERENCES `consequence` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `annotationsoftware_has_consequence`
--

LOCK TABLES `annotationsoftware_has_consequence` WRITE;
/*!40000 ALTER TABLE `annotationsoftware_has_consequence` DISABLE KEYS */;
INSERT INTO `annotationsoftware_has_consequence` VALUES (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),(1,11),(1,12),(1,13),(1,14),(1,15),(1,16),(1,17),(1,18),(1,19),(1,20),(1,21),(1,22),(1,23),(1,24),(1,25),(1,26),(1,27),(1,28),(1,29),(1,30),(1,31),(1,32),(1,33),(1,34),(1,35),(1,36),(1,37),(1,38),(1,39),(1,40),(1,41),(1,42),(1,43),(1,44),(1,45),(1,46),(1,47),(1,48),(1,49),(1,50),(1,51),(1,52),(1,53),(1,54),(1,55),(1,56),(1,57),(1,58),(1,59),(1,60),(1,61),(1,62),(1,63),(1,64),(1,65),(1,66),(1,67),(1,68),(1,69),(1,71),(1,72),(1,73),(1,74),(1,75),(1,76),(1,77),(1,78),(1,79),(1,80),(1,81),(1,82),(1,83),(1,84),(1,85),(1,86),(1,87),(1,88),(1,89),(1,90),(1,91),(1,92),(1,93),(1,94),(1,95),(1,96);
/*!40000 ALTER TABLE `annotationsoftware_has_consequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consequence`
--

DROP TABLE IF EXISTS `consequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `consequence` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `allele` varchar(45) DEFAULT NULL,
  `codingchange` varchar(128) DEFAULT NULL,
  `transcriptid` varchar(128) NOT NULL,
  `transcriptversion` int(11) DEFAULT NULL,
  `type` varchar(128) NOT NULL,
  `biotype` varchar(45) NOT NULL,
  `canonical` tinyint(4) DEFAULT NULL,
  `aachange` varchar(60) DEFAULT NULL,
  `cdnaposition` varchar(45) DEFAULT NULL,
  `cdsposition` varchar(45) DEFAULT NULL,
  `proteinposition` varchar(45) DEFAULT NULL,
  `proteinlength` int(11) DEFAULT NULL,
  `cdnalength` int(11) DEFAULT NULL,
  `cdslength` int(11) DEFAULT NULL,
  `impact` varchar(25) NOT NULL,
  `exon` varchar(45) DEFAULT NULL,
  `intron` varchar(45) DEFAULT NULL,
  `strand` int(11) DEFAULT NULL,
  `genesymbol` varchar(45) DEFAULT NULL,
  `featuretype` varchar(150) DEFAULT NULL,
  `distance` int(11) DEFAULT NULL,
  `warnings` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `fk_idx` (`codingchange`,`aachange`,`proteinposition`,`proteinlength`,`type`,`impact`,`strand`,`transcriptid`,`transcriptversion`,`canonical`,`biotype`,`cdnaposition`,`cdsposition`,`cdnalength`,`cdslength`,`genesymbol`,`featuretype`,`distance`,`allele`,`exon`,`intron`)
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consequence`
--

LOCK TABLES `consequence` WRITE;
/*!40000 ALTER TABLE `consequence` DISABLE KEYS */;
INSERT INTO `consequence` VALUES (1,'G','c.742A>C','ENST00000334528',-1,'missense_variant','protein_coding',0,'p.Asn248His','742','742','248',1196,12355,3591,'MODERATE','1/17','',0,'FMN1','transcript',-1,''),(2,'G','c.742A>C','ENST00000558197',-1,'missense_variant','protein_coding',0,'p.Asn248His','1020','742','248',503,4060,1512,'MODERATE','1/2','',0,'FMN1','transcript',-1,''),(3,'G','c.2044-2069A>C','ENST00000559047',-1,'sequence_feature','protein_coding',0,'','','','',-1,-1,-1,'LOW','','',0,'FMN1','region_of_interest:Mediates_interaction_with_alpha-catenin',-1,''),(4,'G','c.2044-2069A>C','ENST00000559047',-1,'intron_variant','protein_coding',0,'','','','',-1,-1,-1,'MODIFIER','2/17','',0,'FMN1','transcript',-1,''),(5,'G','c.1868-59068A>C','ENST00000561249',-1,'intron_variant','protein_coding',0,'','','','',-1,-1,-1,'MODIFIER','1/15','',0,'FMN1','transcript',-1,''),(6,'G','n.239+651A>C','ENST00000559150',-1,'intron_variant','processed_transcript',0,'','','','',-1,-1,-1,'MODIFIER','1/1','',0,'FMN1','transcript',-1,''),(7,'T','c.403G>A','ENST00000552197',-1,'missense_variant','protein_coding',0,'p.Glu135Lys','1088','403','135',503,2599,1512,'MODERATE','7/17','',0,'SLC38A1','transcript',-1,''),(8,'T','c.403G>A','ENST00000398637',-1,'missense_variant','protein_coding',0,'p.Glu135Lys','1098','403','135',487,8066,1464,'MODERATE','7/17','',0,'SLC38A1','transcript',-1,''),(9,'T','c.403G>A','ENST00000549049',-1,'missense_variant','protein_coding',0,'p.Glu135Lys','966','403','135',487,3196,1464,'MODERATE','6/16','',0,'SLC38A1','transcript',-1,''),(10,'T','c.403G>A','ENST00000439706',-1,'missense_variant','protein_coding',0,'p.Glu135Lys','1206','403','135',487,3436,1464,'MODERATE','8/18','',0,'SLC38A1','transcript',-1,''),(11,'T','c.403G>A','ENST00000546893',-1,'missense_variant','protein_coding',0,'p.Glu135Lys','866','403','135',487,2332,1464,'MODERATE','7/17','',0,'SLC38A1','transcript',-1,''),(12,'T','c.403G>A','ENST00000546893',-1,'sequence_feature','protein_coding',0,'','','','',-1,-1,-1,'LOW','','',0,'SLC38A1','topological_domain:Cytoplasmic',-1,''),(13,'T','n.460G>A','ENST00000549633',-1,'non_coding_exon_variant','processed_transcript',0,'','','','',-1,-1,-1,'MODIFIER','5/12','',0,'SLC38A1','transcript',-1,''),(14,'T','n.562G>A','ENST00000551506',-1,'non_coding_exon_variant','processed_transcript',0,'','','','',-1,-1,-1,'MODIFIER','5/5','',0,'SLC38A1','transcript',-1,''),(15,'C','c.593C>G','ENST00000318737',-1,'stop_gained','protein_coding',0,'p.Ser198*','722','593','198',1444,4661,4335,'HIGH','6/26','',0,'C9orf84','transcript',-1,''),(16,'C','c.476C>G','ENST00000394777',-1,'stop_gained','protein_coding',0,'p.Ser159*','721','476','159',1370,4955,4113,'HIGH','4/23','',0,'C9orf84','transcript',-1,''),(17,'C','c.476C>G','ENST00000394779',-1,'stop_gained','protein_coding',0,'p.Ser159*','721','476','159',1405,5060,4218,'HIGH','4/24','',0,'C9orf84','transcript',-1,''),(18,'C','c.593C>G','ENST00000374287',-1,'stop_gained','protein_coding',0,'p.Ser198*','854','593','198',1444,4847,4335,'HIGH','8/28','',0,'C9orf84','transcript',-1,''),(19,'C','c.785C>G','ENST00000374283',-1,'stop_gained','protein_coding',0,'p.Ser262*','930','785','262',488,1973,1467,'HIGH','8/12','',0,'C9orf84','transcript',-1,''),(20,'A','c.186G>C','ENST00000369454',-1,'synonymous_variant','protein_coding',0,'p.Ile62Ile','487','186','62',213,3505,642,'LOW','1/2','',0,'RAB39B','transcript',-1,''),(21,'A','c.214G>C','ENST00000313654',-1,'missense_variant','protein_coding',0,'p.Gly72Ser','455','214','72',3333,10661,10002,'MODERATE','1/75','',0,'LAMA3','transcript',-1,''),(22,'A','c.214G>A','ENST00000399516',-1,'missense_variant','protein_coding',0,'p.Gly72Ser','214','214','72',3277,9834,9834,'MODERATE','1/74','',0,'LAMA3','transcript',-1,''),(23,'A','c.214G>A','ENST00000313654',-1,'sequence_feature','protein_coding',0,'','','','',-1,-1,-1,'LOW','','',0,'LAMA3','domain:Laminin_N-terminal',-1,''),(24,'A','c.214G>A','ENST00000399516',-1,'sequence_feature','protein_coding',0,'','','','',-1,-1,-1,'LOW','','',0,'LAMA3','domain:Laminin_N-terminal',-1,''),(25,'A','n.214G>A','ENST00000585600',-1,'non_coding_exon_variant','non_stop_decay',0,'','','','',-1,-1,-1,'MODIFIER','1/13','',0,'LAMA3','transcript',-1,''),(26,'C','c.1445T>C','ENST00000233954',-1,'missense_variant','protein_coding',0,'p.Met482Thr','1716','1445','482',556,2058,1671,'MODERATE','11/11','',0,'IL1RL1','transcript',-1,''),(27,'C','c.1445T>C','ENST00000233954',-1,'sequence_feature','protein_coding',0,'','','','',-1,-1,-1,'LOW','','',0,'IL1RL1','topological_domain:Cytoplasmic',-1,''),(28,'C','c.1445T>C','ENST00000233954',-1,'sequence_feature','protein_coding',0,'','','','',-1,-1,-1,'LOW','','',0,'IL1RL1','domain:TIR',-1,''),(29,'C','c.-10966T>C','ENST00000409599',-1,'upstream_gene_variant','protein_coding',0,'','','','',-1,-1,-1,'MODIFIER','','',0,'IL18R1','transcript',4234,''),(30,'C','n.-4350T>C','ENST00000466357',-1,'upstream_gene_variant','retained_intron',0,'','','','',-1,-1,-1,'MODIFIER','','',0,'IL18R1','transcript',4350,''),(31,'C','c.-28-10938T>C','ENST00000410040',-1,'intron_variant','protein_coding',0,'','','','',-1,-1,-1,'MODIFIER','1/10','',0,'IL18R1','transcript',-1,''),(32,'A','c.1362G>A','ENST00000299440',-1,'synonymous_variant','protein_coding',0,'p.Leu454Leu','1474','1362','454',1043,6564,3132,'LOW','2/2','',0,'RAG1','transcript',-1,''),(33,'A','c.1362G>A','ENST00000299440',-1,'sequence_feature','protein_coding',0,'','','','',-1,-1,-1,'LOW','','',0,'RAG1','DNA-binding_region:NBD',-1,''),(34,'A','n.*908C>T','ENST00000524423',-1,'downstream_gene_variant','processed_transcript',0,'','','','',-1,-1,-1,'MODIFIER','','',0,'RAG2','transcript',908,''),(35,'A','n.1362G>A','ENST00000534663',-1,'non_coding_exon_variant','nonsense_mediated_decay',0,'','','','',-1,-1,-1,'MODIFIER','8/10','',0,'RAG1','transcript',-1,''),(36,'T','c.1944G>T','ENST00000534015',-1,'splice_region_variant&synonymous_variant','protein_coding',0,'p.Pro648Pro','1945','1944','648',805,2787,2418,'LOW','15/20','',0,'NCAM1','transcript',-1,'WARNING_TRANSCRIPT_NO_START_CODON'),(37,'T','c.1839G>T','ENST00000401611',-1,'splice_region_variant&synonymous_variant','protein_coding',0,'p.Thr613Thr','1839','1839','613',664,1995,1995,'LOW','16/18','',0,'NCAM1','transcript',-1,'WARNING_TRANSCRIPT_NO_START_CODON'),(38,'T','n.447G>T','ENST00000526322',-1,'splice_region_variant&non_coding_exon_variant','processed_transcript',0,'','','','',-1,-1,-1,'LOW','6/10','',0,'NCAM1','transcript',-1,''),(39,'T','n.342G>T','ENST00000531817',-1,'splice_region_variant&non_coding_exon_variant','nonsense_mediated_decay',0,'','','','',-1,-1,-1,'LOW','5/11','',0,'NCAM1','transcript',-1,''),(40,'T','n.323G>T','ENST00000528590',-1,'splice_region_variant&non_coding_exon_variant','processed_transcript',0,'','','','',-1,-1,-1,'LOW','4/10','',0,'NCAM1','transcript',-1,''),(41,'T','c.1973-5581G>T','ENST00000524665',-1,'intron_variant','protein_coding',0,'','','','',-1,-1,-1,'MODIFIER','13/18','',0,'NCAM1','transcript',-1,'WARNING_TRANSCRIPT_NO_START_CODON'),(42,'T','n.2242-5581G>T','ENST00000531915',-1,'intron_variant','processed_transcript',0,'','','','',-1,-1,-1,'MODIFIER','15/20','',0,'NCAM1','transcript',-1,''),(43,'T','n.2040+5623G>T','ENST00000527506',-1,'intron_variant','processed_transcript',0,'','','','',-1,-1,-1,'MODIFIER','13/15','',0,'NCAM1','transcript',-1,''),(44,'T','c.1442-5581G>T','ENST00000533760',-1,'intron_variant','protein_coding',0,'','','','',-1,-1,-1,'MODIFIER','13/16','',0,'NCAM1','transcript',-1,''),(45,'T','n.2037-5581G>T','ENST00000397957',-1,'intron_variant','processed_transcript',0,'','','','',-1,-1,-1,'MODIFIER','14/19','',0,'NCAM1','transcript',-1,''),(46,'T','n.231-15090G>T','ENST00000526427',-1,'intron_variant','processed_transcript',0,'','','','',-1,-1,-1,'MODIFIER','1/3','',0,'NCAM1','transcript',-1,''),(47,'T','n.2265-5581G>T','ENST00000531044',-1,'intron_variant','processed_transcript',0,'','','','',-1,-1,-1,'MODIFIER','13/19','',0,'NCAM1','transcript',-1,''),(48,'T','c.1795+5623G>T','ENST00000316851',-1,'intron_variant','protein_coding',0,'','','','',-1,-1,-1,'MODIFIER','13/17','',0,'NCAM1','transcript',-1,''),(49,'T','n.508+4472G>T','ENST00000530543',-1,'intron_variant','processed_transcript',0,'','','','',-1,-1,-1,'MODIFIER','5/6','',0,'NCAM1','transcript',-1,''),(50,'T','n.323+5623G>T','ENST00000533073',-1,'intron_variant','processed_transcript',0,'','','','',-1,-1,-1,'MODIFIER','3/7','',0,'NCAM1','transcript',-1,''),(51,'T','n.192+5623G>T','ENST00000525355',-1,'intron_variant','retained_intron',0,'','','','',-1,-1,-1,'MODIFIER','2/3','',0,'NCAM1','transcript',-1,''),(52,'T','n.281G>T','ENST00000525691',-1,'non_coding_exon_variant','retained_intron',0,'','','','',-1,-1,-1,'MODIFIER','1/4','',0,'NCAM1','transcript',-1,''),(53,'G','c.3226A>G','ENST00000506720',-1,'missense_variant','protein_coding',0,'p.Thr1076Ala','3226','3226','1076',1580,4962,4743,'MODERATE','17/25','',0,'LPHN3','transcript',-1,''),(54,'G','c.3022A>G','ENST00000512091',-1,'missense_variant','protein_coding',0,'p.Thr1008Ala','3769','3022','1008',1240,12636,3723,'MODERATE','18/26','',0,'LPHN3','transcript',-1,''),(55,'G','c.3022A>G','ENST00000514591',-1,'missense_variant','protein_coding',0,'p.Thr1008Ala','3351','3022','1008',1469,6297,4410,'MODERATE','18/25','',0,'LPHN3','transcript',-1,''),(56,'G','c.3022A>G','ENST00000506700',-1,'missense_variant','protein_coding',0,'p.Thr1008Ala','3195','3022','1008',1231,4838,3696,'MODERATE','16/23','',0,'LPHN3','transcript',-1,''),(57,'G','c.3226A>G','ENST00000509896',-1,'missense_variant','protein_coding',0,'p.Thr1076Ala','3399','3226','1076',1308,5069,3927,'MODERATE','17/25','',0,'LPHN3','transcript',-1,''),(58,'G','c.3226A>G','ENST00000511324',-1,'missense_variant','protein_coding',0,'p.Thr1076Ala','3399','3226','1076',1299,5042,3900,'MODERATE','17/24','',0,'LPHN3','transcript',-1,''),(59,'G','c.3022A>G','ENST00000545650',-1,'missense_variant','protein_coding',0,'p.Thr1008Ala','3195','3022','1008',1469,6124,4410,'MODERATE','16/23','',0,'LPHN3','transcript',-1,''),(60,'G','c.3226A>G','ENST00000507164',-1,'missense_variant','protein_coding',0,'p.Thr1076Ala','3355','3226','1076',1342,4982,4029,'MODERATE','17/25','',0,'LPHN3','transcript',-1,''),(61,'G','c.3226A>G','ENST00000508693',-1,'missense_variant','protein_coding',0,'p.Thr1076Ala','3355','3226','1076',1351,5009,4056,'MODERATE','17/26','',0,'LPHN3','transcript',-1,''),(62,'G','c.3226A>G','ENST00000507625',-1,'missense_variant','protein_coding',0,'p.Thr1076Ala','3332','3226','1076',1528,4843,4587,'MODERATE','17/23','',0,'LPHN3','transcript',-1,''),(63,'G','c.3022A>G','ENST00000504896',-1,'missense_variant','protein_coding',0,'p.Thr1008Ala','3117','3022','1008',1283,4816,3852,'MODERATE','16/25','',0,'LPHN3','transcript',-1,''),(64,'G','c.3022A>G','ENST00000514157',-1,'missense_variant','protein_coding',0,'p.Thr1008Ala','3117','3022','1008',1274,4789,3825,'MODERATE','16/24','',0,'LPHN3','transcript',-1,''),(65,'G','c.3226A>G','ENST00000506746',-1,'missense_variant','protein_coding',0,'p.Thr1076Ala','3226','3226','1076',1571,4935,4716,'MODERATE','17/24','',0,'LPHN3','transcript',-1,''),(66,'G','c.3022A>G','ENST00000508946',-1,'missense_variant','protein_coding',0,'p.Thr1008Ala','3022','3022','1008',1512,4758,4539,'MODERATE','16/24','',0,'LPHN3','transcript',-1,''),(67,'G','c.3022A>G','ENST00000514996',-1,'missense_variant','protein_coding',0,'p.Thr1008Ala','3022','3022','1008',1503,4731,4512,'MODERATE','16/23','',0,'LPHN3','transcript',-1,''),(68,'G','c.1393A>G','ENST00000502815',-1,'missense_variant','protein_coding',0,'p.Thr465Ala','1394','1393','465',917,4284,2754,'MODERATE','8/14','',0,'LPHN3','transcript',-1,'WARNING_TRANSCRIPT_NO_START_CODON'),(69,'G','c.3022A>G','ENST00000512091',-1,'sequence_feature','protein_coding',0,'','','','',-1,-1,-1,'LOW','','',0,'LPHN3','topological_domain:Extracellular',-1,''),(71,'A','c.2219G>T','ENST00000272427',-1,'missense_variant','protein_coding',0,'p.Trp740Leu','2350','2219','740',811,5918,2436,'MODERATE','21/22','',0,'EXOC6B','transcript',-1,''),(72,'A','n.158G>T','ENST00000490919',-1,'non_coding_exon_variant','processed_transcript',0,'','','','',-1,-1,-1,'MODIFIER','3/4','',0,'EXOC6B','transcript',-1,''),(73,'A','n.140G>T','ENST00000471335',-1,'non_coding_exon_variant','processed_transcript',0,'','','','',-1,-1,-1,'MODIFIER','3/4','',0,'EXOC6B','transcript',-1,''),(74,'A','n.183G>T','ENST00000492257',-1,'non_coding_exon_variant','processed_transcript',0,'','','','',-1,-1,-1,'MODIFIER','2/3','',0,'EXOC6B','transcript',-1,''),(75,'G','c.1381C>G','ENST00000286398',-1,'missense_variant','protein_coding',0,'p.Leu461Val','1669','1381','461',1197,5976,3594,'MODERATE','11/25','',0,'SMC2','transcript',-1,''),(76,'G','c.1381C>G','ENST00000374793',-1,'missense_variant','protein_coding',0,'p.Leu461Val','1594','1381','461',1197,5909,3594,'MODERATE','11/25','',0,'SMC2','transcript',-1,''),(77,'G','c.1381C>G','ENST00000303219',-1,'missense_variant','protein_coding',0,'p.Leu461Val','1731','1381','461',1099,3650,3300,'MODERATE','11/24','',0,'SMC2','transcript',-1,''),(78,'G','c.1381C>G','ENST00000374787',-1,'missense_variant','protein_coding',0,'p.Leu461Val','1717','1381','461',1197,4266,3594,'MODERATE','11/25','',0,'SMC2','transcript',-1,''),(79,'G','c.1381C>G','ENST00000303219',-1,'sequence_feature','protein_coding',0,'','','','',-1,-1,-1,'LOW','','',0,'SMC2','coiled-coil_region',-1,''),(80,'G','c.1381C>G','ENST00000374787',-1,'sequence_feature','protein_coding',0,'','','','',-1,-1,-1,'LOW','','',0,'SMC2','coiled-coil_region',-1,''),(81,'C','c.586A>G','ENST00000306480',-1,'missense_variant','protein_coding',0,'p.Arg196Gly','732','586','196',271,6198,816,'MODERATE','5/6','',0,'TMEM192','transcript',-1,''),(82,'C','c.574A>G','ENST00000506087',-1,'missense_variant','protein_coding',0,'p.Arg192Gly','757','574','192',267,2225,804,'MODERATE','6/7','',0,'TMEM192','transcript',-1,''),(83,'C','c.163A>G','ENST00000505095',-1,'missense_variant','protein_coding',0,'p.Arg55Gly','698','163','55',83,789,254,'MODERATE','6/6','',0,'TMEM192','transcript',-1,'WARNING_TRANSCRIPT_INCOMPLETE'),(84,'C','c.586A>G','ENST00000306480',-1,'sequence_feature','protein_coding',0,'','','','',-1,-1,-1,'LOW','','',0,'TMEM192','topological_domain:Cytoplasmic',-1,''),(85,'C','c.574A>G','ENST00000506087',-1,'sequence_feature','protein_coding',0,'','','','',-1,-1,-1,'LOW','','',0,'TMEM192','topological_domain:Cytoplasmic',-1,''),(86,'T','c.3152G>A','ENST00000399788',-1,'missense_variant','protein_coding',0,'p.Arg1051Gln','3515','3152','1051',1690,10763,5073,'MODERATE','21/28','',0,'KDM5A','transcript',-1,''),(87,'T','c.3152G>A','ENST00000382815',-1,'missense_variant','protein_coding',0,'p.Arg1051Gln','3515','3152','1051',1636,5274,4911,'MODERATE','21/28','',0,'KDM5A','transcript',-1,''),(88,'T','c.2009G>A','ENST00000544760',-1,'missense_variant','protein_coding',0,'p.Arg670Gln','2200','2009','670',714,2338,2147,'MODERATE','12/13','',0,'KDM5A','transcript',-1,'WARNING_TRANSCRIPT_INCOMPLETE'),(89,'T','n.-3176G>A','ENST00000540156',-1,'upstream_gene_variant','retained_intron',0,'','','','',-1,-1,-1,'MODIFIER','','',0,'KDM5A','transcript',3176,''),(90,'T','n.266G>A','ENST00000535269',-1,'non_coding_exon_variant','retained_intron',0,'','','','',-1,-1,-1,'MODIFIER','1/2','',0,'KDM5A','transcript',-1,''),(91,'A','c.1271G>A','ENST00000262794',-1,'missense_variant','protein_coding',0,'p.Arg424His','1354','1271','424',1211,3960,3636,'MODERATE','9/27','',0,'MOV10L1','transcript',-1,''),(92,'A','c.1271G>A','ENST00000545383',-1,'missense_variant','protein_coding',0,'p.Arg424His','1355','1271','424',1211,3889,3636,'MODERATE','10/28','',0,'MOV10L1','transcript',-1,''),(93,'A','c.1271G>A','ENST00000395858',-1,'missense_variant','protein_coding',0,'p.Arg424His','1295','1271','424',1165,3763,3498,'MODERATE','9/26','',0,'MOV10L1','transcript',-1,''),(94,'A','c.1211G>A','ENST00000540615',-1,'missense_variant','protein_coding',0,'p.Arg404His','1431','1211','404',1165,3941,3498,'MODERATE','9/26','',0,'MOV10L1','transcript',-1,''),(95,'A','c.-1436G>A','ENST00000395843',-1,'5_prime_UTR_variant','protein_coding',0,'','','','',-1,-1,-1,'MODIFIER','9/22','',0,'MOV10L1','transcript',32526,''),(96,'A','n.308G>A','ENST00000434497',-1,'non_coding_exon_variant','nonsense_mediated_decay',0,'','','','',-1,-1,-1,'MODIFIER','3/7','',0,'MOV10L1','transcript',-1,'');
/*!40000 ALTER TABLE `consequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consequence_has_gene`
--

DROP TABLE IF EXISTS `consequence_has_gene`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `consequence_has_gene` (
  `consequence_id` int(11) NOT NULL,
  `gene_id` int(11) NOT NULL,
  PRIMARY KEY (`consequence_id`,`gene_id`),
  KEY `fk_consequence_has_gene_gene_idx` (`gene_id`),
  KEY `fk_consequence_has_gene_consequence_idx` (`consequence_id`),
  CONSTRAINT `fk_Consequence_has_Gene_Consequence1` FOREIGN KEY (`consequence_id`) REFERENCES `consequence` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Consequence_has_Gene_Gene1` FOREIGN KEY (`gene_id`) REFERENCES `gene` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consequence_has_gene`
--

LOCK TABLES `consequence_has_gene` WRITE;
/*!40000 ALTER TABLE `consequence_has_gene` DISABLE KEYS */;
INSERT INTO `consequence_has_gene` VALUES (1,1),(2,1),(3,1),(4,1),(5,1),(6,1),(7,7),(8,7),(9,7),(10,7),(11,7),(12,7),(13,7),(14,7),(15,15),(16,15),(17,15),(18,15),(19,15),(20,20),(21,21),(22,21),(23,21),(24,21),(25,21),(26,26),(27,26),(28,26),(29,29),(30,29),(31,29),(32,32),(33,32),(34,34),(35,32),(36,36),(37,36),(38,36),(39,36),(40,36),(41,36),(42,36),(43,36),(44,36),(45,36),(46,36),(47,36),(48,36),(49,36),(50,36),(51,36),(52,36),(53,53),(54,53),(55,53),(56,53),(57,53),(58,53),(59,53),(60,53),(61,53),(62,53),(63,53),(64,53),(65,53),(66,53),(67,53),(68,53),(69,53),(71,71),(72,71),(73,71),(74,71),(75,75),(76,75),(77,75),(78,75),(79,75),(80,75),(81,81),(82,81),(83,81),(84,81),(85,81),(86,86),(87,86),(88,86),(89,86),(90,86),(91,91),(92,91),(93,91),(94,91),(95,91),(96,91);
/*!40000 ALTER TABLE `consequence_has_gene` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ensembl`
--

DROP TABLE IF EXISTS `ensembl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ensembl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` int(11) DEFAULT NULL,
  `date` varchar(45) DEFAULT NULL,
  `referencegenome_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ensembl_index` (`version`,`date`),
  KEY `fk_ensembl_referencegenome_idx` (`referencegenome_id`),
  CONSTRAINT `fk_Ensembl_ReferenceGenome1` FOREIGN KEY (`referencegenome_id`) REFERENCES `referencegenome` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ensembl`
--

LOCK TABLES `ensembl` WRITE;
/*!40000 ALTER TABLE `ensembl` DISABLE KEYS */;
/*!40000 ALTER TABLE `ensembl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ensembl_has_gene`
--

DROP TABLE IF EXISTS `ensembl_has_gene`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ensembl_has_gene` (
  `ensembl_id` int(11) NOT NULL,
  `gene_id` int(11) NOT NULL,
  PRIMARY KEY (`ensembl_id`,`gene_id`),
  KEY `fk_ensembl_has_gene_gene_idx` (`gene_id`),
  KEY `fk_ensembl_has_gene_ensembl_idx` (`ensembl_id`),
  CONSTRAINT `fk_Ensembl_has_Gene_Ensembl1` FOREIGN KEY (`ensembl_id`) REFERENCES `ensembl` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Ensembl_has_Gene_Gene1` FOREIGN KEY (`gene_id`) REFERENCES `gene` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ensembl_has_gene`
--

LOCK TABLES `ensembl_has_gene` WRITE;
/*!40000 ALTER TABLE `ensembl_has_gene` DISABLE KEYS */;
/*!40000 ALTER TABLE `ensembl_has_gene` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entity`
--

DROP TABLE IF EXISTS `entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `entity` (
  `id` varchar(15) NOT NULL,
  `project_id` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `entity_index` (`id`),
  KEY `fk_vase_project_idx` (`project_id`),
  CONSTRAINT `fk_Case_Project1` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entity`
--

LOCK TABLES `entity` WRITE;
/*!40000 ALTER TABLE `entity` DISABLE KEYS */;
INSERT INTO `entity` VALUES ('patient1',NULL),('patient2',NULL),('patient3',NULL);
/*!40000 ALTER TABLE `entity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gene`
--

DROP TABLE IF EXISTS `gene`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gene` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `symbol` varchar(25) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `biotype` varchar(45) DEFAULT NULL,
  `chr` varchar(15) DEFAULT NULL,
  `start` bigint(20) DEFAULT NULL,
  `end` bigint(20) DEFAULT NULL,
  `synonyms` varchar(45) DEFAULT NULL,
  `geneid` varchar(45) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `strand` varchar(1) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `gene_idx` (`symbol`,`name`,`biotype`,`chr`,`start`,`end`,`synonyms`,`geneid`,`description`,`strand`,`version`)
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gene`
--

LOCK TABLES `gene` WRITE;
/*!40000 ALTER TABLE `gene` DISABLE KEYS */;
INSERT INTO `gene` VALUES (21,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000053747',NULL,NULL,NULL),(22,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000053747',NULL,NULL,NULL),(23,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000053747',NULL,NULL,NULL),(24,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000053747',NULL,NULL,NULL),(25,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000053747',NULL,NULL,NULL),(91,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000073146',NULL,NULL,NULL),(92,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000073146',NULL,NULL,NULL),(93,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000073146',NULL,NULL,NULL),(94,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000073146',NULL,NULL,NULL),(95,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000073146',NULL,NULL,NULL),(96,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000073146',NULL,NULL,NULL),(86,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000073614',NULL,NULL,NULL),(87,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000073614',NULL,NULL,NULL),(88,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000073614',NULL,NULL,NULL),(89,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000073614',NULL,NULL,NULL),(90,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000073614',NULL,NULL,NULL),(7,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000111371',NULL,NULL,NULL),(8,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000111371',NULL,NULL,NULL),(9,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000111371',NULL,NULL,NULL),(10,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000111371',NULL,NULL,NULL),(11,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000111371',NULL,NULL,NULL),(12,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000111371',NULL,NULL,NULL),(13,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000111371',NULL,NULL,NULL),(14,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000111371',NULL,NULL,NULL),(26,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000115602',NULL,NULL,NULL),(27,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000115602',NULL,NULL,NULL),(28,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000115602',NULL,NULL,NULL),(29,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000115604',NULL,NULL,NULL),(30,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000115604',NULL,NULL,NULL),(31,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000115604',NULL,NULL,NULL),(75,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000136824',NULL,NULL,NULL),(76,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000136824',NULL,NULL,NULL),(77,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000136824',NULL,NULL,NULL),(78,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000136824',NULL,NULL,NULL),(79,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000136824',NULL,NULL,NULL),(80,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000136824',NULL,NULL,NULL),(71,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000144036',NULL,NULL,NULL),(72,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000144036',NULL,NULL,NULL),(73,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000144036',NULL,NULL,NULL),(74,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000144036',NULL,NULL,NULL),(36,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000149294',NULL,NULL,NULL),(37,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000149294',NULL,NULL,NULL),(38,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000149294',NULL,NULL,NULL),(39,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000149294',NULL,NULL,NULL),(40,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000149294',NULL,NULL,NULL),(41,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000149294',NULL,NULL,NULL),(42,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000149294',NULL,NULL,NULL),(43,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000149294',NULL,NULL,NULL),(44,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000149294',NULL,NULL,NULL),(45,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000149294',NULL,NULL,NULL),(46,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000149294',NULL,NULL,NULL),(47,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000149294',NULL,NULL,NULL),(48,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000149294',NULL,NULL,NULL),(49,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000149294',NULL,NULL,NULL),(50,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000149294',NULL,NULL,NULL),(51,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000149294',NULL,NULL,NULL),(52,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000149294',NULL,NULL,NULL),(53,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000150471',NULL,NULL,NULL),(54,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000150471',NULL,NULL,NULL),(55,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000150471',NULL,NULL,NULL),(56,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000150471',NULL,NULL,NULL),(57,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000150471',NULL,NULL,NULL),(58,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000150471',NULL,NULL,NULL),(59,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000150471',NULL,NULL,NULL),(60,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000150471',NULL,NULL,NULL),(61,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000150471',NULL,NULL,NULL),(62,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000150471',NULL,NULL,NULL),(63,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000150471',NULL,NULL,NULL),(64,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000150471',NULL,NULL,NULL),(65,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000150471',NULL,NULL,NULL),(66,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000150471',NULL,NULL,NULL),(67,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000150471',NULL,NULL,NULL),(68,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000150471',NULL,NULL,NULL),(69,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000150471',NULL,NULL,NULL),(20,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000155961',NULL,NULL,NULL),(70,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000155961',NULL,NULL,NULL),(15,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000165181',NULL,NULL,NULL),(16,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000165181',NULL,NULL,NULL),(17,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000165181',NULL,NULL,NULL),(18,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000165181',NULL,NULL,NULL),(19,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000165181',NULL,NULL,NULL),(32,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000166349',NULL,NULL,NULL),(33,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000166349',NULL,NULL,NULL),(35,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000166349',NULL,NULL,NULL),(81,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000170088',NULL,NULL,NULL),(82,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000170088',NULL,NULL,NULL),(83,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000170088',NULL,NULL,NULL),(84,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000170088',NULL,NULL,NULL),(85,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000170088',NULL,NULL,NULL),(34,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000175097',NULL,NULL,NULL),(1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000248905',NULL,NULL,NULL),(2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000248905',NULL,NULL,NULL),(3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000248905',NULL,NULL,NULL),(4,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000248905',NULL,NULL,NULL),(5,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000248905',NULL,NULL,NULL),(6,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ENSG00000248905',NULL,NULL,NULL);
/*!40000 ALTER TABLE `gene` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `genotype`
--

DROP TABLE IF EXISTS `genotype`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `genotype` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `genotype` varchar(45) DEFAULT NULL,
  `readdepth` int(11) DEFAULT NULL,
  `filter` varchar(45) DEFAULT NULL,
  `likelihoods` varchar(45) DEFAULT NULL,
  `genotypelikelihoods` varchar(45) DEFAULT NULL,
  `genotypelikelihoodshet` varchar(45) DEFAULT NULL,
  `posteriorprobs` varchar(45) DEFAULT NULL,
  `genotypequality` int(11) DEFAULT NULL,
  `haplotypequalities` varchar(45) DEFAULT NULL,
  `phaseset` varchar(45) DEFAULT NULL,
  `phasingquality` int(11) DEFAULT NULL,
  `alternateallelecounts` varchar(45) DEFAULT NULL,
  `mappingquality` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `genotype_idx` (`genotype`,`readdepth`,`filter`,`likelihoods`,`genotypelikelihoods`,`genotypelikelihoodshet`,`genotypequality`,`haplotypequalities`,`phaseset`,`phasingquality`,`alternateallelecounts`,`mappingquality`,`posteriorprobs`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `genotype`
--

LOCK TABLES `genotype` WRITE;
/*!40000 ALTER TABLE `genotype` DISABLE KEYS */;
INSERT INTO `genotype` VALUES (1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `genotype` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project` (
  `id` varchar(15) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `project_index` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project`
--

LOCK TABLES `project` WRITE;
/*!40000 ALTER TABLE `project` DISABLE KEYS */;
/*!40000 ALTER TABLE `project` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `referencegenome`
--

DROP TABLE IF EXISTS `referencegenome`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `referencegenome` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `source` varchar(45) DEFAULT NULL,
  `build` varchar(45) DEFAULT NULL,
  `version` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `rf_idx` (`source`,`build`,`version`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `referencegenome`
--

LOCK TABLES `referencegenome` WRITE;
/*!40000 ALTER TABLE `referencegenome` DISABLE KEYS */;
INSERT INTO `referencegenome` VALUES (1,'Ensembl','GRCh37','17');
/*!40000 ALTER TABLE `referencegenome` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sample`
--

DROP TABLE IF EXISTS `sample`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sample` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `identifier` varchar(15) DEFAULT NULL,
  `entity_id` varchar(15) DEFAULT NULL,
  `cancerentity` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `fk_idx` (`identifier`,`entity_id`,`cancerentity`),
  KEY `fk_sample_case_idx` (`entity_id`),
  CONSTRAINT `fk_Sample_Case1` FOREIGN KEY (`entity_id`) REFERENCES `entity` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sample`
--

LOCK TABLES `sample` WRITE;
/*!40000 ALTER TABLE `sample` DISABLE KEYS */;
INSERT INTO `sample` VALUES (1,'QTEST001AL','patient1','HCC'),(2,'QTEST002AT','patient2','HCC'),(3,'QTEST003A3','patient3','ALL');
/*!40000 ALTER TABLE `sample` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sample_has_variant`
--

DROP TABLE IF EXISTS `sample_has_variant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sample_has_variant` (
  `sample_id` int(11) NOT NULL,
  `variant_id` int(11) NOT NULL,
  `vcfinfo_id` int(11) NOT NULL,
  `genotype_id` int(11) NOT NULL,
  PRIMARY KEY (`sample_id`,`variant_id`,`vcfinfo_id`,`genotype_id`),
  UNIQUE KEY `idx_sample_has_variant` (`sample_id`,`variant_id`,`vcfinfo_id`,`genotype_id`),
  KEY `fk_sample_has_variant_variant_idx` (`variant_id`),
  KEY `fk_sample_has_variant_vcfinfo1_idx` (`vcfinfo_id`),
  KEY `fk_sample_has_variant_genotype1_idx` (`genotype_id`),
  KEY `fk_sample_has_variant_sample1_idx` (`sample_id`),
  CONSTRAINT `fk_Sample_has_Variant_Variant1` FOREIGN KEY (`variant_id`) REFERENCES `variant` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_sample_has_variant_genotype1` FOREIGN KEY (`genotype_id`) REFERENCES `genotype` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_sample_has_variant_sample1` FOREIGN KEY (`sample_id`) REFERENCES `sample` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_sample_has_variant_vcfinfo1` FOREIGN KEY (`vcfinfo_id`) REFERENCES `vcfinfo` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sample_has_variant`
--

LOCK TABLES `sample_has_variant` WRITE;
/*!40000 ALTER TABLE `sample_has_variant` DISABLE KEYS */;
INSERT INTO `sample_has_variant` VALUES (1,1,1,1),(1,2,2,1),(1,3,3,1),(1,4,4,1),(1,5,5,1),(2,4,4,1),(2,6,6,1),(2,7,7,1),(2,8,8,1),(2,9,9,1),(3,11,11,1),(3,12,12,1),(3,13,13,1),(3,14,14,1),(3,15,15,1);
/*!40000 ALTER TABLE `sample_has_variant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `variant`
--

DROP TABLE IF EXISTS `variant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `variant` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `chr` varchar(15) NOT NULL,
  `start` bigint(20) NOT NULL,
  `end` bigint(20) NOT NULL,
  `ref` tinytext NOT NULL,
  `obs` tinytext NOT NULL,
  `issomatic` tinyint(4) NOT NULL,
  `uuid` varchar(36) NOT NULL,
  `databaseidentifier` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `variant_idx` (`chr`,`start`,`end`,`ref`(255),`obs`(255),`issomatic`,`databaseidentifier`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `variant`
--

LOCK TABLES `variant` WRITE;
/*!40000 ALTER TABLE `variant` DISABLE KEYS */;
INSERT INTO `variant` VALUES (1,'15',33359344,33359344,'T','C',1,'e741f1f2-2c39-4d00-b311-5bf4cf896005','.'),(2,'12',46601390,46601390,'C','G',1,'d0b9a24d-e95a-44a2-8279-e895772f97cf','.'),(3,'9',114518682,114518682,'G','T',1,'aed9a27b-f9aa-4f8f-911f-81936aef306e','.'),(4,'X',154493388,154493388,'G','C',1,'c001f62d-5729-4dd8-9900-2f62d768a12d','.'),(5,'18',21269861,21269861,'G','C',1,'eb88a067-8c81-4c74-81e7-27d059c09add','.'),(6,'2',102968155,102968155,'T','C',1,'c68a421d-1028-405e-8ee0-17b6a250e1ad','.'),(7,'11',36596216,36596216,'G','A',1,'ccefd3cf-0737-4c73-bb1b-a3e45ab8d591','.'),(8,'11',113111509,113111509,'G','T',1,'ab938f60-1d0d-4124-a548-8a5b4575f1c8','.'),(9,'4',62849311,62849311,'A','G',1,'74632419-3371-4920-b9d6-638a7860fc64','.'),(11,'2',72411294,72411294,'C','A',1,'4ad08366-6b4c-4b98-8ea5-24a5637ca417','.'),(12,'9',106875723,106875723,'C','G',1,'9d9840fe-085d-4bc4-b9f1-a881662616d8','.'),(13,'4',166006829,166006829,'T','C',1,'7ddac11f-f383-462f-8c4a-4cefdbe79da5','.'),(14,'12',420115,420115,'C','T',1,'a44d09a9-198b-4849-a2ea-06cd004123a6','.'),(15,'22',50555597,50555597,'G','A',1,'ca32be21-bae0-46a2-9f7d-5a707256bc75','.');
/*!40000 ALTER TABLE `variant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `variant_has_consequence`
--

DROP TABLE IF EXISTS `variant_has_consequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `variant_has_consequence` (
  `variant_id` int(11) NOT NULL,
  `consequence_id` int(11) NOT NULL,
  PRIMARY KEY (`variant_id`,`consequence_id`),
  UNIQUE KEY `fk_idx` (`variant_id`,`consequence_id`),
  KEY `fk_variant_has_consequence_consequence_idx` (`consequence_id`),
  KEY `fk_variant_has_consequence_variant_idx` (`variant_id`),
  CONSTRAINT `fk_Variant_has_Consequence_Consequence1` FOREIGN KEY (`consequence_id`) REFERENCES `consequence` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Variant_has_Consequence_Variant1` FOREIGN KEY (`variant_id`) REFERENCES `variant` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `variant_has_consequence`
--

LOCK TABLES `variant_has_consequence` WRITE;
/*!40000 ALTER TABLE `variant_has_consequence` DISABLE KEYS */;
INSERT INTO `variant_has_consequence` VALUES (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(2,7),(2,8),(2,9),(2,10),(2,11),(2,12),(2,13),(2,14),(3,15),(3,16),(3,17),(3,18),(3,19),(4,20),(5,21),(5,22),(5,23),(5,24),(5,25),(6,26),(6,27),(6,28),(6,29),(6,30),(6,31),(7,32),(7,33),(7,34),(7,35),(8,36),(8,37),(8,38),(8,39),(8,40),(8,41),(8,42),(8,43),(8,44),(8,45),(8,46),(8,47),(8,48),(8,49),(8,50),(8,51),(8,52),(9,53),(9,54),(9,55),(9,56),(9,57),(9,58),(9,59),(9,60),(9,61),(9,62),(9,63),(9,64),(9,65),(9,66),(9,67),(9,68),(9,69),(11,71),(11,72),(11,73),(11,74),(12,75),(12,76),(12,77),(12,78),(12,79),(12,80),(13,81),(13,82),(13,83),(13,84),(13,85),(14,86),(14,87),(14,88),(14,89),(14,90),(15,91),(15,92),(15,93),(15,94),(15,95),(15,96);
/*!40000 ALTER TABLE `variant_has_consequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `variant_has_referencegenome`
--

DROP TABLE IF EXISTS `variant_has_referencegenome`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `variant_has_referencegenome` (
  `variant_id` int(11) NOT NULL,
  `referencegenome_id` int(11) NOT NULL,
  PRIMARY KEY (`variant_id`,`referencegenome_id`),
  UNIQUE KEY `fk_idx` (`variant_id`,`referencegenome_id`),
  KEY `fk_Variant_has_ReferenceGenome_ReferenceGenome1_idx` (`referencegenome_id`),
  KEY `fk_Variant_has_ReferenceGenome_Variant1_idx` (`variant_id`),
  CONSTRAINT `fk_Variant_has_ReferenceGenome_ReferenceGenome1` FOREIGN KEY (`referencegenome_id`) REFERENCES `referencegenome` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Variant_has_ReferenceGenome_Variant1` FOREIGN KEY (`variant_id`) REFERENCES `variant` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `variant_has_referencegenome`
--

LOCK TABLES `variant_has_referencegenome` WRITE;
/*!40000 ALTER TABLE `variant_has_referencegenome` DISABLE KEYS */;
INSERT INTO `variant_has_referencegenome` VALUES (1,1),(2,1),(3,1),(4,1),(5,1),(6,1),(7,1),(8,1),(9,1),(11,1),(12,1),(13,1),(14,1),(15,1);
/*!40000 ALTER TABLE `variant_has_referencegenome` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `variant_has_variantcaller`
--

DROP TABLE IF EXISTS `variant_has_variantcaller`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `variant_has_variantcaller` (
  `variant_id` int(11) NOT NULL,
  `variantcaller_id` int(11) NOT NULL,
  PRIMARY KEY (`variant_id`,`variantcaller_id`),
  UNIQUE KEY `fk_idx` (`variant_id`,`variantcaller_id`),
  KEY `fk_variant_has_variantcaller_variantcaller_idx` (`variantcaller_id`),
  KEY `fk_variant_has_variantcaller_variant_idx` (`variant_id`),
  CONSTRAINT `fk_Variant_has_VariantCaller_Variant1` FOREIGN KEY (`variant_id`) REFERENCES `variant` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Variant_has_VariantCaller_VariantCaller1` FOREIGN KEY (`variantcaller_id`) REFERENCES `variantcaller` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `variant_has_variantcaller`
--

LOCK TABLES `variant_has_variantcaller` WRITE;
/*!40000 ALTER TABLE `variant_has_variantcaller` DISABLE KEYS */;
INSERT INTO `variant_has_variantcaller` VALUES (1,1),(2,1),(3,1),(4,1),(5,1),(6,1),(7,1),(8,1),(9,1),(11,1),(12,1),(13,1),(14,1),(15,1);
/*!40000 ALTER TABLE `variant_has_variantcaller` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `variantcaller`
--

DROP TABLE IF EXISTS `variantcaller`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `variantcaller` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `version` varchar(15) NOT NULL,
  `doi` varchar(30) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `fk_idx` (`name`,`version`,`doi`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `variantcaller`
--

LOCK TABLES `variantcaller` WRITE;
/*!40000 ALTER TABLE `variantcaller` DISABLE KEYS */;
INSERT INTO `variantcaller` VALUES (1,'Strelka','2.0','10.1038/s41592-018-0051-x');
/*!40000 ALTER TABLE `variantcaller` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vcfinfo`
--

DROP TABLE IF EXISTS `vcfinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vcfinfo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ancestralallele` varchar(45) DEFAULT NULL,
  `allelecount` varchar(45) DEFAULT NULL,
  `allelefreq` varchar(45) DEFAULT NULL,
  `numberalleles` int(11) DEFAULT NULL,
  `basequality` int(11) DEFAULT NULL,
  `cigar` varchar(45) DEFAULT NULL,
  `dbsnp` tinyint(4) DEFAULT NULL,
  `hapmaptwo` tinyint(4) DEFAULT NULL,
  `hapmapthree` tinyint(4) DEFAULT NULL,
  `thousandgenomes` tinyint(4) DEFAULT NULL,
  `combineddepth` int(11) DEFAULT NULL,
  `endpos` int(11) DEFAULT NULL,
  `rms` int(11) DEFAULT NULL,
  `mqzero` int(11) DEFAULT NULL,
  `strandbias` int(11) DEFAULT NULL,
  `numbersamples` int(11) DEFAULT NULL,
  `somatic` tinyint(4) DEFAULT NULL,
  `validated` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `info_idx` (`ancestralallele`,`allelecount`,`allelefreq`,`numberalleles`,`basequality`,`cigar`,`dbsnp`,`hapmaptwo`,`hapmapthree`,`thousandgenomes`,`combineddepth`,`endpos`,`rms`,`mqzero`,`strandbias`,`numbersamples`,`somatic`,`validated`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vcfinfo`
--

LOCK TABLES `vcfinfo` WRITE;
/*!40000 ALTER TABLE `vcfinfo` DISABLE KEYS */;
INSERT INTO `vcfinfo` VALUES (2,'p.E135K','[]','[]',-1,-1,'',0,0,0,0,-1,-1,-1,-1,-1,-1,0,0),(5,'p.G72S','[]','[]',-1,-1,'',0,0,0,0,-1,-1,-1,-1,-1,-1,0,0),(4,'p.I62I','[]','[]',-1,-1,'',0,0,0,0,-1,-1,-1,-1,-1,-1,0,0),(7,'p.L454L','[]','[]',-1,-1,'',0,0,0,0,-1,-1,-1,-1,-1,-1,0,0),(12,'p.L461V','[]','[]',-1,-1,'',0,0,0,0,-1,-1,-1,-1,-1,-1,0,0),(6,'p.M482T','[]','[]',-1,-1,'',0,0,0,0,-1,-1,-1,-1,-1,-1,0,0),(1,'p.N248H','[]','[]',-1,-1,'',0,0,0,0,-1,-1,-1,-1,-1,-1,0,0),(14,'p.R1051Q','[]','[]',-1,-1,'',0,0,0,0,-1,-1,-1,-1,-1,-1,0,0),(13,'p.R192G','[]','[]',-1,-1,'',0,0,0,0,-1,-1,-1,-1,-1,-1,0,0),(15,'p.R424H','[]','[]',-1,-1,'',0,0,0,0,-1,-1,-1,-1,-1,-1,0,0),(3,'p.S262*','[]','[]',-1,-1,'',0,0,0,0,-1,-1,-1,-1,-1,-1,0,0),(9,'p.T1008A','[]','[]',-1,-1,'',0,0,0,0,-1,-1,-1,-1,-1,-1,0,0),(8,'p.T613T','[]','[]',-1,-1,'',0,0,0,0,-1,-1,-1,-1,-1,-1,0,0),(11,'p.W277L','[]','[]',-1,-1,'',0,0,0,0,-1,-1,-1,-1,-1,-1,0,0);
/*!40000 ALTER TABLE `vcfinfo` ENABLE KEYS */;
UNLOCK TABLES;
SET FOREIGN_KEY_CHECKS=1;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-07-02 11:30:06
