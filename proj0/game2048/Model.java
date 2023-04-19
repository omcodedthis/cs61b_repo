package game2048;

import javax.swing.border.Border;
import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author om
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true if this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */
    public boolean tilt(Side side) {
        board.setViewingPerspective(side);

        boolean changed;
        changed = false;

        for (int c = 3; c >= 0; c--) {
            if (changed) {
                moveColumn(c);
            }
            else {
                changed = moveColumn(c);
            }
        }
        board.setViewingPerspective(side.NORTH);

        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    /** Moves the tiles in a given column. Returns true if a move occurs. */
    public boolean moveColumn(int c) {
        boolean colChanged;

        colChanged = checkForMerges(c);

        return colChanged;
    }

    /** Checks if a merge of two tiles of the same value can occur. Returns true if a merge occurs. */
    public boolean checkForMerges(int c) {
        boolean moveOccured = false;

        int totalTiles = findTotalTiles(c);

        // if no tiles with values in a given row, false is returned as no move can occur.
        if (totalTiles == 0) {
            return moveOccured;
        }

        Tile[] tilesInSameCol = new Tile[totalTiles];
        makeArrayofTiles(tilesInSameCol, totalTiles, c);
        int totalIndex = totalTiles - 1;

        // if the column only has one tile with a value, it is moved to the edge in the direction of the tilt.
        if (totalTiles == 1) {
            Tile onlyOneTile = tilesInSameCol[0];

            board.move(c, 3, onlyOneTile);
            moveOccured = true;

            return moveOccured;
        }

        // moves the tiles in the tilesInSameCol array.
        moveOccured = merge(tilesInSameCol, totalIndex, c);

        clearSpacesBetweenTiles(c);

        return moveOccured;
    }

    /** Returns the total number of tiles in a given column. */
    public int findTotalTiles(int c) {
        int totalTiles = 0;

        for (int k = 0; k < board.size(); k++) {
            if (board.tile(c, k) == null) {
                continue;
            }
            else {
                totalTiles += 1;
            }
        }
        return totalTiles;
    }

    /** Add tiles with values in a given column to an array, tilesInSameCol. */
    public void makeArrayofTiles(Tile[] tilesInSameCol, int totalTiles, int c) {
        int totalIndex = totalTiles - 1;
        int cnt = 0;
        for (int r = 3; r >= 0; r--) {
            if (board.tile(c, r) == null) {
                continue;
            }

            tilesInSameCol[totalIndex - cnt] = board.tile(c, r);
            cnt++;
        }
    }

    /** Merges the tiles in the tilesInSameCol array. Returns true if a move occurs. */
    public boolean merge(Tile[] tilesInSameCol, int totalIndex, int c) {
        boolean moveOccured = false;

        for (int i = totalIndex; i >= 0; i--) {
            if (i == 0) {
                break;
            }

            Tile topTile = tilesInSameCol[i];
            Tile bottomTile = tilesInSameCol[i - 1];

            // if the two tiles have the same value, bottomTile is moved to the same location as the topTile.
            if (topTile.value() == bottomTile.value()) {
                boolean tilesMerge = board.move(c, topTile.row(), bottomTile);
                moveOccured = true;

                if (tilesMerge) {
                    score += (topTile.value()) * 2;
                }
            }

            // else the bottomTile is moved just below the topTile, preventing a merge from occurring.
            else {
                int newRow = validIndex(topTile.row());
                board.move(c, newRow, bottomTile);
                moveOccured = true;
            }
        }

        return moveOccured;
    }


    /** Removes the spaces between tiles for a given column. */
    public void clearSpacesBetweenTiles(int c) {
        for (int r = 3; r >= 0; r--) {
            Tile currentTile = board.tile(c, r);

            if (currentTile == null) {
                continue;
            }

            int newRow = currentTile.row() + 1;

            if (newRow >= board.size()) {
                continue;
            }

            while (board.tile(c, newRow) == null) {
                newRow++;

                if (newRow >= board.size()) {
                    break;
                }
            }

            boolean tilesMerge = board.move(c, newRow - 1, currentTile);
            if (tilesMerge) {
                score += currentTile.value() * 2;
            }
        }
    }

    /** Ensures that the tile's new index is valid. */
    public int validIndex(int index) {
        int newIndex = index - 1;

        if (newIndex >= board.size()) {
            newIndex = board.size() - 1;
        }

        else if (newIndex < 0) {
            newIndex = 0;
        }
        return newIndex;
    }

    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        int boardSize = b.size();

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                Tile currentTile = b.tile(i, j);

                if (currentTile == null) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        int boardSize = b.size();

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j <boardSize; j++) {
                Tile currentTile = b.tile(i, j);

                if (currentTile == null) {
                    continue;
                }

                if (currentTile.value() == MAX_PIECE) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
       if (emptySpaceExists(b)) {
           return true;
       }

       else if (sameAdjacentTiles(b)) {
           return true;
       }

       else {
           return false;
       }
    }

    /** Returns true if there are two adjacent tiles with the same value. */
    public static boolean sameAdjacentTiles(Board b) {
        int boardSize = b.size();

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                Tile currentTile = b.tile(i, j);

                if (checkSameTiles(b, currentTile)) {
                    return true;
                }
            }
        }

        return false;
    }

    /** Checks the adjacent tiles for the same value. */
    public static boolean checkSameTiles(Board b, Tile t) {
        int boardSize = b.size();

        int tCol = t.col();
        int tRow = t.row();

        // checks for adjacent tiles within the same column.
        for (int i = -1; i < 2; i++) {
            if (validIndex(tCol, i, boardSize)) {
                int tileValue = t.value();

                int comparingValue = b.tile(tCol + i, tRow).value();

                if (tileValue == comparingValue) {
                    return true;
                }
            }
        }

        // checks for adjacent tiles within the same row.
        for (int j = -1; j < 2; j++) {
            if (validIndex(tRow, j, boardSize)) {
                int tileValue = t.value();

                int comparingValue = b.tile(tCol, tRow + j).value();

                if (tileValue == comparingValue) {
                    return true;
                }
            }
        }

        return false;
    }

    /** Returns true if the index is between 0 <= index < boardSize. */
    public static boolean validIndex(int tIndex, int k, int boardSize) {
        int index = tIndex + k;

        if (index == tIndex) {
            return false;
        }

        else if (0 <= index && index < boardSize) {
            return true;
        }

        else {
            return false;
        }

    }

    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
