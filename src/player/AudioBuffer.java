package player;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

@Deprecated
public class AudioBuffer extends InputStream {

    ArrayList<Byte> b = new ArrayList<>();
    LinkedBlockingQueue<Byte> queue = new LinkedBlockingQueue<>();
    int pos;
    private final int sizeToClean = 128000;

    AudioBuffer() {
        pos = 0;
    }

    public synchronized int read() throws IOException {
        if (b.size() == 0) {
            return -1;
        }
        pos++;
        return b.get(pos);
    }

    public synchronized int read(byte[] bytes, int off, int len) throws IOException {
        len = Math.min(len, queue.size());
        for (int i = off; i < len; i++) {
            //bytes[i]=b.get(pos+i);
            try {
                bytes[i] = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        pos += len;
        if (pos > sizeToClean) {
            System.out.println("Antes:" + pos);
            //cleanBuffer();
            System.out.println("Depois:" + pos);

        }
        return len;
    }

    private void cleanBuffer() {
        b.subList(0, sizeToClean - 8000).clear();
        pos -= sizeToClean - 8000;
    }

    public synchronized void write(byte[] buffer) {
        for (Byte b1 : buffer) {
            //b.add(b1);
            try {
                queue.put(b1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
