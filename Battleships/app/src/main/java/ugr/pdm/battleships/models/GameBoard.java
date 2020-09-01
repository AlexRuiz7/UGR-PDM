package ugr.pdm.battleships.models;


public class GameBoard {
    private static int BATTLESHIPS_COUNT = 5;

    private int size;
    private int shipCount;
    private Cell[][] cells;
    private Battleship[] battleships;


    /**
     * Constructor
     *
     * @param aSize tamaño del tablero
     */
    public GameBoard(final int aSize) {
        size = aSize;
        cells = new Cell[size][size];
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
     * @param aCell celda del tablero a comprobar
     * @return true si es una posición válida, false en otro caso
     */
    public boolean isValidPosition(final Cell aCell) {
        int row = aCell.getRow();
        int col = aCell.getColumn();

        // Comprobación adyacente horizontal izquierda
        if (col - 1 >= 0)
            if (cells[row][col - 1].hasBattleship())
                return true;
        // Comprobación adyacente horizontal derecha
        if (col + 1 < size)
            if (cells[row][col + 1].hasBattleship())
                return true;
        // Comprobación adyacente vertical izquierda
        if (row - 1 >= 0)
            if (cells[row - 1][col].hasBattleship())
                return true;
        // Comprobación adyacente vertical derecha
        if (row + 1 < size)
            if (cells[row + 1][col].hasBattleship())
                return true;

        return false;
    }


    /**
     * 
     * @return
     */
    public int getBattleshipsLeft() {
        return shipCount + 1;
    }
    

    /**
     * Añade una casilla al tablero
     * 
     * @param aCell casilla a añadir
     */
    public void addCell(final Cell aCell) {
        int x = aCell.getRow();
        int y = aCell.getColumn();

        cells[x][y] = aCell;
    }
    
    
    /**
     * Devuelve el tablero a su estado inicial
     */
    public void clearBoard() {
        initBattleships();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                cells[row][col].clear();
            }
        }
    }
}
