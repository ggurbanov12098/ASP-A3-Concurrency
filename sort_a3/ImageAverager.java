import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * The ImageAverager class extends JFrame and sets up the GUI.
 * It initializes the ImageProcessor and starts the image processing.
 */
public class ImageAverager extends JFrame {

    /** The original image loaded from the file. */
    private BufferedImage originalImage;

    @SuppressWarnings("unused") // just showing what squareSize is for
    /** The side length of the square block used for averaging colors. */
    private final int squareSize;

    @SuppressWarnings("unused") // just showing what squareSize is for
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
        imagePanel = new ImagePanel(originalImage);
        JScrollPane scrollPane = new JScrollPane(imagePanel);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Start processing in a separate thread to keep the GUI responsive
        new Thread(() -> {
            ImageProcessor processor = new ImageProcessor(originalImage, squareSize);

            if (mode.equals("S")) {
                processor.processImageSingleThreaded(() -> imagePanel.repaint());
            } else if (mode.equals("M")) {
                processor.processImageMultiThreaded(() -> imagePanel.repaint());
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
}
