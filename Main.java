import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        final String filename = args[0];            // The filename of the image to process
        final int squareSize; //args[1]             // The side length of the averaging square
        final String mode = args[2].toUpperCase();  // The processing mode

        // Square size validation
        try {
            squareSize = Integer.parseInt(args[1]);
            if (squareSize <= 0) {
                throw new NumberFormatException("Square size must be a positive integer.");
            }
        } catch (NumberFormatException e) {
            System.err.println("Error: Square size must be a positive integer.");
            System.exit(1);
            return; // Unreachable, but added to satisfy the compiler
        }

        // Mode validation
        if (!mode.equals("S") && !mode.equals("M")) {
            System.err.println("Error: Invalid processing mode. Use 'S' for single-threaded or 'M' for multi-threaded.");
            System.exit(1);
        }

        // Command-line argument validation
        if (args.length != 3) {
            System.err.println("Usage: java Main <filename> <square size> <mode>");
            System.err.println("Mode: 'S' for single-threaded, 'M' for multi-threaded");
            System.exit(1);
        }

        // Create the ImageAverager GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new ImageAverager(filename, squareSize, mode));
    }
}
