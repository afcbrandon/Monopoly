import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

// class that will contain all the properties and non-properties
public class GameBoardSpaces {

    private ArrayList<Player> allPlayers;
    private HashMap<Integer, Property> properties; // we will use the HashMap for fast lookup
    private List<ChanceCard> chanceDeck = new ArrayList<>();
    private List<CommunityChestCard> chestDeck = new ArrayList<>();
    private GameGUI gameGUI;

    public GameBoardSpaces(ArrayList<Player> players, GameGUI gameGUI) {
        this.allPlayers = players != null ? players : new ArrayList<>();
        this.gameGUI = gameGUI;     //  Initialize gameGUI
        properties = new HashMap<>();
        initializeBoardSpaces();
        initializeChanceDeck();
    }

    /*  ##################
        ### Properties ###
        ##################  */

    public ArrayList<Player> getPlayers() {
        return allPlayers;
    }

    /*  ##################
        ### Getters ###
        ##################  */

    /*  ##################
        ### Setters ###
        ##################  */

    /*  ##################
        ### Properties ###
        ##################  */

    /// Function that sets the properties of all the spaces on the board
    public void initializeBoardSpaces() {

        //  Railroads
        properties.put(6, new Property("Reading Railroad", 200, 25, 100));
        properties.put(16, new Property("Pennsylvania Railroad", 200, 25, 100));
        properties.put(26, new Property("B & O Railroad", 200, 25, 100));
        properties.put(36, new Property("Short Line", 200, 25, 100));

        //  Utilities
        properties.put(13, new Property("Electric Company", 150, 0, 75));
        properties.put(29, new Property("Water Works", 150, 0, 75));

        // Purple Street
        properties.put(2, new Property("Mediterranean Avenue", "Purple", 60,
                                        2, 50, 50, 30));
        properties.put(4, new Property("Baltic Avenue", "Purple", 60, 
                                        4, 50, 50, 30));

        // Light Blue Street
        properties.put(7, new Property("Oriental Avenue", "Light Blue", 100, 
                                        6, 50, 50, 50));
        properties.put(9, new Property("Vermont Avenue", "Light Blue", 100,
                                        6, 50, 50, 50));
        properties.put(10, new Property("Connecticut Avenue", "Light Blue", 120, 
                                        8, 50, 50, 60));

        // Pink Street
        properties.put(12, new Property("St. Charles Place", "Pink", 140, 
                                        10, 100, 100, 70));
        properties.put(14, new Property("States Avenue", "Pink", 140, 
                                        10,  100, 100,70));
        properties.put(15, new Property("Virginia Avenue", "Pink", 160, 
                                        12, 100, 100,80));

        // Orange Street
        properties.put(17, new Property("St. James Place", "Orange", 180, 
                                        14, 100, 100,90));
        properties.put(19, new Property("Tennessee Avenue", "Orange", 180, 
                                        14, 100, 100,90));
        properties.put(20, new Property("New York Avenue", "Orange", 200, 
                                        16, 100, 100, 90));

        // Red Street
        properties.put(22, new Property("Kentucky Avenue", "Red", 220, 
                                        18, 150, 150, 110));
        properties.put(24, new Property("Indiana Avenue", "Red", 220, 
                                        18, 150, 150, 110));
        properties.put(25, new Property("Illinois Avenue", "Red", 240, 
                                        20, 150, 150, 120));

        // Yellow Street
        properties.put(27, new Property("Atlantic Avenue", "Yellow", 260, 
                                        22, 150, 150, 130));
        properties.put(28, new Property("Ventnor Avenue", "Yellow", 260, 
                                    22, 150, 150, 130));
        properties.put(30, new Property("Marvin Gardens", "Yellow", 280, 
                                    24, 150, 150, 140));

        // Green Street
        properties.put(32, new Property("Pacific Avenue", "Green", 300, 
                                    26, 200, 200, 150));
        properties.put(33, new Property("North Carolina Avenue", "Green", 300, 
                                    26, 200, 200, 150));
        properties.put(35, new Property("Pennsylvania Avenue", "Green", 320, 
                                    28, 200, 200, 150));

        // Dark Blue Street
        properties.put(38, new Property("Park Place", "Dark Blue", 350, 
                                    35, 200, 200, 175));
        properties.put(40, new Property("Boardwalk", "Dark Blue", 400, 
                                    50, 200, 200, 200));
    }

    /// Function that checks, and returns, the type of the board space as a String
    public String spaceType(int spaceNum) {

        switch (spaceNum) {
            case 1:     //  Go Space
                return "Go";
            case 3:     //  Chest Space
            case 18:
            case 34:
                return "Chest";
            case 5:     //  Tax Space
            case 39:
                return "Tax";
            case 8:     //  Chance Space
            case 23:
            case 37:
                return "Chance";
            case 11:    //  Jail Space
            case 31:
                return "Jail";
            case 21:    //  Free Parking Space
                return "Parking";
            default:    // Property Space
                return "Property";
        }

    }

    // This will get the property at a specified space
    public Property getProperty(int spaceNumber) {
        return properties.get(spaceNumber);
    }

    public int getNearestRailroad(int position) {
        int[] railroads = {5, 15, 25, 35};
        for (int railroad : railroads) {
            if (railroad > position) return railroad;
        }
        return railroads[0];
    }


    public int getNearestUtility(int position) {
        int[] utilities = {13, 29};
        for (int utility : utilities) {
            if (utility > position) {
                return utility;
            }
        }
        return utilities[0]; // Wrap around
    }

    public Property getPropertyAt(int position) {
        return properties.containsKey(position) ? properties.get(position) : null;
    }


    public void initializeChanceDeck() {
        chanceDeck = new ArrayList<>();

        chanceDeck.add(new ChanceCard("Advance to Boardwalk"));
        chanceDeck.add(new ChanceCard("Advance to Go (Collect $200)"));
        chanceDeck.add(new ChanceCard("Advance to Illinois Avenue. If you pass Go, collect $200"));
        chanceDeck.add(new ChanceCard("Advance to St. Charles Place. If you pass Go, collect $200"));
        chanceDeck.add(new ChanceCard("Advance to the nearest Railroad. If unowned, you may buy it from the Bank. If owned, pay twice the rental"));
        chanceDeck.add(new ChanceCard("Advance to the nearest Utility. If unowned, you may buy it from the Bank. If owned, throw dice and pay owner 10 times the amount thrown"));
        chanceDeck.add(new ChanceCard("Bank pays you dividend of $50"));
        chanceDeck.add(new ChanceCard("Get Out of Jail Free"));
        chanceDeck.add(new ChanceCard("Go Back 3 Spaces"));
        chanceDeck.add(new ChanceCard("Go to Jail. Go directly to Jail, do not pass Go, do not collect $200"));
        chanceDeck.add(new ChanceCard("Make general repairs on all your property. For each house pay $25. For each hotel pay $100"));
        chanceDeck.add(new ChanceCard("Speeding fine $15"));
        chanceDeck.add(new ChanceCard("Take a trip to Reading Railroad. If you pass Go, collect $200"));
        chanceDeck.add(new ChanceCard("You have been elected Chairman of the Board. Pay each player $50"));
        chanceDeck.add(new ChanceCard("Your building loan matures. Collect $150"));

        Collections.shuffle(chanceDeck);  // Shuffle the deck so it’s random each game
    }
    public ChanceCard drawChanceCard(Player player) {
        if (chanceDeck.isEmpty()) {
            initializeChanceDeck(); // Reshuffle or refill deck
        }

        ChanceCard card = chanceDeck.removeFirst();

        JOptionPane.showMessageDialog(null, player.getPlayerName() + " drew a Chance card: " + card.getDescription());

        return card;
    }

    public void initializeChestDeck() {
        chestDeck.clear();

        chestDeck.add(new CommunityChestCard("Advance to Go (Collect $200)"));
        chestDeck.add(new CommunityChestCard("Bank error in your favor. Collect $200"));
        chestDeck.add(new CommunityChestCard("Doctor’s fee. Pay $50"));
        chestDeck.add(new CommunityChestCard("From sale of stock you get $50"));
        chestDeck.add(new CommunityChestCard("Get Out of Jail Free"));
        chestDeck.add(new CommunityChestCard("Go to Jail. Go directly to jail, do not pass Go, do not collect $200"));
        chestDeck.add(new CommunityChestCard("Holiday fund matures. Receive $100"));
        chestDeck.add(new CommunityChestCard("Income tax refund. Collect $20"));
        chestDeck.add(new CommunityChestCard("It is your birthday. Collect $10 from every player"));
        chestDeck.add(new CommunityChestCard("Life insurance matures. Collect $100"));
        chestDeck.add(new CommunityChestCard("Pay hospital fees of $100"));
        chestDeck.add(new CommunityChestCard("Pay school fees of $50"));
        chestDeck.add(new CommunityChestCard("Receive $25 consultancy fee"));
        chestDeck.add(new CommunityChestCard("You are assessed for street repairs. Pay $40 per house and $115 per hotel"));
        chestDeck.add(new CommunityChestCard("You have won second prize in a beauty contest. Collect $10"));

        Collections.shuffle(chestDeck);
    }

    public CommunityChestCard drawCommunityChestCard(Player player) {
        if (chestDeck.isEmpty()) {
            initializeChestDeck(); // Refill and reshuffle if empty
        }

        CommunityChestCard card = chestDeck.removeFirst();

        JOptionPane.showMessageDialog(null, player.getPlayerName() + " drew a Community Chest card: " + card.getDescription());


        return card;
    }

    // Will check if space is a property
    public boolean isProperty(int spaceNumber) {
        return properties.containsKey(spaceNumber);
    }

    protected void payRent(Player currentPlayer, int spaceNumber, int diceRoll) {
        if (isProperty(spaceNumber)) {
            Property property = getProperty(spaceNumber);
            Player owner = property.getOwner();

            if (property.getOwner() == null) {
                currentPlayer.surrenderAssetsToBank();
                return;
            }

            if (property.getOwner() != null && property.getOwner() != currentPlayer) {
                int rentAmount = property.getRent();

                // Check for utilities
                if (property.getName().equals("Electric Company") || property.getName().equals("Water Works")) {
                    int utilitiesOwned = property.getOwner().countUtilities();
                    rentAmount = (utilitiesOwned == 1) ? 4 * diceRoll : 10 * diceRoll;
                }
                // Check for railroads
                else if (property.getName().contains("Railroad")) {
                    int railroadsOwned = property.getOwner().countRailroads();
                    switch (railroadsOwned) {
                        case 2: rentAmount = 50; break;
                        case 3: rentAmount = 100; break;
                        case 4: rentAmount = 200; break;
                        default: rentAmount = 25; break;
                    }
                }

                if (currentPlayer.getMoney() >= rentAmount) {
                    currentPlayer.updateMoney(-rentAmount);
                    owner.updateMoney(rentAmount);
                    // Update both player and owner panel after rent is paid
                    gameGUI.updatePlayerPanel(currentPlayer);
                    gameGUI.updatePlayerPanel(owner);
                    JOptionPane.showMessageDialog(null,
                            currentPlayer.getPlayerName() + " paid $" + rentAmount +
                                    " rent to " + property.getOwner().getPlayerName() +
                                    " for landing on " + property.getName() + "!");
                } else {
                    // TODO: PLAYER MUST SELL ASSETS TO BANK TO PAY OFF DEBT TO PROPERTY OWNER
                    int option = JOptionPane.showConfirmDialog(null,
                            currentPlayer.getPlayerName() + " does not have enough money to pay $" + rentAmount + ".\n" +
                                    "Do you want to sell your assets to the bank to try and stay in the game?",
                            "Insufficient Funds", JOptionPane.YES_NO_OPTION);

                    if (option == JOptionPane.YES_OPTION) {
                        currentPlayer.sellAssetsToBankInteractive();
                        ;

                        if (currentPlayer.getMoney() >= rentAmount) {
                            currentPlayer.updateMoney(-rentAmount);
                            property.getOwner().updateMoney(rentAmount);
                            JOptionPane.showMessageDialog(null,
                                    currentPlayer.getPlayerName() + " paid $" + rentAmount + " rent to " +
                                            property.getOwner().getPlayerName() + " after selling assets!");
                        } else {
                            JOptionPane.showMessageDialog(null,
                                    currentPlayer.getPlayerName() + " still cannot afford the rent and is BANKRUPT!");
                            currentPlayer.transferAssetsTo(property.getOwner());
                        }
                    } else {
                        JOptionPane.showMessageDialog(null,
                                currentPlayer.getPlayerName() + " refused to sell assets and is BANKRUPT!");
                        currentPlayer.transferAssetsTo(property.getOwner());
                    }
                }

            }
        }
    }

    /* Function that allows player to purchase property when they land on a property space, or pay rent if someone else already owns the property */
    protected void purchaseProperty(Player currentPlayer, int spaceNumber, int diceRoll) {

        System.out.println("purchaseProperty called, gameGUI is: " + this.gameGUI);

        if (isProperty(spaceNumber)) {
            Property property = getProperty(spaceNumber);

            // Check if the property is already owned
            if (property.getOwner() == currentPlayer) {
                return; // exit early if player already owns the property
            }

            if (property.getOwner() != null) {
                payRent(currentPlayer, spaceNumber, diceRoll);
                return; // exit early after rent has been paid to owner of this property
            }

            int price = property.getPrice();

            //  Handle bots automatically
            if (currentPlayer instanceof Bot) {
                if (currentPlayer.getMoney() >= price) {
                    boolean success = property.buyProperty(currentPlayer);
                    if (success) {
                        currentPlayer.addProperty(property);
                        property.setOwner(currentPlayer);
                        String msg = currentPlayer.getPlayerName() + " bought " + property.getName() + " for $" + price;
                        System.out.println(msg);
                        JOptionPane.showMessageDialog(null, msg);
                    }
                } 
                else {
                    JOptionPane.showMessageDialog(null, currentPlayer.getPlayerName() + " cannot afford " + property.getName());
                }
            }

            //  Handle human players with a confirmation dialog
            else {
                int choice = JOptionPane.showConfirmDialog(
                        null,
                        currentPlayer.getPlayerName() + ", do you want to purchase " + property.getName() + " for $" + price + "?",
                        "Purchase Property",
                        JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    boolean success = property.buyProperty(currentPlayer);
                    if (success) {
                        currentPlayer.addProperty(property);
                        property.setOwner(currentPlayer);
                        gameGUI.updatePlayerPanel(currentPlayer);   // Update Player Panel stats
                        JOptionPane.showMessageDialog(null, currentPlayer.getPlayerName() + " successfully purchased " + property.getName() + "!");
                    } 
                    else {
                        JOptionPane.showMessageDialog(null, "You don't have enough money to purchase this property!");
                    }
                } 
                else {
                    startAuction(property, allPlayers); // Launch auction if declined
                }
            }
        }

    }

    public void startAuction(Property property, ArrayList<Player> allPlayers) {
        //shows message about the auction starting
        JOptionPane.showMessageDialog(null, "Auction for " + property.getName() + " has started!");

        Player highestBidder = null;
        int highestBid = 0;

        for (Player player : allPlayers) {
            if (player != property.getOwner()) {
                //here we get the input on how much they are bidding or if they even are bidding
                String bidInput = JOptionPane.showInputDialog(player.getPlayerName() + ", enter your bid for the property " + property.getName() + " (or 0 to pass): ");
                //checks that it actually has something
                if (bidInput != null && !bidInput.isEmpty()) {
                    try {
                        int bid = Integer.parseInt(bidInput);
                        //see if it is the highest bid and if they actually have that money
                        if (bid > highestBid && bid <= player.getMoney()) {
                            highestBid = bid;
                            highestBidder = player;
                        }
                    } catch (NumberFormatException e) {
                        //catches error
                        JOptionPane.showMessageDialog(null, "Invalid bid. Please enter a valid number.");
                    }
                }
            }
        }

        if (highestBidder != null) {
            highestBidder.updateMoney(-highestBid);                         //subtracks the amount of money they bid
            highestBidder.addProperty(property);
            property.setOwner(highestBidder);                               //sets as owner
            JOptionPane.showMessageDialog(null, highestBidder.getPlayerName() + " won the auction for $" + highestBid + "!");
            gameGUI.updatePlayerPanel(highestBidder);  // Update player panel for Player that bid the highest
        } else {
            JOptionPane.showMessageDialog(null, "No one bid on the property. It remains unowned.");
        }
    }

    /*  #####################
        ### Tax Functions ###
        #####################  */

    public int payIncomeTax(Player player, int selection) {

        if (selection == 0) {       //  Pay $200 Income Tax
            JOptionPane.showMessageDialog(null, player.getPlayerName() + " paid a $200 income tax.");
            return -200;       // Return negative 200, since the player will pay 200 dollars in tax
        }
        else {          //  Pay 10% of current income (money)
            int percentageOfIncome = (int)(Math.ceil(player.getMoney() * 0.10));
            JOptionPane.showMessageDialog(null, player.getPlayerName() + "paid a $" +
                    percentageOfIncome + " income tax.");

            return -percentageOfIncome;     //  Return the negative of percentageOfIncome because it will be subtracted from the player's money
        }
    }

    public int payLuxuryTax(Player player) {
        return -100;
    }
}