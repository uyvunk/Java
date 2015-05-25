import com.mongodb.DBObject;


public class AppNode {
     public String	referenceDate;        
     public String	url;                  
     public String	name;                   
     public String	developer;              
     public String	isTopDeveloper;         
     public String 	developerURL;           
     public String 	publicationDate;        
     public String	category;               
     public String	isFree;                 
     public String	price;                  
     public double	reviewers;              
     public String	coverImgUrl;           
     public String	description;            
     public Score	score;                  
     public String	lastUpdateDate;         
     public double	appSize;                
     public String	instalations;           
     public String	currentVersion;         
     public String	minimumOSVersion;       
     public String	contentRating;          
     public String	haveInAppPurchases;     
     public String	developerEmail;         
     public String	developerWebsite;       
     public String	developerPrivacyPolicy; 
     public String	physicalAddress;   
     
     // Construct an AppNode which contain all the info that is available from the DB
     public AppNode(DBObject t) {
		this.url = getElStrVal(t, "Url"); // getting app's page url
		this.name = getElStrVal(t, "Name"); // assigning Name to appName
		this.developer = getElStrVal(t, "Developer");
		this.isTopDeveloper = getElStrVal(t, "IsTopDeveloper");
		this.developerURL = getElStrVal(t, "DeveloperURL");
		this.publicationDate = getElStrVal(t, "PublicationDate");
		this.category = getElStrVal(t, "Category");
		this.isFree = getElStrVal(t, "IsFree");
		this.price = getElStrVal(t, "Price");
		this.reviewers = Double.parseDouble(t.get("Reviewers").toString());
		this.coverImgUrl = getElStrVal(t, "CoverImgUrl");
		this.description = getElStrVal(t, "Description");
		this.score = new Score(t);
		this.lastUpdateDate = getElStrVal(t, "LastUpdateDate");
		this.appSize = Double.parseDouble(t.get("AppSize").toString());
		this.instalations = getElStrVal(t, "Instalations");
		this.currentVersion = getElStrVal(t, "CurrentVersion");
		this.minimumOSVersion = getElStrVal(t, "MinimumOSVersion");
		this.contentRating = getElStrVal(t, "ContentRating");
		this.haveInAppPurchases = getElStrVal(t, "HaveInAppPurchases");
		this.developerEmail = getElStrVal(t, "DeveloperEmail");
		this.developerWebsite = getElStrVal(t, "DeveloperWebsite");
		this.developerPrivacyPolicy = getElStrVal(t, "DeveloperPrivacyPolicy");
		this.physicalAddress = getElStrVal(t, "PhysicalAddress");
     }
     
     public String toString() {
    	 String result =	"Name: " + this.name + "\n" +
    			 			"URL: " + this.url + "\n" +
    			 			"Developer: " + this.developer + "\n" +
    			 			"Developer Email: " + this.developerEmail + "\n" +
    			 			"Is Top Developer: " + this.isTopDeveloper + "\n" +
    			 			"Developer URL: " + this.developerURL + "\n" +
    			 			"ReferenceDate: " + this.referenceDate + "\n" +
    			 			"Publication Date: " + this.publicationDate + "\n" +
    			 			"Category: " + this.category + "\n" +
    			 			"Is Free?: " + this.isFree + "\n" +
    			 			"Price (USD): $" + this.price + "\n" +
    			 			"Number of Reviewers: " + this.reviewers + "\n" +
    			 			"Cover IMG url: " + this.coverImgUrl + "\n" + 
    			 			"Desciption: " + this.description + "\n" +
    			 			"Score: " + this.score.toString() + "\n" +
    			 			"Last Update: " + this.lastUpdateDate + "\n" +
    			 			"App Size (MB): " + this.appSize + "\n" +
    			 			"Installations Range: " + this.instalations + "\n" +
    			 			"Current Version: " + this.currentVersion + "\n" +
    			 			"Minimum OS Version: " + this.minimumOSVersion + "\n" +
    			 			"Conten Rating: " + this.contentRating + "\n" +
    			 			"Have In-App Purchases: " + this.haveInAppPurchases + "\n" +
    			 			"Developer Privacy Policy: " + this.developerPrivacyPolicy + "\n" +
    			 			"Physical Address: " + this.physicalAddress	+ "\n";
    	 
    	 return result;
     }
     
     private String getElStrVal(DBObject t, String at) {
    	 if (t.get(at) == null) {
    		 return "null";
    	 } else {
    		 return t.get(at).toString();
    	 }
     }
}

