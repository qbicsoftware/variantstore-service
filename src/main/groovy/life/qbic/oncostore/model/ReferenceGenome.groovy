package life.qbic.oncostore.model

class ReferenceGenome {

    final String source
    final String build
    final String version

    ReferenceGenome(String source, String build, String version) {
        this.source = source
        this.build = build
        this.version = version
    }

    String getSource() {
        return source
    }

    String getBuild() {
        return build
    }

    String getVersion() {
        return version
    }
}
