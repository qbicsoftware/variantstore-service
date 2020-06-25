package life.qbic.oncostore.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.model.naming.NamingStrategies

import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.Id

@MappedEntity(namingStrategy = NamingStrategies.UnderScoreSeparatedLowerCase.class)
class TransactionStatus{

    @Id
    @GeneratedValue
    Integer id
    String uuid
    @Column(name = "fileName")
    String fileName
    @Column(name = "fileSize")
    Float fileSize
    String status

    TransactionStatus() {
    }

    void setId(Integer id) {
        this.id = id
    }

    void setUuid(String uuid) {
        this.uuid = uuid
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
    String getUuid() {
        return uuid
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

enum Status{
    started,
    processing,
    finished
}
