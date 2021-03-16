package org.audiorec;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Application {

    private static final String TITLE = "Audio Recorder";

    public volatile boolean mRunning;
    public volatile boolean fileSelected;

    public static Application app = null;

    static {
        try {
            app = new Application();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mainloop() {
        SwingUtilities.invokeLater(() -> {

            /**
             * Substance not supported in JDK 8
             *try {
             *    UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceNightShadeLookAndFeel");
             *} catch (Exception e) {
             *    e.printStackTrace();
             *}
             */

            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("GTK+".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (UnsupportedLookAndFeelException e) {
                // handle exception
            } catch (ClassNotFoundException e) {
                // handle exception
            } catch (InstantiationException e) {
                // handle exception
            } catch (IllegalAccessException e) {
                // handle exception
            }

            try {
                Window.render(TITLE);
            } catch (Exception e ){
                e.printStackTrace();
            }
        });

        while (!fileSelected) {
            Thread.yield();
            fileSelected = Window.getFileSelected();
        }

        System.out.println("Path Selected");

        Recorder.captureAudio();

        while (!mRunning) {
            Thread.yield();
            mRunning = Window.getRecStatus();
        }

        File wavFile = new File(Window.getSavePath() + "/record.wav");

        final Recorder recorder = new Recorder();

        Thread recordThread = new Thread(() -> {
            try {
                System.out.println("Start recording...");
                recorder.start();
            } catch (LineUnavailableException ex) {
                ex.printStackTrace();
                System.exit(-1);
            }
        });

        recordThread.start();

        while (mRunning) {
            Thread.yield();
            mRunning = Window.getRecStatus();
        }

        try {
            recorder.stop();
            System.out.println("STOPPED");
            recorder.save(wavFile);
            System.out.println("SAVED");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.out.println("DONE");
    }
}
