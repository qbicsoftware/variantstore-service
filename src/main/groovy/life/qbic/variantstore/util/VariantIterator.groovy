package life.qbic.variantstore.util

import htsjdk.samtools.util.CloseableIterator
import htsjdk.variant.variantcontext.VariantContext
import life.qbic.variantstore.model.SimpleVariantContext
import life.qbic.variantstore.model.Variant

/**
 * An iterator class for variants
 *
 * @since: 1.0.0
 */
class VariantIterator implements CloseableIterator<SimpleVariantContext> {

    /**
     * The closeable iterator of type VariantContext
     */
    private final CloseableIterator<VariantContext> variantContext
    /**
     * The annotation software that is used in this context
     */
    private final String annotationType

    VariantIterator(CloseableIterator<VariantContext> variantContext) {
        this.variantContext = variantContext
    }

    VariantIterator(CloseableIterator<VariantContext> variantContext, String annotationType) {
        this.variantContext = variantContext
        this.annotationType = annotationType
    }

    @Override
    boolean hasNext() {
        return variantContext.hasNext()
    }

    @Override
    SimpleVariantContext next() {
        return new Variant(variantContext.next(), annotationType)
    }

    @Override
    void close() {
        variantContext.close()
    }

    @Override
    void remove() {
        variantContext.remove()
    }
}
