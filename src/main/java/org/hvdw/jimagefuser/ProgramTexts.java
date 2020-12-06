package org.hvdw.jimagefuser;

public class ProgramTexts {
    /* HTML in Swing components follows the HTML 3.2 standard from 1996. See https://www.w3.org/TR/2018/SPSD-html32-20180315/
    All strings use "internal" tags, but not begin and end tags as we use the String.format(ProgramTexts.HTML, <width>, helptext)
     */
    public static final String Author = "Harry van der Wolf";
    public static final String ProjectWebSite = "http://hvdwolf.github.io/jImageFuser";
    public static final String Version = "0.99.0";
    public static final String HTML = "<html><body style='width: %1spx'>%1s";
    public static final String cancelledETlocatefromStartup = "<html>You cancelled providing the location of exiftool.<br>I will now exit jImageFuser.</html>";
    public static final String downloadInstallET = "I will now open the ExifTool website in your browser and then close jImageFuser.<br>"
            +"After having downloaded and installed ExifTool you can reopen jImageFuser.<br><br>If ExifTool is in your PATH, jImageFuser will simply continue.<br><br>"
            +"If ExifTool is NOT in your PATH, you need to specify the location where you installed ExifTool.";
    public static final String wrongETbinaryfromStartup = "<html>This is not the exiftool executable/binary.<br>I can't use it and will now exit jImageFuser.</html>";
}
