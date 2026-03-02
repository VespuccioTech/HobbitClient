package volta.vespa.hobbitclient.bean;

public class Player {

    private final String name;
    private final int x;
    private final int y;

    public Player(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}