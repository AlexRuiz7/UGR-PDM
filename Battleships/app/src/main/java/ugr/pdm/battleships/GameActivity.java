package ugr.pdm.battleships;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ugr.pdm.battleships.models.Battle;
import ugr.pdm.battleships.models.Cell;
import ugr.pdm.battleships.models.GameBoard;
import ugr.pdm.battleships.ui.CellView;


/**
 * En esta clase mato moscas a cañonazos, sry not sry
 */
public class GameActivity extends GameSetupActivity {

    public static String TAG = "GameActivity";
    private GameBoard playerTwoBoard;
    private FirebaseUser mUser;
    private boolean showPlayerOneBoard, turnIsConsumed;
    // TODO arreglar esta barbaridad y hacer que Gameboard controle su mierda
    private int playerOneSectionsLeft, playerTwoSectionsLeft;


    /**
     * Método onCreate
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Recuperar la batalla desde Intent
        currentBattle = (Battle) getIntent().getSerializableExtra(TAG);

        showPlayerOneBoard = !currentBattle.getPlayerOneHasTurn();
        turnIsConsumed = false;
        playerTwoBoard = new GameBoard(GRID_SIZE);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        playerOneSectionsLeft = 0;
        playerTwoSectionsLeft = 0;

        initViews();
    }


    /**
     * Inicializa las vistas
     */
    private void initViews() {
        Button toggleBoardsButton = findViewById(R.id.clear_button);
        toggleBoardsButton.setText(getString(R.string.alternate_board_button_text));
        toggleBoardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlayerOneBoard = !showPlayerOneBoard;
                toggleBoard();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishTurn();
                onBackPressed();
            }
        });

        updateUI();
        removeUnusedViews();
    }


    /**
     *
     */
    private void updateUI() {
        TextView title = findViewById(R.id.game_info);
//        title.setText(getString(R.string.game_info_label, mUser.getDisplayName()));
        String msg = (showPlayerOneBoard) ? "Jugador 1" : "Jugador 2";
        title.setText(getString(R.string.game_info_label, msg));
    }


    /**
     *
     */
    private void removeUnusedViews() {
        SwitchCompat switchAxe = findViewById(R.id.vertical_switch);
        ((ViewGroup) switchAxe.getParent()).removeView(switchAxe);

        TextView textView = findViewById(R.id.remaining_setup_text);
        textView.setText("");

        if (currentBattle.hasFinished()) {
            turnIsConsumed = true;
            if (currentBattle.playerOneHasTurn()) {
                textView.setText("!Has ganado!");
            } else {
                textView.setText("!Has perdido!");
            }
        }
//        ((ViewGroup) textView.getParent()).removeView(textView);

        textView = findViewById(R.id.current_ship_text);
        ((ViewGroup) textView.getParent()).removeView(textView);
    }


    /**
     *
     */
    private void toggleBoard() {
        updateUI();
        rootLayout.removeAllViews();

        for (int i = 0; i < GRID_SIZE; i++) {
            LinearLayout row = new LinearLayout(GameActivity.this);
            for (int j = 0; j < GRID_SIZE; j++) {
                if (showPlayerOneBoard) {
                    if (playerOneBoard.getCell(i, j).getParent() != null) {
                        ((ViewGroup) playerOneBoard.getCell(i, j).getParent())
                                .removeView(playerOneBoard.getCell(i, j));
                    }
                    row.addView(playerOneBoard.getCell(i, j));
                } else {
                    if (playerTwoBoard.getCell(i, j).getParent() != null) {
                        ((ViewGroup) playerTwoBoard.getCell(i, j).getParent())
                                .removeView(playerTwoBoard.getCell(i, j));
                    }
                    row.addView(playerTwoBoard.getCell(i, j));
                }
            }
            rootLayout.addView(row);
        }
    }


    /**
     *
     */
    @Override
    public void onGlobalLayout() {
        int width = rootLayout.getMeasuredWidth();
        int height = rootLayout.getMeasuredHeight();

        double sizeA = (double) width / GRID_SIZE;
        double sizeB = (double) height / GRID_SIZE;
        double smallestSize = (Math.min(sizeA, sizeB));
        int smallestSizeInt = (int) Math.floor(smallestSize);
        GridLayout.LayoutParams lp = getNewLayoutParams(smallestSizeInt);

        for (int i = 0; i < GRID_SIZE; i++) {
            LinearLayout row = new LinearLayout(GameActivity.this);
            int k = i * GRID_SIZE;
            for (int j = 0; j < GRID_SIZE; j++) {
                // Reconstruir el modelo del primer jugador
                Cell packedCell = currentBattle.getPlayerOneBoard().get(k + j);
                Cell cell = new Cell(i, j, packedCell.getBattleship());
                cell.setColor(packedCell.getColor());

                // Construir la vista
                CellView cellView = new CellView(GameActivity.this);
                cellView.setModel(cell);
                cellView.setLayoutParams(lp);
                // Mostrar tablero del enemigo
                if (currentBattle.playerOneHasTurn()) {
                    cellView.setOnClickListener(null);
                } else {
                    cellView.hide();
                    cellView.setOnClickListener(this);
                    row.addView(cellView);
                }

                // Almacenar
                playerOneBoard.addCell(cellView);

                // TODO arreglar esta barbaridad y hacer que Gameboard controle su mierda
                if (cellView.hasBattleship()) {
                    playerOneSectionsLeft++;
                }

                // Reconstruir modelo del segundo jugador
                packedCell = currentBattle.getPlayerTwoBoard().get(k + j);
                cell = new Cell(i, j, packedCell.getBattleship());
                cell.setColor(packedCell.getColor());

                // Construir la vista
                cellView = new CellView(GameActivity.this);
                cellView.setModel(cell);
                cellView.setLayoutParams(lp);
                // Mostrar tablero del enemigo
                if (currentBattle.playerOneHasTurn()) {
                    cellView.hide();
                    cellView.setOnClickListener(this);
                    row.addView(cellView);
                } else {
                    cellView.setOnClickListener(null);
                }

                // Almacenar
                playerTwoBoard.addCell(cellView);

                // TODO arreglar esta barbaridad y hacer que Gameboard controle su mierda
                if (cellView.hasBattleship()) {
                    playerTwoSectionsLeft++;
                }
            }
            rootLayout.addView(row);
        }
        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        Log.e(TAG, playerOneSectionsLeft + "");
        Log.e(TAG, playerTwoSectionsLeft + "");
    }


    /**
     * Click listener asociado a cada celda del tablero.
     *
     * @param view Celda que se ha pulsado
     */
    @Override
    public void onClick(View view) {
        final CellView cellView = (CellView) view;

        if (!turnIsConsumed) {
            if (cellView.hasBattleship()) {
                cellView.damageBattleship();
                checkEndGame();
            } else {
                cellView.setMissed();
                turnIsConsumed = true;
                confirmButton.setEnabled(true);
            }
        }
    }


    /**
     *
     */
    private void checkEndGame() {
        if (currentBattle.playerOneHasTurn()) {
            playerTwoSectionsLeft--;
            Log.e(TAG, playerTwoSectionsLeft + "");
        } else {
            playerOneSectionsLeft--;
            Log.e(TAG, playerOneSectionsLeft + "");
        }

        if (playerOneSectionsLeft <= 0 || playerTwoSectionsLeft <= 0) {
            currentBattle.setFinished();
            currentBattle.changeTurn();
            TextView textView = findViewById(R.id.remaining_setup_text);
            textView.setText("!Has ganado!");
            textView.setVisibility(View.VISIBLE);
            turnIsConsumed = true;
            confirmButton.setEnabled(true);
        }
    }


    /**
     *
     */
    private void finishTurn() {
        // Cambiar turno
        currentBattle.changeTurn();

        // Inicialización de variables necesarias
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        // Empaquetar el estado de la partida y guardar en firebase
        currentBattle.update(playerOneBoard.pack(), playerTwoBoard.pack());
        dbRef.child("battles").child(currentBattle.getBattleID()).setValue(currentBattle);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!turnIsConsumed || currentBattle.hasFinished())
                onBackPressed();
        }
        return true;
    }
}