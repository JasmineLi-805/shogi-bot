import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Shogi {
    private static Board board = new Board();
    private static List<Character> upperCapture = new LinkedList<>();
    private static List<Character> lowerCapture = new LinkedList<>();

    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("-i")) {
            interactiveMode(true, 0);
        } else if (args.length == 2 && args[0].equals("-f")) {
            Utils.TestCase testCase = null;

            try {
                testCase = Utils.parseTestCase(args[1]);
            } catch (Exception e) {
                System.out.println("Exception occurred: Failed to read from file.");
                e.printStackTrace();
            }

            if (testCase != null) {
                fileMode(testCase);
            }
        }

        /*Utils.TestCase testCase = null;

        try {
            testCase = Utils.parseTestCase("BoxShogi_Test_Cases/promoteLeavingZone.in");
            // testCase = Utils.parseTestCase(args[1]);
        } catch (Exception e) {
            System.out.println("Exception occurred: Failed to read from file.");
            e.printStackTrace();
        }

        if (testCase != null) {
            fileMode(testCase);
        }*/

        // System.out.println("You are in file mode");
    }

    public static void interactiveMode(boolean newStart, int r) {
        // tracking the game state.
        //   gameState = 0: game continues;  gameState = 1: illegal move
        //   gameState = 2: checkmate     ;  gameState = 3: tie game
        int gameState = 0;

        // even for lower, odd for UPPER
        int round = r;

        Scanner console = new Scanner(System.in);

        if (newStart) {
            initializeBoard();
        }

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
                printState();
                break;
            }

            if (round == 400) {
                gameState = 3;
                break;
            }
        }

        printEndGameMessage(gameState, round % 2 == 0);
    }

    public static void fileMode(Utils.TestCase fileCase) {
        setBoard(fileCase.initialPieces);
        setPieces(fileCase.upperCaptures, fileCase.lowerCaptures);

        int gameState = 0;
        int i;
        for (i = 0; i < fileCase.moves.size(); i++) {
            boolean upper = i % 2 == 1;
            String action = fileCase.moves.get(i);
            if (i == fileCase.moves.size() - 1) {
                printAction(upper, action);
            }

            if (!applyAction(fileCase.moves.get(i), upper)){
                gameState = 1;
                break;
            }

            if (i >= 400) {
                gameState = 3;
                break;
            }
        }

        if (i >= 400) {
            gameState = 3;
        }

        printState();
        if (gameState == 0) {
            if (i % 2 == 0) {
                System.out.println("lower> ");
            } else {
                System.out.println("UPPER> ");
            }
            // interactiveMode(false, i);
        } else {
            printEndGameMessage(gameState, i % 2 != 0);
        }
    }

    public static void setPieces(List<String> upperCaptures, List<String> lowerCaptures) {
        if (!upperCaptures.isEmpty()) {
            for (String s : upperCaptures) {
                if (s.length() > 0) {
                    upperCapture.add(s.charAt(0));
                }
            }
        }

        if (!lowerCaptures.isEmpty()) {
            for (String s : lowerCaptures) {
                if (s.length() > 0) {
                    lowerCapture.add(s.charAt(0));
                }
            }
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
            String startPosition = action[1];
            Step startP = toPosition(startPosition);
            String endPosition = action[2];
            Step endP = toPosition(endPosition);

            // check if can be moved
            if (!validMove(action[1], action[2], upper)) {
                return false;
            }


            if (action.length == 4 && action[3].equals("promote")) {  // promote on request
                //if ((upper && endP.y != 0) || (!upper && endP.y != 4)) {
                //    return false;  // the piece is not landing on promotion zone
                //}

                // if the piece is not starting or ending on promotion zone
                if (upper && endP.y == 0) {

                } else if (upper && startP.y == 0) {

                } else if (!upper && endP.y == 4) {

                } else if (!upper && startP.y == 4){

                } else {
                    return false;
                }

                Piece toPromote = board.getPiece(startP.x, startP.y);
                if (!toPromote.promote()) {
                    return false;  // the piece cannot be promoted
                }
            }

            Piece exist = board.getPiece(endP.x, endP.y);
            if (exist != null) {  // capture if there is the piece of the other player
                capture(exist, upper);
            }

            board.movePiece(startP.x, startP.y, endP.x, endP.y);

        } else if (action[0].equals("drop")) {
            if (action.length != 3) {
                return false;
            }
            return dropPiece(action[1], action[2], upper);
        } else {
            return false;
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

    public static boolean validMove(String start, String end, boolean upper) {
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
        }

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

    public static boolean setBoard(List<Utils.InitialPosition> initialPieces) {
        if (initialPieces == null) {
            return false;
        }

        for (Utils.InitialPosition piece : initialPieces) {
            Step posi = toPosition(piece.position);
            if (board.isOccupied(posi.x, posi.y)) {
                System.out.println("Square occupied.");
                return false;
            }

            String name = piece.piece;
            boolean upper = (int) name.charAt(name.length() - 1) < 97;
            Piece toSet;
            if (name.startsWith("+")) {  // not handling the case when the piece cannot be promoted
                toSet = PieceFactory.createPiece(name.substring(1), upper, true);
            } else {
                toSet = PieceFactory.createPiece(name, upper, false);
            }

            if (toSet == null) {
                System.out.println("Cannot create piece.");
                return false;
            }

            //System.out.println("right before dropping");
            board.dropPiece(toSet, posi.x, posi.y);

        }

        return true;
    }

    public static void printEndGameMessage(int gameState, boolean upper) {
        if (gameState == 1) {
            if (upper) {
                System.out.println("lower player wins.  Illegal move.");
            } else {
                System.out.println("UPPER player wins.  Illegal move.");
            }
        } else if (gameState == 2) {
            System.out.println("checkmate");
        } else {
            System.out.println("Tie game.  Too many moves.");
        }
    }
}
