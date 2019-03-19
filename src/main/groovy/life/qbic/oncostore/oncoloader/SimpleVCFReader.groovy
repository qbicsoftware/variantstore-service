package life.qbic.oncostore.oncoloader

import htsjdk.samtools.util.CloseableIterator
import htsjdk.variant.vcf.VCFFileReader
import life.qbic.oncostore.model.SimpleVariantContext
import life.qbic.oncostore.util.VariantIterator


/**
 * Implementation of the VCFReader interface.
 *
 * It serves as an adapter class to the more complex
 * VCFFileReader from htsjdk (htsjdk.variant.vcf.VCFFileReader).
 *
 * It parses a given VCF file and returns its content as
 * Iterator instance of type SimpleVariantContext, which itself
 * is a simplified adapter class of the more complex VariantContext
 * from htsjdk.
 */
class SimpleVCFReader implements VCFReader{

    private final CloseableIterator<SimpleVariantContext> variantIterator
    private final VCFFileReader reader

    /**
     * Constructs a SimpleVCFReader.
     */
    public SimpleVCFReader(VCFFileReader reader) {
        this.reader = reader
        this.variantIterator = new VariantIterator(reader.iterator())
    }

    @Override
    CloseableIterator<SimpleVariantContext> iterator() {
        variantIterator
    }
}