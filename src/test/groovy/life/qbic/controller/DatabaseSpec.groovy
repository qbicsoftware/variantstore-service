package life.qbic.controller

import groovy.sql.Sql
import io.micronaut.test.annotation.MicronautTest
import life.qbic.micronaututils.DataSource
import life.qbic.oncostore.service.VariantstoreStorage
import spock.lang.Specification
import javax.inject.Inject

@MicronautTest(environments=['test'])
class DatabaseSpec extends Specification {

    @Inject
    VariantstoreStorage storage


    def "confirm that storage connection is alive"() {
        when:
        DataSource dataSource = storage.dataSource

        then:
        dataSource
        dataSource.connection
        dataSource.connection.isValid(10)
    }

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
}

