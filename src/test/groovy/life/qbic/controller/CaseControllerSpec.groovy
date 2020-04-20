package life.qbic.controller

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
import life.qbic.oncostore.controller.CaseController
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Inject

@MicronautTest(environments=['test'])
class CaseControllerSpec extends Specification{
    @Inject
    ApplicationContext ctx

    @Inject
    EmbeddedServer embeddedServer

    @Inject
    @Client('/')
    HttpClient client

    def "verify CaseController bean exists"() {
        given:
        ctx

        expect:
        ctx.containsBean(CaseController)
    }

    @Unroll
    void "should be reachable and return all three cases"() {
        when:
        HttpResponse response = client.toBlocking().exchange("/cases", List)

        then:
        response.status == HttpStatus.OK
        response.body().size() == 3
    }

    @Unroll
    void "should return Http 200 for given case identifier if available "() {
        when:
        HttpResponse response = client.toBlocking().exchange("/cases/${identifier}")

        then:
        response.status == status

        where:
        identifier || status
        "patient1"   ||   HttpStatus.OK
        "patient2"   ||  HttpStatus.OK
    }

    @Unroll
    void "should return Http 404 for given case identifier if not available "() {
        when:
        def identifier = "patientX"
        client.toBlocking().exchange("/cases/${identifier}", HttpResponse)

        then:
        HttpClientResponseException t = thrown(HttpClientResponseException)
        t.getStatus().code == 404
    }

    @Unroll
    void "should return the right number of cases dependent of filtering for chromosome"() {
        when:
        HttpResponse response = client.toBlocking().exchange("/cases?chromosome=${chr}", List)

        then:
        response.status == HttpStatus.OK
        response.body().size() == size

        where:
        chr | size
        1   |   0
        4   |   2
        15  |   1
        "X" |   2
    }
}