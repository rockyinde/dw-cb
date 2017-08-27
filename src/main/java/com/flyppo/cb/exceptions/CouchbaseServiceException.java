package com.flyppo.cb.exceptions;

/**
 * generic DAO exception
 * 
 * @author mmt6461
 *
 */
public class CouchbaseServiceException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public CouchbaseServiceException (String message) {
        super(message);
    }

    public CouchbaseServiceException (String message, Throwable cause) {
        super(message, cause);
    }
}
