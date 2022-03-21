package life.qbic.processing

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Property
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
import life.qbic.variantstore.model.Gene
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

@MicronautTest(transactional = false)
@Property(name= "micronaut.server.multipart.max-file-size", value = "10MB")
@Property(name= "micronaut.server.max-request-size", value = "20MB")
@Property(name= "micronaut.server.netty.max-initial-line-length", value = "3000000")
class GeneImportSpec extends Specification{

    @Inject
    ApplicationContext applicationContext

    @Inject
    EmbeddedServer embeddedServer

    @Inject
    @Client('/')
    Rx3HttpClient httpClient

    void "should import genes and metadata from gff3 file to the variantstore"() {
        when:
        def requestBody = MultipartBody.builder()
                .addPart("files", new File("${file}"))
                .build()

        HttpRequest request = HttpRequest.POST("/genes", requestBody)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        HttpResponse response = httpClient.toBlocking().exchange(request)

        then:
        response.status() == status
        HttpRequest geneRequest = HttpRequest.GET('/genes/ENSG00000215567')
        HttpResponse geneResponse = httpClient.toBlocking().exchange(geneRequest, Set<Gene>.class)
        geneResponse.status() == HttpStatus.OK

        where:
        file || status
        "src/test/resources/data/Homo_sapiens.GRCh38.87.chromosome.15.gff3"  || HttpStatus.OK
    }
}
