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
import life.qbic.variantstore.controller.ProjectController
import spock.lang.Specification
import spock.lang.Unroll

@MicronautTest(transactional = false)
class ProjectControllerSpec extends Specification{

    @Inject
    ApplicationContext applicationContext

    @Inject
    EmbeddedServer embeddedServer

    @Inject
    @Client('/')
    Rx3HttpClient httpClient

    def "verify ProjectController bean exists"() {
        given:
        applicationContext

        expect:
        applicationContext.containsBean(ProjectController)
    }

    @Unroll
    void "should be reachable and return one project"() {
        when:
        HttpResponse response = httpClient.toBlocking().exchange("/projects", List)

        then:
        response.status == HttpStatus.OK
        response.body().size() == 1
    }

    @Unroll
    void "should return Http 200 for given case identifier if available "() {
        when:
        HttpResponse response = httpClient.toBlocking().exchange("/projects/${identifier}")

        then:
        response.status == status

        where:
        identifier || status
        "PROJ"   ||   HttpStatus.OK
    }

    @Unroll
    void "should return Http 404 for given case identifier if not available "() {
        when:
        def identifier = "PROJ2"
        httpClient.toBlocking().exchange("/projects/${identifier}", HttpResponse)

        then:
        HttpClientResponseException t = thrown(HttpClientResponseException)
        t.getStatus().code == 404
    }
}
