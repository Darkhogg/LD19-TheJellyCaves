package es.darkhogg.ld19;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public enum Sound {
    DIG("dig.wav", "dig1.wav", "dig2.wav", "dig3.wav", "dig4.wav"), //
    HURT("hurt.wav"), //
    DEATH("death.wav"), //
    THROW("throw.wav"), //
    ENEMY_DEATH("enemdeath.wav"), //
    PICKUP("pickup.wav"), //
    START("start.wav"), //
    HEART("heart.wav");

    private final byte[][] datas;

    private Sound (String... names) {
        datas = new byte[names.length][];

        for (int i = 0; i < names.length; i++) {
            URL url = Sound.class.getResource(names[i]);
            
            System.out.printf("Loading Sound.%s from '%s'...%n", this, url);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (InputStream in = url.openStream()) {
                // Copy input to output
                byte[] buf = new byte[4096];
                int r;
                while ((r = in.read(buf)) != -1) {
                    out.write(buf, 0, r);
                }

            } catch (IOException exc) {
                exc.printStackTrace();
            } finally {
                datas[i] = out.toByteArray();
            }
        }
    }

    private class SoundPlayer implements Runnable {
        @Override
        public void run () {
            try {

                byte[] data = datas[(int) (Math.random() * datas.length)];

                AudioInputStream audioInput = AudioSystem.getAudioInputStream(new ByteArrayInputStream(data));
                AudioFormat audioFormat = audioInput.getFormat();
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

                SourceDataLine audioLine;

                audioLine = (SourceDataLine) AudioSystem.getLine(info);

                audioLine.open(audioFormat);

                audioLine.start();

                int bytesRead = 0;
                byte[] audioData = new byte[1024 * 128]; // 128 KiB

                try {
                    while (bytesRead != -1) {
                        bytesRead = audioInput.read(audioData);

                        if (bytesRead >= 0) {
                            audioLine.write(audioData, 0, bytesRead);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    audioLine.drain();
                    audioLine.close();
                }
            } catch (LineUnavailableException e1) {
                e1.printStackTrace();
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void play () {
        Thread sndPlayer = new Thread(new SoundPlayer());
        sndPlayer.setDaemon(true);
        sndPlayer.start();
    }
}
