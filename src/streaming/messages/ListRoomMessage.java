package streaming.messages;

import auth.Token;
import player.Track;
import streaming.Room;

import java.util.*;

/**
 * Created by m_bot on 22/05/2016.
 */
public class ListRoomMessage extends Message {

    public ListRoomMessage(Hashtable<Integer, Room> rooms) {
        super(Type.ROOM_LIST);
        this.args = new String[rooms.size() * 2];
        int i = 0;

        Iterator it = rooms.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Room r = (Room) pair.getValue();

            this.args[i] = String.valueOf(r.getPort());
            Track t = r.getPlaylist().getCurrentTrack();
            if (t == null)
                this.args[i + 1] = "No music playing";
            else this.args[i + 1] = t.getInfo().getTitle();
            i = i + 2;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type.toString());
        sb.append(" Rooms available: ");
        for (int i = 0; i < args.length; i = i + 2) {
            sb.append(args[i]);
            sb.append(" ");
            sb.append(args[i + 1]);
            sb.append(" ");
        }
        return sb.toString();
    }
}
