package org.hvdw.jimagefuser;

import ch.qos.logback.classic.Logger;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.hvdw.jimagefuser.controllers.ExifTool;
import org.hvdw.jimagefuser.controllers.StandardFileIO;
import org.hvdw.jimagefuser.facades.IPreferencesFacade;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.hvdw.jimagefuser.facades.IPreferencesFacade.PreferenceKey.*;

public class PreferencesDialog extends JDialog {
    JPanel generalPanel;
    JButton buttonSave;
    JButton buttonCancel;
    JTextField ExiftoolLocationtextField;
    JButton ExiftoolLocationbutton;
    JTextField ImgStartFoldertextField;
    JButton ImgStartFolderButton;
    JCheckBox UseLastOpenedFoldercheckBox;
    JCheckBox CheckVersioncheckBox;
    private JComboBox metadataLanuagecomboBox;
    private JTabbedPane tabbedPanel;
    private JPanel contentPanel;
    private JComboBox localecomboBox;
    private JRadioButton JFilechooserradioButton;
    private JRadioButton AwtdialogradioButton;
    private JLabel filedialogexplained;
    private JComboBox loglevelcomboBox;
    private JLabel logleveltext;
    private JPanel loglevelpanel;
    private JPanel filechooserpanel;
    private JCheckBox preserveModDatecheckBox;
    private JLabel ExampleColumnImage;


    // Initialize all the helper classes
    //AppPreferences AppPrefs = new AppPreferences();
    private IPreferencesFacade prefs = IPreferencesFacade.defaultInstance;
    //private static final ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(PreferencesDialog.class);
    private final static Logger logger = (Logger) LoggerFactory.getLogger(PreferencesDialog.class);
    HashMap<String, String> retrievedPreferences = new HashMap<String, String>();

    public PreferencesDialog() {

        setContentPane(contentPanel);
        setModal(true);
        getRootPane().setDefaultButton(buttonSave);
        this.setIconImage(Utils.getFrameIcon());

        buttonSave.addActionListener(e -> onSave());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        generalPanel.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        ExiftoolLocationbutton.addActionListener(actionEvent -> {
            String ETpath = "";
            ETpath = ExifTool.whereIsExiftool(generalPanel);
            getExiftoolPath(generalPanel, ExiftoolLocationtextField, ETpath, "preferences");
        });

        ImgStartFolderButton.addActionListener(actionEvent -> getDefaultImagePath(generalPanel, ImgStartFoldertextField));

    }

    private void onSave() {
        // add your code here
        savePrefs();
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }


    // As the exiftoolLocator is already created in the Utils as startup check we simply leave it there
    // although you can discuss that it can also be a preference if we deviate from the standard one
    // of course we do need the return value which we will get from the listener
    public void getExiftoolPath(JPanel myComponent, JTextField myExiftoolTextfield, String ePath, String fromWhere) {
        if ("cancelled".equals(ePath)) {
            if ("startup".equals(fromWhere)) {
                JOptionPane.showMessageDialog(myComponent, String.format(ProgramTexts.HTML, 400, ResourceBundle.getBundle("translations/program_strings").getString("prefs.etlocatecanceltext")), ResourceBundle.getBundle("translations/program_strings").getString("prefs.etlocatecanceltitle"), JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(myComponent, String.format(ProgramTexts.HTML, 400, ResourceBundle.getBundle("translations/program_strings").getString("prefs.etlocatecanceltext")), ResourceBundle.getBundle("translations/program_strings").getString("prefs.etlocatecanceltitle"), JOptionPane.WARNING_MESSAGE);
            }
        } else if ("no exiftool binary".equals(ePath)) {
            if ("startup".equals(fromWhere)) {
                JOptionPane.showMessageDialog(myComponent, String.format(ProgramTexts.HTML, 400, ResourceBundle.getBundle("translations/program_strings").getString("prefs.etwrongtext")), ResourceBundle.getBundle("translations/program_strings").getString("prefs.etwrongtitle"), JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(myComponent, String.format(ProgramTexts.HTML, 400, ResourceBundle.getBundle("translations/program_strings").getString("prefs.etwrongtext")), ResourceBundle.getBundle("translations/program_strings").getString("prefs.etwrongtitle"), JOptionPane.WARNING_MESSAGE);
            }
        } else { // Yes. It looks like we have a correct exiftool selected
            // remove all possible line breaks
            ePath = ePath.replace("\n", "").replace("\r", "");
            //prefs.put("exiftool", ePath);
            myExiftoolTextfield.setText(ePath);
        }
    }

    // Locate the default image path, if the user wants it
    public void getDefaultImagePath(JPanel myComponent, JTextField defImgFolder) {
        String SelectedFolder;
        String prefFileDialog = prefs.getByKey(PREFERRED_FILEDIALOG, "jfilechooser");

        if ("jfilechooser".equals(prefFileDialog)) {
            final JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle(ResourceBundle.getBundle("translations/program_strings").getString("prefs.locateprefimgfolder"));
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int status = chooser.showOpenDialog(myComponent);
            if (status == JFileChooser.APPROVE_OPTION) {
                SelectedFolder = chooser.getSelectedFile().getAbsolutePath();
                defImgFolder.setText(SelectedFolder);
            }
        } else {
            JFrame dialogframe = new JFrame("");
            FileDialog chooser = new FileDialog(dialogframe, ResourceBundle.getBundle("translations/program_strings").getString("stfio.loadfolder"), FileDialog.LOAD);
            //chooser.setDirectory(startFolder);
            chooser.setMultipleMode(false);
            chooser.setVisible(true);

            SelectedFolder = chooser.getDirectory();
            if (!(SelectedFolder == null)) {
                defImgFolder.setText(SelectedFolder);
            }

        }
    }


    private void savePrefs() {
        logger.info("Saving the preferences");
        logger.info("exiftool {}", ExiftoolLocationtextField.getText());
        logger.info("defaultstartfolder {}", ImgStartFoldertextField.getText());
        logger.info("uselastopenedfolder {}", UseLastOpenedFoldercheckBox.isSelected());
        logger.info("Check for new version on startup {}", CheckVersioncheckBox.isSelected());
        logger.info("Preferred application language", localecomboBox.getSelectedItem());
        if (JFilechooserradioButton.isSelected()) {
            logger.info("Preferred file dialog", "jfilechooser");
        } else {
            logger.info("Preferred file dialog", "awtdialog");
        }
        logger.info("preservemodifydate {}", preserveModDatecheckBox.isSelected());
        logger.info("loglevel {}", loglevelcomboBox.getSelectedItem());


        if (!(retrievedPreferences.get("ExiftoolLocation").equals(ExiftoolLocationtextField.getText()))) {
            //if (!ExiftoolLocationtextField.getText().isEmpty()) {
            logger.trace("{}: {}", EXIFTOOL_PATH.key, ExiftoolLocationtextField.getText());
            prefs.storeByKey(EXIFTOOL_PATH, ExiftoolLocationtextField.getText());
        }
        if (!ImgStartFoldertextField.getText().isEmpty()) {
            logger.trace("{}: {}", DEFAULT_START_FOLDER.key, ImgStartFoldertextField.getText());
            prefs.storeByKey(DEFAULT_START_FOLDER, ImgStartFoldertextField.getText());
        }
        if (JFilechooserradioButton.isSelected()) {
            logger.trace("Preferred file dialog: jfilechooser");
            prefs.storeByKey(PREFERRED_FILEDIALOG, "jfilechooser");
        } else {
            logger.trace("Preferred file dialog: awtdialog");
            prefs.storeByKey(PREFERRED_FILEDIALOG, "awtdialog");
        }

        logger.trace("{}: {}", USE_LAST_OPENED_FOLDER.key, UseLastOpenedFoldercheckBox.isSelected());
        prefs.storeByKey(USE_LAST_OPENED_FOLDER, UseLastOpenedFoldercheckBox.isSelected());

        logger.trace("{}: {}", VERSION_CHECK.key, CheckVersioncheckBox.isSelected());
        prefs.storeByKey(VERSION_CHECK, CheckVersioncheckBox.isSelected());

        logger.trace("{}: {}", PREFERRED_APP_LANGUAGE.key, localecomboBox.getSelectedItem());
        prefs.storeByKey(PREFERRED_APP_LANGUAGE, (String) localecomboBox.getSelectedItem());

        logger.trace("{} {}", PRESERVE_MODIFY_DATE.key, preserveModDatecheckBox.isSelected());
        prefs.storeByKey(PRESERVE_MODIFY_DATE, preserveModDatecheckBox.isSelected());

        logger.trace("{}: {}", LOG_LEVEL.key, loglevelcomboBox.getSelectedItem());
        prefs.storeByKey(LOG_LEVEL, (String) loglevelcomboBox.getSelectedItem());

        JOptionPane.showMessageDialog(generalPanel, ResourceBundle.getBundle("translations/program_strings").getString("prefs.settingssaved"), ResourceBundle.getBundle("translations/program_strings").getString("prefs.settingssaved"), JOptionPane.INFORMATION_MESSAGE);
    }


    private void retrievePreferences() {
        // get current preferences
        ExiftoolLocationtextField.setText(prefs.getByKey(EXIFTOOL_PATH, ""));
        retrievedPreferences.put("ExiftoolLocation", prefs.getByKey(EXIFTOOL_PATH, ""));
        ImgStartFoldertextField.setText(prefs.getByKey(DEFAULT_START_FOLDER, ""));
        retrievedPreferences.put("ImgStartFolder", prefs.getByKey(DEFAULT_START_FOLDER, ""));
        UseLastOpenedFoldercheckBox.setSelected(prefs.getByKey(USE_LAST_OPENED_FOLDER, false));
        retrievedPreferences.put("UseLastOpenedFolder", String.valueOf(prefs.getByKey(USE_LAST_OPENED_FOLDER, false)));
        CheckVersioncheckBox.setSelected(prefs.getByKey(VERSION_CHECK, true));
        retrievedPreferences.put("CheckVersion", String.valueOf(prefs.getByKey(VERSION_CHECK, true)));
        localecomboBox.setSelectedItem(prefs.getByKey(PREFERRED_APP_LANGUAGE, "System default"));
        retrievedPreferences.put("locale", prefs.getByKey(PREFERRED_APP_LANGUAGE, "System default"));
        if ("jfilechooser".equals(prefs.getByKey(PREFERRED_FILEDIALOG, "jfilechooser"))) {
            JFilechooserradioButton.setSelected(true);
            AwtdialogradioButton.setSelected(false);
        } else {
            JFilechooserradioButton.setSelected(false);
            AwtdialogradioButton.setSelected(true);
        }
        retrievedPreferences.put("filedialog", prefs.getByKey(PREFERRED_FILEDIALOG, "jfilechooser"));
        loglevelcomboBox.setSelectedItem(prefs.getByKey(LOG_LEVEL, "Info"));
        retrievedPreferences.put("loglevel", prefs.getByKey(LOG_LEVEL, "Info"));
        preserveModDatecheckBox.setSelected(prefs.getByKey(PRESERVE_MODIFY_DATE, true));
        retrievedPreferences.put("preserveModDate", String.valueOf(prefs.getByKey(PRESERVE_MODIFY_DATE, true)));
        retrievedPreferences.put("udFilefilter", prefs.getByKey(USER_DEFINED_FILE_FILTER, ""));
    }

    // The  main" function of this class
    public void showDialog() {
        //setSize(750, 600);
        setTitle(ResourceBundle.getBundle("translations/program_strings").getString("preferences.title"));
        pack();
        double x = getParent().getBounds().getCenterX();
        double y = getParent().getBounds().getCenterY();
        //setLocation((int) x - getWidth() / 2, (int) y - getHeight() / 2);
        setLocationRelativeTo(null);
        String languages = StandardFileIO.readTextFileAsStringFromResource("texts/Languages.txt");
        String[] exiftoolLanguages = languages.split("\\r?\\n"); // split on new lines
        metadataLanuagecomboBox.setModel(new DefaultComboBoxModel(exiftoolLanguages));
        String Locales = StandardFileIO.readTextFileAsStringFromResource("texts/Locales.txt");
        String[] appLocales = Locales.split("\\r?\\n"); // split on new lines
        localecomboBox.setModel(new DefaultComboBoxModel(appLocales));
        filedialogexplained.setText(String.format(ProgramTexts.HTML, 500, ResourceBundle.getBundle("translations/program_strings").getString("prefs.dialogexplained")));
        logleveltext.setText(String.format(ProgramTexts.HTML, 500, ResourceBundle.getBundle("translations/program_strings").getString("prefs.logleveltext")));

        retrievePreferences();
        setVisible(true);

    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        tabbedPanel = new JTabbedPane();
        contentPanel.add(tabbedPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(900, 600), null, 1, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setMinimumSize(new Dimension(-1, -1));
        panel1.setPreferredSize(new Dimension(-1, -1));
        tabbedPanel.addTab(this.$$$getMessageFromBundle$$$("translations/program_strings", "prefs.generaltab"), panel1);
        generalPanel = new JPanel();
        generalPanel.setLayout(new GridLayoutManager(2, 1, new Insets(5, 5, 5, 5), -1, -1));
        generalPanel.setMaximumSize(new Dimension(-1, -1));
        generalPanel.setMinimumSize(new Dimension(-1, -1));
        generalPanel.setPreferredSize(new Dimension(-1, -1));
        generalPanel.setRequestFocusEnabled(false);
        panel1.add(generalPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(850, 600), null, 2, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(5, 2, new Insets(5, 5, 5, 5), -1, -1));
        generalPanel.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, Font.BOLD, -1, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        this.$$$loadLabelText$$$(label1, this.$$$getMessageFromBundle$$$("translations/program_strings", "prefs.exiftoolocation"));
        panel2.add(label1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel2.add(panel3, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ExiftoolLocationtextField = new JTextField();
        ExiftoolLocationtextField.setMinimumSize(new Dimension(300, 30));
        ExiftoolLocationtextField.setPreferredSize(new Dimension(550, 30));
        panel3.add(ExiftoolLocationtextField);
        ExiftoolLocationbutton = new JButton();
        this.$$$loadButtonText$$$(ExiftoolLocationbutton, this.$$$getMessageFromBundle$$$("translations/program_strings", "prefs.btnchoose"));
        panel3.add(ExiftoolLocationbutton);
        final JLabel label2 = new JLabel();
        Font label2Font = this.$$$getFont$$$(null, Font.BOLD, -1, label2.getFont());
        if (label2Font != null) label2.setFont(label2Font);
        this.$$$loadLabelText$$$(label2, this.$$$getMessageFromBundle$$$("translations/program_strings", "prefs.defaultimgstartdir"));
        panel2.add(label2, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel2.add(panel4, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ImgStartFoldertextField = new JTextField();
        ImgStartFoldertextField.setMinimumSize(new Dimension(300, 30));
        ImgStartFoldertextField.setPreferredSize(new Dimension(550, 30));
        ImgStartFoldertextField.setText("");
        panel4.add(ImgStartFoldertextField);
        ImgStartFolderButton = new JButton();
        this.$$$loadButtonText$$$(ImgStartFolderButton, this.$$$getMessageFromBundle$$$("translations/program_strings", "prefs.btnchoose"));
        panel4.add(ImgStartFolderButton);
        UseLastOpenedFoldercheckBox = new JCheckBox();
        this.$$$loadButtonText$$$(UseLastOpenedFoldercheckBox, this.$$$getMessageFromBundle$$$("translations/program_strings", "prefs.alwaysuselastfolder"));
        UseLastOpenedFoldercheckBox.setToolTipText("Selecting this checkbox will overrule the \"Default image start directory:\"");
        panel2.add(UseLastOpenedFoldercheckBox, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel5.setMinimumSize(new Dimension(-1, -1));
        panel5.setPreferredSize(new Dimension(-1, -1));
        tabbedPanel.addTab(this.$$$getMessageFromBundle$$$("translations/program_strings", "prefs.languagetab"), panel5);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(3, 2, new Insets(10, 20, 10, 20), -1, -1));
        panel5.add(panel6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        this.$$$loadLabelText$$$(label3, this.$$$getMessageFromBundle$$$("translations/program_strings", "prefs.metdatadisplaylang"));
        panel6.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel6.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        metadataLanuagecomboBox = new JComboBox();
        metadataLanuagecomboBox.setPreferredSize(new Dimension(300, 30));
        panel6.add(metadataLanuagecomboBox, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        this.$$$loadLabelText$$$(label4, this.$$$getMessageFromBundle$$$("translations/program_strings", "prefs.applanguage"));
        panel6.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        localecomboBox = new JComboBox();
        panel6.add(localecomboBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(5, 1, new Insets(5, 5, 5, 5), -1, -1));
        tabbedPanel.addTab(this.$$$getMessageFromBundle$$$("translations/program_strings", "prefs.system"), panel7);
        final Spacer spacer2 = new Spacer();
        panel7.add(spacer2, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        filechooserpanel = new JPanel();
        filechooserpanel.setLayout(new GridLayoutManager(2, 2, new Insets(5, 5, 5, 5), -1, -1));
        panel7.add(filechooserpanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        filechooserpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label5 = new JLabel();
        this.$$$loadLabelText$$$(label5, this.$$$getMessageFromBundle$$$("translations/program_strings", "prefs.whichdialog"));
        filechooserpanel.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        filechooserpanel.add(panel8, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        JFilechooserradioButton = new JRadioButton();
        JFilechooserradioButton.setSelected(true);
        this.$$$loadButtonText$$$(JFilechooserradioButton, this.$$$getMessageFromBundle$$$("translations/program_strings", "prefs.jfilechooser"));
        panel8.add(JFilechooserradioButton);
        AwtdialogradioButton = new JRadioButton();
        this.$$$loadButtonText$$$(AwtdialogradioButton, this.$$$getMessageFromBundle$$$("translations/program_strings", "prefs.awtdialog"));
        panel8.add(AwtdialogradioButton);
        filedialogexplained = new JLabel();
        filedialogexplained.setText("Label");
        filechooserpanel.add(filedialogexplained, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        loglevelpanel = new JPanel();
        loglevelpanel.setLayout(new GridLayoutManager(2, 3, new Insets(5, 5, 5, 5), -1, -1));
        panel7.add(loglevelpanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        loglevelpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label6 = new JLabel();
        this.$$$loadLabelText$$$(label6, this.$$$getMessageFromBundle$$$("translations/program_strings", "prefs.loglevel"));
        loglevelpanel.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        loglevelcomboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Off");
        defaultComboBoxModel1.addElement("Error");
        defaultComboBoxModel1.addElement("Warn");
        defaultComboBoxModel1.addElement("Info");
        defaultComboBoxModel1.addElement("Debug");
        defaultComboBoxModel1.addElement("Trace");
        loglevelcomboBox.setModel(defaultComboBoxModel1);
        loglevelpanel.add(loglevelcomboBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 2, false));
        final JLabel label7 = new JLabel();
        this.$$$loadLabelText$$$(label7, this.$$$getMessageFromBundle$$$("translations/program_strings", "prefs.logrestart"));
        loglevelpanel.add(label7, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        logleveltext = new JLabel();
        logleveltext.setText("Label");
        loglevelpanel.add(logleveltext, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        CheckVersioncheckBox = new JCheckBox();
        this.$$$loadButtonText$$$(CheckVersioncheckBox, this.$$$getMessageFromBundle$$$("translations/program_strings", "prefs.checknewversion"));
        panel7.add(CheckVersioncheckBox, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        preserveModDatecheckBox = new JCheckBox();
        this.$$$loadButtonText$$$(preserveModDatecheckBox, this.$$$getMessageFromBundle$$$("translations/program_strings", "prefs.presmoddate"));
        panel7.add(preserveModDatecheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPanel.add(panel9, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 1, false));
        final Spacer spacer3 = new Spacer();
        panel9.add(spacer3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel9.add(panel10, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonSave = new JButton();
        this.$$$loadButtonText$$$(buttonSave, this.$$$getMessageFromBundle$$$("translations/program_strings", "dlg.save"));
        panel10.add(buttonSave, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        this.$$$loadButtonText$$$(buttonCancel, this.$$$getMessageFromBundle$$$("translations/program_strings", "dlg.cancel"));
        panel10.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(JFilechooserradioButton);
        buttonGroup.add(AwtdialogradioButton);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    private static Method $$$cachedGetBundleMethod$$$ = null;

    private String $$$getMessageFromBundle$$$(String path, String key) {
        ResourceBundle bundle;
        try {
            Class<?> thisClass = this.getClass();
            if ($$$cachedGetBundleMethod$$$ == null) {
                Class<?> dynamicBundleClass = thisClass.getClassLoader().loadClass("com.intellij.DynamicBundle");
                $$$cachedGetBundleMethod$$$ = dynamicBundleClass.getMethod("getBundle", String.class, Class.class);
            }
            bundle = (ResourceBundle) $$$cachedGetBundleMethod$$$.invoke(null, path, thisClass);
        } catch (Exception e) {
            bundle = ResourceBundle.getBundle(path);
        }
        return bundle.getString(key);
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadLabelText$$$(JLabel component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setDisplayedMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadButtonText$$$(AbstractButton component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPanel;
    }

}
