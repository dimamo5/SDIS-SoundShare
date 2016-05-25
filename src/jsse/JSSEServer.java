package jsse;

import javax.net.ssl.*;
import java.io.*;
import java.net.SocketException;

/**
 * Created by Sonhs on 19/05/2016.
 */
public class JSSEServer {

    public static void main(String[] args) {

        SSLServerSocket sslserversocket = null;
        SSLSocket sslsocket = null;

        try {
            System.setProperty("javax.net.ssl.keyStore","keystore");
            System.setProperty("javax.net.ssl.keyStorePassword","123456");

     SSLServerSocketFactory sslserversocketfactory =
                    (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

            sslserversocket =
                    (SSLServerSocket) sslserversocketfactory.createServerSocket(9999);

            System.out.println("Waiting 4 connection");
            sslsocket = (SSLSocket) sslserversocket.accept();

            System.out.println("Waiting 4 handshake");
            sslsocket.startHandshake();

            System.out.println("Receiving");

            InputStream inputstream = sslsocket.getInputStream();
            InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
            BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

            String string = null;
            while ((string = bufferedreader.readLine()) != null) {
                System.out.println(string);
                System.out.flush();
            }
        } catch (SocketException exception) {
            if(exception.toString().equals("java.net.SocketException: Connection reset")){
                System.out.println("End point(client) disconnected.");
                try {
                   if(sslsocket != null) {
                       sslsocket.close(); //close this point
                   }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            exception.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
