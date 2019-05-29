package life.qbic.oncostore.util

import life.qbic.oncostore.model.Annotation
import life.qbic.oncostore.model.Consequence
import life.qbic.oncostore.model.Gene
import life.qbic.oncostore.model.SimpleVariantContext

/**
 * Static class to provide mapping of consequence annotation attributes
 * to index for different version of Ensembl Variant Effect Predictor and SnpEff.
 */
public class AnnotationHandler {

    public static Map<String, Map> vep = [:]
    public static Map<String, Map> snpEff = [:]

    static {
        // VEP version 95
        Map<String, Integer> vep1 = [:]
        vep1.put("allele", 0)
        vep1.put("consequence", 1)
        vep1.put("impact", 2)
        vep1.put("symbol", 3) //gene symbol
        vep1.put("gene", 4) // gene ID
        vep1.put("feature_type", 5)
        vep1.put("transcriptID", 6) // feature
        vep1.put("bioType", 7)
        vep1.put("exon", 8)
        vep1.put("intron", 9)
        vep1.put("cdsCoding", 10)
        vep1.put("cDNApos", 11)
        vep1.put("cdsPos", 12)
        vep1.put("protPos", 13)
        vep1.put("strand", 18)
        vep1.put("canonical", 23)
        vep.put("95", vep1)

        // snpEff annotation version 1.0
        Map<String, Integer> snpeff1 = [:]
        snpeff1.put("allele", 0)
        snpeff1.put("consequence", 1)
        snpeff1.put("impact", 2)
        snpeff1.put("symbol", 3) //gene symbol
        snpeff1.put("gene", 4) // gene ID
        snpeff1.put("feature_type", 5)
        snpeff1.put("transcriptID", 6) // feature
        snpeff1.put("bioType", 7)
        snpeff1.put("exon", 8)
        snpeff1.put("cdsCoding", 9)
        snpeff1.put("protCoding", 10)
        snpeff1.put("protPos", 13)
        snpEff.put("4.3t", snpeff1)
    }

    static SimpleVariantContext addAnnotationsToVariant(SimpleVariantContext simpleVariant, Annotation annotationSofware) {
        def variantConsequences = []
        def key = (annotationSofware.getName() == "vep") ? "CSQ": "ANN"
        def consequences = simpleVariant.getAttribute(key).split(',', -1)

        consequences.each { annotation ->
            def cons = new Consequence()
            def consequence = populateConsequence(cons, annotation, annotationSofware)
            variantConsequences.add(consequence)
        }

        simpleVariant.consequences = variantConsequences
        return simpleVariant
    }

    private static Consequence populateConsequence(Consequence cons, String annotation, Annotation annotationSofware) {
        def parsedAnnotation = annotation.split('\\|', -1)
        def version = annotationSofware.getVersion()

        switch (annotationSofware.getName())  {
            case "vep":
                cons.setCodingChange(parsedAnnotation[vep[version].get("cdsCoding")])
                def (transcriptVersion, aaChange) = parsedAnnotation[vep[version].get("protCoding")].tokenize(':')
                cons.setTranscriptID(parsedAnnotation[vep[version].get("transcriptID")])
                cons.setTranscriptVersion(transcriptVersion)
                cons.setConsequenceType(parsedAnnotation[vep[version].get("consequence")])
                cons.setBioType(parsedAnnotation[vep[version].get("bioType")])
                cons.setCanonical(parsedAnnotation[vep[version].get("canonical")])
                cons.setAaChange(aaChange)
                /* for insertions and deletions we expect a range (start-end) for the protein position  */
                def protPositions = vep[version].get("protPos").split('-')
                if(protPositions.size() > 1) {
                    cons.setAaStart(protPositions.get(0).toInteger())
                    cons.setAaEnd(protPositions.get(1).toInteger())
                }
                else {
                    def protPos = (vep[version].get("protPos") != null) ? vep[version].get("protPos").toInteger() : vep[version].get("protPos")
                    cons.setAaStart(protPos)
                    cons.setAaEnd(protPos)
                }
                cons.setImpact(parsedAnnotation[vep[version].get("impact")])
                //cons.setGene(new Gene(parsedAnnotation[vep[version].get("gene")]))
                cons.setGeneID(parsedAnnotation[vep[version].get("gene")])
                cons.setStrand(parsedAnnotation[vep[version].get("strand")])
                break

            case "snpeff":
                cons.setCodingChange(parsedAnnotation[snpEff[version].get("cdsCoding")])
                cons.setTranscriptID(parsedAnnotation[snpEff[version].get("transcriptID")])
                //@TODO get transcript version
                cons.setTranscriptVersion(0)
                def (protStart, protLength) = parsedAnnotation[snpEff[version].get("protPos")].tokenize('/')
                def protPos = (protStart != null) ? protStart.toInteger() : protStart
                cons.setAaStart(protPos)
                cons.setAaEnd(protPos)
                cons.setConsequenceType(parsedAnnotation[snpEff[version].get("consequence")])
                cons.setBioType(parsedAnnotation[snpEff[version].get("bioType")])
                //cons.setCanonical(false) //how to determine?
                cons.setAaChange(parsedAnnotation[snpEff[version].get("protCoding")])
                cons.setImpact(parsedAnnotation[snpEff[version].get("impact")])
                cons.setGeneID(parsedAnnotation[snpEff[version].get("gene")])
                break

            default:
                throw new IllegalArgumentException("Unknown annotation software: " + annotationSofware.getName());
        }

        return cons
    }
}
