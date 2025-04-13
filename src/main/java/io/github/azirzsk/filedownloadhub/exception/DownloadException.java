package io.github.azirzsk.filedownloadhub.exception;

/**
 * @author azir
 * @since 2025/04/13
 */
public class DownloadException extends RuntimeException {

    public DownloadException(String message, Throwable cause) {
        super(message, cause);
    }

    public DownloadException(String message) {
        super(message);
    }
}
