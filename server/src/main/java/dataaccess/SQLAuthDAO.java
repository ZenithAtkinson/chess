package dataaccess;

import model.AuthData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {
    private Connection connection;

    public SQLAuthDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        try {
            String query = "SELECT * FROM AuthData WHERE authToken = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, authToken);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new AuthData(
                        resultSet.getString("authToken"),
                        resultSet.getString("username")
                );
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error accessing auth data", e);
        }
        return null;
    }

    @Override
    public boolean addAuthData(AuthData authData) throws DataAccessException {
        try {
            String query = "INSERT INTO AuthData (authToken, username) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, authData.getAuthToken());
            statement.setString(2, authData.getUsername());
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error adding auth data", e);
        }
    }

    @Override
    public boolean deleteAuthData(String authToken) throws DataAccessException {
        try {
            String query = "DELETE FROM AuthData WHERE authToken = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, authToken);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting auth data", e);
        }
    }
}
