import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/** Main Swing window that wires game controls, game state, maze, and renderer together. */
public class MazeGameFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private Maze maze;
    private GameState gameState;
    private final PathFinder pathFinder = new PathFinder();
    private final MazePanel mazePanel = new MazePanel();
    private final JLabel movesLabel = new JLabel();
    private final JLabel timerLabel = new JLabel();
    private final JLabel pointsLabel = new JLabel();
    private final JLabel statusLabel = new JLabel("Playing", SwingConstants.CENTER);
    private final JComboBox<String> sizeSelector = new JComboBox<>(new String[] {"Small (10x10)", "Medium (20x20)", "Large (30x30)"});
    private boolean solutionShown;
    private int overallOptimalMoves;

    /** Creates, lays out, and starts a new maze-game window. */
    public MazeGameFrame() {
        super("Maze Solver Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        add(mazePanel, BorderLayout.CENTER);
        add(createControls(), BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.NORTH);
        statusLabel.setOpaque(true);
        installKeyHandling();
        startNewGame(10, 10);
        new Timer(250, e -> updateLabels()).start();
        setSize(900, 900);
        setLocationByPlatform(true);
    }

    private JPanel createControls() {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        JButton closest = new JButton("Show Closest Solution"), shortest = new JButton("Show Shortest Path");
        JButton hint = new JButton("Hint"), retry = new JButton("Try Again"), newGame = new JButton("New Game");
        closest.setFocusable(false); shortest.setFocusable(false); hint.setFocusable(false); retry.setFocusable(false); newGame.setFocusable(false);
        closest.addActionListener(e -> { showSolution(true); requestGameFocus(); });
        shortest.addActionListener(e -> { showSolution(false); requestGameFocus(); });
        hint.addActionListener(e -> { showHint(); requestGameFocus(); });
        retry.addActionListener(e -> resetPlayer()); newGame.addActionListener(e -> confirmNewGame());
        controls.add(closest); controls.add(shortest); controls.add(hint); controls.add(retry); controls.add(sizeSelector); controls.add(newGame);
        controls.add(movesLabel); controls.add(timerLabel); controls.add(pointsLabel);
        return controls;
    }

    private void installKeyHandling() {
        KeyAdapter listener = new KeyAdapter() { @Override public void keyPressed(KeyEvent e) { handleKey(e); } };
        addKeyListener(listener); mazePanel.setFocusable(true); mazePanel.addKeyListener(listener);
    }

    private void handleKey(KeyEvent event) {
        int row = 0, col = 0;
        switch (event.getKeyCode()) {
            case KeyEvent.VK_UP: case KeyEvent.VK_W: row = -1; break;
            case KeyEvent.VK_DOWN: case KeyEvent.VK_S: row = 1; break;
            case KeyEvent.VK_LEFT: case KeyEvent.VK_A: col = -1; break;
            case KeyEvent.VK_RIGHT: case KeyEvent.VK_D: col = 1; break;
            default: return;
        }
        if (gameState != null && gameState.move(maze, row, col)) {
            mazePanel.setSolution(null); mazePanel.setHint(null); solutionShown = false;
            if (gameState.hasWon()) showWin(); else setStatus("Playing", new Color(225, 245, 225));
            updateLabels(); mazePanel.repaint();
        }
    }

    private void showSolution(boolean biased) {
        if (maze == null || gameState == null) return;
        Point currentPosition = gameState.getPlayer();
        List<Point> path = biased
                ? pathFinder.findTrailBiasedPath(maze, currentPosition, maze.getEnd(), gameState.getUserTrail())
                : pathFinder.findShortestPath(maze, maze.getStart(), maze.getEnd());
        mazePanel.setHint(null); mazePanel.setSolution(path); solutionShown = true; setStatus("Solution shown", new Color(220, 235, 255));
    }

    private void showHint() {
        if (maze == null || gameState == null || gameState.hasWon()) return;
        mazePanel.setSolution(null); solutionShown = false;
        Point nextStep = pathFinder.findNextStep(maze, gameState.getPlayer(), maze.getEnd());
        if (nextStep != null) gameState.recordHint();
        mazePanel.setHint(nextStep);
        setStatus("Playing", new Color(225, 245, 225));
        updateLabels();
    }

    private void resetPlayer() { if (maze != null) { gameState = new GameState(maze); mazePanel.setGame(maze, gameState); mazePanel.setSolution(null); mazePanel.setHint(null); solutionShown = false; setStatus("Playing", new Color(225, 245, 225)); updateLabels(); requestGameFocus(); } }
    private void confirmNewGame() {
        if (gameState != null && !gameState.hasWon() && gameState.getMoveCount() > 0 && JOptionPane.showConfirmDialog(this, "Discard the current maze and start a new one?", "New Game", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        int size = sizeSelector.getSelectedIndex() == 0 ? 10 : sizeSelector.getSelectedIndex() == 1 ? 20 : 30;
        startNewGame(size, size);
    }
    private void startNewGame(int rows, int cols) { maze = new Maze(rows, cols); gameState = new GameState(maze); overallOptimalMoves = Math.max(0, pathFinder.findShortestPath(maze, maze.getStart(), maze.getEnd()).size() - 1); mazePanel.setGame(maze, gameState); mazePanel.setSolution(null); mazePanel.setHint(null); solutionShown = false; setStatus("Playing", new Color(225, 245, 225)); updateLabels(); SwingUtilities.invokeLater(this::requestGameFocus); }
    private void showWin() {
        int optimalMoves = overallOptimalMoves;
        int moves = gameState.getMoveCount();
        int efficiency = moves == 0 ? 100 : (int) Math.round(optimalMoves * 100.0 / moves);
        int points = calculatePoints(optimalMoves, moves, gameState.getHintsUsed());
        setStatus("Won! You took " + moves + " moves - optimal was " + optimalMoves + " (" + efficiency + "% efficient). Score: " + points + ".", new Color(185, 240, 185));
        JOptionPane.showMessageDialog(this, "You won!\nYou took " + moves + " moves - optimal was " + optimalMoves + " (" + efficiency + "% efficient).\nScore: " + points + " (" + gameState.getHintsUsed() + " hints used).", "Victory", JOptionPane.INFORMATION_MESSAGE);
    }
    private int calculatePoints(int optimalMoves, int moves, int hintsUsed) {
        int safeMoves = Math.max(1, moves);
        int efficiencyPoints = (int) Math.round(1000.0 * optimalMoves / Math.max(optimalMoves, safeMoves));
        return Math.max(0, efficiencyPoints - hintsUsed * 50);
    }
    private void updateLabels() {
        if (gameState != null) {
            movesLabel.setText("Moves: " + gameState.getMoveCount());
            timerLabel.setText("Time: " + gameState.getElapsedSeconds() + "s");
            pointsLabel.setText("Points: " + calculatePoints(overallOptimalMoves, gameState.getMoveCount(), gameState.getHintsUsed()));
        }
    }
    private void setStatus(String text, Color color) { statusLabel.setText(text); statusLabel.setBackground(color); }
    private void requestGameFocus() { mazePanel.requestFocusInWindow(); requestFocusInWindow(); }

    /** Launches the game application. */
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> { MazeGameFrame frame = new MazeGameFrame(); frame.setVisible(true); }); }
}
