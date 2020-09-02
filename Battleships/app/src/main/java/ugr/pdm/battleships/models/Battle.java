package ugr.pdm.battleships.models;

import java.util.List;

public class Battle {
    private String playerOneID, playerTwoID;
    private List<Cell> playerOneBoard, playerTwoBoard;

    public Battle(String playerOneID, String playerTwoID, List<Cell> playerOneBoard, List<Cell> playerTwoBoard) {
        this.playerOneID = playerOneID;
        this.playerTwoID = playerTwoID;
        this.playerOneBoard = playerOneBoard;
        this.playerTwoBoard = playerTwoBoard;
    }

    public Battle() { }

    public String getPlayerOneID() {
        return playerOneID;
    }

    public String getPlayerTwoID() {
        return playerTwoID;
    }

    public List<Cell> getPlayerOneBoard() {
        return playerOneBoard;
    }

    public List<Cell> getPlayerTwoBoard() {
        return playerTwoBoard;
    }

}
