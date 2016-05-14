package player;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por organizar a playlist.
 */
public class Playlist {
    // TODO: 14-05-2016 Verificar se um ArrayList é efectivamente a melhor maneira de ordernarmos a música. Eu acho que é capaz de haver maneiras melhores by:Duarte
    private ArrayList<Track> playlist = new ArrayList<>();
    private int i = 0;
    private boolean repeat = false;

    public Track getNextTrack() {
        return new Track();
    }

    ;

    public boolean addRequestedTrack(String music, int clientNo) {
        return false;
    }

    ;

    public Track getPreviousTrack() {
        return new Track();
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public boolean removeTrack(String trackName) {
        return false;
    }

    public List getCurrentOrderedPlaylist(){
        int i = 0;
        ArrayList<String> tracks = new ArrayList<>();
        for(i = 0; i < playlist.size(); i++){
            tracks.add(playlist.get(i).getTrackName());
        }
        return tracks;
    }
}