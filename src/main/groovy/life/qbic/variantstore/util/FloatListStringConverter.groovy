package life.qbic.variantstore.util

import io.micronaut.core.convert.ConversionContext
import io.micronaut.data.model.runtime.convert.AttributeConverter
import java.util.stream.Collectors
import jakarta.inject.Singleton

/**
 * Float to List<String> converter
 *
 * @since: 1.1.0
 */
@Singleton
class FloatListStringConverter implements AttributeConverter<List<Float>, String> {

    @Override
    String convertToPersistedValue(List<Float> entityValue, ConversionContext context) {
        def formattedValue = entityValue.toString()
                .replace("[", "")
                .replace("]", "")
                .replace(" ", "")
        return entityValue ? formattedValue : ""
    }

    @Override
    List<Float> convertToEntityValue(String persistedValue, ConversionContext context) {
        return (persistedValue || !persistedValue.empty) ? Arrays.asList(persistedValue.split(",")).stream().map(Float::parseFloat).collect(Collectors.toList()) : []
    }
}
