import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class GameGUI extends JFrame {
    private ArrayList<Player> players;
    private JPanel playersPanel;
    private JPanel controlPanel;
    private JButton rollButton;
    private JButton endTurnButton;
    private JButton buildButton;
    private int currentPlayerIndex;
    private GameBoardSpaces boardSpaces;

    public GameGUI(ArrayList<Player> players) {
        this.players = players;
        this.boardSpaces = new GameBoardSpaces(players);
        boardSpaces.setGameGUI(this);   // Set the GameGUI instance for later use in GameBoardSpaces, when updating player profiles

        // Randomly select the first player
        Random rand = new Random();
        this.currentPlayerIndex = rand.nextInt(players.size());

        // Set up the frame
        setTitle("Monopoly Game");
        if ( players.size() >= 6 ) {
            setSize(300, 800);
        }
        else if (players.size() >= 4 ) {
            setSize(300, 600);
        }
        else {
            setSize(300, 400);
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize buttons
        rollButton = new JButton("Roll Dice");
        rollButton.setFocusable(false);
        rollButton.addActionListener( _ -> {
            Player currentPlayer = players.get(currentPlayerIndex);

            do {
                currentPlayer.playerTurn();
                updatePlayerPanel(currentPlayer);
            } while (currentPlayer.getRolledDouble());

            rollButton.setEnabled(false); // Human's turn ends here
        });

        endTurnButton = new JButton("End Turn");
        endTurnButton.setFocusable(false);
        endTurnButton.addActionListener( _ -> {
            endTurn();
            rollButton.setEnabled(!(players.get(currentPlayerIndex) instanceof Bot)); // Only enable for humans
            handleBotTurns(); // If it's a bot, take turns automatically
        });

        buildButton = new JButton("Build Property");
        buildButton.setFocusable(false);    //  disabled keyboard focus
        updateBuildButtonState();

        // Debug button (unchanged)
        JButton debugButton = new JButton("Debug");
        debugButton.setFocusable(false);
        debugButton.addActionListener( _ -> {
            Player currentPlayer = players.get(currentPlayerIndex);
            showDebugPanel(currentPlayer);
        });

        // Create player panel
        playersPanel = new JPanel(new GridLayout(players.size(), 1));
        for (Player player : players) {
            JPanel playerPanel = createPlayerProfilePanel(player);
            playerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            playersPanel.add(playerPanel);
        }

        // Control panel
        this.controlPanel = new JPanel(new GridLayout(0, 1));
        controlPanel.add(rollButton);
        controlPanel.add(endTurnButton);
        controlPanel.add(buildButton);
        controlPanel.add(debugButton);

        add(playersPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);

        startGame(); // Begin game logic
    }

    private JPanel createPlayerProfilePanel(Player player) {
        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.setName(player.getPlayerName());

        panel.add(new JLabel("Name: " + player.getPlayerName()));
        panel.add(new JLabel("Token: " + player.getToken()));
        panel.add(new JLabel("Money: $" + player.getMoney()));

        int pos = player.getPosition();
        String description = boardSpaces.isProperty(pos)
                ? boardSpaces.getProperty(pos).getName()
                : boardSpaces.spaceType(pos);

        panel.add(new JLabel("Position: " + pos + " - " + description));
        return panel;
    }

    // Function that updates the player panel, and can be called from other Java classes
    public void updatePlayerPanel(Player player) {
        for (Component comp : playersPanel.getComponents()) {
            JPanel panel = (JPanel) comp;
            if (panel.getName().equals(player.getPlayerName())) {
                JLabel moneyLabel = (JLabel) panel.getComponent(2);
                JLabel posLabel = (JLabel) panel.getComponent(3);

                moneyLabel.setText("Money: $" + player.getMoney());
                int pos = player.getPosition();
                String desc = boardSpaces.isProperty(pos)
                        ? boardSpaces.getProperty(pos).getName()
                        : boardSpaces.spaceType(pos);
                posLabel.setText("Position: " + pos + " - " + desc);

                panel.setBackground(player.getIsEliminated() ? Color.RED : null);
                break;
            }
        }
    }

    /*  Function that enables buildButton if player owns a Full Color Set */
    private void updateBuildButtonState() {
        boolean canBuild = !players.get(currentPlayerIndex).getFullColorsets().isEmpty();
        buildButton.setEnabled(canBuild);
        System.out.println("Checking buildButton enable state for player: " + players.get(currentPlayerIndex).getPlayerName());
        System.out.println("buildButton " + (canBuild ? "ENABLED" : "DISABLED") + " - Full color sets: " + canBuild);
    }

    private void endTurn() {
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (players.get(currentPlayerIndex).getIsEliminated());

        checkWinner();

        Player currentPlayer = players.get(currentPlayerIndex);
        JOptionPane.showMessageDialog(this, "It's now " + currentPlayer.getPlayerName() + "'s turn!");
        updateBuildButtonState();
    }

    private void handleBotTurns() {
        while (players.get(currentPlayerIndex) instanceof Bot) {
            Bot bot = (Bot) players.get(currentPlayerIndex);
            do {
                bot.playerTurn();
                updatePlayerPanel(bot);
                // TODO: Add code for bot to update control panel {might be needed for bought to build houses}
            } while (bot.getRolledDouble());

            endTurn();
        }
        rollButton.setEnabled(true); // Enable for the next human
    }

    private void showDebugPanel(Player player) {
        JPanel panel = new JPanel(new GridLayout(4, 2));
        JTextField moneyField = new JTextField();
        JTextField posField = new JTextField();

        panel.add(new JLabel("Add/Subtract Money:"));
        panel.add(moneyField);
        panel.add(new JLabel("Move to Position (1-40):"));
        panel.add(posField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Debug Tool", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int moneyChange = Integer.parseInt(moneyField.getText());
                int newPos = Integer.parseInt(posField.getText());

                player.updateMoney(moneyChange);
                player.setPosition(newPos);
                updatePlayerPanel(player);
                boardSpaces.purchaseProperty(player, player.getPosition(), 1);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        }
    }

    private void checkWinner() {
        int activeCount = 0;
        Player winner = null;
        for (Player p : players) {
            if (!p.getIsEliminated()) {
                activeCount++;
                winner = p;
            }
        }
        if (activeCount == 1 && winner != null) {
            JOptionPane.showMessageDialog(this, winner.getPlayerName() + " wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }

    private void startGame() {
        handleBotTurns(); // Handle bot-only first turn
        rollButton.setEnabled(!(players.get(currentPlayerIndex) instanceof Bot));
    }
}
