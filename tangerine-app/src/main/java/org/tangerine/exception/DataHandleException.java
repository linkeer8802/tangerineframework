package org.tangerine.exception;

public class DataHandleException extends RuntimeException {

	private static final long serialVersionUID = 2776187921314017301L;

	/**
     * Creates a new instance.
     */
    public DataHandleException() {
    }

    /**
     * Creates a new instance.
     */
    public DataHandleException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance.
     */
    public DataHandleException(String message) {
        super(message);
    }

    /**
     * Creates a new instance.
     */
    public DataHandleException(Throwable cause) {
        super(cause);
    }
}
