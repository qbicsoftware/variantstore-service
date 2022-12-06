package life.qbic.controller

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.rxjava3.http.client.Rx3HttpClient
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import life.qbic.variantstore.controller.GeneController
import spock.lang.Specification
import spock.lang.Unroll


@MicronautTest(transactional = false)
class GeneControllerSpec extends Specification{

    @Inject
    ApplicationContext applicationContext

    @Inject
    EmbeddedServer embeddedServer

    @Inject
    @Client('/')
    Rx3HttpClient httpClient


    def "verify GeneController bean exists"() {
        given:
        applicationContext

        expect:
        applicationContext.containsBean(GeneController)
    }

    void "should be reachable and return all genes"() {
        when:
        HttpResponse response = httpClient.toBlocking().exchange("/genes", List)

        then:
        response.status == HttpStatus.OK
        response.body().size() == 16
    }

    @Unroll
    void "should return Http 200 for given gene identifier if available "() {
        when:
        HttpResponse response = httpClient.toBlocking().exchange("/genes/${identifier}")

        then:
        response.status == status

        where:
        identifier || status
        "ENSG00000073146"   ||   HttpStatus.OK
        "ENSG00000150471"   ||  HttpStatus.OK
    }


    @Unroll
    void "should return Http 404 for given gene identifier if not available "() {
        when:
        def identifier = "ENSG00000150499"
        HttpResponse response = httpClient.toBlocking().exchange("/genes/${identifier}")

        then:
        HttpClientResponseException e = thrown(HttpClientResponseException)
        e.status == HttpStatus.NOT_FOUND
    }
}
