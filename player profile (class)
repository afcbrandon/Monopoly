public class GameGUI extends JFrame {
    private Player[] players;
    private GameBoard gameBoard;
    private JPanel playersPanel;
    private JButton rollButton;
    private JButton endTurnButton;
    private int currentPlayerIndex;

    public GameGUI(Player[] players, GameBoard gameBoard) {
        this.players = players;
        this.gameBoard = gameBoard; // Asignamos el tablero

        Random rand = new Random();
        this.currentPlayerIndex = rand.nextInt(players.length);

        setTitle("Monopoly Game");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        rollButton = new JButton("Roll Dice");
        rollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Player currentPlayer = players[currentPlayerIndex];
                currentPlayer.rollAndMove();
                gameBoard.movePlayer(currentPlayer, currentPlayer.getPosition());
                updatePlayerPanel(currentPlayer);
            }
        });

        endTurnButton = new JButton("End Turn");
        endTurnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                endTurn();
            }
        });

        playersPanel = new JPanel();
        playersPanel.setLayout(new GridLayout(players.length, 1));

        for (Player player : players) {
            JPanel playerPanel = createPlayerProfilePanel(player);
            playersPanel.add(playerPanel);
            playerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(rollButton);
        controlPanel.add(endTurnButton);

        add(playersPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}
