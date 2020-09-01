package ugr.pdm.battleships.models;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import ugr.pdm.battleships.R;

public class Cell extends androidx.appcompat.widget.AppCompatImageView {

    private int column, row;
    private Battleship battleship;

    
    /**
     * Constructor
     *
     * @param context contexto de la aplicación
     */
    public Cell(Context context) {
        super(context);
        column = 0;
        row = 0;
        battleship = null;
    }

    /**
     * Constructor
     * 
     * @param context contexto de la aplicación
     * @param x fila del tablero
     * @param y columna del tablero
     */
    public Cell(Context context, int x, int y) {
        super(context);
        row = x;
        column = y;
        battleship = null;
        setImageResource(R.drawable.cell_shape);
        setPadding(0, 0, 0, 0);
    }

    /**
     * Elimina el barco de la casilla
     */
    public void clear() {
        battleship = null;
        setColor(Color.WHITE);
    }

    /**
     * Añade un barco a la casilla
     * 
     * @param b barco a añadir
     */
    public void setBattleship(Battleship b) {
        this.battleship = b;
        setColor(Color.GREEN);
    }

    /**
     * @return el barco que hay en la casilla o null
     */
    public Battleship getBattleship() {
        return battleship;
    }

    /**
     * @return true si ha un barco en la casilla
     */
    public boolean hasBattleship() {
        return battleship != null;
    }

    /**
     * Daña el barco que hay en la casilla
     */
    public void damageBattleship() {
        battleship.addDamage();
        battleship = null;
        setColor(Color.RED);
    }

    /**
     * Cambia el color de la casilla
     * 
     * @param aColor nuevo color
     */
    private void setColor(final int aColor) {
        GradientDrawable drawable = (GradientDrawable) getDrawable();
        drawable.setColor(aColor);
    }
    
    /**
     * Comprueba si la casilla dada como parámetro está en la misma fila
     *
     * @param aCell casilla a comparar
     * @return true si la casilla dada está en la misma fila
     */
    public boolean sameRow(final Cell aCell) {
        return this.row == aCell.row;
    }

    /**
     * Comprueba si la casilla dada como parámetro está en la misma columna
     *
     * @param aCell casilla a comparar
     * @return true si la casilla dada está en la misma columna
     */
    public boolean sameColumn(final Cell aCell) {
        return this.column == aCell.column;
    }

    /**
     * @return la fila de la casilla
     */
    public int getRow() {
        return row;
    }

    /**
     * @return la columna de la casilla
     */
    public int getColumn() {
        return column;
    }
}
