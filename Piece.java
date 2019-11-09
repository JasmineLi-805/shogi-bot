import java.util.*;


public class Piece {
    String name;
    Map<Step, List<Step>> moves;
    boolean promoted;
    boolean upper;

    // promotes this piece, returns true iff successful
    public boolean promote() {
        return true;
    }

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
