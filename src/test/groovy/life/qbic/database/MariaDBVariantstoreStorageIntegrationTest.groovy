package life.qbic.database

import io.micronaut.test.annotation.MicronautTest
import life.qbic.oncostore.service.VariantstoreStorage
import spock.lang.Specification

import javax.inject.Inject

@MicronautTest(environments=['test'])
class MariaDBVariantstoreStorageIntegrationTest extends Specification {

    @Inject
    VariantstoreStorage storage

    def "confirm that storage connection is alive"() {
        when:
        def dataSource = storage.dataSource.connection

        then:
        assert dataSource
    }
}
