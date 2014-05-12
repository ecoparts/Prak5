package uk.ac.york.minesweeper;

interface SurroundingProcessor {

    /**
     * Processes the given tile (which is guaranteed to exist).
     *
     * @param x x position of tile
     * @param y y position of tile
     */
    void process(int x, int y);

}
