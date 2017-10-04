package com.CSCI490;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class loads the DAO properties file 'dao.properties'
 * 
 * The constructor takes a specific key (The key will be used as property prefix of the property file)
 * @author Junkai
 *
 */
public class DAOProperties {

	private static final String PROPERTIES_FILE="dao.properties";
	private static final Properties PROPERTIES=new Properties();
	
	
	/**
	 * Check if we have the property file
	 * Check if the property file is null
	 * "static" because we always want to check if the properties file exist (even if we do not construct the object yet)
	 */
	static{
		
		//use the following statement to load the PROPERTIES_FILE (in the root folder)
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream propertiesFile = classLoader.getResourceAsStream(PROPERTIES_FILE);
		
		//Raise Exception if the property file does not exist
		if(propertiesFile==null){
			throw new DAOConfigurationException("Properties file '"+PROPERTIES_FILE+"'does not exist in classpath");
		}
		
		//Raise Exception if the properties file cannot be loaded into PROPERTY Object
		try{
			PROPERTIES.load(propertiesFile);
			
		}catch(IOException e){
			throw new DAOConfigurationException("The properties file exists but cannot be loaded, properties file'"+PROPERTIES_FILE+"'",e);
		}
	}
	
	private String prefixKey;
	
	/**
	 * Constructor
	 * Instead of using PROPERTY class, we construct a DAOProperties instance for a given specific key (the key will be used as property key prefix of the DAO properties file)
	 * 
	 * It separates different component of the properties in the "dao.properties file"
	 * 
	 * For example, "authentication.jdbc.url=jdbc:mysql://localhost:3306/authentication"
	 * You will use "authentication.jdbc" as the string required by the constructor and "url" as the String required by getOrioerty()
	 * 
	 */
	
	public DAOProperties(String prefixKey)throws DAOConfigurationException{
		this.prefixKey=prefixKey;
	}
	
	/**
	 * Return the DAOProperties instance specific property value associated with the given key with the option to indicate whether the property is mandatory or not
	 * 
	 * @throws DAOConfigurationException If the returned property value is null or empty while it is mandatory
	 */
	
	public String getProperty(String key, boolean mandatory) throws DAOConfigurationException{
		
		String fullKey=prefixKey+"."+key;
		String property = PROPERTIES.getProperty(fullKey);
		
		if(property==null||property.trim().length()==0){
			if(mandatory){
				//throw the exception if the specific property cannot be found in the loaded PRPERTY_FILE
				throw new DAOConfigurationException("Required property '"+fullKey+"'"+"is missing in properties file'"+PROPERTIES_FILE+"'");
			}else{
				//Make empty value null as a better mark (make more sense)
				property = null;
			}
		}
		
		return property;
	}
	
	
	
}
