/**
 * Represents the x and y coordinate
 */
public class Step {
    public int x;      // x coordinate
    public int y;      // y coordinate

    public Step(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Step other) {
        return this.x == other.x && this.y == other.y;
    }
}
