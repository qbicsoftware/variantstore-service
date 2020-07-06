package life.qbic.controller

import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.context.ApplicationContext
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject

@MicronautTest(transactional = false)
@Property(name= "micronaut.server.port", value = "-1")
@Property(name= "micronaut.security.enabled", value = "true")
@Property(name= "micronaut.security.oauth2.enabled", value = "false")
class SecuritySpec extends TestcontainerSpecification{

    @Inject
    ApplicationContext applicationContext

    @Inject
    EmbeddedServer embeddedServer

    @Inject
    @Client('/')
    RxHttpClient httpClient

    def "/cases is secured"() {
        when:
        httpClient.toBlocking().exchange(HttpRequest.GET("/cases"))

        then:
        HttpClientResponseException e = thrown()
        e.response.status() == HttpStatus.UNAUTHORIZED
    }

    def "/genes is secured"() {
        when:
        httpClient.toBlocking().exchange(HttpRequest.GET("/genes"))

        then:
        HttpClientResponseException e = thrown()
        e.response.status() == HttpStatus.UNAUTHORIZED
    }

    def "/samples is secured"() {
        when:
        httpClient.toBlocking().exchange(HttpRequest.GET("/samples"))

        then:
        HttpClientResponseException e = thrown()
        e.response.status() == HttpStatus.UNAUTHORIZED
    }

    def "/variants is secured"() {
        when:
        httpClient.toBlocking().exchange(HttpRequest.GET("/variants"))

        then:
        HttpClientResponseException e = thrown()
        e.response.status() == HttpStatus.UNAUTHORIZED
    }
}
