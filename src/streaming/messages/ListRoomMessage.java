package streaming.messages;

import streaming.Room;

import java.util.*;

/**
 * Created by m_bot on 22/05/2016.
 */
public class ListRoomMessage extends Message {

    public ListRoomMessage(Hashtable<Integer, Room> rooms) {
        super();
        this.arg = new String[rooms.size()];
        this.type=Type.LIST_ROOM;
        int i = 0;

        Iterator it = rooms.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            Room r = (Room) pair.getValue();

            this.arg[i] = String.valueOf(r.getPort());
            this.arg[i + 1] = r.getPlaylist().getCurrentTrack().getInfo().getTitle();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type.toString());
        sb.append(" Rooms available: ");
        for (int i = 0; i < arg.length; i = i + 2)  {
            sb.append(arg[i]);
            sb.append(" ");
            sb.append(arg[i + 1]);
            sb.append(" ");
        }
        return sb.toString();
    }
}
