package life.qbic.controller

import io.micronaut.test.support.TestPropertyProvider
import org.testcontainers.containers.MariaDBContainer
import spock.lang.Specification

abstract class TestcontainerSpecification extends Specification implements TestPropertyProvider{

    static MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>("mariadb:10.4")
            .withInitScript('variantstore_test_db.sql')
            .withDatabaseName('oncostore')
            .withUsername('root')
            .withPassword('')
            .withReuse(true)
            .withNetwork(null)


    static {
        mariaDBContainer
                .start()
    }

    @Override
    Map<String, String> getProperties() {
        def properties = ["datasources.default.url": mariaDBContainer.jdbcUrl,
                          "datasources.default.driverClassName": mariaDBContainer.driverClassName,
                          "datasources.default.username": mariaDBContainer.username,
                          "datasources.default.password": mariaDBContainer.password,

                          "datasources.transactions.url": mariaDBContainer.jdbcUrl,
                          "datasources.transactions.driverClassName": mariaDBContainer.driverClassName,
                          "datasources.transactions.username": mariaDBContainer.username,
                          "datasources.transactions.password": mariaDBContainer.password
        ]
        return properties
    }
}