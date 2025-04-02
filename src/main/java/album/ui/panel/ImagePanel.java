package album.ui.panel;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {
    private Image image;
    private int width;
    private int height;

    public void setImageSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 10, 10, width, height, this);
        }
    }
}
