package life.qbic.controller

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
import life.qbic.oncostore.controller.VariantController
import spock.lang.Unroll

import javax.inject.Inject

@MicronautTest(transactional = false)
class VariantControllerSpec extends TestcontainerSpecification{

    @Inject
    private ApplicationContext applicationContext

    @Inject
    EmbeddedServer embeddedServer

    @Inject
    @Client('/')
    RxHttpClient httpClient

    def "verify VariantController bean exists"() {
        given:
        applicationContext

        expect:
        applicationContext.containsBean(VariantController)
    }

    void "should be reachable and return all variants"() {
        when:
        HttpResponse response = httpClient.toBlocking().exchange("/variants", List)

        then:
        response.status == HttpStatus.OK
        response.body().size() == 14
    }

    /*
    @Unroll
    void "should return Http 200 for given variant identifier if available "() {
        when:
        HttpResponse response = client.toBlocking().exchange("/variants/${identifier}")

        then:
        response.status == status

        where:
        identifier || status
        "04e1e278-3f46-45cc-9a50-458f76b73513"   ||   HttpStatus.OK
        "2f8fdbe9-12ec-4b98-9ce7-db2b6dfac780"   ||  HttpStatus.OK
    }

*/

    @Unroll
    void "should return Http 404 for given variant identifier if not available "() {
        when:
        def identifier = "2f8fdbe9-12ec-4b98-9ce7-db2b6dfac789"
        httpClient.toBlocking().exchange("/variants/${identifier}", HttpResponse)

        then:
        HttpClientResponseException t = thrown(HttpClientResponseException)
        t.getStatus() == HttpStatus.NOT_FOUND
    }
}