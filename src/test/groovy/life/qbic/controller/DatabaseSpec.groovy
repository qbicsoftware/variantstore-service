package life.qbic.controller

import groovy.sql.Sql
import io.micronaut.test.annotation.MicronautTest
import life.qbic.micronaututils.QBiCDataSource
import life.qbic.oncostore.service.VariantstoreStorage

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


/*
    def "confirm that database was setup"() {
        given:
        Sql sql = new Sql(storage.dataSource.connection)

        when:
        def result = sql.rows ("Show tables")

        then:
        result
    }

    def "confirm that the number of variants is 14"() {
        given:
        Sql sql = new Sql(storage.dataSource.connection)

        when:
        def result = sql.rows ("Select * from variant")

        then:
        result.size() == 14
    }
*/
}

