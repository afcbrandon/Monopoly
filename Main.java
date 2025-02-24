public class Main {
  public static void main(String[] args) {
    new Main().go();
  }

  private void go() {
    GameBoard.Board();
    Player player1 = new Player("Bill", 1200);
    player1.rollAndMove();
    player1.rollAndMove();
  }
}
