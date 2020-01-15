package life.qbic.database

import io.micronaut.test.annotation.MicronautTest

import life.qbic.oncostore.service.OncostoreStorage
import spock.lang.Specification

import javax.inject.Inject

@MicronautTest(environments=['test'])
class MariaDBOncostoreStorageIntegrationTest extends Specification {

    @Inject
    OncostoreStorage storage

    def "confirm that storage connection is alive"() {
        when:
        def dataSource = storage.dataSource.connection

        then:
        assert dataSource
    }
}
