package org.hvdw.jimagefuser.view;

import org.apache.commons.lang3.ArrayUtils;
import org.hvdw.jimagefuser.MyVariables;
import org.hvdw.jimagefuser.Utils;
import org.hvdw.jimagefuser.controllers.ImageFunctions;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;


/**
 * Version one of this viewer checked if an image was smaller than screen size or not and would show an image in a frame wirh borders at the exact size.
 * This version simply displays images full screen as 99.9% of all images are bigger than screen size and it makes it easier for previous/next buttons
 * And one singel view is eaiser for the eyes than a window constantly resizing.
 */
public class JavaImageViewer {
    private final static ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(JavaImageViewer.class);
    public BufferedImage resizedImg = null;
    public int panelWidth = 0;
    public int panelHeight = 0;


    private BufferedImage ResizeImage(File image, int orientation) {

        int scrwidth = MyVariables.getScreenWidth();
        int scrheight = MyVariables.getScreenHeight();
        try {
            BufferedImage img = ImageIO.read(new File(image.getPath().replace("\\", "/")));
            if (orientation > 1) {
                resizedImg = ImageFunctions.rotate(img, orientation);
                resizedImg = ImageFunctions.scaleImageToContainer(resizedImg, scrwidth, scrheight);
            } else { // No rotation necessary
                resizedImg = ImageFunctions.scaleImageToContainer(img, scrwidth, scrheight);
            }
        } catch (IOException iex) {
            logger.error("error in resizing the image {}", iex);
        }
        return resizedImg;
    }


    private void LoadShowImage(JLabel ImgLabel, JLabel infoLabel, String whichaction) {
        int newindex;
        boolean bde = false;
        int[] basicdata = {0, 0, 999, 0, 0, 0, 0, 0};
        File image = MyVariables.getCurrentFileInViewer();
        File[] files = MyVariables.getLoadedFiles();
        int curIndex = ArrayUtils.indexOf(files, image);
        if ("previous".equals(whichaction)) {
            newindex = curIndex -1;
            if (newindex < 0) {
                //newindex = 0;
                newindex = files.length -1; // loop from first to last; index starts at 0
            }
        } else {
            newindex = curIndex + 1;
            if (newindex >= files.length) {
                newindex = 0; // loop from last to first
            }
        }
        logger.debug("current index {}; new index {}", curIndex, newindex);
        image = files[newindex];
        MyVariables.setCurrentFileInViewer(image);
        try {
            basicdata = ImageFunctions.getbasicImageData(image);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            bde = true;
        }
        if (bde) {
            // We had some error. Mostly this is the orientation
            basicdata[2]= 1;
        }

        String fileName = image.getName();
        String imginfo = Utils.returnBasicImageDataString(fileName, "OneLineHtml");
        infoLabel.setText(imginfo);
        BufferedImage resizedImg = ResizeImage(image, basicdata[2]);
        ImageIcon icon = new ImageIcon(resizedImg);
        ImgLabel.setIcon(icon);
    }


    public class ArrowAction extends AbstractAction {

        private String cmd;
        private JFrame frame;
        private JLabel ImgLabel;
        private JLabel infoLabel;

        public ArrowAction(String cmd, JFrame frame, JLabel ImgLabel, JLabel infoLabel) {
            this.cmd = cmd;
            this.frame = frame;
            this.ImgLabel = ImgLabel;
            this.infoLabel = infoLabel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (cmd.equalsIgnoreCase("Escape")) {
                logger.debug("The Escape key was pressed!");
                frame.dispose();
            } else if (cmd.equalsIgnoreCase("LeftArrow")) {
                logger.debug("The left arrow was pressed!");
                LoadShowImage(ImgLabel, infoLabel, "previous");
            } else if (cmd.equalsIgnoreCase("RightArrow")) {
                logger.debug("The right arrow was pressed!");
                LoadShowImage(ImgLabel, infoLabel, "next");
            }
        }
    }


    /**
     * This is the imageviewer. Currently with close/previous/next buttons.
     * Later we might make it a time based slideshow also with pause play buttons.
     **/
    public void ViewImageInFullscreenFrame (String viewOption) {
        BufferedImage img = null;
        String fileName = "";
        File image = null;
        boolean frameborder = false;

        String ImgPath = MyVariables.getSelectedImagePath();
        image = new File(ImgPath);
        MyVariables.setCurrentFileInViewer(image);
        fileName = image.getName();

        //Application.OS_NAMES currentOsName = getCurrentOsName();
        //String filenameExt = Utils.getFileExtension(MyVariables.getSelectedImagePath());

        try {
            img = ImageIO.read(image);
        } catch (IOException ioe) {
            logger.error("error loading image {}", ioe.toString());
        }

        JFrame frame = new JFrame(fileName);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel imageViewPane = new JPanel();
        frame.setContentPane(imageViewPane);
        frame.setIconImage(Utils.getFrameIcon());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int scrwidth = screenSize.width;
        MyVariables.setScreenWidth(screenSize.width);
        int scrheight = screenSize.height;
        MyVariables.setScreenHeight(screenSize.height);
        float scrratio = scrwidth / scrheight;
        int[] basicdata = {0, 0, 9999, 0, 0, 0, 0, 0};
        boolean bde = false;


        try {
            basicdata = ImageFunctions.getbasicImageData(image);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            bde = true;
        }
        if (bde) {
            // We had some error. Mostly this is the orientation
            basicdata[2] = 1;
        }

        resizedImg = ResizeImage(image, basicdata[2]);

        String imginfo = Utils.returnBasicImageDataString(fileName, "OneLineHtml");

        frame.setTitle(Utils.returnBasicImageDataString(fileName, "OneLine"));
        JPanel thePanel = new JPanel(new BorderLayout());
        thePanel.setBackground(Color.white);
        thePanel.setOpaque(true);
        JLabel ImgLabel = new JLabel();
        ImgLabel.setHorizontalAlignment(JLabel.CENTER);
        ImgLabel.setVerticalAlignment(JLabel.CENTER);
        // Create the top info panel
        JPanel InfoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        //InfoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel infoLabel = new JLabel();
        infoLabel.setText(imginfo);
        InfoPanel.add(infoLabel);
        InfoPanel.setPreferredSize(new Dimension(screenSize.width - 100, 25));

        //create the bottom button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));


        if ("singleimage".equals(viewOption)) {
            // Text close button for single image
            JButton btnTextClose = new JButton();
            btnTextClose.setText(ResourceBundle.getBundle("translations/program_strings").getString("dlg.close"));
            btnTextClose.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.dispose();
                }
            });
            buttonPanel.add(btnTextClose);
        } else {
            JButton btnClose = new JButton();
            btnClose.setIcon(new ImageIcon(getClass().getResource("/icons/baseline_stop_black_24dp.png")));
            //btnClose.setIcon(new ImageIcon(getClass().getResource("/icons/outline_stop_black_24dp.png")));
            btnClose.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.dispose();
                }
            });
            JButton btnSkipPrevious = new JButton();
            btnSkipPrevious.setIcon(new ImageIcon(getClass().getResource("/icons/baseline_skip_previous_black_24dp.png")));
            btnSkipPrevious.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    LoadShowImage(ImgLabel, infoLabel,"previous");
                }
            });
            JButton btnSkipNext = new JButton();
            btnSkipNext.setIcon(new ImageIcon(getClass().getResource("/icons/baseline_skip_next_black_24dp.png")));
            btnSkipNext.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    LoadShowImage(ImgLabel, infoLabel, "next");
                }
            });

            buttonPanel.add(btnClose);
            buttonPanel.add(btnSkipPrevious);
            buttonPanel.add(btnSkipNext);
            buttonPanel.setPreferredSize(new Dimension(75, 45));
        }

        // Create keybindings. No issues with focusing like the keylistener
        InputMap im = imageViewPane.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = imageViewPane.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Escape");
        am.put("Escape", new ArrowAction("Escape", frame, ImgLabel, infoLabel));

        if ("multiimages".equals(viewOption)) {
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "RightArrow");
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "LeftArrow");
            am.put("RightArrow", new ArrowAction("RightArrow", frame, ImgLabel, infoLabel));
            am.put("LeftArrow", new ArrowAction("LeftArrow", frame, ImgLabel, infoLabel));
            // End of the key bindings
        }

        thePanel.add(InfoPanel, BorderLayout.PAGE_START);
        ImageIcon icon = new ImageIcon(resizedImg);
        ImgLabel.setIcon(icon);
        thePanel.add(ImgLabel, BorderLayout.CENTER);
        thePanel.add(buttonPanel, BorderLayout.PAGE_END);
        //thePanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
        thePanel.setPreferredSize(new Dimension(screenSize.width, screenSize.height - 30));
        frame.add(thePanel);

        //if (!frameborder) {
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //frame.setUndecorated(true) will create a borderless window. Only use in slideshow where we have buttons anyway.
        frame.setUndecorated(true);
        //}
        frame.pack();
        frame.setVisible(true);

    }
}
