package streaming.messages;

import player.Track;

/**
 * Created by duarte on 21-05-2016.
 */
public class MusicMessage extends Message {
    public MusicMessage(Track track, double sec) {
        super(Type.MUSIC);
        this.args = new String[4];
        this.args[0] = track.getTrackName();
        this.args[1] = track.getInfo().getAuthor();
        this.args[2] = String.valueOf(track.getInfo().getFullTime());
        this.args[3] = String.valueOf(sec);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type.toString());
        sb.append(" Currently playing music ");
        sb.append(args[0]);
        sb.append(" from ");
        sb.append(args[1]);
        sb.append(" with duration ");
        sb.append(args[2]);
        sb.append(" seconds.");
        return sb.toString();
    }
}
