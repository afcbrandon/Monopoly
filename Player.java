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
    private boolean rolledDouble;
    private int rolledDoubleCounter;
    private boolean hasOutOfJailCard = false;
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
        this.rolledDouble = false;
        this.rolledDoubleCounter = 0;
        this.globalDiceRoll = 0;
    }

    /*  ###############
        ### Getters ###
        ###############  */

    // Function that returns the player's name
    public String getPlayerName() {
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
    // Function that returns the boolean value of rolledDouble, which is used to determine if player can roll again
    public boolean getRolledDouble() {
        return this.rolledDouble;
    }
    public boolean getIsJailed() {
        return this.isJailed;
    }
    public boolean hasOutOfJailCard() {
        return this.hasOutOfJailCard;
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
    public void setPosition(int position) {
        this.position = position;
    }

    public boolean getIsEliminated() {
        return isEliminated;
    }

    /* Function that moves the player's token along the board */
    public void moveSpaces(int rolledAmount) {
        this.position = (position + rolledAmount) % 40; // Monopoly board has 40 spaces
        if (this.position == 0) {    // 40 % 40 = 0, but 40th space is valid
            this.position = 40;
        }
    }

    public void addProperty(Property p) {
        ownedProperties.add(p);
    }
    public ArrayList<Property> getOwnedProperties() {
        return ownedProperties;
    }
    public void setJailed(boolean jailed) {
        this.isJailed = jailed;
    }
    public void setOutOfJailCard(boolean hasCard){
        this.hasOutOfJailCard = hasCard;
    }


    public void transferAssetsTo(Player receiver) {
        // Transfer remaining money
        receiver.updateMoney(this.money);
        this.money = 0;

        // Transfer all owned properties
        for (Property p : new ArrayList<>(ownedProperties)) {
            receiver.addProperty(p);
            p.setOwner(receiver);
        }
        ownedProperties.clear();

        // Mark this player as eliminated
        this.setElimination();

        JOptionPane.showMessageDialog(null,
                this.name + " has gone bankrupt and transferred all assets to " + receiver.getPlayerName());
    }
    public void surrenderAssetsToBank() {

        this.money = 0;


        for (Property p : new ArrayList<>(ownedProperties)) {
            p.setOwner(null);
        }

        ownedProperties.clear(); // Clear list

        
        this.setElimination();

        JOptionPane.showMessageDialog(null,
                this.name + " has gone bankrupt and surrendered all assets to the bank.");
    }

    // Will: leaving commented out. supposed to allow the player to decide which property to mortgage but got stuck trying to update
    // mortgaged from false to true
   /* public void mortgageProperties() {
        while(!ownedProperties.isEmpty() && getMoney() < 1){
            String[] options = new String[ownedProperties.size()];
            for (int i = 0; i < ownedProperties.size(); i++){
                Property p = ownedProperties.get(i);
                options[i] = p.getName() + " ($" + (p.getMortgageValue() + ")");
            }

            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Select which property you would like to mortgage",
                    "Mortgage Property",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice >= 0){
                Property selected = ownedProperties.add(choice);

                updateMoney(selected.getMortgageValue());
            }
        }
    } */

    public void sellAssetsToBankInteractive() {
        while (!ownedProperties.isEmpty()) {
            String[] options = new String[ownedProperties.size()];
            for (int i = 0; i < ownedProperties.size(); i++) {
                Property p = ownedProperties.get(i);
                options[i] = p.getName() + " ($" + (p.getPrice() / 2) + ")";
            }

            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Select a property to sell (for half value):",
                    "Sell Property",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice >= 0) {
                Property selected = ownedProperties.remove(choice);
                updateMoney(selected.getPrice() / 2);
                selected.setOwner(null);
                JOptionPane.showMessageDialog(null, selected.getName() + " sold for $" + (selected.getPrice() / 2));
            } else {
                break; // Cancel or close
            }

            int continueSelling = JOptionPane.showConfirmDialog(null, "Sell another property?", "Continue?", JOptionPane.YES_NO_OPTION);
            if (continueSelling != JOptionPane.YES_OPTION) {
                break;
            }
        }

        if (ownedProperties.isEmpty()) {
            JOptionPane.showMessageDialog(null, "You have no more properties to sell.");
        }
    }




    /*  #############################
        ### Functions for Player ###
        ############################  */
    
    /* Function that is called at the start of a player's turn */
    public void playerTurn() {
      
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
            if (currentSpace == 31) {   // Go to Jail
                JOptionPane.showMessageDialog(null, "Go to Jail!");
                this.isJailed = true;
                this.position = 11;
            }

            // If player landed on space 11, then they are most likely just visiting.

        }
        else if (fieldType.equals("Chance")) {      //  Chance Card Space
            ChanceCard drawnCard = gbSpace.drawChanceCard(this); // Get a random Chance card
            JOptionPane.showMessageDialog(null, this.name + " drew a Chance card: " + drawnCard.getDescription());
            drawnCard.applyEffect(this, gbSpace); // Apply the effect
        }
        else if (fieldType.equals("Chest")) {       //  Chest Card Space

        }
        else if (fieldType.equals("Tax")) {     //  Tax Space

            if (this.position == 5) {         //  Income Tax
                String[] options = { "Pay $200", "Pay 10% of income" };
                var selection = JOptionPane.showOptionDialog(null, "How would " + this.name +
                    " like to pay the income tax?", "Income Tax", 0, 1,
                     null, options, options[0]);

                updateMoney(gbSpace.payIncomeTax(this, selection));
            }
            else {      //  Luxury Tax
                updateMoney(gbSpace.payLuxuryTax(this));
            }
        }
        else {              //  Property Space

            gbSpace.purchaseProperty(this, currentSpace, this.globalDiceRoll);

        }

    }

    // will get how may color properties there are in total
    public int getTotalPropertiesInSet(String color){
        switch (color) {
            case "Brown": return 2;
            case "Blue": return 2;
            case "Pink":
            case "Orange":
            case "Red":
            case "Yellow":
            case "Green": return 3;
            case "Dark Blue": return 2;
            default: return 0; // No color set
        }
    }

    public boolean ownsFullSet(String color) {
        int count = 0;
        int totalNeeded = getTotalPropertiesInSet(color);//again this gets total count

        for (Property property : ownedProperties) {
            if (property.getStreetColor() != null && property.getStreetColor().equals(color)) {
                count++;
            }
        }
        return count == totalNeeded;
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
        String options[] = {"Roll Dice", "Pay $50", "Get Out Of Jail Free"};
        var userOption = JOptionPane.showOptionDialog(null, "", "Jail", 0, 1, null, options, options[0]);

        // Player selects option to roll doubles
        if (userOption == 0) {
            int diceOne = diceRoll();
            int diceTwo = diceRoll();
            int result = diceOne + diceTwo;

            /* If the player successfully rolls double, then the player leaves jail, and the jail counter resets. */
            if (diceOne == diceTwo) {
                JOptionPane.showMessageDialog(null, 
                    this.name + " rolled a " + diceOne + " and " + diceTwo + "!. Move " + result + " spaces.");
                this.isJailed = false;
                moveSpaces(result);
                this.jailCounter = 0;
            }
            else {  /* If the player fails to roll doubles, then the jailCounter increments by 1. */
                JOptionPane.showMessageDialog(null, this.name + " failed to roll doubles. Stay in Jail for 1 more turn.");
                this.jailCounter += 1;
            }

            if (jailCounter == 3) { // Player has failed to roll a double after 3 turns. They must pay $50 and move the amount of spaces they rolled
                
                if ((this.money - 50) <= 0) { // Player has gone bankrupt and they are eliminated
                    JOptionPane.showMessageDialog(null, this.name + " has paid the $50 fine and has gone bankrupt. They are eliminated.");
                    this.isEliminated = true;
                } else {    // Player leaves jail and moves the amount they previously rolled
                    JOptionPane.showMessageDialog(null, 
                        this.name + " failed to roll doubles for 3 turns in a row. Pay $50 and move " + result + " spaces.");
                    updateMoney(-50);
                    this.isJailed = false;
                    this.jailCounter = 0;
                    moveSpaces(result);
                }
            }
        }
        // Player chooses to pay $50 fine
        else if (userOption == 1) {
            JOptionPane.showMessageDialog(null, this.name + " paid $50 to escape jail.");
            updateMoney(-50);
            this.isJailed = false;
            playerRoll();
        }
        else if (userOption == 2) {
            if (this.hasOutOfJailCard()) { // Check if the player has the "Get Out of Jail Free" card
                JOptionPane.showMessageDialog(null, this.name + " used their 'Get Out of Jail Free' card!");
                this.isJailed = false;  // The player is no longer in jail
                this.removeOutOfJailCard();  // Remove the card from the player's inventory
                playerRoll(); // Roll the dice to continue the game
            } else {
                JOptionPane.showMessageDialog(null, "You don't have a 'Get Out of Jail Free' card.");
            }
        }
    }
  private void removeOutOfJailCard() {
        this.hasOutOfJailCard = false;
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
            this.rolledDouble = true;
            this.rolledDoubleCounter += 1;
        }
        else {
            this.rolledDoubleCounter = 0;
            this.rolledDouble = false;
        }

        if (this.rolledDoubleCounter == 3) {
            JOptionPane.showMessageDialog(null,
                this.name + " has rolled doubles 3 times in a row! GO TO JAIL!");
            this.isJailed = true;
            this.rolledDouble = false;      // Reset the double counter for next time
            this.position = 11;     // Jail Position
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
