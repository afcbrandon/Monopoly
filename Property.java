public class Property {
  private String name;
  private int price;
  private int rent;
  private Player owner;

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
}