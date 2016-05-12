package server;

import org.tritonus.share.sampled.file.TAudioFileFormat;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by m_bot on 12/05/2016.
 */
public class InfoMusic {

    private String filename;
    private String title;
    private String author;
    private int hours;
    private int minutes;
    private int seconds;
    private int fullTime;

    public InfoMusic(String filename) {
        this.filename = filename;
    }

    public void splitToComponentTimes(Long biggy)
    {
        int hours = (int) (biggy / 3600);
        int remainder = (int) (biggy - hours * 3600);
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;

        this.hours = hours;
        this.minutes = mins;
        this.seconds = secs;
    }

    public void getMusicInfo() {
        File file = new File(System.getProperty("user.dir") + "/" + filename);
        AudioFileFormat baseFileFormat = null;
        AudioFormat baseFormat = null;
        try {
            baseFileFormat = AudioSystem.getAudioFileFormat(file);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        baseFormat = baseFileFormat.getFormat();
        // TAudioFileFormat properties
        if (baseFileFormat instanceof TAudioFileFormat) {
            Map properties = ((TAudioFileFormat) baseFileFormat).properties();
            String key = "duration";
            long val = (long) properties.get(key);
            val = val / 1000000;
            this.fullTime = (int) val;
            this.splitToComponentTimes(val);

            key = "title";
            this.title = (String) properties.get(key);

            key = "author";
            this.author = (String) properties.get(key);
        }
    }

    public String getFilename() {
        return filename;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getFullTime() {
        return fullTime;
    }
}
