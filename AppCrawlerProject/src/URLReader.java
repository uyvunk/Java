import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class URLReader {

	public static void main(String[] args) throws IOException {
		URL oracle = new URL("https://play.google.com/store/apps/details?id=com.netflix.mediaclient&hl=en");
        BufferedReader in = new BufferedReader(
        new InputStreamReader(oracle.openStream()));
        //Create array for the screenshots urls
    	List<String> list_url = new ArrayList<String>();

        String inputLine;
        String htmlContent="";
        while ((inputLine = in.readLine()) != null)
            htmlContent+=inputLine;
        in.close();
       // System.out.println(htmlContent);
        
        Document doc = Jsoup.parse(htmlContent);
        Elements elements = doc.getElementsByTag("IMG");
        for (int i=0 ; i< elements.size(); i++){
        	if (elements.get(i).hasClass("full-screenshot")) {
        		String s = elements.get(i).attr("src");
        		list_url.add(s);
        		System.out.println(s);
        	}
        }
	}

}
