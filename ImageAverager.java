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
public class ImageAverager extends JFrame {

    private BufferedImage originalImage;
    private BufferedImage displayedImage;
    private int squareSize;
    private String mode;
    private ImagePanel imagePanel;

    public ImageAverager(String filename, int squareSize, String mode) {
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
        // JScrollPane scrollPane = new JScrollPane(imagePanel);
        // getContentPane().add(scrollPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Start processing in a separate thread
        new Thread(() -> {
            if (mode.equals("S")) {
                processImageSingleThreaded();
            } else if (mode.equals("M")) {
                processImageMultiThreaded();
            } else {
                System.err.println("Error: Invalid processing mode. Use 'S' for single-threaded or 'M' for multi-threaded.");
                System.exit(1);
            }

            // Save the result
            try {
                ImageIO.write(originalImage, "jpg", new File("result.jpg"));
                System.out.println("Processing complete. Result saved as 'result.jpg'.");
            } catch (IOException e) {
                System.err.println("Error: Unable to save result image.");
            }
        }).start();
    }

    private void processImageSingleThreaded() {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        for (int y = 0; y < height; y += squareSize) {
            for (int x = 0; x < width; x += squareSize) {
                processBlock(x, y);

                // Update display after each block
                SwingUtilities.invokeLater(() -> imagePanel.repaint());

                try {
                    Thread.sleep(10); // Add delay to observe progress
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        // Final repaint
        SwingUtilities.invokeLater(() -> imagePanel.repaint());
    }

    private void processImageMultiThreaded() {
        int numThreads = Runtime.getRuntime().availableProcessors();
        Thread[] threads = new Thread[numThreads];
        int height = originalImage.getHeight();
        int segmentHeight = height / numThreads;

        for (int i = 0; i < numThreads; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                int startY = threadIndex * segmentHeight;
                int endY = (threadIndex == numThreads - 1) ? height : startY + segmentHeight;

                for (int y = startY; y < endY; y += squareSize) {
                    for (int x = 0; x < originalImage.getWidth(); x += squareSize) {
                        processBlock(x, y);

                        // Update display after each block
                        SwingUtilities.invokeLater(() -> imagePanel.repaint());

                        try {
                            Thread.sleep(10); // Add delay to observe progress
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
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
                Thread.currentThread().interrupt();
            }
        }
        // Final repaint
        SwingUtilities.invokeLater(() -> imagePanel.repaint());
    }

    private void processBlock(int xStart, int yStart) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int xEnd = Math.min(xStart + squareSize, width);
        int yEnd = Math.min(yStart + squareSize, height);

        long sumRed = 0, sumGreen = 0, sumBlue = 0;
        int count = 0;

        // Sum up the colors
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

        // Calculate average color
        int avgRed = (int) (sumRed / count);
        int avgGreen = (int) (sumGreen / count);
        int avgBlue = (int) (sumBlue / count);
        Color avgColor = new Color(avgRed, avgGreen, avgBlue);

        // Set the block to average color
        for (int y = yStart; y < yEnd; y++) {
            for (int x = xStart; x < xEnd; x++) {
                originalImage.setRGB(x, y, avgColor.getRGB());
            }
        }
    }

    private class ImagePanel extends JPanel {

        private int displayedWidth;
        private int displayedHeight;

        public ImagePanel() {
            // Resize image if larger than screen
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int maxWidth = (int) (screenSize.width * 0.8);
            int maxHeight = (int) (screenSize.height * 0.8);

            int imageWidth = originalImage.getWidth();
            int imageHeight = originalImage.getHeight();

            double widthRatio = (double) maxWidth / imageWidth;
            double heightRatio = (double) maxHeight / imageHeight;
            double ratio = Math.min(1.0, Math.min(widthRatio, heightRatio));

            displayedWidth = (int) (imageWidth * ratio);
            displayedHeight = (int) (imageHeight * ratio);

            displayedImage = new BufferedImage(displayedWidth, displayedHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = displayedImage.createGraphics();
            g.drawImage(originalImage, 0, 0, displayedWidth, displayedHeight, null);
            g.dispose();

            setPreferredSize(new Dimension(displayedWidth, displayedHeight));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Update displayedImage
            Graphics2D g2d = displayedImage.createGraphics();
            g2d.drawImage(originalImage, 0, 0, displayedWidth, displayedHeight, null);
            g2d.dispose();
            g.drawImage(displayedImage, 0, 0, null);
        }
    }

    public static void main(String[] args) {
        // Validate command-line arguments
        if (args.length != 3) {
            System.err.println("Usage: java ImageAverager <filename> <square size> <mode>");
            System.err.println("Mode: 'S' for single-threaded, 'M' for multi-threaded");
            System.exit(1);
        }

        final String filename = args[0];
        final String mode = args[2].toUpperCase();
        int tempSquareSize = 0;

        try {
            tempSquareSize = Integer.parseInt(args[1]);
            if (tempSquareSize <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            System.err.println("Error: Square size must be a positive integer.");
            System.exit(1);
        }

        final int squareSize = tempSquareSize; // Now squareSize is effectively final

        // Start the application on the EDT
        SwingUtilities.invokeLater(() -> new ImageAverager(filename, squareSize, mode));
    }
}
