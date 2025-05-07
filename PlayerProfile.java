public class PlayerProfile {
    private String playerId;
    private String username;
    private int gamesPlayed;
    private int gamesWon;
    private double totalMoneyEarned;
    private int propertiesOwned;

    public PlayerProfile(String playerId, String username) {
        this.playerId = playerId;
        this.username = username;
        this.gamesPlayed = 0;
        this.gamesWon = 0;
        this.totalMoneyEarned = 0;
        this.propertiesOwned = 0;
    }


    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public double getTotalMoneyEarned() {
        return totalMoneyEarned;
    }

    public void setTotalMoneyEarned(double totalMoneyEarned) {
        this.totalMoneyEarned = totalMoneyEarned;
    }

    public int getPropertiesOwned() {
        return propertiesOwned;
    }

    public void setPropertiesOwned(int propertiesOwned) {
        this.propertiesOwned = propertiesOwned;
    }

    // Method to update game statistics
    public void incrementGamesPlayed() {
        this.gamesPlayed++;
    }

    public void incrementGamesWon() {
        this.gamesWon++;
    }

    public void addMoneyEarned(double amount) {
        this.totalMoneyEarned += amount;
    }

    public void incrementPropertiesOwned() {
        this.propertiesOwned++;
    }

    public void decrementPropertiesOwned() {
        if (this.propertiesOwned > 0) {
            this.propertiesOwned--;
        }
    }

    @Override
    public String toString() {
        return "Player: " + username + " (ID: " + playerId + ")\n" +
               "Games Played: " + gamesPlayed + "\n" +
               "Games Won: " + gamesWon + "\n" +
               "Total Money Earned: $" + String.format("%.2f", totalMoneyEarned) + "\n" +
               "Properties Owned: " + propertiesOwned;
    }
}