/*  Temporarily commenting out this class and returning to it later

public class Property {
    private String name;
    private int price;
    private int rent;
    private Player owner;
    private boolean hasHotel;

    public Property(String name, int price, int rent) {
        this.name = name;
        this.price = price;
        this.rent = rent;
        this.owner = null;
        this.hasHotel = false;
    }

    public boolean upgradeToHotel(Player player) {
        if (this.owner != player) {
            System.out.println("You don't own this property!");
            return false;
        }
        if (hasHotel) {
            System.out.println("This property already has a hotel!");
            return false;
        }
        if (player.getMoney() < price / 2) {  // Example cost for upgrade
            System.out.println("Not enough money to upgrade!");
            return false;
        }
        player.deductMoney(price / 2);
        hasHotel = true;
        rent *= 2; // Double the rent when a hotel is built
        System.out.println(name + " has been upgraded to a hotel!");
        return true;
    }

    public int getRent() {
        return rent;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }
}


 */
