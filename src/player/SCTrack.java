package player;

import org.json.JSONException;
import org.json.JSONObject;
import soundcloud.SCComms;
import streaming.Room;
import util.ServerSingleton;

import java.io.BufferedInputStream;
import java.util.Map;

/**
 * Created by Sonhs on 20/05/2016.
 */
public class SCTrack extends Track{

    private JSONObject track;
    private String stream_url;
    protected final static int stream_kbps = 128;

    public SCTrack(String clientRequested, JSONObject track) throws JSONException {
        super(clientRequested);
        this.track = track;
        stream_url = track.getString("stream_url");
        info = getMusicInfo(track);
        this.setStream(ServerSingleton.getInstance().getSoundCloudComms().getStreamData(ServerSingleton.getInstance().getSoundCloudComms().get_stream_from_url(stream_url)));
    }

    private InfoMusic getMusicInfo(JSONObject track) {
        InfoMusic info = null;
        Map info_track = SCComms.get_info(track);
        info = new InfoMusic((String) info_track.get("title"),
                (String) info_track.get("author"),
                (int) info_track.get("duration"),(long) info_track.get("original_content_size"));
        System.out.println("dur: " + info_track.get("duration"));

        return info;
    }

    public JSONObject getTrack() {
        return track;
    }

    public void setTrack(JSONObject track) {
        this.track = track;
    }

    public String getStream_url() {
        return stream_url;
    }

    @Override
    public void sendTrack(double sec, Room room){
        BufferedInputStream stream = new BufferedInputStream(this.getStream());

        setSent(true);
        long bytesperSec = getBytesPerSec();
        double frameToElapse = bytesperSec * sec / Room.FRAMESIZE;
        double frameToElapseRounded = Math.round(frameToElapse);
        System.out.println("Enviar " + this.getTrackName() + " - " + this.getInfo().getAuthor() + " Duration: " + this.getInfo().getFullTime());

        room.sendNewTrackMessageToAllClients(this, sec);
        sendTrackFromStream(room, stream, frameToElapseRounded, true);
    }

    @Override
    public long getBytesPerSec() {
        return getStream_kbps();
    }

    public void setStream_url(String stream_url) {
        this.stream_url = stream_url;
    }

    public double getFullTime() {
        return info.getFullTime();
    }

    public String getAuthor() {
        return info.getAuthor();
    }

    public String getTrackName() {
        return info.getTrackName();
    }

    public static int getStream_kbps() {
        return stream_kbps;
    }

}
