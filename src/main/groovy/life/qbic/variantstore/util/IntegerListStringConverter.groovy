package life.qbic.variantstore.util

import io.micronaut.core.convert.ConversionContext
import io.micronaut.data.model.runtime.convert.AttributeConverter
import jakarta.inject.Singleton
import java.util.stream.Collectors

/**
 *
 *
 * @since: 1.1.0
 */
@Singleton
class IntegerListStringConverter implements AttributeConverter<List<Integer>, String> {

    @Override
    String convertToPersistedValue(List<Integer> entityValue, ConversionContext context) {
        def formattedValue = entityValue.toString()
                .replace("[", "")
                .replace("]", "")
                .replace(" ", "")
        return entityValue ? formattedValue : ""
    }

    @Override
    List<Integer> convertToEntityValue(String persistedValue, ConversionContext context) {
        return (persistedValue && !persistedValue.empty) ? Arrays.asList(persistedValue.split(",")).stream().map(Integer::parseInt).collect(Collectors.toList()) : []
    }
}
