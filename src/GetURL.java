import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * <pre class="doc_header">
 * <p>
 * </pre>
 *
 * @author kelmore5
 * @custom.date 3/18/17
 */
public class GetURL {
    private URL url;
    private String urlString;
    private String html;
    GetURL(String _urlString)
    {
        urlString = _urlString;
        url = checkURL();
        if(!(url == null))
            html = getHTML(urlString);
    }

    private String getHTML(String urlToRead)
    {
        String html = ""; // A long string containing all the HTML
        try
        {
            URL url = new URL(urlToRead);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null)
            {
                html += line;
            }
            rd.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return html;
    }

    public String getTitle()
    {
        String title;
        if(html.contains("<title>") && html.contains("</title>"))
            title = html.substring((html.indexOf("<title>")+7), (html.indexOf("</title>")));
        else
            title = url.toString();
        return title;
    }

    private URL checkURL()
    {
        if(!urlString.startsWith("http://"))
            urlString = "http://" + urlString;
        try
        {
            if(!urlString.substring(7,11).equals("www."))
            {
                urlString = urlString.substring(7);
                urlString = "http://www." + urlString;
            }
            if(!urlString.substring(12).contains("."))
                urlString = urlString.substring(11);
            url = new URL(urlString);
        }
        catch(MalformedURLException | StringIndexOutOfBoundsException ex) {
            url = null;
        }
        return url;
    }

    URL getURL()
    {
        return url;
    }
    public String getHTML()
    {
        return html;
    }
}