package life.qbic.variantstore.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.model.naming.NamingStrategies

/**
 * A DTO representing the status of a transaction
 *
 * @since: 1.1.0
 */
@MappedEntity(namingStrategy = NamingStrategies.UnderScoreSeparatedLowerCase.class)
class TransactionStatus{

    /**
     * The database id
     */
    @Id
    @GeneratedValue
    Integer id
    /**
     * The identifier
     */
    String identifier
    /**
     * The file name
     */
    String fileName
    /**
     * The file size
     */
    Float fileSize
    /**
     * The status of a transaction. It is a {@link Status}
     */
    String status

    TransactionStatus() {
    }

    void setId(Integer id) {
        this.id = id
    }

    void setIdentifier(String uuid) {
        this.identifier = uuid
    }

    void setFileName(String fileName) {
        this.fileName = fileName
    }

    void setFileSize(Float fileSize) {
        this.fileSize = fileSize
    }

    void setStatus(String status) {
        this.status = status
    }

    Integer getId() {
        return id
    }

    @JsonProperty("uuid")
    String getIdentifier() {
        return identifier
    }

    @JsonProperty("fileName")
    String getFileName() {
        return fileName
    }

    @JsonProperty("fileSize")
    Float getFileSize() {
        return fileSize
    }

    @JsonProperty("status")
    String getStatus() {
        return status
    }
}

/**
 * Possible status of a transaction
 *
 * @since: 1.1.0
 */
enum Status{
    started,
    processing,
    finished
}
