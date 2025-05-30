import javax.swing.*;
import javax.xml.crypto.KeySelector.Purpose;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class Player {
    protected final String name;
    protected int money;
    protected int position;
    private boolean isEliminated;
    private char playerToken;
    protected boolean isJailed;
    private int jailCounter;
    protected int globalDiceRoll;
    protected boolean rolledDouble;
    protected int rolledDoubleCounter;
    private boolean hasOutOfJailCard = false;
    private ArrayList<Property> ownedProperties;
    private HashMap<String, Integer> totalColorSetProperties;     // Variable that holds the colors that a player owns, and the amount of properties in that color
    private HashMap<String, Integer> ownedFullColorSets;            // Variable that holds the total amount of FULL colorsets that the player owns
    private HashMap<String, List<String>> colorsetPropertyNames;
    private boolean isBot = false;

    protected GameBoardSpaces gbSpace;

    //Constructor for both humans and bots
    public Player(String name, boolean isBot) {
        this.name = name;
        this.money = 1500;
        this.position = 1; // Starting at position 1 (Go space)
        this.isEliminated = false;
        this.isJailed = false;
        this.jailCounter = 0;
        this.rolledDouble = false;
        this.rolledDoubleCounter = 0;
        this.globalDiceRoll = 0;
        this.ownedProperties = new ArrayList<>();
        this.totalColorSetProperties = new HashMap<String, Integer>();
        this.ownedFullColorSets = new HashMap<String, Integer>();
        this.colorsetPropertyNames = new HashMap<String, List<String>>();
        this.isBot = isBot;
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
    
    /*  Function that returns whether a player is in jail */
    public boolean getIsJailed() {
        return this.isJailed;
    }
    
    /*  Function that returns whether a player has a Get Out Of Jail card */
    public boolean hasOutOfJailCard() {
        return this.hasOutOfJailCard;
    }
    
    // Funciton that returns the jailCounter of a player
    public int getJailCounter() {
        return this.jailCounter;
    }

    /*  Function that checks to see how many full colorsets the player owns. If the hashmap returned is empty, then player does not own any full colorsets */
    public HashMap<String, Integer> getFullColorsets () {
        HashMap<String, Integer> checkedStreets = new HashMap<>();

        for ( String streetColor : totalColorSetProperties.keySet() ) {
            int propertiesOwned = totalColorSetProperties.get(streetColor);
            if ( checkFullColorSet(streetColor, propertiesOwned) == true ) {
                checkedStreets.put(streetColor, propertiesOwned);
            }
        }

        return checkedStreets;
    }

    // Function that gets the available properties of a colorset
    public HashMap<String, List<String>> getListOfColorSetPropertyNames(Player player, String colorSet) {
        return colorsetPropertyNames;
    }

    // Returns the number of houses in a property
    public int getPropertyHouseNum(String propName) {
        int count = 0;

        for ( Property p : ownedProperties ) {
            if ( p.getName().equals(propName) ) count = p.getNumHouses();
        }

        return count;
    }

    // Returns the number of hotels in a property
    public int getPropertyHotelNum(String propName) {
        int count = 0;

        for ( Property p : ownedProperties ) {
            if ( p.getName().equals(propName) ) count = p.getNumHotels();
        }

        return count;
    }
    

    // Returns a hashmap of the Properties of a colorset and the amount of Houses it has

    /*  ###############
        ### Setters ###
        ###############  */
    public void setGBoardSpaces(GameBoardSpaces gBoardSpaces) {
        this.gbSpace = gBoardSpaces;
    }

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

    public ArrayList<Property> getOwnedProperties() {
        return ownedProperties;
    }

    public void setJailed(boolean jailed) {
        this.isJailed = jailed;
    }

    public void setOutOfJailCard(boolean hasCard){
        this.hasOutOfJailCard = hasCard;
    }
    
    public void setJailCounter(int jailCounter) {
        this.jailCounter = jailCounter;

        if ( this.jailCounter <= 0 ) {  // if jailCounter is set to 0 via debug, then player is no longer in jail
            this.isJailed = false;
        }
    }

    /*  #############################################################################################
        ### Functions for Player  when they have run out of money and must surrender their assets ###
        #############################################################################################  */

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

    // Function that surrenders assets to the bank if player is in debt to bank
    public void surrenderAssetsToBank() {

        int totalValueOfImprovementsReturned = 0;
        if ( gbSpace != null && gbSpace.getBank() != null ) {
            Bank bank = gbSpace.getBank();
            for ( Property p : new ArrayList<>(ownedProperties) ) {
                p.clearPropertyAndReturnToBank(bank);
                p.setOwner(null);   // Property is now unowned
            }
        } else {    // This code is merely for good practice to catch an error. It should never run
            for ( Property p : new ArrayList<>(ownedProperties) ) {
                p.setNumHotels(0);
                p.setNumHouses(0);
                p.updateRentToOriginalRent();
                p.setOwner(null);
            }
        }

        this.money = 0; // Player has no more money
        ownedProperties.clear();
        totalColorSetProperties.clear();
        ownedFullColorSets.clear();
        colorsetPropertyNames.clear();

        this.setElimination();
        JOptionPane.showMessageDialog(null,
                this.name + " has gone bankrupt and surrendered all their assets to the bank.");
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

    /*  #############################
        ### Functions for Selling ###
        #############################  */

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

    // Function that removes property from this player
    public void removeProperty( Property propertyToSell ) {
        if ( ownedProperties.remove(propertyToSell) ) {
            System.out.println(this.name + " is removing property: " + propertyToSell.getName());
            if (propertyToSell.getStreetColor() != null && !propertyToSell.getStreetColor().isEmpty()) {
                // updateColorsetSell handles decrementing counts in totalColorSetProperties
                // and removing property name from colorsetPropertyNames list.
                updateColorsetSell(propertyToSell.getStreetColor(), propertyToSell.getName());
            }
        } else {
            System.out.println("Warning: Attempted to remove property '" + propertyToSell.getName() + "' not found in " + this.name + "'s portfolio.");
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
    protected void handleLandingOnSpace(int currentSpace) {

        //  Board checks to see what type of space the player is currently on
        String fieldType = gbSpace.spaceType(currentSpace);

        if (fieldType.equals("Go") || fieldType.equals("Parking")) {        //  Space is either Go or Free Parking
            if ( fieldType.equals("Go")) {

            }
        }
        else if (fieldType.equals("Jail")) {        //  Space is a Jail Space
            if (currentSpace == 31) {   // Go to Jail
                JOptionPane.showMessageDialog(null, "Go to Jail!");
                this.isJailed = true;
                this.position = 11;
            }
        }
        else if (fieldType.equals("Chance")) {      //  Chance Card Space
            ChanceCard drawnCard = gbSpace.drawChanceCard(this); // Get a random Chance card

            drawnCard.applyEffect(this, gbSpace); // Apply the effect
        }
        else if (fieldType.equals("Chest")) {       //  Chest Card Space
            CommunityChestCard drawnCommunityCard = gbSpace.drawCommunityChestCard(this);
            drawnCommunityCard.applyEffect(this, gbSpace);
        }
        else if (fieldType.equals("Tax")) {     //  Tax Space

            if (this.position == 5) {         //  Income Tax.
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

    // Function that resets the rolledDouble flag
    public void resetRolledDouble() {
        this.rolledDouble = false;
        // Keep rolledDoubleCounter as is, it resets naturally when a non-double is rolled or jail happens.
    }

    public void resetRolledDoubleCounter() {
        this.rolledDoubleCounter = 0;
    }


    /*  ##############################
        ### Functions for ColorSet ###
        ##############################  */

    // **************************************** COLORSET FUNCTIONS

    /*  Function that adds properties to ownedProperties and totalColorSetProperties */
    public void addProperty(Property boughtProperty) {
        ownedProperties.add(boughtProperty);

        if ( boughtProperty.getStreetColor() == null || boughtProperty.getStreetColor().isEmpty() ) {
            System.out.println("Property has no street color. Not added to a colorset");
            return;
        }

        for (Property p : ownedProperties) {
            if ( p.getName().equals(boughtProperty.getName()) ) {
                updateColorsetBuy( p.getStreetColor(), p.getName() );

                System.out.println(this.name + " added " + p.getName() + " to their colorset");
                System.out.println( "Total colorset properties for " + this.name );
                System.out.println(totalColorSetProperties);
            }
        }

    }

    /*  Function that handles selling property to another player */
    public void sellProperty(Property soldProperty, Player newOwner) {
        
        updateColorsetSell( soldProperty.getStreetColor(), soldProperty.getName() ); // property is removed from current player's HashMap totalColorSetProperties
        ownedProperties.remove(soldProperty);   // Remove property from ArrayList ownedProperties
        newOwner.addProperty(soldProperty); // property is added to newOwner
    }

    /*  Function that increments the amount of properties in a colorset when player buys a property */
    private void updateColorsetBuy(String streetColor, String streetName) {

        // Increment number of properties on a given Color Set
        if ( totalColorSetProperties.containsKey(streetColor) ) {
            totalColorSetProperties.put( streetColor, totalColorSetProperties.get(streetColor) + 1 );
        }
        else {
            totalColorSetProperties.put( streetColor, 1 );
        }

        // adds to colorsetPropertyNames { key: streetColor   value: streetName }
        this.colorsetPropertyNames.computeIfAbsent(streetColor, _ -> new ArrayList<>()).add(streetName);
        
    }

    /*  Function that decrements the amount of properties on a colorset when player sells a property */
    private void updateColorsetSell(String streetColor, String streetName) {

        totalColorSetProperties.put(streetColor, totalColorSetProperties.get(streetColor) - 1);

        if (totalColorSetProperties.get(streetColor) == 0) {
            totalColorSetProperties.remove(streetColor);
        }

        //  removes from colorsetPropertyNames { key: streetColor   value: streetName }
        colorsetPropertyNames.get(streetColor).remove(streetName);

    }

    /*  Function that checks to see if player owns the full colorset in a Street Color. Returns false if they do not */
    public boolean checkFullColorSet(String streetColor, Integer totalPropertiesOwned) {

        switch (streetColor) {
            case "Light Blue":
            case "Pink":
            case "Orange":
            case "Red":
            case "Yellow":
            case "Green":
                if ( totalColorSetProperties.get(streetColor) == 3 ) 
                return true; 
                break;
            case "Purple":
            case "Dark Blue":
                if ( totalColorSetProperties.get(streetColor) == 2 ) 
                return true; 
                break;
            default:    // Do Nothing
        }

        return false;
    }

    // Returns a boolean if player owns the full colorses. True if they own fullcolorset, false if they do not
    public boolean ownsFullColorSet(String streetColor) {

        if ( streetColor == null || streetColor.isEmpty() || !totalColorSetProperties.containsKey(streetColor) ) {
            return false;
        } 

        int propertiesOwned = totalColorSetProperties.get(streetColor);
        int requiredProperties;

        switch (streetColor) {
            case "Light Blue":
            case "Pink":
            case "Orange":
            case "Red":
            case "Yellow":
            case "Green":
                requiredProperties = 3;
                break;
            case "Purple":
            case "Dark Blue":
                requiredProperties = 2;
                break;
            default:
                return false;   //  Not a buildable color set
        }

        return propertiesOwned == requiredProperties;
    }

    // Function that determines if the player can currently build on a specific property within a full color set they own.
    public boolean canBuildOnProperty(Property property, GameBoardSpaces boardSpaces) {

        if (property == null || property.getOwner() != this || property.getStreetColor() == null) {
            return false; // Not owned or not a colored property
        }

        String color = property.getStreetColor();
        if (!ownsFullColorSet(color)) {
            return false; // Don't own the full set
        }

        // Check property limits
        if (property.getNumHotels() > 0) {
            return false; // Already has a hotel
        }

        // Check Bank inventory
        Bank bank = boardSpaces.getBank();
        boolean needsHouse = property.getNumHouses() < 4;
        boolean needsHotel = property.getNumHouses() == 4;

        if (needsHotel && !bank.canDispenseHotel()) {
            return false; // Needs a hotel, but none available
        }
        if (needsHouse && !bank.canDispenseHouse()) {
            // If it needs a house (has < 4) but bank has none, cannot build house.
            // If it needs a hotel (has 4) bank check is done above.
            return false;
        }

        // Check even building rule
        int minHousesInSet = 5; // Start high (5 represents a hotel)
        int maxHousesInSet = -1; // Start low
        List<Property> propertiesInSet = new ArrayList<>();

        // Find min/max houses in the set
        for (Property p : ownedProperties) {
            if (color.equals(p.getStreetColor())) {
                propertiesInSet.add(p);
                int currentImprovements = (p.getNumHotels() > 0) ? 5 : p.getNumHouses();
                minHousesInSet = Math.min(minHousesInSet, currentImprovements);
                maxHousesInSet = Math.max(maxHousesInSet, currentImprovements);
            }
        }

        // Determine current property's improvement level
        int thisPropertyImprovements = (property.getNumHotels() > 0) ? 5 : property.getNumHouses();

        // Cannot build if this property already has the max number of improvements in the set,
        // unless all properties in the set have the same max (e.g., all have 4 houses ready for hotels).
        // More simply: Can only build on properties that currently have the minimum number of houses in the set.
        if (thisPropertyImprovements > minHousesInSet) {
            return false; // Must build on properties with fewer houses first
        }

        // If this property has the minimum number of houses, and it's less than 5 (i.e., not a hotel yet),
        // then building is allowed (subject to bank inventory check already done).
        return thisPropertyImprovements < 5;
     }

    /*  #######################################
        ### Function for checking Passed Go ###
        #######################################  */

    //created checkPassedGo function
    protected void checkPassedGo(int previousPosition) {
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
    protected void getOutOfJail() {
        // TODO: Implement a JOptionPane that asks the user if they would like to roll double, pay a fine, or use a card
        String options[] = {"Roll Dice", "Pay $50", "Get Out Of Jail Free"};
        var userOption = JOptionPane.showOptionDialog(null, "", "Jail", 0, 1, null, options, options[0]);

        // If player does not have a get out Jail Free card, then the button is disabled
        if ( hasOutOfJailCard() == false ) {
            // TODO: Code that disables button if player does not have a get out of jail card
        }

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
                this.rolledDouble = true;   // Indicate a double was rolled for this turn's action
                this.rolledDoubleCounter = 1;
                moveSpaces(result);
                this.jailCounter = 0;
                //  handleLandingOnSpace() will be called by GameG
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
            if ( this.money >= 50 ) {
                JOptionPane.showMessageDialog(null, this.name + " paid $50 to escape jail.");
                updateMoney(-50);
                this.isJailed = false;
                this.jailCounter = 0;
                this.rolledDouble = false;
                this.rolledDoubleCounter = 0;
                playerRoll();
                //  handleLandingOnSpace will be called by GameGUI
            }
        }
        // Player has Get Out of Jail Free card
        else {
            if ( hasOutOfJailCard() ) {
                JOptionPane.showMessageDialog(null, this.name + " used their 'Get Out of Jail Free' card!");
                this.isJailed = false;  // The player is no longer in jail
                this.removeOutOfJailCard();  // Remove the card from the player's inventory
                this.rolledDouble = false;
                this.rolledDoubleCounter = 0;
                playerRoll(); // Roll the dice to continue the game
                // handleLandingOnSpace() will be called by GameGUI
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
    protected int diceRoll() {
        return new Random().nextInt(6) + 1;
    }

    /* Function where player rolls the two dice */
    protected void playerRoll() {
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
