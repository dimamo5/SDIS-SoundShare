import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AudioBuffer extends InputStream{

    ArrayList<Byte> buf = new ArrayList<>();
    int pos;
    private final int sizeToClean=64000;

    AudioBuffer(){
        pos=0;
    }
    public synchronized int read() throws IOException {
        if (buf.size()==0) {
            return -1;
        }
        pos++;
        return buf.get(pos);
    }
    public synchronized int read(byte[] bytes, int off, int len) throws IOException {
        len = Math.min(len, buf.size());

        return len;
    }

    public synchronized void write(byte[] buffer){
        

    }
}
