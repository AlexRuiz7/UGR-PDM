package ugr.pdm.battleships.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;

@IgnoreExtraProperties
public class Battle implements Serializable {
    private String playerOneID, playerTwoID;
    private List<Cell> playerOneBoard, playerTwoBoard;
    private boolean hasFinished, playerOneHasTurn;
    @Exclude
    private String battleID;


    /**
     * Constructor
     *
     * @param playerOneID    id del jugador 1
     * @param playerTwoID    id del jugador 2
     * @param playerOneBoard tablero del jugador 1
     * @param playerTwoBoard tablero del jugador 2
     */
    public Battle(String playerOneID, String playerTwoID, List<Cell> playerOneBoard, List<Cell> playerTwoBoard) {
        this.playerOneID = playerOneID;
        this.playerTwoID = playerTwoID;
        this.playerOneBoard = playerOneBoard;
        this.playerTwoBoard = playerTwoBoard;
        hasFinished = false;
        playerOneHasTurn = false;
    }

    /**
     * Constructor vacío requerido por Firebase
     **/
    public Battle() {
    }


    /**
     * @return id del jugador 1
     */
    public String getPlayerOneID() {
        return playerOneID;
    }

    /**
     * @return id del jugador 2
     */
    public String getPlayerTwoID() {
        return playerTwoID;
    }

    /**
     * @return tablero del jugador 1
     */
    public List<Cell> getPlayerOneBoard() {
        return playerOneBoard;
    }

    /**
     * @return tablero del jugador 2
     */
    public List<Cell> getPlayerTwoBoard() {
        return playerTwoBoard;
    }

    /**
     * @param board tablero del jugador 2
     */
    public void setPlayerTwoBoard(List<Cell> board) {
        playerTwoBoard = board;
    }

    /**
     * @return id de la batalla
     */
    @Exclude
    public String getBattleID() {
        return battleID;
    }

    /**
     * @param id id de la batalla
     */
    public void setBattleID(String id) {
        battleID = id;
    }

    /**
     * @return true si ha acabado la partida
     */
    public boolean hasFinished() {
        return hasFinished;
    }

    public boolean getHasFinished() {
        return hasFinished;
    }

    public void setFinished() {
        hasFinished = true;
    }

    public boolean getPlayerOneHasTurn() {
        return playerOneHasTurn;
    }

    public boolean playerOneHasTurn() {
        return playerOneHasTurn;
    }

    public void changeTurn() {
        playerOneHasTurn = !playerOneHasTurn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Battle battle = (Battle) o;
        return battleID.equals(battle.battleID);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hashCode(battleID);
    }

    @Override
    public String toString() {
        return "Battle{" +
                "playerOneID='" + playerOneID + '\'' +
                ", playerTwoID='" + playerTwoID + '\'' +
                ", hasFinished=" + hasFinished +
                ", battleID='" + battleID + '\'' +
                '}';
    }

    public void update(List<Cell> boardOne, List<Cell> boardTwo) {
        playerOneBoard = boardOne;
        playerTwoBoard = boardTwo;
    }
}
