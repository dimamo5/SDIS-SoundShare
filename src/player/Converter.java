package player;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Created by m_bot on 12/05/2016.
 */
public class Converter {
    private String toConvert;
    private String resultConversion;
    private final int BITRATE = 192;
    private final int CHANNELS = 2;
    private final float SAMPLE = 44100;

    public Converter(String toConvert, String resultConversion) {
        this.toConvert = toConvert;
        this.resultConversion = resultConversion;
    }

    public boolean encodeMP3() {
        try {
            File fileConvert = new File(System.getProperty("user.dir") + "/" + toConvert);
            File fileConversion = new File(System.getProperty("user.dir") + "/" + resultConversion);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fileConvert);

            Encoder enc = new Encoder();
            EncodingAttributes att = new EncodingAttributes();
            InfoMusic inf = new InfoMusic(fileConvert);
            inf.getMusicInfo();
            att.setDuration((float)inf.getFullTime());
            AudioAttributes audioAttributes = new AudioAttributes();

            audioAttributes.setBitRate(BITRATE);
            audioAttributes.setChannels(CHANNELS);
            audioAttributes.setSamplingRate((int)SAMPLE);

            att.setAudioAttributes(audioAttributes);
            att.setFormat("mp3");

            enc.encode(fileConvert, fileConversion, att);
        } catch (EncoderException e) {
            e.printStackTrace();
            return false;
        }
        catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
