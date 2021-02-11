package life.qbic.oncostore.parser

import htsjdk.samtools.util.CloseableIterator
import htsjdk.variant.variantcontext.VariantContext
import htsjdk.variant.vcf.VCFFileReader
import htsjdk.variant.vcf.VCFIteratorBuilder
import life.qbic.oncostore.model.SimpleVariantContext
import life.qbic.oncostore.util.VariantIterator

/**
 * An implementation of the VCFReader interface.
 *
 * It serves as an adapter class to the more complex
 * VCFFileReader from htsjdk (htsjdk.variant.vcf.VCFFileReader).
 *
 * It parses a given VCF file and returns its content as
 * Iterator instance of type {@SimpleVariantContext}, which itself
 * is a simplified adapter class of the more complex VariantContext
 * from the library htsjdk.
 *
 * @since: 1.0.0
 */
class SimpleVCFReader implements VCFReader{

    /**
     * The Iterator instance of type {@SimpleVariantContext}.
     */
    private final CloseableIterator<SimpleVariantContext> variantIterator
    /**
     * The VCFFileReader instance.
     */
    private final VCFFileReader reader

    public SimpleVCFReader(VCFFileReader reader) {
        this.reader = reader
        this.variantIterator = new VariantIterator(reader.iterator())
    }

    public SimpleVCFReader(InputStream inputStream, String annotationType) {
        this.variantIterator = new VariantIterator((CloseableIterator<VariantContext>) new VCFIteratorBuilder().open(inputStream).iterator(), annotationType)
    }

    public SimpleVCFReader(InputStream inputStream) {
        this.variantIterator = new VariantIterator((CloseableIterator<VariantContext>) new VCFIteratorBuilder().open(inputStream).iterator())
    }

    @Override
    CloseableIterator<SimpleVariantContext> iterator() {
        variantIterator
    }

    void close() {
        try {
            if (reader != null) {
                reader.close()}
            this.variantIterator.close()
        } catch (final IOException ioe) {
            ioe.printStackTrace()
        }
    }
}