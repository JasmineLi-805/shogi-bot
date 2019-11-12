import java.util.*;

/**
 * Piece represents a piece in the BoxShogi game.
 */
public class Piece {
    String name;                    // name of the piece
    Map<Step, List<Step>> moves;    // possible moves and the path each move would take
    boolean promoted;               // true if the piece is promoted
    boolean upper;                  // true if belongs to the UPPER player

    /**
     * verifies if s is a valid move for the current piece, returns true if it is.
     *
     * @param s the move to be verified
     * @return true iff s is a valid move for the piece.
     */
    public boolean validMove(Step s) {
        for (Step move: moves.keySet()) {
            if (move.equals(s)){
                return true;
            }
        }
        return false;
    }

    /**
     * returns a list of move for this piece to take the step s
     * for example, if a piece needs to take the step (4, 0),
     * the steps it need to take to do so would be [(1, 0), (2, 0), (3, 0)]
     *
     * @param s the move
     * @return a list of move for this piece to take the step s
     */
    public List<Step> getPath(Step s) {
        for (Step move: moves.keySet()) {
            if (move.equals(s)){
                return moves.get(move);
            }
        }
        return null;
    }

    /**
     * promotes this piece, return true iff successful
     *
     * @return true iff the piece is successfully promoted
     */
    public boolean promote() { return true; }

    /**
     * gets the name of the piece
     *
     * @return the name of the piece
     */
    public String getName() {
        return this.name;
    }

    /**
     * gets the moves and the paths for each move for this piece.
     *
     * @return a map representing the possible moves and paths needed for this piece
     */
    public Map<Step, List<Step>> getMoves() {
        return this.moves;
    }

    /**
     * returns true iff this piece is promoted
     *
     * @return true iff this piece is promoted
     */
    public boolean isPromoted() {
        return this.promoted;
    }
    /**
     * returns true iff this piece belongs to the UPPER player
     *
     * @return true iff this piece belongs to the UPPER player
     */
    public boolean isUpper() {
        return this.upper;
    }


    /*public String toString() {
        return "";
    }*/
}
