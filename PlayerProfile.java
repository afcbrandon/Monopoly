public class PlayerProfile {
    private String name;
    private int balance;
    private int position;
    private PlayerToken token;

    public PlayerProfile(String name, int balance, int position, PlayerToken token) {
        this.name = name;
        this.balance = balance;
        this.position = position;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public PlayerToken getToken() {
        return token;
    }

    public void setToken(PlayerToken token) {
        this.token = token;
    }

    public void updateBalance(int amount) {
        this.balance += amount;
    }

    @Override
    public String toString() {
        return "PlayerProfile{" +
                "name='" + name + '\'' +
                ", balance=" + balance +
                ", position=" + position +
                ", token=" + token +
                '}';
    }
}
