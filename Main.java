import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Validate command-line arguments
        if (args.length != 3) {
            System.err.println("Usage: java Main <filename> <square size> <mode>");
            System.err.println("Mode: 'S' for single-threaded, 'M' for multi-threaded");
            System.exit(1);
        }

        // Mode validation
        final String mode = args[2].toUpperCase();
        if (!mode.equals("S") && !mode.equals("M")) {
            System.err.println("Error: Invalid processing mode. Use 'S' for single-threaded or 'M' for multi-threaded.");
            System.exit(1);
        }


        int tempSquareSize;
        // Parse and validate the square size argument
        try {
            tempSquareSize = Integer.parseInt(args[1]);
            if (tempSquareSize <= 0) {
                throw new NumberFormatException("Square size must be a positive integer.");
            }
        } catch (NumberFormatException e) {
            System.err.println("Error: Square size must be a positive integer.");
            System.exit(1);
            return; // Unreachable, but added to satisfy the compiler
        }
        
        final String filename = args[0];
        final int squareSize = tempSquareSize; // Now squareSize is final
        // final String mode = args[2].toUpperCase();
        SwingUtilities.invokeLater(() -> new ImageAverager(filename, squareSize, mode));
    }
}
