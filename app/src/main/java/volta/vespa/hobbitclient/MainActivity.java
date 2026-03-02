package volta.vespa.hobbitclient;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import volta.vespa.hobbitclient.bean.Obstacle;
import volta.vespa.hobbitclient.bean.Player;
import volta.vespa.hobbitclient.business.GameCoordinator;
import volta.vespa.hobbitclient.business.GameEventListener;

public class MainActivity extends AppCompatActivity implements GameEventListener {

    private final List<ImageView> gridCells = new ArrayList<>();
    private GameCoordinator coordinator;

    private static final int COLUMNS = 8;
    private static final int ROWS = 100;

    private String localPlayerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Generazione nome casuale (come fatto in precedenza)
        int randomNumber = (int) (Math.random() * 99) + 1;
        localPlayerName = "Player" + randomNumber;

        buildDynamicGrid();
        bindControls();

        // Nuova logica di connessione
        EditText editIp = findViewById(R.id.editIp);
        EditText editPorta = findViewById(R.id.editPorta);
        Button btnConnetti = findViewById(R.id.btnConnetti);

        btnConnetti.setOnClickListener(v -> {
            String ip = editIp.getText().toString().trim();
            String porta = editPorta.getText().toString().trim();

            if (!ip.isEmpty() && !porta.isEmpty()) {
                String fullAddress = ip + ":" + porta;
                coordinator = new GameCoordinator(this, localPlayerName, fullAddress);
                coordinator.connect();

                // Opzionale: disabilita i campi dopo la connessione
                btnConnetti.setEnabled(false);
                editIp.setEnabled(false);
                editPorta.setEnabled(false);
            } else {
                Toast.makeText(this, "Inserisci IP e Porta!", Toast.LENGTH_SHORT).show();
            }
        });
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

    private static final int COLOR_EMPTY = 0xFF37474F;
    private static final int COLOR_OBSTACLE = 0xFFFF5252;
    private static final int COLOR_PLAYER_LOCAL = 0xFF00E5FF;
    private static final int COLOR_PLAYER_OTHER = 0xFFB2FF59;

    private void renderWorld(List<Player> players, List<Obstacle> obstacles) {
        gridCells.forEach(cell -> cell.setBackgroundColor(COLOR_EMPTY));
        obstacles.forEach(b -> paintCell(b.getX(), b.getY(), COLOR_OBSTACLE));

        players.forEach(p -> {
            int color = p.getName().equals(localPlayerName) ? COLOR_PLAYER_LOCAL : COLOR_PLAYER_OTHER;
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