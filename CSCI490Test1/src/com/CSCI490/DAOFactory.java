package com.CSCI490;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * This class is a DAOFactory for a SQL database (dealing with connection to the database)
 * This class requires a property file called 'dao.properties' is the class path, it should have the following properties:
 * name.url* (The 'name.url' must represent either the JDBC URL or JNDI name of the database.)
 * name.driver
 * name.username
 * name.password
 * (Those marked with * are required, others are optional and can be left away or empty. Only the
 *  username is required when any password is specified.)
 * 
 * How to get connection to the database?
 * First, you use getInstance() to get an instance (Url, database driver, user name, password) of the database you created before. The specific instance returned depends on the properties file configuration.
 * Then, you will call getConnection to get connection to the database. You can get connection either by using the sql.DriverManager or by using the sql.DataSource.
 * Using DataSource is recommended since it can be used for connection polling (May be updated later)
 * 
 * If you specify the driver property, then the url property will be assumed as JDBC URL. If you
 * omit the driver property, then the url property will be assumed as JNDI name. When using JNDI
 * with username/password preconfigured, you can omit the username and password properties as well.
 * 
 * Here are basic examples of valid properties for a database with the name 'authentication':
 * (JDBC)
 * authentication.jdbc.url=jdbc:mysql://localhost:3306/authentication
 * authentication.jdbc.driver=com.mysql.jdbc.Driver
 * authentication.jdbc.username=junkai
 * authentication.jdbc.password=password
 * (JNDI)
 * authentication.jndi.url = jdbc/authentication
 * 
 * Here is a basic use example:
 * 
 * DAOFactory authentication = DAOFactory.getInstance("authentication.jdbc");
 * UserDAO userDAO = authentication.getUserDAO();
 * 
 * @author Junkai
 *
 */

public abstract class DAOFactory {
	
	//Constants for building path for SQLConnection
	private static final String PROPERTY_URL="url";
	private static final String PROPERTY_DRIVER="driver";
	private static final String PROPERTY_USERNAME = "username";
    private static final String PROPERTY_PASSWORD = "password";
    
    
    /**This class is used for get an Instance of the given database name
     * It provides two ways for getting an instance, depends on if it can find the driverClassName
     * The returned instance can be either a dataManager or a dataSource
     * 
     * @param databaseName The name of the database you are connecting to.
     * @return A new DAOFactory instance for the given database name.
     * @throws DAOConfigurationException If the database name is null, or if the properties file is
     * missing in the classpath or cannot be loaded, or if a required property is missing in the
     * properties file, or if either the driver cannot be loaded or the dataSource cannot be found.
     */
    public static DAOFactory getInstance(String databaseName) throws DAOConfigurationException {
        if (databaseName == null) {
            throw new DAOConfigurationException("Database name is null.");
        }

        //use the created DAOProperties class to get required properties for database connection
        //The prefix and a key will be needed to find each property
        //E.g. databaseName="authentication.jdbc", PROPERTY_URL="url". Then you will get the property "authentication.jdbc.url"
        DAOProperties properties = new DAOProperties(databaseName);
        String url = properties.getProperty(PROPERTY_URL, true);
        String driverClassName = properties.getProperty(PROPERTY_DRIVER, false);
        String password = properties.getProperty(PROPERTY_PASSWORD, false);
        String username = properties.getProperty(PROPERTY_USERNAME, password != null);
        DAOFactory instance;

               
        
        // If driver is specified, then load it to let it register itself with DriverManager.
        if (driverClassName != null) {
            try {
            	//try to load the given classDriver. The classDriver is mysql database driver in this case
                Class.forName(driverClassName);
            } catch (ClassNotFoundException e) {
                throw new DAOConfigurationException(
                    "Driver class '" + driverClassName + "' is missing in classpath.", e);
            }
            instance = new DriverManagerDAOFactory(url, username, password);
        }

        // Else assume URL as DataSource URL and lookup it in the JNDI.
        else {
            DataSource dataSource;
            try {
                dataSource = (DataSource) new InitialContext().lookup(url);
            } catch (NamingException e) {
                throw new DAOConfigurationException(
                    "DataSource '" + url + "' is missing in JNDI.", e);
            }
            if (username != null) {
                instance = new DataSourceWithLoginDAOFactory(dataSource, username, password);
            } else {
                instance = new DataSourceDAOFactory(dataSource);
            }
        }

        return instance;
    }
    
    /**
     * Used to build connection to the database
     * @return a connection to the database
     * @throws SQLException If acquiring the connection fails
     */
    abstract Connection getConnection() throws SQLException;
    
    /**
     * Return the UserDAO associated with the current DAOFactory
     */
    
    public UserDAO getUserDAO(){
    	return new UserDAOJDBC(this);
    }
    
    
    
}

/**
 * This is another class that inherit DAOFactory. It is a DriverManager based DAOFactory.
 * This class is called if the "driverClassName" can be found
 * 
 */
class DriverManagerDAOFactory extends DAOFactory {
    private String url;
    private String username;
    private String password;

    DriverManagerDAOFactory(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    //Override the abstract method in the super class
    @Override
    Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}

/**
 * This is another class that inherit DAOFactory. It is a DataSource based DAOFactory.
 * This class is called if the "driverClassName, userName" cannot be found
 * 
 */
class DataSourceDAOFactory extends DAOFactory {
    private DataSource dataSource;

    DataSourceDAOFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}

/**
 * This is another class that inherit DAOFactory. It is a DataSource-with-Login based DAOFactory.
 * This class is called if the "driverClassName" cannot be found, but the userName can be found.
 */
class DataSourceWithLoginDAOFactory extends DAOFactory {
    private DataSource dataSource;
    private String username;
    private String password;

    DataSourceWithLoginDAOFactory(DataSource dataSource, String username, String password) {
        this.dataSource = dataSource;
        this.username = username;
        this.password = password;
    }

    @Override
    Connection getConnection() throws SQLException {
        return dataSource.getConnection(username, password);
    }
}


