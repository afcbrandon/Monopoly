import java.util.HashMap;
import java.util.Map;

public class GameStatsService {
    private Map<String, PlayerProfile> profiles = new HashMap<>();

    public void createNewProfile(String playerId, String username) {
        if (!profiles.containsKey(playerId)) {
            profiles.put(playerId, new PlayerProfile(playerId, username));
            System.out.println("Profile created for player: " + username);
        } else {
            System.out.println("Profile already exists for player ID: " + playerId);
        }
    }

    public PlayerProfile getProfile(String playerId) {
        return profiles.get(playerId);
    }

    public void updateGamesPlayed(String playerId) {
        PlayerProfile profile = getProfile(playerId);
        if (profile != null) {
            profile.incrementGamesPlayed();
        } else {
            System.out.println("Profile not found for player ID: " + playerId);
        }
    }

    public void updateGamesWon(String playerId) {
        PlayerProfile profile = getProfile(playerId);
        if (profile != null) {
            profile.incrementGamesWon();
        } else {
            System.out.println("Profile not found for player ID: " + playerId);
        }
    }

    public void updateMoneyEarned(String playerId, double amount) {
        PlayerProfile profile = getProfile(playerId);
        if (profile != null) {
            profile.addMoneyEarned(amount);
        } else {
            System.out.println("Profile not found for player ID: " + playerId);
        }
    }

    public void updatePropertiesOwned(String playerId, int change) {
        PlayerProfile profile = getProfile(playerId);
        if (profile != null) {
            if (change > 0) {
                for (int i = 0; i < change; i++) {
                    profile.incrementPropertiesOwned();
                }
            } else if (change < 0) {
                for (int i = 0; i < Math.abs(change); i++) {
                    profile.decrementPropertiesOwned();
                }
            }
        } else {
            System.out.println("Profile not found for player ID: " + playerId);
        }
    }

    public void displayProfile(String playerId) {
        PlayerProfile profile = getProfile(playerId);
        if (profile != null) {
            System.out.println("\n--- Player Stats ---");
            System.out.println(profile);
            System.out.println("--------------------");
        } else {
            System.out.println("Profile not found for player ID: " + playerId);
        }
    }
}