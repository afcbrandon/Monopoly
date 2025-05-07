import java.util.ArrayList;

public class rentTest {
    public static void main(String[] args) {
        Player testPlayer = new Player("TestPlayer", new GameBoardSpaces(new ArrayList<>()),false);

        // Create properties for a specific color (e.g., Purple)
        // In your actual game, you would normally get these from your GameBoardSpaces,
        // but here we create them manually for testing.
        Property purple1 = new Property("Mediterranean Avenue", "Purple", 60, 2, 50, 50, 30, false);
        Property purple2 = new Property("Baltic Avenue", "Purple", 60, 4, 50, 50, 30, false);

        // Test 1: Only one property is owned; rent should remain base value.
        purple1.setOwner(testPlayer);
        // We use the base rent (since the full set is not owned).
        System.out.println("Rent for " + purple1.getName() + " without full set: " + purple1.getRent());

        // Test 2: Now, assign the second property to simulate owning the full set.
        purple2.setOwner(testPlayer);
        // Since the player owns all properties of the Purple set,
        // we assume the rent should double.
        int expectedRent = purple1.getRent() * 2;
        System.out.println("Expected doubled rent for " + purple1.getName() + ": " + expectedRent);

        // In a real implementation you might have a method like calculateRent() that factors this in.
        // For this demo, we simulate that check using the player's own method:
        boolean fullSet = testPlayer.ownsFullSet("Purple");
        int actualRent = fullSet ? purple1.getRent() * 2 : purple1.getRent();
        System.out.println("Actual rent (after checking full set): " + actualRent);
    }
}
