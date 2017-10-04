package com.CSCI490;

/**
 * This class is used for runtime exception handling, such as missing a necessary file
 * For example, you may miss a property file in the class path
 * 
 * @author Junkai
 *
 */
public class DAOConfigurationException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public DAOConfigurationException(String message){
		super(message);
	}
	
	public DAOConfigurationException(Throwable cause){
		super(cause);
	}
	
	public DAOConfigurationException(String message, Throwable cause){
		super(message,cause);
	}
	
}
