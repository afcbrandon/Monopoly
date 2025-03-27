import javax.swing.*;
import java.util.HashMap;

// class that will contain all the properties and non-properties
public class GameBoardSpaces {
    private HashMap<Integer, Property> properties;

    public GameBoardSpaces() {
        properties = new HashMap<>();

        properties.put(3, new Property("Mediterranean Avenue", 60, 4));
        properties.put(13, new Property("Electric Company", 150, 0));
        properties.put(29, new Property("Water Works", 150, 0));


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
            if (property.getOwner() != null){
                int price = property.getPrice();
                int choice = JOptionPane.showConfirmDialog(null, currentPlayer.getName() + ", do you want to purchase " + property.getName() + " for $ " + price + "?",
                        "Purchase Property", JOptionPane.YES_NO_OPTION); // this is how we will save the choice they take
           //Here
           if (choice == JOptionPane.YES_OPTION) {
            boolean success = property.buyProperty(currentPlayer);
            if (success) {
                
                currentPlayer.addProperty(property);
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