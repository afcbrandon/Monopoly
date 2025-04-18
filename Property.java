import java.util.HashMap;

public class Property {
    private String name;
    private String streetColor;
    private int price;
    private int rent;
    private int numHouses;
    private int numHotels;
    private int costHouses;
    private int costHotels;
    private int mortgageValue;
    private Player owner;
    private HashMap<String, Integer> totalHouses = new HashMap<>();
    private HashMap<String, Integer> totalHotels = new HashMap<>();
    private boolean mortgaged;


    // Constructor used by spaces with no street color "Railroads" and utilities
    public Property(String name, int price, int rent) {
        this.name = name;
        this.price = price;
        this.rent = rent;
        this.streetColor = null;    // Properties do not have a street
    }

    // Constructor used by spaces with properties and streets
    public Property(String name, String streetColor, int price, int rent, int costHouses, int costHotels, int mortgageValue, boolean mortgaged) {
        this.name = name;
        this.streetColor = streetColor;
        this.price = price;
        this.rent = rent;
        this.numHouses = 0;
        this.numHotels = 0;
        this.costHouses = costHouses;
        this.costHotels = costHotels;
        this.mortgageValue = mortgageValue;
        this.mortgaged = false;
        this.owner = null; // every property starts with no owner
        this.totalHouses.put(name, numHouses);
        this.totalHotels.put(name, numHotels);
    }

    /*  ###############
        ### Getters ###
        ###############  */
    
    public String getName() {
        return name;
    }
    public int getRent() {
        return rent;
    }
    public Player getOwner() {
        return owner;
    }
    public int getPrice() {
        return price;
    }
    public String getStreetColor() {
        return streetColor;
    }
    public int getNumHouses() {
        return this.numHouses;
    }
    public int getNumHotels() {
        return this.numHotels;
    }
    public int getTotalHouses(String propertyName) {
        return totalHouses.get(propertyName);
    }
    public int getTotalHotels(String propertyName) {
        return totalHotels.get(propertyName);
    }
    public int getMortgageValue(){
        return this.mortgageValue;
    }


    /*  ###############
        ### Setters ###
        ###############  */

    /// Function that sets the owner
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    /// Setters that update that total houses build in the set
    public void setBuildHouse(String propertyName, int numHouses) {
        totalHotels.put(propertyName, totalHotels.getOrDefault(propertyName, 0) + 1);
    }
    public void setBuildHotel(String propertyName) {
        this.totalHotels.put(propertyName, 1);
    }

    /*  ##########################
        ### Property Functions ###
        ########################## */

    // Function that returns the name of the property, dependent on the space
    public boolean buyProperty(Player player) {
        if (owner != null) {
            //This property already has owner
            return false;
        }
        if (player.getMoney() >= price) {
            //The player has enough money to buy the property
            player.setMoney(player.getMoney() - price); // deducts the money
            this.owner = player;// sets the player as the owner
            return true;
        }
        return false;// They cant buy property bc they dont have enough money
    }

    // Functions that return the total properties a player owns

    //  Function that allows player to upgrade houses to hotels
    public void upgradeToHotel(String propertyName) {
        owner.updateMoney(-costHotels);
        setBuildHotel(propertyName);
        numHotels++;
    }

    public void sellHouse(Player player) {
        player.updateMoney( costHouses / 2 );   // player gets back half the value of a house
    }

    // Function that allows player to sell hotel
    public void sellHotel(Player player) {
        player.updateMoney( this.costHotels / 2 ); // player gets back half the value of the hotel
        numHotels--;
    }
}