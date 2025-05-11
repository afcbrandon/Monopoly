import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/*
  Class that handles all UserInterface prompts within the console
 */
public class UserInterface {
  private Scanner uiScanner = new Scanner(System.in);

  private boolean playWithBots;  // This will store whether we're playing with bots or not

  // Constructor to accept the boolean value
  public UserInterface(boolean playWithBots) {
    this.playWithBots = playWithBots;
  }
  /*  Function that begins the game process
      The function first calls the getPlayers function
        that prompts the user for the number of players (between 2-8 players).
  */
  public void start() {
    int totalPlayers = getNumberOfPlayers();
    int humanPlayers = playWithBots ? getNumberOfHumanPlayers(totalPlayers) : totalPlayers;

    ArrayList<Player> pList = createPlayers(totalPlayers, humanPlayers, null);

    SwingUtilities.invokeLater(() -> {
      JFrame frame = new JFrame("Monopoly");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setContentPane(new GameBoard("Monopoly Board Numbered.jpg"));  // Optional
      frame.setSize(800, 800);
      frame.setVisible(true);

      GameGUI gameGUI = new GameGUI(pList);
      GameBoardSpaces gameBoardSpaces = new GameBoardSpaces(pList, gameGUI);

      for (Player player : pList) {
        if (player instanceof Bot) {
          ((Bot) player).setGameBoardSpaces(gameBoardSpaces);
        }
      }


    });
  }
  /*
    This function prompts the user for the number of players in the game,
    and returns the total number (between 2-8 players)
  */
  private int getNumberOfPlayers() {
    int totalPlayers = 0;

    System.out.print("Enter the total number of players (2-8): ");
    do {
      String input = uiScanner.nextLine();
      totalPlayers = Integer.parseInt(input);

      if (totalPlayers < 2 || totalPlayers > 8) {
        System.out.print("ERROR! Please enter a valid number (2-8): ");
      }
    } while (totalPlayers < 2 || totalPlayers > 8);

    return totalPlayers;
  }

  private int getNumberOfHumanPlayers(int totalPlayers) {
    int humans = 0;

    System.out.print("Enter number of human players (at least 1): ");
    do {
      String input = uiScanner.nextLine();
      humans = Integer.parseInt(input);

      if (humans < 1 || humans > totalPlayers) {
        System.out.print("ERROR! Must be between 1 and " + totalPlayers + ": ");
      }
    } while (humans < 1 || humans > totalPlayers);

    return humans;
  }
  /*
    Function that creates an ArrayList of players determined by the number of players
      Also calls chooseToken function to let each player choose their token
  */
  private ArrayList<Player> createPlayers(int totalPlayers, int humanPlayers, GameBoardSpaces gameBoardSpaces) {
    ArrayList<Player> playerList = new ArrayList<>();
    PlayerToken tokenList = new PlayerToken();

    for (int i = 0; i < totalPlayers; i++) {
      Player player;
      boolean isBot = playWithBots && i >= humanPlayers;

      if (isBot) {
        String botName = "Bot " + (i - humanPlayers + 1);  // Bot 1, 2, ...
        System.out.println(botName + " has joined the game!");
        player = new Bot(botName, gameBoardSpaces);
        //player = null;  // TODO: TEMPORARY CODE. TO BE REMOVED LATER

        char botToken = tokenList.getTokenList().get(0);
        tokenList.chooseToken(botToken);
        player.setToken(botToken);
        System.out.println(botName + " chose token: " + botToken);
      } else {
        System.out.print("Enter name for Player " + (i + 1) + ": ");
        String playerName = uiScanner.nextLine().trim();
        while (playerName.isEmpty()) {
          System.out.print("ERROR! Please enter a valid name for Player " + (i + 1) + ": ");
          playerName = uiScanner.nextLine().trim();
        }

        player = new Player(playerName, false);

        if (i == 7) {
          Character lastToken = tokenList.getLastToken();
          System.out.println("The token for " + player.getPlayerName() + " is " + lastToken);
          player.setToken(lastToken);
        } else {
          chooseToken(tokenList, playerList, player);
        }
      }

      playerList.add(player);
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
    System.out.print("Select the token for " + player.getPlayerName() + ": ");
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