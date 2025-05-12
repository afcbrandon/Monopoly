import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.List;

public class GameGUI extends JFrame {
    private ArrayList<Player> players;
    private JPanel playersPanel;
    private JPanel controlPanel;
    private JButton rollButton;
    private JButton endTurnButton;
    private JButton buildButton;
    private int currentPlayerIndex;
    private GameBoardSpaces boardSpaces;

    public GameGUI(ArrayList<Player> players) {
        this.players = players;
        this.boardSpaces = new GameBoardSpaces(players, this);

        // Goes through the list of players, and passes the SAME boardSpaces instance
        for (Player p : players) {
            p.setGBoardSpaces(this.boardSpaces);
        }

        // Randomly select the first player
        Random rand = new Random();
        this.currentPlayerIndex = rand.nextInt(players.size());

        // Set up the frame
        setTitle("Monopoly Game");
        if ( players.size() >= 6 ) {
            setSize(300, 800);
        }
        else if (players.size() >= 4 ) {
            setSize(300, 600);
        }
        else {
            setSize(300, 400);
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize buttons
        rollButton = new JButton("Roll Dice");
        rollButton.setFocusable(false);
        rollButton.addActionListener( _ -> {
            Player currentPlayer = players.get(currentPlayerIndex);
            do {
                currentPlayer.playerTurn();
                updatePlayerPanel(currentPlayer);
            } while (currentPlayer.getRolledDouble());

            rollButton.setEnabled(false); // Human's turn ends here
        });

        endTurnButton = new JButton("End Turn");
        endTurnButton.setFocusable(false);
        endTurnButton.addActionListener( _ -> {
            endTurn();
            rollButton.setEnabled(!(players.get(currentPlayerIndex) instanceof Bot)); // Only enable for humans
            handleBotTurns(); // If it's a bot, take turns automatically
        });

        buildButton = new JButton("Build Property");
        buildButton.setFocusable(false);    //  disabled keyboard focus
        updateBuildButtonState();
        buildButton.addActionListener( _ -> {
            Player currentPlayer = players.get(currentPlayerIndex);

            buildPropertyButton(currentPlayer);
        });

        // Debug button (unchanged)
        JButton debugButton = new JButton("Debug");
        debugButton.setFocusable(false);
        debugButton.addActionListener( _ -> {
            Player currentPlayer = players.get(currentPlayerIndex);
            showDebugPanel(currentPlayer);
        });

        // Create player panel
        playersPanel = new JPanel(new GridLayout(players.size(), 1));
        for (Player player : players) {
            JPanel playerPanel = createPlayerProfilePanel(player);
            playerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            playersPanel.add(playerPanel);
        }

        // Control panel
        this.controlPanel = new JPanel(new GridLayout(0, 1));
        controlPanel.add(rollButton);
        controlPanel.add(endTurnButton);
        controlPanel.add(buildButton);
        controlPanel.add(debugButton);

        add(playersPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);

        startGame(); // Begin game logic
    }

    private JPanel createPlayerProfilePanel(Player player) {
        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.setName(player.getPlayerName());

        panel.add(new JLabel("Name: " + player.getPlayerName()));
        panel.add(new JLabel("Token: " + player.getToken()));
        panel.add(new JLabel("Money: $" + player.getMoney()));

        int pos = player.getPosition();
        String description = boardSpaces.isProperty(pos)
                ? boardSpaces.getProperty(pos).getName()
                : boardSpaces.spaceType(pos);

        panel.add(new JLabel("Position: " + pos + " - " + description));
        return panel;
    }

    // Function that updates the player panel, and can be called from other Java classes
    public void updatePlayerPanel(Player player) {
        for (Component comp : playersPanel.getComponents()) {
            JPanel panel = (JPanel) comp;
            if (panel.getName().equals(player.getPlayerName())) {
                JLabel moneyLabel = (JLabel) panel.getComponent(2);
                JLabel posLabel = (JLabel) panel.getComponent(3);

                moneyLabel.setText("Money: $" + player.getMoney());
                int pos = player.getPosition();
                String desc = boardSpaces.isProperty(pos)
                        ? boardSpaces.getProperty(pos).getName()
                        : boardSpaces.spaceType(pos);
                posLabel.setText("Position: " + pos + " - " + desc);

                updateBuildButtonState();

                panel.setBackground(player.getIsEliminated() ? Color.RED : null);
                break;
            }
        }
    }

    /*  Function that enables buildButton if player owns a Full Color Set */
    private void updateBuildButtonState() {
        Player currPlayer = players.get(currentPlayerIndex);
        boolean canBuild = !currPlayer.getFullColorsets().isEmpty(); // returns false if hashmap that checks if player owns a full colorset is empty
        buildButton.setEnabled(canBuild);
        System.out.println("Checking buildButton enable state for player: " + currPlayer.getPlayerName());
        System.out.println("buildButton " + (canBuild ? "ENABLED" : "DISABLED") + " - Full color sets: " + canBuild);
    }

    // Function for buildButton
    private void buildPropertyButton(Player currentPlayer) {

        JFrame buildJFrame = new JFrame();
        buildJFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildJFrame.setResizable(false);

        JPanel buildJPanel = new JPanel(new GridLayout(currentPlayer.getFullColorsets().size() + 1, 1));
        HashMap<String,Integer> availColorSets = currentPlayer.getFullColorsets();

        // Height and Width
        final int WIDTH = 300;
        final int HEIGHT = 100 * (availColorSets.size() + 1);

        buildJPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        JButton[] buttons = new JButton[availColorSets.size()];
        String[] colorNames = new String[availColorSets.size()];

        int index = 0;
        for ( String cN : availColorSets.keySet() ) {
            colorNames[index] = cN;
            index++;
        }

        // Creates buttons with the name of their respective Color
        for ( int i = 0; i < availColorSets.size(); i++ ) {
            buttons[i] = new JButton(colorNames[i]);
            buildJPanel.add(buttons[i]);
        }

        // Iterate through JButton Array
        for ( Component component : buildJPanel.getComponents() ) {
            // Check if component is a JButton
            if ( component instanceof JButton ) {
                JButton button = (JButton) component;
                String buttonColorName = button.getText();

                // Add actionListener
                button.addActionListener( _ -> {
                    propertyNamesButtons(buildJFrame, currentPlayer, buttonColorName);
                });

            }
        }

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener( _ -> {
            buildJFrame.dispose();
        });


        buildJPanel.add(cancelButton);

        buildJFrame.add(buildJPanel);
        buildJFrame.pack(); // pack the frame

        buildJFrame.setLocationRelativeTo(null);
        buildJFrame.setAlwaysOnTop(true);
        buildJFrame.setVisible(true);
        
    }

    // Called when player chooses a colorset to build on
    private void propertyNamesButtons(JFrame parentFrame, Player currPlayer, String colorName) {

        // hide the initial frame
        parentFrame.setVisible(false);
        System.out.println("ColorSet Frame set to Hidden!");

        HashMap<String, List<String>> listOfColorProperties = new HashMap<String, List<String>>();
        listOfColorProperties = currPlayer.getListOfColorSetPropertyNames(currPlayer, colorName);
        int listSize = listOfColorProperties.get(colorName).size(); // variable that gets the size of the list for a given colorSet that contains the Properties
        JFrame propertyJFrame = new JFrame();
        JPanel propertyJPanel = new JPanel();
        propertyJPanel.setLayout(new BoxLayout(propertyJPanel, BoxLayout.Y_AXIS));

        
        // Set panel size
        final int WIDTH = 300;
        final int HEIGHT = 50;
        propertyJPanel.setPreferredSize( new Dimension(WIDTH, (listSize * HEIGHT) + (HEIGHT / 2) ) );

        // Code to create buttons for properties
        for ( int i = 0; i < listSize; i++) {
            propertyJPanel.add( new JButton(listOfColorProperties.get(colorName).get(i)) );  // creates a button and gives it the name of the Property at index i
        }

        // Iterate through buttons and add actionListener for them
        for ( Component component : propertyJPanel.getComponents() ) {
            // Check if component is a JButton
            if ( component instanceof JButton ) {
                JButton button = (JButton) component;
                button.setAlignmentX(CENTER_ALIGNMENT); // Align Buttons in the Center
                button.setMinimumSize(new Dimension(WIDTH, HEIGHT));
                button.setPreferredSize(new Dimension(WIDTH, HEIGHT));
                button.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE / 2));
                String bttnPropertyName = button.getText();

                // Add actionLister
                button.addActionListener( _ -> {
                    System.out.println("Selected button for " + bttnPropertyName);
                    houseOrHotelButtons(propertyJFrame, currPlayer, bttnPropertyName);
                });
            }
        }

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setAlignmentX(CENTER_ALIGNMENT);
        cancelButton.setMinimumSize(new Dimension(WIDTH, HEIGHT / 2));
        cancelButton.setPreferredSize(new Dimension(WIDTH, HEIGHT / 2));
        cancelButton.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        cancelButton.addActionListener( _ -> {
            parentFrame.setVisible(true);
            System.out.println("ColorSet Frame set to Visible!");
            propertyJFrame.dispose();
        });

        propertyJPanel.add(cancelButton);
        
        propertyJFrame.add(propertyJPanel);
        propertyJFrame.pack();  // pack the frame

        propertyJFrame.setLocationRelativeTo(null);
        propertyJFrame.setAlwaysOnTop(true);
        propertyJFrame.setVisible(true);

    }

    private void houseOrHotelButtons(JFrame parentFrame, Player currPlayer, String propertyName) {

        parentFrame.setVisible(false);
        
        JFrame houseHotelFrame = new JFrame();
        JPanel houseHotelPanel = new JPanel();
        houseHotelPanel.setLayout(new GridLayout(2, 1));
        houseHotelPanel.setPreferredSize(new Dimension(300, 100));

        JButton houseButton = new JButton("Build House");
        JButton hotelButton = new JButton("Build Hotel");
        JButton cancelButton = new JButton("Cancel");

        houseButton.addActionListener( _ -> {
            buildHouses(houseHotelFrame, propertyName);
        });

        hotelButton.addActionListener( _ -> {
            buildHotel(houseHotelFrame, propertyName);
        });

        // Code Logic that decides which button to add. If player can build house then show house button, if player can build hotel then show hotel button

        houseHotelPanel.add(houseButton);

        cancelButton.addActionListener( _ -> {
            houseHotelFrame.dispose();
            parentFrame.setVisible(true);
        });

        houseHotelPanel.add(cancelButton);
        
        houseHotelFrame.add(houseHotelPanel);
        houseHotelFrame.pack();

        houseHotelFrame.setLocationRelativeTo(parentFrame);
        houseHotelFrame.setAlwaysOnTop(true);
        houseHotelFrame.setVisible(true);
    }

    private void buildHouses(JFrame parentFrame, String propertyName) {
        Player currPlayer = players.get(currentPlayerIndex);

        parentFrame.setVisible(false);  // hide the parent frame

        final int WIDTH = 350;
        final int HEIGHT = 100;

        JFrame buildHouseFrame = new JFrame();
        JPanel buildHousePanel = new JPanel();
        buildHousePanel.setLayout(new BoxLayout(buildHousePanel, BoxLayout.Y_AXIS));

        // Add a vertical gap before question label
        buildHousePanel.add(Box.createVerticalStrut(10));

        JLabel questionLabel = new JLabel("Do you want to build a house on " + propertyName + "?");
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);    // center the question label
        buildHousePanel.add(questionLabel);

        // add a vertical gap between questionLabel and the two buttons
        buildHousePanel.add(Box.createVerticalStrut(10));

        JButton yesButton = new JButton("Yes");
        JButton noButton = new JButton("No");

        // Actions Listener for yesButton
        yesButton.addActionListener( _ -> {

            buildHouseFrame.dispose();
        
            // create a new panel with message telling player they built the house
            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));

            // add a vertical gap before the label
            messagePanel.add(Box.createVerticalStrut(10));

            JLabel messageLabel = new JLabel(currPlayer.getPlayerName() + " built a house on " + propertyName);
            messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // center the message label
            messagePanel.add(messageLabel);
            messagePanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));

            // add a vertical gap between the label and the okButton
            messagePanel.add(Box.createVerticalStrut(10));

            // create an ok button that closes the frame
            JButton okButton = new JButton("Ok");
            okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            okButton.addActionListener( _ -> {
                parentFrame.dispose();
            });
            messagePanel.add(okButton); // add ok button to messagePanel

            // Replace the content pane of the parent frame
            parentFrame.getContentPane().removeAll();
            parentFrame.getContentPane().add(messagePanel, BorderLayout.CENTER);
            parentFrame.getContentPane().revalidate();  // refresh the panel
            parentFrame.getContentPane().repaint();
            parentFrame.pack();

            parentFrame.pack();
            parentFrame.setVisible(true);

        });

        // Action Listener for no Button
        noButton.addActionListener( _ -> {

            buildHouseFrame.dispose();
            parentFrame.setVisible(true);
        });

        buildHousePanel.add(yesButton);
        buildHousePanel.add(noButton);
        buildHousePanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        buildHouseFrame.getContentPane().add(buildHousePanel);  // Add the panel to the frame's pane
        buildHouseFrame.pack();
        buildHouseFrame.setLocationRelativeTo(parentFrame);
        buildHouseFrame.setVisible(true);

    }

    private void buildHotel(JFrame parentFrame, String propertyName) {

    }


    // Function that ends the player's turn
    private void endTurn() {
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (players.get(currentPlayerIndex).getIsEliminated());

        checkWinner();

        Player currentPlayer = players.get(currentPlayerIndex);
        JOptionPane.showMessageDialog(this, "It's now " + currentPlayer.getPlayerName() + "'s turn!");
        updateBuildButtonState();
    }

    private void handleBotTurns() {
        while (players.get(currentPlayerIndex) instanceof Bot) {
            Bot bot = (Bot) players.get(currentPlayerIndex);
            do {
                bot.playerTurn();
                updatePlayerPanel(bot);
                // TODO: Add code for bot to update control panel {might be needed for bought to build houses}
            } while (bot.getRolledDouble());

            endTurn();
        }
        rollButton.setEnabled(true); // Enable for the next human
    }

    private void showDebugPanel(Player player) {
        JPanel panel = new JPanel(new GridLayout(4, 2));
        JTextField moneyField = new JTextField();
        JTextField posField = new JTextField();

        panel.add(new JLabel("Add/Subtract Money:"));
        panel.add(moneyField);
        panel.add(new JLabel("Move to Position (1-40):"));
        panel.add(posField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Debug Tool", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int moneyChange = Integer.parseInt(moneyField.getText());
                int newPos = Integer.parseInt(posField.getText());

                player.updateMoney(moneyChange);
                player.setPosition(newPos);
                updatePlayerPanel(player);
                boardSpaces.purchaseProperty(player, player.getPosition(), 1);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        }
    }

    private void checkWinner() {
        int activeCount = 0;
        Player winner = null;
        for (Player p : players) {
            if (!p.getIsEliminated()) {
                activeCount++;
                winner = p;
            }
        }
        if (activeCount == 1 && winner != null) {
            JOptionPane.showMessageDialog(this, winner.getPlayerName() + " wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }

    private void startGame() {
        handleBotTurns(); // Handle bot-only first turn
        rollButton.setEnabled(!(players.get(currentPlayerIndex) instanceof Bot));
    }
}
