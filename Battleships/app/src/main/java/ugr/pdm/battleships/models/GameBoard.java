package ugr.pdm.battleships.models;


import java.util.ArrayList;
import java.util.List;

import ugr.pdm.battleships.ui.CellView;

public class GameBoard {
    private static int BATTLESHIPS_COUNT = 5;

    private int size;
    private int shipCount;
    private CellView[][] cellViews;
    private Battleship[] battleships;


    /**
     * Constructor
     *
     * @param aSize tamaño del tablero
     */
    public GameBoard(final int aSize) {
        size = aSize;
        cellViews = new CellView[size][size];
        battleships = new Battleship[BATTLESHIPS_COUNT];

        initBattleships();
    }


    /**
     * Crea la flota de barcos
     * 
     * TODO nombres en string arrray resource. Encapsular en un bucle for
     */
    private void initBattleships() {
        shipCount = 0;
        // Portaaviones
        battleships[shipCount++] = new Battleship(5, "Portaviones");
        // Destructor
        battleships[shipCount++] = new Battleship(4, "Destructor");
        // Crucero
        battleships[shipCount++] = new Battleship(3, "Crucero");
        // Submarino
        battleships[shipCount++] = new Battleship(2, "Submarino");
        // Patrullera
        battleships[shipCount++] = new Battleship(1, "Patrullera");
    }


    /**
     * Elimina y devuelve el siguiente barco de la flota.
     * 
     * @return siguiente barco de la flota o null si no queda ninguno
     */
    public Battleship getNextBattleship() {
        int l_index = BATTLESHIPS_COUNT - (shipCount--);

        return (shipCount >= 0) ? battleships[l_index] : null;
    }

    
    /**
     * Comprueba si la celda dada como parámetro es apta para añadir un barco en ella, esto es,
     * que sea adayacente a una celda con el mismo barco.
     * 
     * @param aCellView celda del tablero a comprobar
     * @return true si es una posición válida, false en otro caso
     */
    public boolean isValidPosition(final CellView aCellView) {
        int row = aCellView.getRow();
        int col = aCellView.getColumn();

        // Comprobación adyacente horizontal izquierda
        if (col - 1 >= 0)
            if (cellViews[row][col - 1].hasBattleship())
                return true;
        // Comprobación adyacente horizontal derecha
        if (col + 1 < size)
            if (cellViews[row][col + 1].hasBattleship())
                return true;
        // Comprobación adyacente vertical izquierda
        if (row - 1 >= 0)
            if (cellViews[row - 1][col].hasBattleship())
                return true;
        // Comprobación adyacente vertical derecha
        if (row + 1 < size)
            if (cellViews[row + 1][col].hasBattleship())
                return true;

        return false;
    }


    /**
     * @return número de barcos que quedan por colocar
     */
    public int getBattleshipsLeft() {
        return shipCount + 1;
    }
    

    /**
     * Añade una casilla al tablero
     * 
     * @param aCellView casilla a añadir
     */
    public void addCell(final CellView aCellView) {
        int x = aCellView.getRow();
        int y = aCellView.getColumn();

        cellViews[x][y] = aCellView;
    }
    
    
    /**
     * Devuelve el tablero a su estado inicial
     */
    public void clearBoard() {
        initBattleships();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                cellViews[row][col].clear();
            }
        }
    }


    /**
     * Prepara el estado del tablero para enviarlo a Firebase. Para ello restaura la salud de los
     * barcos y transforma la matriz que representa el tablero en una lista, de forma que pueda
     * almacenarse en Firebase
     *
     * @return el tablero almacenado en una lista
     */
    public List<Cell> pack() {
        List<Cell> cellsAsList = new ArrayList<>(size*size);

        // Revivir barcos
        for (Battleship b : battleships)
            b.revive();

        // Transformar matriz de celdas a List
        for (int i = 0; i< size; i++) {
            for (int j = 0; j < size; j++)
                cellsAsList.add(cellViews[i][j].getModel());
        }

        return cellsAsList;
    }
}
