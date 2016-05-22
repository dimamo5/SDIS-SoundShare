package streaming.messages;

import player.Track;

/**
 * Created by duarte on 21-05-2016.
 */
public class MusicMessage extends Message{
    public MusicMessage(Track track, double sec){
        super(Type.MUSIC);
        this.arg=new String[4];
        this.arg[0]= track.getTrackName();
        this.arg[1]= track.getInfo().getAuthor();
        this.arg[2]=String.valueOf(track.getInfo().getFullTime());
        this.arg[3]=String.valueOf(sec);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type.toString());
        sb.append(" Currently playing music ");
        sb.append(arg[0]);
        sb.append(" from ");
        sb.append(arg[1]);
        sb.append(" with duration ");
        sb.append(arg[2]);
        sb.append(" seconds.");
        return sb.toString();
    }
}
