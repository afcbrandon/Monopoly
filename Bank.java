// Bank.java
public class Bank {
    private int availableHouses;
    private int availableHotels;

    // Standard Monopoly house and hotel limits
    public static final int MAX_HOUSES = 32;
    public static final int MAX_HOTELS = 12;

    public Bank() {
        this.availableHouses = MAX_HOUSES;
        this.availableHotels = MAX_HOTELS;
    }

    public int getAvailableHouses() {
        return availableHouses;
    }

    public int getAvailableHotels() {
        return availableHotels;
    }

    public boolean canDispenseHouse() {
        return availableHouses > 0;
    }

    public boolean canDispenseHotel() {
        return availableHotels > 0;
    }

    // Function that returns a house, and decrements a house from Bank
    public boolean dispenseHouse() {
        if ( canDispenseHouse() ) {
            availableHouses--;
            System.out.println("Bank: House dispensed. Houses remaining: " + availableHouses);
            return true;
        }
        System.out.println("Bank: No houses available to dispense.");
        return false;
    }

    // Function that returns a hotel, and decrements a hotel from the bank
    public boolean dispenseHotel() {
        if ( canDispenseHotel() ) {
            availableHotels--;
            System.out.println("Bank: Hotel dispensed. Hotels remaining: " + availableHotels);
            return true;
        }
        System.out.println("Bank: No hotels available to dispense.");
        return false;
    }

    // Function that adds back houses to the bank
    public void returnHouses(int count) {
        if (count <= 0) return;
        this.availableHouses += count;
        // Ensure we don't exceed the maximum (shouldn't happen in normal play if logic is correct)
        if (this.availableHouses > MAX_HOUSES) {
            System.out.println("Bank Warning: Returned houses ("+ count +") resulted in " + this.availableHouses + ". Clamping to " + MAX_HOUSES);
            this.availableHouses = MAX_HOUSES;
        }
        System.out.println("Bank: " + count + " houses returned. Houses available: " + availableHouses);
    }

    // Function that adds back hotels to the bank
    public void returnHotels(int count) {
        if (count <= 0) return;
        this.availableHotels += count;
        if (this.availableHotels > MAX_HOTELS) {
            System.out.println("Bank Warning: Returned hotels ("+ count +") resulted in " + this.availableHotels + ". Clamping to " + MAX_HOTELS);
            this.availableHotels = MAX_HOTELS;
        }
        System.out.println("Bank: " + count + " hotels returned. Hotels available: " + availableHotels);
    }
}