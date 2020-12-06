package org.hvdw.jimagefuser.controllers;

import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.concurrent.Executor;

public class CommandRunner {
    public final static ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(CommandRunner.class);

    /*
     * All exiftool commands go through this method
     */
    public static String runCommand(List<String> cmdparams) throws InterruptedException, IOException {

        StringBuilder res = new StringBuilder();
        logger.debug("commandrunner {}", cmdparams.toString());

        ProcessBuilder builder = new ProcessBuilder(cmdparams);
        logger.trace("Did ProcessBuilder builder = new ProcessBuilder(cmdparams);");
        try {
            builder.redirectErrorStream(true);
            Process process = builder.start();
            //Use a buffered reader to prevent hangs on Windows
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                res.append(line).append(System.lineSeparator());
                logger.trace("tasklist: " + line);
            }
            process.waitFor();
        } catch (IOException e) {
            logger.error("IOException error", e);
            res.append("IOException error")
                    .append(System.lineSeparator())
                    .append(e.getMessage());
        }
        return res.toString();
    }

    /*
     * This shows the output of exiftool after it has run
     */
    public static void outputAfterCommand(String output) {
        JTextArea textArea = new JTextArea(output);
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        scrollPane.setPreferredSize(new Dimension(500, 500));
        JOptionPane.showMessageDialog(null, scrollPane, "Output from the given command", JOptionPane.INFORMATION_MESSAGE);
    }

    /*
     * This executes the commands via runCommand and shows/hides the progress bar
     * This one is special for the output windows and has 2 parameters
     */
    public static void runCommandWithProgressBar(List<String> cmdparams, JProgressBar progressBar, boolean output) {
        // Create executor thread to be able to update my gui when longer methods run
        Executor executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String res = runCommand(cmdparams);
                    logger.debug("res is\n{}", res);
                    progressBar.setVisible(false);
                    if (output) {
                        outputAfterCommand(res);
                    }
                } catch (IOException | InterruptedException ex) {
                    logger.debug("Error executing command");
                }

            }
        });
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                progressBar.setVisible(true);
            }
        });
    }


}
