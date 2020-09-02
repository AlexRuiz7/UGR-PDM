package ugr.pdm.battleships.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import ugr.pdm.battleships.R;
import ugr.pdm.battleships.models.Battleship;
import ugr.pdm.battleships.models.Cell;

public class CellView extends androidx.appcompat.widget.AppCompatImageView {

//    private int column, row;
//    private Battleship battleship;
    private Cell model;

    public Cell getModel() {
        return model;
    }

    /**
     * Constructor
     *
     * @param context contexto de la aplicación
     */
    public CellView(Context context) {
        super(context);
//        column = 0;
//        row = 0;
//        battleship = null;
        model = new Cell();
    }

    /**
     * Constructor
     * 
     * @param context contexto de la aplicación
     * @param x fila del tablero
     * @param y columna del tablero
     */
    public CellView(Context context, int x, int y) {
        super(context);
//        row = x;
//        column = y;
//        battleship = null;
        model = new Cell(x, y, null);
        setImageResource(R.drawable.cell_shape);
        setPadding(0, 0, 0, 0);
    }

    /**
     * Elimina el barco de la casilla
     */
    public void clear() {
//        battleship = null;
        model.setBattleship(null);
        setColor(Color.WHITE);
    }

    /**
     * Añade un barco a la casilla
     * 
     * @param b barco a añadir
     */
    public void setBattleship(Battleship b) {
//        this.battleship = b;
        model.setBattleship(b);
        setColor(Color.GREEN);
    }

    /**
     * @return el barco que hay en la casilla o null
     */
    public Battleship getBattleship() {
//        return battleship;
        return model.getBattleship();
    }

    /**
     * @return true si ha un barco en la casilla
     */
    public boolean hasBattleship() {
//        return battleship != null;
        return model.getBattleship() != null;
    }

    /**
     * Daña el barco que hay en la casilla
     */
    public void damageBattleship() {
//        battleship.addDamage();
//        battleship = null;

        setColor(Color.RED);
    }

    /**
     * Cambia el color de la casilla
     *
     * @param aColor nuevo color
     */
    private void setColor(final int aColor) {
        model.setColor(aColor);
        GradientDrawable drawable = (GradientDrawable) getDrawable();
        drawable.setColor(aColor);
    }
    
    /**
     * Comprueba si la casilla dada como parámetro está en la misma fila
     *
     * @param aCellView casilla a comparar
     * @return true si la casilla dada está en la misma fila
     */
    public boolean sameRow(final CellView aCellView) {
        return model.getRow() == aCellView.getRow();
    }

    /**
     * Comprueba si la casilla dada como parámetro está en la misma columna
     *
     * @param aCellView casilla a comparar
     * @return true si la casilla dada está en la misma columna
     */
    public boolean sameColumn(final CellView aCellView) {
        return model.getColumn() == aCellView.getColumn();
    }

    /**
     * @return la fila de la casilla
     */
    public int getRow() {
//        return row;
        return model.getRow();
    }

    /**
     * @return la columna de la casilla
     */
    public int getColumn() {
//        return column;
        return model.getColumn();
    }
}
