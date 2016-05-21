package player;

import org.json.JSONException;
import org.json.JSONObject;
import soundcloud.SCComms;
import util.ServerSingleton;

import java.io.File;
import java.util.Map;

/**
 * Created by Sonhs on 20/05/2016.
 */
public class SCTrack extends Track{

    private InfoMusic info;
    private JSONObject track;
    private String stream_url;

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
                (int) info_track.get("duration"));

        return info;
    }


    public InfoMusic getInfo() {
        return info;
    }

    public void setInfo(InfoMusic info) {
        this.info = info;
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

    public File getFile() {
        return null;
    }

}
