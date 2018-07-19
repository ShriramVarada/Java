import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;

public class Driver {


    public static void main(String[] args)
    throws Exception{
        try {
            URL google = new URL("https://play.google.com/store");
            URL googlabooks = new URL(google, "books");
            URL googlemusic = new URL("https", "play.google.com", "/music");
            // if a space int eh URL, then (for eg. " " is replaced with %20

            URI uri = new URI("https", "play.google.com", "/books/uploads", "");

            URL url = uri.toURL();

            System.out.println("protocol = " + url.getProtocol());
            System.out.println("authority = " + url.getAuthority());
            System.out.println("host = " + url.getHost());
            System.out.println("port = " + url.getPort());
            System.out.println("path = " + url.getPath());
            System.out.println("query = " + url.getQuery());
            System.out.println("filename = " + url.getFile());
            System.out.println("ref = " + url.getRef());

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(google.openStream()));
            String inputLines;

//            while((inputLines = bufferedReader.readLine()) != null){
//                System.out.println(inputLines);
//            }
            bufferedReader.close();

            URLConnection urlConnection = google.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(urlConnection.getOutputStream());
            bufferedOutputStream.write(2);

            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            while((inputLines = bufferedReader.readLine()) != null){
                System.out.println(inputLines);
            }
            bufferedOutputStream.close();
        } catch (MalformedURLException | URISyntaxException e) {
            e.getMessage();
        }
    }
}
