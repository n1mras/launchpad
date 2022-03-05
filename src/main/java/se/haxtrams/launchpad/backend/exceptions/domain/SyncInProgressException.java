package se.haxtrams.launchpad.backend.exceptions.domain;

public class SyncInProgressException extends RuntimeException {
    public SyncInProgressException() {
        super();
    }

    public SyncInProgressException(String message) {
        super(message);
    }

    public SyncInProgressException(String message, Throwable cause) {
        super(message, cause);
    }

    public SyncInProgressException(Throwable cause) {
        super(cause);
    }

    protected SyncInProgressException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
