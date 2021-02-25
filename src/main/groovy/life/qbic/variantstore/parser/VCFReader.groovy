package life.qbic.variantstore.parser

import htsjdk.samtools.util.CloseableIterator
import life.qbic.variantstore.model.SimpleVariantContext

/**
 * Simplified interface to parse VCF files.
 *
 * Should only provide access to parsed VCF files, namely the variant context in each line.
 *
 * @since: 1.0.0
 */
interface VCFReader {

    CloseableIterator<SimpleVariantContext> iterator()
}