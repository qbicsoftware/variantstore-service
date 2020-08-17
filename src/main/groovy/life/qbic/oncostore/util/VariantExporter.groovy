package life.qbic.oncostore.util

import ca.uhn.fhir.context.FhirContext
import life.qbic.oncostore.model.Variant
import org.hl7.fhir.r4.model.*
import org.hl7.fhir.r4.model.codesystems.ObservationCategory

import java.time.Instant

class VariantExporter {

    private static Map<String, String> vcfHeaders = [:]

    /**
     * headers for different Variant Call Format versions*/
    static {
        vcfHeaders['4.1'] = "##fileformat=VCFv4.1 " +
                "\n##fileDate=%s\n##source=%s\n##reference=%s\nCHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\n"
        vcfHeaders["4.2"] = "##fileformat=VCFv4.2 " +
                "\n##fileDate=%s\n##source=%s\n##reference=%s\n#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\n"
    }

    /**
     * Generates VCF content from list of variants
     * @param variants the variants to export in VCF
     * @return a VCF content
     */
    static String exportVariantsToVCF(List<Variant> variants, Boolean withConsequences, String referenceGenome,
                                      String annotationSoftware) {
        def vcfContent = new StringBuilder()
        def date = new Date().format('yyyyMMdd')

        // allow to choose VCF version
        def vcfHeader = String.format(vcfHeaders["4.1"], date, 'variantstore', referenceGenome)
        vcfContent.append(vcfHeader)

        //determine if SnpEff or VEP
        variants.each { var ->
            vcfContent.append(var.toVcfFormat())
            if (withConsequences) {
                vcfContent.append(";")
                if (annotationSoftware.toLowerCase() == "snpeff") {
                    vcfContent.append("${AnnotationHandler.AnnotationTools.SNPEFF.tag}=")
                    vcfContent.append(var.consequences.collect { AnnotationHandler.toSnpEff(it) }.join(","))
                } else {
                    vcfContent.append("${AnnotationHandler.AnnotationTools.VEP.tag}=")
                    vcfContent.append(var.consequences.collect { AnnotationHandler.toVep(it) }.join(","))
                }
            }
            vcfContent.append("\n")
        }
        return vcfContent
    }

    /**
     * Generates JSON content in FHIR format from list of variants
     * @param variants the variants to export in FHIR
     * @return a FHIR content
     */
    static String exportVariantsToFHIR(List<Variant> variants, Boolean withConsequences, String referenceGenome) {

        // @TODO get patient ID if needed
        def patientReference = new Reference(new Patient().setIdentifier([new Identifier().setValue("#patient")]))

        // initialize diagnostic report
        DiagnosticReport diagnosticReport = new DiagnosticReport().tap {
            meta = new Meta().tap {
                profile = [new CanonicalType(new URI("http://hl7" + "" + "" + "" + "" + ".org/fhir/uv/genomics-reporting/StructureDefinition/diagnosticreport"))]
            }
            id = ""
            code = new CodeableConcept(new Coding("http://loinc.org", "81247-9", "Master HL7 genetic " + "variant " +
                    "reporting panel"))
            status = DiagnosticReport.DiagnosticReportStatus.FINAL
            issued = Date.from(Instant.now())
            subject = patientReference
        }

        def containedVariants = []
        def variantReferences = []

        // one observation for each variant
        variants.each { variant ->
            def variantObservation = new Observation().tap {
                id = variant.identifier
                variantReferences.add(new Reference("#${variant.identifier}"))

                meta = new Meta().tap {
                    profile = [new CanonicalType("http://hl7" + "" + "" + "" + ".org/fhir/uv/genomics-reporting/StructureDefinition/variant")]
                }
                status = Observation.ObservationStatus.FINAL
                category = [new CodeableConcept(new Coding(ObservationCategory.LABORATORY.system,
                        ObservationCategory.LABORATORY.toCode(), ObservationCategory.LABORATORY.display))]
                code = new CodeableConcept(new Coding("http://loinc.org", "69548-6", "Genetic variant assessment"))
                value = new CodeableConcept(new Coding("http://loinc.org", "LA9633-4", "Present"))
                method = new CodeableConcept(new Coding("http://loinc.org", "LA26398-0", "Sequencing"))
            }

            def variantObservationComponents = []
            variantObservationComponents.add(new Observation.ObservationComponentComponent().tap {
                code = new CodeableConcept(new Coding("http://loinc.org", "92822-6", "Genomic coordinate system " +
                        "[Type]"))
                value = new CodeableConcept(new Coding("http://loinc.org", "LA30102-0", "1-based character counting"))
            })

            variantObservationComponents.add(new Observation.ObservationComponentComponent().tap {
                code = new CodeableConcept(new Coding("http://hl7" + "" + "" + "" + ".org/fhir/uv/genomics-reporting/CodeSystem/tbd-codes", "exact-start-end", "Variant exact start and end"))
                value = new Range().tap {
                    low = new Quantity(variant.startPosition.longValue())
                    high = new Quantity(variant.endPosition.longValue())
                }
            })

            variantObservationComponents.add(new Observation.ObservationComponentComponent().tap {
                code = new CodeableConcept(new Coding("http://loinc.org", "69547-8", "Genomic ref allele [ID]"))
                value = new StringType(variant.referenceAllele)
            })

            variantObservationComponents.add(new Observation.ObservationComponentComponent().tap {
                value = new CodeableConcept(new Coding("http://loinc.org", "69551-0", "Genomic alt allele [ID]"))
                value = new StringType(variant.observedAllele)
            })


            if (variant.consequences.get(0)) {
                variantObservationComponents.add(new Observation.ObservationComponentComponent().tap {
                    code = new CodeableConcept(new Coding("http://loinc.org", "48018-6", "Gene studied [ID]"))
                    // @TODO how to get gene HGNC? For now lets us the available identifier
                    // @TODO should we collect all gene Ids ?
                    // the HGNC gene symbol as the display text and HGNC gene ID
                    value = new CodeableConcept(new Coding("http://www.genenames.org/geneId", variant.consequences
                            .get(0).geneId, variant.consequences.get(0).geneSymbol))
                })
            }

            variantObservationComponents.add(new Observation.ObservationComponentComponent().tap {
                code = new CodeableConcept(new Coding("http://loinc.org", "48001-2", "Cytogenetic (chromosome) " +
                        "location"))
                value = new StringType(variant.chromosome)
            })

            def referenceGenomeComponent = new Observation.ObservationComponentComponent()
            // determine whether UCSC or Ensembl reference genome
            if (referenceGenome.contains("hg")) {
                referenceGenomeComponent.code = new CodeableConcept(new Coding("http://loinc.org", "62373-6", "Human"
                        + " reference assembly release, UCSC version [Identifier]"))
                if (referenceGenome.contains("hg18")) {
                    referenceGenomeComponent.value = new CodeableConcept(new Coding("http://loinc.org", "LA14026-1",
                            "hg18"))
                } else if (referenceGenome.contains("hg19")) {
                    referenceGenomeComponent.value = new CodeableConcept(new Coding("http://loinc.org", "LA14025-3",
                            "hg19"))
                }
            } else {
                referenceGenomeComponent.code = new CodeableConcept(new Coding("http://loinc.org", "62374-4", "Human"
                        + " reference sequence assembly version"))
                if (referenceGenome.contains("GRCh37")) {
                    referenceGenomeComponent.value = new CodeableConcept(new Coding("http://loinc.org", "LA14029-5",
                            "GRCh37"))
                } else if (referenceGenome.contains("GRCh38")) {
                    referenceGenomeComponent.value = new CodeableConcept(new Coding("http://loinc.org", "LA26806-2",
                            "GRCh38"))
                }
            }
            variantObservationComponents.add(referenceGenomeComponent)

            def genomicSourceComponent = new Observation.ObservationComponentComponent()
            genomicSourceComponent.code = new CodeableConcept(new Coding("http://loinc.org", "48002-0", "Genomic " +
                    "source class"))
            // do we need any other type?
            if (variant.isSomatic) {
                genomicSourceComponent.value = new CodeableConcept(new Coding("http://loinc.org", "LA6684-0",
                        "Somatic"))
            } else {
                genomicSourceComponent.value = new CodeableConcept(new Coding("http://loinc.org", "LA6683-2",
                        "Germline"))
            }
            variantObservationComponents.add(genomicSourceComponent)

            // variant annotation dependent components
            // @TODO how to deal with multiple consequences? Can we just add multiple components?
            variant.getConsequences().each { consequence ->

                variantObservationComponents.add(new Observation.ObservationComponentComponent().tap {
                    code = new CodeableConcept(new Coding("http://loinc.org", "48004-6", "DNA change (c.HGVS)"))
                    value = new CodeableConcept(new Coding("http://varnomen.hgvs.org", consequence.codingChange,
                            consequence.codingChange))
                })

                variantObservationComponents.add(new Observation.ObservationComponentComponent().tap {
                    code = new CodeableConcept(new Coding("http://loinc.org", "48005-3", "Amino acid change " + "" +
                            "(pHGVS)"))
                    value = new CodeableConcept(new Coding("http://varnomen.hgvs.org", consequence.aaChange,
                            consequence.aaChange))
                })

                variantObservationComponents.add(new Observation.ObservationComponentComponent().tap {
                    code = new CodeableConcept(new Coding("http://loinc.org", "48006-1", "Amino acid change " + "type"))
                    // @TODO mapping from consequence type to LOINC Preferred Answer List, possible to use
                    //  Sequence Ontology?
                    // for now we will just use the given type
                    //def type = ConsequenceTypes.getLoincMapping(consequence.type)
                    //value = new CodeableConcept(new Coding("http://loinc.org", type.tag, type.toString()))
                    value = new CodeableConcept(new Coding("http://sequenceontology.org", consequence.type.toString(), consequence.type.toString()))
                })

                variantObservationComponents.add(new Observation.ObservationComponentComponent().tap {
                    code = new CodeableConcept(new Coding("http://loinc.org", "51958-7", "Transcript " + "reference "
                            + "sequence " + "[ID]"))
                    // @TODO ensembl?
                    value = new CodeableConcept(new Coding("http://www.ncbi.nlm.nih.gov/refseq", consequence
                            .transcriptId, consequence.transcriptId))
                })
            }

            if (!variant.vcfInfo.alleleFrequency.isEmpty()) {
                variantObservationComponents.add(new Observation.ObservationComponentComponent().tap {
                    code = new CodeableConcept(new Coding("http://loinc.org", "81258-6", "Sample variant allelic " +
                            "frequency [NFr]"))
                    value = new DecimalType(variant.vcfInfo.alleleFrequency.get(0))
                })
            }

            if (variant.vcfInfo.combinedDepth) {
                variantObservationComponents.add(new Observation.ObservationComponentComponent().tap {
                    code = new CodeableConcept(new Coding("http://loinc.org", "82121-5",
                            "Allelic read depth"))
                    value = new Quantity().tap {
                        value = variant.vcfInfo.combinedDepth.longValue()
                        system = "http://unitsofmeasure.org"
                        code = "{reads}/{base}"
                        unit = "reads per base pair"
                    }
                })
            }

            // @TODO needed?
            /*
            def observation_dv_component3 = new Observation.ObservationComponentComponent()
            observation_dv_component3.code = new CodeableConcept(new Coding("http://loinc.org", "53034-5", "Allelic
            state"))
            observation_dv_component3.value = new CodeableConcept(new Coding("http://loinc.org",alleles.at[index,
            'CODE'], alleles.at[index,'ALLELE']))
             */

            variantObservation.component = variantObservationComponents
            containedVariants.add(variantObservation)
        }
        diagnosticReport.contained = containedVariants
        diagnosticReport.result = variantReferences

        // Create a context for R4, do we need other versions?
        FhirContext contextR4 = FhirContext.forR4()
        def fhirContent = contextR4.newJsonParser().setPrettyPrint(true).encodeResourceToString(diagnosticReport)

        return fhirContent
    }
}
