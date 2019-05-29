package life.qbic.oncostore.database

class OncostoreStorageException extends RuntimeException{

    OncostoreStorageException() {
            super()
        }

    OncostoreStorageException(String message) {
            super(message)
        }

    OncostoreStorageException(String message, Throwable cause) {
            super(message, cause)
        }

}
