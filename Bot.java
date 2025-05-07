import javax.swing.*;
import java.util.Random;

public class Bot extends Player {
    private int doubleRollsInARow = 0;

    public Bot(String name, GameBoardSpaces gBoardSpaces) {
        super(name, gBoardSpaces, true); // Bot is set to true
    }

    public void playerTurn() {
        if (this.isJailed) {
            jailEscape();
            return;
        }

        boolean continueTurn;
        int turnCount = 0;

        do {
            int diceRoll = rollDice();
            this.globalDiceRoll = diceRoll;

            move(diceRoll);
            JOptionPane.showMessageDialog(null, this.name + " moved to position " + this.position);

            handleSpace(this.position);

            continueTurn = this.rolledDouble;
            turnCount++;

            // Go to jail after 3 consecutive doubles
            if (continueTurn) {
                doubleRollsInARow++;
                if (doubleRollsInARow == 3) {
                    JOptionPane.showMessageDialog(null, this.name + " rolled 3 doubles in a row and goes to Jail!");
                    this.position = 11; // Jail position
                    this.isJailed = true;
                    break;
                }
            } else {
                doubleRollsInARow = 0;
            }

        } while (continueTurn && !this.isJailed);
    }

    public int rollDice() {
        Random rand = new Random();
        int die1 = rand.nextInt(6) + 1;
        int die2 = rand.nextInt(6) + 1;
        this.rolledDouble = (die1 == die2);

        int total = die1 + die2;
        JOptionPane.showMessageDialog(null, this.name + " rolled a " + die1 + " and a " + die2 + " (Total: " + total + ")");
        return total;
    }

    public void move(int spaces) {
        this.position = (this.position + spaces) % 40;
    }

    public void handleSpace(int currentSpace) {
        String fieldType = gbSpace.spaceType(currentSpace);

        switch (fieldType) {
            case "Go":
            case "Parking":
                break;

            case "Jail":
                if (currentSpace == 31) {
                    JOptionPane.showMessageDialog(null, this.name + " landed on Go to Jail!");
                    this.position = 11;
                    this.isJailed = true;
                }
                break;

            case "Chance":
                ChanceCard drawnCard = gbSpace.drawChanceCard(this);
                JOptionPane.showMessageDialog(null, this.name + " drew a Chance card: " + drawnCard.getDescription());
                drawnCard.applyEffect(this, gbSpace);
                break;

            case "Chest":
                // You can add logic here to handle Community Chest cards
                break;

            case "Tax":
                if (this.position == 5) {
                    updateMoney(gbSpace.payIncomeTax(this, 0)); // Always pay $200
                } else {
                    updateMoney(gbSpace.payLuxuryTax(this));
                }
                break;

            default: // Property
                gbSpace.purchaseProperty(this, currentSpace, this.globalDiceRoll);
                break;
        }
    }

    public void jailEscape() {
        if (this.money >= 50) {
            JOptionPane.showMessageDialog(null, this.name + " paid $50 to escape jail.");
            updateMoney(-50);
            this.isJailed = false;
            playerTurn(); // Resume normal turn after jail escape
        } else {
            JOptionPane.showMessageDialog(null, this.name + " cannot afford to leave jail and stays there.");
        }
    }

    public boolean getRolledDouble() {
        return this.rolledDouble;
    }
}
