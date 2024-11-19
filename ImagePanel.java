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
         
        // Determine screen size, assume mine is 1920x1080
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //if in DisplaySettings->Scale enabled for 125%, if not take original screenSize and skip this
        //DPI Scaling: 1920/1.25(125%) = 1536   1080/1.25(125%) = 864
        System.out.println("screenSize.width is: " + screenSize.width);     //1536
        System.out.println("screenSize.height is: " + screenSize.height);   //864

        // Window Resolution: 1228.8x691.2 (80% of width and height)
        int maxWidth = (int) (screenSize.width * 0.8);  //1536*0.8 = 1228.8
        int maxHeight = (int) (screenSize.height * 0.8);//864*0.8 = 691.2

        // Assume inputted image has 3840x2160 resolution
        int imageWidth = originalImage.getWidth();
        System.out.println("imageWidth is: " + imageWidth);     //3840
        int imageHeight = originalImage.getHeight();
        System.out.println("imageHeight is: " + imageHeight);   //2160

        // Calculate scaling ratio while maintaining aspect ratio
        double widthRatio = (double) maxWidth / imageWidth;     //// 1228.8/3840
        System.out.println("widthRatio is: " + widthRatio);     //0.31979166666666664
        double heightRatio = (double) maxHeight / imageHeight;  //// 691.2/2160
        System.out.println("heightRatio is: " + heightRatio);   //0.3199074074074074

        double ratio = Math.min(1.0, Math.min(widthRatio, heightRatio)); 
        System.out.println("Ratio is: " + ratio);               //0.31979166666666664 

        displayedWidth = (int) (imageWidth * ratio);            // 3840*0.31979166666666664 = 1227.99744
        displayedHeight = (int) (imageHeight * ratio);          // 2160*0.31979166666666664 = 690.74999

        // Create a scaled instance of the image for display
        displayedImage = new BufferedImage(displayedWidth, displayedHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = displayedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, displayedWidth, displayedHeight, null); //draw scaled image
        g.dispose(); // release resources of G2D object

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
