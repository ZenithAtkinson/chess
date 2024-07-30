package dataaccess;

import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utils.JsonUtil;

public class JsonUtilTest {

    @Test
    public void testSerialization() {
        GameData game = new GameData(1, "whiteUser", "blackUser", "gameName", "additionalParam");
        String json = JsonUtil.toJson(game);

        Assertions.assertNotNull(json);
    }

    @Test
    public void testDeserialization() {
        String json = "{\"gameID\":1,\"whiteUsername\":\"whiteUser\",\"blackUsername\":\"blackUser\",\"gameName\":\"gameName\",\"additionalParameter\":\"additionalParam\"}";
        GameData game = JsonUtil.fromJson(json, GameData.class);

        Assertions.assertNotNull(game);
        Assertions.assertEquals(1, game.getGameID());
        Assertions.assertEquals("whiteUser", game.getWhiteUsername());
    }
}
