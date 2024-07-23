package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClearService {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    //For debugging stuff
    private static final Logger logger = LoggerFactory.getLogger(ClearService.class);

    //main DAO data
    public ClearService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    //clearing, debug statements for each CLEAR
    public void clear() throws DataAccessException {
        logger.debug("Clearing all user data");
        userDAO.clear();
        logger.debug("Clearing all game data");
        gameDAO.clear();
        logger.debug("Clearing all auth data");
        authDAO.clear();
        logger.debug("All data cleared");
    }
}
