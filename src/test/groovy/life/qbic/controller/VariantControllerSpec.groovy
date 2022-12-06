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
import life.qbic.variantstore.controller.VariantController
import spock.lang.Specification
import spock.lang.Unroll


@MicronautTest(transactional = false)
class VariantControllerSpec extends Specification{

    @Inject
    private ApplicationContext applicationContext

    @Inject
    EmbeddedServer embeddedServer

    @Inject
    @Client('/')
    Rx3HttpClient httpClient


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


    void "should return variants based on start position"() {
        when:
        HttpResponse response = httpClient.toBlocking().exchange("/variants?${query}", List)

        then:
        response.status == status
        response.body().size() == resultSize

        where:
        query || status || resultSize
        "startPosition=166006829" || HttpStatus.OK || 1
        "startPosition=166006829&withVcf=true" || HttpStatus.OK || 1
        "startPosition=166006829&withGenotypes=true&withConsequences=true" || HttpStatus.OK || 1
    }


    void "should return variants based on specified chromosome and start position"() {
        when:
        HttpResponse response = httpClient.toBlocking().exchange("/variants?${query}", List)

        then:
        response.status == status
        response.body().size() == resultSize

        where:
        query || status || resultSize
        "chromosome=4&startPosition=166006829" || HttpStatus.OK || 1
        "chromosome=4&startPosition=166006829&withVcf=true" || HttpStatus.OK || 1
        "chromosome=4&startPosition=166006829&withGenotypes=true&withConsequences=true" || HttpStatus.OK || 1
    }

    @Unroll
    void "should return variants based on sample identifier #query"() {
        when:
        HttpResponse response = httpClient.toBlocking().exchange("/variants?${query}", List)

        then:
        response.status == status
        response.body().size() == resultSize

        where:
        query || status || resultSize
        "sampleId=QTEST001AL" || HttpStatus.OK || 5
        "sampleId=QTEST001AL&withVcf=true" || HttpStatus.OK || 5
        "sampleId=QTEST001AL&withConsequences=true" || HttpStatus.OK || 5
        "sampleId=QTEST001AL&withConsequences=true&withVcf=true" || HttpStatus.OK || 5
        "sampleId=QTEST001AL&withGenotypes=true&withConsequences=true" || HttpStatus.OK || 5
    }


    void "should return variants based on gene name"() {
        when:
        HttpResponse response = httpClient.toBlocking().exchange("/variants?${query}", List)

        then:
        response.status == status
        response.body().size() == resultSize

        where:
        query || status || resultSize
        "gene=LAMA3" || HttpStatus.OK || 1
        "gene=LAMA3&withVcf=true" || HttpStatus.OK || 1
        "gene=LAMA3&withConsequences=true" || HttpStatus.OK || 1
        "gene=LAMA3&withConsequences=true&withVcf=true" || HttpStatus.OK || 1
        "gene=LAMA3&withConsequences=true&withGenotypes=true" || HttpStatus.OK || 1
    }


    void "should return variants based on gene identifier"() {
        when:
        HttpResponse response = httpClient.toBlocking().exchange("/variants?${query}", List)

        then:
        response.status == status
        response.body().size() == resultSize

        where:
        query || status || resultSize
        "geneId=ENSG00000115602" || HttpStatus.OK || 1
        "geneId=ENSG00000115602&withVcf=true" || HttpStatus.OK || 1
        "geneId=ENSG00000115602&withConsequences=true" || HttpStatus.OK || 1
        "geneId=ENSG00000115602&withConsequences=true&withVcf=true" || HttpStatus.OK || 1
        "geneId=ENSG00000115602&withConsequences=true&withGenotypes=true" || HttpStatus.OK || 1
    }


    @Unroll
    void "should return variants based on sample identifier and gene identifier #query"() {
        when:
        HttpResponse response = httpClient.toBlocking().exchange("/variants?${query}", List)

        then:
        response.status == status
        response.body().size() == resultSize

        where:
        query || status || resultSize
        "sampleId=QTEST001AL&geneId=ENSG00000053747" || HttpStatus.OK || 1
        "sampleId=QTEST001AL&geneId=ENSG00000053747&withVcf=true" || HttpStatus.OK || 1
        "sampleId=QTEST001AL&geneId=ENSG00000053747&withConsequences=true" || HttpStatus.OK || 1
        "sampleId=QTEST001AL&geneId=ENSG00000053747&withConsequences=true&withVcf=true" || HttpStatus.OK || 1
        "sampleId=QTEST001AL&geneId=ENSG00000053747&withGenotypes=true&withConsequences=true" || HttpStatus.OK || 1
    }

    void "should return Http 200 for given variant identifier if available "() {
        when:
        HttpResponse response = httpClient.toBlocking().exchange("/variants/${identifier}")

        then:
        response.status == status

        where:
        identifier || status
        "e741f1f2-2c39-4d00-b311-5bf4cf896005"   ||  HttpStatus.OK
        "d0b9a24d-e95a-44a2-8279-e895772f97cf"   ||  HttpStatus.OK
    }

    void "should return Http 404 for given variant identifier if not available "() {
        when:
        def identifier = "2f8fdbe9-12ec-4b98-9ce7-db2b6dfac789"
        HttpResponse response = httpClient.toBlocking().exchange("/variants/${identifier}")

        then:
        def e = thrown(HttpClientResponseException)
        e.status == HttpStatus.NOT_FOUND
    }
}
