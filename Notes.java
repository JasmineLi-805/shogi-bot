import java.util.HashMap;
import java.util.LinkedList;

public class Notes extends Piece {
    public Notes(String name, boolean upper) {
        this.name = name;
        this.upper = upper;
        this.promoted = false;

        this.moves = new HashMap<>();
        Step s;
        for (int i = 1; i < 5; i++) {
            s = new Step(i, 0);
            if (i == 1) {
                this.moves.put(s, null);
            } else {
                this.moves.put(s, new LinkedList<>());
            }

            for (int j = 1; j < i; j++) {
                moves.get(s).add(new Step(j, 0));
            }
        }

        for (int i = 1; i < 5; i++) {
            s = new Step(0, i);
            if (i == 1) {
                this.moves.put(s, null);
            } else {
                this.moves.put(s, new LinkedList<>());
            }

            for (int j = 1; j < i; j++) {
                moves.get(s).add(new Step(0, j));
            }
        }

        for (int i = -1; i > -5; i--) {
            s = new Step(i, 0);
            if (i == -1) {
                this.moves.put(s, null);
            } else {
                this.moves.put(s, new LinkedList<>());
            }

            for (int j = -1; j > i; j--) {
                moves.get(s).add(new Step(j, 0));
            }
        }

        for (int i = -1; i > -5; i--) {
            s = new Step(0, i);
            if (i == -1) {
                this.moves.put(s, null);
            } else {
                this.moves.put(s, new LinkedList<>());
            }

            for (int j = -1; j > i; j--) {
                moves.get(s).add(new Step(0, j));
            }
        }
    }

    @Override
    public boolean promote() {
        if (this.promoted) {
            return false;
        }
        this.name = "+" + this.name;
        this.moves.put(new Step(1, 1), null);
        this.moves.put(new Step(-1, 1), null);
        this.moves.put(new Step(1, -1), null);
        this.moves.put(new Step(-1, -1), null);
        this.promoted = true;

        return true;
    }

}
