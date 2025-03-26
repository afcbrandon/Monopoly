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
    public String getName() {
        return name;
    }
    public int getRent() {
        return rent;
    }
    public Player getOwner() {
        return owner;
    }
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    /* Function that returns the price of the property, dependent on the space */
    public int propertyPrice (int boardSpace) {
        int price;

        switch (boardSpace) {
            case 1:     //  Purple Spaces: Mediterranean Avenue, Baltic Avenue
            case 3: 
                price = 60; 
                break;
            case 6:     //  Light Blue Spaces: Oriental Avenue, Vermont Avenue, Connecticut Avenue
            case 7:
            case 8:
                price = 100;
                if (boardSpace == 8) {
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