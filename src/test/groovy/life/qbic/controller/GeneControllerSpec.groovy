package life.qbic.controller

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
import life.qbic.oncostore.controller.GeneController
import life.qbic.oncostore.controller.VariantController
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Inject

@MicronautTest
class GeneControllerSpec extends Specification{
    @Inject
    ApplicationContext ctx

    @Inject
    EmbeddedServer embeddedServer

    @Inject
    @Client('/')
    HttpClient client

    def "verify GeneController bean exists"() {
        given:
        ctx

        expect:
        ctx.containsBean(GeneController)
    }

    void "should be reachable and return all genes"() {
        when:
        HttpResponse response = client.toBlocking().exchange("/genes", List)

        then:
        response.status == HttpStatus.OK
        response.body().size() == 96
    }

    @Unroll
    void "should return Http 200 for given gene identifier if available "() {
        when:
        HttpResponse response = client.toBlocking().exchange("/genes/${identifier}")

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
        client.toBlocking().exchange("/genes/${identifier}", HttpResponse)

        then:
        HttpClientResponseException t = thrown(HttpClientResponseException)
        t.getStatus().code == 404
    }

}