package life.qbic.controller


trait ConfigurationFixture implements MariaDbContainerFixture{

    Map<String, Object> getConfiguratiion() {
        Map<String, Object> m = [:]

        if (specName)  {
            m['spec.name'] = specName
        }

        m += mariaDbConfiguration

        m
    }

    String getSpecName() {
        null
    }
}