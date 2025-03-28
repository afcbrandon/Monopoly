/*
    Function that prints the spaces of the GameBoard to the Console
 */

import java.awt.Image;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class GameBoard extends JPanel {

    private Image image;

    /// Constructor
    public GameBoard(String imagePath) {
        this.image = new ImageIcon(imagePath).getImage();
    }

    /// Function that outputs a monopoly board by
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
    }

}