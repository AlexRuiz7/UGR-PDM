package ugr.pdm.battleships;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import ugr.pdm.battleships.models.Battleship;
import ugr.pdm.battleships.models.Cell;
import ugr.pdm.battleships.models.GameBoard;

public class GameSetupActivity extends AppCompatActivity
        implements ViewTreeObserver.OnGlobalLayoutListener, View.OnClickListener {

    private static final String TAG = "GameSetupActivity";
    public static final int GRID_SIZE = 10;

    private GameBoard playerBoard;
    private Cell previousClickedCell;
    private Battleship currentBattleship;

    private boolean placeOnVertical;

    private LinearLayout rootLayout;
    private TextView currentShipTextView;
    private TextView remainingShipsTextView;


    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_setup_layout);
        initViews();

        playerBoard = new GameBoard(GRID_SIZE);
        getNextShip();
    }


    /**
     * Inicializa las vistas
     */
    private void initViews() {
        rootLayout = findViewById(R.id.game_board);
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(this);
        remainingShipsTextView = findViewById(R.id.remaining_setup_text);
        currentShipTextView = findViewById(R.id.current_ship_text);

        initListeners();
    }


    /**
     * Inicializa los click listeners de los botones
     */
    private void initListeners () {
        Button clearGridButton = findViewById(R.id.clear_button);
        Button confirmButton = findViewById(R.id.confirm_button);
        SwitchCompat verticalSwitch = findViewById(R.id.vertical_switch);

        // Click Listener del botón LIMPIAR. Resetea el tablero
        clearGridButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerBoard.clearBoard();
                resetPreviousCell();
                getNextShip();
            }
        });

        // Click Listener del botón FINALIZAR. Guarda el tablero y continua a la siguiente pantalla
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(GameSetupActivity.this, "FINALIZAR", Toast.LENGTH_SHORT).show();
                // TODO pasar a siguiente pantalla
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
                Cell cell = new Cell(GameSetupActivity.this, i, j);

                cell.setLayoutParams(lp);

                cell.setOnClickListener(this);
                row.addView(cell);
                playerBoard.addCell(cell);
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
    private GridLayout.LayoutParams getNewLayoutParams(int layoutSize) {
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
        currentBattleship = playerBoard.getNextBattleship();
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

        String shipsLeft = getString(R.string.remaining_cells_to_setup, playerBoard.getBattleshipsLeft());
        remainingShipsTextView.setText(shipsLeft);
    }


    /**
     *  Limpia el contenido de previousClickedCell
     */
    private void resetPreviousCell() {
        previousClickedCell = null;
    }


    /**
     * Click listener asociado a cada celda del tablero.
     *
     * @param view Celda que se ha pulsado
     */
    @Override
    public void onClick(View view) {
        final Cell cell = (Cell) view;

        // No se aplican condicones en la primera cassilla
        if (previousClickedCell == null) {
            setBattleshipSection(cell);
            previousClickedCell = cell;
        }
        // El resto de casillas deben cumplir una serie de condiciones.
        //  1. Deben estar en la misma fila o columna dependiendo del modo seleccionado en el Switch
        //  2. Deben ser adyacentes
        //  3. Deben quedar barcos (o secciones del mismo) por colocar
        else {
            if (placeOnVertical) {
                if (cell.sameColumn(previousClickedCell)) {
                    setBattleshipSection(cell);
                }
            } else if (cell.sameRow(previousClickedCell)) {
                setBattleshipSection(cell);
            }
        }

    }


    /**
     * Coloca una sección del barco actual en el tablero, si se cumplen las condicones necesarias.
     *
     * @param cell casilla en la que colocar la sección del barco
     */
    private void setBattleshipSection(Cell cell) {
        if (currentBattleship != null) {

            // Se añade la sección del barco a la casilla y se elimina dicha sección del barco
            // con addDamage()
            if (playerBoard.isValidPosition(cell) && playerBoard.getBattleshipsLeft() != 0 || previousClickedCell == null) {
                currentBattleship.addDamage();
                cell.setBattleship(currentBattleship);
            }

            // No quedan secciones del barco por colocar, se pasa al siguiente.
            if (currentBattleship.isDestroyed()) {
                getNextShip();
            }
        }
    }
}

