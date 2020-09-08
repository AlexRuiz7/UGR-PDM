package ugr.pdm.battleships.models;


import android.graphics.Color;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;


/**
 * Clase que representa el estado de una casilla
 */
@IgnoreExtraProperties
public class Cell implements Serializable {

    /**
     * Atributos
     **/
    @Exclude
    private int row, column;
    private int color;
    private Battleship battleship;

    /**
     * Constructor vacío requerido por Firebase
     **/
    public Cell() { }

    /**
     * Constructor con parámetros
     *
     * @param row fila
     * @param column columna
     * @param battleship barco en la casilla o null
     */
    public Cell(int row, int column, Battleship battleship) {
        this.row = row;
        this.column = column;
        this.battleship = battleship;
        color = Color.WHITE;
    }

    @Exclude
    public int getRow() {
        return row;
    }

    @Exclude
    public int getColumn() {
        return column;
    }

    public Battleship getBattleship() {
        return battleship;
    }

    public int getColor() {
        return color;
    }

    public void setBattleship(Battleship b) {
        this.battleship = b;
        setColor(Color.GREEN);
    }

    public void setColor(int aColor) {
        color = aColor;
    }

    public void damageBattleship() {
        battleship.addDamage();
        battleship = null;
        color = Color.RED;
    }
}
