package soundcloud;

import org.json.JSONException;
import org.json.JSONObject;
import player.SCTrack;

/**
 * Created by duarte on 21-05-2016.
 */
public class TrackGetter {
    private SCComms comms = new SCComms();

    public TrackGetter(SCComms comms) {
        this.comms = comms;
    }

    public SCTrack getTrackByName(String songName, String clientNo) throws JSONException {
        JSONObject track = (JSONObject) comms.search_for_track(songName).get(0);
        SCTrack scTrack = new SCTrack(clientNo, track);
        return scTrack;

    }

    // TODO: 21-05-2016 Não está feito, falta chamada no SCComms para receber uma track a partir do url
    public SCTrack getTrackByURL(String url) throws JSONException {
        return new SCTrack("client01", new JSONObject());
    }

}
