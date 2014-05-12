package uk.ac.york.minesweeper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * A component which can display
 * a minefield graphically and handle various events.
 */
public class MinefieldPanel extends JComponent {

    /**
     * Logger.
     */
    private static final Logger
    LOGGER = Logger.getLogger(MinefieldPanel.class.getName());

    /**
     * ConsoleHandler.
     */
    private static final ConsoleHandler CH = new ConsoleHandler();

    /**
     * Version.
     */
    private static final long serialVersionUID = 1L;

    /** Size of all the tiles. */
    private static final int TILE_SIZE = 32;

    /** Width of the bevel. */
    private static final int BEVEL_WIDTH = 2;

    /** Font vertical offset (from top to BASELINE). */
    private static final int FONT_VOFFSET = 24;

    /** The font to draw numbers with. */
    private static final Font FONT = new Font(Font.MONOSPACED, Font.BOLD, 24);


    /** Default background colour. */
    private static final Color COLOUR_BACKGROUND = new Color(0xC0, 0xC0, 0xC0);

    /** Light grey for bevels. */
    private static final Color COLOUR_LIGHT = new Color(0xE0, 0xE0, 0xE0);

    /** Dark grey for bevels. */
    private static final Color COLOUR_DARK = new Color(0x80, 0x80, 0x80);

    /** Colour of question marks. */
    private static final Color COLOUR_QUESTION = Color.WHITE;

    /** The colours of the numbers (0 is unused). */
    private static final Color[] COLOUR_NUMBERS = new Color[]
    {
        null,                           // 0 = Unused
        new Color(0x00, 0x00, 0xFF),    // 1 = Blue
        new Color(0x00, 0x7F, 0x00),    // 2 = Green
        new Color(0xFF, 0x00, 0x00),    // 3 = Red
        new Color(0x2F, 0x2F, 0x9F),    // 4 = Dark Blue
        new Color(0x7F, 0x00, 0x00),    // 5 = Maroon
        new Color(0x9F, 0x9F, 0x2F),    // 6 = Turquoise
        new Color(0x00, 0x00, 0x00),    // 7 = Black
        new Color(0x7F, 0x7F, 0x7F),    // 8 = Grey
    };

    /** Current minefield. */
    private Minefield minefield;

    /** Currently selected tile (null most of the time). */
    private Point selectedTile;

    /** List of state change listeners. */
    private ArrayList<MinefieldStateChangeListener>
    listeners = new ArrayList<MinefieldStateChangeListener>();

    /**
     * Initializes a new MinefieldPanel with the given Minefield.
     *
     * There must always be a minefield to display (you cannot pass null)
     *
     * @param minefield minefield to display
     */
    public MinefieldPanel(final Minefield mineField) {
        this.addMouseListener(new MouseEventListener());
        this.setBackground(COLOUR_BACKGROUND);
        this.setOpaque(true);
        this.setFont(FONT);
        this.setMinefield(mineField);
    }

    /**
     * Adds a listener to which received game state change events.
     *
     * @param listener listener to add
     */
    public final void
    addStateChangeListener(final MinefieldStateChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);

        }

    }

    /**
     * Removes a listener which received game state change events.
     *
     * @param listener listener to remove
     */
    public final void
    removeStateChangeListener(final MinefieldStateChangeListener listener) {

        listeners.remove(listener);
    }

    /**
     * Fires the state changed event.
     */
    private void fireStateChangeEvent() {
        MinefieldStateChangeEvent event = new MinefieldStateChangeEvent(this);

        for (MinefieldStateChangeListener listener : listeners) {
            listener.stateChanged(event);
        }

    }

    /**
     * Gets the current minefield.
     *
     * @return current minefield
     */
    public final Minefield getMinefield() {
        return minefield;
    }

    /**
     * Sets a new minefield for the component.
     *
     * @param newMinefield the new minefield
     */
    public void setMinefield(Minefield newMinefield) {
        if (newMinefield == null) {

            throw new IllegalArgumentException("newMinefield cannot be null");
        }


        this.minefield = newMinefield;

        // Reset selected tile
        this.selectedTile = null;

        // Update all visuals
        this.setSize(getPreferredSize());
        this.repaint();

        // Fire event
        this.fireStateChangeEvent();

        CH.setFormatter(new OwnFormatter());

        LOGGER.addHandler(CH);

        LOGGER.info("MINENFELD initiiert.");

    }

    /**
     * Draws a character on a tile.
     *
     * @param g graphics object
     * @param x x position of top-left of tile
     * @param y y position of top-left of tile
     * @param c character to draw
     */
    private static void
    drawCharacter(final Graphics g, final int x, final int y, final char c) {
        // Get coordinates to draw at
        int drawX = x + (TILE_SIZE - g.getFontMetrics().charWidth(c)) / 2;
        int drawY = y + FONT_VOFFSET;

        // Draw the character
        g.drawChars(new char[] {c}, 0, 1, drawX, drawY);
    }

    /**
     * Draws an image at the given tile location.
     *
     * @param g graphics object
     * @param tileX x position of top-left of tile
     * @param tileY y position of top-left of tile
     * @param img image to draw
     */
    private static void drawImage(final Graphics g, final int tileX,
            final int tileY, final BufferedImage img) {
        int xOff = tileX + (TILE_SIZE - img.getWidth()) / 2;
        int yOff = tileY + (TILE_SIZE - img.getHeight()) / 2;

        g.drawImage(img, xOff, yOff, null);
    }


    @Override
    public final void paintComponent(final Graphics gOld) {
        Graphics2D g = (Graphics2D) gOld;
        int selectedX;
        int selectedY;
        if (selectedTile == null) {
            selectedX = -1;
        } else {
            selectedX = selectedTile.x;
        }
        if (selectedTile == null) {
            selectedY = -1;
        } else {
            selectedY = selectedTile.y;
        }
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        if (isOpaque()) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        for (int x = 0; x < minefield.getWidth(); x++) {
            for (int y = 0; y < minefield.getHeight(); y++) {
                int graphicsX1 = x * TILE_SIZE;
                int graphicsY1 = y * TILE_SIZE;
                g.setColor(COLOUR_DARK);
                g.drawLine(graphicsX1, graphicsY1,
                        graphicsX1 + TILE_SIZE, graphicsY1);
                g.drawLine(graphicsX1, graphicsY1,
                        graphicsX1, graphicsY1 + TILE_SIZE);
                if (minefield.getTileState(x, y) == TileState.UNCOVERED) {
                    int tileValue = minefield.getTileValue(x, y);
                    if (tileValue < 0) {
                        drawImage(g, graphicsX1, graphicsY1, Images.getMine());
                    } else if (tileValue > 0) {
                        g.setColor(COLOUR_NUMBERS[tileValue]);
                        drawCharacter(g, graphicsX1,
                                graphicsY1, (char) ('0' + tileValue));
                    }
                } else {
                    if (x != selectedX || y != selectedY) {
                        int bevelX2 = graphicsX1 + TILE_SIZE - BEVEL_WIDTH;
                        int bevelY2 = graphicsY1 + TILE_SIZE - BEVEL_WIDTH;
               g.setColor(COLOUR_LIGHT);
               g.fillRect(graphicsX1, graphicsY1, TILE_SIZE, BEVEL_WIDTH);
               g.fillRect(graphicsX1, graphicsY1, BEVEL_WIDTH, TILE_SIZE);
               g.setColor(COLOUR_DARK);
               g.fillRect(graphicsX1, bevelY2, TILE_SIZE, BEVEL_WIDTH);
               g.fillRect(bevelX2, graphicsY1, BEVEL_WIDTH, TILE_SIZE);
                    }
                    if (minefield.getTileState(x, y) == TileState.FLAGGED) {
                        drawImage(g, graphicsX1, graphicsY1, Images.getFlag());
                    } else if (minefield.getTileState(x, y)
                            == TileState.QUESTION) {
                        g.setColor(COLOUR_QUESTION);
                        drawCharacter(g, graphicsX1, graphicsY1, '?');
                    }
                }
            }
        }
    }

    @Override
    public final Dimension getPreferredSize() {
        return new Dimension(TILE_SIZE * minefield.getWidth(),
                             TILE_SIZE * minefield.getHeight());
    }

    @Override
    public final Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    public final Dimension getMinimumSize() {
        return getPreferredSize();
    }

    /**
     * Handles all mouse events within the game area.
     */
    private class MouseEventListener extends MouseAdapter {
        /**
         * Calculates the selected tile from a mouse event.
         * @return Point
         * @param e MouseEvent
         */
        private Point getTileFromEvent(final MouseEvent e) {
            return new Point(e.getX() / TILE_SIZE, e.getY() / TILE_SIZE);
        }

        @Override
        public void mouseExited(final MouseEvent e) {
            // Clear selected tile
            if (selectedTile != null) {
                selectedTile = null;
                repaint();
            }
        }

        @Override
        public void mousePressed(final MouseEvent e) {
            // Ignore if finished
            if (minefield.isFinished()) {
                return;

            }


            // Get tile position
            Point tile = getTileFromEvent(e);

            // Right or left click?
            if (SwingUtilities.isLeftMouseButton(e)) {
                // Do not select tiles with flags on
                if (minefield.getTileState(tile.x, tile.y)
                        == TileState.FLAGGED) {

                    return;
                }


                // Set new selected tile
                selectedTile = tile;
            } else if (SwingUtilities.isRightMouseButton(e)) {
                TileState newState;

                // Change flagged state
                switch(minefield.getTileState(tile.x, tile.y)) {
                    case COVERED:   newState = TileState.FLAGGED;   break;
                    case FLAGGED:   newState = TileState.QUESTION;  break;
                    case UNCOVERED: newState = TileState.UNCOVERED; break;
                    default:        newState = TileState.COVERED;   break;
                }

                minefield.setTileState(tile.x, tile.y, newState);
            }

            repaint();
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            // Ignore if finished
            if (minefield.isFinished()) {

                return;
            }


            // Ensure there was a tile selected
            if (selectedTile != null) {
                // Ensure the tile was the same as the one clicked on
                if (selectedTile.equals(getTileFromEvent(e))) {
                    // Either chord or uncover depending on the number of clicks
                    GameState state = minefield.getGameState();

                    if (e.getClickCount() == 2) {

                         minefield.chord(selectedTile.x, selectedTile.y);
                    } else if (e.getClickCount() == 1) {

                        minefield.uncover(selectedTile.x, selectedTile.y);
                    }


                    // Fire state changed event if needed
                    if (minefield.getGameState() != state) {

                         fireStateChangeEvent();
                    }

                }

                // Clear selected tile
                selectedTile = null;
                repaint();
            }
        }
    }
}
