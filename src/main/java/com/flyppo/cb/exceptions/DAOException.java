package com.flyppo.cb.exceptions;

/**
 * generic DAO exception
 * 
 * @author mmt6461
 *
 */
public class DAOException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public DAOException (String message) {
        super(message);
    }

    public DAOException (String message, Throwable cause) {
        super(message, cause);
    }
}
