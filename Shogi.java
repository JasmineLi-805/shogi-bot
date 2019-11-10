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

            // to implement:
            // check if is in check for both sides
            // if the current player in check, print possible moves
            // else just end game

            String nextMove = promptNextMove(console, upper);
            round++;
            printAction(upper, nextMove);

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

    public static void printAction(boolean upper, String action) {
        if (upper){
            System.out.println("UPPER player action: " + action);
        } else {
            System.out.println("lower player action: " + action);
        }
    }

    public static boolean applyAction(String nextAction, boolean upper) {
        String[] action = nextAction.split(" ",0);

        if (action.length > 4 || action.length < 3) {
            return false;
        }

        if (action[0].equals("move")) {
            // perform move
            if (!movePiece(action[1], action[2], upper)) {
                return false;
            }

            // promote if requested
            if (action.length == 4 && action[3].equals("promote")) {
                // implement promote
            }
        } else if (action[0].equals("drop")) {
            if (action.length != 3) {
                return false;
            }
            return dropPiece(action[1], action[2], upper);
        }


        return true;
    }

    public static boolean dropPiece(String pieceName, String position, boolean upper) {
        char p = pieceName.charAt(0);
        if (upper) {
            // if is UPPER user's round, capitalize piece name
            p = (char)((int)p - 32);
        }

        // if player is dropping a piece never been captured
        if (upper && !upperCapture.contains(p)) {
            return false;
        } else if (!upper && !lowerCapture.contains(p)) {
            return false;
        }

        Step posi = toPosition(position);
        if (board.isOccupied(posi.x, posi.y)) {
            return false;
        }

        if (p == 'p' || p == 'P'){
            // implement special case for preview piece.
        }

        // drop and remove piece from captured list.
        Piece piece = PieceFactory.createPiece("" + p, upper, false);
        board.dropPiece(piece, posi.x, posi.y);
        if (upper) {
            upperCapture.remove(upperCapture.indexOf(p));
        } else {
            lowerCapture.remove(lowerCapture.indexOf(p));
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
        Step step = new Step(dest.x - ori.x, dest.y - ori.y);
        if (!validPath(toMove, ori, step)) {
            return false;
        }

        // when the player already has a piece at the destination.
        Piece p = board.getPiece(dest.x, dest.y);
        if (p != null && p.isUpper() == upper) {
            return false;
        } else if (p != null) {  // capture if there is the piece of the other player
            capture(p, upper);
        }

        board.movePiece(ori.x, ori.y, dest.x, dest.y);
        return true;
    }

    private static void capture(Piece p, boolean upper) {
        String name = p.getName();
        char piece = name.charAt(name.length() - 1);
        if (upper) {
            upperCapture.add((char)(piece - 32));
        } else {
            lowerCapture.add((char)(piece + 32));
        }
    }

    private static boolean validPath(Piece p, Step ori, Step move) {
        // check if move is possible for the piece
        if (!p.validMove(move)) {
            return false;
        }

        // check if there is blockage in this move
        List<Step> steps = p.getPath(move);
        if (steps != null) {
            for (Step s : steps) {
                if (board.isOccupied(ori.x + s.x, ori.y + s.y)) {
                    return false;
                }
            }
        }

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
