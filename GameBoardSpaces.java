import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;

// class that will contain all the properties and non-properties
public class GameBoardSpaces {

    private ArrayList<Player> allPlayers;
    private HashMap<Integer, Property> properties; // we will use the HashMap for fast lookup

    public GameBoardSpaces(ArrayList<Player> players) {
        this.allPlayers = players != null ? players : new ArrayList<>();
        properties = new HashMap<>();
        initializeBoardSpaces();
    }

    /// Function that sets the properties of all the spaces on the board
    public void initializeBoardSpaces() {
        //This will add properties to HashMap with position Key

        // Go Space

        // Jail Spaces

        // Chance Spaces

        // Chest Spaces

        // Tax Spaces

        /*  ##################
            ### Properties ###
            ##################  */

        //  Railroads
        properties.put(6, new Property("Reading Railroad", 200, 0));
        properties.put(16, new Property("Pennsylvania Railroad", 200, 0));
        properties.put(26, new Property("B & O Railroad", 200, 0));
        properties.put(36, new Property("Short Line", 200, 0));
        
        //  Utilities
        properties.put(13, new Property("Electric Company", 150, 0));
        properties.put(29, new Property("Water Works", 150, 0));

        // Purple Street
        properties.put(2, new Property("Mediterranean Avenue", "Purple", 60, 2, 50, 50, 30));
        properties.put(4, new Property("Baltic Avenue", "Purple", 60, 4, 50, 50, 30));
        // Light Blue Street
        properties.put(7, new Property("Oriental Avenue", "Light Blue", 100, 6, 50, 50, 50));
        properties.put(9, new Property("Vermont Avenue", "Light Blue", 100, 6, 50, 50, 50));
        properties.put(10, new Property("Connecticut Avenue", "Light Blue", 120, 8, 50, 50, 60));
        // Pink Street
        properties.put(12, new Property("St. Charles Place", "Pink", 140, 10, 100, 100, 70));
        properties.put(14, new Property("States Avenue", "Pink", 140, 10,  100, 100,70));
        properties.put(15, new Property("Virginia Avenue", "Pink", 160, 12, 100, 100,80));
        // Orange Street
        properties.put(17, new Property("St. James Place", "Orange", 180, 14, 100, 100,90));
        properties.put(19, new Property("Tennessee Avenue", "Orange", 180, 14, 100, 100,90));
        properties.put(20, new Property("New York Avenue", "Orange", 200, 16, 100, 100, 90));
        // Red Street
        properties.put(22, new Property("Kentucky Avenue", "Red", 220, 18, 150, 150, 110));
        properties.put(24, new Property("Indiana Avenue", "Red", 220, 18, 150, 150, 110));
        properties.put(25, new Property("Illinois Avenue", "Red", 240, 20, 150, 150, 120));
        // Yellow Street
        properties.put(27, new Property("Atlantic Avenue", "Yellow", 260, 22, 150, 150, 130));
        properties.put(28, new Property("Ventnor Avenue", "Yellow", 260, 22, 150, 150, 130));
        properties.put(30, new Property("Marvin Gardens", "Yellow", 280, 24, 150, 150,140));
        // Green Street
        properties.put(32, new Property("Pacific Avenue", "Green", 300, 26, 200, 200, 150));
        properties.put(33, new Property("North Carolina Avenue", "Green", 300, 26, 200, 200,150));
        properties.put(35, new Property("Pennsylvania Avenue", "Green", 320, 28, 200, 200,150));
        // Dark Blue Street
        properties.put(38, new Property("Park Place", "Dark Blue", 350, 35, 200, 200,175));
        properties.put(40, new Property("Boardwalk", "Dark Blue", 400, 50, 200, 200,200));
    }

    public Property getPropertyBySpace(int position) {
        return properties.get(position);
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

    // Will check if space is a property
    public boolean isProperty(int spaceNumber) {
        return properties.containsKey(spaceNumber);
    }

    public void payRent(Player currentPlayer, int spaceNumber, int diceRoll) {
        if (isProperty(spaceNumber)) {
            Property property = getProperty(spaceNumber);
            Player owner = currentPlayer;
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
                else if (owner.ownsFullSet(property.getStreetColor())) {
                    rentAmount *= 2;  // Double the rent if the full set is owned
                }
    
                currentPlayer.updateMoney(-rentAmount);
                property.getOwner().updateMoney(rentAmount);
    
                JOptionPane.showMessageDialog(null,
                    currentPlayer.getName() + " paid $" + rentAmount +
                    " rent to " + property.getOwner().getName() +
                    " for landing on " + property.getName() + "!");
            }
        }
    }

    /// Function that allows player to purchase property when they land on a property space, or pay rent if someone else already owns the property
    public void purchaseProperty(Player currentPlayer, int spaceNumber, int diceRoll) {
        if (isProperty(spaceNumber)) {
            Property property = getProperty(spaceNumber);

            // Check if the property is already owned
            if (property.getOwner() != null) {

                //  If-Else statement. If someone else owns the property then pay them, otherwise player has the option to build houses.
                // TODO: Eventually this function will check if player has houses and then ask to build hotels

                if(property.getOwner().getName().equals(currentPlayer.getName())) {     // Current Player owns the property
                    buildHouse(currentPlayer, spaceNumber);
                }
                else {
                    payRent(currentPlayer, spaceNumber, diceRoll);
                }

                return; // Exit the function early since the property is taken

            }

            // Proceed with the purchase since the property is not owned
            int price = property.getPrice();
            int choice = JOptionPane.showConfirmDialog(null, currentPlayer.getName() + ", do you want to purchase " + property.getName() + " for $" + price + "?",
                    "Purchase Property", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                boolean success = property.buyProperty(currentPlayer);
                if (success) {
                    currentPlayer.addProperty(property);
                    property.setOwner(currentPlayer); // Set the owner after purchase!
                    JOptionPane.showMessageDialog(null, currentPlayer.getName() + " successfully purchased " + property.getName() + "!");
                } 
                else {
                    JOptionPane.showMessageDialog(null, "You don't have enough money to purchase this property!");
                }
            }
            else {
                startAuction(property,allPlayers); // If the player decides to not buy a property then the auction will start
            }
        }
    }

    public void buildHouse(Player player, int spaceNumber) {
        //
        String[] options = { "Yes", "No" };
        var selection = JOptionPane.showOptionDialog(null, "", "Income Tax", 0, 1,
                     null, options, options[0]);
        if (selection == 0) {   //  Yes
            // TODO: Add Code Here
        }
        else {  //  No
            JOptionPane.showMessageDialog(null, player.getName() + " chose not to build a house.");
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
                String bidInput = JOptionPane.showInputDialog(player.getName() + ", enter your bid for the property " + property.getName() + " (or 0 to pass): ");
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
            highestBidder.updateMoney(-highestBid); //subtracks the amount of money they bid
            highestBidder.addProperty(property);
            property.setOwner(highestBidder);//sets as owner
            JOptionPane.showMessageDialog(null, highestBidder.getName() + " won the auction for $" + highestBid + "!");
        } else {
            JOptionPane.showMessageDialog(null, "No one bid on the property. It remains unowned.");
        }
    }
    // We will use this in the future to be able to buy property
    public void setOwner(int spaceNumber, Player owner) {
        if (isProperty(spaceNumber)) {
            getProperty(spaceNumber).setOwner(owner);
        }
    }

    /*
        Tax Functions
     */
    public int payIncomeTax(Player player, int selection) {
        
        if (selection == 0) {       //  Pay $200 Income Tax
            JOptionPane.showMessageDialog(null, player.getName() + " paid a $200 income tax.");
            return -200;       // Return negative 200, since the player will pay 200 dollars in tax
        }
        else {          //  Pay 10% of current income (money)
            int percentageOfIncome = (int)(Math.ceil(player.getMoney() * 0.10));
            JOptionPane.showMessageDialog(null, player.getName() + "paid a $" + 
                percentageOfIncome + " income tax.");
            
            return -percentageOfIncome;     //  Return the negative of percentageOfIncome because it will be subtracted from the player's money
        }
    }

    public int payLuxuryTax(Player player) {
       return -100;
    }
}