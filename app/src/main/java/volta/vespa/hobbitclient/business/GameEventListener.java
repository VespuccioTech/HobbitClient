package volta.vespa.hobbitclient.business;

import java.util.List;

import volta.vespa.hobbitclient.bean.Obstacle;
import volta.vespa.hobbitclient.bean.Player;

public interface GameEventListener {

    void onConnected();

    void onError(String error);

    void onWorldUpdate(List<Player> players, List<Obstacle> obstacles);
}