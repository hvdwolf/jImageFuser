package org.hvdw.jimagefuser;

import java.awt.Font;

public class MyConstants {
    public static final String MY_DATA_FOLDER = "jimagefuser_data";
    public static final String[] SUPPORTED_IMAGES = {"jpg", "jpeg", "png", "tif", "tiff"};
    public static final String[] BASIC_IMG_DATA = {"-n", "-S", "-imagewidth", "-imageheight", "-orientation", "-iso", "-fnumber", "-exposuretime", "-focallength", "-focallengthin35mmformat", "-exposurecompensation"};

    // Default font
    public static final Font appdefFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
}
