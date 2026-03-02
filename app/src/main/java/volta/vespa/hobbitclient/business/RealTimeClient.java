package volta.vespa.hobbitclient.business;

import org.json.JSONObject;

import java.util.function.Consumer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class RealTimeClient {

    private static final String SERVER_URL = "ws://192.168.1.5:8080";

    private WebSocket socket;

    private final Consumer<String> messageCallback;
    private final Consumer<String> errorCallback;
    private final Runnable openCallback;

    public RealTimeClient(Consumer<String> messageCallback,
                          Consumer<String> errorCallback,
                          Runnable openCallback) {

        this.messageCallback = messageCallback;
        this.errorCallback = errorCallback;
        this.openCallback = openCallback;
    }

    public void connect() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(SERVER_URL).build();
        socket = client.newWebSocket(request, new InternalListener());
    }

    public void sendMove(String direction) {
        try {
            JSONObject json = new JSONObject();
            json.put("comando", "muovi");
            json.put("direzione", direction);
            socket.send(json.toString());
        } catch (Exception ignored) {
        }
    }

    private void sendLogin() {
        try {
            JSONObject json = new JSONObject();
            json.put("comando", "login");
            json.put("nome", "Player1");
            socket.send(json.toString());
        } catch (Exception ignored) {
        }
    }

    private void requestObstacles() {
        try {
            JSONObject json = new JSONObject();
            json.put("comando", "ostacoli");
            socket.send(json.toString());
        } catch (Exception ignored) {
        }
    }

    private class InternalListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            openCallback.run();
            sendLogin();
            requestObstacles();
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            messageCallback.accept(text);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            errorCallback.accept(t.getMessage());
        }
    }
}