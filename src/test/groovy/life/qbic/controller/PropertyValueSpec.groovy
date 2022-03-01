package life.qbic.controller

import io.micronaut.context.ApplicationContext
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject


@MicronautTest(transactional = false)
class PropertyValueSpec {
    @Inject
    ApplicationContext applicationContext

    def "application name is variantstore"() {
        expect:
        applicationContext.getProperty('micronaut.application.name', String).get() == 'variantstore'
    }

    def "test database user is root"() {
        expect:
        applicationContext.getProperty('datasources.default.username', String).get() == 'root'
    }
}
