import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/*
  Class that handles all UserInterface prompts within the console
 */
public class UserInterface {
  private Scanner uiScanner = new Scanner(System.in);

  /*  Function that begins the game process
      The function first calls the getPlayers function 
        that prompts the user for the number of players (between 2-8 players).
  */
  public void start() {

    int numOfPlayers = getNumberOfPlayers();
    ArrayList<Player> pList = createPlayers(numOfPlayers);

    GameBoard monopolyBoard = new GameBoard("Monopoly Board Numbered.jpg");
    SwingUtilities.invokeLater(() -> {

      JFrame frame = new JFrame("Monopoly");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
      frame.setContentPane(monopolyBoard);

      frame.setSize(800, 800);
      frame.setVisible(true);

    });

    new GameGUI(pList);
  }

  /* 
    This function prompts the user for the number of players in the game, 
    and returns the total number (between 2-8 players)
  */
  private int getNumberOfPlayers() {

    int numOfPlayers = 0;

    System.out.print("Enter the number of players (2-8): ");
    do {
      String totalPlayers = uiScanner.nextLine();
      numOfPlayers = Integer.valueOf(totalPlayers);

      if (numOfPlayers < 2 || numOfPlayers > 8) {
        System.out.print("ERROR! Please enter a valid amount of players (2-8): ");
      }
    } 
    while (numOfPlayers < 2 || numOfPlayers > 8);

    return numOfPlayers;
  }

  /* 
    Function that creates an ArrayList of players determined by the number of players
      Also calls chooseToken function to let each player choose their token
  */
  private ArrayList<Player> createPlayers(int numOfPlayers) {
    
    ArrayList<Player> playerList = new ArrayList<>();
    PlayerToken tokenList = new PlayerToken();
    GameBoardSpaces gameBoardSpaces = new GameBoardSpaces(playerList);

    for (int i = 0; i < numOfPlayers; i++) {
      System.out.print("Enter name for Player " + (i + 1) + ": ");
      String playerName = uiScanner.nextLine().trim();

      while(playerName.isEmpty()) {
        System.out.print("ERROR! Please enter a valid name for Player " + (i + 1) + ": ");
        playerName = uiScanner.nextLine().trim();
      }
      Player player = new Player(playerName, gameBoardSpaces);
      playerList.add(player);

      if (i == 7) { // Max number of players(8), then the last player will get the last token
        Character lastToken = tokenList.getLastToken();
        System.out.println("The token for " + playerList.get(i).getName() + " is " + lastToken);
        playerList.get(i).setToken(lastToken);
      }
      else {
        chooseToken(tokenList, playerList, playerList.get(i));
      }
    }

    return playerList;
  }

  // Function that prompts user to select their token
  public void chooseToken(PlayerToken tokenList, ArrayList<Player> pList, Player player) {

    boolean tokenChosen = false;
    char userChar = ' ';

    // Prints the avaiable tokens
    System.out.println("Available Tokens: ");
    for (Character token : tokenList.getTokenList()) {
      System.out.println(token);
    }

    /*
        Prompts user to select the token, and loops until valid input
     */
    System.out.print("Select the token for " + player.getName() + ": ");
    do {

      String input = uiScanner.nextLine().trim();

      if (input.length() == 1) {  // Ensure only one character is entered
        userChar = input.charAt(0);
        if (tokenList.getTokenList().contains(userChar)) {
          tokenChosen = true;
        }
      }

      if (!tokenChosen) {
        System.out.print("ERROR! Please enter a valid token: ");
      }

    }
    while (!tokenChosen);

    tokenList.chooseToken(userChar);  // Calls the chooseToken function from PlayerToken class
    player.setToken(userChar);  // Calls the setToken from Player class

  }
}
