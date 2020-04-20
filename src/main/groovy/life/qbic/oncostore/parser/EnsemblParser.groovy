package life.qbic.oncostore.parser

import groovy.util.logging.Log4j2
import life.qbic.oncostore.model.Gene
import life.qbic.oncostore.model.ReferenceGenome
import org.biojava.nbio.genome.parsers.gff.GFF3Reader
import org.biojava.nbio.genome.parsers.gff.FeatureList

@Log4j2
class EnsemblParser {

    final List<Gene> genes
    final ReferenceGenome referenceGenome
    final Integer version
    final String date

    EnsemblParser(String url) {

        FeatureList geneList = GFF3Reader.read(url).selectByAttribute("gene_id")
        //def versionMatch = (url =~ /(GRCh)\d+.\d+|\w+(v)\d+/)

        def versionMatch = (url =~ /\w+(v)\d/)
        def referenceGenome = ""
        def ensemblVersion = 0
        if (versionMatch.find()) {
            (referenceGenome, ensemblVersion) = versionMatch[0][0].toString().split("\\.|v")
        }

        File fromFile = new File(url);
        String line
        def splittedLine = ""
        def updateDate = ""
        def firstFound = false
        def secondFound = false
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fromFile))
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

        // Determine reference genome version from Ensembl file
        def (referenceGenomeFromFile, referenceGenomeVersion) = splittedLine.split("v|\\.")

        try {
            assert referenceGenome == referenceGenomeFromFile
        } catch (AssertionError e) {
            println "Given reference genomes do not match: " + e.getMessage()
        }

        log.info("Read ${geneList.size()} genes from provided Ensembl file.")

        def genes = []
        geneList.each{feat ->
            def gene = new Gene()
            def description = (feat.getAttribute("description") != null) ? feat.getAttribute("description").split("\\[").first() : ''
            def synonym = (feat.getAttribute("description") != null) && feat.getAttribute("description").contains("HGNC") ? feat.getAttribute("description").split("\\[").last().split("HGNC:").last().replace("]", "") : ''

            gene.geneId = feat.getAttribute("gene_id")
            gene.bioType = feat.getAttribute("biotype")
            gene.chromosome = feat.seqname()
            gene.geneEnd = feat.location().bioEnd()
            gene.geneStart = feat.location().bioStart()
            gene.name = feat.getAttribute("Name")
            gene.synonyms = [synonym]
            gene.description = description.trim()
            gene.symbol = feat.getAttribute("Name")
            gene.version = feat.getAttribute("version") != null ? feat.getAttribute("version").toInteger(): -1
            gene.strand = feat.location().bioStrand()
            genes.add(gene)
        }
        this.genes = genes
        this.referenceGenome = new ReferenceGenome("Genome Reference Consortium", referenceGenome,referenceGenomeVersion)
        this.version = ensemblVersion.toInteger()
        this.date = updateDate
    }
}
