package life.qbic.controller

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import life.qbic.variantstore.controller.BeaconController
import life.qbic.variantstore.model.BeaconAlleleResponse

import javax.inject.Inject

@MicronautTest(transactional = false)
class BeaconControllerSpec extends TestContainerSpecification{

    @Inject
    ApplicationContext applicationContext

    @Inject
    EmbeddedServer embeddedServer

    @Inject
    @Client('/')
    RxHttpClient httpClient


    void "verify BeaconController bean exists"() {
        given:
        applicationContext

        expect:
        applicationContext.containsBean(BeaconController)
    }


    void "beacon should respond that genotype exists"() {
        when:
        def chr = 12
        def uri = "/beacon/query?assemblyId=GRCh37&chromosome=${chr}&startPosition=46601390&reference=C&observed=G"
        HttpResponse response = httpClient.toBlocking().exchange(uri, BeaconAlleleResponse)

        then:
        response.status == HttpStatus.OK
        response.body.get().exists
    }

    void "beacon should respond that genotype does not exist"() {
        when:
        def chr = 12
        def uri = "/beacon/query?assemblyId=GRCh37&chromosome=${chr}&startPosition=46601391&reference=C&observed=G"
        HttpResponse response = httpClient.toBlocking().exchange(uri, BeaconAlleleResponse)

        then:
        response.status == HttpStatus.OK
        !response.body.get().exists
    }

    void "missing mandatory query values will result in Http 400 Error "() {
        when:
        def chr = 12
        def uri = "/beacon/query?chromosome=${chr}&startPosition=46601391&reference=C&observed=G"
        HttpResponse response = httpClient.toBlocking().exchange(uri, BeaconAlleleResponse)

        then:
        HttpClientResponseException t = thrown(HttpClientResponseException)
        t.getStatus().code == 400
    }
}