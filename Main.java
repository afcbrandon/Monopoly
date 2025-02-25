
public class Main {
  public static void main(String[] args) {
    new Main().go();

  }

  private void go() {
    GameBoard gameBoard = new GameBoard();
    gameBoard.Board();
    Player[] players = new Player[] {new Player("Player 1"), new Player("Player 2")};
    new GameGUI(players);

  }
}