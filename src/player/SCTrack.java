package player;

import org.json.JSONObject;
import soundcloud.SCComms;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by Sonhs on 20/05/2016.
 */
public class SCTrack extends Track{

    private InfoMusic info;
    private JSONObject track;
    private String stream_url;

    public SCTrack(String clientRequested, JSONObject track){
        super(clientRequested);
        this.track = track;
        info = getMusicInfo(track);
    }

    private InfoMusic getMusicInfo(JSONObject track) {
        InfoMusic info = null;
        Map info_track = SCComms.get_info(track);
        info = new InfoMusic((String) info_track.get("title"),
                (String) info_track.get("author"),
                Long.parseLong((String) info_track.get("duration")));

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
}
