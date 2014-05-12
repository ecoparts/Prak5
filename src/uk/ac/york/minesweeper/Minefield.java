package uk.ac.york.minesweeper;

import java.util.Arrays;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

    /**
     * Class containing the game data for the minesweeper game.
     */
public class Minefield {

    /**
     * Logger.
     */
    private static final Logger
    LOGGER = Logger.getLogger(Minefield.class.getName());

    /**
     * Handler.
         */
    private static final ConsoleHandler HANDLER = new ConsoleHandler();

    /** Tile processor for initValues which
    * increments a tile's value if it is not a mine. */
        private final SurroundingTilesProcessor
        PROCESSOR_INIT_VALUES = new SurroundingTilesProcessor() {
            @Override
            public void process(final int x, final int y) {
                // Increment values which are not mines
                if (valuesArray[x][y] >= 0) {

                    valuesArray[x][y]++;
                }

            }
        };

    /** Tile processor for chord which uncovers a tile if it is not flagged. */
    private final SurroundingTilesProcessor
    PROCESSOR_CHORD = new SurroundingTilesProcessor()
    {
        @Override
        public void process(final int x, final int y) {
            // Uncover non-flagged tiles
            if (stateArray[x][y] != TileState.FLAGGED) {

                uncoverNoChecks(x, y);
            }

        }
    };

    /** Tile processor for uncoverNoChecks which uncovers a tile immediately. */
    private final SurroundingTilesProcessor
    PROCESSOR_UNCOVER = new SurroundingTilesProcessor()
    {
        @Override
        public final void process(final int x, final int y) {
            uncoverNoChecks(x, y);
        }
    };

    /**
     * Array für die Werte.
     */
    // Array containing tile values (-1 = mine)
    private final byte[][] valuesArray;


    /**
     * Array für die Zustände.
     */
    // Array containing tile states
    private final TileState[][] stateArray;

    /**
     * Anzahl der Minen.
     *
     */
    // Number of mines
    private final int mines;

    /**
     *
     * Felder die übrig sind.
     */
    // Number of extra tiles which need to uncovered to win
    private int tilesLeft;

    /**
     * Entdeckt die Minen, falls das Spiel endet.
     */
    // If true, uncovers mines when the game finishes
    private boolean uncoverMinesAtEnd = true;

    /**
     *
     * Spielzustand.
     *
     */
    // State of the game
    private GameState gameState = GameState.NOT_STARTED;

    /**
     * Initializes a new Minefield class with the given properties.
     *
     * The mine locations are not allocated until the first click is made
     *
     * @param width width of the minefield in tiles
     * @param height height of the minefield in tiles
     * @param minen number of mines
     */
    public Minefield(final int width, final int height, final int minen) {

        int tilesLinks = (width * height) - minen;

        // Validate arguments
        if (width < 1 || height < 1 || minen < 0) {

            throw new IllegalArgumentException("invalid minefield dimensions");
        }


        if (tilesLinks <= 0) {

            throw new IllegalArgumentException("too many mines");
        }


        // Save initial properties
        this.mines = minen;
        this.tilesLeft = tilesLinks;

        // Create arrays (empty + covered)
        TileState[][] zustandsArray = new TileState[width][height];

        for (int x = 0; x < width; x++) {

            Arrays.fill(zustandsArray[x], TileState.COVERED);

        }

        this.stateArray = zustandsArray;
        this.valuesArray = new byte[width][height];
    }

    /**
     * Gets the width of the minefield in tiles.
     *
     * @return width of the minefield
     */
   public final int getWidth() {
        return valuesArray.length;
    }

    /**
     * Gets the height of the minefield in tiles.
     *
     * @return height of the minefield
     */
    public final int getHeight() {
        return valuesArray[0].length;
    }

    /**
     * Gets the total number of mines in the minefield.
     *
     * @return total number of mines
     */
    public final int getMines() {
        return mines;
    }

    /**
     * Gets a value which is true if
     * all mines are uncovered at the end of the game.
     *
     * @return true if mines are uncovered at the end
     */
    public final boolean isUncoveringMinesAtEnd() {
        return uncoverMinesAtEnd;
    }

    /**
     * Sets a value determining whether
     * mines are uncovered at the end of the game.
     *
     * @param uncoverMinesatEnd true if mines are uncovered at the end
     */
    public final void setUncoverMinesAtEnd(final boolean uncoverMinesatEnd) {
        this.uncoverMinesAtEnd = uncoverMinesatEnd;
    }

    /**
     * Gets the current state of the game.
     *
     * @return the state of the game
     */
    public final GameState getGameState() {
        return gameState;
    }

    /**
     * Returns true if the game has finished.
     *
     * @return true if the game has finished
     */
    public final boolean isFinished() {

        return gameState != GameState.RUNNING
               && gameState != GameState.NOT_STARTED;
    }

    /**
     * Gets the value of the given tile (mine / surrounding mines).
     *
     * This should only be called AFTER
     * the first tile is clicked (or when a tile is uncovered).
     *
     * @param x x position of tile
     * @param y y position of tile
     * @return value of that tile (-1 = mine)
     */
    public final int getTileValue(final int x, final int y) {
        if (gameState == GameState.NOT_STARTED) {

            throw new IllegalStateException("you must call "
                    + "uncover at least once before using getTileValue");
        }


        return valuesArray[x][y];
    }

    /**
     * Gets the state of the given tile.
     *
     * @param x x position of tile
     * @param y y position of tile
     * @return state of that tile
     */
    public final TileState getTileState(final int x, final int y) {
        return stateArray[x][y];
    }

    /**
     * Updates the state of the given tile
     *
     * Can be used to add flags and uncover tiles.
     * You cannot cover a tile that has already been uncovered.
     * If a tile is uncovered,
     * other tile states and the game state may be updated.
     *
     * @param x x position of tile
     * @param y y position of tile
     * @param newState the tile's new state
     */
    public final void setTileState(final int x,
             final int y, final TileState newState) {

        if (isFinished()) {

            throw new IllegalStateException("the game has finished");
        }


        switch (newState) {
            case COVERED:
            case FLAGGED:
            case QUESTION:
                // Set unless we're recovering a tile
                if (stateArray[x][y] == TileState.UNCOVERED) {

                    throw new UnsupportedOperationException("you cannot"
                            + " cover a tile once uncovered");
                }


                stateArray[x][y] = newState;
                break;

            case UNCOVERED:
                // Forward to uncover
                uncover(x, y);
                break;

            default:
                throw new IllegalArgumentException("newState is"
                        + " not a valid tile state");
        }
    }
/**
* Uncovers the tile at the given location.
*
* This method is equivalent
* to calling {@code setTileState(x, y, TileState.UNCOVERED)}
*
* @param x x position of tile
* @param y y position of tile
*/
    public final void uncover(final int x, final int y) {
        if (isFinished()) {

            throw new IllegalStateException("the game has finished");
        }


        // New game?
        if (gameState == GameState.NOT_STARTED) {
            initValues(x, y);
            gameState = GameState.RUNNING;
        }

        // Perform any uncovering
        uncoverNoChecks(x, y);
    }

    /**
* Uncovers the given tile and
* surrounding tiles without performing state checks.
*
* @param x x position of tile
* @param y y position of tile
*/
    private void uncoverNoChecks(final int x, final int y) {
        int width = getWidth();
        int height = getHeight();

        // Ignore if the tile does not exist / is already uncovered
        if (x < 0 || y < 0 || x >= width || y >= height) {

           return;
        }


        if (stateArray[x][y] == TileState.UNCOVERED) {

            return;
        }


        // Uncover this tile
        stateArray[x][y] = TileState.UNCOVERED;
        tilesLeft--;

        // Check for special tiles (0 and mines)
        if (valuesArray[x][y] == 0) {
            // Uncover all surrounding tiles
            processSurrounding(x, y, PROCESSOR_UNCOVER);
            } else if (valuesArray[x][y] < 0) {
            // Hit a mine
            gameState = GameState.LOST;
            uncoverAllMines();
            } else if (tilesLeft <= 0 && gameState == GameState.RUNNING) {
            // Uncovered all the non-mines!
            // The gameState check is required for
            //chording since you may hit a mine and then win.
            // later on the same move (which we don't want to overwrite)
            gameState = GameState.WON;
            uncoverAllMines();
        }
    }

    /**
* Uncovers all mines if uncoverMinesAtEnd is set
*
* This does not uncover correctly flagged mines, but sets incorrectly
* flagged mines to questions.
*/
    private void uncoverAllMines() {
        if (uncoverMinesAtEnd) {
            int width = getWidth();
            int height = getHeight();

            // Set state of all mines to uncovered
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (valuesArray[x][y] < 0) {
                        // Uncover if not flagged
                        if (stateArray[x][y] != TileState.FLAGGED) {

                            stateArray[x][y] = TileState.UNCOVERED;
                        }

                    } else {
                        // Set flags to questions
                        if (stateArray[x][y] == TileState.FLAGGED) {

                            stateArray[x][y] = TileState.QUESTION;
                        }

                    }
                }
            }
        }
    }
    /**
* Attempts to chord using the given central position
*
* Chording causes all the
* surrounding tiles to be uncovered if the number of.
* surrounding flags is equal to the value on the central tile.
*
* This method does nothing if the
* central tile is still covered of the number of
* surrounding flags in incorrect.
*
* @param x x position of central tile
* @param y y position of central tile
*/
    public final void chord(final int x, final int y) {
        if (isFinished()) {

            throw new IllegalStateException("the game has finished");
        }


        // Ensure the tile is uncovered
        if (stateArray[x][y] != TileState.UNCOVERED) {

            return;
        }


        // Check number of surrounding flags
        if (valuesArray[x][y] == countSurroundingFlags(x, y)) {
            // Uncover all surrounding tiles which are not flagged
            processSurrounding(x, y, PROCESSOR_CHORD);
        }
    }
    /**
* Initializes the values grid for a new game.
*
* startX and startY are used to
* prevent mines from appearing at the start location
*
* @param startX x position to prevent mines for
* @param startY y position to prevent mines for
*/
    private void initValues(final int startX, final int startY) {
        int width = getWidth();
        int height = getHeight();

        // Randomly place all the mines
        Random rnd = new Random();

        for (int i = 0; i < mines; i++) {
            int x, y;

            // Keep trying random positions until we've found an acceptable one
            do {
                x = rnd.nextInt(width);
                y = rnd.nextInt(height);
            }
            while(valuesArray[x][y] < 0 || (x == startX && y == startY));

            // Set as a mine
            valuesArray[x][y] = -1;

            // Increment number of mines in all surrounding tiles
            processSurrounding(x, y, PROCESSOR_INIT_VALUES);
        }
    }

    /**
* Counts the number of flags surrounding a position.
*
* @param x x position of central tile
* @param y y position of central tile
* @return number of surrounding flags
*/
    private int countSurroundingFlags(final int x, final int y) {
        int count = 0;
        int width = getWidth();
        int height = getHeight();

        if (y > 0) {
            if (x > 0) {

                if (stateArray[x - 1][y - 1] == TileState.FLAGGED) {
                    count++;
                }
            }
            if (stateArray[x ][y - 1] == TileState.FLAGGED) {
                                    count++;
                                }
            if (x < width - 1) {
                if (stateArray[x + 1][y - 1] == TileState.FLAGGED) {
                    count++;
                }
            }
        }

        if (x > 0) {
            if (stateArray[x - 1][y ] == TileState.FLAGGED) {
                count++;
            }
        }
        if (x < width - 1) {

           if (stateArray[x + 1][y ] == TileState.FLAGGED) {
               count++;
           }
        }

        if (y < height - 1) {
            if (x > 0) {
                if (stateArray[x - 1][y + 1] == TileState.FLAGGED) {
                    count++;
                }
            }
            if (stateArray[x ][y + 1] == TileState.FLAGGED) {
                count++;
                }
            if (x < width - 1) {
                if (stateArray[x + 1][y + 1] == TileState.FLAGGED) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
* Calls a function on all the surrounding tiles which exist.
*
* @param x x position of central tile
* @param y y position of central tile
*/
    private void processSurrounding(final int x,
            final int y, final SurroundingTilesProcessor processor) {
        int width = getWidth();
        int height = getHeight();

        if (y > 0) {
            if (x > 0) {
                processor.process(x - 1, y - 1);
            }
                                processor.process(x , y - 1);
            if (x < width - 1) {
                processor.process(x + 1, y - 1);
            }
        }

        if (x > 0) {
            processor.process(x - 1, y);
        }
        if (x < width - 1) {
            processor.process(x + 1, y);
        }

        if (y < height - 1) {
            if (x > 0) {
                processor.process(x - 1, y + 1);
            }
                                processor.process(x , y + 1);
            if (x < width - 1) {
                processor.process(x + 1, y + 1);
            }
        }
    }

    /**
* Gets a string representing the minefield's current visible state.
* @return String
*/
    @Override
    public final String toString() {
        int width = getWidth();
        int height = getHeight();
        StringBuilder builder = new StringBuilder();
        // Write top line
        builder.append('+');
        for (int x = 0; x < width; x++) {
            builder.append('-');
        }
        builder.append("+\n");
        // Write each line of the minefield
        for (int y = 0; y < height; y++) {
            builder.append('|');
            for (int x = 0; x < width; x++) {
                char c;
                // Handle each tile state
                switch (getTileState(x, y)) {
                    case COVERED:
                        c = '#';
                        break;

                    case FLAGGED:
                        c = 'f';
                        break;

                    case QUESTION:
                        c = '?';
                        break;

                    default:
                        // Show tile's value
                        int tileValue = getTileValue(x, y);

                        if (tileValue < 0) {
                            c = '!';
                        } else if (tileValue == 0) {
                            c = ' ';
                        } else {
                            c = (char) ('0' + tileValue);
                        }

                }

                builder.append(c);
            }

            builder.append("|\n");
        }

        // Write bottom line
        builder.append('+');
        for (int x = 0; x < width; x++) {

            builder.append('-');
        }

        builder.append("+\n");

        return builder.toString();
    }

    /**
* Interface used for processing surrounding tiles.
*/
    private interface SurroundingTilesProcessor {
        /**
* Processes the given tile (which is guaranteed to exist).
*
* @param x x position of tile
* @param y y position of tile
*/
      void process(int x, int y);
    }
}