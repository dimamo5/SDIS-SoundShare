import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class AudioBuffer extends InputStream{

    ArrayList<Byte> b = new ArrayList<>(1);
    int pos;
    private final int sizeToClean=128000;

    AudioBuffer(){
        pos=0;
    }
    public synchronized int read() throws IOException {
        if (b.size()==0) {
            return -1;
        }
        pos++;
        return b.get(pos);
    }
    public synchronized int read(byte[] bytes, int off, int len) throws IOException {
        len = Math.min(len, b.size());
        for (int i = off; i < len; i++) {
            bytes[i]=b.get(pos+i);
        }
        pos+=len;
        if(pos>sizeToClean){
            System.out.println("Antes:" + pos);
            cleanBuffer();
            System.out.println("Depois:" + pos);

        }
        return len;
    }

    private void cleanBuffer() {
        b.subList(0,sizeToClean-8000).clear();
        pos-=sizeToClean-8000;
    }

    public synchronized void write(byte[] buffer){
        for(Byte b1: buffer){
            b.add(b1);
        }
    }
}
