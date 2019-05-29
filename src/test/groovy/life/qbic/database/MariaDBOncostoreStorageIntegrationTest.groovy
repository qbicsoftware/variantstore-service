package life.qbic.database

import io.micronaut.test.annotation.MicronautTest

import life.qbic.oncostore.database.MariaDBOncostoreStorage

import spock.lang.Specification

import javax.inject.Inject

@MicronautTest(environments=['test'])
class MariaDBOncostoreStorageIntegrationTest extends Specification {

    @Inject
    MariaDBOncostoreStorage storage

    def "confirm that storage connection is alive"() {
        when:
        def dataSource = storage.dataSource

        then:
        assert dataSource
    }
}
