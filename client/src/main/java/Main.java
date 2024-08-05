import serverutils.ServerFacade;
import ui.PreLoginUI;

public class Main {
    public static void main(String[] args) {

        //ServerFacade : at 8080
        ServerFacade serverFacade = new ServerFacade(8080);

        // Display: PreLoginUI (which handles PostLoginUI)
        PreLoginUI preLoginUI = new PreLoginUI(serverFacade);
        preLoginUI.display();

    }
}
