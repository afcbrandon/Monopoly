import javax.swing.*;
import java.util.Random;

public class ChanceCard {
    private final String description;

    // Constructor for ChanceCard
    public ChanceCard(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    // Method to apply the effect of the Chance card
    public void applyEffect(Player player, GameBoardSpaces gbSpace){

        // Apply the effects based on the card description
        switch (description) {
            case "Advance to Boardwalk":
                player.setPosition(39);  // Boardwalk is at position 39
                break;

            case "Advance to Go (Collect $200)":
                player.setPosition(1);  // Go space is at position 1
                player.updateMoney(200);
                break;

            case "Advance to Illinois Avenue. If you pass Go, collect $200":
                player.setPosition(24);
                if (player.getPosition() < 1) player.updateMoney(200); // Pass Go
                break;

            case "Advance to St. Charles Place. If you pass Go, collect $200":
                player.setPosition(11);
                if (player.getPosition() < 1) player.updateMoney(200); // Pass Go
                break;

            case "Advance to the nearest Railroad. If unowned, you may buy it from the Bank. If owned, pay twice the rental":
                // Logic to handle nearest Railroad
                int nearestRailroad = gbSpace.getNearestRailroad(player.getPosition());
                player.setPosition(nearestRailroad);
                Property railroad = gbSpace.getPropertyAt(nearestRailroad);
                if (railroad.getOwner() == null) {
                    if (player.getMoney() >= railroad.getPrice()) {
                        railroad.buyProperty(player);
                    }
                } else {
                    int rent = railroad.getRent() * 2; // Pay twice rent if owned
                    player.updateMoney(-rent);
                    railroad.getOwner().updateMoney(rent);
                }
                break;

            case "Advance to the nearest Utility. If unowned, you may buy it from the Bank. If owned, throw dice and pay owner 10 times the amount thrown":
                int nearestUtility = gbSpace.getNearestUtility(player.getPosition());
                player.setPosition(nearestUtility);
                Property utility = gbSpace.getPropertyAt(nearestUtility);
                if (utility.getOwner() == null) {
                    if (player.getMoney() >= utility.getPrice()) {
                        utility.buyProperty(player);
                    }
                } else {
                    int diceRoll = new Random().nextInt(6) + 1 + new Random().nextInt(6) + 1;
                    int rent = diceRoll * 10;
                    player.updateMoney(-rent);
                    utility.getOwner().updateMoney(rent);
                }
                break;

            case "Bank pays you dividend of $50":
                player.updateMoney(50);
                break;

            case "Get Out of Jail Free":
                player.setOutOfJailCard(true);  // Assuming player has an "out of jail" card attribute
                break;

            case "Go Back 3 Spaces":
                player.moveSpaces(-3);
                break;

            case "Go to Jail. Go directly to Jail, do not pass Go, do not collect $200":
                player.setPosition(11);  // Jail is at position 11
                player.setJailed(true);
                break;

            case "Make general repairs on all your property. For each house pay $25. For each hotel pay $100":
                int repairCost = 0;
                for (Property p : player.getOwnedProperties()) {
                    repairCost += p.getNumHouses() * 25;
                    repairCost += p.getNumHotels() * 100;
                }
                player.updateMoney(-repairCost);
                break;

            case "Speeding fine $15":
                player.updateMoney(-15);
                break;

            case "Take a trip to Reading Railroad. If you pass Go, collect $200":
                player.setPosition(5);  // Reading Railroad is at position 5
                if (player.getPosition() < 1) player.updateMoney(200); // Pass Go
                break;

            case "You have been elected Chairman of the Board. Pay each player $50":
                // Assuming a method exists to get all players
                for (Player otherPlayer : gbSpace.getPlayers()) {
                    if (otherPlayer != player) {
                        player.updateMoney(-50);
                        otherPlayer.updateMoney(50);
                    }
                }
                break;

            case "Your building loan matures. Collect $150":
                player.updateMoney(150);  // Collect $150 when the loan matures
                break;

            default:
                JOptionPane.showMessageDialog(null, "This Chance card is not recognized: " + description);
        }
    }
}
