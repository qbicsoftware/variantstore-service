package life.qbic.oncostore.parser

import htsjdk.samtools.util.CloseableIterator
import htsjdk.variant.variantcontext.VariantContext
import htsjdk.variant.vcf.VCFFileReader
import htsjdk.variant.vcf.VCFIterator
import htsjdk.variant.vcf.VCFIteratorBuilder
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

    public SimpleVCFReader(InputStream inputStream) {
        VCFIterator iter = new VCFIteratorBuilder().open(inputStream)
        this.variantIterator = new VariantIterator((CloseableIterator<VariantContext>) iter.iterator())
    }

    @Override
    CloseableIterator<SimpleVariantContext> iterator() {
        variantIterator
    }
}