package life.qbic.controller

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.test.support.TestPropertyProvider

trait MariaDbTestPropertyProvider implements TestPropertyProvider{

    @Override
    Map<String, String> getProperties() {
        [
                "datasources.default.url": MariaDb.mariaDBContainer.getJdbcUrl(),
                "datasources.default.username": MariaDb.mariaDBContainer.getUsername(),
                "datasources.default.password": MariaDb.mariaDBContainer.getPassword(),
                "datasources.default.driverClassName": MariaDb.mariaDBContainer.getDriverClassName(),
                "datasources.default.dialect": Dialect.MYSQL
        ] as Map<String, String>
    }
}