package life.qbic.oncostore.model

class VariantCaller implements Software {

    final String name
    final String version
    final String doi

    VariantCaller(String name, String version, String doi) {
        this.name = name
        this.version = version
        this.doi = doi
    }

    @Override
    String getName() {
        return name
    }

    @Override
    String getVersion() {
        return version
    }

    @Override
    String getDoi() {
        return doi
    }
}
