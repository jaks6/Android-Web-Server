package ee.ut.cs.mc.mass.restserver;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ee.ut.cs.mc.mass.restserver.ble.Sensor;
import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Jakob on 6.02.2015.
 */
public class RestServer extends NanoHTTPD {
    private static final String URI_SAMPLE_XML = "/xml";
    private static final String URI_REQUEST_INFO = "/request_info";
    private static final String URI_GET_ALTITUDE = "/altitude";
    private static final String TAG = RestServer.class.getName();

    // TODO: Allow the user to configure the base path
    private static String BASE_SERVE_PATH = "";
    private final Context context;

    private Sensor mSensor;

    public RestServer(Context context, int port) {
        super(port);
        this.context = context;
        mSensor = new Sensor(context);
    }

    @Override public Response serve(IHTTPSession session) throws FileNotFoundException {
        String Uri = session.getUri();
        String responseBody;

        if (Uri.equals(URI_REQUEST_INFO)){
            // return a HTML page showing details of the request (URI, user agents, params, etc)
            responseBody = getRequestDetailsHtml(session);

        }
        else if (Uri.equals(URI_GET_ALTITUDE)){
            responseBody = Float.toString(mSensor.getHeight()); //TODO: Move this to some service call instead of using it as a field
        }
        else {
            // DEFAULT behaviour: return file or list directory
            String path = Environment.getExternalStorageDirectory().getPath() + Uri;
            File file = new File(path);
            if (file.exists()){
                if (file.isDirectory()){
                    return new Response(getDirectoryFileListHTMLPage(file));
                }
                return new Response(Response.Status.OK,"text/plain",new FileInputStream(file));

            } else {
                responseBody = "Opening dir: '" + path + " 'failed, file doesnt exist:";
                return new Response(Response.Status.NOT_FOUND, "text/plain", responseBody);
            }
        }
        return new Response(responseBody);
    }


    private String getDirectoryFileListHTMLPage(File directory){
        String ipAddress = Util.getIpAddress();

        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head><title>Debug Server</title></head>");
        sb.append("<body>");
        sb.append("<h1>Dir '"+ directory.getPath()+ "' listing</h1>");

        File[] files = directory.listFiles();
        sb.append("<ul>");
        for (File file : files){
            sb.append("<li>");

            String adjustedString = file.getPath().replace(Environment.getExternalStorageDirectory().getPath(), "");
            sb.append("<a href='http://"+ ipAddress +  ":" + getListeningPort() + "/"+ adjustedString  + "'>" + file.getPath() + "</a>");
            sb.append("</li>");
        }
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

    private String getRequestDetailsHtml(IHTTPSession session) {
        Map<String, List<String>> decodedQueryParameters =
                decodeParameters(session.getQueryParameterString());

        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head><title>Debug Server</title></head>");
        sb.append("<body>");
        sb.append("<h1>Debug Server</h1>");

        sb.append("<p><blockquote><b>URI</b> = ").append(
                String.valueOf(session.getUri())).append("<br />");

        sb.append("<b>Method</b> = ").append(
                String.valueOf(session.getMethod())).append("</blockquote></p>");

        sb.append("<h3>Headers</h3><p><blockquote>").
                append(Util.toString(session.getHeaders())).append("</blockquote></p>");

        sb.append("<h3>Parms</h3><p><blockquote>").
                append(Util.toString(session.getParms())).append("</blockquote></p>");

        sb.append("<h3>Parms (multi values?)</h3><p><blockquote>").
                append(Util.toString(decodedQueryParameters)).append("</blockquote></p>");

        try {
            Map<String, String> files = new HashMap<String, String>();
            session.parseBody(files);
            sb.append("<h3>Files</h3><p><blockquote>").
                    append(Util.toString(files)).append("</blockquote></p>");
        } catch (Exception e) {
            e.printStackTrace();
        }

        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }


}
