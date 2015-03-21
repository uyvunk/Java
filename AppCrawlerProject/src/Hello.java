/*
 * My First Java program to say Hello
 */

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hello {   // Save as "Hello.java" under "~/myProject"
    public static void main(String[] args) {
	String fileName = "ReadThis.html";
	String line = null;
	// create both the string pattern and a split pattern
	String pattern = ".*src=\"(.*)\" itemprop=\"screenshot\">";
	String splitP = "img class=\"screenshot\"";

	//create pattern objects
	Pattern regexp = Pattern.compile(pattern);
	Matcher matcher = regexp.matcher("");
	Pattern splitPat = Pattern.compile(splitP);

	try {
	    //	    System.out.println("Hello, world. opening file\n");

	    FileReader fileReader = new FileReader(fileName);
	    BufferedReader bufferedReader = new BufferedReader(fileReader);
	    int c=0;
	    while((line=bufferedReader.readLine()) !=null){
		//create matcher object splitting line along items
		String[] elements = splitPat.split(line);
		
		// get pattern from splits
		if(elements.length > 1){
		    for (String element : elements){
			//System.out.println( element);
			matcher.reset(element);		    
			int end = 0;
			while (matcher.find(end)) {
			    //			System.out.println("Found image between "+matcher.start(0)+" & "+matcher.end(0)+": "+ matcher.group(1) + "\n");
			    System.out.println(matcher.group(1) + "\n");
			    if(end != matcher.end(0)){
				end = matcher.end(0);
			    }else{
				break;
			    }
			}
		    }
		}
		//		System.out.println("line:"+c+ " had "+elements.length+" elements.");c=c+1;
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
            // Or we could just do this: 
            // ex.printStackTrace();
	}
    }
}