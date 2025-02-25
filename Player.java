import java.util.Random;

public class Player {
    private final String name;
    private int money;
    private int position;

    public Player(String name, int initialMoney) {
        this.name = name;
        this.money = initialMoney;
        this.position = 0; // Starting at position 0 (Go space)
    }

    public String getName() {
        return this.name;
    }

    public int getMoney() {
        return this.money;
    }

    public int getPosition() {
        return this.position;
    }

    public void move(int spaces) {
        position = (position + spaces) % 40; // Monopoly board has 40 spaces
    }

    public void updateMoney(int amount) {
        this.money += amount;
    }

    public void rollAndMove() {
        Random rand = new Random();

        int diceOne = rand.nextInt(6) + 1;
        int diceTwo = rand.nextInt(6) + 1;
        int result = diceOne + diceTwo;

        String message = this.name + " rolled a " + diceOne + " and a " + diceTwo + " summing up for a total of " + result;
        System.out.println(message);
        this.move(result);
        System.out.println(this.name + " new location is at slot " + this.getPosition());
    }
}