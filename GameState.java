import java.awt.Point;

/** Stores mutable player, trail, timing, and win state for one maze game. */
public class GameState {
    private Point player;
    private final boolean[][] userTrail;
    private int moveCount;
    private int hintsUsed;
    private final long startedAt;
    private boolean won;

    /** Creates a fresh state positioned at the maze start. */
    public GameState(Maze maze) {
        if (maze == null) throw new IllegalArgumentException("Maze cannot be null.");
        player = maze.getStart();
        userTrail = new boolean[maze.getRows()][maze.getCols()];
        userTrail[player.x][player.y] = true;
        startedAt = maze.getCreatedAt();
    }

    /** Returns the current player position. */
    public Point getPlayer() { return new Point(player); }
    /** Returns the visited-cell grid. */
    public boolean[][] getUserTrail() { return userTrail; }
    /** Returns successful moves made in this game. */
    public int getMoveCount() { return moveCount; }
    /** Returns the number of hints revealed in this game. */
    public int getHintsUsed() { return hintsUsed; }
    /** Returns whole elapsed seconds since this maze started. */
    public long getElapsedSeconds() { return (System.currentTimeMillis() - startedAt) / 1000; }
    /** Returns whether the destination has been reached. */
    public boolean hasWon() { return won; }

    /** Moves one cell when it is in bounds and open; returns whether a move succeeded. */
    public boolean move(Maze maze, int deltaRow, int deltaCol) {
        if (maze == null || won) return false;
        int nextRow = player.x + deltaRow, nextCol = player.y + deltaCol;
        if (nextRow >= 0 && nextCol >= 0 && nextRow < maze.getRows() && nextCol < maze.getCols() && maze.isOpen(nextRow, nextCol)) {
            player = new Point(nextRow, nextCol);
            userTrail[nextRow][nextCol] = true;
            moveCount++;
            Point end = maze.getEnd();
            won = player.equals(end);
            return true;
        }
        return false;
    }

    /** Records that the player revealed one hint. */
    public void recordHint() { hintsUsed++; }
}
