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

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.concurrent.TimeUnit.SECONDS;


public class downloader {
	private static String ROOT_DIR = "D:\\sampleScreenShots\\";
	private static String COLLECTION_NAME = "ProcessedApps_2015", DB_NAME="admin",DB_USER_NAME="siteUserAdmin";
	private static char[] DB_PASSWRD = {'p','a','s','s','w','o','r','d'};
	private static MongoClient mongoClient;
	

	public static void main(String[] args) throws MalformedURLException {
		//Initialize the connection to DB
		MongoCredential credential = MongoCredential.createMongoCRCredential(DB_USER_NAME, DB_NAME, DB_PASSWRD);
		mongoClient = new MongoClient(new ServerAddress(), Arrays.asList(credential));
		DB db = mongoClient.getDB(DB_NAME);	//Open connection to DB_NAME
		DBCollection coll = db.getCollection(COLLECTION_NAME);
		//Create cursor to iterate the elements of the collection
		DBCursor cursor = coll.find().limit(10); //Set the value of limit for testing purpose. Remove limit when ready to run
		
		//Set currentRand for app that doesn't have name
		int currentRand=0;
		try {
			//BasicDBObject query = new BasicDBObject();
	
			while (cursor.hasNext()){
				DBObject tuple = cursor.next();
				String strUrl = tuple.get("CoverImgUrl").toString(); // assigning CoverImgUrl to strUrl
				String appName = tuple.get("Name").toString(); // assigning Name to appName
				if (appName.isEmpty()){
					appName="app "+currentRand;
					currentRand++;
				}
				String path = ROOT_DIR + appName; //Initialize Path to file
				URL appUrl = new URL(strUrl); 
				
				// Use Apache commons IO to create directory with the name appName, and download the image into it
				File file = new File(path);
				if (!file.exists()) {
					if (file.mkdir()) {
						System.out.println("Directory "+appName+" is created!");
					} else {
						System.out.println("Failed to create directory!");
					}
				}
				File photo = new File(path+"\\photo.webp");
				try {
					FileUtils.copyURLToFile(appUrl, photo)	;
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		
		} finally {
			cursor.close();
		}
		mongoClient.close();	//Close Connection to DB_NAME
	}
	
	

}
