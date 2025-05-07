import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

public class TestCommunityChestCard {

    public static void main(String[] args) {
        // Create a mock list of players
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player("Player 1", null, false));
        players.add(new Player("Player 2",null,false));
        players.add(new Player("Player 3", null,false));
        players.add(new Player("Player 4", null,false));
        players.add(new Player("Player 5", null,false)); // Temporarily null

        // Create a mock GameBoardSpaces object and link players
        GameBoardSpaces gbSpace = new GameBoardSpaces(players);

        // Create all Community Chest card examples
        ArrayList<CommunityChestCard> communityChestCards = new ArrayList<>();
        communityChestCards.add(new CommunityChestCard("Advance to Go (Collect $200)"));
        communityChestCards.add(new CommunityChestCard("Bank error in your favor. Collect $200"));
        communityChestCards.add(new CommunityChestCard("Doctor’s fee. Pay $50"));
        communityChestCards.add(new CommunityChestCard("From sale of stock you get $50"));
        communityChestCards.add(new CommunityChestCard("Get Out of Jail Free"));
        communityChestCards.add(new CommunityChestCard("Go to Jail. Go directly to jail, do not pass Go, do not collect $200"));
        communityChestCards.add(new CommunityChestCard("Holiday fund matures. Receive $100"));
        communityChestCards.add(new CommunityChestCard("Income tax refund. Collect $20"));
        communityChestCards.add(new CommunityChestCard("It is your birthday. Collect $10 from every player"));
        communityChestCards.add(new CommunityChestCard("Life insurance matures. Collect $100"));
        communityChestCards.add(new CommunityChestCard("Pay hospital fees of $100"));
        communityChestCards.add(new CommunityChestCard("Pay school fees of $50"));
        communityChestCards.add(new CommunityChestCard("Receive $25 consultancy fee"));
        communityChestCards.add(new CommunityChestCard("You are assessed for street repairs. Pay $40 per house and $115 per hotel"));
        communityChestCards.add(new CommunityChestCard("You have won second prize in a beauty contest. Collect $10"));

        // Test each Community Chest card

        for (CommunityChestCard card : communityChestCards) {
            resetPlayer(players.get(0)); // Reset player before each card is tested
            testCommunityChestCard(card, players.get(0), gbSpace);
        }
    }

    private static void testCommunityChestCard(CommunityChestCard communityChestCard, Player player, GameBoardSpaces gbSpace) {
        int beforePosition = player.getPosition();
        int beforeMoney = player.getMoney();
        boolean wasJailed = player.getIsJailed();
        boolean hadOutOfJailCard = player.hasOutOfJailCard();

        // Prepare optional extra info
        String extraInfo = "";

        if (communityChestCard.getDescription().contains("nearest Utility")) {
            int nearestUtilPos = gbSpace.getNearestUtility(beforePosition);
            String utilName = gbSpace.getPropertyAt(nearestUtilPos).getName();
            extraInfo = "\nNearest Utility: " + utilName + " (Position " + nearestUtilPos + ")";
        }
        if (communityChestCard.getDescription().contains("nearest Railroad")) {
            int nearestRRPos = gbSpace.getNearestRailroad(beforePosition);
            String rrName = gbSpace.getPropertyAt(nearestRRPos).getName();
            extraInfo += "\nNearest Railroad: " + rrName + " (Position " + nearestRRPos + ")";
        }

        JOptionPane.showMessageDialog(null, "Testing Community Chest Card: " + communityChestCard.getDescription() + extraInfo);

        // Apply the Community Chest card effect
        communityChestCard.applyEffect(player, gbSpace);

        int afterPosition = player.getPosition();
        int afterMoney = player.getMoney();
        boolean isJailed = player.getIsJailed();
        boolean hasOutOfJailCard = player.hasOutOfJailCard();
        boolean isEliminated = player.getIsEliminated();
        boolean wasEliminated = player.getIsEliminated();

        JOptionPane.showMessageDialog(null,
                "Before:\nPosition: " + beforePosition +
                        "\nMoney: $" + beforeMoney +
                        "\nJailed: " + (wasJailed ? "Yes" : "No") +
                        "\nHad Out of Jail Card: " + (hadOutOfJailCard ? "Yes" : "No") +
                        "\nEliminated: " + (wasEliminated ? "Yes" : "No") + // Display eliminated status
                        "\n\nAfter:\nPosition: " + afterPosition +
                        "\nMoney: $" + afterMoney +
                        "\nJailed: " + (isJailed ? "Yes" : "No") +
                        "\nHas Out of Jail Card: " + (hasOutOfJailCard ? "Yes" : "No") +
                        "\nEliminated: " + (isEliminated ? "Yes" : "No")); // Display updated eliminated status
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