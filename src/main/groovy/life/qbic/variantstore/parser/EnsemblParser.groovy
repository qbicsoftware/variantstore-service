package life.qbic.variantstore.parser

import groovy.util.logging.Log4j2
import htsjdk.tribble.AbstractFeatureReader
import htsjdk.tribble.gff.Gff3Codec
import htsjdk.tribble.gff.Gff3Feature
import htsjdk.tribble.readers.LineIterator
import life.qbic.variantstore.model.Gene
import life.qbic.variantstore.model.ReferenceGenome

/**
 * A parser to process Ensembl GFF files holding gene information
 *
 * @since: 1.0.0
 */
@Log4j2
class EnsemblParser {

    /**
     * The genes
     */
    final List<Gene> genes
    /**
     * The reference genome
     */
    final ReferenceGenome referenceGenome
    /**
     * The Ensembl version
     */
    final Integer version
    /**
     * The date associated with this Ensembl file
     */
    final String date

    EnsemblParser(File file) {

        Gff3Codec codec = new Gff3Codec()
        AbstractFeatureReader<Gff3Feature, LineIterator> reader = AbstractFeatureReader.getFeatureReader(file
                .absolutePath.toString(), null, codec, false);

        // try to extract reference genome and Ensembl version
        def versionMatch = (file.name =~ /(GRCh)\d+.\d+|\w+(v)\d+/)
        String referenceGenome = ""
        Integer ensemblVersion = 0
        if (versionMatch.find()) {
            (referenceGenome, ensemblVersion) = versionMatch[0][0].toString().split("\\.|v")
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
                def gene = new Gene()
                gene.geneId = feature.ID.split(":")[-1]
                gene.bioType = feature.getAttribute("biotype")
                gene.chromosome = feature.contig
                gene.geneStart = feature.start
                gene.geneEnd = feature.end
                gene.strand = feature.strand
                gene.symbol = feature.name
                gene.version = feature.getAttribute("version") != null ? feature.getAttribute("version").toInteger(): -1
                def description = (feature.getAttribute("description") != null) ? feature.getAttribute("description") : ''
                def synonym = (feature.getAttribute("description") != null) && feature.getAttribute("description").contains("HGNC") ? feature.getAttribute("description").split("\\[").last().split("HGNC:").last().replace("]", "") : ''
                gene.name = description.split("\\[").first().trim()

                gene.synonyms = [synonym]
                gene.description = description.trim()
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

        this.genes = genes
        this.referenceGenome = new ReferenceGenome("Genome Reference Consortium", referenceGenome,
        referenceGenomeVersion as String)
        this.version = ensemblVersion.toInteger()
        this.date = updateDate
    }
}