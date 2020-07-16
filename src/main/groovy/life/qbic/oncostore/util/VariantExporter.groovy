package life.qbic.oncostore.util

import life.qbic.oncostore.model.Variant
import life.qbic.oncostore.parser.MetadataContext

class VariantExporter {

    private static Map<String, String> vcfHeaders = [:]

    /**
     * headers for different Variant Call Format versions
     */
    static {
        vcfHeaders['4.1'] = "##fileformat=VCFv4.1 \n##fileDate=%s\n##source=%s\n##reference=%s\nCHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\n"
        vcfHeaders["4.2"] = "##fileformat=VCFv4.2 \n##fileDate=%s\n##source=%s\n##reference=%s\n#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\n"
    }

    /**
     * Generates VCF content from list of variants
     * @param variants the variants to export in VCF
     * @return a VCF content
     */
    static String exportVariantsToVCF(List<Variant> variants, Boolean withConsequences, MetadataContext metadata) {
        def vcfContent = new StringBuilder()
        def date = new Date().format('yyyyMMdd')

        // allow to choose VCF version
        def vcfHeader = String.format(vcfHeaders["4.1"], date, 'variantstore', metadata.referenceGenome.toString())
        vcfContent.append(vcfHeader)

        //determine if SnpEff or VEP
        variants.each { var ->
            vcfContent.append(var.toVcfFormat())
            if (withConsequences) {
                vcfContent.append(";")
                if (metadata.variantAnnotation.name == "snpeff") {
                    vcfContent.append("${AnnotationHandler.AnnotationTools.SNPEFF.tag}=")
                    vcfContent.append(var.consequences.collect { AnnotationHandler.toSnpEff(it) }.join(","))
                }
                else {
                    vcfContent.append("${AnnotationHandler.AnnotationTools.VEP.tag}=")
                    vcfContent.append(var.consequences.collect { AnnotationHandler.toVep(it) }.join(","))
                }
            }
            vcfContent.append("\n")
        }
        return vcfContent
    }
}
