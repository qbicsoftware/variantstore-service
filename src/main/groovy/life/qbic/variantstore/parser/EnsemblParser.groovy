package life.qbic.variantstore.parser

import groovy.util.logging.Log4j2
import htsjdk.tribble.AbstractFeatureReader
import htsjdk.tribble.gff.Gff3Codec
import htsjdk.tribble.gff.Gff3Feature
import htsjdk.tribble.readers.LineIterator
import life.qbic.variantstore.model.Ensembl
import life.qbic.variantstore.model.Gene
import life.qbic.variantstore.model.ReferenceGenome

/**
 * A parser to process Ensembl GFF files holding gene information
 *
 * @since: 1.0.0
 */
@Log4j2
class EnsemblParser {

    private final GENOME_REFERENCE_SOURCE_GRC = "Genome Reference Consortium"

    /**
     * The metadata stored as {@EnsemblContext}
     */
    final Ensembl ensemblContext

    EnsemblParser(File file) {
        Gff3Codec codec = new Gff3Codec()
        AbstractFeatureReader<Gff3Feature, LineIterator> reader = AbstractFeatureReader.getFeatureReader(file
                .absolutePath.toString(), null, codec, false);

        // try to extract reference genome and Ensembl version
        def referenceMatch = (file.name =~ /(GRCh|hg)\d+/)
        def versionMatch = (file.name =~ /(GRCh)\d+.\d+|\w+(v)\d+/)
        String referenceGenome = ""
        Integer ensemblVersion = 0
        if (versionMatch.find()) {
            def foundPattern = versionMatch[0][0].toString().split("\\.|v")
            referenceGenome = foundPattern.first()
            ensemblVersion = Integer.valueOf(foundPattern.last())
        }
        else if (referenceMatch.find()) {
            referenceGenome = referenceMatch[0][0].toString()
            ensemblVersion = null
        }

        def splittedLine = ""
        def updateDate = ""
        def firstFound = false
        def secondFound = false
        def numberOfGenes = 0
        def genes = []

        // process features
        for (final Gff3Feature feature : reader.iterator()) {
            if(feature.type.contains("gene")) {
                numberOfGenes++
                String geneId = feature.ID.split(":")[-1]
                def bioType = feature.getAttribute("biotype")
                def chromosome = feature.contig
                def geneStart = feature.start as BigInteger
                def geneEnd = feature.end as BigInteger
                def strand = feature.strand.toString()
                def symbol = feature.name
                def version = feature.getAttribute("version") != null ? feature.getAttribute("version").toInteger(): -1
                def description = (feature.getAttribute("description") != null) ? feature.getAttribute("description") : ''
                def synonym = (feature.getAttribute("description") != null) && feature.getAttribute("description").contains("HGNC") ? feature.getAttribute("description").split("\\[").last().split("HGNC:").last().replace("]", "") : ''
                def name = description.split("\\[").first().trim()
                def synonyms = [synonym]
                def geneDescription = description.trim()

                def gene = new Gene(bioType, chromosome, symbol, name, geneStart, geneEnd, geneId, geneDescription, strand, version, synonyms)
                genes.add(gene)
            }
        }

        String line
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file))
        while ((line = bufferedReader.readLine()) != null)
        {
            if (line.startsWith("#!genome-build ")) {
                splittedLine = line.split(" ")[-1]
            }
            else if (line.startsWith("#!genebuild-last-updated")) {
                updateDate = line.split(" ")[-1]
            }
            if (firstFound && secondFound) {
                break
            }
        }

        // determine reference genome version from Ensembl file
        def (referenceGenomeFromFile, referenceGenomeVersion) = splittedLine.split("v|\\.")

        try {
            assert referenceGenome == referenceGenomeFromFile
        } catch (AssertionError e) {
            println "Given reference genomes do not match: " + e.getMessage()
        }

        log.info("Read $numberOfGenes genes from provided Ensembl file.")

        // if the reference genome is specified in the file under #!genome-build we will use this information
        def refernceGenomeToDB = referenceGenomeFromFile ? referenceGenomeFromFile : referenceGenome

        Ensembl ensemblContext = new Ensembl()
        ensemblContext.genes = genes
        ensemblContext.referenceGenome = new ReferenceGenome(GENOME_REFERENCE_SOURCE_GRC, refernceGenomeToDB,
                referenceGenomeVersion as String)
        ensemblContext.version = ensemblVersion ? ensemblVersion.toInteger() : -1
        ensemblContext.date = updateDate

        this.ensemblContext = ensemblContext
    }

    Ensembl getEnsemblContext() {
        return this.ensemblContext
    }
}
