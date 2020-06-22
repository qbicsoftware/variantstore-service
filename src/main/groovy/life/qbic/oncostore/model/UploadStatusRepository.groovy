package life.qbic.oncostore.model

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface UploadStatusRepository extends CrudRepository<UploadStatus, Long> {

    @Override
    List<UploadStatus> findAll()

    Optional<UploadStatus> findByUuid(String uuid)
}