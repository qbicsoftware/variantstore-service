package life.qbic.controller

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.annotation.Client
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.rxjava3.http.client.Rx3HttpClient
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification


@MicronautTest(transactional = false)
@Property(name= "micronaut.server.port", value = "-1")
class SwaggerSpec extends Specification{

    @Inject
    ApplicationContext applicationContext

    @Inject
    EmbeddedServer embeddedServer

    @Inject
    @Client('/')
    Rx3HttpClient httpClient

    def "swagger YAML is exposed"() {
        when:
        HttpResponse response = httpClient.toBlocking().exchange(HttpRequest.GET("/swagger/variantstore-1.1.0-SNAPSHOT.yml"))

        then:
        response.status() == HttpStatus.OK
    }

    def "swagger UI is exposed"() {
        when:
        HttpResponse response = httpClient.toBlocking().exchange(HttpRequest.GET("/swagger-ui/index.html"))

        then:
        response.status() == HttpStatus.OK
    }

    def "rapidoc UI is exposed"() {
        when:
        HttpResponse response = httpClient.toBlocking().exchange(HttpRequest.GET("/rapidoc/index.html"))

        then:
        response.status() == HttpStatus.OK
    }
}
