import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

public class GameGUI extends JFrame {
    private ArrayList<Player> players = new ArrayList<>();
    private JPanel playersPanel;
    private JButton rollButton;
    private JButton endTurnButton;
    private JButton buildButton;
    private int currentPlayerIndex;
    private GameBoardSpaces boardSpaces;
  
    public GameGUI(ArrayList<Player> players) {
        this.players = players;
        this.boardSpaces = new GameBoardSpaces(players);

        // Randomly select the first player
        Random rand = new Random();
        this.currentPlayerIndex = rand.nextInt(players.size());  // Randomly select a starting player

        // Set up the frame
        setTitle("Monopoly Game");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Variable that has the current player selected
        Player currentPlayer = players.get(currentPlayerIndex);

        // Create the roll and end turn buttons
        rollButton = new JButton("Roll Dice");
        rollButton.setFocusable(false);
        rollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                do {
                    currentPlayer.playerTurn();
                    updatePlayerPanel(currentPlayer);
                } while(currentPlayer.getRolledDouble());
        
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

        // buildButton button that gives player the option to buildButton houses on their properties
        buildButton = new JButton("buildButton");
        buildButton.setFocusable(false);
        buildButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPlayer.ownsFullSet(null)) {  // TODO: added null code so that button does not throw errors
                    buildButton.setEnabled(true);
                }
                else {
                    buildButton.setEnabled(false);
                }
            }
        });



        // Create a panel to display all players' profiles
        playersPanel = new JPanel();
        playersPanel.setLayout(new GridLayout(players.size(), 1));

        for (Player player : players) {
            JPanel playerPanel = createPlayerProfilePanel(player);
            playersPanel.add(playerPanel);
            playerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            setSize(300, 400);
            setLocationRelativeTo(null);
        }

        // Add the players panel and buttons to the frame
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(0, 1));
        controlPanel.add(rollButton);
        controlPanel.add(endTurnButton);
        controlPanel.add(buildButton); // quit button
        JButton debugButton = new JButton("Debug");
        debugButton.setFocusable(false);
        debugButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Player currentPlayer = players.get(currentPlayerIndex);
                showDebugPanel(currentPlayer);
            }
        });
        controlPanel.add(debugButton);


        add(playersPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // creates the profile panel for each player
    // *** Brandon *** Updated the playerPanel rows to 4 and added Token label
    private JPanel createPlayerProfilePanel(Player player) {
        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new GridLayout(4, 1)); 

        JLabel nameLabel = new JLabel("Name: " + player.getPlayerName());
        JLabel tokenLabel = new JLabel("Token: " + player.getToken());
        JLabel moneyLabel = new JLabel("Money: $" + player.getMoney());
        String spaceDescription;
        int pos = player.getPosition();

        if (boardSpaces.isProperty(pos)) {
            spaceDescription = boardSpaces.getProperty(pos).getName();
        } else {
            spaceDescription = boardSpaces.spaceType(pos);  // e.g. "Chance", "Tax", etc.
        }
        JLabel positionLabel = new JLabel("Position: " + pos + " - " + spaceDescription);

        playerPanel.add(nameLabel);
        playerPanel.add(tokenLabel);
        playerPanel.add(moneyLabel);
        playerPanel.add(positionLabel);

        playerPanel.setName(player.getPlayerName());

        return playerPanel;
    }

    // updates the profile panel for a specific player
    private void updatePlayerPanel(Player player) {
        Component[] components = playersPanel.getComponents();
        for (Component comp : components) {
            JPanel playerPanel = (JPanel) comp;
            if (playerPanel.getName().equals(player.getPlayerName())) {
                JLabel moneyLabel = (JLabel) playerPanel.getComponent(2);
                JLabel positionLabel = (JLabel) playerPanel.getComponent(3);

                moneyLabel.setText("Money: $" + player.getMoney());
                int pos = player.getPosition();
                String spaceDescription;
                if (boardSpaces.isProperty(pos)) {
                    spaceDescription = boardSpaces.getProperty(pos).getName();
                } else {
                    spaceDescription = boardSpaces.spaceType(pos);
                }
                positionLabel.setText("Position: " + pos + " - " + spaceDescription);

                if (player.getIsEliminated()) {
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
            if (!player.getIsEliminated()) {
                activePlayers++;
                winner = player;
            }
        }
    
        if (activePlayers == 1) {
            JOptionPane.showMessageDialog(this, winner.getPlayerName() + " is the winner!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0); // End the game
        }
    }
    

    //  handles the "End Turn" button
    private void endTurn() {
        do {
        // Move to the next player
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    } while (players.get(currentPlayerIndex).getIsEliminated());
    checkWinner(); // Check if only one player remains

        // Update the GUI to reflect the current player's turn
        Player currentPlayer = players.get(currentPlayerIndex);
        JOptionPane.showMessageDialog(this, "It's now " + currentPlayer.getPlayerName() + "'s turn!");
    }
    private void showDebugPanel(Player player) {
        JPanel panel = new JPanel(new GridLayout(4, 2));

        JTextField moneyField = new JTextField();
        JTextField positionField = new JTextField();

        panel.add(new JLabel("Add/Subtract Money:"));
        panel.add(moneyField);
        panel.add(new JLabel("Move to Position (1-40):"));
        panel.add(positionField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Debug Tool", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int moneyChange = Integer.parseInt(moneyField.getText());
                int newPosition = Integer.parseInt(positionField.getText());

                player.updateMoney(moneyChange);
                player.setPosition(newPosition);
                updatePlayerPanel(player);
                boardSpaces.purchaseProperty(player, player.getPosition(), 1);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid numbers.");
            }
        }
    }



}
