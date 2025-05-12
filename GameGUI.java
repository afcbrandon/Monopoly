import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.List;

public class GameGUI extends JFrame {
    private ArrayList<Player> players;
    private JFrame playerGUIFrame;
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

        this.playerGUIFrame = new JFrame("Monopoly Game");
        this.playerGUIFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Goes through the list of players, and passes the SAME boardSpaces instance
        for (Player p : players) {
            p.setGBoardSpaces(this.boardSpaces);
        }

        // Randomly select the first player
        Random rand = new Random();
        this.currentPlayerIndex = rand.nextInt(players.size());

        /* Code to Retrieve Monopoly Board */
        JFrame boardFrame = new JFrame("Monopoly");
        final int gameWIDTH = 800;
        final int gameHEIGHT = 800;
        boardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        boardFrame.setContentPane(new GameBoard("Monopoly Board Numbered.jpg"));
        boardFrame.setSize(gameWIDTH, gameHEIGHT);
        boardFrame.setLocationRelativeTo(null);
        boardFrame.setVisible(true);
        
        // Set up the panel size
        if ( players.size() >= 6 ) {
            this.playerGUIFrame.setPreferredSize(new Dimension(300, 800));
        }
        else if (players.size() >= 4 ) {
            this.playerGUIFrame.setPreferredSize(new Dimension(300, 600));
        }
        else {
            this.playerGUIFrame.setPreferredSize(new Dimension(300, 400));
        }
        this.playerGUIFrame.setLayout(new BorderLayout());

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

        playerGUIFrame.add(playersPanel, BorderLayout.CENTER);
        playerGUIFrame.add(controlPanel, BorderLayout.SOUTH);

        // Get location and dimensions of boardFrame
        Point boardFrameLocation = boardFrame.getLocation();
        int boardFrameWIDTH = boardFrame.getWidth();
        int boardFrameX = boardFrameLocation.x;
        int boardFrameY = boardFrameLocation.y;

        // Calculate position for playerGUIFrame (to the right of boardFrame)
        int guiFrameX = boardFrameX + boardFrameWIDTH; 
        int guiFrameY = boardFrameY;    // align top of frames

        playerGUIFrame.pack();  // Pack the playerGUI Frame

        playerGUIFrame.setLocation(guiFrameX, guiFrameY);
        playerGUIFrame.setVisible(true);

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

        List<String> propertyNamesInSet = currPlayer.getListOfColorSetPropertyNames(currPlayer, colorName).get(colorName);

        if (propertyNamesInSet == null || propertyNamesInSet.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "Error: No properties found for this color set.", "Error", JOptionPane.ERROR_MESSAGE);
            parentFrame.setVisible(true); // Show the parent frame again
            return;
        }

        // Get the Property objects and find the minimum number of houses in this set
        List<Property> propertiesInThisColorSet = new ArrayList<>();
        int minHousesInSet = 5; // Max houses is 4, hotel is the 5th "house". 

        for ( String propName : propertyNamesInSet ) {
            Property prop = boardSpaces.getPropertyByName(propName);
            if ( prop.getOwner() == currPlayer ) {  // Ensures that the current player owns the property
                propertiesInThisColorSet.add(prop);
                if ( prop.getNumHotels() == 0 && prop.getNumHouses() < minHousesInSet ) {   // only consider houses if no hotel
                    minHousesInSet = prop.getNumHouses();
                } else if ( prop.getNumHotels() == 1 ) { // If there's a hotel, then the property basically has 5 houses
                    minHousesInSet = Math.min(minHousesInSet, 5);   // Hotel means the property is "full" of houses
                }
            }
        }

        // If all properties in the set have a hotel, minHousesInSet might still be 5.
        // Or if all have 4 houses and are eligible for a hotel, minHousesInSet would be 4.

        // Create an object of Jframe and of JPanel
        JFrame propertyJFrame = new JFrame();
        JPanel propertyJPanel = new JPanel();
        propertyJPanel.setLayout(new BoxLayout(propertyJPanel, BoxLayout.Y_AXIS));
        
        // Set panel size
        final int WIDTH = 300;
        final int HEIGHT = 50;
        propertyJPanel.setPreferredSize( new Dimension(WIDTH, (propertyNamesInSet.size() * HEIGHT) + (HEIGHT / 2) ) );

        // Code to create buttons for properties
        for ( String propNameFromList : propertyNamesInSet ) {
            propertyJPanel.add( new JButton(propNameFromList) );
        }

        // Iterate through buttons and add actionListener for them
        for ( Component component : propertyJPanel.getComponents() ) {
            if ( component instanceof JButton ) {
                JButton button = (JButton) component;
                button.setAlignmentX(CENTER_ALIGNMENT);
                button.setMinimumSize(new Dimension(WIDTH, HEIGHT));
                button.setPreferredSize(new Dimension(WIDTH, HEIGHT));
                button.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE / 2));
                String bttnPropertyName = button.getText();

                Property property = boardSpaces.getPropertyByName(bttnPropertyName);
                boolean enableButton = false;

                if (property != null && property.getOwner() == currPlayer) {
                    if (property.getNumHotels() == 1) {
                        // Already has a hotel, cannot build more houses.
                        enableButton = false;
                        System.out.println("Button for " + bttnPropertyName + " disabled: has a hotel.");
                    } else if (property.getNumHouses() == 4) {
                        // Has 4 houses, can only build a hotel next (handled by houseOrHotelButtons).
                        // For the purpose of "building another house", this property is not eligible.
                        // However, it *should* be selectable if the goal is to build a hotel.
                        // The current context is selecting a property to build *something*.
                        // The houseOrHotelButtons will differentiate. So, if it has 4 houses, it's a candidate for a hotel.
                        // Let's assume this screen leads to choosing house OR hotel.
                        // If it has 4 houses, it means it has the min # of houses (if others also have 4) or more.
                        // The condition for enabling should be that its current house count allows it to be the "next" to build on.
                        if (property.getNumHouses() == minHousesInSet) {
                             enableButton = true; // Can be selected to potentially build a hotel
                        } else {
                             enableButton = false; // If it has 4 houses but others have less, can't select it.
                             System.out.println("Button for " + bttnPropertyName + " disabled: has 4 houses, but others in set have fewer (" + minHousesInSet + ").");
                        }
                    } else if (property.getNumHouses() > minHousesInSet) {
                        // This property has more houses than the minimum in the set.
                        // Player must build on properties with fewer houses first.
                        enableButton = false;
                        System.out.println("Button for " + bttnPropertyName + " disabled: has " + property.getNumHouses() + " houses, set minimum is " + minHousesInSet + ".");
                    } else {
                        // property.getNumHouses() == minHousesInSet and < 4
                        // This property is eligible for the next house.
                        enableButton = true;
                    }
                } else {
                    // Property not found or not owned by current player (should not happen if getListOfColorSetPropertyNames is correct)
                    enableButton = false;
                    System.out.println("Button for " + bttnPropertyName + " disabled: property not found or not owned by player.");
                }

                button.setEnabled(enableButton);

                if (enableButton) {
                    button.addActionListener( _ -> {
                        System.out.println("Selected button for " + bttnPropertyName);
                        houseOrHotelButtons(propertyJFrame, currPlayer, bttnPropertyName);
                    });
                }
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

        // Get the property object from its name
        Property property = boardSpaces.getPropertyByName(propertyName);

        // If the selected property already has 4 houses, the max, then the build hotel button is displayed, otherwise the house button is displayed
        if ( property.getNumHouses() == 4 ) {
            houseHotelPanel.add(hotelButton);
            System.out.println("Max number of houses built on " + propertyName + "! buildHotel button enabled!");
        } else {
            houseHotelPanel.add(houseButton);

            // If property has no hotel, then player can build a hotel, otherwise disable the button
            if ( property.getNumHotels() == 0 ) {
                houseButton.setEnabled(true);
            } else {
                houseButton.setEnabled(false);
                System.out.println(propertyName + " has max number of hotels! buildHotel button is disabled");
            }
        }

        cancelButton.addActionListener( _ -> {
            houseHotelFrame.dispose();
            parentFrame.setVisible(true);
        });

        houseHotelPanel.add(cancelButton);
        
        houseHotelFrame.add(houseHotelPanel);
        houseHotelFrame.pack();

        houseHotelFrame.setLocationRelativeTo(null);
        houseHotelFrame.setAlwaysOnTop(true);
        houseHotelFrame.setVisible(true);
    }

    private void buildHouses(JFrame parentFrame, String propertyName) {

        Player currPlayer = players.get(currentPlayerIndex);
        Property property = boardSpaces.getPropertyByName(propertyName);

        parentFrame.setVisible(false);  // hide the parent frame

        final int WIDTH = 400;
        final int HEIGHT = 100;

        JFrame buildHouseFrame = new JFrame();
        JPanel buildHousePanel = new JPanel();
        buildHousePanel.setLayout(new BoxLayout(buildHousePanel, BoxLayout.Y_AXIS));
        buildHousePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // create padding

        // Add a vertical gap before question label
        buildHousePanel.add(Box.createVerticalStrut(10));

        JLabel questionLabel = new JLabel("Do you want to build a house on " + propertyName + " for $" + property.getHouseCost() + "?");
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);    // center the question label
        buildHousePanel.add(questionLabel);

        // add a vertical gap between questionLabel and the two buttons
        buildHousePanel.add(Box.createVerticalStrut(10));

        JButton yesButton = new JButton("Yes");
        JButton cancelButton = new JButton("Cancel");

        // Center buttons and set a preferred size
        Dimension buttonSize = new Dimension(WIDTH - 40, 30);   // Adjusted button width
        yesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        yesButton.setPreferredSize(buttonSize);
        yesButton.setMaximumSize(buttonSize);
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.setPreferredSize(buttonSize);
        cancelButton.setMaximumSize(buttonSize);

        if ( currPlayer.getMoney() < property.getHouseCost() ) {
            yesButton.setText("Not enough money to build a house $(" + property.getHouseCost() + ")");
            yesButton.setEnabled(false);
        } else {
            // Actions Listener for yesButton
            yesButton.addActionListener( _ -> {
                
                buildHouseFrame.dispose();

                // creates a new panel with message telling player they built the house
                JPanel messagePanel = new JPanel();
                messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
            
                // Run code that builds a property on the player space. Run after messagePanel is created so that a label can be added
                if ( property.getOwner() == currPlayer ) {
                    currPlayer.updateMoney(-property.getHouseCost());
                    property.addHouse();
                    updatePlayerPanel(currPlayer);  // update player panel to reflect player's new money balance
                    System.out.println(property.getName() + " Houses: " + property.getNumHouses());
                } else {
                    System.out.println("ERROR! " + currPlayer.getPlayerName() + " does not own this property!");
                }

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
        }

        // Action Listener for no Button
        cancelButton.addActionListener( _ -> {

            buildHouseFrame.dispose();
            parentFrame.setVisible(true);
        });

        buildHousePanel.add(yesButton);
        buildHousePanel.add(cancelButton);

        buildHousePanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        buildHouseFrame.getContentPane().add(buildHousePanel);  // Add the panel to the frame's pane
        buildHouseFrame.pack();
        buildHouseFrame.setLocationRelativeTo(null);
        buildHouseFrame.setVisible(true);

    }

    private void buildHotel(JFrame parentFrame, String propertyName) {

        Player currPlayer = players.get(currentPlayerIndex);
        Property property = boardSpaces.getPropertyByName(propertyName);

        parentFrame.setVisible(false);  // hide the parent frame

        final int WIDTH = 400;
        final int HEIGHT = 100;

        JFrame buildHotelFrame = new JFrame();
        JPanel buildHotelPanel = new JPanel();
        buildHotelPanel.setLayout(new BoxLayout(buildHotelPanel, BoxLayout.Y_AXIS));
        buildHotelPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // create padding

        // Add a vertical gap before question label
        buildHotelPanel.add(Box.createVerticalStrut(10));

        JLabel questionLabel = new JLabel("Do you want to build a hotel on " + propertyName + " for $" + property.getHotelCost() + "?");
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);    // center the question label
        buildHotelPanel.add(questionLabel);

        // add a vertical gap between questionLabel and the two buttons
        buildHotelPanel.add(Box.createVerticalStrut(10));

        JButton yesButton = new JButton("Yes");
        JButton cancelButton = new JButton("Cancel");

        // Center buttons and set a preferred size
        Dimension buttonSize = new Dimension(WIDTH - 40, 30);   // Adjusted button width
        yesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        yesButton.setPreferredSize(buttonSize);
        yesButton.setMaximumSize(buttonSize);
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.setPreferredSize(buttonSize);
        cancelButton.setMaximumSize(buttonSize);

        if ( currPlayer.getMoney() < property.getHotelCost() ) {
            yesButton.setText("Not enough money to build a hotel $(" + property.getHotelCost() + ")");
            yesButton.setEnabled(false);
        } else {
            // Actions Listener for yesButton
            yesButton.addActionListener( _ -> {
                
                buildHotelFrame.dispose();

                // creates a new panel with message telling player they built the house
                JPanel messagePanel = new JPanel();
                messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
            
                // Run code that builds a property on the player space. Run after messagePanel is created so that a label can be added
                if ( property.getOwner() == currPlayer ) {
                    currPlayer.updateMoney(-property.getHotelCost());
                    property.addHotel();
                    updatePlayerPanel(currPlayer);  // update panel to reflect player's new money balance
                    System.out.println(property.getName() + " Hotels: " + property.getNumHotels());
                } else {
                    System.out.println("ERROR! " + currPlayer.getPlayerName() + " does not own this property!");
                }

                // add a vertical gap before the label
                messagePanel.add(Box.createVerticalStrut(10));

                JLabel messageLabel = new JLabel(currPlayer.getPlayerName() + " built a hotel on " + propertyName);
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
        }

        // Action Listener for no Button
        cancelButton.addActionListener( _ -> {

            buildHotelFrame.dispose();
            parentFrame.setVisible(true);
        });

        buildHotelPanel.add(yesButton);
        buildHotelPanel.add(cancelButton);

        buildHotelPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        buildHotelFrame.getContentPane().add(buildHotelPanel);  // Add the panel to the frame's pane
        buildHotelFrame.pack();
        buildHotelFrame.setLocationRelativeTo(null);
        buildHotelFrame.setVisible(true);

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
        moneyField.setText("0");    // set the moneyfield text to 0, for easier debugging
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
