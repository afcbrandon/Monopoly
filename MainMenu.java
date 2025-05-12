import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {
    public MainMenu() {
        setTitle("Monopoly Main Menu");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main panel with vertical layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(255, 239, 213));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Title label
        JLabel titleLabel = new JLabel("Welcome to Monopoly");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(183, 0, 0));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Buttons
        JButton playHumanBtn = new JButton("Play with Human Players");
        JButton playBotBtn = new JButton("Play with Bot(s)");
        JButton exitBtn = new JButton("Exit");

        // Common button styling
        Font buttonFont = new Font("SansSerif", Font.BOLD, 16);
        Color buttonColor = new Color(70, 130, 180);
        Color exitColor = new Color(220, 20, 60);

        styleButton(playHumanBtn, buttonColor);
        styleButton(playBotBtn, buttonColor);
        styleButton(exitBtn, exitColor);

        // Button actions
        playHumanBtn.addActionListener(_ -> {
            dispose();
            new UserInterface(false).start();
        });

        playBotBtn.addActionListener(_ -> {
            dispose();
            new UserInterface(true).start();
        });

        exitBtn.addActionListener(_ -> System.exit(0));

        // Add components to panel
        mainPanel.add(titleLabel);
        mainPanel.add(playHumanBtn);
        mainPanel.add(Box.createVerticalStrut(10)); // Spacer
        mainPanel.add(playBotBtn);
        mainPanel.add(Box.createVerticalStrut(10)); // Spacer
        mainPanel.add(exitBtn);

        add(mainPanel);
        setVisible(true);
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(300, 45));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setFocusPainted(false);
    }
}
