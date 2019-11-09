

public class Shogi {
    public static Board board = new Board();

    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("-i")) {
            System.out.println("You are in interactive mode");
        } else if (args.length == 2 && args[0].equals("-f")) {
            System.out.println("You are in file mode");
        }
    }
}
