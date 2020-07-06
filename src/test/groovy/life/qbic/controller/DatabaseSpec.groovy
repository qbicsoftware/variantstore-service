package life.qbic.controller

import groovy.sql.Sql
import io.micronaut.test.annotation.MicronautTest
import io.micronaut.transaction.TransactionDefinition
import life.qbic.micronaututils.QBiCDataSource
import life.qbic.oncostore.service.VariantstoreStorage
import io.micronaut.transaction.annotation.TransactionalAdvice

import javax.inject.Inject
import javax.transaction.Transactional

@MicronautTest(transactional = false)
class DatabaseSpec extends TestcontainerSpecification {

    @Inject
    VariantstoreStorage storage

    def "confirm that storage connection is alive"() {
        when:
        QBiCDataSource dataSource = storage.dataSource
        println(dataSource)

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

