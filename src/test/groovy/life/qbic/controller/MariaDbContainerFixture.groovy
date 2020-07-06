package life.qbic.controller

import io.micronaut.data.model.query.builder.sql.Dialect
import org.testcontainers.containers.MariaDBContainer

trait MariaDbContainerFixture {

    Map<String, Object> getMariaDbConfiguration() {
        if (MariaDb.mariaDBContainer == null || !MariaDb.mariaDBContainer.isRunning()) {
            MariaDb.init()
            println(MariaDb.mariaDBContainer.getJdbcUrl())
        }
            [
                    'datasources.default.url': MariaDb.mariaDBContainer.getJdbcUrl(),
                    'datasources.default.username': MariaDb.mariaDBContainer.getUsername(),
                    'datasources.default.password': MariaDb.mariaDBContainer.getPassword(),
                    'datasources.default.driverClassName': MariaDb.mariaDBContainer.getDriverClassName(),
            ]
        }
}
