package player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by duarte on 14-05-2016.
 */
public class UploadedTrack extends Track {

    private File file;
    private String filename=null;
    private InfoMusic info;

    public UploadedTrack(String filename, String clientRequested) {
        super(clientRequested);
        this.filename = filename;
        this.file=new File(System.getProperty("user.dir") + "/resources/" + filename);
        this.info = new InfoMusic(file);
        initializeStream();
    }

    private void initializeStream(){
        try {
            this.setStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            System.err.println("File for UploadedTrack " + this.filename + "not found");
        }
    }

    public InfoMusic getInfo() {
        return info;
    }

    public void setInfo(InfoMusic info) {
        this.info = info;
    }

    public String getFilename() {
        return filename;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String getStream_url() {
        return null;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getTrackName() {
        return info.getTrackName();
    }

    public double getFullTime() {
        return info.getFullTime();
    }


    public String getAuthor() {
        return info.getAuthor();
    }
}
