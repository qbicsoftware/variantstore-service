package life.qbic.db.postgres

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import life.qbic.variantstore.service.VariantstoreStorage
import spock.lang.Specification


@MicronautTest(transactional = false)
class PostgresqlDatabaseSpec extends Specification {

    @Inject
    VariantstoreStorage storage

    def "confirm that storage connection is alive"() {
        when:
        storage

        then:
        storage.projectRepository
        storage.caseRepository
        storage.sampleRepository
        storage.variantRepository
    }
}
