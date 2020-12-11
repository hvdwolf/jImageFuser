package org.hvdw.jimagefuser;

import ch.qos.logback.classic.Level;
import org.hvdw.jimagefuser.controllers.*;
import org.hvdw.jimagefuser.facades.IPreferencesFacade;
import org.hvdw.jimagefuser.facades.SystemPropertyFacade;
import org.hvdw.jimagefuser.model.GuiConfig;
import org.hvdw.jimagefuser.view.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hvdw.jimagefuser.Application.OS_NAMES.APPLE;
import static org.hvdw.jimagefuser.facades.IPreferencesFacade.PreferenceKey.*;
import static org.hvdw.jimagefuser.facades.SystemPropertyFacade.SystemPropertyKey.*;
import static org.slf4j.LoggerFactory.getLogger;

public class Utils {

    private final static IPreferencesFacade prefs = IPreferencesFacade.defaultInstance;
    private final static ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) getLogger(Utils.class);

    private Utils() {
        SetLoggingLevel(Utils.class);
    }

    public static void SetApplicationWideLogLevel() {
        //Do this for all classes
        // first to do
        //main level
        Utils.SetLoggingLevel(Application.class);
        Utils.SetLoggingLevel(Utils.class);
        Utils.SetLoggingLevel(MenuActionListener.class);
        Utils.SetLoggingLevel(StandardFileIO.class);
        Utils.SetLoggingLevel(SavedFusedImage.class);
        Utils.SetLoggingLevel(CheckPreferences.class);
        Utils.SetLoggingLevel(CommandRunner.class);
        Utils.SetLoggingLevel(ExifTool.class);
        Utils.SetLoggingLevel(CommandLineArguments.class);
        Utils.SetLoggingLevel(ImageFunctions.class);
        Utils.SetLoggingLevel(mainScreen.class);
        Utils.SetLoggingLevel(PreferencesDialog.class);
        Utils.SetLoggingLevel(GuiConfig.class);
        Utils.SetLoggingLevel(CreateMenu.class);
        Utils.SetLoggingLevel(JavaImageViewer.class);
        Utils.SetLoggingLevel(LinkListener.class);
        Utils.SetLoggingLevel(WebPageInPanel.class);
        Utils.SetLoggingLevel(SimpleWebView.class);
    }




    public static boolean containsIndices(int[] selectedIndices) {
        List<Integer> intList = IntStream.of(selectedIndices).boxed().collect(Collectors.toList());
        return intList.size() != 0;
    }

    static public void SetLoggingLevel(Class usedClass) {
        String logLevel = prefs.getByKey(LOG_LEVEL, "Info");
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) getLogger(usedClass);
        // hardcode in case of debugging/troubleshooting
        //logLevel = "Trace";

        switch (logLevel) {
            case "Off":
                logger.setLevel(Level.OFF);
                break;
            case "Error":
                logger.setLevel(Level.ERROR);
                break;
            case "Warn":
                logger.setLevel(Level.WARN);
                break;
            case "Info":
                logger.setLevel(Level.INFO);
                break;
            case "Debug":
                logger.setLevel(Level.DEBUG);
                break;
            case "Trace":
                logger.setLevel(Level.TRACE);
                break;
            default:
                logger.setLevel(Level.INFO);
                break;
        }
    }


    /*
    / Set default font for everything in the Application
    / from: https://stackoverflow.com/questions/7434845/setting-the-default-font-of-swing-program (Romain Hippeau)
    / To be called like : Utils.setUIFont (new FontUIResource("SansSerif", Font.PLAIN,12));
     */
    public static void setUIFont (javax.swing.plaf.FontUIResource f){
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get (key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put (key, f);
        }
    }

    /*
     * Opens the default browser of the Operating System
     * and displays the specified URL
     */
    static public void openBrowser(String webUrl) {

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(URI.create(webUrl));
                return;
            }
            Application.OS_NAMES os = Utils.getCurrentOsName();

            Runtime runtime = Runtime.getRuntime();
            switch (os) {
                case APPLE:
                    runtime.exec("open " + webUrl);
                    return;
                case LINUX:
                    runtime.exec("xdg-open " + webUrl);
                    return;
                case MICROSOFT:
                    runtime.exec("explorer " + webUrl);
                    return;
            }
        }
        catch (IOException | IllegalArgumentException e) {
            logger.error("Could not open browser", e);
        }
    }

    public static String systemProgramInfo() {
        StringBuilder infostring = new StringBuilder();

        infostring.append("<big>" + ResourceBundle.getBundle("translations/program_strings").getString("sys.title") + "</big><hr><br><table width=\"90%\" border=0>");
        infostring.append("<tr><td>" + ResourceBundle.getBundle("translations/program_strings").getString("sys.os") + "</td><td>" + SystemPropertyFacade.getPropertyByKey(OS_NAME) + "</td></tr>");
        infostring.append("<tr><td>" + ResourceBundle.getBundle("translations/program_strings").getString("sys.osarch") + "</td><td>" + SystemPropertyFacade.getPropertyByKey(OS_ARCH).replaceAll("(\\r|\\n)", "") + "</td></tr>");
        // or use replaceAll(LINE_SEPATATOR, "") or replaceAll("(\\r|\\n)", "")
        infostring.append("<tr><td>" + ResourceBundle.getBundle("translations/program_strings").getString("sys.osv") + "</td><td>" + SystemPropertyFacade.getPropertyByKey(OS_VERSION).replaceAll(SystemPropertyFacade.getPropertyByKey(LINE_SEPARATOR), "") + "</td></tr>");
        infostring.append("<tr><td>" + ResourceBundle.getBundle("translations/program_strings").getString("sys.uhome") + "</td><td>" + SystemPropertyFacade.getPropertyByKey(USER_HOME).replaceAll(SystemPropertyFacade.getPropertyByKey(LINE_SEPARATOR), "") + "</td></tr>");
        infostring.append("<tr><td>&nbsp;</td><td>&nbsp;</td></tr>");
        infostring.append("<tr><td>" + ResourceBundle.getBundle("translations/program_strings").getString("sys.jifv") + "</td><td>" + ProgramTexts.Version.replaceAll("(\\r|\\n)", "") + "</td></tr>");
        //infostring.append("<tr><td>" + ResourceBundle.getBundle("translations/program_strings").getString("sys.ev") + "</td><td>" + (MyVariables.getExiftoolVersion()).replaceAll("(\\r|\\n)", "") + "</td></tr>");
        infostring.append("<tr><td>&nbsp;</td><td>&nbsp;</td></tr>");
        infostring.append("<tr><td>" + ResourceBundle.getBundle("translations/program_strings").getString("sys.jv") + "</td><td>" + SystemPropertyFacade.getPropertyByKey(JAVA_VERSION) + "</td></tr>");
        infostring.append("<tr><td>" + ResourceBundle.getBundle("translations/program_strings").getString("sys.jhome") + "</td><td>" + SystemPropertyFacade.getPropertyByKey(JAVA_HOME) + "</td></tr>");
        infostring.append("</table></html>");

        return infostring.toString();
    }

    // Displays the license in an option pane
    public static void showLicense(JPanel myComponent) {

        String license = StandardFileIO.readTextFileAsStringFromResource("COPYING");
        JTextArea textArea = new JTextArea(license);
        boolean isWindows = Utils.isOsFromMicrosoft();
        if (isWindows) {
            textArea.setFont(new Font("Sans_Serif", Font.PLAIN, 13));
        }
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        scrollPane.setPreferredSize(new Dimension(500, 500));
        JOptionPane.showMessageDialog(myComponent, scrollPane, "GNU GENERAL PUBLIC LICENSE Version 3", JOptionPane.INFORMATION_MESSAGE);
    }

    // Shows or hides the progressbar when called from some (long) running method
    static void progressStatus(JProgressBar progressBar, Boolean show) {
        if (show) {
            progressBar.setVisible(true);
            progressBar.setIndeterminate(true);
            progressBar.setBorderPainted(true);
            progressBar.repaint();
        } else {
            progressBar.setVisible(false);
        }
    }

    /*
     * This method checks for a new version on the repo.
     * It can be called from startup (preferences setting) or from the Help menu
     */
    public static void checkForNewVersion(String fromWhere) {
        String web_version = "";
        boolean versioncheck = prefs.getByKey(VERSION_CHECK, true);
        boolean validconnection = true;
        String update_url = "https://raw.githubusercontent.com/hvdwolf/jImageFuser/master/version.txt";

        if (fromWhere.equals("menu") || versioncheck) {
            try {
                URL url = new URL(update_url);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                web_version = in.readLine();
                in.close();
            } catch (IOException ex) {
                logger.error("upgrade check gives error {}", ex.toString());
                ex.printStackTrace();
                validconnection = false;
                JOptionPane.showMessageDialog(null, String.format(ProgramTexts.HTML, 250, ResourceBundle.getBundle("translations/program_strings").getString("msd.nonetwlong")), ResourceBundle.getBundle("translations/program_strings").getString("msd.nonetwork"), JOptionPane.INFORMATION_MESSAGE);
            }

            if (validconnection) {
                String jv = SystemPropertyFacade.getPropertyByKey(JAVA_VERSION);
                logger.info("Using java version {}: ", jv);
                logger.info("Version on the web: " + web_version);
                logger.info("This version: " + ProgramTexts.Version);
                int version_compare = web_version.compareTo(ProgramTexts.Version);
                if (version_compare > 0) { // This means the version on the web is newer
                    //if (Float.valueOf(web_version) > Float.valueOf(ProgramTexts.Version)) {
                    String[] options = {"No", "Yes"};
                    int choice = JOptionPane.showOptionDialog(null, String.format(ProgramTexts.HTML, 400, ResourceBundle.getBundle("translations/program_strings").getString("msd.jtgnewversionlong")), ResourceBundle.getBundle("translations/program_strings").getString("msd.jtgnewversion"),
                            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    if (choice == 1) { //Yes
                        // Do something
                        openBrowser("https://github.com/hvdwolf/jImageFuser/releases");
                        System.exit(0);
                    }

                } else {
                    if (fromWhere.equals("menu")) {
                        JOptionPane.showMessageDialog(null, String.format(ProgramTexts.HTML, 250, ResourceBundle.getBundle("translations/program_strings").getString("msd.jtglatestversionlong")), ResourceBundle.getBundle("translations/program_strings").getString("msd.jtglatestversion"), JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    // Create correct exiftool command call depending on operating system
    public static String platformExiftool() {
        // exiftool on windows or other
        String exiftool = prefs.getByKey(EXIFTOOL_PATH, "");
        if (isOsFromMicrosoft()) {
            exiftool = exiftool.replace("\\", "/");
        }
        return exiftool;
    }


    ////////////////////////////////// Load images and display them  ///////////////////////////////////

    /**
     * This method returns the file extension based on a filename String
     * @param filename
     * @return file extension
     */
    public static String getFileExtension(String filename) {

        int lastIndexOf = filename.lastIndexOf(".") + 1;
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return filename.substring(lastIndexOf);
    }

    /**
     * This method returns the file extension based on a File object
     * @param file
     * @return file extension
     */
    public static String getFileExtension(File file) {

        String filename = file.getPath();
        int lastIndexOf = filename.lastIndexOf(".") + 1;
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return filename.substring(lastIndexOf);
    }

    public static String getFileNameWithoutExtension(String filename) {
        return filename.replaceFirst("[.][^.]+$", "");
    }
    public static String getFilePathWithoutExtension(String filepath) {
        // Duplicate of above, but makes it easier when working with file paths or only file names
        return filepath.replaceFirst("[.][^.]+$", "");
    }


    /*
    / an instance of LabelIcon holds an icon and label pair for each row.
     */
    private static class LabelIcon {

        Icon icon;
        String label;

        public LabelIcon(Icon icon, String label) {
            this.icon = icon;
            this.label = label;
        }
    }

    static public String returnBasicImageDataString(String filename, String stringType) {
        String strImgData = "";
        double calcFLin35mmFormat = 0.0;
        // hashmap basicImgData: ImageWidth, ImageHeight, Orientation, ISO, FNumber, ExposureTime, focallength, focallengthin35mmformat
        HashMap<String, String> imgBasicData = MyVariables.getimgBasicData();
        StringBuilder imginfo = new StringBuilder();
        NumberFormat df = DecimalFormat.getInstance(Locale.US);
        df.setMaximumFractionDigits(1);


        if ("html".equals(stringType)) {
            imginfo.append("<html>" + filename);
            imginfo.append("<br><br>" + ResourceBundle.getBundle("translations/program_strings").getString("lp.imagesize") + ": " + imgBasicData.get("ImageWidth") + " x " + imgBasicData.get("ImageHeight"));
            if (imgBasicData.containsKey("ISO")) {
                imginfo.append("<br>" + ResourceBundle.getBundle("translations/program_strings").getString("lp.iso") + ": " + imgBasicData.get("ISO"));
            } else {
                imginfo.append("<br>" + ResourceBundle.getBundle("translations/program_strings").getString("lp.iso") + ": " + ResourceBundle.getBundle("translations/program_strings").getString("lp.notavailable"));
            }
            if (imgBasicData.containsKey("FNumber")) {
                imginfo.append("<br>" + ResourceBundle.getBundle("translations/program_strings").getString("lp.fnumber") + ": " + imgBasicData.get("FNumber"));
            } else {
                imginfo.append("<br>" + ResourceBundle.getBundle("translations/program_strings").getString("lp.fnumber") + ": " + ResourceBundle.getBundle("translations/program_strings").getString("lp.notavailable"));
            }
            if (imgBasicData.containsKey("ExposureTime")) {
                imginfo.append("<br>" + ResourceBundle.getBundle("translations/program_strings").getString("lp.exposuretime") + ": 1/" + String.valueOf(Math.round(1 / Float.parseFloat(imgBasicData.get("ExposureTime")))));
            } else {
                imginfo.append("<br>" + ResourceBundle.getBundle("translations/program_strings").getString("lp.exposuretime") + ": " + ResourceBundle.getBundle("translations/program_strings").getString("lp.notavailable"));
            }
            if (imgBasicData.containsKey("FocalLength")) {
                imginfo.append("<br>" + ResourceBundle.getBundle("translations/program_strings").getString("lp.focallength") + ": " + imgBasicData.get("FocalLength") + " mm");
            } else {
                imginfo.append("<br>" + ResourceBundle.getBundle("translations/program_strings").getString("lp.focallength") + ": " + ResourceBundle.getBundle("translations/program_strings").getString("lp.notavailable"));
            }
            if (imgBasicData.containsKey("FocalLengthIn35mmFormat") ) {
                imginfo.append("<br>" + ResourceBundle.getBundle("translations/program_strings").getString("lp.focallength35mm") + ": " + imgBasicData.get("FocalLengthIn35mmFormat") + " mm");
            } else if (imgBasicData.containsKey("ScaleFactor35efl") ) {
                try {
                    calcFLin35mmFormat = Double.parseDouble(imgBasicData.get("FocalLength").trim()) * Double.parseDouble(imgBasicData.get("ScaleFactor35efl").trim());
                    //logger.info("String.valueOf(calcFLin35mmFormat) {} df.format(calcFLin35mmFormat) {}", String.valueOf(calcFLin35mmFormat), df.format(calcFLin35mmFormat));
                    imginfo.append("<br>" + ResourceBundle.getBundle("translations/program_strings").getString("lp.focallength35mm") + ": " + df.format(calcFLin35mmFormat) + " mm");
                } catch (NumberFormatException e) {
                    logger.error("calcFLin35mmFormat failed {}", String.valueOf(calcFLin35mmFormat));
                    e.printStackTrace();
                }
            }
            if (imgBasicData.containsKey("ExposureCompensation")) {
                imginfo.append("<br>" + ResourceBundle.getBundle("translations/program_strings").getString("lp.exposurecompensation") + ": " + imgBasicData.get("ExposureCompensation") + " ev");
            } else {
                imginfo.append("<br>" + ResourceBundle.getBundle("translations/program_strings").getString("lp.exposurecompensation") + ": " + ResourceBundle.getBundle("translations/program_strings").getString("lp.notavailable"));
                imginfo.append("<br>" + ResourceBundle.getBundle("translations/program_strings").getString("lp.focallength") + ": " + ResourceBundle.getBundle("translations/program_strings").getString("lp.notavailable"));
            }
            strImgData = imginfo.toString();
        } else if ("OneLine".equals(stringType)) {
            imginfo.append(ResourceBundle.getBundle("translations/program_strings").getString("lp.filename") + ": " + filename);
            imginfo.append("; " + ResourceBundle.getBundle("translations/program_strings").getString("lp.imagesize") + ": " + imgBasicData.get("ImageWidth") + " x " + imgBasicData.get("ImageHeight"));
            //imginfo.append("; " + ResourceBundle.getBundle("translations/program_strings").getString("lp.orientation") + imgBasicData.get("Orientation"));
            imginfo.append("; " + ResourceBundle.getBundle("translations/program_strings").getString("lp.iso") + ": " + imgBasicData.get("ISO"));
            imginfo.append("; " + ResourceBundle.getBundle("translations/program_strings").getString("lp.fnumber") + ": " + imgBasicData.get("FNumber"));
            if (!(imgBasicData.get("ExposureTime") == null)) {
                imginfo.append("; " + ResourceBundle.getBundle("translations/program_strings").getString("lp.exposuretime") + ": 1/" + String.valueOf(Math.round(1 / Float.parseFloat(imgBasicData.get("ExposureTime")))));
            } else {
                imginfo.append("; " + ResourceBundle.getBundle("translations/program_strings").getString("lp.exposuretime") + ": " + imgBasicData.get("ExposureTime"));
            }
            imginfo.append("; " + ResourceBundle.getBundle("translations/program_strings").getString("lp.focallength") + ": " + imgBasicData.get("FocalLength") + " mm");
            imginfo.append("; " + ResourceBundle.getBundle("translations/program_strings").getString("lp.focallength35mm") + ": " + imgBasicData.get("FocalLengthIn35mmFormat") + " mm");
            imginfo.append("; " + ResourceBundle.getBundle("translations/program_strings").getString("lp.exposurecompensation") + ": " + imgBasicData.get("ExposureCompensation") + " ev");
            strImgData = imginfo.toString();
        } else if ("OneLineHtml".equals(stringType)) {
            imginfo.append("<html><b>" + ResourceBundle.getBundle("translations/program_strings").getString("lp.filename") + "</b>: " + filename);
            imginfo.append("; <b>" + ResourceBundle.getBundle("translations/program_strings").getString("lp.imagesize") + ":</b> " + imgBasicData.get("ImageWidth") + " x " + imgBasicData.get("ImageHeight"));
            //imginfo.append("; " + ResourceBundle.getBundle("translations/program_strings").getString("lp.orientation") + imgBasicData.get("Orientation"));
            imginfo.append("; <b>" + ResourceBundle.getBundle("translations/program_strings").getString("lp.iso") + ":</b> " + imgBasicData.get("ISO"));
            imginfo.append("; <b>" + ResourceBundle.getBundle("translations/program_strings").getString("lp.fnumber") + ":</b> " + imgBasicData.get("FNumber"));
            if (!(imgBasicData.get("ExposureTime") == null)) {
                imginfo.append("; <b>" + ResourceBundle.getBundle("translations/program_strings").getString("lp.exposuretime") + ":</b> 1/" + String.valueOf(Math.round(1 / Float.parseFloat(imgBasicData.get("ExposureTime")))));
            } else {
                imginfo.append("; <b>" + ResourceBundle.getBundle("translations/program_strings").getString("lp.exposuretime") + ":</b> " + imgBasicData.get("ExposureTime"));
            }
            imginfo.append("; <b>" + ResourceBundle.getBundle("translations/program_strings").getString("lp.focallength") + ":</b> " + imgBasicData.get("FocalLength") + " mm");
            imginfo.append("; <b>" + ResourceBundle.getBundle("translations/program_strings").getString("lp.focallength35mm") + ":</b> " + imgBasicData.get("FocalLengthIn35mmFormat") + " mm");
            imginfo.append("; <b>" + ResourceBundle.getBundle("translations/program_strings").getString("lp.exposurecompensation") + ":</b> " + imgBasicData.get("ExposureCompensation") + " ev");
            strImgData = imginfo.toString();

        }
        return strImgData;
    }



    public synchronized static File[] loadImages(String loadingType, JPanel rootPanel, JPanel LeftPanel, JTable tableListfiles, JButton[] commandButtons, JLabel[] mainScreenLabels, JProgressBar progressBar) {
        File[] files;
        boolean files_null = false;

        // "Translate" for clarity, instead of using the array index;
        JLabel OutputLabel = mainScreenLabels[0];
        JLabel lblLoadedFiles = mainScreenLabels[1];


        String prefFileDialog = prefs.getByKey(PREFERRED_FILEDIALOG, "jfilechooser");
        if ("images".equals(loadingType)) {
            logger.debug("load images pushed or menu load images");
            OutputLabel.setText(ResourceBundle.getBundle("translations/program_strings").getString("pt.loadingimages"));
            if ("jfilechooser".equals(prefFileDialog)) {
                logger.debug("load images using jfilechooser");
                files = StandardFileIO.getFileNames(rootPanel);
                logger.debug("AFTER load images using jfilechooser");
            } else {
                logger.debug("load images pushed or menu load images using AWT file dialog");
                files = StandardFileIO.getFileNamesAwt(rootPanel);
                logger.debug("AFTER load images using AWT file dialog");
            }
        } else if ("dropped files".equals(loadingType)){ // files dropped onto our app
            OutputLabel.setText(ResourceBundle.getBundle("translations/program_strings").getString("pt.droppedfiles"));
            files = MyVariables.getLoadedFiles();
        } else { // Use files from command line
            OutputLabel.setText(ResourceBundle.getBundle("translations/program_strings").getString("pt.commandline"));
            files = MyVariables.getLoadedFiles();
        }
        if (files != null) {
            lblLoadedFiles.setText(String.valueOf(files.length));
            logger.debug("After loading images, loading files or dropping files: no. of files > 0");


            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    ImageFunctions.extractThumbnails();
                    Utils.displayFiles(tableListfiles, LeftPanel);
                    MyVariables.setSelectedRow(0);

                    for (JButton btn : commandButtons) {
                        btn.setEnabled(true);
                    }
                    OutputLabel.setText("Creating preview");
                    String result = SavedFusedImage.enfuseImages("preview", progressBar, OutputLabel);
                    //logger.info("result {}", result);
                    if (!"too long".equals(result)) {
                        SavedFusedImage.displayPreview(mainScreenLabels[2]);
                    } else {
                        JOptionPane.showMessageDialog(rootPanel, "This took way too long. Aborting", "Aborting preview creation", JOptionPane.ERROR_MESSAGE);
                    }

                    OutputLabel.setText("");
                    // progressbar enabled immedately after this void run starts in the InvokeLater, so I disable it here at the end of this void run
                    Utils.progressStatus(progressBar, false);
                }
            });
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    progressBar.setVisible(true);
                    //progressPane(rootPanel, true);
                }
            });

        } else {
            logger.debug("no files loaded. User pressed cancel.");
            files_null = true;
            lblLoadedFiles.setText("");
            OutputLabel.setText("");
        }
        List<Integer> selectedIndicesList = new ArrayList<>();
        MyVariables.setselectedIndicesList(selectedIndicesList);
        MyVariables.setLoadedFiles(files);
        progressBar.setVisible(false);

        return files;
    }


    /*
     * Display the loaded files with icon and name
     */
    static void displayFiles(JTable jTable_File_Names, JPanel LeftPanel) {
        int selectedRow, selectedColumn;

        ImageIcon icon = null;
        File[] files = MyVariables.getLoadedFiles();

        DefaultTableModel model = (DefaultTableModel) jTable_File_Names.getModel();

        jTable_File_Names.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            protected void setValue(Object value) {
                if (value instanceof ImageIcon) {
                    setIcon((ImageIcon) value);
                    setText("");
                } else {
                    setIcon(null);
                    super.setValue(value);
                }
            }
        });

        model.setColumnIdentifiers(new String[]{ResourceBundle.getBundle("translations/program_strings").getString("lp.thumbtablephotos"), ResourceBundle.getBundle("translations/program_strings").getString("lp.thumbtabledata")});
        jTable_File_Names.getColumnModel().getColumn(0).setPreferredWidth(170);
        jTable_File_Names.getColumnModel().getColumn(1).setPreferredWidth(250);
        jTable_File_Names.setRowHeight(150);
        //LeftPanel.setSize(430,-1);
        LeftPanel.setPreferredSize(new Dimension(440, -1));

        model.setRowCount(0);
        model.fireTableDataChanged();
        jTable_File_Names.clearSelection();
        jTable_File_Names.setCellSelectionEnabled(true);

        Object[] ImgFilenameRow = new Object[2];
        String filename = "";

        Application.OS_NAMES currentOsName = getCurrentOsName();
        for (File file : files) {
            filename = file.getName().replace("\\", "/");
            logger.debug("Now working on image: " +filename);

            icon = ImageFunctions.analyzeImageAndCreateIcon(file);
            String imginfo = returnBasicImageDataString(filename, "html");
            logger.debug("imginfo {}", imginfo);
            ImgFilenameRow[0] = icon;
            ImgFilenameRow[1] = imginfo;
            model.addRow(ImgFilenameRow);
        }

        MyVariables.setSelectedRow(0);
        MyVariables.setSelectedColumn(0);
    }



    /*
     * This method displays the selected image in the default image viewer for the relevant mime-type (the extension mostly)
     */
    public static void displaySelectedImageInDefaultViewer(int selectedRow, File[] files, JLabel ThumbView) throws IOException {
        String fpath = "";
        if (isOsFromMicrosoft()) {
            fpath = "\"" + files[selectedRow].getPath().replace("\\", "/") + "\"";
        } else {
            fpath = "\"" + files[selectedRow].getPath() + "\"";
        }
        logger.debug("fpath for displaySelectedImageInDefaultViewer is now: {}", fpath);
        BufferedImage img = ImageIO.read(new File(fpath));
        // resize it
        BufferedImage resizedImg = new BufferedImage(300, 225, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(img, 0, 0, 300, 225, null);
        g2.dispose();
        ImageIcon icon = new ImageIcon(resizedImg);
        ThumbView.setIcon(icon);
    }


    public static String getExiftoolPath() {
        String res;
        List<String> cmdparams;
        Application.OS_NAMES currentOs = getCurrentOsName();

        if (currentOs == Application.OS_NAMES.MICROSOFT) {
            String[] params = {"where", "exiftool"};
            cmdparams = Arrays.asList(params);
        } else {
            String[] params = {"which", "exiftool"};
            cmdparams = Arrays.asList(params);
        }

        try {
            res = CommandRunner.runCommand(cmdparams); // res returns path to exiftool; on error on windows "INFO: Could not ...", on linux returns nothing
        } catch (IOException | InterruptedException ex) {
            logger.debug("Error executing command");
            res = ex.getMessage();
        }

        return res;
    }

    /*
    / Retrieves the icon that is used for the window bar icons in the app (windows/linux)
     */
    static public BufferedImage getFrameIcon() {
         BufferedImage frameicon = null;
         try {
             frameicon = ImageIO.read(mainScreen.class.getResource("/icons/jimagefuser-frameicon.png"));
         } catch (IOException ioe) {
             logger.info("error loading frame icon {}", ioe.toString());
         }
         return frameicon;
    }


    /*
    / This method is called from main screen. It needs to detect if we have a raw image, a bmp/gif/jpg/png image, or whatever other kind of file
    / If it is a raw image and we have a raw viewer configured, use method viewInRawViewer
    / if it is an image (whatever) and we always want to use the raw viewer, use method viewInRawViewer
    / If no raw viewer defined, or we have whatever other extension (can also be normal or raw image), use method viewInDefaultViewer (based on mime type and default app)
     */
    public static void displaySelectedImageInExternalViewer() {
        String fpath = "";
        boolean defaultimg = false;
        String[] SimpleExtensions = MyConstants.SUPPORTED_IMAGES;

        Application.OS_NAMES currentOsName = getCurrentOsName();
        int selectedRow = MyVariables.getSelectedRow();
        List<Integer> selectedIndicesList =  MyVariables.getselectedIndicesList();
        File[] files = MyVariables.getLoadedFiles();
        if (selectedIndicesList.size() < 2) { //Meaning we have only one image selected
            logger.debug("selectedRow: {}", String.valueOf(selectedRow));
            if (isOsFromMicrosoft()) {
                fpath = files[selectedRow].getPath().replace("\\", "/");
            } else {
                fpath = files[selectedRow].getPath();
            }
            // Need to build exiftool prefs check
            MyVariables.setSelectedImagePath(fpath);
        }
        String filenameExt = getFileExtension(MyVariables.getSelectedImagePath());

        for (String ext : SimpleExtensions) {
            if (filenameExt.toLowerCase().equals(ext)) { // it is a jpg or tif(f)
                defaultimg = true;
                logger.debug("default image is true");
                break;
            }
        }
        if (defaultimg) {
            JavaImageViewer JIV = new JavaImageViewer();
            JIV.ViewImageInFullscreenFrame("multiimages");
        } else { // We have something else
            Runtime runtime = Runtime.getRuntime();
            try {
                //Application.OS_NAMES currentOsName = getCurrentOsName();
                switch (currentOsName) {
                    case APPLE:
                        runtime.exec("open /Applications/Preview.app " + MyVariables.getSelectedImagePath());
                        logger.info("preview command {}", "open /Applications/Preview.app " + MyVariables.getSelectedImagePath());
                        return;
                    case MICROSOFT:
                        String convImg = MyVariables.getSelectedImagePath().replace("/", "\\");
                        String[] commands = {"cmd.exe", "/c", "start", "\"DummyTitle\"", String.format("\"%s\"", convImg)};
                        runtime.exec(commands);
                        return;
                    case LINUX:
                        String selectedImagePath = MyVariables.getSelectedImagePath().replace(" ", "\\ ");
                        logger.trace("xdg-open {}", selectedImagePath);
                        runtime.exec("xdg-open " + MyVariables.getSelectedImagePath().replace(" ", "\\ "));
                        return;
                }
            } catch (IOException e) {
                logger.error("Could not open image app.", e);
            }
        }

    }

    public static Application.OS_NAMES getCurrentOsName() {
        String OS = SystemPropertyFacade.getPropertyByKey(OS_NAME).toLowerCase();
        if (OS.contains("mac")) return APPLE;
        if (OS.contains("windows")) return Application.OS_NAMES.MICROSOFT;
        return Application.OS_NAMES.LINUX;
    }

    public static boolean isOsFromMicrosoft() {
        return getCurrentOsName() == Application.OS_NAMES.MICROSOFT;
    }
    public static boolean isOsFromApple() {
        return getCurrentOsName() == APPLE;
    }


    @SuppressWarnings("SameParameterValue")
    private static URL getResource(String path) {
        return Utils.class.getClassLoader().getResource(path);
    }
}
