package org.audiorec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;

public class Recorder {

    private static final int PORT = 2;

    private static final int BUFFER_SIZE = 4096;
    private ByteArrayOutputStream recordBytes;
    private TargetDataLine audioLine;
    private AudioFormat format;
    private Mixer mixer;

    private boolean isRunning;

    AudioFormat getAudioFormat() {
        float sampleRate = 44100;
        int sampleSizeInBits = 16;
        int channels = 2;
        int frameSize = 4;
        int frameRate = 44100;
        boolean bigEndian = false;
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                sampleRate, sampleSizeInBits, channels, frameSize, frameRate, bigEndian);
    }

    public static void captureAudio() {
        try {
            Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
            System.out.println("Available mixers:");
            for (Mixer.Info info : mixerInfo) {
                System.out.println(info.getName());
            }//end for loop
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() throws LineUnavailableException {
        format = getAudioFormat();
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
        Mixer mixer = AudioSystem.getMixer(mixerInfo[PORT]);

        // checks if system supports the data line
        if (!AudioSystem.isLineSupported(info)) {
            throw new LineUnavailableException(
                    "The system does not support the specified format.");
        }

        audioLine = (TargetDataLine) mixer.getLine(info);

        audioLine.open();
        audioLine.start();

        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = 0;

        recordBytes = new ByteArrayOutputStream();
        isRunning = true;

        while (isRunning) {
            bytesRead = audioLine.read(buffer, 0, buffer.length);
            recordBytes.write(buffer, 0, bytesRead);
        }
    }

    public void stop() throws IOException {
        isRunning = false;

        if (audioLine != null) {
            audioLine.stop();
            audioLine.close();
        }
    }

    public void save(File wavFile) throws IOException {
        byte[] audioData = recordBytes.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
        AudioInputStream audioInputStream = new AudioInputStream(bais, format,
                audioData.length / format.getFrameSize());

        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, wavFile);

        audioInputStream.close();
        recordBytes.close();
    }
}
