package handlers;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import handlers.HandlerForHttps;
import model.UserData;
import Service.LoginService;
import model.AuthData;

public class LoginHandler extends HandlerForHttps<UserData> {

    private final LoginService loginService;

    public LoginHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.loginService = new LoginService(userDAO, authDAO);
    }

    @Override
    protected Class<UserData> getRequestClass() {
        return UserData.class;
    }

    @Override
    protected AuthData getResult(UserData request, String authToken) throws Exception {
        return loginService.login(request);
    }
}
