package life.qbic.db

import io.micronaut.test.support.TestPropertyProvider
import org.flywaydb.core.Flyway
import org.testcontainers.containers.PostgreSQLContainer
import spock.lang.Specification

import javax.inject.Inject

abstract class PostgresTestContainerSpecification extends Specification implements TestPropertyProvider {


    private static PostgreSQLContainer<?> container =
            new PostgreSQLContainer<>("postgres:13.2")
                    .withInitScript('V1__variantstore-postgres-test.sql')
                    .withDatabaseName('variantstore_db')
                    .withUsername('mohr')
                    .withPassword('password')
                    //.withReuse(true)
                    .withNetwork(null)

    static {
        container
                .start()
    }

        //Flyway.configure()
        //        .dataSource(container.jdbcUrl, container.username, container.password)
        //        .load().migrate()
    //}

    @Override
    Map<String, String> getProperties() {
        HashMap<String, String> properties = new HashMap<>();
        properties.put("datasources.variantstore_postgres.url", container.jdbcUrl);
        properties.put("datasources.variantstore_postgres.username", container.username);
        properties.put("datasources.variantstore_postgres.password", container.password);
        properties.put("datasources.variantstore_postgres.dialect", "POSTGRES");
        properties.put("datasources.variantstore_postgres.driverClassName", container.driverClassName);
        return properties
    }
}