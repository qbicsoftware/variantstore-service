package life.qbic.controller

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import life.qbic.micronaututils.QBiCDataSource
import life.qbic.variantstore.service.VariantstoreStorage
import javax.inject.Inject

@MicronautTest(transactional = false)
class DatabaseSpec extends TestContainerSpecification {

    @Inject
    VariantstoreStorage storage

    def "confirm that storage connection is alive"() {
        when:
        QBiCDataSource dataSource = storage.dataSource

        then:
        dataSource
        dataSource.connection
    }
}

