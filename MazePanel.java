import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;
import javax.swing.JPanel;

/** Renders the maze, player, trails, hints, and solution overlays. */
public class MazePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private Maze maze;
    private GameState gameState;
    private List<Point> solution;
    private Point hint;

    /** Creates an empty panel; call setGame before displaying it. */
    public MazePanel() { setBackground(Color.WHITE); }

    /** Supplies the current maze and game state to render. */
    public void setGame(Maze maze, GameState gameState) { this.maze = maze; this.gameState = gameState; repaint(); }
    /** Shows the supplied complete solution path, or clears it when null. */
    public void setSolution(List<Point> solution) { this.solution = solution; repaint(); }
    /** Shows the supplied one-cell hint, or clears it when null. */
    public void setHint(Point hint) { this.hint = hint == null ? null : new Point(hint); repaint(); }

    /** Paints the current game without changing its state. */
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (maze == null || gameState == null) return;
        int cellSize = Math.max(1, Math.min(getWidth() / maze.getCols(), getHeight() / maze.getRows()));
        int xOffset = (getWidth() - cellSize * maze.getCols()) / 2;
        int yOffset = (getHeight() - cellSize * maze.getRows()) / 2;
        boolean[][] trail = gameState.getUserTrail();
        for (int row = 0; row < maze.getRows(); row++) for (int col = 0; col < maze.getCols(); col++) {
            g.setColor(maze.isWall(row, col) ? Color.DARK_GRAY : (trail[row][col] ? new Color(255, 245, 170) : Color.WHITE));
            g.fillRect(xOffset + col * cellSize, yOffset + row * cellSize, cellSize, cellSize);
            g.setColor(Color.LIGHT_GRAY); g.drawRect(xOffset + col * cellSize, yOffset + row * cellSize, cellSize, cellSize);
        }
        if (solution != null) { g.setColor(new Color(173, 216, 230)); for (Point p : solution) fillCell(g, p, xOffset, yOffset, cellSize); }
        if (hint != null) { g.setColor(new Color(255, 185, 80)); fillCell(g, hint, xOffset, yOffset, cellSize); }
        Point end = maze.getEnd(), player = gameState.getPlayer();
        g.setColor(Color.RED); g.fillOval(xOffset + end.y * cellSize + cellSize / 4, yOffset + end.x * cellSize + cellSize / 4, cellSize / 2, cellSize / 2);
        g.setColor(Color.GREEN.darker()); g.fillOval(xOffset + player.y * cellSize + cellSize / 4, yOffset + player.x * cellSize + cellSize / 4, cellSize / 2, cellSize / 2);
    }

    private void fillCell(Graphics g, Point point, int xOffset, int yOffset, int cellSize) {
        if (point != null && point.x >= 0 && point.y >= 0 && point.x < maze.getRows() && point.y < maze.getCols())
            g.fillRect(xOffset + point.y * cellSize, yOffset + point.x * cellSize, cellSize, cellSize);
    }
}
