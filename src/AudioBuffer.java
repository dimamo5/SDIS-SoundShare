import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by diogo on 18/05/2016.
 */
public class AudioBuffer extends InputStream{

    ArrayList<Byte> buf;
    int pos;

    AudioBuffer(){
        buf=new ArrayList<>();
        pos=0;
    }
    public synchronized int read() throws IOException {
        if (!buf.isEmpty()) {
            return -1;
        }
        return buf.get(pos);
    }
    public synchronized int read(byte[] bytes, int off, int len) throws IOException {
        len = Math.min(len, buf.size());
        Iterator it= buf.iterator();
        int i =0;
        while(i<len){
            bytes[i]=(byte) it.next();
            it.remove();
            i++;
        }
        //pos=pos+len;
        System.out.println("reading: "+ pos);
        return len;
    }

    public synchronized void write(byte[] buffer){
        for (byte aBuffer : buffer) {
            this.buf.add(aBuffer);
        }

    }
}
