package volta.vespa.hobbitclient;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import volta.vespa.hobbitclient.business.GameEventListener;
import volta.vespa.hobbitclient.business.GameCoordinator;
import volta.vespa.hobbitclient.bean.Obstacle;
import volta.vespa.hobbitclient.bean.Player;

public class MainActivity extends AppCompatActivity implements GameEventListener {

    private final List<ImageView> gridCells = new ArrayList<>();
    private GameCoordinator coordinator;

    private static final int COLUMNS = 8;
    private static final int ROWS = 100;

    private final String LOCAL_PLAYER = "Player1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buildDynamicGrid();
        bindControls();

        coordinator = new GameCoordinator(this, LOCAL_PLAYER);
        coordinator.connect();
    }

    private void bindControls() {
        Button up = findViewById(R.id.btnUp);
        Button left = findViewById(R.id.btnSx);
        Button down = findViewById(R.id.btnDown);
        Button right = findViewById(R.id.btnDx);

        up.setOnClickListener(v -> coordinator.requestMove("su"));
        left.setOnClickListener(v -> coordinator.requestMove("sinistra"));
        down.setOnClickListener(v -> coordinator.requestMove("giu"));
        right.setOnClickListener(v -> coordinator.requestMove("destra"));
    }

    @Override
    public void onConnected() {
        runOnUiThread(() ->
                Toast.makeText(this, "Connessione stabilita", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onError(String error) {
        runOnUiThread(() ->
                Toast.makeText(this, error, Toast.LENGTH_LONG).show());
    }

    @Override
    public void onWorldUpdate(List<Player> players, List<Obstacle> obstacles) {
        runOnUiThread(() -> renderWorld(players, obstacles));
    }

    // Costanti di stile
    private static final int COLOR_EMPTY = 0xFF222222;
    private static final int COLOR_OBSTACLE = 0xFFFF3D00; // Rosso Neon
    private static final int COLOR_PLAYER_LOCAL = 0xFF00E5FF; // Ciano
    private static final int COLOR_PLAYER_OTHER = 0xFF76FF03; // Lime

    private void renderWorld(List<Player> players, List<Obstacle> obstacles) {
        gridCells.forEach(cell -> cell.setBackgroundColor(COLOR_EMPTY));

        obstacles.forEach(b -> paintCell(b.getX(), b.getY(), COLOR_OBSTACLE));

        players.forEach(p -> {
            int color = p.getName().equals(LOCAL_PLAYER) ? COLOR_PLAYER_LOCAL : COLOR_PLAYER_OTHER;
            paintCell(p.getX(), p.getY(), color);
        });
    }

    private void buildDynamicGrid() {
        LinearLayout root = findViewById(R.id.campoDinamico);

        // Calcolo dinamico per evitare tagli:
        // Prendiamo la larghezza schermo, togliamo i margini e dividiamo per le colonne
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int cellSize = (screenWidth - 100) / COLUMNS;

        for (int r = 0; r < ROWS; r++) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);

            for (int c = 0; c < COLUMNS; c++) {
                ImageView cell = new ImageView(this);
                // Usiamo cellSize calcolato per far stare tutto perfettamente
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(cellSize, cellSize);
                params.setMargins(1, 1, 1, 1);
                cell.setLayoutParams(params);
                cell.setBackgroundColor(COLOR_EMPTY);

                row.addView(cell);
                gridCells.add(cell);
            }
            root.addView(row);
        }
    }

    private void paintCell(int x, int y, int color) {
        if (x < 0 || x >= COLUMNS || y < 0 || y >= ROWS) return;

        int index = y * COLUMNS + x;
        if (index < gridCells.size()) {
            gridCells.get(index).setBackgroundColor(color);
        }
    }
}