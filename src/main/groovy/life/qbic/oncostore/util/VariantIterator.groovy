package life.qbic.oncostore.util

import htsjdk.samtools.util.CloseableIterator
import htsjdk.variant.variantcontext.VariantContext
import life.qbic.oncostore.model.SimpleVariantContext
import life.qbic.oncostore.model.Variant

public class VariantIterator implements CloseableIterator<SimpleVariantContext> {

    private final CloseableIterator<VariantContext> variantContext
    private final String annotationType

    public VariantIterator(CloseableIterator<VariantContext> variantContext) {
        this.variantContext = variantContext
    }

    public VariantIterator(CloseableIterator<VariantContext> variantContext, String annotationType) {
        this.variantContext = variantContext
        this.annotationType = annotationType
    }

    @Override
    public boolean hasNext() {
        return variantContext.hasNext()
    }

    @Override
    public SimpleVariantContext next() {
        return new Variant(variantContext.next(), annotationType)
    }

    @Override
    public void close() {
        variantContext.close()
    }

    @Override
    public void remove() {
        variantContext.remove()
    }

/*
    @Override
    public List<SimpleVariantContext> toList() {
        return variantContext.toList().collect{
            new SimpleVariant(it)
        }
    }

    @Override
    public Stream<SimpleVariantContext> stream() {
        return (Stream<SimpleVariantContext>) variantContext.stream().collect{
            new SimpleVariant(it)
        }
    }

 */

}
