import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * The ImageAverager class processes a JPEG image by averaging the colors
 * of square blocks of pixels. It supports both single-threaded and multi-threaded
 * processing modes and displays the progress in real-time within a GUI window.
 * The processed image is saved as "result.jpg".
 *
 * Usage:
 *  java ImageAverager filename square size mode
 *
 * Where:
 *  filename: Path to the JPEG image file.
 *  squaresize: Positive integer representing the side length of the averaging square.
 *  mode: 'S' = single-threaded or 'M' = multi-threaded processing.
 *
 * Example:
 *  java ImageAverager monalisa.jpg 20 S
 */

public class Main extends JFrame {

    /** The original image loaded from the file. */
    private BufferedImage originalImage;

    /** The image used for display purposes, possibly scaled down. */
    private BufferedImage displayedImage;

    /** The side length of the square block used for averaging colors. */
    private final int squareSize;

    /** The processing mode: 'S' for single-threaded, 'M' for multi-threaded. */
    private final String mode;

    /** The custom JPanel that displays the image. */
    private final ImagePanel imagePanel;

    /**
     * Constructs an ImageAverager instance, initializes the GUI, and starts processing.
     * @param filename   The path to the JPEG image file.
     * @param squareSize The side length of the averaging square.
     * @param mode       The processing mode ('S' or 'M').
     */
    public Main(String filename, int squareSize, String mode) {
        this.squareSize = squareSize;
        this.mode = mode.toUpperCase();

        // Load the image
        try {
            originalImage = ImageIO.read(new File(filename));
        } catch (IOException e) {
            System.err.println("Error: Unable to load image file.");
            System.exit(1);
        }

        // Set up the GUI
        setTitle("Image Averager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        imagePanel = new ImagePanel();
        JScrollPane scrollPane = new JScrollPane(imagePanel);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Start processing in a separate thread to keep the GUI responsive
        new Thread(() -> {
            if (mode.equals("S")) {
                processImageSingleThreaded();
            } else if (mode.equals("M")) {
                processImageMultiThreaded();
            } else {
                System.err.println("Error: Invalid processing mode. Use 'S' for single-threaded or 'M' for multi-threaded.");
                System.exit(1);
            }

            // Save the processed image
            try {
                ImageIO.write(originalImage, "jpg", new File("result.jpg"));
                System.out.println("Processing complete. Result saved as 'result.jpg'.");
            } catch (IOException e) {
                System.err.println("Error: Unable to save result image.");
            }
        }).start();
    }

    /**
     * Processes the image in a single-threaded manner, averaging each block sequentially.
     */
    private void processImageSingleThreaded() {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // Iterate over the image in squareSize increments
        for (int y = 0; y < height; y += squareSize) {
            for (int x = 0; x < width; x += squareSize) {
                processBlock(x, y);

                // Schedule a repaint on the Event Dispatch Thread (EDT)
                SwingUtilities.invokeLater(() -> imagePanel.repaint());
                try {
                    Thread.sleep(10); // Delay to visualize progress
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupt status
                }
            }
        }
        // Final repaint to ensure all changes are displayed
        SwingUtilities.invokeLater(() -> imagePanel.repaint());
    }

    /**
     * Processes the image using multiple threads, dividing the image into segments
     * based on the number of available CPU cores.
     */
    private void processImageMultiThreaded() {
        int numThreads = Runtime.getRuntime().availableProcessors();
        Thread[] threads = new Thread[numThreads];
        int height = originalImage.getHeight();
        int segmentHeight = height / numThreads;

        // Create and start threads
        for (int i = 0; i < numThreads; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                int startY = threadIndex * segmentHeight;
                int endY = (threadIndex == numThreads - 1) ? height : startY + segmentHeight;

                // Iterate over the assigned segment
                for (int y = startY; y < endY; y += squareSize) {
                    for (int x = 0; x < originalImage.getWidth(); x += squareSize) {
                        processBlock(x, y);

                        // Schedule a repaint on the EDT
                        SwingUtilities.invokeLater(() -> imagePanel.repaint());
                        try {
                            Thread.sleep(10); // Delay to visualize progress
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt(); // Restore interrupt status
                        }
                    }
                }
            });
            threads[i].start();
        }

        // Wait for all threads to complete
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                System.err.println("Error: Thread interrupted.");
                Thread.currentThread().interrupt(); // Restore interrupt status
            }
        }
        // Final repaint after all threads have finished
        SwingUtilities.invokeLater(() -> imagePanel.repaint());
    }

    /**
     * Processes a single block of the image, calculating the average color and
     * setting the entire block to this average color.
     * @param xStart The starting x-coordinate of the block.
     * @param yStart The starting y-coordinate of the block.
     */
    private void processBlock(int xStart, int yStart) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int xEnd = Math.min(xStart + squareSize, width);
        int yEnd = Math.min(yStart + squareSize, height);

        long sumRed = 0, sumGreen = 0, sumBlue = 0;
        int count = 0;

        // Accumulate RGB values within the block
        for (int y = yStart; y < yEnd; y++) {
            for (int x = xStart; x < xEnd; x++) {
                int rgb = originalImage.getRGB(x, y);
                Color color = new Color(rgb);
                sumRed += color.getRed();
                sumGreen += color.getGreen();
                sumBlue += color.getBlue();
                count++;
            }
        }

        // Calculate average RGB values
        int avgRed = (int) (sumRed / count);
        int avgGreen = (int) (sumGreen / count);
        int avgBlue = (int) (sumBlue / count);
        Color avgColor = new Color(avgRed, avgGreen, avgBlue);

        // Set the entire block to the average color
        for (int y = yStart; y < yEnd; y++) {
            for (int x = xStart; x < xEnd; x++) {
                originalImage.setRGB(x, y, avgColor.getRGB());
            }
        }
    }

    /**
     * The ImagePanel class extends JPanel to handle custom drawing of the image.
     * It manages the scaling of the image for display purposes.
     */
    private class ImagePanel extends JPanel {

        /** The width of the displayed (possibly scaled) image. */
        private final int displayedWidth;

        /** The height of the displayed (possibly scaled) image. */
        private final int displayedHeight;

        /** The BufferedImage used for displaying the image. */
        // private final BufferedImage displayedImage;

        /**
         * Constructs an ImagePanel, scaling the image if it's larger than the screen.
         */
        public ImagePanel() {
            // Determine screen size
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int maxWidth = (int) (screenSize.width * 0.8);
            int maxHeight = (int) (screenSize.height * 0.8);

            int imageWidth = originalImage.getWidth();
            int imageHeight = originalImage.getHeight();

            // Calculate scaling ratio while maintaining aspect ratio
            double widthRatio = (double) maxWidth / imageWidth;
            double heightRatio = (double) maxHeight / imageHeight;
            double ratio = Math.min(1.0, Math.min(widthRatio, heightRatio));

            displayedWidth = (int) (imageWidth * ratio);
            displayedHeight = (int) (imageHeight * ratio);

            // Create a scaled instance of the image for display
            displayedImage = new BufferedImage(displayedWidth, displayedHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = displayedImage.createGraphics();
            g.drawImage(originalImage, 0, 0, displayedWidth, displayedHeight, null);
            g.dispose();

            // Set the preferred size of the panel
            setPreferredSize(new Dimension(displayedWidth, displayedHeight));
        }

        /**
         * Overrides the paintComponent method to draw the image.
         * @param g The Graphics object used for drawing.
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Update the displayed image with the current state of originalImage
            Graphics2D g2d = displayedImage.createGraphics();
            g2d.drawImage(originalImage, 0, 0, displayedWidth, displayedHeight, null);
            g2d.dispose();
            g.drawImage(displayedImage, 0, 0, null);
        }
    }

    /**
     * The main method parses command-line arguments and initializes the application.
     * @param args Command-line arguments: <filename> <square size> <mode>
     */
    public static void main(String[] args) {
        // Validate command-line arguments
        if (args.length != 3) {
            System.err.println("Usage: java ImageAverager <filename> <square size> <mode>");
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
            System.err.println("Error: Square size must be appropriate integer.");
            System.exit(1);
            return; // Unreachable, but added to satisfy the compiler
        }

        final int squareSize = tempSquareSize; // Now squareSize is effectively final

        // Start the application on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> new Main(filename, squareSize, mode));
    }
}
