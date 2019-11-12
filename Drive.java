import java.util.HashMap;

/**
 * Represents a drive piece in BoxShogi
 */
public class Drive extends Piece {
    public Drive(String name, boolean upper) {
        this.name = name;
        this.upper = upper;
        this.promoted = false;

        this.moves = new HashMap<>();
        this.moves.put(new Step(-1, 0), null);
        this.moves.put(new Step(1, 0), null);
        this.moves.put(new Step(0, -1), null);
        this.moves.put(new Step(0, 1), null);
        this.moves.put(new Step(-1, -1), null);
        this.moves.put(new Step(-1, 1), null);
        this.moves.put(new Step(1, -1), null);
        this.moves.put(new Step(1, 1), null);
    }

    @Override
    public boolean promote() {
        return false;
    }
}
