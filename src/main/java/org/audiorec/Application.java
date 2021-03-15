package org.audiorec;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

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
            try {
                UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceNightShadeLookAndFeel");
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Window.render(TITLE);
            } catch (Exception e ){
                e.printStackTrace();
            }
        });

        while (!fileSelected) {
            Thread.onSpinWait();
            fileSelected = Window.getFileSelected();
        }

        System.out.println("Path Selected");

        Recorder.captureAudio();

        while (!mRunning) {
            Thread.onSpinWait();
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
            Thread.onSpinWait();
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
