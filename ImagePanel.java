import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * The ImagePanel class extends JPanel to handle custom drawing of the image
 * It manages the scaling of the image for displaying
 */
public class ImagePanel extends JPanel{
    private final int displayedWidth;   // The width of the displayed (possibly scaled) image
    private final int displayedHeight;  // The height of the displayed (possibly scaled) image
    private final BufferedImage displayedImage; // The BufferedImage used for displaying the image
    private final BufferedImage originalImage;  // Reference to the original image
    
    public ImagePanel(BufferedImage originalImage) {
        this.originalImage = originalImage;
         
        // Determine screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // getScreenSize() returns the size of the screen in pixels ...
        // ... with DPI scaling if applied (important for high resolution displays)
        System.out.println("screenSize.width is: " + screenSize.width);
        System.out.println("screenSize.height is: " + screenSize.height);

        // Window Resolution (80% of width and height)
        int maxWidth = (int) (screenSize.width * 0.8);
        int maxHeight = (int) (screenSize.height * 0.8);

        // getWidth and getHeight of the inputted image to know the original size
        int imageWidth = originalImage.getWidth();
        int imageHeight = originalImage.getHeight();
        System.out.println("imageWidth is: " + imageWidth);
        System.out.println("imageHeight is: " + imageHeight);

        // Calculate scaling ratio while maintaining aspect ratio
        double widthRatio = (double) maxWidth / imageWidth;
        double heightRatio = (double) maxHeight / imageHeight;

        // Get the minimum ratio to fit the image in the window
        double ratio = Math.min(1.0, Math.min(widthRatio, heightRatio)); 
        System.out.println("Ratio is: " + ratio);

        // Scale the image dimensions
        displayedWidth = (int) (imageWidth * ratio);
        displayedHeight = (int) (imageHeight * ratio);

        // Create a scaled instance of the image for display
        displayedImage = new BufferedImage(displayedWidth, displayedHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = displayedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, displayedWidth, displayedHeight, null); // Draw the image to the scaled size
        g.dispose(); // Release resources used by the Graphics object

        // Set the preferred size of the panel
        setPreferredSize(new Dimension(displayedWidth, displayedHeight));
    }

    // Override paintComponent() to ensure proper render of image
    // This method should be overridden because it's called automatically by swing to update manually
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (displayedImage != null) {
            // Update the displayed image with the current state of originalImage
            Graphics2D g2d = displayedImage.createGraphics();
            g2d.drawImage(originalImage, 0, 0, displayedWidth, displayedHeight, null);
            g2d.dispose();
            g.drawImage(displayedImage, 0, 0, null);
        }
    }

}
