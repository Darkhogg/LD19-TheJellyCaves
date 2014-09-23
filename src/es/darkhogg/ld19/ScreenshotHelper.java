package es.darkhogg.ld19;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

public final class ScreenshotHelper {

    private static final DateFormat FORMAT_FILE = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    public static void saveScreenshot (final BufferedImage image) {
        final File saveTo = new File("SpaceRecycler_" + FORMAT_FILE.format(new Date()) + ".png");
        try {
            ImageIO.write(image, "png", saveTo);
            System.out.printf("Screenshot saved to '%s'%n", saveTo.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ScreenshotHelper () {
        throw new AssertionError();
    }

}
