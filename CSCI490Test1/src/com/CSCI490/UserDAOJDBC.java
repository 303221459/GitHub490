package com.CSCI490;

import static com.CSCI490.DAOUtil.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.CSCI490.User;

/**
 * UserDAOJDBC is the concrete JDBC implementation of the UserDAO interface.  
 *
 * PrepareStatement is used for improving the ease of use and preventing security issue
 * 
 * If you try to find something in the database, you should use resultSet.next() and map() to retrieve the found value
 * If you try to create or change something in the database, you should use statement.executeUpdate() to check if the change is applied
 * @author Junkai
 * 
 */
public class UserDAOJDBC implements UserDAO {

    // Constants ----------------------------------------------------------------------------------

    private static final String SQL_FIND_BY_ID =
        "SELECT id, email, firstname, lastname FROM User WHERE id = ?";
    private static final String SQL_FIND_BY_EMAIL_AND_PASSWORD =
        "SELECT id, email, firstname, lastname FROM User WHERE email = ? AND password = MD5(?)";
    private static final String SQL_LIST_ORDER_BY_ID =
        "SELECT id, email, firstname, lastname FROM User ORDER BY id";
    private static final String SQL_INSERT =
        "INSERT INTO User (email, password, firstname, lastname) VALUES (?, MD5(?), ?, ?)";
    private static final String SQL_UPDATE =
        "UPDATE User SET email = ?, firstname = ?, lastname = ? WHERE id = ?";
    private static final String SQL_DELETE =
        "DELETE FROM User WHERE id = ?";
    private static final String SQL_EXIST_EMAIL =
        "SELECT id FROM User WHERE email = ?";
    private static final String SQL_CHANGE_PASSWORD =
        "UPDATE User SET password = MD5(?) WHERE id = ?";

    // Vars ---------------------------------------------------------------------------------------

    private DAOFactory daoFactory;

    // Constructors -------------------------------------------------------------------------------

    /**
     * Constructor: Construct an UserDAOJDBC for the given DAOFactory
     * @param daoFactory The DAOFactory that is used for construct this UserDAOJDBC
     */
    UserDAOJDBC(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    // Actions ------------------------------------------------------------------------------------

    @Override
    public User find(Long id) throws DAOException {
        return find(SQL_FIND_BY_ID, id);
    }

    @Override
    public User find(String email, String password) throws DAOException {
        return find(SQL_FIND_BY_EMAIL_AND_PASSWORD, email, password);
    }

    /**
     * Returns the user from the database matching the given SQL query with the given values.
     * @param sql The SQL query to be executed in the database.
     * @param values The PreparedStatement values to be set.
     * @return The user from the database matching the given SQL query with the given values.
     * @throws DAOException If something fails at database level.
     */
    private User find(String sql, Object... values) throws DAOException {
        User user = null;

        try (
            Connection connection = daoFactory.getConnection();
        	//We are trying to find a user, not create a new user. Thus, we do not need "returnGeneratedKey". Set "false"
        	//The prepareStatement is used for searching the required user by ID or "EmailAndPassword"
            PreparedStatement statement = prepareStatement(connection, sql, false, values);
        	//When you execute an SQL query you get back a ResultSet. The ResultSet contains the result of your SQL query.
        	//The result is returned in rows with columns of data
            ResultSet resultSet = statement.executeQuery();
        ) {
            if (resultSet.next()) {
            	//create a new User object that use the retrieved value from the ResultSet
                user = map(resultSet);
            }
        } catch (SQLException e) {
        	//Report the Database access error
            throw new DAOException(e);
        }

        return user;
    }

    @Override
    public List<User> list() throws DAOException {
        List<User> users = new ArrayList<>();

        try (
            Connection connection = daoFactory.getConnection();
            PreparedStatement statement = connection.prepareStatement(SQL_LIST_ORDER_BY_ID);
            ResultSet resultSet = statement.executeQuery();
        ) {
            while (resultSet.next()) {
                users.add(map(resultSet));
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }

        return users;
    }

    @Override
    /**
     * When create a new User, the User Id has to be null since the Id will be assigned by the database directly
     * If Id!= null, raise an exception
     */
    public void create(User user) throws IllegalArgumentException, DAOException {
        if (user.getId() != null) {
        	//System.out.printf("The user id is:%d \n",user.getId());
            throw new IllegalArgumentException("User is already created, the user ID is not null.");
        }

        //create an array for the value which will be created
        Object[] values = {
            user.getEmail(),
            user.getPassword(),
            user.getFirstname(),
            user.getLastname()
        };

        try (
            Connection connection = daoFactory.getConnection();
            PreparedStatement statement = prepareStatement(connection, SQL_INSERT, true, values);
        ) {
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Creating user failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                } else {
                    throw new DAOException("Creating user failed, no generated key obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    /**
     * The created User has to have an Id
     * The update will depend on the Id
     */
    public void update(User user) throws DAOException {
        if (user.getId() == null) {
            throw new IllegalArgumentException("User is not created yet, the user ID is null.");
        }

        Object[] values = {
            user.getEmail(),
            user.getFirstname(),
            user.getLastname(),
            user.getId()
        };

        try (
            Connection connection = daoFactory.getConnection();
            PreparedStatement statement = prepareStatement(connection, SQL_UPDATE, false, values);
        ) {
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Updating user failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    /**
     * There may be something wrong with this method, try to delete a Id=null
     */
    public void delete(User user) throws DAOException {
        Object[] values = { 
            user.getId()
        };

        try (
            Connection connection = daoFactory.getConnection();
            PreparedStatement statement = prepareStatement(connection, SQL_DELETE, false, values);
        ) {
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Deleting user failed, no rows affected.");
            } else {
                user.setId(null);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public boolean existEmail(String email) throws DAOException {
        Object[] values = { 
            email
        };

        boolean emailExist = false;

        try (
            Connection connection = daoFactory.getConnection();
            PreparedStatement statement = prepareStatement(connection, SQL_EXIST_EMAIL, false, values);
            ResultSet resultSet = statement.executeQuery();
        ) {
            emailExist = resultSet.next();
        } catch (SQLException e) {
            throw new DAOException(e);
        }

        return emailExist;
    }

    @Override
    /**
     * If a user has been created, it have to have an Id
     */
    public void changePassword(User user) throws DAOException {
        if (user.getId() == null) {
            throw new IllegalArgumentException("User is not created yet, the user ID is null.");
        }

        Object[] values = {
            user.getPassword(),
            user.getId()
        };

        try (
            Connection connection = daoFactory.getConnection();
            PreparedStatement statement = prepareStatement(connection, SQL_CHANGE_PASSWORD, false, values);
        ) {
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Changing password failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    // Helpers ------------------------------------------------------------------------------------

    /**
     * Map the current row of the given ResultSet to an new User object.
     * @param resultSet The ResultSet of which the current row is to be mapped to an User.
     * @return The mapped User from the current row of the given ResultSet.
     * @throws SQLException If something fails at database level.
     */
    private static User map(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setEmail(resultSet.getString("email"));
        user.setFirstname(resultSet.getString("firstname"));
        user.setLastname(resultSet.getString("lastname"));
        return user;
    }

}