package org.hvdw.jimagefuser.controllers;

import org.hvdw.jimagefuser.*;
import org.hvdw.jimagefuser.facades.IPreferencesFacade;
import org.hvdw.jimagefuser.facades.SystemPropertyFacade;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ResourceBundle;

import static org.hvdw.jimagefuser.Application.OS_NAMES.APPLE;
import static org.hvdw.jimagefuser.facades.IPreferencesFacade.PreferenceKey.*;
import static org.hvdw.jimagefuser.facades.IPreferencesFacade.PreferenceKey.LAST_OPENED_FOLDER;
import static org.hvdw.jimagefuser.facades.SystemPropertyFacade.SystemPropertyKey.USER_HOME;

public class StandardFileIO {

    private static IPreferencesFacade prefs = IPreferencesFacade.defaultInstance;
    private final static ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(StandardFileIO.class);


    public static String readTextFileAsString (String fileName) {
        // This will reference one line at a time
        String line = null;
        StringBuilder totalText = new StringBuilder("");

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                logger.debug(line);
                totalText.append(line);
            }

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            logger.debug("Unable to open file '{}'", fileName);
        }
        catch(IOException ex) {
            logger.debug("Error reading file '{}'", fileName);
        }

        return totalText.toString();
    }


    public static InputStream getResourceAsStream(String path) {
        return Utils.class.getClassLoader().getResourceAsStream(path);
    }

    // Reads a text file from resources
    public static String readTextFileAsStringFromResource(String fileName) {
        String strCurrentLine;
        String strFileContents = "";

        try {
            InputStream is = getResourceAsStream(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            strFileContents = "";
            while ((strCurrentLine = reader.readLine()) != null) {
                strFileContents += strCurrentLine + "\r\n";
            }
        } catch(FileNotFoundException ex) {
            logger.debug("Unable to open file '{}'", fileName);
        } catch(IOException ex) {
            logger.debug("Error reading file '{}'", fileName);
        }
        return strFileContents;
    }

    /* General check method which folder to open
     * Based on preference default folder, "always Use Last used folder" or home folder
     */
    public static String getFolderPathToOpenBasedOnPreferences() {

        boolean useLastOpenedFolder = prefs.getByKey(USE_LAST_OPENED_FOLDER, false);
        String lastOpenedFolder = prefs.getByKey(LAST_OPENED_FOLDER, "");
        String userHome = SystemPropertyFacade.getPropertyByKey(USER_HOME);

        String defaultStartFolder = prefs.getByKey(DEFAULT_START_FOLDER, "");

        //java_11 
        //String startFolder = !defaultStartFolder.isBlank() ? defaultStartFolder : userHome;
        // At least for time being use java_1.8
        String startFolder = !defaultStartFolder.isEmpty() ? defaultStartFolder : userHome;

        //java_11 
        //if (useLastOpenedFolder && !lastOpenedFolder.isBlank()) {
        // At least for time being use java_1.8
        if (useLastOpenedFolder && !lastOpenedFolder.isEmpty()) {
            startFolder = lastOpenedFolder;
        }
        return startFolder;
    }

    /*
     * Get the files from the "Load images" command  via JFilechooser
     */
    public static File[] getFileNames(JPanel myComponent) {
        File[] files = null;
        javax.swing.filechooser.FileFilter imgFilter;
        FileFilter imageFormats = null;
        FileSystemView fsv = FileSystemView.getFileSystemView();

        String userDefinedFilefilter = prefs.getByKey(USER_DEFINED_FILE_FILTER, "");
        String startFolder = getFolderPathToOpenBasedOnPreferences();
        logger.debug("startfolder for load images via JFilechooser {}", startFolder);

        final JFileChooser jchooser = new JFileChooser(startFolder, fsv);
        //final JFileChooser chooser = new NativeJFileChooser(startFolder);

        //FileFilter filter = new FileNameExtensionFilter("(images)", "jpg", "jpeg" , "png", "tif", "tiff");
        if (!"".equals(userDefinedFilefilter)) {
            String[] uDefFilefilter = userDefinedFilefilter.split(",");
            for (int i = 0; i < uDefFilefilter.length; i++)
                uDefFilefilter[i] = uDefFilefilter[i].trim();

            logger.trace("String userDefinedFilefilter {} ; String[] uDefFilefilter {}", userDefinedFilefilter, Arrays.toString(uDefFilefilter));
            imgFilter = new FileNameExtensionFilter(ResourceBundle.getBundle("translations/program_strings").getString("stfio.userdefinedfilter"), uDefFilefilter);
            imageFormats = new FileNameExtensionFilter(ResourceBundle.getBundle("translations/program_strings").getString("stfio.images"), MyConstants.SUPPORTED_IMAGES);
        } else {
            imgFilter = new FileNameExtensionFilter(ResourceBundle.getBundle("translations/program_strings").getString("stfio.images"), MyConstants.SUPPORTED_IMAGES);
        }
        jchooser.setMultiSelectionEnabled(true);
        jchooser.setDialogTitle(ResourceBundle.getBundle("translations/program_strings").getString("stfio.loadimages"));
        jchooser.setFileFilter(imgFilter);
        if (!"".equals(userDefinedFilefilter)) {
            jchooser.addChoosableFileFilter(imageFormats);
        }
        int status = jchooser.showOpenDialog(myComponent);
        //logger.trace("Status returned from ");
        if (status == JFileChooser.APPROVE_OPTION) {
            files = jchooser.getSelectedFiles();
            MyVariables.setLoadedFiles(files);
            prefs.storeByKey(LAST_OPENED_FOLDER, jchooser.getCurrentDirectory().getAbsolutePath());
            logger.debug("jchooser.getCurrentDirectory().getAbsolutePath() {}", jchooser.getCurrentDirectory().getAbsolutePath());
            return files;
        } else {
            files = null;
        }
        return files;
    }

    /*
     * Get the files from the "Load images" command  via Awt Filedialog
     */
    public static File[] getFileNamesAwt(JPanel myComponent) {

        Frame dialogframe = new Frame();
        String startFolder = getFolderPathToOpenBasedOnPreferences();
        String userDefinedFilefilter = prefs.getByKey(USER_DEFINED_FILE_FILTER, "");
        logger.debug("startfolder for load images via Awt {}", startFolder);

        //logger.info("startfolder {}", startFolder);
        FileDialog fdchooser = new FileDialog(dialogframe, ResourceBundle.getBundle("translations/program_strings").getString("stfio.loadimages"), FileDialog.LOAD);
        Application.OS_NAMES os = Utils.getCurrentOsName();
        if (os == APPLE) {
            System.setProperty("apple.awt.fileDialogForDirectories", "false");
            System.setProperty("apple.awt.use-file-dialog-packages", "true");
        }
        fdchooser.setDirectory(startFolder);
        fdchooser.setMultipleMode(true);

        fdchooser.setFilenameFilter(new FilenameFilter() {
            @Override
            public boolean accept(File file, String ext) {
                if (!"".equals(userDefinedFilefilter)) {
                    String[] uDefFilefilter = userDefinedFilefilter.split(",");
                    for (int i = 0; i < uDefFilefilter.length; i++)
                        uDefFilefilter[i] = uDefFilefilter[i].trim();
                    for (int i = 0; i < uDefFilefilter.length; i++) {
                        if (ext.toLowerCase().endsWith(uDefFilefilter[i])) {
                            return true;
                        }
                    }
                    return false;
                } else {
                    for (int i = 0; i < MyConstants.SUPPORTED_IMAGES.length; i++) {
                        if (ext.toLowerCase().endsWith(MyConstants.SUPPORTED_IMAGES[i])) {
                            return true;
                        }
                    }
                    return false;
                }
            }
        });

        fdchooser.setVisible(true);

        File[] files = fdchooser.getFiles();
        //File[] files = chooser.getSelectedFiles();
        if ( files.length == 0) {
            // no selection
            return files = null;
        }
        MyVariables.setLoadedFiles(files);
        prefs.storeByKey(LAST_OPENED_FOLDER, fdchooser.getDirectory());
        return files;
    }


    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    /**
     * This will delete our tmp enfused and aligned files in our tmp folder upon
     * a new run
     * This is not the most fancy version, but it is pretty universal
     * @param prefix
     */
    public static void deleteSameFilesInTmp(String prefix) {
        File tmpfolder = new File(MyVariables.gettmpWorkFolder());

        for (File f : tmpfolder.listFiles()) {
            if (f.getName().startsWith(prefix)) {
                f.delete();
            }
        }
    }

    public static String RecreateOurTempFolder () {
        String result = "Success";
        boolean successfully_erased = true;
        //File tf = null;
        //String tmpfolder = "";

        // Get the temporary directory
        String tempWorkDir = System.getProperty("java.io.tmpdir") + File.separator + "jimagefuser";
        File tmpfolder = new File (tempWorkDir);
        MyVariables.settmpWorkFolder(tempWorkDir);
        if (tmpfolder.exists()) {
            boolean successfully_deleted = deleteDirectory(tmpfolder);
            if (!successfully_deleted) {
                successfully_erased = false;
                result = "Failed to erase " + tempWorkDir + File.separator + "jimagefuser";
                logger.error(result);
            }
        }
        // Now (re)create our tmpfolder
        try {
            //Files.createDirectories(Paths.get(tempWorkDir + File.separator + "jimagefuser"));
            Files.createDirectories(Paths.get(tempWorkDir));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            result = "Creating folder \"" + tempWorkDir + File.separator + "jimagefuser failed";
            logger.error(result);
        }
        // delete our tmp workfolder including contents on program exit
        tmpfolder.deleteOnExit();

        return result;
    }

    public static String noSpacePath () throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;

        String checkPath = MyVariables.getSelectedImagePath();
        if (checkPath.contains(" ")) { //Only checks for first space in string, but that's enough. Even one space is too much
            logger.debug("path contains spaces {}", checkPath);
            File imgfile = new File(MyVariables.getSelectedImagePath());
            String filename = imgfile.getName();
            File targetfile = new File(MyVariables.gettmpWorkFolder() + File.separator + filename);
            if (targetfile.exists()) {
                return MyVariables.gettmpWorkFolder() + File.separator + filename;
            } else {
                try {
                    //Files.copy(imgfile, targetfile);
                    sourceChannel = new FileInputStream(imgfile).getChannel();
                    destChannel = new FileOutputStream(targetfile).getChannel();
                    destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
                }finally {
                    sourceChannel.close();
                    destChannel.close();
                }
                /*} catch (IOException e) {
                    // simply return original path. What else can we do?
                    return checkPath;
                }*/
                return targetfile.getPath();
            }
        } else {
            // simply return original path. Nothing to do
            logger.debug("No spaces in {}", checkPath);
            return checkPath;
        }
        //return checkPath;
    }

    public static File AskSaveToFile(JPanel rootPanel) {
        File file = null;
        String outputformat;
        FileSystemView fsv = FileSystemView.getFileSystemView();

        String startFolder = getFolderPathToOpenBasedOnPreferences();
        logger.debug("file/folder to save the fused image to {}", startFolder);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(startFolder));
        fileChooser.setDialogTitle(ResourceBundle.getBundle("translations/program_strings").getString("dlg.savetotitle"));
        int retval = fileChooser.showSaveDialog(rootPanel);
        if (retval == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            if (!(file == null)) {
                if (file.getName().toLowerCase().endsWith(".jpg")) {
                    // User wants to write a jpeg
                    outputformat = "jpg";
                } else if (file.getName().toLowerCase().endsWith(".png")) {
                    // User wants to write a png
                    outputformat = "png";
                } else if (file.getName().toLowerCase().endsWith(".tif")) {
                    // User wants to write a tif
                    outputformat = "tif";
                } else {
                    outputformat = "unsupported";
                    JOptionPane.showMessageDialog(rootPanel, "Only jpg, png and tif formats are supported", "Unsupported format", JOptionPane.WARNING_MESSAGE);
                    MyVariables.setSaveToFile(null);
                }
            }
            MyVariables.setSaveToFile(file);
        }
        return file;
    }
}
