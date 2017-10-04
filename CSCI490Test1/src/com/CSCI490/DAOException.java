package com.CSCI490;


/**
 * This class is used for exception handling of general exception, including all exceptions
 * that could be raised when connect to the dataBase, such as SQLExceptions.
 * 
 * @author Junkai
 *
 */
public class DAOException extends RuntimeException{

	private static final long serialVersionUID=1L;
	
	public DAOException(String message){
		super(message);
	}
	
	public DAOException(Throwable cause){
		super(cause);
	}
	
	public DAOException(String message, Throwable cause){
		super(message,cause);
	}
	
}
