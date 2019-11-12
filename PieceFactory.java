
public class PieceFactory {

    /**
     * creates a piece based on the given piece name and other information.
     *
     * @param name the name of the piece
     * @param upper true if the piece belongs to the UPPER player
     * @param promote true if the piece is promoted
     * @return a piece created based on the parameters above.
     */
    public static Piece createPiece(String name, boolean upper, boolean promote) {
        Piece piece = null;

        // create different types of pieces based on the piece name
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

        // promote the piece is asked to
        if (promote) {
            piece.promote();
        }

        return piece;
    }
}
