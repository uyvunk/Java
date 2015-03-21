/*
 * pattern matching html in search of screenshot.
 */

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hello {   // Save as "Hello.java" under "~/myProject"
    public static void main(String[] args) {
	String fileName = "ReadThis.html";
	String line = null;
	String pattern = ".*src=\"(.*?)\" itemprop=\"screenshot\">";

	//create pattern object
	Pattern regexp = Pattern.compile(pattern);
	Matcher matcher = regexp.matcher("");

	try {
	    System.out.println("Hello, world. opening file\n");

	    FileReader fileReader = new FileReader(fileName);
	    BufferedReader bufferedReader = new BufferedReader(fileReader);
	    int c=0;
	    while((line=bufferedReader.readLine()) !=null){
		//create matcher object
		matcher.reset(line);
		// get pattern from line
		int end = 0;
		while (matcher.find(end)) {
		    System.out.println("Found image between "+matcher.start(0)+" & "+matcher.end(0)+": "+ matcher.group(1) + "\n");
		    end = matcher.end(0)+1;
		}
		System.out.println("line:"+c);c=c+1;
	    }
	    // close file
	    bufferedReader.close();
	}
	catch(FileNotFoundException ex){
	    System.out.println(
			       "unable to open file '" + fileName + "'");
	}
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");                   
	}
    }
}