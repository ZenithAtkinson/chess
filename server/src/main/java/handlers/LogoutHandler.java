package handlers;

import dataaccess.AuthDAO;
import Service.LogoutService;

public class LogoutHandler extends HandlerForHttps<Void> {

    private final LogoutService logoutService;

    public LogoutHandler(AuthDAO authDAO) {
        this.logoutService = new LogoutService(authDAO);
    }

    @Override
    protected Class<Void> getRequestClass() {
        return Void.class;
    }

    @Override
    protected Void getResult(Void request, String authToken) throws Exception {
        logoutService.logout(authToken);
        return null;
    }
}
