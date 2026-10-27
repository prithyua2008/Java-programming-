import java.io.*;
import java.util.*;

public class StoneGravityRotation {

    // Let every '*' fall to the bottom of its column.
    static char[][] dropStones(char[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        for (int c = 0; c < cols; c++) {
            int stones = 0;
            for (int r = 0; r < rows; r++) {
                if (grid[r][c] == '*') stones++;
            }
            for (int r = 0; r < rows; r++) {
                grid[r][c] = (r >= rows - stones) ? '*' : '.';
            }
        }
        return grid;
    }

    // Rotate 90 degrees clockwise.
    static char[][] rotateCW(char[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        char[][] out = new char[cols][rows];

        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                out[c][r] = grid[rows - 1 - r][c];
            }
        }
        return out;
    }

    // Rotate 90 degrees counter-clockwise.
    static char[][] rotateCCW(char[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        char[][] out = new char[cols][rows];

        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                out[c][r] = grid[r][cols - 1 - c];
            }
        }
        return out;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder all = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            all.append(line).append(' ');
        }

        StringTokenizer tok = new StringTokenizer(all.toString());
        if (!tok.hasMoreTokens()) return;

        int m = Integer.parseInt(tok.nextToken());
        int n = Integer.parseInt(tok.nextToken());

        char[][] grid = new char[m][n];
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                grid[r][c] = tok.nextToken().charAt(0);
            }
        }

        grid = dropStones(grid);

        int k = Integer.parseInt(tok.nextToken());
        for (int i = 0; i < k; i++) {
            String move = tok.nextToken().toLowerCase();

            if (move.equals("right")) {
                grid = rotateCW(grid);
            } else if (move.equals("left")) {
                grid = rotateCCW(grid);
            }

            grid = dropStones(grid);
        }

        StringBuilder sb = new StringBuilder();
        for (char[] row : grid) {
            for (int c = 0; c < row.length; c++) {
                if (c > 0) sb.append(' ');
                sb.append(row[c]);
            }
            sb.append('\n');
        }
        System.out.print(sb);
    }
}
