package ee.ut.cs.mc.mass.restserver;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Jakob on 6.02.2015.
 */
public class RestServer extends NanoHTTPD {
    private static final String URI_SAMPLE_XML = "/xml";
    private final Context context;

    public RestServer(Context context, int port) {
        super(port);
        this.context = context;
    }

    @Override public Response serve(IHTTPSession session) {
        String Uri = session.getUri();
        String responseBody;

        if (Uri.equals(URI_SAMPLE_XML)){
            try {
                InputStream xmlIs = getXmlInputStream("sensor.xml");
                return new Response(Response.Status.OK, "application/xml", xmlIs);

            } catch (IOException e) {
                return new Response(
                        Response.Status.INTERNAL_ERROR,
                        "text/plain",
                        "Unable to load xml");
            }
        }
        else {
            // DEFAULT behaviour:
            // return a HTML page showing details of the request (URI, user agents, params, etc)
            responseBody = getRequestDetailsHtml(session);
        }
        return new Response(responseBody);
    }


    private InputStream getXmlInputStream(String path) throws IOException {
        AssetManager am = context.getAssets();
        InputStream is = am.open(path);

        return is;
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
