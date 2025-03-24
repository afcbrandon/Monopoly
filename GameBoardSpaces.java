import javax.swing.*;
import java.util.HashMap;

// class that will contain all the properties and non-properties
public class GameBoardSpaces {
    private HashMap<Integer, Property> properties;

    public GameBoardSpaces() {
        properties = new HashMap<>();

        properties.put(3, new Property("Mediterranean Avenue", 60, 4));

    }

    // This will get the property at a specified space
    public Property getProperty(int spaceNumber) {
        return properties.get(spaceNumber);
    }

    // Will check if space is a property
    public boolean isProperty(int spaceNumber) {
        return properties.containsKey(spaceNumber);
    }

    public void payRent(Player currentPlayer, int spaceNumber) {
        if (isProperty(spaceNumber)) {
            Property property = getProperty(spaceNumber);
            if (property.getOwner() != null && property.getOwner() != currentPlayer) {
                int rentAmount = property.getRent();
                currentPlayer.updateMoney(-rentAmount);
                property.getOwner().updateMoney(rentAmount);
                JOptionPane.showMessageDialog(null,currentPlayer.getName() + " paid $ " + rentAmount + " rent to " + property.getOwner().getName() + "!");
            }
        }
    }
    // We will use this in the future to be able to buy property
    public void setOwner(int spaceNumber, Player owner) {
        if (isProperty(spaceNumber)) {
            getProperty(spaceNumber).setOwner(owner);
        }
    }
}
class Property {
    private String name;
    private int price;
    private int rent;
    private Player owner;

    public Property(String name, int price, int rent) {
        this.name = name;
        this.price = price;
        this.rent = rent;
        this.owner = null; // every property starts with no owner
    }
    public String getName() {
        return name;
    }
    public int getRent() {
        return rent;
    }
    public Player getOwner() {
        return owner;
    }
    public void setOwner(Player owner) {
        this.owner = owner;
    }
}