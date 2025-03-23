import javax.swing.*;
import java.util.Random;

public class Player {
    private final String name;
    private int money;
    private int position;
    private boolean isEliminated;
    private char playerToken;

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

    // ***BRANDON*** Function that sets the player token
    public void setToken(Character token) {
        this.playerToken = token;
    }

    // ***BRANDON*** Function the returns the player token
    public Character getToken() {
        return this.playerToken;
    }

    public void move(int spaces) {
        position = (position + spaces) % 40; // Monopoly board has 40 spaces
        if (position == 0) {    // 40 % 40 = 0, but 40th space is valid
            position = 40;
        }
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
        int previousPosition = this.position;
        this.move(result);

        checkPassedGo(previousPosition); // added this into the roll and move

    }

    //created checkPassedGo function
    private void checkPassedGo(int previousPosition) {
        // Check if player passed Go or landed on GO and awards 200 bucks
        if (this.position < previousPosition) {
            this.updateMoney(200);
            JOptionPane.showMessageDialog(null, this.name + " passed Go and earned $200!");
        }else if (this.position == 1) {
            this.updateMoney(200);
            JOptionPane.showMessageDialog(null, this.name + " landed on Go and earned $200!");
        }
    }
}
