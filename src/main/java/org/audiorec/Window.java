package org.audiorec;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Window extends JFrame {

    private static JFrame frame;
    private static JFileChooser chooser;
    private static String chooserTitle;
    private static String path;

    private static boolean recording;
    private static boolean fileSelected;

    public static void render(String title) {
        JFrame.setDefaultLookAndFeelDecorated(true);

        frame = new JFrame(title);

        JPanel panel = new JPanel();

        frame.setLayout(new FlowLayout());
        frame.add(panel);

        JButton pathSelector = new JButton("<html><b><u>Select Folder</u></b><br>no path selected</html>");
        frame.add(pathSelector);

        pathSelector.addActionListener(e -> {
            chooserTitle = "Select Save Path";

            chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle(chooserTitle);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                fileSelected = true;
                path = chooser.getSelectedFile().getAbsolutePath();
                System.out.println("Save location: "
                        +  path);
            } else {
                fileSelected = false;
                System.out.println("No Selection ");
            }

            pathSelector.setText("<html><b><u>Select Folder</u></b><br>" + path + "</html>");
            frame.setSize(panel.getPreferredSize());
            frame.pack();
        });

        JButton start = new JButton("Start");
        frame.add(start);
        start.addActionListener(e -> {recording = true;});

        JButton end = new JButton("End");
        frame.add(end);
        end.addActionListener(e -> {recording = false;});

        frame.setIconImage(new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR));
        frame.setSize(panel.getPreferredSize());
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static boolean getRecStatus() {
        return recording;
    }

    public static boolean getFileSelected() {
        return fileSelected;
    }

    public static String getSavePath() {
        return path;
    }
}
