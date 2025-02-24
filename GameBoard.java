/*
    Function that prints the spaces of the GameBoard to the Console
 */
public class GameBoard {
    public static void Board() {
        // For-loop that prints the 40 spaces of the Monopoly Board
        for (int i = 1; i <= 11; i++) {
            /* 
                Inner for loop that determines if a board space is printed, or an empty space.
                If the loop is on the first (1) or last (11) row, then all spaces are printed to the console.
                If the loop is not on the first or last row, then 3 empty spaces are printed instead.
            */
            for (int j = 1; j <= 11; j++) {
                if (i == 1 || i == 11) {
                    System.out.print("[ ]");
                } else {
                    if (j == 1 || j == 11) {
                        System.out.print("[ ]");
                    } else {
                        System.out.print("   ");
                    }
                }
            }
            System.out.println();   // Prints the next row on a new line
        }
    }
}