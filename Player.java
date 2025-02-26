import javax.swing.*;
import java.util.Random;

public class Player {
    private final String name;
    private int money;
    private int position;
    private boolean isEliminated;

    public Player(String name) {
        this.name = name;
        this.money = 1500;
        this.position = 1; // Starting at position 1 (Go space)
        this.isEliminated = false;
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
    
    public boolean isEliminated() {
        return isEliminated;
    }

    public void move(int spaces) {
        position = (position + spaces) % 40; // Monopoly board has 40 spaces
    }

    public void updateMoney(int amount) {
        this.money += amount;
        if (this.money < 0) {
            this.isEliminated = true; // Mark player as eliminated
        }
    }
    
    public void rollAndMove() {
        if (isEliminated) {
            JOptionPane.showMessageDialog(null, this.name + " is eliminated and cannot roll.");
            return;
        }

        Random rand = new Random();

        int diceOne = rand.nextInt(6) + 1;
        int diceTwo = rand.nextInt(6) + 1;
        int result = diceOne + diceTwo;
        JOptionPane.showMessageDialog(null, this.name + " rolled a "  + diceOne + " and a " + diceTwo + " summing up for a total of " + result,"Dice Roll", JOptionPane.INFORMATION_MESSAGE);

        this.move(result);


    }
}
