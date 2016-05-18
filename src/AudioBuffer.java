import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by diogo on 18/05/2016.
 */
public class AudioBuffer extends InputStream{

    ArrayDeque<Byte> buf;
    int pos;

    AudioBuffer(){
        buf=new ArrayDeque<>();
        pos=0;
    }
    public synchronized int read() throws IOException {
        if (!buf.isEmpty()) {
            return -1;
        }
        return buf.remove();
    }
    public synchronized int read(byte[] bytes, int off, int len) throws IOException {
        len = Math.min(len, buf.size());
        for(int i=off; i< len;i++){
            bytes[i]= buf.remove();
        }
        pos+=len;
        return len;
    }

    public synchronized void write(byte[] buffer){
        for (int i =0; i<buffer.length;i++) {
            this.buf.add(buffer[i]);
        }

    }
}
