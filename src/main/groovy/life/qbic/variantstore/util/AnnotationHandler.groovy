package life.qbic.variantstore.util

import groovy.util.logging.Log4j2
import life.qbic.variantstore.model.Annotation
import life.qbic.variantstore.model.Consequence
import life.qbic.variantstore.model.Gene
import life.qbic.variantstore.model.SimpleVariantContext

/**
 * Static class to provide mapping of consequence annotation attributes to index
 * for different version of Ensembl Variant Effect Predictor (VEP) and SnpEff.
 *
 * Futher used to parse given annotation string and to populate {@Consequence} objects.
 *
 * @since: 1.0.0
 */
@Log4j2
class AnnotationHandler {

    /**
     * Maps holding mapping of properties to position in string.
     */
    public static Map<String, Map<String, Object>> vep = [:]
    public static Map<String, Map<String, Object>> snpEff = [:]
    /**
     * Map to store output format for different SnpEff and VEP versions
     */
    public static Map<String, String> snpEffOutput = [:]
    public static Map<String, String> vepOutput = [:]

    /**
     * Available annotation tool tags as found in annotation variants in VCF files.
     */
    enum AnnotationTools{
        VEP("CSQ"), SNPEFF("ANN")

        AnnotationTools(String tag) {
            this.tag = tag
        }
        private final String tag

        String getTag() {
            tag
        }
    }

    static {
        // VEP version 95
        //@TODO additional fields needed?
        Map<String, Integer> vep1 = [:]
        vep1.put("allele", 0)
        vep1.put("consequence", 1)
        vep1.put("impact", 2)
        vep1.put("symbol", 3) //gene symbol
        vep1.put("gene", 4) // gene ID
        vep1.put("featureType", 5)
        vep1.put("transcriptId", 6) // feature
        vep1.put("bioType", 7)
        vep1.put("exon", 8)
        vep1.put("intron", 9)
        vep1.put("cdsCoding", 10)
        vep1.put("proteinCoding", 11)
        vep1.put("cdnaPos", 12)
        vep1.put("cdsPos", 13)
        vep1.put("protPos", 14)
        vep1.put("distance", 18)
        vep1.put("strand", 19)
        vep1.put("canonical", 24)
        vep.put("95", vep1)
        def outputStringVep = "%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s"
        vepOutput.put("95", outputStringVep)

        // snpEff annotation version 1.0
        Map<String, Integer> snpeff1 = [:]
        snpeff1.put("allele", 0)
        snpeff1.put("consequence", 1)
        snpeff1.put("impact", 2)
        snpeff1.put("symbol", 3) //gene symbol
        snpeff1.put("gene", 4) // gene ID
        snpeff1.put("featureType", 5)
        snpeff1.put("transcriptId", 6) // feature
        snpeff1.put("bioType", 7)
        snpeff1.put("rank", 8) // Rank / total: Exon or Intron rank / total number of exons or introns. (Stored as exon for SnpEff)
        snpeff1.put("cdsCoding", 9)
        snpeff1.put("proteinCoding", 10)
        snpeff1.put("cdna", 11)
        snpeff1.put("cds", 12)
        snpeff1.put("protein", 13)
        snpeff1.put("distance", 14)
        snpeff1.put("warnings", 15)
        snpEff.put("4.3t", snpeff1)
        snpEff.put("bioconda::4.3.1t", snpeff1)
        def outputStringSNPeff = "%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s"
        snpEffOutput.put("4.3t", outputStringSNPeff)
        snpEffOutput.put("bioconda::4.3.1t", outputStringSNPeff)
    }

    /**
     * Add annotations as consequence objects to variant.
     * @param simpleVariant a variant and its annotations
     * @param annotationSoftware
     * @return a variant with assigned consequences
     */
    static SimpleVariantContext addAnnotationsToVariant(SimpleVariantContext simpleVariant, Annotation annotationSoftware) {
        def variantConsequences = []

        simpleVariant.consequences.each { annotation ->
            def annotationString = annotation.toString()
            def consequence = populateConsequence(annotationString, annotationSoftware)
            if (consequence) { variantConsequences.add(consequence) }
        }

        simpleVariant.consequences = variantConsequences
        return simpleVariant
    }

    /**
     * Populate Consequence object with values parsed from provided annotation String.
     * @param cons a Consequence object
     * @param annotation a String containing variant annotations
     * @param annotationSoftware a annotation software used to generate the annotations
     * @return a populated Consequence object
     */
    private static Consequence populateConsequence(String annotation, Annotation annotationSoftware) {
        def parsedAnnotation = annotation.split('\\|', -1)
        def version = annotationSoftware.getVersion()
        def softwareName = annotationSoftware.getName().toUpperCase() as AnnotationTools
        def cons = null
        Set<Gene> genes = []

        switch (softwareName) {
            case AnnotationTools.VEP:
                def allele = parsedAnnotation[vep[version].get("allele") as Integer]
                def codingChange = parsedAnnotation[vep[version].get("cdsCoding") as Integer]
                def transcriptId = parsedAnnotation[vep[version].get("transcriptId") as Integer]
                def (transcriptVersionParsed, aaChangeParsed) = parsedAnnotation[vep[version].get("proteinCoding") as Integer]
                        .tokenize(':')
                def transcriptVersion = transcriptVersionParsed ? transcriptVersionParsed.tokenize(".").last() as Integer : -1
                def aaChange = aaChangeParsed ?: ''
                def type = parsedAnnotation[vep[version].get("consequence") as Integer]
                def bioType = parsedAnnotation[vep[version].get("bioType") as Integer]
                def canonicalParsed = parsedAnnotation[vep[version].get("canonical") as Integer]
                def canonical = canonicalParsed && canonicalParsed == "YES"
                def featureType = parsedAnnotation[vep[version].get("featureType") as Integer]
                def exon = parsedAnnotation[vep[version].get("exon") as Integer]
                def intron = parsedAnnotation[vep[version].get("intron") as Integer]
                /* for insertions and deletions we expect a range (start-end) for the protein position  */
                def (protPosition, protLength) = parsedAnnotation[vep[version].get("protPos") as Integer].tokenize('/')
                def proteinPosition = protPosition ?: ''
                def impact = parsedAnnotation[vep[version].get("impact") as Integer]
                def geneId = parsedAnnotation[vep[version].get("gene") as Integer]
                def geneSymbol = parsedAnnotation[vep[version].get("symbol") as Integer]
                def gene = new Gene()
                gene.setGeneId(geneId)
                gene.setSymbol(geneSymbol)
                def (cdna, cdnaLengthParsed) = parsedAnnotation[vep[version].get("cdnaPos") as Integer].tokenize('/')
                def (cds, cdsLengthParsed) = parsedAnnotation[vep[version].get("cdsPos") as Integer].tokenize('/')
                def cdsPosition = cds ?: ''
                def cdnaPosition = cdna ?: ''
                def cdsLength = cdsLengthParsed && !cdsLengthParsed.isEmpty() ? cdsLengthParsed.toInteger() : -1
                def cdnaLength = cdnaLengthParsed && !cdnaLengthParsed.isEmpty() ? cdnaLengthParsed.toInteger() : -1
                def proteinLength = protLength && !protLength.isEmpty() ? protLength.toInteger() : -1
                def distanceParsed = parsedAnnotation[vep[version].get("distance") as Integer]
                def distance = distanceParsed && !distanceParsed.isEmpty() ? distanceParsed.toInteger() : -1
                def strandParsed = parsedAnnotation[vep[version].get("strand") as Integer]
                def strand = strandParsed ? strandParsed.toInteger() : 0
                def warnings = ''
                gene.setGeneId(geneId)
                gene.setSymbol(geneSymbol)
                genes.add(gene)

                cons =  new Consequence(allele, codingChange, transcriptId, transcriptVersion, type,
                        bioType, canonical, aaChange, cdnaPosition, cdsPosition, proteinPosition, proteinLength,
                        cdnaLength, cdsLength, impact, exon, intron, strand, geneSymbol, geneId, featureType,
                        distance, warnings)
                cons.setGenes(genes)
                break

            case AnnotationTools.SNPEFF:
                def geneId = (parsedAnnotation[snpEff[version].get("gene") as Integer].intern() != '') ?
                        parsedAnnotation[snpEff[version].get("gene") as Integer].intern() : ''
                if ((geneId == null) || (geneId == "")) {
                    //@TODO check if OK in every case
                    //log.info("Skipping annotaton with transcript id ${cons.transcriptId} due to missing gene
                    // identifier.")
                    return null
                }
                def allele = parsedAnnotation[snpEff[version].get("allele") as Integer].intern()
                def codingChange = parsedAnnotation[snpEff[version].get("cdsCoding") as Integer].intern()
                def transId = parsedAnnotation[snpEff[version].get("transcriptId") as Integer].tokenize('.') ?: []

                def transcriptId = transId[0]
                def featureType = parsedAnnotation[snpEff[version].get("featureType") as Integer].intern()
                def exon = parsedAnnotation[snpEff[version].get("rank") as Integer].intern()
                // There is only rank in SNPEFF annotations in contrast to VEP, so we will use exon to store rank
                // information
                def intron = ''
                def (cdna, cdnaLengthParsed) = parsedAnnotation[snpEff[version].get("cdna") as Integer].intern().tokenize('/')
                def (cds, cdsLengthParsed) = parsedAnnotation[snpEff[version].get("cds") as Integer].intern().tokenize('/')
                def (protPosition, protLength) = parsedAnnotation[snpEff[version].get("protein") as Integer].intern()
                        .tokenize('/')
                def transcriptVersion = transId.size() > 1 ? transId[-1].toInteger() : -1
                def proteinPosition = protPosition ?: ""
                def proteinLength = protLength && !protLength.isEmpty() ? protLength.toInteger() : -1
                def cdsPosition = cds ?: ""
                def cdsLength = cdsLengthParsed && !cdsLengthParsed.isEmpty() ? cdsLengthParsed.toInteger() : -1
                def cdnaPosition = cdna ?: ""
                def cdnaLength = cdnaLengthParsed && !cdnaLengthParsed.isEmpty() ? cdnaLengthParsed.toInteger() : -1
                def type = parsedAnnotation[snpEff[version].get("consequence") as Integer].intern()
                def bioType = parsedAnnotation[snpEff[version].get("bioType") as Integer].intern()
                def canonical = false //how to determine?
                def strand = 0 //how to determine?
                def aaChange = parsedAnnotation[snpEff[version].get("proteinCoding") as Integer].intern()
                def impact = parsedAnnotation[snpEff[version].get("impact") as Integer].intern()
                def geneSymbol = parsedAnnotation[snpEff[version].get("symbol") as Integer].intern()
                def distanceParsed = parsedAnnotation[snpEff[version].get("distance") as Integer].intern()
                def distance = distanceParsed && !distanceParsed.isEmpty() ? distanceParsed.toInteger() : -1
                def warnings = parsedAnnotation[snpEff[version].get("warnings") as Integer].intern()
                def gene = new Gene()
                // @TODO handle intergenic cases here, i.e. create two genes objects probably
                gene.setGeneId(geneId)
                gene.setSymbol(geneSymbol)
                genes.add(gene)

                cons =  new Consequence(allele, codingChange, transcriptId, transcriptVersion, type,
                        bioType, canonical, aaChange, cdnaPosition, cdsPosition, proteinPosition, proteinLength,
                        cdnaLength, cdsLength, impact, exon, intron, strand, geneSymbol, geneId, featureType,
                        distance, warnings)
                cons.setGenes(genes)
                break

            default:
                throw new IllegalArgumentException("Unknown annotation software: $annotationSoftware.name");
        }

        return cons
    }

    /**
     * Generates SnpEff output for a Consequence object
     * @param cons the provided consequence
     * @param annotationSoftwareVersion the annotation software version used
     * @return a snpEff annotation
     */
    static String toSnpEff(Consequence cons, String annotationSoftwareVersion) {
        return sprintf(snpEffOutput[annotationSoftwareVersion], cons.allele, cons.type, cons.impact, cons.geneSymbol,
                cons.geneId, cons.featureType, cons.transcriptId, cons.bioType, cons.exon, cons.codingChange,
                cons.aaChange, "/".join (cons.cdnaPosition.toString(), cons.cdnaLength.toString()), "/".join(
                cons.cdsPosition.toString(), cons.cdsPosition.toString()), "/".join(cons.proteinPosition.toString(),
                cons.proteinLength.toString()
        ), cons.distance.toString(), cons.warnings)
    }

    /**
     * Generates Variant Effect Predictor output for a Consequence object
     * @param cons the provided consequence
     * @param annotationSoftwareVersion the annotation software version used
     * @return a VEP annotation
     */
    static String toVep(Consequence cons, String annotationSoftwareVersion) {
        // based on default output format: Allele|Consequence|IMPACT|SYMBOL|Gene|Feature_type|Feature|BIOTYPE
        // |EXON|INTRON|HGVSc|HGVSp|cDNA_position|CDS_position|Protein_position|Amino_acids|Codons|Existing_variation
        // |DISTANCE|STRAND|FLAGS|SYMBOL_SOURCE|HGNC_ID
        return sprintf(vepOutput[annotationSoftwareVersion], cons.allele, cons.type, cons.impact, cons.geneSymbol, cons.geneId, cons
                .featureType, cons.bioType, cons.exon, cons.intron, cons.codingChange, cons.aaChange,
                "/".join(cons.cdnaPosition.toString(), cons.cdnaLength.toString()), "/".join(cons.cdsPosition.toString(),
                cons.cdsPosition.toString()), "/".join(cons.proteinPosition.toString(), cons.proteinLength.toString()
        ), cons.distance.toString(), cons.strand, cons.warnings, "", "")
    }
}
