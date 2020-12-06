package org.hvdw.jimagefuser.view;

import org.hvdw.jimagefuser.controllers.MenuActionListener;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;

public class CreateMenu {
    
    private JMenuItem menuItem;

    public void CreateMenuBar(JFrame frame, JPanel rootPanel, JSplitPane splitPanel, JMenuBar menuBar, JMenu myMenu, JLabel OutputLabel, JProgressBar progressBar) {


        MenuActionListener mal = new MenuActionListener(frame, rootPanel, splitPanel, menuBar, OutputLabel, progressBar);

        menuItem = new JMenuItem(ResourceBundle.getBundle("translations/program_strings").getString("fmenu.preferences"));
        myMenu.setMnemonic(KeyEvent.VK_P);
        menuItem.setActionCommand("Preferences");
        menuItem.addActionListener(mal);
        myMenu.add(menuItem);
        menuItem = new JMenuItem(ResourceBundle.getBundle("translations/program_strings").getString("fmenu.exit"));
        menuItem.setMnemonic(KeyEvent.VK_X);
        menuItem.setActionCommand("Exit");
        menuItem.addActionListener(mal);
        myMenu.add(menuItem);



        // Help menu
        myMenu = new JMenu(ResourceBundle.getBundle("translations/program_strings").getString("menu.help"));
        myMenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(myMenu);
        menuItem = new JMenuItem(ResourceBundle.getBundle("translations/program_strings").getString("hmenu.jimagefuserhomepage"));
        menuItem.setActionCommand("jImageFuser homepage");
        menuItem.addActionListener(mal);
        myMenu.add(menuItem);
        //menuItem = new JMenuItem("Manual");
        //myMenu.add(menuItem);
        myMenu.addSeparator();
        menuItem = new JMenuItem(ResourceBundle.getBundle("translations/program_strings").getString("hmenu.onlinemanual"));
        menuItem.setActionCommand("Online manual");
        menuItem.addActionListener(mal);
        myMenu.add(menuItem);
        menuItem = new JMenuItem(ResourceBundle.getBundle("translations/program_strings").getString("hmenu.enfusemanual"));
        menuItem.setActionCommand("Enfuse manual");
        menuItem.addActionListener(mal);
        myMenu.add(menuItem);
        menuItem = new JMenuItem(ResourceBundle.getBundle("translations/program_strings").getString("hmenu.enfusewiki"));
        menuItem.setActionCommand("Enfuse wiki");
        menuItem.addActionListener(mal);
        myMenu.add(menuItem);
        menuItem = new JMenuItem(ResourceBundle.getBundle("translations/program_strings").getString("hmenu.aiswiki"));
        menuItem.setActionCommand("Align_image_stack wiki");
        menuItem.addActionListener(mal);
        myMenu.add(menuItem);

        /*menuItem = new JMenuItem(ResourceBundle.getBundle("translations/program_strings").getString("hmenu.youtube"));
        menuItem.setActionCommand("Youtube channel");
        menuItem.addActionListener(mal);
        myMenu.add(menuItem);*/
        // Here we add the sub menu with help topics
        myMenu.addSeparator();
        menuItem = new JMenuItem(ResourceBundle.getBundle("translations/program_strings").getString("hmenu.credits"));
        menuItem.setActionCommand("Credits");
        myMenu.add(menuItem);
        menuItem.addActionListener(mal);
        menuItem = new JMenuItem(ResourceBundle.getBundle("translations/program_strings").getString("hmenu.donate"));
        menuItem.setActionCommand("Donate");
        myMenu.add(menuItem);
        menuItem.addActionListener(mal);
        menuItem = new JMenuItem(ResourceBundle.getBundle("translations/program_strings").getString("hmenu.license"));
        menuItem.setActionCommand("License");
        menuItem.addActionListener(mal);
        myMenu.add(menuItem);
        menuItem = new JMenuItem(ResourceBundle.getBundle("translations/program_strings").getString("hmenu.translate"));
        menuItem.setActionCommand("Translate");
        menuItem.addActionListener(mal);
        myMenu.add(menuItem);
        myMenu.addSeparator();
        menuItem = new JMenuItem(ResourceBundle.getBundle("translations/program_strings").getString("hmenu.sysproginfo"));
        menuItem.setActionCommand("System/Program info");
        menuItem.addActionListener(mal);
        myMenu.add(menuItem);
        menuItem = new JMenuItem(ResourceBundle.getBundle("translations/program_strings").getString("hmenu.checkfornewversion"));
        menuItem.setActionCommand("Check for new version");
        menuItem.addActionListener(mal);
        myMenu.add(menuItem);
        menuItem = new JMenuItem(ResourceBundle.getBundle("translations/program_strings").getString("hmenu.changelog"));
        menuItem.setActionCommand("Changelog");
        menuItem.addActionListener(mal);
        myMenu.add(menuItem);
        myMenu.addSeparator();
        menuItem = new JMenuItem(ResourceBundle.getBundle("translations/program_strings").getString("hmenu.aboutexiftool"));
        menuItem.setActionCommand("About ExifTool");
        menuItem.addActionListener(mal);
        myMenu.add(menuItem);
        menuItem = new JMenuItem(ResourceBundle.getBundle("translations/program_strings").getString("hmenu.aboutjimagefuser"));
        menuItem.setActionCommand("About jImageFuser");
        menuItem.addActionListener(mal);
        myMenu.add(menuItem);

        // Finally add menubar to the frame
        frame.setJMenuBar(menuBar);

    }
}
