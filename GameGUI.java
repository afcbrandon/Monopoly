import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class GameGUI extends JFrame {
    private Player[] players;
    private JPanel playersPanel;
    private JButton rollButton;
    private JButton endTurnButton;
    private JButton quitButton;//just for testing player elimination
    private int currentPlayerIndex;

    public GameGUI(Player[] players) {
        this.players = players;

        // Randomly select the first player
        Random rand = new Random();
        this.currentPlayerIndex = rand.nextInt(players.length);  // Randomly select a starting player

        // Set up the frame
        setTitle("Monopoly Game");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create the roll and end turn buttons
        rollButton = new JButton("Roll Dice");
        rollButton.setFocusable(false);
        rollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Player currentPlayer = players[currentPlayerIndex];
                currentPlayer.rollAndMove();
                updatePlayerPanel(currentPlayer);
                rollButton.setEnabled(false);
            }
        });

        endTurnButton = new JButton("End Turn");
        endTurnButton.setFocusable(false);
        endTurnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                endTurn();
                rollButton.setEnabled(true);
            }
        });

        quitButton = new JButton("Quit");
        quitButton.setFocusable(false);
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Player currentPlayer = players[currentPlayerIndex];
                // Force elimination by subtracting enough money
                currentPlayer.updateMoney(-2000);
                updatePlayerPanel(currentPlayer);
                JOptionPane.showMessageDialog(GameGUI.this, currentPlayer.getName() + " has quit and is eliminated!");
                checkWinner(); // Check if only one player remains
                endTurn();     // Move to the next player's turn
            }
        });


        // Create a panel to display all players' profiles
        playersPanel = new JPanel();
        playersPanel.setLayout(new GridLayout(players.length, 1));

        for (Player player : players) {
            JPanel playerPanel = createPlayerProfilePanel(player);
            playersPanel.add(playerPanel);
            playerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            setSize(300, 300);
            setLocationRelativeTo(null);
        }

        // Add the players panel and buttons to the frame
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(rollButton);
        controlPanel.add(endTurnButton);
        controlPanel.add(quitButton);// quit button


        add(playersPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // creates the profile panel for each player
    private JPanel createPlayerProfilePanel(Player player) {
        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new GridLayout(3, 1));

        JLabel nameLabel = new JLabel("Name: " + player.getName());
        JLabel moneyLabel = new JLabel("Money: $" + player.getMoney());
        JLabel positionLabel = new JLabel("Position: " + player.getPosition());

        playerPanel.add(nameLabel);
        playerPanel.add(moneyLabel);
        playerPanel.add(positionLabel);

        playerPanel.setName(player.getName());

        return playerPanel;
    }

    // updates the profile panel for a specific player
    private void updatePlayerPanel(Player player) {
        Component[] components = playersPanel.getComponents();
        for (Component comp : components) {
            JPanel playerPanel = (JPanel) comp;
            if (playerPanel.getName().equals(player.getName())) {
                JLabel moneyLabel = (JLabel) playerPanel.getComponent(1);
                JLabel positionLabel = (JLabel) playerPanel.getComponent(2);

                moneyLabel.setText("Money: $" + player.getMoney());
                positionLabel.setText("Position: " + player.getPosition());

                if (player.isEliminated()) {
                    playerPanel.setBackground(Color.RED);  // Change background to red
                } else {
                    playerPanel.setBackground(null);  // Reset to default background color
                }
                break;
            }
        }
    }

    private void checkWinner() {
        int activePlayers = 0;
        Player winner = null;
    
        for (Player player : players) {
            if (!player.isEliminated()) {
                activePlayers++;
                winner = player;
            }
        }
    
        if (activePlayers == 1) {
            JOptionPane.showMessageDialog(this, winner.getName() + " is the winner!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0); // End the game
        }
    }
    

    //  handles the "End Turn" button
    private void endTurn() {
        do {
        // Move to the next player
        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
    } while (players[currentPlayerIndex].isEliminated());
    checkWinner(); // Check if only one player remains

        // Update the GUI to reflect the current player's turn
        Player currentPlayer = players[currentPlayerIndex];
        JOptionPane.showMessageDialog(this, "It's now " + currentPlayer.getName() + "'s turn!");
    }

}
