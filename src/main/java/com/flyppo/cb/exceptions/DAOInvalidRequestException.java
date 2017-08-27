package com.flyppo.cb.exceptions;

/**
 * thrown when the query request is incorrect/invalid
 * generic exception independent of DAO implementations
 * 
 * @author mmt6461
 *
 */
public class DAOInvalidRequestException extends DAOException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public DAOInvalidRequestException(String message) {
        super(message);
    }
    
    public DAOInvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
