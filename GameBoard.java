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

    // Function that returns the floor space number of a board space
    private int spaceIdentifier(int row, int col) {
        int floorSpace = 0;
        int floorTracker;
        
        if (row == 0) {         // First Row
            floorTracker = 21; // First tile at the top of the row is the 21st space on a monopoly board
            floorSpace = col + floorTracker;
        }
        else if (row == 10) {   // Last Row
            floorTracker = 11;  // First tile of the last row is the 11th tile
            floorSpace = floorTracker - col;
        }
        else {  // Rows 1 - 9
            switch (row) {
                case 1:
                    if (col == 0) {
                        floorSpace = 20;
                    }
                    else {
                        floorSpace = 32;
                    }
                    break;
                case 2:
                    if (col == 0) {
                        floorSpace = 19;
                    }
                    else {
                        floorSpace = 33;
                    }
                    break;
                case 3:
                    if (col == 0) {
                        floorSpace = 18;
                    }
                    else {
                        floorSpace = 34;
                    }
                    break;
                case 4:
                    if (col == 0) {
                        floorSpace = 17;
                    }
                    else {
                        floorSpace = 35;
                    }
                    break;
                case 5:
                    if (col == 0) {
                        floorSpace = 16;
                    }
                    else {
                        floorSpace = 36;
                    }
                    break;
                case 6:
                    if (col == 0) {
                        floorSpace = 15;
                    }
                    else {
                        floorSpace = 37;
                    }
                    break;
                case 7:
                    if (col == 0) {
                        floorSpace = 14;
                    }
                    else {
                        floorSpace = 38;
                    }
                    break;
                case 8:
                    if (col == 0) {
                        floorSpace = 13;
                    }
                    else {
                        floorSpace = 39;
                    }
                    break;
                default:
                    if (col == 0) {
                        floorSpace = 12;
                    }
                    else {
                        floorSpace = 40;
                    }
            }
        }

        return floorSpace;
    }
}