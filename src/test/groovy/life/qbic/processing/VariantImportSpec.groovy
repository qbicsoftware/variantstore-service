package life.qbic.processing

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.multipart.MultipartBody
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.rxjava3.http.client.Rx3HttpClient
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import life.qbic.variantstore.model.TransactionStatus
import life.qbic.variantstore.model.Status
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import spock.util.concurrent.PollingConditions


@MicronautTest(transactional = false)
@Property(name= "micronaut.server.multipart.max-file-size", value = "10MB")
@Property(name= "micronaut.server.max-request-size", value = "20MB")
@Property(name= "micronaut.server.netty.max-initial-line-length", value = "3000000")
class VariantImportSpec extends Specification {

    @Inject
    ApplicationContext applicationContext

    @Inject
    EmbeddedServer embeddedServer

    @Inject
    @Client('/')
    Rx3HttpClient httpClient

    @Shared String metadata1 = '{"project": {"identifier": "project1"}, "case": {"identifier": "patient1"}, ' +
            '"variant_annotation": {"version": "4.3t","name": "snpeff", "doi": "10.4161/fly.19695"}, "is_somatic": true, ' +
            '"sample": {"identifier": "QTEST001AL","cancerEntity": "HCC"}, "reference_genome": ' +
            '{"source": "Ensembl", "version": "17", "build": "GRCh37"},"variant_calling": {"version": "2.0", "name": "Strelka", "doi": "10.1038/s41592-018-0051-x"}}'
    @Shared String metadata2 = '{"project": {"identifier": "project1"}, "case": {"identifier": "patient2"}, "variant_annotation": {"version": "4.3t", "name": "snpeff", "doi": "10.4161/fly.19695"}, "is_somatic": true, "sample": {"identifier": "QTEST002AT", ' +
    '"cancerEntity": "HCC"}, "reference_genome": {"source": "Ensembl", "version": "17", "build": ' +
    '"GRCh37"}, "variant_calling": {"version": "2.0", "name": "Strelka", "doi": "10' + '.1038/s41592-018-0051-x"}}'
    @Shared String metadata3 = '{"project": {"identifier": "project2"}, "case": {"identifier": "patient3"}, "variant_annotation": {"version": "4.3t", "name": "snpeff", "doi": "10.4161/fly.19695"}, "is_somatic": true, "sample": {"identifier": "QTEST003A3", ' +
    '"cancerEntity": "ALL"}, "reference_genome": {"source": "Ensembl", "version": "17", "build": ' +
    '"GRCh37"}, "variant_calling": {"version": "2.0", "name": "Strelka", "doi": "10' + '.1038/s41592-018-0051-x"}}'

    @Unroll
    void "should import variants and metadata to the variantstore"() {

        given:
        def requestBody = MultipartBody.builder()
                .addPart("metadata", "${metadata}")
                .addPart("files", new File("${file}"))
                .build()
        HttpRequest request = HttpRequest.POST("/variants", requestBody)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        PollingConditions uploaded = new PollingConditions(delay: 1, initialDelay: 0.5, timeout: 120)

        when:
        HttpResponse response = httpClient.toBlocking().exchange(request)

        then:
        uploaded.eventually {
            HttpRequest transactionRequest = HttpRequest.GET(response.header(HttpHeaders.LOCATION))
            HttpResponse transactionResponse = httpClient.toBlocking().exchange(transactionRequest, TransactionStatus.class)
            String transactionStatus = transactionResponse.body().status
            transactionStatus == Status.finished.toString()
            response.status() == status
        }

        where:
        metadata || file || status
        metadata1 || "src/test/resources/data/patient1_ann.vcf"  || HttpStatus.ACCEPTED
        metadata2 || "src/test/resources/data/patient2_ann.vcf"  || HttpStatus.ACCEPTED
        metadata3 || "src/test/resources/data/patient3_ann.vcf"  || HttpStatus.ACCEPTED
    }
}
