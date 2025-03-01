import java.util.ArrayList;
// this class should create the GamePieces as well as create what each piece looks like
// I am mostly struggling about what my syntax should look like and setting the token design.
// Current goal: initialize and Set game piece design
public class PlayerToken {
    //private String token;
    //private String tokenDesign;

    private ArrayList<Character> tokenList = new ArrayList<>();

    // EMPTY CONSTRUCTOR
    public PlayerToken() {

        this.tokenList.add('!');
        this.tokenList.add('@');
        this.tokenList.add('#');
        this.tokenList.add('$');
        this.tokenList.add('%');
        this.tokenList.add('&');
        this.tokenList.add('*');
        this.tokenList.add('~');

    }

    // Function that updates the token array list by removing already selected tokens
    public void chooseToken(char userChar) {

        for (int i = 0; i < tokenList.size(); i++) {
            if (tokenList.get(i) == userChar) {
                tokenList.remove(i);
            }
        }
    }

    // Function that returns the final token in the list if there are 8 players
    public Character getLastToken() {
        return tokenList.get(0);
    }

    // Function that returns the ArrayList of the available tokens
    public ArrayList<Character> getTokenList() {
        return tokenList;
    }

}
