package life.qbic.controller

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
import life.qbic.oncostore.controller.SampleController
import spock.lang.Unroll

import javax.inject.Inject

@MicronautTest(transactional = false)
class SampleControllerSpec extends TestcontainerSpecification {

    @Inject
    ApplicationContext applicationContext

    @Inject
    EmbeddedServer embeddedServer

    @Inject
    @Client('/')
    RxHttpClient httpClient

    def "verify SampleController bean exists"() {
        given:
        applicationContext

        expect:
        applicationContext.containsBean(SampleController)
    }

    void "should be reachable and return all samples"() {
        when:
        HttpResponse response = httpClient.toBlocking().exchange("/samples", List)

        then:
        response.status == HttpStatus.OK
        response.body().size() == 3
    }

    @Unroll
    void "should return Http 200 for given sample identifier if available "() {
        when:
        HttpResponse response = httpClient.toBlocking().exchange("/samples/${identifier}")

        then:
        response.status == status

        where:
        identifier || status
        "QTEST001AL" || HttpStatus.OK
        "QTEST002AT" || HttpStatus.OK
    }

    void "should return Http 404 for given samples identifier if not available "() {
        when:
        def identifier = "QTEST001XX"
        httpClient.toBlocking().exchange("/samples/${identifier}", HttpResponse)

        then:
        HttpClientResponseException t = thrown(HttpClientResponseException)
        t.getStatus().code == 404
    }

}
