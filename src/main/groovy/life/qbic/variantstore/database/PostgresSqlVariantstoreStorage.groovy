package life.qbic.variantstore.database

import groovy.util.logging.Log4j2
import io.micronaut.context.annotation.Primary
import io.micronaut.context.annotation.Requires
import life.qbic.variantstore.model.*

import life.qbic.variantstore.parser.MetadataContext
import life.qbic.variantstore.repositories.CaseRepository
import life.qbic.variantstore.repositories.ConsequenceRepository
import life.qbic.variantstore.repositories.EnsemblRepository
import life.qbic.variantstore.repositories.GeneRepository
import life.qbic.variantstore.repositories.GenotypeRepository
import life.qbic.variantstore.repositories.ProjectRepository
import life.qbic.variantstore.repositories.ReferenceGenomeRepository
import life.qbic.variantstore.repositories.SampleRepository
import life.qbic.variantstore.repositories.SampleVariantRepository
import life.qbic.variantstore.repositories.VariantAnnotationRepository
import life.qbic.variantstore.repositories.VariantCallerRepository
import life.qbic.variantstore.repositories.VariantRepository
import life.qbic.variantstore.repositories.VcfinfoRepository
import life.qbic.variantstore.service.VariantstoreStorage
import life.qbic.variantstore.util.ListingArguments
import jakarta.inject.*

/**
 *
 *
 *
 *
 * @since: 1.1.0
 */
@Log4j2
@Singleton
@Primary
@Requires(property = "database.specifier", value = "variantstore-postgres")
class PostgresSqlVariantstoreStorage implements VariantstoreStorage {


    @Inject ProjectRepository projectRepository
    @Inject CaseRepository caseRepository
    @Inject SampleRepository sampleRepository
    @Inject VariantRepository variantRepository
    @Inject GeneRepository geneRepository
    @Inject ReferenceGenomeRepository referenceGenomeRepository
    @Inject VariantCallerRepository variantCallerRepository
    @Inject VariantAnnotationRepository variantAnnotationRepository
    @Inject VcfinfoRepository vcfinfoRepository
    @Inject GenotypeRepository genotypeRepository
    @Inject SampleVariantRepository sampleVariantRepository
    @Inject ConsequenceRepository consequenceRepository
    @Inject EnsemblRepository ensemblRepository

    @Override
    Set<Variant> findVariantsForBeaconResponse(String chromosome, BigInteger start, String reference,
                                                       String observed, String assemblyId) {
        try {
            def variant = variantRepository.findForBeacon(chromosome, start, reference, observed, assemblyId)
            return variant
        } catch (Exception e) {
            throw new VariantstoreStorageException("Beacon something? $e.", e.printStackTrace())
        }
    }

    @Override
    List<Case> findCaseById(String identifier) {
        return caseRepository.findByIdentifier(identifier)
    }

    @Override
    List<Sample> findSampleById(String identifier) {
        return sampleRepository.findByIdentifier(identifier)
    }

    @Override
    Set<Variant> findVariantById(String identifier) {
        return variantRepository.findByIdentifier(identifier)
    }

    @Override
    Set<Gene> findGeneById(String identifier, ListingArguments args) {
        try {
            if (args.getEnsemblVersion().isPresent()) {
                return geneRepository.searchByGeneId(identifier, args.getEnsemblVersion().get())
            }
            def ensemblversion = fetchEnsemblVersion()
            if (ensemblversion) {
                return geneRepository.searchByGeneId(identifier, ensemblversion)
            }
            // fall back solution, if there is not ensembl version in the variantstore instance
            return geneRepository.findByGeneId(identifier)
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch gene with identifier $identifier.", e.printStackTrace())
        }
    }

    @Override
    List<Case> findCases(ListingArguments args) {
        try {
            if (args.getGene().isPresent()) {
                if (args.getConsequenceType().isPresent()) {
                    return caseRepository.searchByGeneSymbolAndConsequenceType(args.getGene().get(), args.getConsequenceType().get())
                }
                else {
                    return caseRepository.searchByGeneSymbol(args.getGene().get())
                }
            }
            if (args.getConsequenceType().isPresent()) {
                return caseRepository.searchByConsequenceType(args.getConsequenceType().get())
            }

            if (args.getChromosome().isPresent() && args.getStartPosition().isPresent() && args.getEndPosition()
                    .isPresent()) {
                return caseRepository.searchByChromosomeAndStartPositionRange(args.getChromosome().get(), args.getStartPosition().get(),
                        args.getEndPosition().get())
            }

            if (args.getChromosome().isPresent()) {
                return caseRepository.searchByChromosome(args.getChromosome().get())
            }

            return caseRepository.findAll()
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch cases.", e.printStackTrace())
        }
    }

    @Override
    List<Sample> findSamples(ListingArguments args) {
        try {
            if (args.getCancerEntity().isPresent()) {
                return sampleRepository.findByCancerEntity(args.getCancerEntity().get())
            }
            return sampleRepository.findAll()
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch samples.", e.fillInStackTrace())
        }
    }

    @Override
    Set<Variant> findVariants(ListingArguments args, String referenceGenome, Boolean withConsequences,
                                      String annotationSoftware, Boolean withVcfInfo, Boolean withGenotypes) {
        try {
            if (args.getChromosome().isPresent() && args.getStartPosition().isPresent()) {
                return fetchVariantsByChromosomeAndStartPosition(args.getChromosome().get(), args.getStartPosition()
                        .get(), referenceGenome, withConsequences, annotationSoftware, withVcfInfo, withGenotypes)
            }
            if (args.getStartPosition().isPresent()) {
                return findVariantByStartPositon(args.getStartPosition().get(), referenceGenome, withConsequences,
                        annotationSoftware, withVcfInfo,
                        withGenotypes)
            }
            if (args.getChromosome().isPresent()) {
                return fetchVariantsByChromosome(args.getChromosome().get(), referenceGenome, withConsequences, annotationSoftware, withVcfInfo, withGenotypes)
            }
            if (args.getSampleId().isPresent() && args.getGeneId().isPresent()) {
                return fetchVariantsBySampleAndGeneId(args.getSampleId().get(), args.getGeneId().get(), referenceGenome, withConsequences, annotationSoftware, withVcfInfo, withGenotypes)
            }
            if (args.getSampleId().isPresent()) {
                return fetchVariantsBySample(args.getSampleId().get(), referenceGenome, withConsequences,
                        annotationSoftware, withVcfInfo, withGenotypes)
            }
            if (args.getGeneId().isPresent()) {
                return fetchVariantsByGeneId(args.getGeneId().get(), referenceGenome, withConsequences,
                        annotationSoftware, withVcfInfo, withGenotypes)
            }
            if (args.getGene().isPresent()) {
                return fetchVariantsByGeneName(args.getGene().get(), referenceGenome, withConsequences,
                        annotationSoftware, withVcfInfo, withGenotypes)
            }
            return fetchVariants(referenceGenome, withConsequences, annotationSoftware, withVcfInfo, withGenotypes)
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch variants.", e.printStackTrace())
        }
    }

    @Override
    Annotation findAnnotationSoftwareByConsequence(Consequence consequence) {
        Optional<Consequence> searchResult = consequenceRepository.retrieve(consequence.allele, consequence
                .codingChange, consequence.transcriptId, consequence.transcriptVersion, consequence.type, consequence
                .bioType, consequence.canonical, consequence.aaChange, consequence.cdnaPosition, consequence
                .cdsPosition, consequence.proteinPosition, consequence.proteinLength, consequence.cdnaLength,
                consequence.cdsLength, consequence.impact, consequence.exon, consequence.intron, consequence.strand,
                consequence.geneSymbol, consequence.featureType, consequence.distance, consequence.warnings)
        if (searchResult.present) {
            //@TODO handle multi cases
            searchResult.get().annotations[0]
        }
        return searchResult
    }

    @Override
    ReferenceGenome findReferenceGenomeByVariant(Variant variant) {
        return variantRepository.search(variant.databaseIdentifier, variant.chromosome, variant.startPosition,
                variant.endPosition, variant.referenceAllele, variant.observedAllele, variant.somatic)
    }

    @Override
    Set<Gene> findGenes(ListingArguments args) {
        try {
            if (args.getSampleId()) {
                return geneRepository.getForSampleIdentifier(args.getSampleId().get())
            }
            return geneRepository.findAll()
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not fetch genes.", e.fillInStackTrace())
        }
    }

    @Override
    void storeCaseInStore(Case patient) throws VariantstoreStorageException {
        caseRepository.save(patient)
    }

    @Override
    void storeSampleInStore(Sample sample) throws VariantstoreStorageException {
        sampleRepository.save(sample)
    }

    @Override
    void storeReferenceGenomeInStore(ReferenceGenome referenceGenome) throws VariantstoreStorageException {
        referenceGenomeRepository.save(referenceGenome)
    }

    @Override
    void storeVariantCallerInStore(VariantCaller variantCaller) throws VariantstoreStorageException {
        variantCallerRepository.save(variantCaller)
    }

    @Override
    void storeAnnotationSoftwareInStore(Annotation annotationSoftware) throws VariantstoreStorageException {
        variantAnnotationRepository.save(annotationSoftware)
    }

    @Override
    void storeVariantsInStoreWithMetadata(MetadataContext metadata, Map<String, Sample> sampleIdentifiers, ArrayList<SimpleVariantContext> variants) throws
            VariantstoreStorageException  {
        try {

            def project = metadata.getProject()
            def entity = metadata.getCase()
            def annotationTool = metadata.getVariantAnnotation()
            def variantCaller = metadata.getVariantCalling()
            def refGenome = metadata.getReferenceGenome()

            def retrievedProject = projectRepository.findByIdentifier(project.identifier)
            def newProject
            if (retrievedProject.isPresent()) {
                newProject = retrievedProject.get()
            }
            else {
                newProject = projectRepository.save(project)
            }

            def searchResultVA = variantAnnotationRepository.find(annotationTool.name, annotationTool.version, annotationTool.doi)
            def newVA = searchResultVA.present ? searchResultVA.get() : variantAnnotationRepository.save(annotationTool)

            def searchResultVC = variantCallerRepository.find(variantCaller.name, variantCaller.version, variantCaller.doi)
            def newVariantCaller = searchResultVC.present ? searchResultVC.get() : variantCallerRepository.save(variantCaller)

            def searchResultRG = referenceGenomeRepository.find(refGenome.source, refGenome.build, refGenome.version)
            def newReferenceGenome = searchResultRG.present ? searchResultRG.get() : referenceGenomeRepository.save(refGenome)

            entity.setProject(newProject)
            def retrievedEntity = caseRepository.findByIdentifier(entity.identifier)
            def newEntity
            if (!retrievedEntity.empty) {
                newEntity = caseRepository.update(retrievedEntity.get(0))
            }
            else {
                newEntity = caseRepository.save(entity)
            }

            def newSamples = new HashMap<String, Sample>()
            if (!sampleIdentifiers.isEmpty()) {
                sampleIdentifiers.each { name, sample ->
                    sample.setEntity(newEntity)
                    def searchResult = sampleRepository.findByIdentifier(sample.identifier)
                    def newSample = !searchResult.empty ? searchResult.get(0) : sampleRepository.save(sample)
                    newSamples[name] = newSample
                }
            }

            ArrayList<Variant> newVariants = new ArrayList<Variant>()
            Set<Consequence> consequencesToRegister = new HashSet<Consequence>()
            def consequencesRegistered
            def genotypesToRegister
            def genotypesRegistered
            //@TODO rather use genotype field of Variant class?
            def variantGenotypeMap = new HashMap<Variant, Set<Genotype>>()
            def variantVcfInfoMap = new HashMap<Variant, VcfInfo>()
            def updateNeeded = false

            variants.each {var ->
                consequencesToRegister = new HashSet<Consequence>()
                consequencesRegistered = [] as Set
                genotypesToRegister = [] as Set
                genotypesRegistered = [] as Set

                def searchResult = variantRepository.find(var.chromosome, var.startPosition, var.databaseIdentifier,
                        var.endPosition, var.referenceAllele, var.observedAllele, var.somatic)
                def newVariant = searchResult.present ? searchResult.get() : variantRepository.save(var as Variant)

                var.consequences.each {Consequence consequence ->
                    def searchResultConsequence = consequenceRepository.find(consequence.allele, consequence
                            .codingChange, consequence.transcriptId, consequence.transcriptVersion, consequence.type,
                            consequence.bioType, consequence.canonical, consequence.aaChange, consequence.cdnaPosition,
                    consequence.cdsPosition, consequence.proteinPosition, consequence.proteinLength, consequence.cdnaLength,
                            consequence.cdsLength, consequence.impact, consequence.exon, consequence.intron,
                    consequence.strand, consequence.geneSymbol, consequence.featureType, consequence.distance, consequence.warnings)

                    if (!searchResultConsequence.present) {
                        consequencesToRegister.add(consequence)
                    }

                    consequence.addAnnotationTool(newVA)
                    consequence.genes.each {gene ->
                        def searchResultGene = geneRepository.findByGeneId(gene.geneId)
                        searchResultGene.present ? searchResultGene.get() : geneRepository.save(gene)
                    }
                }

                if (!consequencesToRegister.empty) {
                    newVariant.consequences.addAll(consequencesToRegister)
                }
                else {
                    newVariant.consequences = consequencesRegistered
                }

                var.genotypes.each {Genotype genotype ->
                    def searchResultGenotype = genotypeRepository.find(genotype.genotype, genotype.readDepth, genotype.filter, genotype.likelihoods, genotype.genotypeLikelihoods, genotype.genotypeLikelihoodsHet,
                            genotype.posteriorProbs, genotype.genotypeQuality, genotype.haplotypeQualities, genotype.phaseSet, genotype.phasingQuality,
                            genotype.alternateAlleleCounts, genotype.mappingQuality)

                    if (!searchResultGenotype.present) {
                        genotypesToRegister.add(genotype)
                    }
                    else {
                        genotypesRegistered.add(genotype)
                    }
               }

                def newGenotypes = genotypesToRegister.empty ? [] : genotypeRepository.saveAll(genotypesToRegister)
                variantGenotypeMap[newVariant] = newGenotypes + genotypesRegistered as Set<Genotype>

                def searchResultVcfInfo = vcfinfoRepository.find(var.vcfInfo.ancestralAllele, var.vcfInfo
                        .alleleCount, var.vcfInfo.alleleFrequency, var.vcfInfo.numberAlleles, var.vcfInfo
                        .baseQuality, var.vcfInfo.cigar, var.vcfInfo.dbSnp, var.vcfInfo.hapmapTwo, var.vcfInfo
                        .hapmapThree, var.vcfInfo.thousandGenomes, var.vcfInfo.combinedDepth, var.vcfInfo.endPos, var
                        .vcfInfo.rms, var.vcfInfo.mqZero, var.vcfInfo.strandBias, var.vcfInfo.numberSamples, var
                        .vcfInfo.somatic, var.vcfInfo.validated)

                def newVcfInfo = searchResultVcfInfo.present ? searchResultVcfInfo.get() : vcfinfoRepository.save(var.vcfInfo)
                variantVcfInfoMap[newVariant] = newVcfInfo

                newVariant.addVariantCaller(newVariantCaller)
                newVariant.addReferenceGenome(newReferenceGenome)

                updateNeeded = !(searchResult.present & searchResultVA.present & searchResultVC.present & searchResultRG.present &
                        searchResultVcfInfo.present & consequencesToRegister.empty & genotypesToRegister.empty)

                if (updateNeeded) { newVariants.add(newVariant) }
            }

            if (!newVariants.empty) {
                newVariants = variantRepository.updateAll(newVariants)
            }

            List<SampleVariant> sampleVariants = new ArrayList<SampleVariant>()
            newVariants.each { variant ->
                if (variantGenotypeMap[variant].empty) {
                    newSamples.each {
                        def sampleVariant = new SampleVariant(it.value, variant, variantVcfInfoMap[variant], null)
                        def sampleVariantSearch = sampleVariantRepository.findBySampleAndVariantAndVcfinfoAndGenotype(it.value, variant, variantVcfInfoMap[variant], null)
                        if (!sampleVariantSearch.present) {
                            sampleVariants.add(sampleVariant)
                        }
                    }
                } else {
                    variantGenotypeMap[variant].each { genotype ->
                        def sampleVariant = new SampleVariant(newSamples[genotype.sampleName], variant, variantVcfInfoMap[variant], genotype)
                        def sampleVariantSearch = sampleVariantRepository.findBySampleAndVariantAndVcfinfoAndGenotype(newSamples[genotype.sampleName], variant, variantVcfInfoMap[variant], genotype)
                        if (!sampleVariantSearch.present) {
                            sampleVariants.add(sampleVariant)
                        }
                    }
                }
            }
            if (!sampleVariants.empty) {
                sampleVariantRepository.saveAll(sampleVariants)
            }
        }
        catch (Exception e) {
            throw new VariantstoreStorageException("Could not store variants with metadata in store: $metadata.samples",
                    e.printStackTrace())
        }
    }

    /**
     * Store variants in batch in the store
     * @param variants a list of variants
     * @param list of consequences of the provded variants
     */
    private List tryToStoreVariantsBatch(ArrayList<SimpleVariantContext> variants, ArrayList<Sample> samples) {

        ArrayList<SimpleVariantContext> insertedVars = []
        try {
            //@TODO maybe add batching here, sampleVariant handling
            insertedVars = variantRepository.saveAll(variants)
            //vcfinfoRepository.saveAll(variants.collect{variant -> variant.getVcfInfo()})
            //genotypeRepository.saveAll(variants.collect{ variant -> variant.getGenotypes()}.flatten())
        }
        catch (Exception e) {
            e.printStackTrace()
        }
        finally {
        }
        return insertedVars
    }

    @Override
    void storeGenesWithMetadata(Ensembl ensemblContext) throws VariantstoreStorageException {
        try {
            def searchResultRG = referenceGenomeRepository.find(ensemblContext.referenceGenome.source, ensemblContext.referenceGenome.build, ensemblContext.referenceGenome.version)
            def newReferenceGenome = searchResultRG.present ? searchResultRG.get() : referenceGenomeRepository.save(ensemblContext.referenceGenome)
            ensemblContext.referenceGenome.id  = newReferenceGenome.id
            def searchResultEnsembl = ensemblRepository.find(ensemblContext.version, ensemblContext.date, ensemblContext.referenceGenome)

            if (searchResultEnsembl.present) {
                ensemblContext.genes.each {
                    if(!searchResultEnsembl.get().genes.contains(it)) {
                        searchResultEnsembl.get().genes.add(it)
                    }
                }
            }
            searchResultEnsembl.present ? ensemblRepository.update(searchResultEnsembl.get()) : ensemblRepository.save(ensemblContext)
        } catch (Exception e) {
            throw new VariantstoreStorageException("Could not store genes in store: ", e.printStackTrace())
        }
    }

    Set<Variant> fetchVariants(String referenceGenome, boolean withConsequences, String annotationSoftware, boolean withVcfInfo, boolean withGenotypes) {
        if (withConsequences & withGenotypes) {
            // we will fetch VcfInfo information as well since this case is always VCF output format
            return variantRepository.getAllWithConsequencesAndGenos(annotationSoftware, referenceGenome)
        }
        else if (withConsequences) {
            if (withVcfInfo) {
                return variantRepository.getAllWithConsequencesAndVcfInfo(annotationSoftware, referenceGenome)
            } else {
                return variantRepository.getAllWithConsequencesAndNoVcfInfo(annotationSoftware, referenceGenome)
            }
        } else {
            if (withVcfInfo) {
                return variantRepository.getAllWithVcfInfo(referenceGenome)
            }
            else {
                return variantRepository.findAll()
            }
        }
    }

    /**
     * Get variants by chromosome and start position from the store
     * @param chromosome the chromosome
     * @param start the genomic start position
     * @param referenceGenome the reference genome
     * @param withConsequences true if connected consequences should be returned
     * @param annotationSoftware the annotation software
     * @param withVcInfo true if connected VCF info should be returned
     * @param withGenotypes true if connected genotypes should be returned
     * @param sql the sql connections
     * @return the found variants
     */
    // search, query, get, read or retrieve
    private List<Variant> fetchVariantsByChromosomeAndStartPosition(
            String chromosome, BigInteger start, String referenceGenome, Boolean withConsequences,
            String annotationSoftware, Boolean withVcfInfo, Boolean withGenotypes) {
        if (withConsequences & withGenotypes) {
            // we will fetch VcfInfo information as well since this case is always VCF output format
            return variantRepository.searchByChromosomeAndStartPosition(chromosome, start, annotationSoftware, referenceGenome)
        }
        else if (withConsequences) {
            if (withVcfInfo) {
                return variantRepository.queryByChromosomeAndStartPosition(chromosome, start, annotationSoftware, referenceGenome)
            } else {
                return variantRepository.getByChromosomeAndStartPosition(chromosome, start, annotationSoftware, referenceGenome)
            }
        } else {
            if (withVcfInfo) {
                return variantRepository.retrieveByChromosomeAndStartPosition(chromosome, start, referenceGenome)
            }
            else {
            return variantRepository.findByChromosomeAndStartPosition(chromosome, start, referenceGenome)
            }
        }
    }

    private List<Variant> findVariantByStartPositon(BigInteger startPosition, String referenceGenome,
                                                    Boolean withConsequences, String annotationSoftware,
                                                    Boolean withVcfInfo, Boolean withGenotypes) {
        if (withConsequences & withGenotypes) {
            // we will fetch VcfInfo information as well since this case is always VCF output format
            return variantRepository.searchByStartPosition(startPosition, annotationSoftware, referenceGenome)
        }
        else if (withConsequences) {
            if (withVcfInfo) {
                return variantRepository.queryByStartPosition(startPosition, annotationSoftware, referenceGenome)
            } else {
                return variantRepository.getByStartPosition(startPosition, annotationSoftware, referenceGenome)
            }
        } else {
            if (withVcfInfo) {
                return variantRepository.retrieveByStartPosition(startPosition, referenceGenome)
            }
            else {
                return variantRepository.findByStartPosition(startPosition, referenceGenome)
            }
        }
    }

    private List<Variant> fetchVariantsByChromosome(String chromosome, String referenceGenome,
                                                    Boolean withConsequences, String annotationSoftware,
                                                    Boolean withVcfInfo, Boolean withGenotypes) {
        if (withConsequences & withGenotypes) {
            // we will fetch VcfInfo information as well since this case is always VCF output format
            return variantRepository.searchByChromosome(chromosome, annotationSoftware, referenceGenome)
        }
        if (withConsequences) {
            if (withVcfInfo) {
                return variantRepository.queryByChromosome(chromosome, annotationSoftware, referenceGenome)
            } else {
                return variantRepository.getByChromosome(chromosome, annotationSoftware, referenceGenome)
            }
        }
        if (withVcfInfo) {
            return variantRepository.retrieveByChromosome(chromosome, referenceGenome)
        } else {
            return variantRepository.findByChromosome(chromosome, referenceGenome)
        }
    }

    private Set<Variant> fetchVariantsBySampleAndGeneId(String sampleIdentifier, String geneId, String referenceGenome,
                                          Boolean withConsequences, String annotationSoftware,
                                          Boolean withVcfInfo, Boolean withGenotypes) {
        if (withConsequences & withGenotypes) {
            // we will fetch VcfInfo information as well since this case is always VCF output format
            return variantRepository.findUsingSampleIdAndGeneIdWithConsAndGeno(sampleIdentifier, geneId, referenceGenome, annotationSoftware)
        }
        if (withConsequences) {
            if (withVcfInfo) {
                return variantRepository.findUsingSampleIdAndGeneIdWithConsAndInfo(sampleIdentifier, geneId, referenceGenome,
                        annotationSoftware)
            } else {
                return variantRepository.findUsingSampleIdAndGeneIdWithCons(sampleIdentifier, geneId, referenceGenome,
                        annotationSoftware)
            }
        }
        if (withVcfInfo) {
            return variantRepository.findUsingSampleIdAndGeneIdWithInfo(sampleIdentifier, geneId, referenceGenome)
        } else {
            return variantRepository.findUsingSampleIdAndGeneId(sampleIdentifier, geneId, referenceGenome)
        }
    }


    private Set<Variant> fetchVariantsBySample(String sampleIdentifier, String referenceGenome,
                                 Boolean withConsequences, String annotationSoftware,
                                 Boolean withVcfInfo, Boolean withGenotypes) {
        if (withConsequences & withGenotypes) {
            // we will fetch VcfInfo information as well since this case is always VCF output format
            return variantRepository.findUsingSampleIdWithConsAndGeno(sampleIdentifier, referenceGenome, annotationSoftware)
        }
        if (withConsequences) {
            if (withVcfInfo) {
                return variantRepository.findUsingSampleIdWithConsAndInfo(sampleIdentifier, referenceGenome,
                        annotationSoftware)
            } else {
                return variantRepository.findUsingSampleIdWithCons(sampleIdentifier, referenceGenome,
                        annotationSoftware)
            }
        }
        if (withVcfInfo) {
            return variantRepository.findUsingSampleIdWithInfo(sampleIdentifier, referenceGenome)
        } else {
            return variantRepository.findUsingSampleId(sampleIdentifier, referenceGenome)
        }
    }

    private Set<Variant> fetchVariantsByGeneId(String geneId, String referenceGenome,
                                 Boolean withConsequences, String annotationSoftware,
                                 Boolean withVcfInfo, Boolean withGenotypes) {
        if (withConsequences & withGenotypes) {
            // we will fetch VcfInfo information as well since this case is always VCF output format
            return variantRepository.findUsingGeneIdWithConsAndGeno(geneId, referenceGenome, annotationSoftware)
        }
        if (withConsequences) {
            if (withVcfInfo) {
                return variantRepository.findUsingGeneIdWithConsAndInfo(geneId, referenceGenome, annotationSoftware)
            } else {
                return variantRepository.findUsingGeneIdWithCons(geneId, referenceGenome, annotationSoftware)
            }
        }
        if (withVcfInfo) {
            return variantRepository.findUsingGeneIdWithInfo(geneId, referenceGenome)
        }
        def result = variantRepository.findUsingGeneId(geneId, referenceGenome)
        return result
    }

    private Set<Variant> fetchVariantsByGeneName(String geneName, String referenceGenome,
                                   Boolean withConsequences, String annotationSoftware,
                                   Boolean withVcfInfo, Boolean withGenotypes) {
        if (withConsequences & withGenotypes) {
            // we will fetch VcfInfo information as well since this case is always VCF output format
            return variantRepository.findUsingGeneNameWithConsAndGeno(geneName, referenceGenome, annotationSoftware)
        }
        if (withConsequences) {
            if (withVcfInfo) {
                return variantRepository.findUsingGeneNameWithConsAndInfo(geneName, referenceGenome, annotationSoftware)
            } else {
                return variantRepository.findUsingGeneNameWithCons(geneName, referenceGenome, annotationSoftware)
            }
        }
        if (withVcfInfo) {
            return variantRepository.findUsingGeneNameWithInfo(geneName, referenceGenome)
        }
        def result = variantRepository.findUsingGeneName(geneName, referenceGenome)
        return result
    }

    /**
     * Get the highest Ensembl version that is available in the store
     * @param sql the sql connection
     * @return the version of the ensembl data available
     */
    private Integer fetchEnsemblVersion() {
        return ensemblRepository.fetchMaxVersion()
    }
}
