package life.qbic.oncostore.util

import life.qbic.oncostore.model.Annotation
import life.qbic.oncostore.model.Consequence
import life.qbic.oncostore.model.SimpleVariantContext


/**
 * Static class to provide mapping of consequence annotation attributes
 * to index for different version of Ensembl Variant Effect Predictor and SnpEff.
 */

public class AnnotationHandler {

    public static Map<String, Map<String, Object>> vep = [:]
    public static Map<String, Map<String, Object>> snpEff = [:]
    public static Map<String, String> snpEffOutput = [:]

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
        def outputString = "%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s"
        snpEffOutput.put("4.3t", outputString)
        snpEffOutput.put("bioconda::4.3.1t", outputString)
    }

    /**
     *
     * @param simpleVariant
     * @param annotationSoftware
     * @return
     */
    static SimpleVariantContext addAnnotationsToVariant(SimpleVariantContext simpleVariant, Annotation annotationSoftware) {
        def variantConsequences = []
        def annotationTool = annotationSoftware.getName().toUpperCase()  as AnnotationTools
        def consequences = simpleVariant.getAttribute(annotationTool.getTag())
        consequences.each { annotation ->
            def cons = new Consequence()
            def consequence = populateConsequence(cons, annotation as String, annotationSoftware)
            variantConsequences.add(consequence)
        }

        simpleVariant.consequences = variantConsequences
        return simpleVariant
    }

    /**
     *
     * @param cons
     * @param annotation
     * @param annotationSoftware
     * @return
     */
    private static Consequence populateConsequence(Consequence cons, String annotation, Annotation annotationSoftware) {
        def parsedAnnotation = annotation.split('\\|', -1)
        def version = annotationSoftware.getVersion()
        def softwareName = annotationSoftware.getName().toUpperCase() as AnnotationTools

        switch (softwareName)  {
            case AnnotationTools.VEP:
                cons.allele = parsedAnnotation[vep[version].get("allele") as Integer]
                cons.codingChange = parsedAnnotation[vep[version].get("cdsCoding") as Integer]
                cons.transcriptId = parsedAnnotation[vep[version].get("transcriptId")as Integer]
                def (transcriptVersion, aaChange) = parsedAnnotation[vep[version].get("proteinCoding")as Integer].tokenize(':')
                cons.transcriptVersion = transcriptVersion ? transcriptVersion.split(".")[-1] : -1
                cons.aaChange = aaChange ?: ''
                cons.type = parsedAnnotation[vep[version].get("consequence")as Integer]
                cons.bioType = parsedAnnotation[vep[version].get("bioType")as Integer]
                def canonical = parsedAnnotation[vep[version].get("canonical") as Integer]
                cons.canonical = canonical && canonical == "YES"
                cons.featureType = parsedAnnotation[vep[version].get("featureType")as Integer]
                cons.exon = parsedAnnotation[vep[version].get("exon")as Integer]
                cons.intron = parsedAnnotation[vep[version].get("intron")as Integer]
                /* for insertions and deletions we expect a range (start-end) for the protein position  */
                def (protPosition, protLength) = parsedAnnotation[vep[version].get("protPos") as Integer].tokenize('/')
                cons.proteinPosition = protPosition ?: ''
                cons.impact = parsedAnnotation[vep[version].get("impact") as Integer]
                cons.geneId = parsedAnnotation[vep[version].get("gene") as Integer]
                cons.geneSymbol = parsedAnnotation[vep[version].get("symbol") as Integer]
                def (cdna, cdnaLength) = parsedAnnotation[vep[version].get("cdnaPos") as Integer].tokenize('/')
                def (cds, cdsLength) = parsedAnnotation[vep[version].get("cdsPos")  as Integer].tokenize('/')
                cons.cdsPosition = cds ?: ''
                cons.cdnaPosition = cdna ?: ''
                cons.cdsLength = cdsLength && !cdsLength.isEmpty() ? cdsLength.toInteger() : -1
                cons.cdnaLength = cdnaLength && !cdnaLength.isEmpty() ? cdnaLength.toInteger() : -1
                cons.proteinLength = protLength && !protLength.isEmpty() ? protLength.toInteger() : -1
                def distance = parsedAnnotation[vep[version].get("distance") as Integer]
                cons.distance = distance && !distance.isEmpty() ? distance.toInteger() : -1
                def strand = parsedAnnotation[vep[version].get("strand") as Integer]
                cons.strand = strand ? strand.toInteger() : 0
                cons.warnings = ''
                break

            case AnnotationTools.SNPEFF:
                cons.allele = parsedAnnotation[snpEff[version].get("allele") as Integer]
                cons.codingChange = parsedAnnotation[snpEff[version].get("cdsCoding")  as Integer]
                cons.transcriptId = parsedAnnotation[snpEff[version].get("transcriptId") as Integer]
                cons.featureType = parsedAnnotation[snpEff[version].get("featureType") as Integer]
                cons.exon = parsedAnnotation[snpEff[version].get("rank") as Integer]
                // There is only rank in SNPEFF annotations in contrast to VEP, so we will use exon to store rank information
                cons.intron = ''
                def (cdna, cdnaLength) = parsedAnnotation[snpEff[version].get("cdna") as Integer].tokenize('/')
                def (cds, cdsLength) = parsedAnnotation[snpEff[version].get("cds")  as Integer].tokenize('/')
                def (protPosition, protLength) = parsedAnnotation[snpEff[version].get("protein") as Integer].tokenize('/')
                def featureVersion  = cons.transcriptId.split('.')
                cons.transcriptVersion = !featureVersion.toList().isEmpty() ? featureVersion[-1].toInteger(): -1
                cons.proteinPosition = protPosition ?: ""
                cons.proteinLength = protLength && !protLength.isEmpty() ? protLength.toInteger() : -1
                cons.cdsPosition = cds ?: ""
                cons.cdsLength = cdsLength && !cdsLength.isEmpty() ? cdsLength.toInteger() : -1
                cons.cdnaPosition = cdna ?: ""
                cons.cdnaLength = cdnaLength && !cdnaLength.isEmpty() ? cdnaLength.toInteger() : -1
                cons.type = parsedAnnotation[snpEff[version].get("consequence") as Integer]
                cons.bioType = parsedAnnotation[snpEff[version].get("bioType") as Integer]
                cons.canonical = false //how to determine?
                cons.strand = 0 //how to determine?
                cons.aaChange = parsedAnnotation[snpEff[version].get("proteinCoding") as Integer]
                cons.impact = parsedAnnotation[snpEff[version].get("impact") as Integer]
                def geneId = (parsedAnnotation[snpEff[version].get("gene") as Integer] != '') ? parsedAnnotation[snpEff[version].get("gene") as Integer] : ''
                cons.geneId = geneId
                cons.geneSymbol = parsedAnnotation[snpEff[version].get("symbol") as Integer]
                def distance = parsedAnnotation[snpEff[version].get("distance") as Integer]
                cons.distance = distance && !distance.isEmpty() ? distance.toInteger() : -1
                cons.warnings = parsedAnnotation[snpEff[version].get("warnings") as Integer]
                break

            default:
                throw new IllegalArgumentException("Unknown annotation software: $annotationSoftware.name");
        }

        return cons
    }

    /**
     * Generate snpEff output for consequence
     * @param cons the provided consequence
     * @return a snpEff annotation
     */
    static String toSnpEff(Consequence cons) {
        //@TODO determine version of annotation software?

        return sprintf(snpEffOutput["4.3t"], cons.allele, cons.type, cons.impact, cons.geneSymbol, cons.geneId, cons.featureType, cons.transcriptId, cons.bioType, cons.exon, cons.codingChange, cons.aaChange, "/".join(cons.cdnaPosition.toString(), cons.cdnaLength.toString()), "/".join(cons.cdsPosition.toString(), cons.cdsPosition.toString()), "/".join(cons.proteinPosition.toString(), cons.proteinLength.toString()), cons.distance.toString(), cons.warnings)
    }

    //@TODO implement
    /**
     * Generates VEP output for consequence
     * @param cons the provided consequence
     * @return a VEP annotation
     */
    static String toVep(Consequence cons) {

        return ""
    }
}
