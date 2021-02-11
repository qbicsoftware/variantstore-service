package life.qbic.oncostore.database

/**
 * A class for Variantstore storage-specific exceptions.
 *
 * @since: 1.0.0
 */
class VariantstoreStorageException extends RuntimeException{

    VariantstoreStorageException() {
            super()
        }

    VariantstoreStorageException(String message) {
            super(message)
        }

    VariantstoreStorageException(String message, Throwable cause) {
            super(message, cause)
        }

}
