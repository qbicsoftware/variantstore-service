package life.qbic.variantstore.util

import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class IntegerToListConverter implements AttributeConverter<List<Integer>, String> {

    @Override
    String convertToDatabaseColumn(List<Integer> list) {
        if (list == null) return ""
        return list.join(",")
    }

    @Override
    List<Integer> convertToEntityAttribute(String joined) {
        if (joined == null) return new ArrayList<>()
        return new ArrayList<>(Arrays.asList(joined.split(",")))
    }
}