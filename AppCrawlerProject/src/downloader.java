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
import java.util.Scanner;
import java.util.Set;

import static java.util.concurrent.TimeUnit.SECONDS;


public class downloader {
	private static String ROOT_DIR = ":\\sampleScreenShots\\";
	private static String COLLECTION_NAME = "ProcessedApps_2016", DB_NAME="admin",DB_USER_NAME="siteUserAdmin";
	private static char[] DB_PASSWRD = {'p','a','s','s','w','o','r','d'};
	private static MongoClient mongoClient;
	

	public static void main(String[] args) throws IOException {
		
		// Accept path from user for the directory
		Scanner userInput = new Scanner(System.in);
		System.out.println("Please enter the desire path:\n");
		System.out.println("Which Drive?\n");
		String drive = userInput.nextLine();
		System.out.println("Directory name?\n");
		String dirName = userInput.nextLine();
		String dirpath = drive + ":\\" + dirName + "\\";
		// Create the required directory
		File dir = new File(dirpath);
		if (!dir.exists()) {
			dir.mkdir();
		} else {
			System.out.println("The directory already exists. Would you like to add files into it? (Y/N)\n");
			String answer = userInput.nextLine();
			if (answer.charAt(0) == 'n' || answer.charAt(0) == 'N') {
				System.out.println("Exit now!");
				System.exit(0);
			}
		}
		 int appCounter = 0;	// count total apps has been processed in this session
		 int scrCounter = 0;	// count total screenshots has been processed in this session
		 
		 // Prompt user how many apps to download
		 System.out.println("How many apps need to download screenshot?\n");
		 int num_app = userInput.nextInt();
		
		//Initialize the connection to DB
		MongoCredential credential = MongoCredential.createMongoCRCredential(DB_USER_NAME, DB_NAME, DB_PASSWRD);
		mongoClient = new MongoClient(new ServerAddress(), Arrays.asList(credential));
		DB db = mongoClient.getDB(DB_NAME);	//Open connection to DB_NAME
		DBCollection coll = db.getCollection(COLLECTION_NAME);
		//Create cursor to iterate the elements of the collection
		DBCursor cursor = coll.find().limit(num_app); //Set the value of required apps	
			
		//Set currentRand for app that doesn't have name (it's name will be a number)
		int currentRand=0;
		try {
			//BasicDBObject query = new BasicDBObject();
	
			while (cursor.hasNext()){
				DBObject tuple = cursor.next();
				// Get the information about the apps (aka all the attributes of this app in
				// the database)
				AppNode currentApp = new AppNode(tuple);
				String strUrl = currentApp.url;		// get app URL
				String appName = cleanString(currentApp.name);	// get app Name
				String appDescription = currentApp.toString();
				
				if (appName.isEmpty()){
					appName="app "+currentRand;
					currentRand++;
				}
				String path = dirpath + appName; //Initialize Path to file
				// Use Apache commons IO to create directory with the name appName, and download the image into it
				File file = new File(path);
				if (!file.exists()) {
					if (file.mkdir()) {
						System.out.println("Directory "+appName+" is created!");
						appCounter++;
						//getting the url_list of screenshots from the screenshot_finder
						List<String> screenshot_url_list = screenshot_finder(strUrl);
						// if the url is valid, go ahead to download the img
						if (screenshot_url_list != null) {
							int a = 0;
							for (String each_screenshot : screenshot_url_list){
								try {
									URL scrSh_url = new URL(formatURL(each_screenshot)); 
									File photo = new File(path+"\\screenShot" + a + ".webp");
									FileUtils.copyURLToFile(scrSh_url, photo)	;
									a++;
								} catch (Exception e) {
									System.out.println(e);
								}
							}
							try {
								appDescription += "Total ScreenShot: " + a + "\n";
							} catch (Exception e) {
								System.out.println(e);
							}
							
							scrCounter += a;
							
							// Write the file to ABOUTME.txt
							File aboutme = new File(path + "\\ABOUTME.txt");
							FileUtils.writeStringToFile(aboutme, appDescription);
						} else {	// the url is not valid, roll back, remove the directory just being created
							System.out.println("+_+ Cannot download " + appName + "x_x");
							file.delete();
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
		
		// Print total number of apps has been crawled
		System.out.println("\n\nDownloaded " + scrCounter + " screenshots from " + appCounter + " apps!");
	}
	
	/**
	 * take an input url as a String and spit out the availale screenshot url stored in a List
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private static List<String> screenshot_finder(String url) {
		try {
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
		} catch (Exception e){
			System.out.println("The URL: " + url + " is not available.");
			System.out.println(e.getMessage());
			return null;
		}
		
	}
	/*
	 * Clean special characters from the input to prevent
	 * the error in file system when try to read the string that
	 * has special char like . or ..
	 * @param input
	 * @return
	 */
	private static String cleanString(String input) {
		String result = input;
		char[] special = {'.', '/', '\\'};
		for (int i = 0; i < special.length; i++) {
			result = result.replace(special[i], '_');
		}
		return result;
	}
	
	private static String formatURL(String input) {
		if(input.startsWith("//"))
			return "http:" + input;
		return input;
	}

}
