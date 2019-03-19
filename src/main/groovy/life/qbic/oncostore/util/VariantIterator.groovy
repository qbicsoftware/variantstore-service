package life.qbic.oncostore.util

import htsjdk.samtools.util.CloseableIterator
import htsjdk.variant.variantcontext.VariantContext
import life.qbic.oncostore.model.SimpleVariant
import life.qbic.oncostore.model.SimpleVariantContext

import java.util.stream.Stream

public class VariantIterator implements CloseableIterator<SimpleVariantContext> {

    private final CloseableIterator<VariantContext> variantContext

    public VariantIterator(CloseableIterator<VariantContext> variantContext) {
        this.variantContext = variantContext
    }

    @Override
    public boolean hasNext() {
        return variantContext.hasNext()
    }

    @Override
    public SimpleVariantContext next() {
        return new SimpleVariant(variantContext.next())
    }

    @Override
    public void close() {
        variantContext.close()
    }

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
}
