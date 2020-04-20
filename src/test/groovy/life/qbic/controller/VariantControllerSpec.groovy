package life.qbic.controller

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Property
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MicronautTest
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import life.qbic.oncostore.controller.VariantController
import spock.lang.*
import javax.inject.Inject

@MicronautTest(environments=['test'])
class VariantControllerSpec extends Specification{
    @Inject
    ApplicationContext ctx

    @Inject
    EmbeddedServer embeddedServer

    @Inject
    @Client('/')
    HttpClient client

    def "verify VariantController bean exists"() {
        given:
        ctx

        expect:
        ctx.containsBean(VariantController)
    }

    void "variants endpoint should be reachable"() {
        when:
        HttpResponse response = client.toBlocking().exchange("/variants")

        then:
        response.status == HttpStatus.OK
    }
}