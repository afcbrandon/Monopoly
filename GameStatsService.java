
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class GameStatsService {
    private Map<String, PlayerProfile> profiles = new HashMap<>();
    private ArrayList<Player> players; 

    // Constructor that receives the player list
    public GameStatsService(ArrayList<Player> players) {
        this.players = players;
        initializeProfiles(); 
    }

    // Method to initialize player profiles
    private void initializeProfiles() {
        if (players != null) {
            for (Player player : players) {
                
                //createNewProfile(player.getPlayerId(), player.getName());
            }
        }
    }

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

    // Updates player statistics.
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

 
    public void displayAllProfiles() {
        if (profiles.isEmpty()) {
            System.out.println("No player profiles available.");
            return;
        }

        System.out.println("\n--- All Player Stats ---");
        for (PlayerProfile profile : profiles.values()) {
            System.out.println(profile); 
            System.out.println("--------------------");
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
