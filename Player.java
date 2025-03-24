import javax.swing.*;
import java.util.Random;

public class Player {
    private final String name;
    private int money;
    private int position;
    private boolean isEliminated;
    private char playerToken;
    private boolean isJailed;
    private int jailCounter;

    public Player(String name) {
        this.name = name;
        this.money = 1500;
        this.position = 1; // Starting at position 1 (Go space)
        this.isEliminated = false;
        this.isJailed = false;
        this.jailCounter = 0;
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

    public void moveSpaces(int spaces) {
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
    
    /* Function that is called at the start of a player's turn */
    public void playerTurn() {
        if (isEliminated) {
            JOptionPane.showMessageDialog(null, this.name + " is eliminated and cannot roll.");
            return;
        }

        // Player has option to leave jail, if jailed
        if (isJailed) {
            getOutOfJail();
        } else {
            rollDice();
        }

    }

    //created checkPassedGo function
    private void checkPassedGo(int previousPosition) {
        // Check if player passed Go or landed on GO and awards 200 bucks
        if (this.position < previousPosition) {
            this.updateMoney(200);
            JOptionPane.showMessageDialog(null, this.name + " passed Go and earned $200!");
        } else if (this.position == 1) {
            this.updateMoney(200);
            JOptionPane.showMessageDialog(null, this.name + " landed on Go and earned $200!");
        }
    }

    // ability for the player to get out of jail
    private void getOutOfJail() {
        // TODO: Implement a JOptionPane that asks the user if they would like to roll double, pay a fine, or use a card
        // **** CODE **** //

        String userOption = ""; // *** Dummy Code to remove errors ***

        // Player selects option to roll doubles
        if (userOption.equals("A")) {
            Random rand = new Random();

            int diceOne = rand.nextInt(6) + 1;
            int diceTwo = rand.nextInt(6) + 1;
            int result = diceOne + diceTwo;

            if (diceOne == diceTwo) {
                this.isJailed = false;
                moveSpaces(result);

            }
            else {
                this.jailCounter += 1;  // increment jail counter, meaning that the player failed to roll doubles
            }
        }

        // Player chooses to pay $50 fine
        if (userOption.equals("B")) {
            updateMoney(-50);
        }

        // TODO: Implement a 'Get Out of Jail card'
        // **** CODE **** //
    }

    // Function that allows user to roll dice.
    private void rollDice() {

        // variables that check to see if user has rolled doubles. 
        boolean rolledDoubles;
        int doubleCounter = 0;

        do {
            rolledDoubles = false;  // boolean to check if doubles are rolled is set to false at the start of each loop
            Random rand = new Random();

            int diceOne = rand.nextInt(6) + 1;
            int diceTwo = rand.nextInt(6) + 1;
            int result = diceOne + diceTwo;
            JOptionPane.showMessageDialog(null, this.name + " rolled a "  + diceOne + " and a " + diceTwo + 
                " summing up for a total of " + result, "Dice Roll", JOptionPane.INFORMATION_MESSAGE);
            int previousPosition = this.position;
            this.moveSpaces(result);

            if (diceOne == diceTwo) {
                rolledDoubles = true;
                doubleCounter += 1;
            }

            // If statement that increments the rolled double counter. If player rolls double 3 times, then the player is sent to jail.
            if (doubleCounter == 3) {
                this.isJailed = true;
                // TODO: Create a function that updated the player's position to the jail space
                break;
            }

            if (!isJailed) {    // Check if player has passed go only if player has not been jailed. Player in jail cannot collect GO money
                checkPassedGo(previousPosition); // added this into the roll and move
            }

        } while (rolledDoubles);
    }
    
}
