package volta.vespa.hobbitclient.business;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import volta.vespa.hobbitclient.bean.Obstacle;
import volta.vespa.hobbitclient.bean.Player;

public class JsonMapper {

    public static class ServerMessage {
        public MessageType type;
        public List<Player> players = new ArrayList<>();
        public List<Obstacle> obstacles = new ArrayList<>();
    }

    public enum MessageType {
        PLAYERS, OBSTACLES, UNKNOWN
    }

    public ServerMessage parse(String raw) {

        ServerMessage msg = new ServerMessage();

        try {
            JSONObject json = new JSONObject(raw);
            String command = json.optString("comando");

            switch (command) {

                case "posizioneTutti":
                    msg.type = MessageType.PLAYERS;
                    JSONArray players = json.getJSONArray("utenti");
                    for (int i = 0; i < players.length(); i++) {
                        JSONObject p = players.getJSONObject(i);
                        msg.players.add(
                                new Player(
                                        p.getString("nome"),
                                        p.getInt("x"),
                                        p.getInt("y")
                                )
                        );
                    }
                    break;

                case "ostacoli":
                    msg.type = MessageType.OBSTACLES;
                    JSONArray obs = json.getJSONArray("posizioni");
                    for (int i = 0; i < obs.length(); i++) {
                        JSONObject o = obs.getJSONObject(i);
                        msg.obstacles.add(
                                new Obstacle(
                                        o.getString("tipo"),
                                        o.getInt("x"),
                                        o.getInt("y")
                                )
                        );
                    }
                    break;

                default:
                    msg.type = MessageType.UNKNOWN;
            }

        } catch (Exception ignored) {
        }

        return msg;
    }
}