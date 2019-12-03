package life.qbic.oncostore.util

import life.qbic.oncostore.model.Variant

class VariantExporter {

    //TODO keep different version?
    private static Map<String, GString> vcfHeaders = [:]

    static {
        vcfHeaders["4.1"] = """##fileformat=VCFv4.1
                                ##fileDate=
                                ##source=
                                ##reference=
                                """
        vcfHeaders["4.2"] = """"""
    }

    static exportVariantsToVCF(List<Variant> variants) {
        def vcfContent = """"""
        vcfContent.concat(vcfHeaders["4.1"].re)
        variants.each { var ->
            vcfContent.concat(var.toVcf())
            vcfContent.concat(",".join(var.consequences.collect(AnnotationHandler.toSnpEff(it))))
        }
    }
}
