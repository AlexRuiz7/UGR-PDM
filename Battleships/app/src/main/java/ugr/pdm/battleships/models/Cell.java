package ugr.pdm.battleships.models;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Cell {

    @Exclude
    private int row, column;
    private Battleship battleship;
    private int color;

    public Cell() { }

    public Cell(int row, int column, Battleship battleship) {
        this.row = row;
        this.column = column;
        this.battleship = battleship;
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
    }

    public void setColor(int aColor) {
        color = aColor;
    }
}
