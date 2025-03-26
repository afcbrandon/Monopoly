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

    public void purchaseProperty(Player currentPlayer, int spaceNumber) {
        if (isProperty(spaceNumber)) {
            Property property = getProperty(spaceNumber);
            if (property.getOwner() != null){
                int price = property.getPrice();
                int choice = JOptionPane.showConfirmDialog(null, currentPlayer.getName() + ", do you want to purchase " + property.getName() + " for $ " + price + "?",
                        "Purchase Property", JOptionPane.YES_NO_OPTION); // this is how we will save the choice they take
                //Here
                if (choice == JOptionPane.YES_OPTION) {
                    boolean success = property.buyProperty(currentPlayer);
                    // will check if they can or cant buy the property and yeah
                    if (success) {
                        JOptionPane.showMessageDialog(null,currentPlayer.getName() + " successfully purchased "  + property.getName() + "!");
                    } else {
                        JOptionPane.showMessageDialog(null, "You don't have enough money to purchase this property!");
                    }
                }
            }else{
                JOptionPane.showMessageDialog(null,"This property is owned by " + property.getOwner().getName() + "!");
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