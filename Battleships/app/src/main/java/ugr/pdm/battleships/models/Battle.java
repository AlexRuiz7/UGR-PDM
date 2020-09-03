package ugr.pdm.battleships.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;

@IgnoreExtraProperties
public class Battle implements Serializable {
    private String playerOneID, playerTwoID;
    private List<Cell> playerOneBoard, playerTwoBoard;
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
    public void setPlayerTowBoard(List<Cell> board) {
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
}
