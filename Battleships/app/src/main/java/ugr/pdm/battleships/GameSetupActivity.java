package ugr.pdm.battleships;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ugr.pdm.battleships.models.Battle;
import ugr.pdm.battleships.models.Battleship;
import ugr.pdm.battleships.models.Friend;
import ugr.pdm.battleships.models.GameBoard;
import ugr.pdm.battleships.ui.CellView;

public class GameSetupActivity extends AppCompatActivity
        implements ViewTreeObserver.OnGlobalLayoutListener, View.OnClickListener {

    private static final String TAG = "GameSetupActivity";
    public static final int GRID_SIZE = 10;

    protected GameBoard playerOneBoard;
    private CellView previousClickedCellView;
    private Battleship currentBattleship;
    protected Battle currentBattle;

    private boolean placeOnVertical;

    protected LinearLayout rootLayout;
    private TextView currentShipTextView;
    private TextView remainingShipsTextView;
    protected Button confirmButton;


    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_setup_layout);
        initViews();

        currentBattle = null;
        // Recuperamos el estado de la partida
        if (getIntent().hasExtra(BattlesActivity.TAG)) {
            currentBattle = (Battle) getIntent().getSerializableExtra(BattlesActivity.TAG);
            currentBattle.changeTurn();
        }

        playerOneBoard = new GameBoard(GRID_SIZE);
        getNextShip();
    }


    /**
     * Este método se ejecuta como resultado de la actividad lanzada por esta actividad para
     * seleccionar al segundo jugador de la partida.
     *
     * Las responsabilidades de este método es recuperar el amigo seleccionado y llamar al método
     * sendBattle que realiza las subrutinas de gestión necesarias relacionadas con Firebase
     *
     * @param requestCode código de petición con la que se lanzó la nueva actividad
     * @param resultCode código de respuesta que devuelve la actividad lanzada
     * @param data datos que devuelve la actividad lanzada
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == 0 && data != null) {
            if (data.hasExtra(FriendsActivity.TAG)) {
                Friend selectedFriend = (Friend) data.getSerializableExtra(FriendsActivity.TAG);
                createBattle(selectedFriend);
                Toast.makeText(this, "Invitación enviada", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }
    }

    /**
     * Inicializa las vistas
     */
    private void initViews() {
        rootLayout = findViewById(R.id.game_board);
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(this);
        remainingShipsTextView = findViewById(R.id.remaining_setup_text);
        currentShipTextView = findViewById(R.id.current_ship_text);
        confirmButton = findViewById(R.id.confirm_button);
        confirmButton.setEnabled(false);

        initListeners();
    }


    /**
     * Inicializa los click listeners de los botones
     */
    private void initListeners () {
        Button clearGridButton = findViewById(R.id.clear_button);
        SwitchCompat verticalSwitch = findViewById(R.id.vertical_switch);

        // Click Listener del botón LIMPIAR. Resetea el tablero
        clearGridButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmButton.setEnabled(false);
                playerOneBoard.clearBoard();
                resetPreviousCell();
                getNextShip();
            }
        });

        // Click Listener del botón FINALIZAR.
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentBattle == null) {
                    // Abre la ventana de amigos para seleccionar al segundo jugador
                    Intent selectFriendIntent = new Intent(GameSetupActivity.this, FriendsActivity.class);
                    startActivityForResult(selectFriendIntent, 0);
                } else {
                    currentBattle.setPlayerTwoBoard(playerOneBoard.pack());
                    DatabaseReference battleRef = FirebaseDatabase.getInstance().getReference("battles");
                    battleRef.child(currentBattle.getBattleID()).setValue(currentBattle);
                    onBackPressed();
                }
            }
        });

        // Click Listener del interruptor VERTICAL. Resetea el tablero
        verticalSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                placeOnVertical = isChecked;
                resetPreviousCell();
            }
        });
    }


    /**
     * TreeViewListener. Se utiliza para generar el tablero de forma que se ajuste al tamaño de
     * pantalla del dispositvo, asegurando que la proporción correcta de GRID_SIZE x GRID_SIZE
     * con todas las celdas del mismo tamaño.
     *
     * El tamaño de cada celda se calcula como la distancia del layout padre entre GRID_SIZE. El
     * tamaño del conetendor padre lo obtenemos con getMeasuredWitdh, getMeasuredHeight.
     * Una vez calculado el tamaño de la celda se le asigna explícitamente con LayoutParams
     *
     * Finalmente se añade la celda al layout, se le añade su onClickListener y se añade al tablero.
     *
     * El TreeViewListener se elimina al final del proceso para evitar su ejecución continua.
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
            LinearLayout row = new LinearLayout(GameSetupActivity.this);

            for (int j = 0; j < GRID_SIZE; j++) {
                CellView cellView = new CellView(GameSetupActivity.this, i, j);
                cellView.setLayoutParams(lp);
                cellView.setOnClickListener(this);
                row.addView(cellView);
                playerOneBoard.addCell(cellView);
            }
            rootLayout.addView(row);
        }
        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }


    /**
     * Genera y devuelve un LayoutParams con el tamaño definido por layoutSize
     *
     * @param layoutSize tamaño del layout
     * @return nuevos parámetros de tipo LayoutParams
     */
    protected GridLayout.LayoutParams getNewLayoutParams(int layoutSize) {
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
        lp.width = layoutSize;
        lp.height = layoutSize;
        lp.leftMargin = 0;
        lp.rightMargin = 0;
        lp.topMargin = 0;
        lp.bottomMargin = 0;
        return lp;
    }


    /**
     * Recupera el siguiente barco a colocar y llama a actualizar la IU
     */
    private void getNextShip() {
        currentBattleship = playerOneBoard.getNextBattleship();
        resetPreviousCell();
        updateUI();
    }


    /**
     * Actualiza la IU en base al barco actual
     */
    private void updateUI() {
        String currentShipName = getString(R.string.placing_ship_label, "-");
        if (currentBattleship != null) {
            currentShipName = getString(R.string.placing_ship_label, currentBattleship.getName());
        }
        currentShipTextView.setText(currentShipName);

        String shipsLeft = getString(R.string.remaining_cells_to_setup, playerOneBoard.getBattleshipsLeft());
        remainingShipsTextView.setText(shipsLeft);

        if (playerOneBoard.getBattleshipsLeft() == 0) {
            confirmButton.setEnabled(true);
        }
    }


    /**
     *  Limpia el contenido de previousClickedCell
     */
    private void resetPreviousCell() {
        previousClickedCellView = null;
    }


    /**
     * Click listener asociado a cada celda del tablero.
     *
     * @param view Celda que se ha pulsado
     */
    @Override
    public void onClick(View view) {
        final CellView cellView = (CellView) view;

        // No se aplican condiciones en la primera cassilla
        if (previousClickedCellView == null) {
            setBattleshipSection(cellView);
            previousClickedCellView = cellView;
        }
        // El resto de casillas deben cumplir una serie de condiciones.
        //  1. Deben estar en la misma fila o columna dependiendo del modo seleccionado en el Switch
        //  2. Deben ser adyacentes
        //  3. Deben quedar barcos (o secciones del mismo) por colocar
        else {
            if (placeOnVertical) {
                if (cellView.sameColumn(previousClickedCellView)) {
                    setBattleshipSection(cellView);
                }
            } else if (cellView.sameRow(previousClickedCellView)) {
                setBattleshipSection(cellView);
            }
        }

    }


    /**
     * Coloca una sección del barco actual en el tablero, si se cumplen las condicones necesarias.
     *
     * @param cellView casilla en la que colocar la sección del barco
     */
    private void setBattleshipSection(CellView cellView) {
        if (currentBattleship != null) {

            // Se añade la sección del barco a la casilla y se elimina dicha sección del barco
            // con addDamage()
            if (playerOneBoard.isValidPosition(cellView) && playerOneBoard.getBattleshipsLeft() != 0 || previousClickedCellView == null) {
                currentBattleship.addDamage();
                cellView.setBattleship(currentBattleship);
            }

            // No quedan secciones del barco por colocar, se pasa al siguiente.
            if (currentBattleship.isDestroyed()) {
                getNextShip();
            }
        }
    }

    /**
     * Registra una nueva partida en Firebase
     *
     * @param selectedFriend amigo seleccionado como segundo jugador de la partida
     */
    private void createBattle (Friend selectedFriend) {
        // Inicialización de variables necesarias
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef;

        // Crear referencias y registros para la nueva partida
        dbRef = db.getReference("battles");
        DatabaseReference battleRef = dbRef.push();
        String battleID = battleRef.getKey();

        // Empaquetar el estado de la partida y guardar en firebase
        Battle battle = new Battle(mUser.getUid(), selectedFriend.getPersonId(), playerOneBoard.pack(), null);
        battleRef.setValue(battle);

        // Apuntar referecia a users
        dbRef = db.getReference("users");

        // Enviar envitación a selectedFriend
        // False indica que la partida no está en juego
        dbRef
            .child(selectedFriend.getPersonId())
            .child("battleInvites")
            .child(battleID)
            .setValue(false);

        // Registrar ID de partida en el perfil del usuario
        dbRef
                .child(mUser.getUid())
                .child("battles")
                .child(battleID)
                .setValue(true);
    }
}

