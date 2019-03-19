package life.qbic.oncostore.oncoloader

import htsjdk.samtools.util.CloseableIterator
import life.qbic.oncostore.model.SimpleVariantContext

/**
 * Simplified interface to parse VCF files.
 *
 * Should only provide access to parsed VCF files,
 * namely the variant context in each line.
 */
interface VCFReader {

    CloseableIterator<SimpleVariantContext> iterator()

}