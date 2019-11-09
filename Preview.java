import java.util.HashMap;

public class Preview extends Piece {
    public Preview(String name, boolean upper) {
        this.name = name;
        this.upper = upper;
        this.promoted = false;

        this.moves = new HashMap<>();
        if (upper) {
            this.moves.put(new Step(0, -1), null);
        } else {
            this.moves.put(new Step(0, 1), null);
        }
    }

    @Override
    public boolean promote() {
        if (this.promoted) {
            return false;
        }
        moves.clear();
        this.moves.put(new Step(-1, 0), null);
        this.moves.put(new Step(1, 0), null);
        this.moves.put(new Step(0, -1), null);
        this.moves.put(new Step(0, 1), null);
        if (this.upper) {
            this.moves.put(new Step(-1, -1), null);
            this.moves.put(new Step(1, -1), null);
        } else {
            this.moves.put(new Step(-1, 1), null);
            this.moves.put(new Step(1, 1), null);
        }
        this.promoted = true;

        return true;
    }
}
