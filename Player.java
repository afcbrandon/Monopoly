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
    private boolean diceDouble;
    private int diceDoubleCounter;

    private GameBoardSpaces gBoardSpaces;

    public Player(String name, GameBoardSpaces gBoardSpaces) {
        this.name = name;
        this.money = 1500;
        this.position = 1; // Starting at position 1 (Go space)
        this.isEliminated = false;
        this.isJailed = false;
        this.jailCounter = 0;
        this.gBoardSpaces = new GameBoardSpaces();
        this.diceDouble = false;
        this.diceDoubleCounter = 0;
    }

    /*  ###############
        ### Getters ###
        ###############  */

    // Function that returns the player's name
    public String getName() {
        return this.name;
    }
    // function that returns the player's current money
    public int getMoney() {
        return this.money;
    }
    // Function that returns the player's position on the board
    public int getPosition() {
        return this.position;
    }
    // ***BRANDON*** Function that sets the player token
    public void setToken(Character token) {
        this.playerToken = token;
    }
    // ***BRANDON*** Function the returns the player token
    public Character getToken() {
        return this.playerToken;
    }
    // Function that returns the boolean value of diceDouble
    public boolean getDiceDouble() {
        return this.diceDouble;
    }

    /*  ###############
        ### Setters ###
        ###############  */
    public void setMoney(int money) {
        this.money = money;
    }

    public boolean isEliminated() {
        return isEliminated;
    }

    /* Function that moves the player's token along the board */
    public void moveSpaces(int rolledAmount) {
        position = (position + rolledAmount) % 40; // Monopoly board has 40 spaces
        if (position == 0) {    // 40 % 40 = 0, but 40th space is valid
            this.position = 40;
        }
    }

    /* Function that update's the player's available money */
    public void updateMoney(int amount) {
        this.money += amount;
        if (this.money < 1) {
            this.isEliminated = true; // Mark player as eliminated
        }
    }

    /*  #############################
        ### Functions for Player ###
        ############################  */
    
    /* Function that is called at the start of a player's turn */
    public void playerTurn() {
        // TODO: Check to see if this section of code is even necessary
        if (isEliminated) {
            JOptionPane.showMessageDialog(null, this.name + " is eliminated and cannot roll.");
            return;
        }

        // Player has option to leave jail, if jailed
        if (this.isJailed) {
            getOutOfJail();
        } else {
            playerRoll();
        }

        // TODO: Add function that determines what player does dependent on the space they land on
        //boardSpace();

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

    /*  ############################################
        ### Functions that handle player in jail ###
        ############################################  */

    // ability for the player to get out of jail
    private void getOutOfJail() {
        // TODO: Implement a JOptionPane that asks the user if they would like to roll double, pay a fine, or use a card
        // **** CODE **** //

        String userOption = ""; // *** Dummy Code to remove errors ***

        // Player selects option to roll doubles
        if (userOption.equals("A")) {
            int diceOne = diceRoll();
            int diceTwo = diceRoll();
            int result = diceOne + diceTwo;

            /* If the player successfully rolls double, then the player leaves jail, and the jail counter resets. */
            if (diceOne == diceTwo) {
                this.isJailed = false;
                moveSpaces(result);
                this.jailCounter = 0;
            }
            else {  /* If the player fails to roll doubles, then the jailCounter increments by 1. */
                this.jailCounter += 1;
            }

            if (jailCounter == 3) { // Player has failed to roll a double after 3 turns. They must pay $50 and move the amount of spaces they rolled
                updateMoney(-50);
                
                if (this.money <= 0) { // Player has gone bankrupt and they are eliminated
                    this.isEliminated = true;
                } else {    // Player leaves jail and moves the amount they previously rolled
                    this.isJailed = false;
                    this.jailCounter = 0;
                    moveSpaces(result);
                }
            }
        }

        // Player chooses to pay $50 fine
        if (userOption.equals("B")) {
            updateMoney(-50);
        }

        // TODO: Implement a 'Get Out of Jail card'
        // **** CODE **** //
    }
  
    /*  ##########################
        ### Functions for Dice ###
        ##########################  */

    /* Function that rolls a six-sided-die and returns its value */
    private int diceRoll() {
        return new Random().nextInt(6) + 1;
    }

    /* Function where player rolls the two dice */
    private void playerRoll() {
        int previousPosition = this.position;
        int diceOne = diceRoll();
        int diceTwo = diceRoll();
        int rollResult = diceOne + diceTwo;

        JOptionPane.showMessageDialog(null, this.name + " rolled a "  + diceOne + " and a " + diceTwo + 
                " summing up for a total of " + rollResult, "Dice Roll", JOptionPane.INFORMATION_MESSAGE);
        moveSpaces(rollResult);

        checkPassedGo(previousPosition);

        // **ALEX**This will check if a player landed on a property and pay rent if needed(hopefully have not tested it yet)
        /*
        if(gameBoardSpaces.isProperty(this.position)) {
            gameBoardSpaces.payRent(this, this.position); //pay rent to the property owner
        } */

        if (diceOne == diceTwo) {
            this.diceDouble = true;
            this.diceDoubleCounter += 1;
        }
        else {
            this.diceDoubleCounter = 0;
            this.diceDouble = false;
        }

        if (this.diceDoubleCounter == 3) {
            JOptionPane.showMessageDialog(null, 
                this.name + " has rolled doubles 3 times in a row! GO TO JAIL!");
            this.isJailed = true;
            this.position = 10;
        }
    }
}
