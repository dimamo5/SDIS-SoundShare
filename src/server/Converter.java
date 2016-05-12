package server;

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
            AudioAttributes audioAttributes = new AudioAttributes();

            audioAttributes.setBitRate(audioInputStream.getFormat().getSampleSizeInBits());
            audioAttributes.setChannels(audioInputStream.getFormat().getChannels());
            audioAttributes.setSamplingRate((int)audioInputStream.getFormat().getSampleRate());

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
