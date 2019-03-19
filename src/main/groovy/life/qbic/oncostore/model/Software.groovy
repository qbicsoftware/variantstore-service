package life.qbic.oncostore.model


interface Software {

    final String name
    final String version
    final String doi

    String getName()

    String getVersion()

    String getDoi()
}