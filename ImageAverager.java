import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;


public class ImageAverager extends JFrame{
    private BufferedImage originalImage;
    @SuppressWarnings("unused")
    private final int squareSize;   // The side length of the averaging square
    @SuppressWarnings("unused")
    private final String mode;  // The processing mode ("S" for single-threaded, "M" for multi-threaded)
    private final ImagePanel imagePanel; // The custom JPanel that displays the image

    public ImageAverager(String filename, int squareSize, String mode) {
        this.squareSize = squareSize;
        this.mode = mode.toUpperCase();
        try {
            originalImage = ImageIO.read(new File(filename));
        } catch (IOException e) {
            System.err.println("Error: Unable to load image file.");
            System.exit(1);
        }
        
        // Set up the JFrame GUI
        setTitle("Pixelator"); // Title of custom (JFrame) window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Specifies what happens when the user closes the window
        imagePanel = new ImagePanel(originalImage); // new class for GUI, to handle and display the image
        getContentPane().add(imagePanel, BorderLayout.CENTER); //adds image to content pane and places center
        pack(); // auto-sizes the window to fit the preferred size and layout
        setLocationRelativeTo(null); // centers the window on the screen (because of null)
        setVisible(true); // Visible to the user

        // Start processing in appropriate thread to keep GUI responsive
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

            // Saving processed image
            try {
                ImageIO.write(originalImage, "jpg", new File("result.jpg"));
                System.out.println("Processing complete. Result saved as 'result.jpg'.");
            } catch (IOException e) {
                System.err.println("Error: Failed to save result image.");
            }
        }).start();
    }
}
