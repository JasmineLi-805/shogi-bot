public class Step {
    int x;
    int y;

    public Step(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Step other) {
        return this.x == other.x && this.y == other.y;
    }

}
