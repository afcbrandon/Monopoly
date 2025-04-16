import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

public class TestChanceCard {

    public static void main(String[] args) {
        // Create a mock list of players
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player("Player 1", null));
        players.add(new Player("Player 2", null));
        players.add(new Player("Player 3", null));
        players.add(new Player("Player 4", null));
        players.add(new Player("Player 5", null));; // Temporarily null

        // Create a mock GameBoardSpaces object and link players
        GameBoardSpaces gbSpace = new GameBoardSpaces(players);

        // Create all ChanceCard examples
        ArrayList<ChanceCard> chanceCards = new ArrayList<>();
        chanceCards.add(new ChanceCard("Advance to Boardwalk"));
        chanceCards.add(new ChanceCard("Bank pays you dividend of $50"));
        chanceCards.add(new ChanceCard("Go to Jail. Go directly to Jail, do not pass Go, do not collect $200"));
        chanceCards.add(new ChanceCard("Advance to Go (Collect $200)"));
        chanceCards.add(new ChanceCard("Advance to Illinois Avenue. If you pass Go, collect $200"));
        chanceCards.add(new ChanceCard("Advance to St. Charles Place. If you pass Go, collect $200"));
        chanceCards.add(new ChanceCard("Advance to the nearest Railroad. If unowned, you may buy it from the Bank. If owned, pay twice the rental"));
        chanceCards.add(new ChanceCard("Advance to the nearest Utility. If unowned, you may buy it from the Bank. If owned, throw dice and pay owner 10 times the amount thrown"));
        chanceCards.add(new ChanceCard("Get Out of Jail Free"));
        chanceCards.add(new ChanceCard("Go Back 3 Spaces"));
        chanceCards.add(new ChanceCard("Make general repairs on all your property. For each house pay $25. For each hotel pay $100"));
        chanceCards.add(new ChanceCard("Speeding fine $15"));
        chanceCards.add(new ChanceCard("Take a trip to Reading Railroad. If you pass Go, collect $200"));
        chanceCards.add(new ChanceCard("You have been elected Chairman of the Board. Pay each player $50"));
        chanceCards.add(new ChanceCard("Your building loan matures. Collect $150"));

        // Test each ChanceCard
        for (ChanceCard card : chanceCards) {
            resetPlayer(players.get(0));
            testChanceCard(card, players.get(0), gbSpace);
        }
    }

    private static void testChanceCard(ChanceCard chanceCard, Player player, GameBoardSpaces gbSpace) {
        int beforePosition = player.getPosition();
        int beforeMoney = player.getMoney();
        boolean wasJailed = player.getIsJailed();
        boolean hadOutOfJailCard = player.hasOutOfJailCard();

        // Prepare optional extra info
        String extraInfo = "";

        if (chanceCard.getDescription().contains("nearest Utility")) {
            int nearestUtilPos = gbSpace.getNearestUtility(beforePosition);
            String utilName = gbSpace.getPropertyAt(nearestUtilPos).getName();
            extraInfo = "\nNearest Utility: " + utilName + " (Position " + nearestUtilPos + ")";
        }
        if (chanceCard.getDescription().contains("nearest Railroad")) {
            int nearestRRPos = gbSpace.getNearestRailroad(beforePosition);
            String rrName = gbSpace.getPropertyAt(nearestRRPos).getName();
            extraInfo += "\nNearest Railroad: " + rrName + " (Position " + nearestRRPos + ")";
        }

        JOptionPane.showMessageDialog(null, "Testing Chance Card: " + chanceCard.getDescription() + extraInfo);

        // Apply the ChanceCard effect
        chanceCard.applyEffect(player, gbSpace);

        int afterPosition = player.getPosition();
        int afterMoney = player.getMoney();
        boolean isJailed = player.getIsJailed();
        boolean hasOutOfJailCard = player.hasOutOfJailCard();

        JOptionPane.showMessageDialog(null,
                "Before:\nPosition: " + beforePosition +
                        "\nMoney: $" + beforeMoney +
                        "\nJailed: " + (wasJailed ? "Yes" : "No") +
                        "\nHad Out of Jail Card: " + (hadOutOfJailCard ? "Yes" : "No") +
                        "\n\nAfter:\nPosition: " + afterPosition +
                        "\nMoney: $" + afterMoney +
                        "\nJailed: " + (isJailed ? "Yes" : "No") +
                        "\nHas Out of Jail Card: " + (hasOutOfJailCard ? "Yes" : "No"));
    }

    private static void resetPlayer(Player player) {
        Random rand = new Random();
        int randomPosition = rand.nextInt(40); // Random board position (0-39)
        int defaultMoney = 1500;

        player.setPosition(randomPosition);
        player.setJailed(false);
        player.setOutOfJailCard(false);
        player.setMoney(defaultMoney);
    }
}
