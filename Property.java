public class Property {
    private String name;
    private String street;
    private int price;
    private int rent;
    private Player owner;

    // Constructor used by spaces with no street color "Railroads" and utilities
    public Property(String name, int price, int rent) {
        this.name = name;
        this.price = price;
        this.rent = rent;
        this.street = null;    // Properties do not have a street
    }

    // Constructor used by spaces with properties and streets
    public Property(String name, String streetColor, int price, int rent) {
        this.name = name;
        this.street = streetColor;
        this.price = price;
        this.rent = rent;
        this.owner = null; // every property starts with no owner
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
        return street;
    }


    /*  ###############
        ### Setters ###
        ###############  */

    /// Function that sets the owner
    public void setOwner(Player owner) {
        this.owner = owner;
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

}