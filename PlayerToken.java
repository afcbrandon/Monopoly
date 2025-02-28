import java.util.Scanner;
// this class should create the GamePieces as well as create what each piece looks like
// I am mostly struggling about what my syntax should look like and setting the token design.
// Current goal: initialize and Set game piece design
public class PlayerToken {
    private String token;
    private String tokenDesign;

    // was trying to make sure the token and tokenDesign was getting sent to the get and set methods
    // might not be needed yet
    public void Main(String token, String tokenDesign){
        // setPlayerToken(token);
        // setTokenDesign(tokenDesign);

    }

    // sets up default constructor for a player token
    public void Token(String token, String tokenDesign, int position) {
        this.token = token;
        this.tokenDesign = tokenDesign;
        int position1 = 0;
    }

    // allows player to enter desired game piece
    // needs a check to make sure piece is not used
    public static String getPlayerToken(){

        String token;
        Scanner userInput = new Scanner(System.in);
        System.out.println("Please enter your desired game piece(Thimble, Boot, RaceCar, Dog, Penguin, Cat, TopHat, WheelBarrow) >> ");
        token = userInput.nextLine();
        return token;

    }

    // this is where I am getting stuck at with the code
    public String getTokenDesign() {
        return tokenDesign;
    }

    public void setTokenDesign(String tokenDesign){
        tokenDesign = token;
    }

    public void setPlayerToken(){

    }


}
