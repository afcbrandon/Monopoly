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
        
        JFrame houseHotelFrame = new JFrame("Manage " + propertyName);
        JPanel houseHotelPanel = new JPanel();

        houseHotelPanel.setLayout(new GridLayout(0, 1, 0, 10));
        houseHotelPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding
        //houseHotelPanel.setPreferredSize(new Dimension(300, 150));

        JButton houseButton = new JButton("Build House ($" + boardSpaces.getPropertyByName(propertyName).getHouseCost() + ")");
        JButton hotelButton = new JButton("Build Hotel ($" + boardSpaces.getPropertyByName(propertyName).getHotelCost() + ")");
        JButton sellPropertyButton = new JButton("Sell Property");
        JButton cancelButton = new JButton("Cancel");

        // Get the property object from its name
        Property property = boardSpaces.getPropertyByName(propertyName);
        Bank bank = boardSpaces.getBank();

        boolean showHouseButton = false;
        boolean showHotelButton = false;

        if ( property.getNumHotels() == 0 ) {   // If property does not have hotel
            if ( property.getNumHouses() < 4 ) {
                showHouseButton = true;
                if ( !bank.canDispenseHouse() ) {   // Bank house no more houses
                    houseButton.setText("Build House (None in Bank)");
                    houseButton.setEnabled(false);
                }
                if ( currPlayer.getMoney() < property.getHouseCost() ) {
                    houseButton.setEnabled(false);
                    houseButton.setToolTipText("Not enough money"); // message when player hovers mouse over button
                }
            } else if ( property.getNumHouses() == 4 ) {
                showHotelButton = true;
                if ( !bank.canDispenseHotel() ) {   // Bank has no more hotels
                    hotelButton.setText("Build Hotel (None in Bank)");
                    hotelButton.setEnabled(false);
                }
                if ( currPlayer.getMoney() < property.getHotelCost() ) {
                    hotelButton.setEnabled(false);
                    hotelButton.setToolTipText("Not enough money"); // message when player hovers mouse over button
                }
            }
        }

        if ( showHouseButton ) {
            houseHotelPanel.add(houseButton);
            houseButton.addActionListener( _ -> {
                buildHouses(houseHotelFrame, propertyName);  // houseHotelFrame becomes the parentFrame for buildHotel dialog
            });
        }

        if ( showHotelButton ) {
            houseHotelPanel.add(hotelButton);
            hotelButton.addActionListener( _ -> {
                buildHotel(houseHotelFrame, propertyName);  // houseHotelFrame becomes the parentFrame for buildHotel dialog
            });
        }

        // Add Sell Property Button
        if ( property.getOwner() == currPlayer ) {
            houseHotelPanel.add(sellPropertyButton);
            sellPropertyButton.addActionListener( _ -> {
                showSellOptionsDialog(houseHotelFrame, currPlayer, propertyName);
            });
        }

        // Message if no actions (e.g., already has hotel, or not owned)
        if (!showHouseButton && !showHotelButton && (property.getOwner() != currPlayer || property.getNumHotels() > 0 && property.getOwner() == currPlayer)) {
            String message = "No building actions available for " + propertyName + ".";
            if (property.getOwner() != currPlayer) {
                message = "You do not own " + propertyName + ".";
            } else if (property.getNumHotels() > 0) {
                message = propertyName + " already has a hotel.";
            }
            JLabel noBuildLabel = new JLabel(message, SwingConstants.CENTER);
            houseHotelPanel.add(noBuildLabel);
        }

        // Cancel Button Action Listener
        cancelButton.addActionListener( _ -> {
            houseHotelFrame.dispose();
            parentFrame.setVisible(true);   // Show the property list frame again
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
        Property property = boardSpaces.getPropertyByName(propertyName);
        Bank bank = boardSpaces.getBank();

        parentFrame.setVisible(false);  // hide the parent frame

        final int WIDTH = 450;
        final int HEIGHT = 120;

        JFrame buildHouseFrame = new JFrame("Build House on " + propertyName);
        JPanel buildHousePanel = new JPanel();
        buildHousePanel.setLayout(new BoxLayout(buildHousePanel, BoxLayout.Y_AXIS));
        buildHousePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));     // create padding
        buildHousePanel.add(Box.createVerticalStrut(10));   // Add a vertical gap before question label

        JLabel questionLabel = new JLabel("Build a house on " + propertyName + " for $" + property.getHouseCost() + "?");
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);    // center the question label
        buildHousePanel.add(questionLabel);
        buildHousePanel.add(Box.createVerticalStrut(10));   // add a vertical gap between questionLabel and the two buttons

        JButton yesButton = new JButton("Yes");
        JButton noButton = new JButton("No");

        // Center buttons and set a preferred size
        Dimension buttonSize = new Dimension(WIDTH - 60, 30);   // Adjusted button width
        yesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        yesButton.setPreferredSize(buttonSize);
        yesButton.setMaximumSize(buttonSize);
        noButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        noButton.setPreferredSize(buttonSize);
        noButton.setMaximumSize(buttonSize);

        if ( currPlayer.getMoney() < property.getHouseCost() ) {
            yesButton.setText("Not enough money ($" + property.getHouseCost() + ")");
            yesButton.setEnabled(false);
        } else if ( property.getNumHotels() > 0 ) {
            yesButton.setText("Property has a hotel. Cannot add houses.");
            yesButton.setEnabled(false);
        } else if ( property.getNumHouses() >= 4 ) {
            yesButton.setText("Max houses built. Build hotel Instead");
            yesButton.setEnabled(false);
        } else if ( !bank.canDispenseHouse() ) {
            yesButton.setText("No houses available from the bank!");
            yesButton.setEnabled(false);
        }
        else {
            // Actions Listener for yesButton
            yesButton.addActionListener( _ -> {
                
                buildHouseFrame.dispose();

                currPlayer.updateMoney(-property.getHouseCost());
                property.setNumHouses(property.getNumHouses() + 1); // Update the house count of property
                bank.dispenseHouse();   // get a house from the bank

                updatePlayerPanel(currPlayer);
                updateBuildButtonState();

                System.out.println(currPlayer.getPlayerName() + " built a house on " + property.getName() +
                                    ". Houses: " + property.getNumHouses() +
                                    ". Bank Houses: " + bank.getAvailableHouses());

                JOptionPane.showMessageDialog(null, 
                        currPlayer.getPlayerName() + " built a house on " + propertyName + "!",
                        "House Built", JOptionPane.INFORMATION_MESSAGE);
                parentFrame.dispose();
            });
        }

        // Action Listener for no Button
        noButton.addActionListener( _ -> {
            buildHouseFrame.dispose();
            parentFrame.setVisible(true);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        buildHousePanel.add(buttonPanel);
        buildHousePanel.add(Box.createVerticalStrut(10));

        buildHouseFrame.getContentPane().add(buildHousePanel);  // Add the panel to the frame's pane
        buildHouseFrame.pack();
        buildHouseFrame.setLocationRelativeTo(parentFrame);
        buildHouseFrame.setAlwaysOnTop(true);
        buildHouseFrame.setVisible(true);
    }

    private void buildHotel(JFrame parentFrame, String propertyName) {

        Player currPlayer = players.get(currentPlayerIndex);
        Property property = boardSpaces.getPropertyByName(propertyName);
        Bank bank = boardSpaces.getBank();

        parentFrame.setVisible(false);  // hide the parent frame

        final int WIDTH = 450;
        final int HEIGHT = 120;

        JFrame buildHotelFrame = new JFrame("Build Hotel on " + propertyName);
        JPanel buildHotelPanel = new JPanel();
        buildHotelPanel.setLayout(new BoxLayout(buildHotelPanel, BoxLayout.Y_AXIS));
        buildHotelPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // create padding
        buildHotelPanel.add(Box.createVerticalStrut(10)); // Add a vertical gap before question label

        JLabel questionLabel = new JLabel("Build hotel on " + propertyName + " for $" + property.getHotelCost() + "? (Returns 4 houses to the bank)");
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);    // center the question label
        buildHotelPanel.add(questionLabel);
        buildHotelPanel.add(Box.createVerticalStrut(10));  // add a vertical gap between questionLabel and the two buttons

        JButton yesButton = new JButton("Yes");
        JButton noButton = new JButton("No");

        // Center buttons and set a preferred size
        Dimension buttonSize = new Dimension(WIDTH - 60, 30);   // Adjusted button width
        yesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        yesButton.setPreferredSize(buttonSize);
        yesButton.setMaximumSize(buttonSize);
        noButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        noButton.setPreferredSize(buttonSize);
        noButton.setMaximumSize(buttonSize);

        if ( property.getNumHouses() != 4 ) {
            yesButton.setText("Must have 4 houses to build a hotel.");
            yesButton.setEnabled(false);
        } else if ( property.getNumHotels() > 0 ) {
            yesButton.setText("Hotel already built on this property.");
            yesButton.setEnabled(false);
        } else if ( currPlayer.getMoney() < property.getHotelCost() ) {
            yesButton.setText("Not enough money ($" + property.getHotelCost() + ")");
            yesButton.setEnabled(false);
        } else if ( !bank.canDispenseHotel() ) {
            yesButton.setText("No hotels available from the bank!");
            yesButton.setEnabled(false);
        } 
        else {
            // Actions Listener for yesButton
            yesButton.addActionListener( _ -> {
                
                buildHotelFrame.dispose();

                currPlayer.updateMoney(-property.getHotelCost());
                
                property.setNumHotels(1);   // this sets numHouses to 0 and updates rent
                bank.returnHouses(4);   // return 4 houses back to the bank
                bank.dispenseHotel();   // get a hotel from the bank

                updatePlayerPanel(currPlayer);
                updateBuildButtonState();

                System.out.println(currPlayer.getPlayerName() + " built a hotel on " + property.getName() +
                                    ". Bank Houses: " + bank.getAvailableHouses() +
                                    ", Bank Hotels: " + bank.getAvailableHotels());

                JOptionPane.showMessageDialog(null, 
                        currPlayer.getPlayerName() + " built a hotel on " + propertyName + "!",
                        "Hotel Built", JOptionPane.INFORMATION_MESSAGE);
                parentFrame.dispose();

            });
        }

        // Action Listener for no Button
        noButton.addActionListener( _ -> {
            buildHotelFrame.dispose();
            parentFrame.setVisible(true);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        buildHotelPanel.add(buttonPanel);
        buildHotelPanel.add(Box.createVerticalStrut(10));

        buildHotelFrame.getContentPane().add(buildHotelPanel);  // Add the panel to the frame's pane
        buildHotelFrame.pack();
        buildHotelFrame.setLocationRelativeTo(parentFrame);
        buildHotelFrame.setAlwaysOnTop(true);
        buildHotelFrame.setVisible(true);

    }

    private void showSellOptionsDialog(JFrame parentFrame, Player seller, String propertyName) {

        parentFrame.setVisible(false); // Hide the "Manage [Property Name]" frame

        Property propertyToSell = boardSpaces.getPropertyByName(propertyName);
        // This check should be redundant if sell button is only enabled for owned properties, but good for safety
        if (propertyToSell == null || propertyToSell.getOwner() != seller) {
            JOptionPane.showMessageDialog(parentFrame, "Error: You do not own this property or it's invalid.", "Sell Error", JOptionPane.ERROR_MESSAGE);
            parentFrame.setVisible(true);
            return;
        }

        JFrame sellOptionsFrame = new JFrame("Sell Options for " + propertyName);
        JPanel sellOptionsPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        sellOptionsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JButton sellToBankButton = new JButton("Sell to Bank");
        JButton sellToPlayerButton = new JButton("Sell to Another Player");
        JButton cancelSellButton = new JButton("Cancel");

        sellToBankButton.addActionListener(e -> {
            sellOptionsFrame.dispose();
            handleSellToBank(seller, propertyToSell, parentFrame); // parentFrame is the "Manage Property" dialog
        });

        sellToPlayerButton.addActionListener(e -> {
            sellOptionsFrame.dispose();
            handleSellToPlayer(seller, propertyToSell, parentFrame); // parentFrame is the "Manage Property" dialog
        });

        cancelSellButton.addActionListener(e -> {
            sellOptionsFrame.dispose();
            parentFrame.setVisible(true); // Re-show the "Manage [Property Name]" frame
        });

        sellOptionsPanel.add(new JLabel("How do you want to sell " + propertyName + "?", SwingConstants.CENTER));
        sellOptionsPanel.add(sellToBankButton);
        sellOptionsPanel.add(sellToPlayerButton);
        sellOptionsPanel.add(cancelSellButton);

        sellOptionsFrame.add(sellOptionsPanel);
        sellOptionsFrame.pack();
        sellOptionsFrame.setLocationRelativeTo(parentFrame);
        sellOptionsFrame.setAlwaysOnTop(true);
        sellOptionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        sellOptionsFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (parentFrame != null) {
                    parentFrame.setVisible(true);
                }
            }
        });
        sellOptionsFrame.setVisible(true);
    }

    private void handleSellToBank(Player seller, Property property, JFrame originalManagePropertyFrame) {
        Bank bank = boardSpaces.getBank();
        int totalMoneyGainedThisTransaction = 0;

        // Step 1: Sell improvements on this specific property first
        if (property.getNumHouses() > 0 || property.getNumHotels() > 0) {
            int improvementsValue = property.calcImprovementsSellValue();
            int confirmImprovements = JOptionPane.showConfirmDialog(null,
                    "To sell " + property.getName() + " to the bank, all its improvements must be sold first for $" + improvementsValue + ".\n" +
                    "Proceed with selling improvements?",
                    "Sell Improvements to Bank", JOptionPane.YES_NO_OPTION);

            if (confirmImprovements == JOptionPane.YES_OPTION) {
                int moneyFromImprovements = property.clearPropertyAndReturnToBank(bank); // This updates bank and property
                seller.updateMoney(moneyFromImprovements);
                totalMoneyGainedThisTransaction += moneyFromImprovements;
                updatePlayerPanel(seller); // Update GUI for seller
                JOptionPane.showMessageDialog(null, "Sold improvements on " + property.getName() + " for $" + moneyFromImprovements + ".");
            } else {
                JOptionPane.showMessageDialog(null, "Sale of " + property.getName() + " to bank cancelled (improvements not sold).");
                originalManagePropertyFrame.setVisible(true); // Re-show the "Manage Property" frame
                return;
            }
        }

        // Step 2: Sell the bare property
        int propertySellPrice = property.getMortgageValue(); // Usually mortgage value or half original price
                                                            // Using getMortgageValue() as it's defined.
                                                            // If you want exactly half price: property.getPrice() / 2;
        int confirmSellProperty = JOptionPane.showConfirmDialog(null,
                "Sell " + property.getName() + " (unimproved) to the bank for $" + propertySellPrice + "?",
                "Sell Property to Bank", JOptionPane.YES_NO_OPTION);

        if (confirmSellProperty == JOptionPane.YES_OPTION) {
            seller.updateMoney(propertySellPrice);
            totalMoneyGainedThisTransaction += propertySellPrice;
            
            seller.removeProperty(property); // Player.java method needed - will handle colorset updates
            property.setOwner(null);         // Property is now unowned
            // property.setMortgaged(false); // Selling to bank typically clears mortgage status

            updatePlayerPanel(seller);
            updateBuildButtonState(); // Crucial, as player might lose a color set or ability to build

            JOptionPane.showMessageDialog(null, property.getName() + " sold to the bank for $" + propertySellPrice + ".\n" +
                    "Total gained from this transaction: $" + totalMoneyGainedThisTransaction);
            originalManagePropertyFrame.dispose(); // Sale complete, close the "Manage Property" frame
        } else {
            JOptionPane.showMessageDialog(null, "Sale of " + property.getName() + " to bank cancelled.");
            // If they cancelled selling property but sold improvements, improvements are gone.
            originalManagePropertyFrame.setVisible(true); // Re-show to reflect (now unimproved) property
        }
    }

    // In GameGUI.java
private void handleSellToPlayer(Player seller, Property property, JFrame originalManagePropertyFrame) {
    Bank bank = boardSpaces.getBank(); // Needed for selling improvements

    // Step 1: Property MUST be unimproved to be sold to another player.
    if (property.getNumHouses() > 0 || property.getNumHotels() > 0) {
        int improvementsValue = property.calcImprovementsSellValue();
        int confirmSellImprovements = JOptionPane.showConfirmDialog(null,
                "Properties must be unimproved to sell to another player.\n" +
                "You must first sell all improvements on " + property.getName() + " to the bank for $" + improvementsValue + ".\n" +
                "Proceed with selling improvements to the bank?",
                "Sell Improvements to Bank", JOptionPane.YES_NO_OPTION);

        if (confirmSellImprovements == JOptionPane.YES_OPTION) {
            int moneyFromImprovements = property.clearPropertyAndReturnToBank(bank);
            seller.updateMoney(moneyFromImprovements);
            updatePlayerPanel(seller);
            JOptionPane.showMessageDialog(null, "Sold improvements on " + property.getName() + " for $" + moneyFromImprovements + ".");
            // Now property is unimproved, can proceed
        } else {
            JOptionPane.showMessageDialog(null, "Sale to another player cancelled (improvements not sold to bank).");
            originalManagePropertyFrame.setVisible(true);
            return;
        }
    }

    // Step 2: Select Buyer
    List<Player> potentialBuyers = new ArrayList<>();
    for (Player p : players) {
        if (p != seller && !p.getIsEliminated()) {
            potentialBuyers.add(p);
        }
    }

    if (potentialBuyers.isEmpty()) {
        JOptionPane.showMessageDialog(null, "No other active players to sell to.");
        originalManagePropertyFrame.setVisible(true);
        return;
    }

    String[] buyerNames = potentialBuyers.stream().map(Player::getPlayerName).toArray(String[]::new);
    String selectedBuyerName = (String) JOptionPane.showInputDialog(null,
            "Select player to sell " + property.getName() + " to:",
            "Select Buyer", JOptionPane.QUESTION_MESSAGE, null, buyerNames, buyerNames[0]);

    if (selectedBuyerName == null) { // User cancelled buyer selection
        originalManagePropertyFrame.setVisible(true);
        return;
    }

    Player buyer = null;
    for (Player p : potentialBuyers) {
        if (p.getPlayerName().equals(selectedBuyerName)) {
            buyer = p;
            break;
        }
    }

    if (buyer == null) { // Should not happen
        JOptionPane.showMessageDialog(null, "Error selecting buyer. Sale cancelled.");
        originalManagePropertyFrame.setVisible(true);
        return;
    }

    // Step 3: Seller sets the price
    String priceString = JOptionPane.showInputDialog(null,
            seller.getPlayerName() + ", enter the selling price for " + property.getName() + " to " + buyer.getPlayerName() + ":",
            "Set Selling Price", JOptionPane.QUESTION_MESSAGE);

    int agreedPrice;
    try {
        agreedPrice = Integer.parseInt(priceString);
        if (agreedPrice < 0) {
            JOptionPane.showMessageDialog(null, "Price cannot be negative. Sale cancelled.");
            originalManagePropertyFrame.setVisible(true);
            return;
        }
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(null, "Invalid price entered. Sale cancelled.");
        originalManagePropertyFrame.setVisible(true);
        return;
    }

    // Step 4: Buyer Confirmation
    int confirmPurchase = JOptionPane.showConfirmDialog(null,
            buyer.getPlayerName() + ", do you want to buy " + property.getName() + "\nfrom " + seller.getPlayerName() + " for $" + agreedPrice + "?\n" +
            "Your current money: $" + buyer.getMoney(),
            "Confirm Purchase", JOptionPane.YES_NO_OPTION);

    if (confirmPurchase == JOptionPane.YES_OPTION) {
        if (buyer.getMoney() >= agreedPrice) {
            seller.updateMoney(agreedPrice);
            buyer.updateMoney(-agreedPrice);

            seller.removeProperty(property); // Updates seller's internal lists and color sets
            buyer.addProperty(property);   // Updates buyer's internal lists and color sets
            property.setOwner(buyer);      // Critical: Set new owner on the Property object

            updatePlayerPanel(seller);
            updatePlayerPanel(buyer);
            updateBuildButtonState(); // For both players, as color sets might have changed status

            JOptionPane.showMessageDialog(null,
                    property.getName() + " sold by " + seller.getPlayerName() + " to " + buyer.getPlayerName() + " for $" + agreedPrice + ".");
            originalManagePropertyFrame.dispose(); // Sale complete, close the "Manage Property" frame
        } else {
            JOptionPane.showMessageDialog(null, buyer.getPlayerName() + " does not have enough money ($" + agreedPrice + ") to buy " + property.getName() + ".");
            originalManagePropertyFrame.setVisible(true); // Transaction failed, re-show "Manage Property"
        }
    } else {
        JOptionPane.showMessageDialog(null, buyer.getPlayerName() + " declined to purchase " + property.getName() + ".");
        originalManagePropertyFrame.setVisible(true); // Transaction failed, re-show "Manage Property"
    }
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
