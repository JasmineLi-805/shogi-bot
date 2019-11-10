import java.util.*;


public class Piece {
    String name;
    Map<Step, List<Step>> moves;
    boolean promoted;
    boolean upper;

    // verify the step s is a valid move for the piece
    public boolean validMove(Step s) {
        for (Step move: moves.keySet()) {
            if (move.equals(s)){
                return true;
            }
        }
        return false;
    }

    public List<Step> getPath(Step s) {
        for (Step move: moves.keySet()) {
            if (move.equals(s)){
                return moves.get(move);
            }
        }
        return null;
    }

    // promotes this piece, returns true iff successful
    public boolean promote() { return true; }

    // getter methods
    public String getName() {
        return this.name;
    }
    public Map<Step, List<Step>> getMoves() {
        return this.moves;
    }
    public boolean isPromoted() {
        return this.promoted;
    }
    public boolean isUpper() {
        return this.upper;
    }

    public String toString() {
        return "";
    }
}
