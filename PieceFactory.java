public class PieceFactory {
    public Piece createPiece(String name, boolean upper, boolean promote) {
        Piece piece = null;

        if (name.endsWith("D") || name.endsWith("d")) {
            piece = new Drive(name, upper);
        } else if (name.endsWith("N") || name.endsWith("n")) {
            piece = new Notes(name, upper);
        } else if (name.endsWith("G") || name.endsWith("g")) {
            piece = new Governance(name, upper);
        } else if (name.endsWith("S") || name.endsWith("s")) {
            piece = new Shield(name, upper);
        } else if (name.endsWith("R") || name.endsWith("r")) {
            piece = new Relay(name, upper);
        } else if (name.endsWith("P") || name.endsWith("p")) {
            piece = new Preview(name, upper);
        } else {
            return piece;
        }

        if (promote) {
            piece.promote();
        }

        return piece;
    }
}
