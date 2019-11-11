import java.util.*;

/**
 * The Shogi class manages a Shogi game and allows the client to play in interactive mode or file mode
 *     use the "java Shogi -i" command to play in interactive mode
 *     use the "java Shogi -f <file_path>" command to play in file mode.
 */
public class Shogi {
    private static Board board = new Board();   // keep track of board state
    private static List<Character> upperCapture = new LinkedList<>();   // pieces captured by UPPER user
    private static List<Character> lowerCapture = new LinkedList<>();   // pieces captured by lower user

    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("-i")) {     // play in interactive mode
            interactiveMode(true, 0);
        } else if (args.length == 2 && args[0].equals("-f")) {     // play in file mode
            // read from the file.
            Utils.TestCase testCase = null;
            try {
                testCase = Utils.parseTestCase(args[1]);
            } catch (Exception e) {
                System.out.println("Exception occurred: Failed to read from file.");
                e.printStackTrace();
            }

            // start game in file mode
            if (testCase != null) {
                fileMode(testCase);
            }
        } else {
            System.out.println("Usage: 'java Shogi -i' for interactive mode");
            System.out.println("       'java Shogi -f <file_path>' for file mode");
        }
    }

    /**
     * Plays the game in interactive mode
     *
     * @param newStart true if it is a new start of the game
     * @param r the number of rounds played before interactive mode starts
     */
    public static void interactiveMode(boolean newStart, int r) {
        // tracking the game state.
        //   gameState = 0: game continues;  gameState = 1: illegal move
        //   gameState = 2: checkmate     ;  gameState = 3: tie game
        int gameState = 0;

        // even for lower, odd for UPPER
        int round = r;

        Scanner console = new Scanner(System.in);

        // initialize the board if is a new game
        if (newStart) {
            initializeBoard();
        }

        while (gameState == 0) {
            boolean upper = round % 2 == 1;
            printState();

            // check if the current player is in check, end game if is checkmate
            if (check(upper) && !isOutOfCheck(upper)) {
                gameState = 2;
                break;
            }

            String nextMove = promptNextMove(console, upper);
            round++;
            printAction(upper, nextMove);

            // apply the move, if the move cannot be applied, it is illegal.
            if (!applyAction(nextMove, upper)) {
                gameState = 1;
                printState();
                break;
            }

            // if the game has reached too many rounds.
            if (round == 400) {
                gameState = 3;
                break;
            }
        }

        printEndGameMessage(gameState, round % 2 == 0);
    }

    /**
     * Plays the game in file mode.
     *
     * @param fileCase a TestCase parsed from the file
     */
    public static void fileMode(Utils.TestCase fileCase) {
        // setup the board and the captured pieces.
        setBoard(fileCase.initialPieces);
        setPieces(fileCase.upperCaptures, fileCase.lowerCaptures);

        // tracking the game state.
        //   gameState = 0: game continues;  gameState = 1: illegal move
        //   gameState = 2: checkmate     ;  gameState = 3: tie game
        int gameState = 0;
        int i;
        for (i = 0; i < fileCase.moves.size(); i++) {
            // it is the UPPER player's round if round number is odd. Round number starts from 0.
            boolean upper = i % 2 == 1;
            String action = fileCase.moves.get(i);
            if (i == fileCase.moves.size() - 1) {
                printAction(upper, action);
            }

            // apply the move, if could not be applied it is an illegal move, game ends
            if (!applyAction(fileCase.moves.get(i), upper)){
                gameState = 1;
                break;
            }

            // when there is too many moves, it's a tie, game ends
            if (i >= 400) {
                gameState = 3;
                break;
            }
        }

        printState();

        // when the last move is also the 400th move.
        if (i >= 400) {
            gameState = 3;
        }

        // if the current user is in check and there is no move out of check, it is a checkmate
        boolean upper = i % 2 == 1;
        if (check(upper) && !isOutOfCheck(upper)) {
            gameState = 2;
        }

        if (gameState == 0) {
            if (i % 2 == 0) {
                System.out.println("lower> ");
            } else {
                System.out.println("UPPER> ");
            }

            /* redirect to interactive mode */
            // interactiveMode(false, i);
        } else {
            printEndGameMessage(gameState, upper);
        }
    }

    /**
     * Applies the given action, return true iff the action is applied successfully.
     * If false if returned, the action is illegal.
     *
     * @param nextAction command for the action, should be either in the format
     *                   "move <coordinate> <coordinate> [promote]" or "drop <piece> <coordinate>"
     *
     * @param upper true if it is the upper player applying the action, false if is the lower player
     * @return true iff the action is successfully applied.
     */
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

            // if this move put the current player in check, it is an illegal move
            if (peekForCheckMove(upper, startP, endP)){
                return false;
            }

            Piece toPromote = board.getPiece(startP.x, startP.y);
            if (action.length == 4 && action[3].equals("promote")) {  // promote on request
                // if the piece is not starting or ending on promotion zone
                if (!(upper && endP.y == 0) && !(upper && startP.y == 0)
                        && !(!upper && endP.y == 4) && !(!upper && startP.y == 4)) {
                    return false;
                }

                if (!toPromote.promote()) {
                    return false;  // the piece cannot be promoted
                }
            } else if (toPromote.getName().equals("p") || toPromote.getName().equals("P")) {
                // forced promote if Preview is in promotion zone
                if ((upper && endP.y == 0) || (!upper && endP.y == 4)) {
                    toPromote.promote();
                }
            }

            Piece exist = board.getPiece(endP.x, endP.y);
            if (exist != null) {  // capture if there is the piece of the other player
                capture(exist, upper);
            }

            // apply the move
            board.movePiece(startP.x, startP.y, endP.x, endP.y);

        } else if (action[0].equals("drop")) {      // to drop a piece
            if (action.length != 3) {
                return false;
            }

            // if the drop action is not valid
            if (!validDrop(action[1], action[2], upper)){
                return false;
            }

            // drop and remove piece from captured list.
            char p = action[1].charAt(0);
            if (upper) {
                // if is UPPER user's round, capitalize piece name
                p = (char)((int)p - 32);
            }
            Step posi = toPosition(action[2]);

            // create and drop the piece
            Piece piece = PieceFactory.createPiece("" + p, upper, false);
            board.dropPiece(piece, posi.x, posi.y);

            // remove the piece from captured list.
            if (upper) {
                upperCapture.remove(upperCapture.indexOf(p));
            } else {
                lowerCapture.remove(lowerCapture.indexOf(p));
            }
        } else {
            return false;
        }

        return true;
    }

    public static boolean check(boolean upper) {
        // find the Drive piece for this user
        String drivePosition = "";
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Piece p = board.getPiece(i, j);
                if (p != null && upper == p.isUpper() && p.getName().toLowerCase().equals("d")){
                    drivePosition += "" + (char)(i + 97) + "" + (j + 1);
                    break;
                }
            }
        }

        if (drivePosition.equals("")) {
            System.out.println("No drive on board.");
            return false;
        }

        // for each opponent's piece, can they get to Drive in one move?
        String curr = "";
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                curr = "" + (char)(i + 97) + "" + (j + 1);
                Piece p = board.getPiece(i, j);

                if (p != null) {
                    // if is an opponent's piece
                    if (p.isUpper() != upper) {
                        if (validMove(curr, drivePosition, p.isUpper())) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
    public static boolean validDrop(String pieceName, String position, boolean upper) {
        char p = pieceName.charAt(0);
        if (upper && (int)p > 96) {
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
            if (!validPreviewDrop(posi, upper)) {
                return false;
            }
        }

        return true;
    }
    public static boolean validPreviewDrop(Step position, boolean upper){
        // if is in promotion zone
        if (upper && position.y == 0) {
            return false;
        } else if (!upper && position.y == 4) {
            return false;
        }

        // if immediate checkmate
        if (upper) {
            Piece p = board.getPiece(position.x, position.y - 1);
            if (p != null && p.getName().equals("d")) {
                return false;
            }
        } else {
            Piece p = board.getPiece(position.x, position.y + 1);
            if (p != null && p.getName().equals("D")) {
                return false;
            }
        }

        // have another same-team unpromoted preview in same column
        for (int i = 0; i < 5; i++) {
            Piece p = board.getPiece(position.x, i);
            if (upper) {
                if (p != null && p.getName().equals("P")) {
                    return false;
                }
            } else {
                if (p != null && p.getName().equals("p")) {
                    return false;
                }
            }
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
    private static boolean peekForCheckMove(boolean upper, Step ori, Step dest) {
        Piece p = board.getPiece(dest.x, dest.y);
        board.movePiece(ori.x, ori.y, dest.x, dest.y);

        boolean inCheck = check(upper);

        board.movePiece(dest.x, dest.y, ori.x, ori.y);
        board.dropPiece(p, dest.x, dest.y);

        return inCheck;
    }
    private static boolean peekForCheckDrop(Piece p, Step loc, boolean upper) {
        board.dropPiece(p, loc.x, loc.y);
        boolean inCheck = check(upper);
        board.dropPiece(null, loc.x, loc.y);

        return inCheck;
    }
    private static boolean isOutOfCheck(boolean upper) {
        List<String> suggestions = suggest(upper);

        if (suggestions.isEmpty()) {
            return false;
        } else {
            if (upper) {
                System.out.println("UPPER player is in check!");
            } else {
                System.out.println("lower player is in check!");
            }

            Collections.sort(suggestions);
            System.out.println("Available moves:");
            for (String s : suggestions) {
                System.out.println(s);
            }
            return true;
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

    private static void capture(Piece p, boolean upper) {
        String name = p.getName();
        char piece = name.charAt(name.length() - 1);
        if (upper) {
            upperCapture.add((char)(piece - 32));
        } else {
            lowerCapture.add((char)(piece + 32));
        }
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

    private static List<String> suggest(boolean upper) {
        List<String> suggestions = new LinkedList<>();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Piece p = board.getPiece(i, j);
                if (p != null && p.isUpper() == upper) {
                    for (Step s : p.getMoves().keySet()) {
                        Step ori = new Step(i, j);
                        Step dest = new Step(i + s.x, j + s.y);
                        if (dest.x < 0 || dest.y < 0 || dest.x > 4 || dest.y > 4) {
                            continue;
                        }
                        String start = stepToString(ori);
                        String end = stepToString(dest);
                        if (validMove(start, end, upper) && !peekForCheckMove(upper, ori, dest)) {
                            suggestions.add("move " + start + " " + end);
                        }
                    }
                }
            }
        }

        if (upper) {
            for (char p : upperCapture) {
                for (int i = 4; i < 5; i++) {
                    for (int j = 0; j < 5; j++) {
                        if (validDrop("" + p, stepToString(i, j), upper)) {
                            String name = "" + p;
                            Piece piece = PieceFactory.createPiece(name, upper, false);
                            if (!peekForCheckDrop(piece, new Step(i, j), upper)) {
                                suggestions.add("drop " + name.toLowerCase() + " " + stepToString(i, j));
                            }
                        }
                    }
                }
            }
        } else {
            for (char p : lowerCapture) {
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 5; j++) {
                        if (validDrop("" + p, stepToString(i, j), upper)) {
                            String name = "" + p;
                            Piece piece = PieceFactory.createPiece(name, upper, false);
                            if (!peekForCheckDrop(piece, new Step(i, j), upper)) {
                                suggestions.add("drop " + name.toLowerCase() + " " + stepToString(i, j));
                            }
                        }
                    }
                }
            }
        }


        return suggestions;
    }

    private static Step toPosition(String location) {
        return new Step((int)location.charAt(0) - 97, (int)location.charAt(1) - 49);
    }
    private static String stepToString(int x, int y) {
        String result = "";
        result += (char)(x + 97);
        result += "" + (y + 1);

        return result;
    }
    private static String stepToString(Step s) {
        String result = "";
        result += (char)((int)s.x + 97);
        result += "" + (s.y + 1);

        return result;
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
    public static void printAction(boolean upper, String action) {
        if (upper){
            System.out.println("UPPER player action: " + action);
        } else {
            System.out.println("lower player action: " + action);
        }
    }
    public static void printEndGameMessage(int gameState, boolean upper) {
        if (gameState == 1) {
            if (upper) {
                System.out.println("lower player wins.  Illegal move.");
            } else {
                System.out.println("UPPER player wins.  Illegal move.");
            }
        } else if (gameState == 2) {
            if (upper) {
                System.out.println("lower player wins.  Checkmate.");
            } else {
                System.out.println("UPPER player wins.  Checkmate.");
            }
        } else {
            System.out.println("Tie game.  Too many moves.");
        }
    }
}
