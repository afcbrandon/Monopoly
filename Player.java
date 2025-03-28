import javax.swing.*;
import java.util.Random;
import java.util.ArrayList;

public class Player {
    private final String name;
    private int money;
    private int position;
    private boolean isEliminated;
    private char playerToken;
    private boolean isJailed;
    private int jailCounter;
    private int globalDiceRoll;
    private boolean diceDouble;
    private int diceDoubleCounter;
    private ArrayList<Property> ownedProperties = new ArrayList<>();

    private GameBoardSpaces gbSpace;

    public Player(String name, GameBoardSpaces gBoardSpaces) {
        this.name = name;
        this.money = 1500;
        this.position = 1; // Starting at position 1 (Go space)
        this.isEliminated = false;
        this.isJailed = false;
        this.jailCounter = 0;
        this.gbSpace = gBoardSpaces; 
        this.diceDouble = false;
        this.diceDoubleCounter = 0;
        this.globalDiceRoll = 0;
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

    ///  Function that automatically eliminates player, if they player should quit
    public void setElimination() {
        this.money = 0;
        this.isEliminated = true;
    }
    /// Function that sets the player's money
    public void setMoney(int money) {
        this.money = money;
    }

    /// Function that update's the player's available money 
    public void updateMoney(int amount) {
        this.money += amount;
        if (this.money < 1) {
            this.isEliminated = true; // Mark player as eliminated
        }
    }

    public boolean getIsEliminated() {
        return isEliminated;
    }

    /* Function that moves the player's token along the board */
    public void moveSpaces(int rolledAmount) {
        position = (position + rolledAmount) % 40; // Monopoly board has 40 spaces
        if (position == 0) {    // 40 % 40 = 0, but 40th space is valid
            this.position = 40;
        }
    }

    public void addProperty(Property p) {
        ownedProperties.add(p);
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
            handleLandingOnSpace(this.position); // This function will check and handle property purchases
        }
    }

    // Function that handles action, dependent on the player's current space.
    public void handleLandingOnSpace(int currentSpace) {
        
        //  Board checks to see what type of space the player is currently on
        String fieldType = gbSpace.spaceType(currentSpace);

        if (fieldType.equals("Go") || fieldType.equals("Parking")) {        //  Space is either Go or Free Parking      

        }
        else if (fieldType.equals("Jail")) {        //  Space is a Jail Space

        }
        else if (fieldType.equals("Chance")) {      //  Chance Card Space

        }
        else if (fieldType.equals("Chest")) {       //  Chest Card Space

        }
        else if (fieldType.equals("Tax")) {     //  Tax Space
            
            if (this.position == 5) {         //  Income Tax
                String[] options = { "Pay $200", "Pay 10% of income" };
                var selection = JOptionPane.showOptionDialog(null, "How would " + this.name + 
                    " like to pay the     income tax?", "Income Tax", 0, 1,
                     null, options, options[0]);

                gbSpace.payIncomeTax(this, selection);
            }
            else {      //  Luxury Tax
                gbSpace.payLuxuryTax(this);
            }
        }
        else {              //  Property Space

            gbSpace.purchaseProperty(this, currentSpace, this.globalDiceRoll);

            /*
            Property property = gbSpace.getPropertyBySpace(this.position);

            if (property != null && property.getOwner() == null) {
                int option = JOptionPane.showConfirmDialog(null,
                        this.name + ", do you want to purchase " + property.getName() + " for $" + property.getPrice() + "?",
                        "Buy Property " + property.getName(), JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.YES_OPTION) {
                    if (this.money >= property.getPrice()) {
                        this.updateMoney(-property.getPrice());
                        property.setOwner(this);
                        this.ownedProperties.add(property);
                        JOptionPane.showMessageDialog(null, this.name + " bought " + property.getName() + "!");
                    }else {
                        JOptionPane.showMessageDialog(null, "You don't have enough money to buy " + property.getName() + "!");
                    }
                }

            } 
            else if (property != null) {
                JOptionPane.showMessageDialog(null, "This property is already owned by " 
                    + property.getOwner().getName() + ".");
            }
                    */

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
        this.globalDiceRoll = rollResult;

        JOptionPane.showMessageDialog(null, this.name + " rolled a "  + diceOne + " and a " + diceTwo + 
                " summing up for a total of " + rollResult, "Dice Roll", JOptionPane.INFORMATION_MESSAGE);
        moveSpaces(rollResult);

        checkPassedGo(previousPosition);

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
            this.diceDouble = false;
            this.position = 10;
        }
    }

    public int countUtilities() {
        int count = 0;
        for (Property p : ownedProperties) {
            String name = p.getName();
            if (name.equals("Electric Company") || name.equals("Water Works")) {
                count++;
            }
        }
        return count;
    }
    public int countRailroads() {
        int count = 0;
        for (Property p : ownedProperties) {
            String name = p.getName();
            if (name.equals("Reading Railroad") || name.equals("Pennsylvania Railroad") ||
                name.equals("B & O Railroad") || name.equals("Short Line")) {
                count++;
            }
        }
        return count;
    }
    
}
