import javax.swing.*;

public class Main {
  public static void main(String[] args) {
    new Main().go();

  }

  private void go() {
    GameBoard gameBoard = new GameBoard();
    gameBoard.Board();
    // Get number of players through input dialog
    int numberOfPlayers = 0;

    while (numberOfPlayers < 2 || numberOfPlayers > 8) {
      String input = JOptionPane.showInputDialog("Enter number of players (2-8):");
      try {
        numberOfPlayers = Integer.parseInt(input);
        if (numberOfPlayers < 2 || numberOfPlayers > 8) {
          JOptionPane.showMessageDialog(null, "Please enter a number between 2 and 8");
        }
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number.");
      }
    }

    // Create players array based on user input
    Player[] players = new Player[numberOfPlayers];
    for (int i = 0; i < numberOfPlayers; i++) {
      players[i] = new Player("Player " + (i + 1));
    }
    new GameGUI(players);
  }
}