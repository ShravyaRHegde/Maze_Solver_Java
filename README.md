# Maze Solver Java

## Build and run

Compile with the standard JDK (no external dependencies):

```powershell
javac *.java
java MazeGameFrame
```

## Controls and rules

Start at the green circle and reach the red circle. Dark cells are walls, white cells are open, and light-yellow cells show places you have visited. Move with the arrow keys or **WASD**. The Moves and Time labels update while playing.

Use **Hint** to reveal just the next cell on a correct route. **Show Shortest Path** displays the overall pure-BFS route from the maze start to the goal, while **Show Closest Solution** uses the distinct BFS mode biased toward your visited trail from your current position. Choose Small (10x10), Medium (20x20), or Large (30x30) before starting a New Game. Starting a new game confirms before discarding progress.

## Points

You can earn up to 1000 points. Your score falls as your moves exceed the maze's overall shortest route: taking the optimal number of moves earns the maximum score. Every hint used deducts 50 points; scores cannot drop below zero.
