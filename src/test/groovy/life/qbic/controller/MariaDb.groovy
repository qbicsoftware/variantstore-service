package life.qbic.controller

import org.testcontainers.containers.MariaDBContainer

class MariaDb {

    static MariaDBContainer mariaDBContainer

    static init() {
        if (mariaDBContainer == null) {
            mariaDBContainer = new MariaDBContainer()
                    .withDatabaseName('variantstoreTestDb')
                    .withUsername('test')
                    .withPassword('test')

            mariaDBContainer
                    .withNetwork(null)
                    .withReuse(true)
                    .start()
        }
    }
}
