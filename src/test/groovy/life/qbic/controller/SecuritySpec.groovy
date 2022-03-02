package life.qbic.controller

import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.context.ApplicationContext
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.rxjava3.http.client.Rx3HttpClient
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification


@MicronautTest(transactional = false)
@Property(name= "micronaut.server.port", value = "-1")
@Property(name= "micronaut.security.enabled", value = "true")
@Property(name= "micronaut.security.oauth2.enabled", value = "false")
class SecuritySpec extends Specification{

    @Inject
    ApplicationContext applicationContext

    @Inject
    EmbeddedServer embeddedServer

    @Inject
    @Client('/')
    Rx3HttpClient httpClient

    def "cases is secured"() {
        when:
        httpClient.toBlocking().exchange(HttpRequest.GET("/cases"))

        then:
        HttpClientResponseException e = thrown()
        e.response.status() == HttpStatus.UNAUTHORIZED
    }

    def "genes is secured"() {
        when:
        httpClient.toBlocking().exchange(HttpRequest.GET("/genes"))

        then:
        HttpClientResponseException e = thrown()
        e.response.status() == HttpStatus.UNAUTHORIZED
    }

    def "samples is secured"() {
        when:
        httpClient.toBlocking().exchange(HttpRequest.GET("/samples"))

        then:
        HttpClientResponseException e = thrown()
        e.response.status() == HttpStatus.UNAUTHORIZED
    }

    def "variants is secured"() {
        when:
        httpClient.toBlocking().exchange(HttpRequest.GET("/variants"))

        then:
        HttpClientResponseException e = thrown()
        e.response.status() == HttpStatus.UNAUTHORIZED
    }
}
