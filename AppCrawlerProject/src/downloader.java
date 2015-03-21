/***
 * @author vu
 */
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ParallelScanOptions;
import com.mongodb.ServerAddress;

import org.apache.commons.io.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.concurrent.TimeUnit.SECONDS;


public class downloader {
	private static String ROOT_DIR = "D:\\sampleScreenShots\\";
	private static String COLLECTION_NAME = "ProcessedApps_2015", DB_NAME="admin",DB_USER_NAME="siteUserAdmin";
	private static char[] DB_PASSWRD = {'p','a','s','s','w','o','r','d'};
	private static MongoClient mongoClient;
	

	public static void main(String[] args) throws IOException {
		//Initialize the connection to DB
		MongoCredential credential = MongoCredential.createMongoCRCredential(DB_USER_NAME, DB_NAME, DB_PASSWRD);
		mongoClient = new MongoClient(new ServerAddress(), Arrays.asList(credential));
		DB db = mongoClient.getDB(DB_NAME);	//Open connection to DB_NAME
		DBCollection coll = db.getCollection(COLLECTION_NAME);
		//Create cursor to iterate the elements of the collection
		DBCursor cursor = coll.find().limit(32); //Set the value of limit for testing purpose. Remove limit when ready to run
		
		//Set currentRand for app that doesn't have name
		int currentRand=0;
		try {
			//BasicDBObject query = new BasicDBObject();
	
			while (cursor.hasNext()){
				DBObject tuple = cursor.next();
				String strUrl = tuple.get("Url").toString(); // getting app's page url
				String appName = tuple.get("Name").toString(); // assigning Name to appName
				if (appName.isEmpty()){
					appName="app "+currentRand;
					currentRand++;
				}
				String path = ROOT_DIR + appName; //Initialize Path to file
				// Use Apache commons IO to create directory with the name appName, and download the image into it
				File file = new File(path);
				if (!file.exists()) {
					if (file.mkdir()) {
						System.out.println("Directory "+appName+" is created!");
						//getting the url_list of screenshots from the screenshot_finder
						List<String> screenshot_url_list = screenshot_finder(strUrl);
						int a = 0;
						for (String each_screenshot : screenshot_url_list){
							URL scrSh_url = new URL(each_screenshot); 
							File photo = new File(path+"\\screenShot" + a + ".webp");
							try {
								FileUtils.copyURLToFile(scrSh_url, photo)	;
								a++;
							} catch (Exception e) {
								System.out.println(e);
							}
						}
					} else {
						System.out.println("Failed to create directory!");
					}
				}
								
			}
		
		} finally {
			cursor.close();
		}
		mongoClient.close();	//Close Connection to DB
	}
	
	/**
	 * take an input url as a String and spit out the availale screenshot url stored in a List
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private static List<String> screenshot_finder(String url) throws IOException{
		URL oracle = new URL(url);
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
        return list_url;
	}

}
