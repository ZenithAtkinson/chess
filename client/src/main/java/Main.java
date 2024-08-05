import serverutils.ServerFacade;
import ui.PreLoginUI;

public class Main {
    public static void main(String[] args) {

        // ServerFacade with the server port
        ServerFacade serverFacade = new ServerFacade(8080);

        // Display: PreLoginUI (which handles PostLoginUI)
        PreLoginUI preLoginUI = new PreLoginUI(serverFacade);
        preLoginUI.display();

    }
}
