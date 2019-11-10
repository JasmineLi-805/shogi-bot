/**
 * Class to represent Box Shogi board
 */
public class Board {

    Piece[][] board;

    final int BOARD_SIZE = 5;

    // Initializes the starting board
    public Board() {
    	this.board = new Piece[BOARD_SIZE][BOARD_SIZE];
    }

    // drops the given piece on the board
    public void dropPiece(Piece p, int col, int row) {
        this.board[col][row] = p;
    }

    public void movePiece(int c1, int r1, int c2, int r2) {
        this.board[c2][r2] = this.board[c1][r1];
        this.board[c1][r1] = null;
    }

    public Piece getPiece(int col, int row) {
        return this.board[col][row];
    }

    /* Print board */
    public String toString() {
        String[][] pieces = new String[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece curr = board[col][row];

                if (curr != null) {
                    pieces[col][row] = curr.getName();
                } else {
                    pieces[col][row] = "";
                }
                // pieces[col][row] = this.isOccupied(col, row) ? board[col][row].toString() : "";
            }
        }
        return stringifyBoard(pieces);
    }

    private boolean isOccupied(int col, int row) {
        return board[col][row] != null;
    }

    private String stringifyBoard(String[][] board) {
        String str = "";

        for (int row = board.length - 1; row >= 0; row--) {

            str += Integer.toString(row + 1) + " |";
            for (int col = 0; col < board[row].length; col++) {
                str += stringifySquare(board[col][row]);
            }
            str += System.getProperty("line.separator");
        }

        str += "    a  b  c  d  e" + System.getProperty("line.separator");

        return str;
    }

    private String stringifySquare(String sq) {
        switch (sq.length()) {
            case 0:
                return "__|";
            case 1:
                return " " + sq + "|";
            case 2:
                return sq + "|";
        }

        throw new IllegalArgumentException("Board must be an array of strings like \"\", \"P\", or \"+P\"");
    }
}

