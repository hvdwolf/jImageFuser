package org.hvdw.jimagefuser.controllers;

import org.hvdw.jimagefuser.MyVariables;
import org.hvdw.jimagefuser.Utils;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SavedFusedImage {
    private final static ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(SavedFusedImage.class);

    HashMap<String, String> parameters = MyVariables.getParameters();

    /**
     * This method shows the preview in the bigger right pane in the JLabel
     * @param lblPreview
     */
    public static void displayPreview(JLabel lblPreview) {
        File fusedImage = new File(MyVariables.getFusedImage());
        if (fusedImage.exists()) {
            try {
                BufferedImage img = ImageIO.read(fusedImage);
                BufferedImage resizedImage = ImageFunctions.scaleImageToContainer(img, 700, 700);
                ImageIcon icon = new ImageIcon(resizedImage);
                lblPreview.setIcon(icon);
            } catch (IOException e) {
                logger.error("Error reading fused preview");
                e.printStackTrace();
            }
        }
    }


    /**
     * Convert a file object to the "preview"  version of the file and return as string
     * @param file
     * @return
     */
    private static String previewFileString(File file) {
        String previewfilestring = "";
        String tmpfolder = MyVariables.gettmpWorkFolder();
        try {
        String filestring = file.getCanonicalPath();
        //logger.info("filestring {}", filestring);
        String filename = file.getName();
        int strlastindex = filename.lastIndexOf(".");
        //logger.info("filename {} filename.substring(0, (strlastindex-1)) {}", filename, filename.substring(0, strlastindex) );
        // preview images (and thumbnails) always get a lowercase file extension
        previewfilestring = tmpfolder + File.separator + filename.substring(0, strlastindex) + "_PreviewImage" + filename.substring(strlastindex).toLowerCase();
        //logger.info("requested Preview image {}", previewfilestring);

        } catch (IOException e) {
            logger.error("Error reading canonical path {}", e.toString());
            e.printStackTrace();
        }
        return previewfilestring;
    }

    private static List<String> getEnfuseParameters() {
        List<String> enfparams = new ArrayList<String>();

        // First get some data
        HashMap<String, String> parameters = MyVariables.getParameters();
        String strFusedImage = MyVariables.getFusedImage();
        String strFusedImageExtension = Utils.getFileExtension(strFusedImage);

        //now we get a boring list of "if"s
        if ("jpg".equals(strFusedImageExtension.toLowerCase()) || "jpeg".equals(strFusedImageExtension.toLowerCase())) {
            enfparams.add("--compression=" + parameters.get("spnrJpeg"));
        } else if ("tif".equals(strFusedImageExtension.toLowerCase()) || "tiff".equals(strFusedImageExtension.toLowerCase())) {
            enfparams.add("--compression=" + parameters.get("cmbBoxTiff"));
        }
        // PNG is automatically saved with DEFLATE

        if (("false").equals(parameters.get("cmbBoxTiff"))) {
            enfparams.add("--levels=" + (parameters.get("spnrLevels")).trim());
        }
        if ("true".equals(parameters.get("hardMaskcheckBox"))) {
            enfparams.add("--hard-mask");
        }
        if ("true".equals(parameters.get("CIECAM02CheckBox"))) {
            enfparams.add("-c");
        }
        /*if ("true".equals(parameters.get("blendAcrosscheckBox"))) {
            enfparams.add("")
        }*/
        enfparams.add("--exposure-weight=" + parameters.get("spnrExposure"));
        enfparams.add("--saturation-weight=" + parameters.get("spnrSaturation"));
        enfparams.add("--contrast-weight=" + parameters.get("spnrContrast"));
        enfparams.add("--entropy-weight=" + parameters.get("spnrEntropy"));
        enfparams.add("--exposure-mu=" + parameters.get("spnrwExposureOptimum"));  //used to be --wExposureMU
        enfparams.add("--exposure-sigma=" + parameters.get("spnrwExposureWidth"));    // used to be --wExposureSigma

        return enfparams;
    }

    /**
     * This method aligns the images before they are processed by enfuse
     * Alternatively: it can be used for users only wanting to align the image before "other"  processing
     * @param imagestype
     * @param progressBar
     */
    public synchronized static void alignImages(String imagestype, JProgressBar progressBar) {
        String result = "";
        List<String> cmdparams = new ArrayList<String>();
        File[] loadedFiles = MyVariables.getLoadedFiles();
        String tmpfolder = MyVariables.gettmpWorkFolder();
        int noOfFiles = loadedFiles.length;
        String checkFile = "";

        cmdparams.clear();
        cmdparams.add("align_image_stack");
        cmdparams.add("-l");
        cmdparams.add("--gpu");
        // method to read all AIS parameters
        // cmdparams.addAll(getAisParameters());
        cmdparams.add("-a");
        if ("full".equals(imagestype)) {
            checkFile = tmpfolder + File.separator + "ais_001000" + String.valueOf(noOfFiles-1) +".tif";
            StandardFileIO.deleteSameFilesInTmp("ais_001");
            cmdparams.add(tmpfolder + File.separator + "ais_001");
            for (File file : loadedFiles) {
                try {
                    cmdparams.add(file.getCanonicalPath());
                } catch (IOException e) {
                    logger.error("Error reading canonical path {}", e.toString());
                    e.printStackTrace();
                }
            }
        } else {
            checkFile = tmpfolder + File.separator + "ais_pre_001000" + String.valueOf(noOfFiles-1) +".tif";
            StandardFileIO.deleteSameFilesInTmp("ais_pre_001");
            cmdparams.add(tmpfolder + File.separator + "ais_pre_001");
            for (File file : loadedFiles) {
                cmdparams.add(previewFileString(file));
            }
        }
        CommandRunner.runCommandWithProgressBar(cmdparams, progressBar, false);

        // As the command runs ins the background to free the Gui, we need a trigger to know hen to continue
        File aisCheckFile = new File(checkFile);
        Long fileSize = aisCheckFile.length();
        // But we also need a trigger in case the app hangs or runs into an error
        long start = System.currentTimeMillis();

        boolean aisReady = false;
        while (!aisReady) {
            if (!(fileSize == null) && aisCheckFile.exists() && (fileSize > 4096)) {
                try {
                    Thread.sleep(250);
                    logger.info("fused image exists");
                    aisReady = true;
                } catch (InterruptedException e) {
                    logger.error("sleep fails {}", e.toString());
                    e.printStackTrace();
                }
            } else {
                Long current = System.currentTimeMillis();
                long elapsed = current - start;
                if (elapsed > 50000) { //ais takes long. On big images this can easily be 25 seconds
                    result = "too long";
                    aisReady = true;
                }
            }
        }
    }

    public synchronized static String enfuseImages(String imagestype, JProgressBar progressBar, JLabel OutputLabel) {
        String result = "";
        List<String> cmdparams = new ArrayList<String>();
        //String fusedImage = "";
        String strFusedImage = "";

        // First get some data
        HashMap<String, String> parameters = MyVariables.getParameters();
        //logger.debug("parameters {}", parameters.toString());
        File[] loadedFiles = MyVariables.getLoadedFiles();
        int noOfFiles = loadedFiles.length;
        //logger.info("files {}", Arrays.toString(loadedFiles));
        String tmpfolder = MyVariables.gettmpWorkFolder();
        //logger.info("tmpfolder {}", tmpfolder);

        cmdparams.clear();
        cmdparams.add("enfuse");
        cmdparams.add("-o");

        if ("full".equals(imagestype)) {
            strFusedImage = MyVariables.getFusedImage();
            //tmpFusedImage= tmpfolder + File.separator + "enfuse_output.jpg";
            cmdparams.add(strFusedImage);
            //cmdparams.add("--compression=" + parameters.get("spnrJpeg"));
            if ("true".equals(parameters.get("AIScheckBox"))) {
                logger.info("aligning the input images for our final image");
                OutputLabel.setText("aligning the input images for our final image");
                SavedFusedImage.alignImages("full", progressBar);
                for (int i = 0; i < (noOfFiles); i++) {
                    cmdparams.add(tmpfolder + File.separator + "ais_001000" + String.valueOf(i) + ".tif");
                }
                logger.info("Now (en)fusing the image after alignment");
                OutputLabel.setText("Now (en)fusing the image after alignment");
            } else {
                logger.info("(en)fuse the image without aligning");
                OutputLabel.setText("(en)fuse the image without aligning");
                for (File file : loadedFiles) {
                    try {
                        cmdparams.add(file.getCanonicalPath());
                    } catch (IOException e) {
                        logger.error("Error converting file object {}: {}", file.getName(), e.toString());
                        e.printStackTrace();
                    }
                }
            }
            // method to read all enfuse parameters
            cmdparams.addAll(getEnfuseParameters());
        } else if ("preview".equals(imagestype)) {
            // Create the first preview after loading the images
            strFusedImage = tmpfolder + File.separator + "enfuse_pre_output.jpg";
            cmdparams.add(strFusedImage);
            cmdparams.add("--compression=" + parameters.get("spnrJpeg"));
            for (File file : loadedFiles) {
                //logger.info("file {}", file.getPath());
                cmdparams.add(previewFileString(file));
            }
        } else if ("alignedpreview".equals(imagestype)) {
            // The preview has already been aligned by the alignImages method
            strFusedImage = tmpfolder + File.separator + "enfuse_pre_output.jpg";
            cmdparams.add(strFusedImage);
            cmdparams.add("--compression=" + parameters.get("spnrJpeg"));
            cmdparams.add(tmpfolder + File.separator + "ais_pre_001*.tif");
        }
        logger.debug("enfuse command string {}", cmdparams.toString());

        try {
            result = CommandRunner.runCommand(cmdparams);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // As the command runs in the background to free the Gui we need a trigger to know when to continue
        File fusedImage = new File(strFusedImage);
        Long fileSize = fusedImage.length();
        // But we also need a trigger in case the app hangs or runs into an error
        long start = System.currentTimeMillis();

        boolean fusedImageExists = false;
        while (!fusedImageExists) {
            fileSize = fusedImage.length();
            if (!(fileSize == null) && fusedImage.exists() && (fileSize > 4096)) {
                try {
                    Thread.sleep(250);
                    logger.info("fused image exists");
                    MyVariables.setFusedImage(strFusedImage);
                    result = strFusedImage;
                    fusedImageExists = true;
                    OutputLabel.setText("");
                } catch (InterruptedException e) {
                    logger.error("sleep fails {}", e.toString());
                    e.printStackTrace();
                }
            } else {
                Long current = System.currentTimeMillis();
                long elapsed = current - start;
                if (elapsed > 10000) {
                    result = "too long";
                    OutputLabel.setText("This took too long. Aborting.");
                    fusedImageExists = true;
                }
            }
        }

        OutputLabel.setText("");
        return result;
    }

    /**
     * This is the overall method that calls the alignImages method (when requested by the user)
     * and it calls enfuseImages method.
     * @param rootPanel
     * @param OutputLabel
     * @param progressBar
     * @return
     */
    public static String SaveImage(JPanel rootPanel, JLabel OutputLabel, JProgressBar progressBar) {
        String result = "";
        List<String> cmdparams = new ArrayList<String>();

        // First get some data
        HashMap<String, String> parameters = MyVariables.getParameters();
        File[] loadedFiles = MyVariables.getLoadedFiles();
        int[] selectedFilenamesIndices = MyVariables.getSelectedFilenamesIndices();
        File savetofile = MyVariables.getSaveToFile();

        if (!(savetofile == null)) {
            if("true".equals(parameters.get("AIScheckBox"))) {
                String imagestype = parameters.get("imagetypes");
                alignImages(imagestype, progressBar);
            }
            logger.debug("Selected output file {}", savetofile.getPath());
            if (savetofile.getName().toLowerCase().endsWith(".jpg")) {
                // User wants to write a jpeg
            } else if (savetofile.getName().toLowerCase().endsWith(".png")) {
                // User wants to write a png
            } else if (savetofile.getName().toLowerCase().endsWith(".tif")) {
                // User wants to write a tif
            } else {
                // Due to previous checks we should never get here, but you never know
                JOptionPane.showMessageDialog(rootPanel, "Only jpg, png and tif formats are supported", "Unsupported format", JOptionPane.WARNING_MESSAGE);
            }
        }
        return result;
    }
}
