import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {
    public MainMenu() {

        setTitle("Monopoly Main Menu");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1));

        JButton playHumanBtn = new JButton("Play with Human Players");
        JButton playBotBtn = new JButton("Play with Bot(s)");
        JButton exitBtn = new JButton("Exit");

        playHumanBtn.addActionListener( _ -> {
            dispose();  // Close this window
            new UserInterface(false).start(); // false = human players only
        });

        playBotBtn.addActionListener( _ -> {
            dispose();
            new UserInterface(true).start(); // true = play with bots
        });

        exitBtn.addActionListener( _ -> System.exit(0));

        add(playHumanBtn);
        add(playBotBtn);
        add(exitBtn);

        setVisible(true);
    }
}
