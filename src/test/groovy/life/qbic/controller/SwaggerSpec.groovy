package life.qbic.controller

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.annotation.Client
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.http.client.HttpClient
import io.micronaut.test.annotation.MicronautTest
import spock.lang.*

import javax.inject.Inject

@MicronautTest
@Property(name= "micronaut.server.port", value = "-1")
class SwaggerSpec extends Specification{

    @Inject
    ApplicationContext ctx

    @Inject
    EmbeddedServer embeddedServer

    @Inject
    @Client('/')
    HttpClient client

    def "swagger YAML is exposed"() {
        when:
        HttpResponse response = client.toBlocking().exchange(HttpRequest.GET("/swagger/variantstore-0.6.yml"))

        then:
        response.status() == HttpStatus.OK
    }

    def "swagger UI is exposed"() {
        when:
        HttpResponse response = client.toBlocking().exchange(HttpRequest.GET("/swagger-ui/index.html"))

        then:
        response.status() == HttpStatus.OK
    }

    def "rapidoc UI is exposed"() {
        when:
        HttpResponse response = client.toBlocking().exchange(HttpRequest.GET("/rapidoc/index.html"))

        then:
        response.status() == HttpStatus.OK
    }
}
