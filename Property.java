import java.util.HashMap;

public class Property {
    private String name;
    private String streetColor;
    private int price;
    private int rent;
    private int originalRent;
    private int numHouses;
    private int numHotels;
    private int costHouses;
    private int costHotels;
    private int mortgageValue;
    private Player owner;
    private boolean isMortgaged;

    // Constructor used by spaces with no street color "Railroads" and utilities
    public Property(String name, int price, int rent, int mortgageValue) {
        this.name = name;
        this.price = price;
        this.rent = rent;
        this.originalRent = rent;
        this.mortgageValue = mortgageValue;
        this.isMortgaged = false;
    }

    // Constructor used by spaces with properties and streets
    public Property(String name, String streetColor, int price, int rent, int costHouses, int costHotels, int mortgageValue) {
        this.name = name;
        this.streetColor = streetColor;
        this.price = price;
        this.rent = rent;
        this.originalRent = rent;
        this.numHouses = 0;
        this.numHotels = 0;
        this.costHouses = costHouses;
        this.costHotels = costHotels;
        this.mortgageValue = mortgageValue;
        this.isMortgaged = false;
        this.owner = null; // every property starts with no owner
    }

    /*  ###############
        ### Getters ###
        ###############  */
    
    public String getName() {
        return this.name;
    }
    public int getRent() {
        return this.rent;
    }
    public Player getOwner() {
        return this.owner;
    }
    public int getPrice() {
        return this.price;
    }
    public String getStreetColor() {
        return this.streetColor;
    }
    public int getNumHouses() {
        return this.numHouses;
    }
    public int getNumHotels() {
        return this.numHotels;
    }
    public int getHouseCost() {
        return this.costHouses;
    }
    public int getHotelCost() {
        return this.costHotels;
    }
    public int getMortgageValue(){
        return this.mortgageValue;
    }

    /*  ###############
        ### Setters ###
        ###############  */

    /* Function that sets the owner */
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public void setNumHouses(int numHouses) {
        this.numHouses = numHouses;

        if ( this.numHotels == 0 ) {
            if ( this.numHouses == 0 ) {
                this.rent = this.originalRent;  // set rent of property back to original rent if no property is upon it
            } else {
                updateRentHouses(); // update the rent based on the amount of houses on said property
            }
        }
    }

    public void setNumHotels(int numHotels) {
        this.numHotels = numHotels;
        if ( this.numHotels > 0 ) {
            this.numHouses = 0; // Hotel replaces houses
            updateRentHotels();
        } else {
            setNumHouses(this.numHouses);   // Triggers code that updates the rent of a property depending on the number of houses left
        }
    }

    /*  ######################
        ### Bank Functions ###
        ###################### */

    // Function that removes all improvements(houses and hotels) from a property and surrenders them to the bank
    // Returns the half cash value of all the improvements returns
    public int clearPropertyAndReturnToBank(Bank bank) {
        int valueReturnToBank = calcImprovementsSellValue();

        if ( this.numHotels > 0 ) {
            bank.returnHotels(this.numHotels);
            bank.returnHouses(4 * this.numHotels);  // When you sell a hotel, you must also return the 4 houses to the bank
            System.out.println(name + ": Returned " + this.numHotels + " hotel(s) and " + (4 * this.numHotels) + " houses to the bank.");
            this.numHotels = 0;
            this.numHouses = 0;
        } else if ( this.numHouses > 0 ) {
            bank.returnHouses(this.numHouses);
            System.out.println(name + ": Returned " + this.numHouses + " house(s) to the bank.");
            this.numHouses = 0;
        }

        if ( valueReturnToBank > 0 ) {
            this.rent = this.originalRent; // reset rent back to original
            System.out.println(name + ": All Properties cleared. Rent reset to $" + this.rent);
        } else {
            System.out.println(name + ": No improvements to clear.");
        }

        return valueReturnToBank;
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

    public int getSellValue() {
        return this.price / 2; // or adjust if you have a different mortgage/sell-back rule
    }

    /*  Function that increments number of houses on the property by 1 */
    public void addHouse() {
        this.numHouses++;
        updateRentHouses();
    }

    /* Function that increments the number of hotels on the property by 1 */
    public void addHotel() {
        this.numHotels++;
        updateRentHotels();
    }

    public int calcImprovementsSellValue() {
        int value = 0;

        if ( this.numHotels > 0 ) {
            value += (this.costHotels / 2) * this.numHotels;
            value += (this.costHouses / 2) * 4 * this.numHotels;
        } else if ( this.numHouses > 0 ) {
            value += (this.costHouses / 2) * this.numHouses;
        }

        return value;
    }

    /*  ###########################################################################################
        ### Function that updates the rent based on the amount of houses/hotels on the property ###
        ###########################################################################################  */

    // Function that resets rent to original price ( when player sells property)
    public void updateRentToOriginalRent() {
        this.rent = this.originalRent;
    }

    /* Function that updates the rent based on the amount of houses on a property space */
    public void updateRentHouses() {

        switch (this.name) {
            case "Mediterranean Avenue":
                if (this.numHouses == 1) {
                    this.rent = 10;
                }
                else if (this.numHouses == 2) {
                    this.rent = 30;
                }
                else if (this.numHouses == 3) {
                    this.rent = 90;
                }
                else if (this.numHouses == 4) {
                    this.rent = 160;
                }
                break;
            case "Baltic Avenue":
                if (this.numHouses == 1) {
                    this.rent = 20;
                }
                else if (this.numHouses == 2) {
                    this.rent = 60;
                }
                else if (this.numHouses == 3) {
                    this.rent = 180;
                }
                else if (this.numHouses == 4) {
                    this.rent = 320;
                }
                break;
            
            case "Oriental Avenue":
            case "Vermont Avenue":
                if (this.numHouses == 1) {
                    this.rent = 30;
                }
                else if (this.numHouses == 2) {
                    this.rent = 90;
                }
                else if (this.numHouses == 3) {
                    this.rent = 270;
                }
                else if (this.numHouses == 4) {
                    this.rent = 400;
                }
                break;
            case "Connecticut Avenue":
                if (this.numHouses == 1) {
                    this.rent = 40;
                }
                else if (this.numHouses == 2) {
                    this.rent = 100;
                }
                else if (this.numHouses == 3) {
                    this.rent = 300;
                }
                else if (this.numHouses == 4) {
                    this.rent = 450;
                }
                break;

            case "St. Charles Place":
            case "States Avenue":
                if (this.numHouses == 1) {
                    this.rent = 50;
                }
                else if (this.numHouses == 2) {
                    this.rent = 150;
                }
                else if (this.numHouses == 3) {
                    this.rent = 450;
                }
                else if (this.numHouses == 4) {
                    this.rent = 625;
                }
                break;
            case "Virginia Avenue":
                if (this.numHouses == 1) {
                    this.rent = 60;
                }
                else if (this.numHouses == 2) {
                    this.rent = 180;
                }
                else if (this.numHouses == 3) {
                    this.rent = 500;
                }
                else if (this.numHouses == 4) {
                    this.rent = 700;   
                }
                break;

            case "St. James Place":
            case "Tennessee Avenue":
                if (this.numHouses == 1) {
                    this.rent = 70;
                }
                else if (this.numHouses == 2) {
                    this.rent = 200;
                }
                else if (this.numHouses == 3) {
                    this.rent = 550;
                }
                else if (this.numHouses == 4) {
                    this.rent = 750;
                }
                break;
            case "New York Avenue":
                if (this.numHouses == 1) {
                    this.rent = 80;
                }
                else if (this.numHouses == 2) {
                    this.rent = 220;
                }
                else if (this.numHouses == 3) {
                    this.rent = 600;
                }
                else if (this.numHouses == 4) {
                    this.rent = 800;
                }
                break;

            case "Kentucky Avenue":
            case "Indiana Avenue":
                if (this.numHouses == 1) {
                    this.rent = 90;
                }
                else if (this.numHouses == 2) {
                    this.rent = 250;
                }
                else if (this.numHouses == 3) {
                    this.rent = 700;
                }
                else if (this.numHouses == 4) {
                    this.rent = 875;
                }
                break;
            case "Illinois Avenue":
                if (this.numHouses == 1) {
                    this.rent = 100;
                }
                else if (this.numHouses == 2) {
                    this.rent = 300;
                }
                else if (this.numHouses == 3) {
                    this.rent = 750;
                }
                else if (this.numHouses == 4) {
                    this.rent = 925;
                }
                break;

            case "Atlantic Avenue":
            case "Ventnor Avenue":
                if (this.numHouses == 1) {
                    this.rent = 110;
                }
                else if (this.numHouses == 2) {
                    this.rent = 330;
                }
                else if (this.numHouses == 3) {
                    this.rent = 800;
                }
                else if (this.numHouses == 4) {
                    this.rent = 975;
                }
                break;
            case "Marvin Gardens":
                if (this.numHouses == 1) {
                    this.rent = 120;
                }
                else if (this.numHouses == 2) {
                    this.rent = 360;
                }
                else if (this.numHouses == 3) {
                    this.rent = 850;
                }
                else if (this.numHouses == 4) {
                    this.rent = 1025;
                }
                break;

            case "Pacific Avenue":
            case "North Carolina Avenue":
                if (this.numHouses == 1) {
                    this.rent = 130;
                }
                else if (this.numHouses == 2) {
                    this.rent = 390;
                }
                else if (this.numHouses == 3) {
                    this.rent = 900;
                }
                else if (this.numHouses == 4) {
                    this.rent = 1100;
                }
                break;
            case "Pennsylvania Avenue":
                if (this.numHouses == 1) {
                    this.rent = 150;
                }
                else if (this.numHouses == 2) {
                    this.rent = 450;
                }
                else if (this.numHouses == 3) {
                    this.rent = 1000;
                }
                else if (this.numHouses == 4) {
                    this.rent = 1200;
                }
                break;

            case "Park Place":
                if (this.numHouses == 1) {
                    this.rent = 175;
                }
                else if (this.numHouses == 2) {
                    this.rent = 500;
                }
                else if (this.numHouses == 3) {
                    this.rent = 1100;
                }
                else if (this.numHouses == 4) {
                    this.rent = 1300;
                }
                break;
            case "Boardwalk":
                if (this.numHouses == 1) {
                    this.rent = 200;
                }
                else if (this.numHouses == 2) {
                    this.rent = 600;
                }
                else if (this.numHouses == 3) {
                    this.rent = 1400;
                }
                else if (this.numHouses == 4) {
                    this.rent = 1700;
                }
                break;

            default:    // Do NOTHING
        }
    }

    /*  Function that updates the rent if a property has a hotel on its space */
    public void updateRentHotels() {

        switch (this.name) {
            case "Mediterranean Avenue":
                this.rent = 250;
                break;
            case "Baltic Avenue":
                this.rent = 450;
                break;
            
            case "Oriental Avenue":
            case "Vermont Avenue":
                this.rent = 550;
                break;
            case "Connecticut Avenue":
                this.rent = 600;
                break;

            case "St. Charles Place":
            case "States Avenue":
                this.rent = 750;
                break;
            case "Virginia Avenue":
                this.rent = 900;
                break;

            case "St. James Place":
            case "Tennessee Avenue":
                this.rent = 950;
                break;
            case "New York Avenue":
                this.rent = 1000;
                break;

            case "Kentucky Avenue":
            case "Indiana Avenue":
                this.rent = 1050;
                break;
            case "Illinois Avenue":
                this.rent = 1100;
                break;

            case "Atlantic Avenue":
            case "Ventnor Avenue":
                this.rent = 1150;
                break;
            case "Marvin Gardens":
                this.rent = 1200;
                break;

            case "Pacific Avenue":
            case "North Carolina Avenue":
                this.rent = 1275;
                break;
            case "Pennsylvania Avenue":
                this.rent = 1400;
                break;
                
            case "Park Place":
                this.rent = 1500;
                break;
            case "Boardwalk":
                this.rent = 2000;
                break;

            default:    // DO NOTHING
        }
    }



}