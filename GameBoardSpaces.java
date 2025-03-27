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
        initializeProperties();
    }

    public void initializeProperties() {
        //This will add properties to HashMap with position Key
        properties.put(3, new Property("Mediterranean Avenue", 60, 4));
        properties.put(13, new Property("Electric Company", 150, 0));
        properties.put(29, new Property("Water Works", 150, 0));
        properties.put(6, new Property("Reading Railroad", 200, 25));
        properties.put(16, new Property("Pennsylvania Railroad", 200, 25));
        properties.put(26, new Property("B. & O. Railroad", 200, 25));
        properties.put(36, new Property("Short Line", 200, 25));

    }

    public Property getPropertyBySpace(int position) {
        return properties.get(position);
    }

    /// Function that checks the type of the board space
    public void spaceType(int spaceNum) {

        switch (spaceNum) {
            case 1:     // Go Space
                break;
            case 3:     // Chest Spaces
            case 18:
            case 34:
                // TODO: Calls a function that draws a chest card
                break;
            case 5:     // Tax Spaces
            case 39:
                // TODO: Calls a function that prompts player to pay a tax
                break;
            case 8:     // Chance Spaces
            case 23:
            case 37:
                // TODO: Calls a function that draws a chance card
                break;
            case 11:
            case 31:
                break;
            case 21:    // Free Parking Space
                // TODO: NOTHING! FREE SPACE! Maybe tell user they are on a free parking space
                break;
            default:    // Property Spaces
                // TODO: Calls a function that handles property options (Buying, Selling, Mortgaging, etc.)
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

    public void purchaseProperty(Player currentPlayer, int spaceNumber) {
        if (isProperty(spaceNumber)) {
            Property property = getProperty(spaceNumber);

            // Check if the property is already owned
            if (property.getOwner() != null) {
                JOptionPane.showMessageDialog(null, "This property is owned by " + property.getOwner().getName() + "!");
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
}