package player;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por organizar a playlist.
 */
public class Playlist {
    // TODO: 14-05-2016 Verificar se um ArrayList é efectivamente a melhor maneira de ordernarmos a música. Eu acho que é capaz de haver maneiras melhores by:Duarte
    private ArrayList<Track> playlist = new ArrayList<>();
    private int playlist_actual_pos = -1;
    private boolean repeat = false;

    public Track getCurrentTrack() {
        if ((playlist.size() <= playlist_actual_pos) || (playlist_actual_pos == -1))
            return null;
        else return playlist.get(playlist_actual_pos);
    }

    public int getCurrentPosition() {
        return playlist_actual_pos;
    }

    public Track getNextTrack() {
        if (playlist_actual_pos + 1 >= playlist.size() && !isRepeat()) {
            return null;
        } else {
            if (playlist_actual_pos >= playlist.size() && isRepeat()) {
                playlist_actual_pos = -1;
            }
            Track next_track = playlist.get(playlist_actual_pos + 1);

            if (playlist_actual_pos >= playlist.size() && isRepeat()) {
                playlist_actual_pos = -1;
            }

            return next_track;
        }
    }

    public boolean skipTrack() {
        if (playlist_actual_pos + 1 >= playlist.size() && !isRepeat()) {
            return false;
        } else {
            if (playlist_actual_pos >= playlist.size() && isRepeat()) {
                playlist_actual_pos = -1;
            }
            playlist_actual_pos++;

            if (playlist_actual_pos >= playlist.size() && isRepeat()) {
                playlist_actual_pos = -1;
            }
            return true;
        }
    }

    public void addRequestedTrack(Track track) {
        playlist.add(track);
        if (playlist_actual_pos == -1) {
            playlist_actual_pos = 0;
        }

    }

    public void addRequestedUploadedTrack(String music, String clientNo) {
        UploadedTrack uploadedTrack = new UploadedTrack(music, clientNo);
        addRequestedTrack(uploadedTrack);

    }

    public Track getPreviousTrack() {
        if (playlist_actual_pos == 0 && !isRepeat()) {
            return null;
        } else {

            if (playlist_actual_pos == 0 && isRepeat()) {
                playlist_actual_pos = playlist.size();
            }

            playlist_actual_pos--;
            Track previousUploadedTrack = playlist.get(playlist_actual_pos);

            return previousUploadedTrack;
        }
    }

    public boolean isRepeat() {
        return repeat;
    }

    public boolean isEmpty() {
        return playlist.isEmpty();
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public boolean removeTrack(String trackName) {
        return false;
    }

    public List getCurrentOrderedPlaylist() {
        int i = 0;
        ArrayList<String> tracks = new ArrayList<>();
        for (i = 0; i < playlist.size(); i++) {
            if (playlist.get(i) instanceof SCTrack) {
                tracks.add(((SCTrack) playlist.get(i)).getInfo().getTrackName());
            } else {
                tracks.add(((UploadedTrack) playlist.get(i)).getInfo().getTrackName());
            }

        }
        return tracks;
    }

    public String getPlaylistString() {
        StringBuilder sb = new StringBuilder();
        List l = getCurrentOrderedPlaylist();

        for (int i = 0; i < l.size(); i++) {
            sb.append(i + 1);
            sb.append(": ");
            sb.append(l.get(i));
            sb.append("\n");
        }
        String playlist = sb.toString();
        return playlist;
    }
}