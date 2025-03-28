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
        properties.put(2, new Property("Mediterranean Avenue", "Purple", 60, 2));
        properties.put(4, new Property("Baltic Avenue", "Purple", 60, 4));
        // Light Blue Street
        properties.put(7, new Property("Oriental Avenue", "Light Blue", 100, 6));
        properties.put(9, new Property("Vermont Avenue", "Light Blue", 100, 6));
        properties.put(10, new Property("Connecticut Avenue", "Light Blue", 120, 8));
        // Pink Street
        properties.put(12, new Property("St. Charles Place", "Pink", 140, 10));
        properties.put(14, new Property("States Avenue", "Pink", 140, 10));
        properties.put(15, new Property("Virginia Avenue", "Pink", 160, 12));
        // Orange Street
        properties.put(17, new Property("St. James Place", "Orange", 180, 14));
        properties.put(19, new Property("Tennessee Avenue", "Orange", 180, 14));
        properties.put(20, new Property("New York Avenue", "Orange", 200, 16));
        // Red Street
        properties.put(22, new Property("Kentucky Avenue", "Red", 220, 18));
        properties.put(24, new Property("Indiana Avenue", "Red", 220, 18));
        properties.put(25, new Property("Illinois Avenue", "Red", 240, 20));
        // Yellow Street
        properties.put(27, new Property("Atlantic Avenue", "Yellow", 260, 22));
        properties.put(28, new Property("Ventnor Avenue", "Yellow", 260, 22));
        properties.put(30, new Property("Marvin Gardens", "Yellow", 280, 24));
        // Green Street
        properties.put(32, new Property("Pacific Avenue", "Green", 300, 26));
        properties.put(33, new Property("North Carolina Avenue", "Green", 300, 26));
        properties.put(35, new Property("Pennsylvania Avenue", "Green", 320, 28));
        // Dark Blue Street
        properties.put(38, new Property("Park Place", "Dark Blue", 350, 35));
        properties.put(40, new Property("Boardwalk", "Dark Blue", 400, 50));
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
    
                currentPlayer.updateMoney(-rentAmount);
                property.getOwner().updateMoney(rentAmount);
    
                JOptionPane.showMessageDialog(null,
                    currentPlayer.getName() + " paid $" + rentAmount +
                    " rent to " + property.getOwner().getName() +
                    " for landing on " + property.getName() + "!");
            }
        }
    }

    public void purchaseProperty(Player currentPlayer, int spaceNumber, int diceRoll) {
        if (isProperty(spaceNumber)) {
            Property property = getProperty(spaceNumber);

            // Check if the property is already owned
            if (property.getOwner() != null) {
                payRent(currentPlayer, spaceNumber, diceRoll);
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
                } else {
                    JOptionPane.showMessageDialog(null, "You don't have enough money to purchase this property!");
                }
            }else{
                startAuction(property,allPlayers); // If the player decides to not buy a property then the auction will start
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
    public void payIncomeTax(Player player, int selection) {
        
        if (selection == 0) {       //  Pay $200 Income Tax
            JOptionPane.showMessageDialog(null, player.getName() + " paid a $200 income tax.");
            player.updateMoney(-200);
        }
        else {          //  Pay 10% of current income (money)
            int percentageOfIncome = (int)(Math.ceil(player.getMoney() * 0.10));
            JOptionPane.showMessageDialog(null, player.getName() + "paid a $" + 
                percentageOfIncome + " income tax.");
            player.updateMoney(-percentageOfIncome);
        }

    }

    public void payLuxuryTax(Player player) {
       player.updateMoney(-100);
    }
}