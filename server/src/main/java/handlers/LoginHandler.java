package handlers;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import request.LoginRequest;
import response.LoginResult;
import Service.LoginService;

public class LoginHandler extends HandlerForHttps<LoginRequest> {
    private final LoginService loginService;

    public LoginHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.loginService = new LoginService(userDAO, authDAO);
    }

    @Override
    protected Class<LoginRequest> getRequestClass() {
        return LoginRequest.class;
    }

    @Override
    protected LoginResult getResult(LoginRequest request, String authToken) throws Exception {
        return loginService.login(request);
    }
}
