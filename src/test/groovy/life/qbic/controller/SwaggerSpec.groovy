package life.qbic.controller

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest

import javax.inject.Inject

@MicronautTest(transactional = false)
@Property(name= "micronaut.server.port", value = "-1")
class SwaggerSpec extends TestContainerSpecification{

    @Inject
    ApplicationContext applicationContext

    @Inject
    EmbeddedServer embeddedServer

    @Inject
    @Client('/')
    RxHttpClient httpClient

    def "swagger YAML is exposed"() {
        when:
        HttpResponse response = httpClient.toBlocking().exchange(HttpRequest.GET("/swagger/variantstore-0.6.yml"))

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
