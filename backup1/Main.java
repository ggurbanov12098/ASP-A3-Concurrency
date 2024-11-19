import javax.swing.SwingUtilities;

/**
 * The Main class contains the main method, which is the entry point of the application.
 * It parses command-line arguments and initializes the ImageAverager.
 */
public class Main {

    /**
     * The main method parses command-line arguments and starts the application.
     * @param args Command-line arguments: <filename> <square size> <mode>
     */
    public static void main(String[] args) {
        // Validate command-line arguments
        if (args.length != 3) {
            System.err.println("Usage: java Main <filename> <square size> <mode>");
            System.err.println("Mode: 'S' for single-threaded, 'M' for multi-threaded");
            System.exit(1);
        }

        final String filename = args[0];
        final String mode = args[2].toUpperCase();
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

        final int squareSize = tempSquareSize; // Now squareSize is effectively final

        // Start the application on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> new ImageAverager(filename, squareSize, mode));
    }
}
