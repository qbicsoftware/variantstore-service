package life.qbic.oncostore.util

import life.qbic.oncostore.model.Variant

class VariantExporter {

    //TODO keep different version?
    private static Map<String, String> vcfHeaders = [:]

    /**
     * headers for different Variant Call Format versions
     */
    static {
        vcfHeaders['4.1'] = "##fileformat=VCFv4.1 \n##fileDate=%s\n##source=%s\n##reference=\nCHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\n"
        vcfHeaders["4.2"] = "##fileformat=VCFv4.2 \n##fileDate=%s\n##source=%s\n##reference=\n#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\n"
    }

    /**
     * Generates VCF content from list of variants
     * @param variants the variants to export in VCF
     * @return a VCF content
     */
    static String exportVariantsToVCF(List<Variant> variants) {
        def vcfContent = new StringBuilder()
        def date = new Date().format('yyyyMMdd')
        def vcfHeader = String.format(vcfHeaders["4.1"], date, 'Variantstore')
        vcfContent.append(vcfHeader)

        variants.each { var ->
            vcfContent.append(var.toVcfFormat())
            vcfContent.append(var.consequences.collect{AnnotationHandler.toSnpEff(it)}.join(","))
            vcfContent.append("\n")
        }
        return vcfContent
    }
}
