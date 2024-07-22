package dataaccess;

import model.AuthData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLAuthDAO implements AuthDAO {
    private Connection connection;

    public SQLAuthDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        String query = "SELECT * FROM AuthData WHERE authToken = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
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
        String query = "INSERT INTO AuthData (authToken, username) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
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
        String query = "DELETE FROM AuthData WHERE authToken = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, authToken);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting auth data", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String query = "TRUNCATE TABLE AuthData";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing auth data", e);
        }
    }

    @Override
    public List<AuthData> getAllAuthData() throws DataAccessException {
        List<AuthData> authDataList = new ArrayList<>();
        String query = "SELECT * FROM AuthData";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                authDataList.add(new AuthData(
                        resultSet.getString("authToken"),
                        resultSet.getString("username")
                ));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving all auth data", e);
        }
        return authDataList;
    }
}
