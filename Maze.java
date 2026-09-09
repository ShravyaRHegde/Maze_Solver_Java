import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Holds maze grid data and generates solvable mazes without any UI concerns.
 */
public class Maze {
    private final int rows;
    private final int cols;
    private final int[][] grid;
    private final Point start = new Point(1, 1);
    private final Point end;
    private final long createdAt = System.currentTimeMillis();

    /** Creates a new generated maze with the supplied logical dimensions. */
    public Maze(int rows, int cols) {
        if (rows < 2 || cols < 2) {
            throw new IllegalArgumentException("Maze dimensions must be at least 2 by 2.");
        }
        this.rows = rows;
        this.cols = cols;
        this.grid = new int[rows * 2 + 1][cols * 2 + 1];
        this.end = new Point(grid.length - 2, grid[0].length - 2);
        generate();
    }

    /** Returns the number of display-grid rows. */
    public int getRows() { return grid.length; }

    /** Returns the number of display-grid columns. */
    public int getCols() { return grid[0].length; }

    /** Returns a defensive copy of the start position. */
    public Point getStart() { return new Point(start); }

    /** Returns a defensive copy of the destination position. */
    public Point getEnd() { return new Point(end); }

    /** Returns the time at which this maze was created. */
    public long getCreatedAt() { return createdAt; }

    /** Returns whether a display-grid cell is in bounds and passable. */
    public boolean isOpen(int row, int col) {
        return row >= 0 && col >= 0 && row < getRows() && col < getCols() && grid[row][col] == 0;
    }

    /** Returns whether a display-grid cell is a wall or lies outside the maze. */
    public boolean isWall(int row, int col) { return !isOpen(row, col); }

    /** Builds a recursive-backtracker spanning-tree maze, with a few optional loops. */
    private void generate() {
        for (int row = 0; row < getRows(); row++)
            for (int col = 0; col < getCols(); col++)
                grid[row][col] = 1;

        Random random = new Random();
        boolean[][] visited = new boolean[rows][cols];
        carve(0, 0, visited, random);

        List<Point> removableWalls = new ArrayList<>();
        for (int row = 1; row < getRows() - 1; row++) {
            for (int col = 1; col < getCols() - 1; col++) {
                if (grid[row][col] == 1 && ((row % 2 == 0 && col % 2 == 1) || (row % 2 == 1 && col % 2 == 0))) {
                    removableWalls.add(new Point(row, col));
                }
            }
        }
        Collections.shuffle(removableWalls, random);
        int loops = Math.max(1, removableWalls.size() * (5 + random.nextInt(6)) / 100);
        for (int i = 0; i < loops && i < removableWalls.size(); i++) {
            Point wall = removableWalls.get(i);
            grid[wall.x][wall.y] = 0;
        }
    }

    private void carve(int cellRow, int cellCol, boolean[][] visited, Random random) {
        visited[cellRow][cellCol] = true;
        grid[cellRow * 2 + 1][cellCol * 2 + 1] = 0;
        List<Point> directions = new ArrayList<>();
        directions.add(new Point(-1, 0)); directions.add(new Point(1, 0));
        directions.add(new Point(0, -1)); directions.add(new Point(0, 1));
        Collections.shuffle(directions, random);
        for (Point direction : directions) {
            int nextRow = cellRow + direction.x;
            int nextCol = cellCol + direction.y;
            if (nextRow >= 0 && nextCol >= 0 && nextRow < rows && nextCol < cols && !visited[nextRow][nextCol]) {
                grid[cellRow * 2 + 1 + direction.x][cellCol * 2 + 1 + direction.y] = 0;
                carve(nextRow, nextCol, visited, random);
            }
        }
    }
}
