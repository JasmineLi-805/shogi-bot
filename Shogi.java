import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Shogi {
    private static Board board = new Board();
    private static List<Character> upperCapture = new LinkedList<>();
    private static List<Character> lowerCapture = new LinkedList<>();

    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("-i")) {
            interactiveGame();
        } else if (args.length == 2 && args[0].equals("-f")) {
            System.out.println("You are in file mode");
        }

        // printState();
    }

    public static void interactiveGame() {
        // tracking the game state.
        //   gameState = 0: game continues;  gameState = 1: illegal move
        //   gameState = 2: checkmate     ;  gameState = 4: tie game
        int gameState = 0;

        // even for lower, odd for UPPER
        int round = 0;

        Scanner console = new Scanner(System.in);

        initializeBoard();

        while (gameState == 0) {
            boolean upper = round % 2 == 1;
            printState();

            // check if is in check

            String nextMove = promptNextMove(console, upper);
            round++;

            // apply the move
            if (!applyAction(nextMove, upper)) {
                gameState = 1;
                break;
            }

        }

        if (gameState == 1) {
            System.out.println("Illegal move.");
        }
    }

    public static boolean applyAction(String nextAction, boolean upper) {
        String[] action = nextAction.split(" ",0);

        if (action.length > 4 || action.length < 3) {
            return false;
        }

        if (action[0].equals("move")) {
            if (!movePiece(action[1], action[2], upper)) {
                return false;
            }
        }

        return true;
    }

    public static boolean movePiece(String start, String end, boolean upper) {
        Step ori = toPosition(start);
        Piece toMove = board.getPiece(ori.x, ori.y);
        // if there is no piece at the location or the piece doesn't belong to the current player, move fail.
        if (toMove == null || toMove.isUpper() != upper) {
            return false;
        }

        Step dest = toPosition(end);
        // check if move is possible: piece.validMove, board.validMove

        // when the player already has a piece at the destination.
        Piece p = board.getPiece(dest.x, dest.y);
        if (p != null && p.isUpper() == upper) {
            return false;
        } else if (p != null) {
            // capture p
        }

        board.movePiece(ori.x, ori.y, dest.x, dest.y);
        return true;
    }

    public static Step toPosition(String location) {
        return new Step((int)location.charAt(0) - 97, (int)location.charAt(1) - 49);
    }

    public static String promptNextMove(Scanner console, boolean upper) {
        if (upper) {
            System.out.print("UPPER> ");
        } else {
            System.out.print("lower> ");
        }
        String input = console.nextLine();
        return input.trim();
    }

    public static void printState() {
        System.out.println(board.toString());

        System.out.print("Captures UPPER:");
        for (char c : upperCapture) {
            System.out.print(" " + c);
        }
        System.out.println();

        System.out.print("Captures lower:");
        for (char c : lowerCapture) {
            System.out.print(" " + c);
        }
        System.out.println();
        System.out.println();
    }

    public static void initializeBoard() {
        char[] pieces = {'D', 'R', 'P', 'S', 'N', 'G', 'd', 'r', 'p', 's', 'n', 'g'};
        int[][] position = {{5, 5}, {3, 5}, {5, 4}, {4, 5}, {1, 5}, {2, 5},
                            {1, 1}, {3, 1}, {1, 2}, {2, 1}, {5, 1}, {4, 1}};
        for (int i = 0; i < pieces.length; i++) {
            Piece p = PieceFactory.createPiece("" + pieces[i], (int)pieces[i] < 97, false);
            board.dropPiece(p, position[i][0] - 1, position[i][1] - 1);
        }
    }
}
