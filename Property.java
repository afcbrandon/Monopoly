public class Property {
    private String name;
    private int price;
    private int rent;
    private Player owner;
    private GameBoard gBoard;

    public Property(String name, int price, int rent) {
        this.name = name;
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

    public String propertyName(int boardSpace) {
        
        String pName;

        switch (boardSpace) {
            case 2: pName = "Mediterranean Avenue"; break;
            case 4: pName = "Baltic Avenue"; break;
            case 7: pName = "Oriental Avenue"; break;
            case 9: pName = "Vermont Avenue"; break;
            case 10: pName = "Connecticut Avenue"; break;
            case 12: pName = "St. Charles Place"; break;
            case 14: pName = "States Avenue"; break;
            case 15: pName = "Virginia Avenue"; break;
            case 17: pName = "St. James Place"; break;
            case 19: pName = "Tennesse Avenue"; break;
            case 20: pName = "New York Avenue"; break;
            case 22: pName = "Kentucky Avenue"; break;
            case 24: pName = "Indiana Avenue"; break;
            case 25: pName = "Illinois Avenue"; break;
            case 27: pName = "Atlantic Avenue"; break;
            case 28: pName = "Ventnor Avenue"; break;
            case 30: pName = "Marvin Gardens"; break;
            case 32: pName = "Pacific Avenue"; break;
            case 33: pName = "North Carolina Avenue"; break;
            case 35: pName = "Pennsylvania Avenue"; break;
            case 38: pName = "Park Place"; break;
            default: pName = "Boardwalk";
        }

        return pName;
    }

    /// Function that returns the price of the property, dependent on the space
    public int propertyPrice (int boardSpace) {
        int price;

        switch (boardSpace) {
            case 2:     //  Purple Spaces: Mediterranean Avenue, Baltic Avenue
            case 4: 
                price = 60; 
                break;
            case 7:     //  Light Blue Spaces: Oriental Avenue, Vermont Avenue, Connecticut Avenue
            case 9:
            case 10:
                price = 100;
                if (boardSpace == 10) {
                    price += 20;
                }
                break;
            case 12:    //  Pink Spaces: St. Charles Place, States Avenue, Virginia Avenue
            case 14: 
            case 15:
                price = 140;
                if (boardSpace == 15) {
                    price += 20;
                }
                break;
            case 17:    // Orange Spaces: St. James Place, Tennesse Avenue, New York Avenue
            case 19:
            case 20:
                price = 180;
                if (boardSpace == 20) {
                    price += 20;
                }
                break;
            case 22:    // Red Spaces: Kentucky Avenue, Indiana Avenue, Illinois Avenue
            case 24:
            case 25:
                price = 220;
                if (boardSpace == 25) {
                    price += 20;
                }
                break;
            case 27:    // Yellow Spaces: Atlantic Avenue, Ventnor Avenure, Marvin Gardens
            case 28:
            case 30:
                price = 260;
                if (boardSpace == 30) {
                    price += 20;
                }
                break;
            case 32:    // Green Spaces: Pacific Avenue, North Carolina Avenue, Pennsylvania Avenue
            case 33:
            case 35:
                price = 300;
                if (boardSpace == 35) {
                    price += 20;
                }
                break;
            default:    // Dark Blue Spaces: Park Place, Boardwalk
                price = 350;
                if (boardSpace == 40) {
                    price += 50;
                }
        }

        return price;
    }
}