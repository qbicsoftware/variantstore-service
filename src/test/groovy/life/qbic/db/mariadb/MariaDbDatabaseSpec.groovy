package life.qbic.db.mariadb

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import life.qbic.variantstore.service.VariantstoreStorage
import spock.lang.Specification

@MicronautTest(transactional = false)
class MariaDbDatabaseSpec extends Specification {

    @Inject
    VariantstoreStorage storage

    def "confirm that storage connection is alive"() {
        when:
        storage.dataSource

        then:
        storage.dataSource.connection
    }
}
