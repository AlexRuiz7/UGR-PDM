package ugr.pdm.battleships.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import ugr.pdm.battleships.R;
import ugr.pdm.battleships.models.Battleship;
import ugr.pdm.battleships.models.Cell;

public class CellView extends androidx.appcompat.widget.AppCompatImageView {

    // Modelo
    private Cell model;


    /**
     * Constructor
     *
     * @param context contexto de la aplicación
     */
    public CellView(Context context) {
        super(context);
        model = new Cell();
        setImageResource(R.drawable.cell_shape);
        setPadding(0, 0, 0, 0);
    }

    /**
     * Constructor
     * 
     * @param context contexto de la aplicación
     * @param x fila del tablero
     * @param y columna del tablero
     */
    public CellView(Context context, int x, int y) {
        this(context);
        model = new Cell(x, y, null);
        setColor(Color.WHITE);
    }

    /**
     * Elimina el barco de la casilla
     */
    public void clear() {
        model.setBattleship(null);
        setColor(Color.WHITE);
    }

    /**
     * Añade un barco a la casilla
     * 
     * @param b barco a añadir
     */
    public void setBattleship(Battleship b) {
        model.setBattleship(b);
        setColor(Color.GREEN);
    }

    /**
     * @return el barco que hay en la casilla o null
     */
    public Battleship getBattleship() {
        return model.getBattleship();
    }

    /**
     * @return true si ha un barco en la casilla
     */
    public boolean hasBattleship() {
        return model.getBattleship() != null;
    }

    /**
     * Daña el barco que hay en la casilla
     */
    public void damageBattleship() {
        setColor(Color.RED);
        model.damageBattleship();
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
        return model.getRow();
    }

    /**
     * @return la columna de la casilla
     */
    public int getColumn() {
        return model.getColumn();
    }

    /**
     * @return el modelo que que contiene el estado de la casilla
     */
    public Cell getModel() {
        return model;
    }

    public void setModel(Cell model) {
        this.model = model;
        setColor(model.getColor());
    }

    public void setMissed() {
        if (model.getColor() != Color.RED) {
            setColor(Color.GRAY);
            model.setColor(Color.GRAY);
        }
    }

    public void hide() {
        if (hasBattleship())
            setColor(Color.WHITE);
    }
}
