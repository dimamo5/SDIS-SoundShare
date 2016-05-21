package player;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by duarte on 14-05-2016.
 */
public class UploadedTrack extends Track {

    private File file;
    private String filename=null;
    private InfoMusic track_info;

    public UploadedTrack(String filename, String clientRequested) {
        super(clientRequested);
        this.filename = filename;
        this.file=new File(System.getProperty("user.dir") + "/resources/" + filename);
        this.track_info = new InfoMusic(file);
    }

    public InfoMusic getTrack_info() {
        return track_info;
    }

    public void setTrack_info(InfoMusic track_info) {
        this.track_info = track_info;
    }

    public String getFilename() {
        return filename;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getTrackName() {
        return track_info.getTrackName();
    }

    public double getFullTime() {
        return track_info.getFullTime();
    }


    public String getAuthor() {
        return track_info.getAuthor();
    }
}
