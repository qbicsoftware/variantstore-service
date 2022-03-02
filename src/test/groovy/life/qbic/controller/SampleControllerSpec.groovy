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
import life.qbic.variantstore.controller.SampleController
import life.qbic.variantstore.model.Sample
import spock.lang.Specification
import spock.lang.Unroll


@MicronautTest(transactional = false)
class SampleControllerSpec extends Specification {

    @Inject
    ApplicationContext applicationContext

    @Inject
    EmbeddedServer embeddedServer

    @Inject
    @Client('/')
    Rx3HttpClient httpClient

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
        identifier   || status
        "QTEST001AL" || HttpStatus.OK
        "QTEST002AT" || HttpStatus.OK
    }

    @Unroll
    void "should return sample based on specified cancer entity"() {
        when:
        HttpResponse response = httpClient.toBlocking().exchange("/samples?cancerEntity=${cancerEntity}", List<Sample>)

        then:
        Sample sample = response.body.get()[0]
        sample.identifier == identifier

        where:
        cancerEntity   || identifier
        "HCC" || "QTEST001AL"
        "ALL" || "QTEST003A3"
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
