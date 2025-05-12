import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
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
    //  private JButton buildButton;
    private JButton manageAssetsButton;
    private int currentPlayerIndex;
    private GameBoardSpaces boardSpaces;
    private Bank bank;

    public GameGUI(ArrayList<Player> players) {
        this.players = players;
        this.boardSpaces = new GameBoardSpaces(players, this);
        this.bank = this.boardSpaces.getBank();
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

        // --- Control Panel Buttons ---
        rollButton = new JButton("Roll Dice");
        rollButton.setFocusable(false);
        rollButton.addActionListener( e -> {
            Player currentPlayer = players.get(currentPlayerIndex);

            if ( !currentPlayer.getIsJailed() ) {
                currentPlayer.playerRoll(); // Perform one roll actions
                updatePlayerPanel(currentPlayer);   // Update after roll
                currentPlayer.handleLandingOnSpace(currentPlayer.getPosition());
                updatePlayerPanel(currentPlayer); // Update panel again after landing action

                // Disable roll button if they didn't roll doubles, enable end turn
                if ( !currentPlayer.getRolledDouble() || currentPlayer.getIsJailed() ) {
                    rollButton.setEnabled(false);
                    endTurnButton.setEnabled(true);
                } else {
                    // Rolled doubles, can roll again
                    rollButton.setEnabled(true);
                    endTurnButton.setEnabled(false);
                    JOptionPane.showMessageDialog(playerGUIFrame, "You rolled doubles! Roll again.", "Doubles!", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                // If jailed, rolling is handled within the getOutOfJail logic
                currentPlayer.getOutOfJail();
                updatePlayerPanel(currentPlayer);
                // After attempting to get out of jail, enable end turn
                rollButton.setEnabled(false);
                endTurnButton.setEnabled(true);
            }

            updateManageAssetsButtonState();    // Update asset button state

        });

        endTurnButton = new JButton("End Turn");
        endTurnButton.setFocusable(false);
        endTurnButton.setEnabled(false);
        endTurnButton.addActionListener( e -> {
            endTurn();
            // Set the button state for the next player
            Player newCurrentPlayer = players.get(currentPlayerIndex);
            if ( newCurrentPlayer instanceof Bot && !newCurrentPlayer.getIsEliminated() ) {
                handleBotTurns();   // Only call if the new current player is an active bot
            }

        });

        // Manage Assets Button
        manageAssetsButton = new JButton("Manage Assets");
        manageAssetsButton.setFocusable(false);
        manageAssetsButton.addActionListener( e -> {
            Player currentPlayer = players.get(currentPlayerIndex);
            showManageAssetsDialog(currentPlayer);
        });
        updateManageAssetsButtonState(); // Set initial state

        // Debug button
        JButton debugButton = new JButton("Debug");
        debugButton.setFocusable(false);
        debugButton.addActionListener( e -> {
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
        controlPanel.add(manageAssetsButton);
        controlPanel.add(debugButton);

        playerGUIFrame.add(playersPanel, BorderLayout.CENTER);
        playerGUIFrame.add(controlPanel, BorderLayout.SOUTH);

        // Position player GUI next to board
        Point boardFrameLocation = boardFrame.getLocation();
        int boardFrameWIDTH = boardFrame.getWidth();
        int boardFrameX = boardFrameLocation.x;
        int boardFrameY = boardFrameLocation.y;
        int guiFrameX = boardFrameX + boardFrameWIDTH + 10; // Add 10px gap
        int guiFrameY = boardFrameY;

        playerGUIFrame.pack();
        playerGUIFrame.setLocation(guiFrameX, guiFrameY);
        playerGUIFrame.setVisible(true);

        startGame(); // begin the game logic

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

    private void updateManageAssetsButtonState() {
        Player currentPlayer = players.get(currentPlayerIndex);
        boolean canManage = !(currentPlayer instanceof Bot) 
                                && !currentPlayer.getIsEliminated() 
                                && !currentPlayer.getOwnedProperties().isEmpty();
        manageAssetsButton.setEnabled(canManage);
    }

    private void showManageAssetsDialog(Player player) {
        JFrame manageFrame = new JFrame("Manage Assets for " + player.getPlayerName());
        manageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        manageFrame.setAlwaysOnTop(true);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<Property> ownedProps = new ArrayList<>(player.getOwnedProperties());
        // Sort properties for consistent display (e.g., by name or color)
        ownedProps.sort(Comparator.comparing(Property::getName));

        if ( ownedProps.isEmpty() ) {
            mainPanel.add(new JLabel("You do not own any properties."));
        } else {
            // Create a scroll pane in case the list is long
            JPanel propertiesListPanel = new JPanel();
            propertiesListPanel.setLayout(new BoxLayout(propertiesListPanel, BoxLayout.Y_AXIS));

            for (Property prop : ownedProps) {
                JPanel propPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                propPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY)); // Separator line

                // Property Label (with color indicator if possible)
                String propLabelText = prop.getName();
                if ( prop.getStreetColor() != null ) {
                     propLabelText += " (" + prop.getStreetColor() + ")";
                }
                 if ( prop.getNumHotels() > 0 ) {
                     propLabelText += " [Hotel]";
                 } else if ( prop.getNumHouses() > 0 ) {
                     propLabelText += " [" + prop.getNumHouses() + " House" + (prop.getNumHouses() > 1 ? "s" : "") + "]";
                 }
                JLabel propLabel = new JLabel(propLabelText);
                propLabel.setPreferredSize(new Dimension(200, 20)); // Give label consistent width
                propPanel.add(propLabel);

                // --- Build/Improve Button ---
                JButton buildImproveButton = new JButton("Build/Improve");
                boolean canBuildOnThisProperty = false;
                if ( prop.getStreetColor() != null && player.ownsFullColorSet(prop.getStreetColor()) ) {
                    // Check if this specific property is eligible for the *next* build action
                    // (based on even building rules and house/hotel limits)
                    canBuildOnThisProperty = player.canBuildOnProperty(prop, boardSpaces); // Need this helper method in Player
                }
                buildImproveButton.setEnabled(canBuildOnThisProperty);
                buildImproveButton.addActionListener(e -> {
                    // This dialog should close before opening the next one
                    manageFrame.dispose();
                    // Call the existing flow, which handles house/hotel choice
                    houseOrHotelButtons(null, player, prop.getName()); // Pass null as parent, it's not needed here
                });
                propPanel.add(buildImproveButton);


                // --- Sell Button ---
                JButton sellButton = new JButton("Sell");
                sellButton.setEnabled(true); // Always possible to initiate selling owned property
                sellButton.addActionListener(e -> {
                    // This dialog should close before opening the next one
                    manageFrame.dispose();
                    showSellOptionsDialog(null, player, prop.getName()); // Pass null as parent
                });
                propPanel.add(sellButton);

                propertiesListPanel.add(propPanel);
            }
             JScrollPane scrollPane = new JScrollPane(propertiesListPanel);
             scrollPane.setPreferredSize(new Dimension(450, 300)); // Adjust size as needed
             mainPanel.add(scrollPane);
        }

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> manageFrame.dispose());
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Panel to center the close button
        closePanel.add(closeButton);
        mainPanel.add(closePanel);


        manageFrame.add(mainPanel);
        manageFrame.pack();
        manageFrame.setLocationRelativeTo(playerGUIFrame); // Position relative to the main player GUI
        manageFrame.setVisible(true);
    }

/*
    // Function that enables buildButton if player owns a Full Color Set
    private void updateBuildButtonState() {
        Player currPlayer = players.get(currentPlayerIndex);
        boolean canBuild = !currPlayer.getFullColorsets().isEmpty(); // returns false if hashmap that checks if player owns a full colorset is empty
        buildButton.setEnabled(canBuild);
        System.out.println("Checking buildButton enable state for player: " + currPlayer.getPlayerName());
        System.out.println("buildButton " + (canBuild ? "ENABLED" : "DISABLED") + " - Full color sets: " + canBuild);
    }
*/

    // Function that updates the player panel, and can be called from other Java classes
    public void updatePlayerPanel(Player player) {
        for (Component comp : playersPanel.getComponents()) {
            if (comp instanceof JPanel) {
                 JPanel panel = (JPanel) comp;
                 if (panel.getName() != null && panel.getName().equals(player.getPlayerName())) {
                     // Ensure components exist and are JLabels before casting
                     if (panel.getComponentCount() > 3) {
                         Component moneyComp = panel.getComponent(2);
                         Component posComp = panel.getComponent(3);

                         if (moneyComp instanceof JLabel) {
                             ((JLabel) moneyComp).setText("Money: $" + player.getMoney());
                         }
                         if (posComp instanceof JLabel) {
                             int pos = player.getPosition();
                             String desc = boardSpaces.isProperty(pos)
                                 ? boardSpaces.getProperty(pos).getName()
                                 : boardSpaces.spaceType(pos);
                             ((JLabel) posComp).setText("Position: " + pos + " - " + desc);
                         }
                     }
                     // Update background for eliminated players
                     panel.setBackground(player.getIsEliminated() ? Color.LIGHT_GRAY : playerGUIFrame.getBackground()); // Use default background
                     break; // Found the player, exit loop
                 }
            }
        }
        // Update button states after updating panel info
        if (player == players.get(currentPlayerIndex)) {
             updateManageAssetsButtonState();
             // Also update roll/end turn buttons based on current player state
             boolean canRoll = !player.getIsEliminated() && !(player instanceof Bot) && !endTurnButton.isEnabled(); // Can roll if turn not ended
             rollButton.setEnabled(canRoll);
        }
    }

    private void houseOrHotelButtons(JFrame parentFrameToDispose, Player currPlayer, String propertyName) {

        if ( parentFrameToDispose != null ) {
            parentFrameToDispose.setVisible(false);
        }
        
        JFrame houseHotelFrame = new JFrame("Build on " + propertyName);
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
        });
        houseHotelPanel.add(cancelButton);
        
        houseHotelFrame.add(houseHotelPanel);
        houseHotelFrame.pack();

        houseHotelFrame.setLocationRelativeTo(playerGUIFrame);
        houseHotelFrame.setAlwaysOnTop(true);
        houseHotelFrame.setVisible(true);
    }

    private void buildHouses(JFrame frameToDispose, String propertyName) {

        Player currPlayer = players.get(currentPlayerIndex);
        Property property = boardSpaces.getPropertyByName(propertyName);
        Bank bank = boardSpaces.getBank();

        frameToDispose.setVisible(false);  // hide the parent frame

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
                updateManageAssetsButtonState();    // update main button state

                System.out.println(currPlayer.getPlayerName() + " built a house on " + property.getName() +
                                    ". Houses: " + property.getNumHouses() +
                                    ". Bank Houses: " + bank.getAvailableHouses());

                JOptionPane.showMessageDialog(null, 
                        currPlayer.getPlayerName() + " built a house on " + propertyName + "!",
                        "House Built", JOptionPane.INFORMATION_MESSAGE);
            });
        }

        // Action Listener for no Button
        noButton.addActionListener( _ -> {
            buildHouseFrame.dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        buildHousePanel.add(buttonPanel);
        buildHousePanel.add(Box.createVerticalStrut(10));

        buildHouseFrame.getContentPane().add(buildHousePanel);  // Add the panel to the frame's pane
        buildHouseFrame.pack();
        buildHouseFrame.setLocationRelativeTo(playerGUIFrame);
        buildHouseFrame.setAlwaysOnTop(true);
        buildHouseFrame.setVisible(true);
    }

    private void buildHotel(JFrame frameToDispose, String propertyName) {

        Player currPlayer = players.get(currentPlayerIndex);
        Property property = boardSpaces.getPropertyByName(propertyName);
        Bank bank = boardSpaces.getBank();

        frameToDispose.setVisible(false);  // hide the parent frame

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
                updateManageAssetsButtonState();

                System.out.println(currPlayer.getPlayerName() + " built a hotel on " + property.getName() +
                                    ". Bank Houses: " + bank.getAvailableHouses() +
                                    ", Bank Hotels: " + bank.getAvailableHotels());

                JOptionPane.showMessageDialog(null, 
                        currPlayer.getPlayerName() + " built a hotel on " + propertyName + "!",
                        "Hotel Built", JOptionPane.INFORMATION_MESSAGE);
            });
        }

        // Action Listener for no Button
        noButton.addActionListener( _ -> {
            buildHotelFrame.dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        buildHotelPanel.add(buttonPanel);
        buildHotelPanel.add(Box.createVerticalStrut(10));

        buildHotelFrame.getContentPane().add(buildHotelPanel);  // Add the panel to the frame's pane
        buildHotelFrame.pack();
        buildHotelFrame.setLocationRelativeTo(playerGUIFrame);
        buildHotelFrame.setAlwaysOnTop(true);
        buildHotelFrame.setVisible(true);

    }

    private void showSellOptionsDialog(JFrame frameToDispose, Player seller, String propertyName) {

        if ( frameToDispose != null ) {
            frameToDispose.dispose();
        }

        Property propertyToSell = boardSpaces.getPropertyByName(propertyName);
        // This check should be redundant if sell button is only enabled for owned properties, but good for safety
        if (propertyToSell == null || propertyToSell.getOwner() != seller) {
            JOptionPane.showMessageDialog(frameToDispose, "Error: You do not own this property or it's invalid.", "Sell Error", JOptionPane.ERROR_MESSAGE);
            frameToDispose.setVisible(true);
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
            handleSellToBank(seller, propertyToSell, null); // parentFrame is the "Manage Property" dialog
        });

        sellToPlayerButton.addActionListener(e -> {
            sellOptionsFrame.dispose();
            handleSellToPlayer(seller, propertyToSell, null); // parentFrame is the "Manage Property" dialog
        });

        cancelSellButton.addActionListener(e -> {
            sellOptionsFrame.dispose();
        });

        sellOptionsPanel.add(new JLabel("How do you want to sell " + propertyName + "?", SwingConstants.CENTER));
        sellOptionsPanel.add(sellToBankButton);
        sellOptionsPanel.add(sellToPlayerButton);
        sellOptionsPanel.add(cancelSellButton);

        sellOptionsFrame.add(sellOptionsPanel);
        sellOptionsFrame.pack();
        sellOptionsFrame.setLocationRelativeTo(playerGUIFrame);
        sellOptionsFrame.setAlwaysOnTop(true);
        sellOptionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        sellOptionsFrame.setVisible(true);
    }

    private void handleSellToBank(Player seller, Property property, JFrame frameToClose) {
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
                JOptionPane.showMessageDialog(playerGUIFrame, "Sold improvements on " + property.getName() + " for $" + moneyFromImprovements + ".");
                if ( frameToClose != null ) {
                    frameToClose.dispose();
                }
            } else {
                JOptionPane.showMessageDialog(playerGUIFrame, "Sale of " + property.getName() + " to bank cancelled (improvements not sold).");
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
            updateManageAssetsButtonState();; // Crucial, as player might lose a color set or ability to build

            JOptionPane.showMessageDialog(playerGUIFrame, property.getName() + " sold to the bank for $" + propertySellPrice + ".\n" +
                    "Total gained from this transaction: $" + totalMoneyGainedThisTransaction);
            if ( frameToClose != null ) {
                frameToClose.dispose();
            }
        } else {
            JOptionPane.showMessageDialog(playerGUIFrame, "Sale of " + property.getName() + " to bank cancelled.");
            // If they cancelled selling property but sold improvements, improvements are gone.
        }
    }

    private void handleSellToPlayer(Player seller, Property property, JFrame frameToClose) {
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
            JOptionPane.showMessageDialog(playerGUIFrame, "Sold improvements on " + property.getName() + " for $" + moneyFromImprovements + ".");
            // Now property is unimproved, can proceed
        } else {
            JOptionPane.showMessageDialog(playerGUIFrame, "Sale to another player cancelled (improvements not sold to bank).");
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
        return;
    }

    String[] buyerNames = potentialBuyers.stream().map(Player::getPlayerName).toArray(String[]::new);
    String selectedBuyerName = (String) JOptionPane.showInputDialog(null,
            "Select player to sell " + property.getName() + " to:",
            "Select Buyer", JOptionPane.QUESTION_MESSAGE, null, buyerNames, buyerNames[0]);

    if (selectedBuyerName == null) { // User cancelled buyer selection
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
            return;
        }
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(null, "Invalid price entered. Sale cancelled.");
        return;
    }

    // Step 4: Buyer Confirmation
    int confirmPurchase = JOptionPane.showConfirmDialog(playerGUIFrame,
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
            updateManageAssetsButtonState();; // For both players, as color sets might have changed status

            JOptionPane.showMessageDialog(playerGUIFrame,
                    property.getName() + " sold by " + seller.getPlayerName() + " to " + buyer.getPlayerName() + " for $" + agreedPrice + ".");
            if ( frameToClose != null ) {
                frameToClose.dispose();
            }
        } else {
            JOptionPane.showMessageDialog(playerGUIFrame, buyer.getPlayerName() + " does not have enough money ($" + agreedPrice + ") to buy " + property.getName() + ".");
        }
    } else {
        JOptionPane.showMessageDialog(playerGUIFrame, buyer.getPlayerName() + " declined to purchase " + property.getName() + ".");
    }
}


    // Function that ends the player's turn
    private void endTurn() {
         // Reset double roll status for the player whose turn just ended
        players.get(currentPlayerIndex).resetRolledDouble();

        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (players.get(currentPlayerIndex).getIsEliminated());

        checkWinner(); // Check if the game ends

        if (!playerGUIFrame.isVisible()) return; // Exit if game window closed by winner check

        Player currentPlayer = players.get(currentPlayerIndex);
        JOptionPane.showMessageDialog(playerGUIFrame, "It's now " + currentPlayer.getPlayerName() + "'s turn!");

        // Update button states for the NEW current player
        updateManageAssetsButtonState();
        boolean nextIsHuman = !(currentPlayer instanceof Bot);
        rollButton.setEnabled(nextIsHuman && !currentPlayer.getIsEliminated()); // Enable roll if human and not out
        endTurnButton.setEnabled(false); // End turn always disabled at start
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
