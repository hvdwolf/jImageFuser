package org.hvdw.jimagefuser.controllers;

import org.hvdw.jimagefuser.*;
import org.hvdw.jimagefuser.facades.SystemPropertyFacade;
import org.hvdw.jimagefuser.model.GuiConfig;
import org.hvdw.jimagefuser.view.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static org.hvdw.jimagefuser.facades.SystemPropertyFacade.SystemPropertyKey.OS_NAME;
import static org.slf4j.LoggerFactory.getLogger;

public class MenuActionListener implements ActionListener  {
    private final static ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) getLogger(MenuActionListener.class);

    PreferencesDialog prefsDialog = new PreferencesDialog();
    private SimpleWebView WV = new SimpleWebView();

    public int[] selectedIndices;
    public List<Integer> selectedIndicesList = new ArrayList<>();

    public JFrame frame;
    public JPanel rootPanel;
    public JSplitPane splitPanel;
    public JLabel OutputLabel;
    public JMenuBar menuBar;
    public JProgressBar progressBar;
    public JComboBox UserCombiscomboBox;

    public MenuActionListener(JFrame frame, JPanel rootPanel, JSplitPane splitPanel, JMenuBar menuBar, JLabel OutputLabel, JProgressBar progressBar) {

        this.frame = frame;
        this.rootPanel = rootPanel;
        this.splitPanel = splitPanel;
        this.menuBar = menuBar;
        this.OutputLabel = OutputLabel;
        this.progressBar = progressBar;
        this.UserCombiscomboBox = UserCombiscomboBox;
    }




    // menuListener
    public void actionPerformed(ActionEvent mev) {
        String[] dummy = null;
        logger.info("Selected: {}", mev.getActionCommand());
        selectedIndicesList = MyVariables.getselectedIndicesList();
        if (selectedIndicesList == null) {
            selectedIndicesList = new ArrayList<>();
        }

        switch (mev.getActionCommand()) {
            case "Preferences":
                prefsDialog.showDialog();
                break;
            case "Exit":
                StandardFileIO.deleteDirectory(new File(MyVariables.gettmpWorkFolder()) );
                GuiConfig.SaveGuiConfig(frame, rootPanel, splitPanel);
                System.exit(0);
                break;
            case "About jImageFuser":
                //JOptionPane.showMessageDialog(mainScreen.this.rootPanel, String.format(ProgramTexts.HTML, 450, ResourceBundle.getBundle("translations/program_help_texts").getString("abouttext")), ResourceBundle.getBundle("translations/program_help_texts").getString("abouttitle"), JOptionPane.INFORMATION_MESSAGE);
                WV.HTMLView(ResourceBundle.getBundle("translations/program_help_texts").getString("abouttitle"), ResourceBundle.getBundle("translations/program_help_texts").getString("abouttext"), 500, 450);
                break;
            case "About ExifTool":
                JOptionPane.showMessageDialog(rootPanel, String.format(ProgramTexts.HTML, 450, ResourceBundle.getBundle("translations/program_help_texts").getString("aboutexiftool")), ResourceBundle.getBundle("translations/program_help_texts").getString("aboutexiftooltitle"), JOptionPane.INFORMATION_MESSAGE);
                break;
            case "jImageFuser homepage":
                Utils.openBrowser(ProgramTexts.ProjectWebSite);
                break;
            case "Enfuse manual":
                Utils.openBrowser("http://enblend.sourceforge.net/enfuse.doc/enfuse_4.2.xhtml/enfuse.html");
                break;
            case "Online manual":
                Utils.openBrowser(ProgramTexts.ProjectWebSite + "/manual/index.html");
                break;
            case "Enfuse wiki":
                Utils.openBrowser("https://wiki.panotools.org/Enfuse");
                break;
            case "Align_image_stack wiki":
                Utils.openBrowser("https://wiki.panotools.org/Align_image_stack");
                break;
            case "onlinemanuales":
                Utils.openBrowser("https://docs.museosabiertos.org/jimagefuser");
                break;
            case "Youtube channel":
                Utils.openBrowser("https://www.youtube.com/playlist?list=PLAHD8RNkeuGdyRH7BKFefc7p72Dp6jVjW");
                break;
            case "Credits":
                //JOptionPane.showMessageDialog(rootPanel, String.format(ProgramTexts.HTML, 400, ProgramTexts.CreditsText), "Credits", JOptionPane.INFORMATION_MESSAGE);
                String Credits = StandardFileIO.readTextFileAsStringFromResource("texts/credits.html");
                WV.HTMLView(ResourceBundle.getBundle("translations/program_strings").getString("hmenu.credits"), String.format(ProgramTexts.HTML, 600, Credits), 700, 600);
                break;
            case "System/Program info":
                String os = SystemPropertyFacade.getPropertyByKey(OS_NAME);
                if (os.contains("APPLE") || os.contains("Mac") ) {
                    JOptionPane.showMessageDialog(rootPanel, String.format(ProgramTexts.HTML, 650, Utils.systemProgramInfo()), ResourceBundle.getBundle("translations/program_strings").getString("sys.title"), JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(rootPanel, String.format(ProgramTexts.HTML, 500, Utils.systemProgramInfo()), ResourceBundle.getBundle("translations/program_strings").getString("sys.title"), JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case "License":
                Utils.showLicense(rootPanel);
                break;
            case "Check for new version":
                Utils.checkForNewVersion("menu");
                break;
            case "Translate":
                Utils.openBrowser("https://github.com/hvdwolf/jImageFuser/blob/master/translations/Readme.md");
                break;
            case "Changelog":
                Utils.openBrowser("https://github.com/hvdwolf/jImageFuser/blob/master/Changelog.md");
                break;
            case "Donate":
                Utils.openBrowser("https://hvdwolf.github.io/jImageFuser/donate.html");
                // Disable for the time being
                //WebPageInPanel WPIP = new WebPageInPanel();
                //WPIP.WebPageInPanel(rootPanel,"https://hvdwolf.github.io/jImageFuser/donate.html", 700,300);
                break;
            // Below this line we will add our Help sub menu containing the helptexts topics in this program
            case "editdataexif":
                JOptionPane.showMessageDialog(rootPanel, String.format(ProgramTexts.HTML, 700, ResourceBundle.getBundle("translations/program_help_texts").getString("exifandxmphelp")), ResourceBundle.getBundle("translations/program_help_texts").getString("exifhelptitle"), JOptionPane.INFORMATION_MESSAGE);
                break;
            case "editdataxmp":
                JOptionPane.showMessageDialog(rootPanel, String.format(ProgramTexts.HTML, 700, ResourceBundle.getBundle("translations/program_help_texts").getString("exifandxmphelp")), ResourceBundle.getBundle("translations/program_help_texts").getString("xmphelptitle"), JOptionPane.INFORMATION_MESSAGE);
                break;
            case "editdatagps":
                JOptionPane.showMessageDialog(rootPanel, String.format(ProgramTexts.HTML, 600, ResourceBundle.getBundle("translations/program_help_texts").getString("gpshelp")), ResourceBundle.getBundle("translations/program_help_texts").getString("gpshelptitle"), JOptionPane.INFORMATION_MESSAGE);
                break;
            case "editdatageotag":
                JOptionPane.showMessageDialog(rootPanel, String.format(ProgramTexts.HTML, 700, ResourceBundle.getBundle("translations/program_help_texts").getString("geotagginghelp")), ResourceBundle.getBundle("translations/program_help_texts").getString("geotagginghelptitle"), JOptionPane.INFORMATION_MESSAGE);
                break;
            case "editdatagpano":
                JOptionPane.showMessageDialog(rootPanel, String.format(ProgramTexts.HTML, 450, ResourceBundle.getBundle("translations/program_help_texts").getString("gpanohelp")), ResourceBundle.getBundle("translations/program_help_texts").getString("gpanohelptitle"), JOptionPane.INFORMATION_MESSAGE);
                break;
            case "editdatalens":
                JOptionPane.showMessageDialog(rootPanel, String.format(ProgramTexts.HTML, 450, ResourceBundle.getBundle("translations/program_help_texts").getString("lenshelptext")), ResourceBundle.getBundle("translations/program_help_texts").getString("lenshelptitle"), JOptionPane.INFORMATION_MESSAGE);
                break;
            case "copydata":
                JOptionPane.showMessageDialog(rootPanel, String.format(ProgramTexts.HTML, 450, ResourceBundle.getBundle("translations/program_help_texts").getString("copymetadatatext")), ResourceBundle.getBundle("translations/program_help_texts").getString("copymetadatatitle"), JOptionPane.INFORMATION_MESSAGE);
                break;
            case "exiftoolcommands":
//                    JOptionPane.showMessageDialog(rootPanel, String.format(ProgramTexts.HTML, 700, ResourceBundle.getBundle("translations/program_help_texts").getString("yourcommands")), ResourceBundle.getBundle("translations/program_help_texts").getString("yourcommandstitle"), JOptionPane.INFORMATION_MESSAGE);
                WV.HTMLView(ResourceBundle.getBundle("translations/program_help_texts").getString("exiftoolcommandstitle"), ResourceBundle.getBundle("translations/program_help_texts").getString("exiftoolcommands"), 700, 500);
                break;
            case "exiftooldb":
                JOptionPane.showMessageDialog(rootPanel, String.format(ProgramTexts.HTML, 700, ResourceBundle.getBundle("translations/program_help_texts").getString("exiftooldbhelptext")), ResourceBundle.getBundle("translations/program_help_texts").getString("exiftooldbtitle"), JOptionPane.INFORMATION_MESSAGE);
                break;
            case "menurenaminginfo":
                JOptionPane.showMessageDialog(rootPanel, String.format(ProgramTexts.HTML, 700, ResourceBundle.getBundle("translations/program_help_texts").getString("renamingtext")), ResourceBundle.getBundle("translations/program_help_texts").getString("renamingtitle"), JOptionPane.INFORMATION_MESSAGE);
                break;
            default:
                break;
        }

    }
}
