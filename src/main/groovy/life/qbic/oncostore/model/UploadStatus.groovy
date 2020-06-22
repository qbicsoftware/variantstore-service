package life.qbic.oncostore.model

import com.fasterxml.jackson.annotation.JsonProperty

import javax.persistence.*

@Entity
class UploadStatus {

    @Id
    @GeneratedValue
    Long id
    String uuid
    String fileName
    Long fileSize
    String status

    UploadStatus() {
    }

    void setId(Long id) {
        this.id = id
    }

    void setUuid(String uuid) {
        this.uuid = uuid
    }

    void setFileName(String fileName) {
        this.fileName = fileName
    }

    void setFileSize(Long fileSize) {
        this.fileSize = fileSize
    }

    void setStatus(String status) {
        this.status = status
    }

    Long getId() {
        return id
    }

    @JsonProperty("UUID")
    String getUuid() {
        return uuid
    }

    @JsonProperty("fileName")
    String getFileName() {
        return fileName
    }

    @JsonProperty("fileSize")
    long getFileSize() {
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
