import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.*;
public class downloader {

	public static void main(String[] args) throws MalformedURLException {
		// TODO Auto-generated method stub
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
		

	}

}