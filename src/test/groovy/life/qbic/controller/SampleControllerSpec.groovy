package life.qbic.controller

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
import life.qbic.oncostore.controller.SampleController
import spock.lang.Specification
import javax.inject.Inject


@MicronautTest(environments=["test"])
class SampleControllerSpec extends Specification{
    @Inject
    ApplicationContext ctx

    @Inject
    EmbeddedServer embeddedServer

    @Inject
    @Client('/')
    HttpClient client

    def "verify SampleController bean exists"() {
        given:
        ctx

        expect:
        ctx.containsBean(SampleController)
    }

    void "samples endpoint should be reachable"() {
        when:
        HttpResponse response = client.toBlocking().exchange("/samples")

        then:
        response.status == HttpStatus.OK
    }

}
