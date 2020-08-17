package life.qbic.oncostore.util

// https://loinc.org/48006-1/
enum ConsequenceTypesLoinc {

    WILD_TYPE("LA9658-1"), DELETION("LA6692-3"), DUPLICATION("LA6686-5"), FRAMESHIFT("LA6694-9"),
    INITIATING_METHIONINE("LA6695-6"), INSERTION("LA6687-3"), INSERTION_AND_DELETION("LA9659-9"),
    MISSENSE("LA6698-0"), NONSENSE("LA6699-8"), SILENT("LA6700-4"), STOP_CODON_MUTATION("LA6701-2")

    private final String tag

    ConsequenceTypesLoinc(String tag) {
        this.tag = tag
    }

    String getTag() {
        tag
    }
}