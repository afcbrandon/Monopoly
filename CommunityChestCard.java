import javax.swing.*;

public class CommunityChestCard {
    private final String description;

    // Constructor
    public CommunityChestCard(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void applyEffect(Player player, GameBoardSpaces gbSpace) {

        switch (description) {
            case "Advance to Go (Collect $200)":
                player.setPosition(1);  // Go space is at position 1
                player.updateMoney(200);
                break;

            case "Bank error in your favor. Collect $200":
                player.updateMoney(200);
                break;

            case "Doctor’s fee. Pay $50":
                player.updateMoney(-50);
                break;

            case "From sale of stock you get $50":
                player.updateMoney(50);
                break;

            case "Get Out of Jail Free":
                player.setOutOfJailCard(true);
                break;

            case "Go to Jail. Go directly to jail, do not pass Go, do not collect $200":
                player.setPosition(11);  // Jail is at position 11
                player.setJailed(true);
                break;

            case "Holiday fund matures. Receive $100":
                player.updateMoney(100);
                break;

            case "Income tax refund. Collect $20":
                player.updateMoney(20);
                break;

            case "It is your birthday. Collect $10 from every player":
                for (Player otherPlayer : gbSpace.getPlayers()) {
                    if (otherPlayer != player) {
                        player.updateMoney(10);
                        otherPlayer.updateMoney(-10);
                    }
                }
                break;

            case "Life insurance matures. Collect $100":
                player.updateMoney(100);
                break;

            case "Pay hospital fees of $100":
                player.updateMoney(-100);
                break;

            case "Pay school fees of $50":
                player.updateMoney(-50);
                break;

            case "Receive $25 consultancy fee":
                player.updateMoney(25);
                break;

            case "You are assessed for street repairs. Pay $40 per house and $115 per hotel":
                int repairCost = 0;
                for (Property p : player.getOwnedProperties()) {
                    repairCost += p.getNumHouses() * 40;
                    repairCost += p.getNumHotels() * 115;
                }
                player.updateMoney(-repairCost);
                break;

            case "You have won second prize in a beauty contest. Collect $10":
                player.updateMoney(10);
                break;

            default:
                JOptionPane.showMessageDialog(null, "This Community Chest card is not recognized: " + description);
        }
    }
}
