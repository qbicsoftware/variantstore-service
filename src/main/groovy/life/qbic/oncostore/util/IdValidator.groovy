package life.qbic.oncostore.util

import java.util.regex.Matcher
import java.util.regex.Pattern


class IdValidator {

    private static final Pattern VALID_QBIC_SAMPLE_CODE = Pattern.compile('(Q[A-Z0-9]{4}[0-9]{3}[A-Z][A-Z0-9]$)|(Q[A-Z0-9]{4}ENTITY-[0-9]*$)', Pattern.CASE_INSENSITIVE);
    private static final Pattern VALID_UUID = Pattern.compile('/^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$/', Pattern.CASE_INSENSITIVE)

    public static isValidSampleCode(String code) {
        Matcher matcher = VALID_QBIC_SAMPLE_CODE.matcher(code)
        return matcher.find()
    }

    public static isValidUUID(String identifier) {
        def matcher = VALID_UUID.matcher(identifier)
        return matcher.find()
    }
}