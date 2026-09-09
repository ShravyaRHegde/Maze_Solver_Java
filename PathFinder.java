import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/** Finds maze paths using breadth-first search, including a user-trail-biased mode. */
public class PathFinder {
    /** Returns the pure shortest path from start to end using BFS. */
    public List<Point> findShortestPath(Maze maze, Point start, Point end) {
        return bfs(maze, start, end, null, false);
    }

    /** Returns a BFS path whose equal-depth exploration is biased toward the supplied trail. */
    public List<Point> findTrailBiasedPath(Maze maze, Point start, Point end, boolean[][] userTrail) {
        return bfs(maze, start, end, userTrail, true);
    }

    /** Returns the next cell on a shortest path from start to end, or null if none is available. */
    public Point findNextStep(Maze maze, Point start, Point end) {
        List<Point> path = findShortestPath(maze, start, end);
        return path.size() > 1 ? new Point(path.get(1)) : null;
    }

    private List<Point> bfs(Maze maze, Point start, Point end, boolean[][] userTrail, boolean biasToUser) {
        List<Point> result = new ArrayList<>();
        if (maze == null || start == null || end == null || !maze.isOpen(start.x, start.y) || !maze.isOpen(end.x, end.y)) return result;
        boolean[][] visited = new boolean[maze.getRows()][maze.getCols()];
        Point[][] parent = new Point[maze.getRows()][maze.getCols()];
        Queue<Point> q = new LinkedList<>();
        q.add(new Point(start));
        visited[start.x][start.y] = true;
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        while (!q.isEmpty()) {
            Point p = q.poll();
            if (p.x == end.x && p.y == end.y) break;
            List<Point> neighbors = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                int nx = p.x + dx[i], ny = p.y + dy[i];
                if (nx >= 0 && ny >= 0 && nx < maze.getRows() && ny < maze.getCols() && maze.isOpen(nx, ny) && !visited[nx][ny]) {
                    neighbors.add(new Point(nx, ny));
                }
            }
            if (biasToUser) neighbors.sort(Comparator.comparingInt(n -> distanceToTrail(n.x, n.y, userTrail)));
            for (Point n : neighbors) {
                visited[n.x][n.y] = true;
                parent[n.x][n.y] = p;
                q.add(n);
            }
        }
        if (!visited[end.x][end.y]) return result;
        Point cur = new Point(end);
        while (cur != null) { result.add(cur); cur = parent[cur.x][cur.y]; }
        Collections.reverse(result);
        return result;
    }

    private int distanceToTrail(int x, int y, boolean[][] userTrail) {
        if (userTrail == null) return 100;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < userTrail.length; i++)
            for (int j = 0; j < userTrail[i].length; j++)
                if (userTrail[i][j]) min = Math.min(min, Math.abs(x - i) + Math.abs(y - j));
        return min == Integer.MAX_VALUE ? 100 : min;
    }
}
