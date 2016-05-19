package player;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por organizar a playlist.
 */
public class Playlist {
    // TODO: 14-05-2016 Verificar se um ArrayList é efectivamente a melhor maneira de ordernarmos a música. Eu acho que é capaz de haver maneiras melhores by:Duarte
    private ArrayList<Track> playlist = new ArrayList<>();
    private int i = -1;
    private boolean repeat = false;

    public InfoMusic getCurrentTrack(){
        return playlist.get(i);
    }

    public InfoMusic getNextTrack() {
        if(i >= playlist.size() && !isRepeat()){
            return null;
        }else{
            if(i >= playlist.size() && isRepeat()){
                i = -1;
            }
            i++;
            Track nextTrack = playlist.get(i);

            if(i >= playlist.size() && isRepeat()) {
                i = -1;
            }

            return nextTrack;
        }
    }

    public void  addRequestedTrack(String music, String clientNo) {
        Track track = new Track(music,clientNo);
        if(track.getFile()!=null)
            playlist.add(track);

    }

    public Track getPreviousTrack() {
        if(i == 0 && !isRepeat()){
            return null;
        }else{

            if(i==0 && isRepeat()){
                i = playlist.size();
            }

            i--;
            Track previousTrack = playlist.get(i);

            return previousTrack;
        }
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