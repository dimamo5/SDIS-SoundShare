package player;

import streaming.ClientHandler;
import streaming.Room;

import java.io.*;

/**
 * Created by duarte on 14-05-2016.
 */
public class UploadedTrack extends Track {

    private File file;
    private String filename = null;

    public UploadedTrack(String filename, String clientRequested) {
        super(clientRequested);
        this.filename = filename;
        this.file = new File(System.getProperty("user.dir") + "/resources/" + filename);
        this.info = new InfoMusic(file);
        initializeStream();
    }

    private void initializeStream() {
        try {
            this.setStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            System.err.println("File for UploadedTrack " + this.filename + "not found");
        }
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
        return getInfo().getTrackName();
    }

    public double getFullTime() {
        return getInfo().getFullTime();
    }

    public String getAuthor() {
        return getInfo().getAuthor();
    }

    public void sendTrack(double sec, Room room, ClientHandler c) {
        byte[] buf = new byte[Room.FRAMESIZE];
        setSent(true);
        File f = getFile();
        int songTime = info.getFullTime();
        BufferedInputStream stream = new BufferedInputStream(this.getStream());

        System.out.println("Enviar " + this.getTrackName() + " - " + this.getInfo().getAuthor() + " Duration: " + this.getInfo().getFullTime());

        room.sendMusicMessage(c, this, sec);

        int chunks = (int) f.length() / Room.FRAMESIZE;
        double bytesperSec = getBytesPerSec();
        double frameToElapse = bytesperSec * sec / Room.FRAMESIZE;
        double frameToElapseRounded = Math.round(frameToElapse);

        System.out.println("Tamanho ficheiro: " + f.length() + " Bytes per sec: " + bytesperSec + " Frames passed: " + frameToElapse);

        sendTrackFromStream(room, stream, chunks, frameToElapseRounded, false, c);
    }

    @Override
    public long getBytesPerSec() {
        return (file.length() - 4) / info.getFullTime();
    }
}
