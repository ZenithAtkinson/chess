package handlers;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import model.AuthData;
import model.UserData;
import Service.UserService;

public class RegisterHandler extends HandlerForHttps<UserData> {

    private final UserService userService;

    public RegisterHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.userService = new UserService(userDAO, authDAO);
    }

    @Override
    protected Class<UserData> getRequestClass() {
        return UserData.class;
    }

    @Override
    protected AuthData getResult(UserData user, String authToken) throws Exception {
        return userService.register(user);
    }
}
