package database;

import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Request;
import com.soundcloud.api.Token;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;


/**
 * Created by Sonhs on 07/05/2016.
 */


public class SCComms {

    public static ApiWrapper wrapper = null;

    public SCComms() {
        File wrapperFile = new File("wrapper.ser");
        try {
            wrapper = ApiWrapper.fromFile(wrapperFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //"""""""""TODO CREATE version ov createwrapper to encapsulate that metdos/info/data
    public void create_wrapper_instance() {

        File WRAPPER_SER = new File("wrapper.ser");
        wrapper = new ApiWrapper("1bbc622f314334af39a7d712c1b0a9c4", "aa54aa4ca198d24e17513787227f3200", null, null);

        try {
            wrapper.login("sergio.mieic@gmail.com", "password", Token.SCOPE_NON_EXPIRING);
            wrapper.toFile(WRAPPER_SER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String appendGetArgs(String api, String[] args) {
        if (args != null) {
            if (args.length > 0 && args.length % 2 == 0) {
                api += "?";
                for (int i = 0, l = args.length; i < l; i += 2) {
                    if (i != 0) {
                        api += "&";
                    }
                    api += (args[i] + "=" + args[i + 1]);
                }
                if (wrapper.getToken() == null) {
                    api += ("&consumer_key=" + "1bbc622f314334af39a7d712c1b0a9c4");
                }
            }
        } else {
            api += "?consumer_key=" + "1bbc622f314334af39a7d712c1b0a9c4";
        }
        return api;
    }


    //TODO GET STREAM URL !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //stream_url = client.get(track.stream_url, :allow_redirects => true)
    //then stream from that url
    //more info : https://developers.soundcloud.com/docs/api/guide#playing

    public static void main(String args[]) {

        SCComms sc = new SCComms();

        String x = "/tracks.json?q=numb";

        String req = sc.appendGetArgs("/tracks.json", new String[]{"q", "numb"});

        System.out.println(req);

        JSONObject myObject = null;

        try {
            //String encoded = URLEncoder.encode(req, "UTF-8");

            final Request resource = Request.to(x);
            System.out.println("GET " + resource);

            HttpResponse resp = wrapper.get(resource);

            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null;) {
                    builder.append(line).append("\n");
                }
                JSONTokener tokener = new JSONTokener(builder.toString());
                JSONArray finalResult = new JSONArray(tokener);


                //!!!!!!!!!!!!!!!!!!!importante TODO verificar se o token "streamable" da track é true antes de tentar fazer stream

                for(int i = 0; i< finalResult.length(); i++)
                    System.out.println(finalResult.get(i).toString());

               /* while (iterator.hasNext()) {
                    JSONObject countyJSON = iterator.next();
                }*/

            } else {
                System.err.println("Invalid status received: " + resp.getStatusLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

     /*catch (JSONException e) {
            e.printStackTrace();
    }*/
    }
}