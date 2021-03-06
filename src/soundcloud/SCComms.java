package soundcloud;

import com.soundcloud.api.*;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Sonhs on 07/05/2016.
 */

public class SCComms {

    private static ApiWrapper wrapper = null;

    public SCComms() {

        ///create_wrapper_instance();
        File wrapperFile = new File("wrapper.ser");
        try {
            wrapper = ApiWrapper.fromFile(wrapperFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /*
        Initalizes the wrapper instance and serializes it, only have to be executed if no wrapper instance exists or outdated
    */
    public void create_wrapper_instance() {

        File WRAPPER_SER = new File("wrapper2.ser");
        wrapper = new ApiWrapper("1bbc622f314334af39a7d712c1b0a9c4", "aa54aa4ca198d24e17513787227f3200", null, null);

        try {
            wrapper.login("sergio.mieic@gmail.com", "password", Token.SCOPE_NON_EXPIRING);
            wrapper.toFile(WRAPPER_SER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Searchs for a track on SoundCloud
     *
     * @param track_name user input to get searched
     * @returns the search's result's list (can be empty if no valid result found) or null if any error ocurred
     */
    public JSONArray search_for_track(String track_name) {
        JSONArray result = null, streamable_tracks = null;

        String request = appendGetArgs("/tracks.json", new String[]{"q", track_name});

        try {
            final Request resource = Request.to(request);

            HttpResponse resp = wrapper.get(resource);

            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                /* Get contents from HTTP Response*/
                BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null; ) {
                    builder.append(line).append("\n");
                }
                JSONTokener tokener = new JSONTokener(builder.toString());
                result = new JSONArray(tokener);

                //Fills JSONArray only with streamable tracks
                streamable_tracks = new JSONArray();

                for (int i = 0; i < result.length(); i++) {
                    JSONObject track = (JSONObject) result.get(i);

                    if (track.getBoolean("streamable")) {
                        streamable_tracks.put(track);
                    }
                }
            } else {
                System.err.println("Invalid status received: " + resp.getStatusLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return streamable_tracks;
    }

    /*
        Tries to obtain stream's url location
        @param stream_url a string with the stream url to get streamable url
        @return url_location or null if any error ocurred
     */
    public String get_stream_url_location(String stream_url) {

        String stream_url_location = null;

        try {
            HttpResponse req_resp = wrapper.get(Request.to(stream_url, "allow_redirects=false"));

            //check if status is 302
            if (req_resp.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) {

                InputStream instream = req_resp.getEntity().getContent();
                String conv = get_string_from_input_stream(instream);

                JSONObject response = new JSONObject(conv);
                stream_url_location = response.getString("location");
            } else {
                System.err.println("Invalid status received: " + req_resp.getStatusLine());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stream_url_location;
    }


    public String get_stream_from_track(JSONObject track) {
        String stream_url = null;
        try {
            stream_url = track.getString("stream_url");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return get_stream_url_location(stream_url);
    }

    public String get_stream_from_url(String stream_url) {
        return get_stream_url_location(stream_url);
    }


    /*======================= UTILS METHODS SECTION =====================*/
      /*
        Append args to obtain the string request to be (HTTP)executed
        @param request (partial request) "/tracks.json"
        @param args search args to be concatenated to the @request
     */
    private String appendGetArgs(String request, String[] args) {
        if (args != null) {
            if (args.length > 0 && args.length % 2 == 0) {
                request += "?";
                for (int i = 0, l = args.length; i < l; i += 2) {
                    if (i != 0) {
                        request += "&";
                    }
                    request += (args[i] + "=" + args[i + 1]);
                }
                if (wrapper.getToken() == null) {
                    request += ("&consumer_key=" + "1bbc622f314334af39a7d712c1b0a9c4");
                }
            }
        } else {
            request += "?consumer_key=" + "1bbc622f314334af39a7d712c1b0a9c4";
        }
        return request;
    }


    /*
        Converts InputStream to String
        @param is the inpustream to be converted
        @retuns the string result of the conversion
     */
    private String get_string_from_input_stream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;

        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    /*
       Receives a stream url link location of a track and plays it
       @param stream_url the stream url
     */
    private void play(String stream_url) {
        Player mp3player = null;
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new URL(stream_url).openStream());
            mp3player = new Player(in);
            mp3player.play();
        } catch (MalformedURLException ex) {
        } catch (IOException e) {
        } catch (JavaLayerException e) {
        } catch (NullPointerException ex) {
        }
    }


    public static Map get_info(JSONObject track) {
        Map info = new HashMap<String, String>();

        try {
            info.put("title", track.get("title"));
            info.put("duration", track.get("duration"));
            info.put("author", track.getJSONObject("user").get("username"));
            info.put("original_content_size", track.getLong("original_content_size"));
            info.put("stream", track.getString("stream_url"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return info;
    }

    /*
       Receives a stream url link location of a track and returns the respective InputStream
       @param stream_url the stream url
       @return InputStream data
     */
    public InputStream getStreamData(String stream_url) {

        InputStream in = null;

        try {
            in = new BufferedInputStream(new URL(stream_url).openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return in;
    }

    /*===================================================================================*/

    public static void main(String args[]) throws JSONException {

        SCComms sc = new SCComms();

        //System.out.println(sc.get_info((JSONObject) sc.search_for_track("numb").get(0)).toString());

        //plays the first track from search's result list
        sc.play(sc.get_stream_from_track((JSONObject) sc.search_for_track("Black Friday").get(0)));
    }
}