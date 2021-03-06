package uk.ac.york.minesweeper;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

/**
 * Static class containing the game's images.
 */
public final class Images {

    /**
     * Logger für Images.
     */
    private static final Logger
    LOGGER = Logger.getLogger(Images.class.getName());

    /**
     * Der Handler für den Logger.
     */
    private static final ConsoleHandler HANDLER = new ConsoleHandler();

    /** Resources directory (beginning and ending with forward slash). */
    private static final String RES_DIRECTORY = "/res/";

    /** Image of a sea mine. */
    private static final BufferedImage MINE = loadImageResource("mine.png");

    /** Image of a generic flag. */
    private static final BufferedImage FLAG = loadImageResource("flag.png");

    /** Image of a normal face. */
    private static final BufferedImage
    FACE_NORMAL = loadImageResource("default.png");

    /** Image of a face when you win. */
    private static final BufferedImage FACE_WON = loadImageResource("won.png");

    /** Image of a face when you lose. */
   private static final BufferedImage FACE_LOST = loadImageResource("lost.png");

    private Images() {

    }

    public static BufferedImage getMine() {
        return MINE;
    }

    public static BufferedImage getFlag() {
        return FLAG;
    }

    public static BufferedImage getFaceNormal() {
        return FACE_NORMAL;
    }

    public static BufferedImage getFaceWon() {
        return FACE_WON;
    }

    public static BufferedImage getFaceLost() {
        return FACE_LOST;
    }
    /**
     * Loads an image from the resources directory.
     *
     * @param name image name
     * @return the loaded image
     */
    private static BufferedImage loadImageResource(final String name) {


        try (InputStream imgStream =
                Images.class.getResourceAsStream(RES_DIRECTORY + name)) {
            HANDLER.setFormatter(new OwnFormatter());
            LOGGER.addHandler(HANDLER);

            // Decompress image
            return ImageIO.read(imgStream);
        }
        catch (IOException e) {

            LOGGER.severe("FEHLER BEIM LADEN DES BILDES.");

            throw new RuntimeException("Could not load image file: " + name, e);
        }
    }

}
