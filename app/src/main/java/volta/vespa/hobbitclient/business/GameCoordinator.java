package volta.vespa.hobbitclient.business;

import java.util.ArrayList;
import java.util.List;

import volta.vespa.hobbitclient.bean.Obstacle;
import volta.vespa.hobbitclient.bean.Player;

public class GameCoordinator {

    private final GameEventListener view;
    private final RealTimeClient socketClient;
    private final JsonMapper mapper = new JsonMapper();

    private final List<Player> players = new ArrayList<>();
    private final List<Obstacle> obstacles = new ArrayList<>();

    private final String playerName;

    public GameCoordinator(GameEventListener view, String playerName) {
        this.view = view;
        this.playerName = playerName;

        socketClient = new RealTimeClient(this::handleIncomingMessage,
                view::onError,
                view::onConnected);
    }

    public void connect() {
        socketClient.connect();
    }

    public void requestMove(String direction) {
        socketClient.sendMove(direction);
    }

    private void handleIncomingMessage(String json) {

        JsonMapper.ServerMessage msg = mapper.parse(json);

        switch (msg.type) {

            case OBSTACLES:
                obstacles.clear();
                obstacles.addAll(msg.obstacles);
                break;

            case PLAYERS:
                players.clear();
                players.addAll(msg.players);
                break;
        }

        view.onWorldUpdate(players, obstacles);
    }
}