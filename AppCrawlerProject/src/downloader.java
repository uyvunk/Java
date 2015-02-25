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

	private static MongoClient mongoClient;

	public static void main(String[] args) throws MalformedURLException {
		char[] password = {'p','a','s','s','w','o','r','d'};
		MongoCredential credential = MongoCredential.createMongoCRCredential("siteUserAdmin", "admin", password);
		MongoClient mongoClient = new MongoClient(new ServerAddress(), Arrays.asList(credential));
		//mongoClient = new MongoClient( "localhost" , 27017 );
		DB db = mongoClient.getDB("admin");
		DBCollection coll = db.getCollection("testData");
		DBCursor cursor = coll.find();
		try {
			while (cursor.hasNext()){
				System.out.println(cursor.next());
			}
		
		} finally {
			cursor.close();
		}
		
		
		
		
		
		
		
		
		
		
		
		//Initialize Path to file
		String strUrl = "https://lh6.ggpht.com/8rkB5fph8KfgpAM27k3lIODTLUpuPlMmP9W65ldZQNMUdSob591CVl4ztrdsIDiy_WwP=h900-rw";
		URL appUrl = new URL(strUrl);
		String appName = "app1";
		String path = "Z:\\sampleScreenShots\\"+appName;
		
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
		
		mongoClient.close();
	}
	
	

}
