package player;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por organizar a playlist.
 */
public class Playlist {
    // TODO: 14-05-2016 Verificar se um ArrayList é efectivamente a melhor maneira de ordernarmos a música. Eu acho que é capaz de haver maneiras melhores by:Duarte
    private ArrayList<UploadedTrack> playlist = new ArrayList<>();
    private int i = -1;
    private boolean repeat = false;

    public UploadedTrack getCurrentTrack(){
        return playlist.get(i);
    }

    public UploadedTrack getNextTrack() {
        if(i+1 >= playlist.size() && !isRepeat()){
            return null;
        }else{
            if(i >= playlist.size() && isRepeat()){
                i = -1;
            }
            UploadedTrack nextUploadedTrack = playlist.get(i+1);

            if(i >= playlist.size() && isRepeat()) {
                i = -1;
            }

            return nextUploadedTrack;
        }
    }

    public boolean skipTrack(){
        if(i+1 >= playlist.size() && !isRepeat()){
            return false;
        }else{
            if(i >= playlist.size() && isRepeat()){
                i = -1;
            }
            i++;

            if(i >= playlist.size() && isRepeat()) {
                i = -1;
            }
            return true;
        }
    }

    public void  addRequestedTrack(String music, String clientNo) {
        UploadedTrack uploadedTrack = new UploadedTrack(music,clientNo);
        if(uploadedTrack.getFile()!=null){
            playlist.add(uploadedTrack);
            if(i==-1){
                i=0;
            }
        }

    }

    public UploadedTrack getPreviousTrack() {
        if(i == 0 && !isRepeat()){
            return null;
        }else{

            if(i==0 && isRepeat()){
                i = playlist.size();
            }

            i--;
            UploadedTrack previousUploadedTrack = playlist.get(i);

            return previousUploadedTrack;
        }
    }

    public boolean isRepeat() {
        return repeat;
    }

    public boolean isEmpty(){
        return playlist.isEmpty();
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