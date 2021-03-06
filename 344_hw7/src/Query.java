//VU NGUYEN
//CSE 344 HW 7

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.FileInputStream;

/**
 * Runs queries against a back-end database
 */
public class Query {
	private String configFilename;
	private Properties configProps = new Properties();

	private String jSQLDriver;
	private String jSQLUrl;
	private String jSQLUser;
	private String jSQLPassword;
	private String jSQLCustomerDB;
	// DB Connection
	private Connection conn;
        private Connection customerConn;

	// Canned queries

	// LIKE does a case-insensitive match
	private static final String SEARCH_SQL_BEGIN =
		"SELECT * FROM movie WHERE name LIKE '%";
	private static final String SEARCH_SQL_END = 
		"%' ORDER BY id";

	private static final String DIRECTOR_MID_SQL = "SELECT y.* "
					 + "FROM movie_directors x, directors y "
					 + "WHERE x.mid = ? and x.did = y.id";
	private PreparedStatement directorMidStatement;
	
//	private static final String SEARCH_MOVIE_ID = "SELECT * FROM movie WHERE name LIKE ? ORDER BY id";
//	private PreparedStatement searchMovieStatement;
	/* uncomment, and edit, after your create your own customer database */
	
	private static final String CUSTOMER_LOGIN_SQL = 
		"SELECT * FROM customer WHERE login = ? and password = ?";
	private PreparedStatement customerLoginStatement;

	private static final String BEGIN_TRANSACTION_SQL = 
		"SET TRANSACTION ISOLATION LEVEL SERIALIZABLE; BEGIN TRANSACTION;";
	private PreparedStatement beginTransactionStatement;

	private static final String COMMIT_SQL = "COMMIT TRANSACTION";
	private PreparedStatement commitTransactionStatement;

	private static final String ROLLBACK_SQL = "ROLLBACK TRANSACTION";
	private PreparedStatement rollbackTransactionStatement;
	
	private static final String MID_RENTAL_STATUS_SQL ="SELECT * FROM Rental WHERE movie_id = ? and status=\'open\'";
	private PreparedStatement rentalStatusStatement;
	

	private static final String ACTOR_MID_SQL = "SELECT DISTINCT a.* "
			 + "FROM casts c, actor a "
			 + "WHERE c.mid = ? and c.pid = a.id ORDER BY a.lname";
	private PreparedStatement actorMidStatement;
	
	private static final String USR_ID_NAME_SQL = "SELECT first_name, last_name FROM Customer WHERE id = ?";
	private PreparedStatement userNameStatement;
	
	private static final String REMAINING_RENTAL_SQL = "SELECT (SELECT p.maximum_number FROM Customer c, Plans p WHERE c.id = ?"
			+ " AND c.plan_id = p.plan_id)-count(*) FROM Rental r WHERE r.customer_id = ? AND r.status='open'";
	private PreparedStatement remainingRentalStatement;
	
	private static final String VALID_PLAN_SQL = "SELECT count(*) from Plans WHERE plan_id = ?";
	private PreparedStatement validPlanStatement;
	
	private static final String VALID_MOVIE_SQL = "SELECT count(*) FROM movie WHERE id = ?";
	private PreparedStatement validMovieStatement;
	
	public Query(String configFilename) {
		this.configFilename = configFilename;
	}

    /**********************************************************/
    /* Connection code to SQL Azure. Example code below will connect to the imdb database on Azure
       IMPORTANT NOTE:  You will need to create (and connect to) your new customer database before 
       uncommenting and running the query statements in this file .
     */

	public void openConnection() throws Exception {
		configProps.load(new FileInputStream(configFilename));

		jSQLDriver   = configProps.getProperty("videostore.jdbc_driver");
		jSQLUrl	   = configProps.getProperty("videostore.imdb_url");
		jSQLUser	   = configProps.getProperty("videostore.sqlazure_username");
		jSQLPassword = configProps.getProperty("videostore.sqlazure_password");
		jSQLCustomerDB = configProps.getProperty("videostore.customer_url");

		/* load jdbc drivers */
		Class.forName(jSQLDriver).newInstance();

		/* open connections to the imdb database */

		conn = DriverManager.getConnection(jSQLUrl, // database
						   jSQLUser, // user
						   jSQLPassword); // password
                
		conn.setAutoCommit(true); //by default automatically commit after each statement 
		conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

		/* Also you will put code here to specify the connection to your
		   customer DB.  E.g. */

		   customerConn = DriverManager.getConnection(jSQLCustomerDB, jSQLUser, jSQLPassword);
		   customerConn.setAutoCommit(true); //by default automatically commit after each statement
		   customerConn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		
	        
	}

	public void closeConnection() throws Exception {
		conn.close();
		customerConn.close();
	}

    /**********************************************************/
    /* prepare all the SQL statements in this method.
      "preparing" a statement is almost like compiling it.  Note
       that the parameters (with ?) are still not filled in */

	public void prepareStatements() throws Exception {

		directorMidStatement = conn.prepareStatement(DIRECTOR_MID_SQL);
		
		/* uncomment after you create your customers database */
		
		customerLoginStatement = customerConn.prepareStatement(CUSTOMER_LOGIN_SQL);
		beginTransactionStatement = customerConn.prepareStatement(BEGIN_TRANSACTION_SQL);
		commitTransactionStatement = customerConn.prepareStatement(COMMIT_SQL);
		rollbackTransactionStatement = customerConn.prepareStatement(ROLLBACK_SQL);
		

		/* add here more prepare statements for all the other queries you need */
		actorMidStatement = conn.prepareStatement(ACTOR_MID_SQL);
		rentalStatusStatement = customerConn.prepareStatement(MID_RENTAL_STATUS_SQL);
		userNameStatement = customerConn.prepareStatement(USR_ID_NAME_SQL);
		remainingRentalStatement = customerConn.prepareStatement(REMAINING_RENTAL_SQL);
		validPlanStatement = customerConn.prepareStatement(VALID_PLAN_SQL);
		validMovieStatement = conn.prepareStatement(VALID_MOVIE_SQL);
//		searchMovieStatement = conn.prepareStatement(SEARCH_MOVIE_ID);
	}


    /**********************************************************/
    /* Suggested helper functions; you can complete these, or write your own
       (but remember to delete the ones you are not using!) */

	public int getRemainingRentals(int cid) throws Exception {
		/* How many movies can she/he still rent?
		   You have to compute and return the difference between the customer's plan
		   and the count of outstanding rentals */
		remainingRentalStatement.clearParameters();
		remainingRentalStatement.setInt(1, cid);
		remainingRentalStatement.setInt(2, cid);
		ResultSet remainingRental_set = remainingRentalStatement.executeQuery();
		if (remainingRental_set.next()) {
			int remainRental = remainingRental_set.getInt(1);
			remainingRental_set.close();
			return remainRental;	//return the remaining retal of the current customer
		} else {
			remainingRental_set.close();
			return (-1);								// return -1 if the ResultSet is empty
		}
		
	}

	public String getCustomerName(int cid) throws Exception {
		/* Find the first and last name of the current customer. */
		userNameStatement.clearParameters();
		userNameStatement.setInt(1, cid);
		ResultSet userName_set = userNameStatement.executeQuery();
		if (userName_set.next()) {
			String fname = userName_set.getString(1);
			String lname = userName_set.getString(2);
			userName_set.close();
			return fname +" "+ lname;	// returning fname lname of the current user
		} else {
			userName_set.close();
			return null;
		}
																// return null if the ResultSet is empty

	}

	public boolean isValidPlan(int planid) throws Exception {
		/* Is planid a valid plan ID?  You have to figure it out */
		validPlanStatement.clearParameters();
		validPlanStatement.setInt(1, planid);
		ResultSet validPlan_set = validPlanStatement.executeQuery();
		if (validPlan_set.next() && validPlan_set.getInt(1)==1) {
			validPlan_set.close();
			return true;
		} else {
			validPlan_set.close();
			return false;
		}
	}

	public boolean isValidMovie(int mid) throws Exception {
		/* is mid a valid movie ID?  You have to figure it out */
		validMovieStatement.clearParameters();
		validMovieStatement.setInt(1, mid);
		ResultSet validMovie_set = validMovieStatement.executeQuery();
		if (validMovie_set.next() && validMovie_set.getInt(1)==1) {
			validMovie_set.close();
			return true;
		} else {
			validMovie_set.close();
			return false;
		}
	}
//
//	private int getRenterID(int mid) throws Exception {
//		/* Find the customer id (cid) of whoever currently rents the movie mid; return -1 if none */
//		return (77);
//	}

    /**********************************************************/
    /* login transaction: invoked only once, when the app is started  */
	public int transaction_login(String name, String password) throws Exception {
		/* authenticates the user, and returns the user id, or -1 if authentication fails */
		/* Uncomment after you create your own customers database */
		
		int cid;

		customerLoginStatement.clearParameters();
		customerLoginStatement.setString(1,name);
		customerLoginStatement.setString(2,password);
		ResultSet cid_set = customerLoginStatement.executeQuery();
		if (cid_set.next()) cid = cid_set.getInt(1);
		else cid = -1;
		cid_set.close();
		return(cid);
		 
	}

	public void transaction_printPersonalData(int cid) throws Exception {
		/* println the customer's personal data: name, and plan number */
		String fullname = getCustomerName(cid);
		int remainRental = getRemainingRentals(cid);
		System.out.println("Hi "+ fullname +".\tYou have "+ remainRental +" remaining rentals. Enjoy Renting!");
	}


    /**********************************************************/
    /* main functions in this project: */

	public void transaction_search(int cid, String movie_title)
			throws Exception {
		/* searches for movies with matching titles: SELECT * FROM movie WHERE name LIKE movie_title */
		/* prints the movies, directors, actors, and the availability status:
		   AVAILABLE, or UNAVAILABLE, or YOU CURRENTLY RENT IT */

		/* Interpolate the movie title into the SQL string */
		movie_title = movie_title.replace("'", "''"); //sanitize the input
		String searchSql = SEARCH_SQL_BEGIN + movie_title + SEARCH_SQL_END;
		Statement searchStatement = conn.createStatement();	
		ResultSet movie_set = searchStatement.executeQuery(searchSql);
		while (movie_set.next()) {
			int mid = movie_set.getInt(1);
			System.out.println("ID: " + mid + " NAME: "
					+ movie_set.getString(2) + " YEAR: "
					+ movie_set.getString(3));
			/* do a dependent join with directors */
			directorMidStatement.clearParameters();
			directorMidStatement.setInt(1, mid);
			ResultSet director_set = directorMidStatement.executeQuery();
			while (director_set.next()) {
				System.out.println("\t\tDirector: " + director_set.getString(3)
						+ " " + director_set.getString(2));
			}
			director_set.close();
			
			//Find the actor for the current movie
			actorMidStatement.clearParameters();
			actorMidStatement.setInt(1, mid);
			ResultSet actor_set = actorMidStatement.executeQuery();
			while (actor_set.next()) {
				System.out.println("\t\tActor: " + actor_set.getString(3)
						+ " " + actor_set.getString(2));
			}
			actor_set.close();
			
			//Find the Rental Status for the current movie
			rentalStatusStatement.clearParameters();
			rentalStatusStatement.setInt(1, mid); 	//plug mid to the query the status of the movie
			ResultSet rentalStatus_set = rentalStatusStatement.executeQuery();
			if (rentalStatus_set.next()) {
				int whorentingID = rentalStatus_set.getInt(1);
				if (whorentingID == cid)
					System.out.println("\t\tRental Status: YOU CURRENTLY RENT IT");
				else
					System.out.println("\t\tRental Status: UNAVAILABLE");
			} else {
					System.out.println("\t\tRental Status: AVAILABLE");
			}
				
			
		}
		movie_set.close();
		System.out.println();
	}

	public void transaction_choosePlan(int cid, int pid) throws Exception {
	    /* updates the customer's plan to pid: UPDATE customer SET plid = pid */
	    /* remember to enforce consistency ! */
		String choosePlan_HEAD = "UPDATE Customer SET plan_id = ";
		String choosePlan_BODY = " WHERE id = ";
		String updateSql = choosePlan_HEAD + pid + choosePlan_BODY + cid ;
		customerConn.setAutoCommit(false);
		Statement choosePlanStatement = customerConn.createStatement();
		int rowAffected = choosePlanStatement.executeUpdate(updateSql);	//update plan_id from customer table
		customerConn.setAutoCommit(true);
		if (rowAffected == 1) {
			customerConn.commit();
			System.out.println("Plan has been updated");
		} else {
			customerConn.rollback();
			System.out.println("Plan updated UNSUCESSFULLY. Please try again later");
		}
		
	}

	public void transaction_listPlans() throws Exception {
	    /* println all available plans: SELECT * FROM plan */
		String listPlansql = "SELECT * from Plans";
		Statement listPlanStatement = customerConn.createStatement();
		ResultSet listPlan_set = listPlanStatement.executeQuery(listPlansql);
		System.out.println("\tID\tPlan\t\t\tNumber of Rental\tMonthly Fee\n");
		while (listPlan_set.next()){
			System.out.println("\t" + listPlan_set.getInt(1) + "\t"+ listPlan_set.getString(2) + "\t\t\t" + listPlan_set.getInt(3) + "\t\t\t" + listPlan_set.getInt(4));
		}
		listPlan_set.close();
	}

	public void transaction_rent(int cid, int mid) throws Exception {
	    /* rent the movie mid to the customer cid */
	    /* remember to enforce consistency ! */
		
		/********************* Testing Concurrency Codes**************/
//		Scanner input = new Scanner(System.in);
//		System.out.println("Please insert mid:");
//		int mid = input.nextInt();
		
		/**********************************************/
		
		//Check if the give mid is valid
		boolean valid = isValidMovie(mid);
		if (valid) {
			customerConn.setAutoCommit(false);
			rentalStatusStatement.clearParameters();
			rentalStatusStatement.setInt(1, mid); 	//plug mid to the query the status of the movie
			ResultSet rentalStatus_set = rentalStatusStatement.executeQuery();
			if (rentalStatus_set.next()) {
				int whorentingID = rentalStatus_set.getInt(1);
				if (whorentingID == cid)
					System.out.println("\t\tRental Status: YOU'RE HOLDING IT");
				else
					System.out.println("\t\tRental Status: UNAVAILABLE");
				customerConn.setAutoCommit(true);
			} else {
					int remainRental = getRemainingRentals(cid);
					if (remainRental > 0)	{
							String date = getDate();
							String rentingSql = "INSERT INTO Rental VALUES (" + cid + "," + mid + ",'open'," + date + ")";
							Statement rentingStatement = customerConn.createStatement();
							int rowAffected = rentingStatement.executeUpdate(rentingSql);	//update plan_id from customer table 
							if (rowAffected == 1) {
								customerConn.commit();
								System.out.println("\t\tRental Status: YOU GOT IT");
							} else {
								customerConn.rollback();
								System.out.println("\t\tRental Status: UNSUCCESSFUL. Please try another time.");
							}
					}
					customerConn.setAutoCommit(true);	
			} 
		rentalStatus_set.close();
		} else {
			System.out.println("Invalid Movie ID. Please try again");
		}
		
	}

	public void transaction_return(int cid, int mid) throws Exception {
	    /* return the movie mid by the customer cid */
		boolean valid = isValidMovie(mid);
		if (valid) {
			customerConn.setAutoCommit(false);
			rentalStatusStatement.clearParameters();
			rentalStatusStatement.setInt(1, mid); 	//plug mid to the query the status of the movie
			ResultSet rentalStatus_set = rentalStatusStatement.executeQuery();
			if (rentalStatus_set.next() && rentalStatus_set.getInt(1) == cid) {
					String returningSql = "UPDATE Rental SET status = 'close' WHERE customer_id = " + cid + " AND movie_id = " + mid 
										+ " AND status = 'open'";
					Statement returningStatement = customerConn.createStatement();
					int rowAffected = returningStatement.executeUpdate(returningSql);
					if (rowAffected == 1) {
						customerConn.commit();
						System.out.println("\t\tRental Status: YOU'VE RETURNED IT");
					} else {
						customerConn.rollback();
						System.out.println("\t\tRental Status: UNSUCCESSFUL. Please try another time.");
					}
					customerConn.setAutoCommit(true);
			} else {
					System.out.println("\t\tReturn Status: YOU'RE NOT RENTING THIS MOVIE");
			}						
		rentalStatus_set.close();
		} else {
			System.out.println("Invalid Movie ID. Please try again");
		}
	}

	public void transaction_fastSearch(int cid, String movie_title)
			throws Exception {
		/* like transaction_search, but uses joins instead of dependent joins
		   Needs to run three SQL queries: (a) movies, (b) movies join directors, (c) movies join actors
		   Answers are sorted by mid.
		   Then merge-joins the three answer sets */
		movie_title = movie_title.replace("'", "''");	//sanitize the input
		//Run a query to join MOVIE, ACTOR, DIRECTORS, CASTS, and MOVIE_DIRECTORS
		String movieSql = "SELECT DISTINCT id, name, year FROM movie WHERE name LIKE '%"+ movie_title +"%' ORDER BY id";
		String mov_dirSql = "SELECT DISTINCT m.id, d.lname, d.fname from movie m, directors d, movie_directors md WHERE"
										+" m.name LIKE '%"+ movie_title +"%' AND m.id = md.mid and md.did = d.id ORDER BY m.id";
		String mov_actSql = "SELECT DISTINCT m.id, a.lname, a.fname from movie m, actor a, casts c WHERE m.name LIKE '%"+ movie_title 
								+"%' AND m.id = c.mid and c.pid = a.id ORDER BY m.id, a.lname";
		
		Statement movieStatement = conn.createStatement();		
		Statement mov_dirStatement = conn.createStatement();
		Statement mov_actStatement = conn.createStatement();
		ResultSet mov_set = movieStatement.executeQuery(movieSql);//m.id, m.name, m.year
		ResultSet mov_dir_set = mov_dirStatement.executeQuery(mov_dirSql); //m.id, d.lname, d.fname
		ResultSet mov_act_set = mov_actStatement.executeQuery(mov_actSql); //m.id, a.lname, a.fname
		
		int cdmid = 0;
		int camid = 0;
		while (mov_set.next()) {
			int cmid = mov_set.getInt(1);	
			System.out.println("ID: " + cmid + " NAME: "
					+ mov_set.getString(2) + " YEAR: "
					+ mov_set.getString(3));
			
			if(cdmid <= cmid) {
				if(cdmid == cmid)
					System.out.println("\t\tDirector: "+ mov_dir_set.getString(2)+" "+ mov_dir_set.getString(3)); //get d.fname and d.lname
				while (mov_dir_set.next()) {
					cdmid = mov_dir_set.getInt(1);
					if (cdmid == cmid)
						System.out.println("\t\tDirector: "+ mov_dir_set.getString(2)+" "+ mov_dir_set.getString(3)); //get d.fname and d.lname
					if (cdmid > cmid)
						break;
				} 
			}
			
			if(camid <= cmid) {
				if(camid == cmid)
					System.out.println("\t\tActor: "+ mov_act_set.getString(2)+" "+ mov_act_set.getString(3)); //get d.fname and d.lname
				while (mov_act_set.next()) {
					camid = mov_act_set.getInt(1);
					if (camid == cmid)
						System.out.println("\t\tActor: "+ mov_act_set.getString(2)+" "+ mov_act_set.getString(3)); //get d.fname and d.lname
					if (camid > cmid)
						break;
				} 
			}
	
		}
		mov_set.close();
		mov_dir_set.close();
		mov_act_set.close();
	}
		
	/***
	 * 
	 * @return current Date under format MMddyyyyHHmm
	 */
	public String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("MMddyyyyHHmm");
		Date date = new Date();
		return dateFormat.format(date);
	}
		

    /* Uncomment helpers below once you've got beginTransactionStatement,
       commitTransactionStatement, and rollbackTransactionStatement setup from
       prepareStatements():
    
       public void beginTransaction() throws Exception {
	    customerConn.setAutoCommit(false);
	    beginTransactionStatement.executeUpdate();	
        }

        public void commitTransaction() throws Exception {
	    commitTransactionStatement.executeUpdate();	
	    customerConn.setAutoCommit(true);
	}
        public void rollbackTransaction() throws Exception {
	    rollbackTransactionStatement.executeUpdate();
	    customerConn.setAutoCommit(true);
	    } 
    */

}
