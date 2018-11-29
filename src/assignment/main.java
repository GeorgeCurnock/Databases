package assignment;
import java.sql.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {
    // Variables for initial database connection
    public static final String DB_NAME = "jdbc:postgresql://mod-intro-databases.cs.bham.ac.uk/";
    public static final String UNAME = "gxc712";
    public static final String PWORD = "qcsprzhm2i";
    public static Scanner scan;

    public static void main(String[] args){

        // Attempts to load required driver
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("PostgreSQL driver registered.");
        } catch (ClassNotFoundException ex) {
            System.out.println("Driver not found");
            System.exit(1);
        }

        // Attempt to make a connection to the DB
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_NAME, UNAME, PWORD);

            if (conn != null) {
                System.out.println("Database accessed!");
            } else {
                System.out.println("Failed to make connection");
                System.exit(1);
            }

            // Initial call to create the tables
            CreateTables.executeStartup(conn);
            System.out.println("Tables created");

            // Initial call to populate the tables
            PopulateTables.executePopulation(conn);
            System.out.println("Tables populated");

            // Setup for the UI loop
            // Scanner to read the user input
            scan = new Scanner(System.in);
            System.out.println("Welcome to the database UI");
            System.out.println("Type 'help' for available commands");
            // string to hold the user input
            String user = "";

            /*
            /   5 possible cases for the UI loop
            /   - party report          Generates a party report for the specified ID in the PartyReports directory
            /   - menu report           Generates a menu report for the specified ID in the MenuReports directory
            /   - insert party          Adds a party entry to the database if all parameters are valid
            /                           Errors not type checked by java are handled by constraints in the DB
            /   - exit                  Exits the program
            /   - help                  Provides a list of all available commands
            /
            */

            while(true){
                System.out.print("Ready for user request: ");
                user = scan.nextLine();

                if(user.toLowerCase().equals("party report")){
                    System.out.print("Please enter the id of the party: ");
                    int id = scan.nextInt();
                    // nextLine() moves to the next line for the next input
                    scan.nextLine();
                    if(ReportGenerator.PartyReportGenerator(conn, id)){
                        // if party report generation successful
                        System.out.println("Party_report_" + id + " has been generated");
                    }
                    else {
                        // if party report generation unsuccessful
                        System.out.println("Report generation failed");
                    }
                }
                else if(user.toLowerCase().equals("menu report")){
                    System.out.print("Please enter the id of the menu: ");
                    int id = scan.nextInt();
                    // nextLine() moves to the next line for the next input
                    scan.nextLine();
                    if(ReportGenerator.MenuReportGenerator(conn, id)){
                        // if menu report generation successful
                        System.out.println("Menu_report_" + id + " has been generated");
                    }
                    else{
                        // if menu report generation unsuccessful
                        System.out.println("Report generation failed");
                    }
                }
                else if(user.toLowerCase().equals("help")){
                    // List all available commands
                    System.out.println("Available commands:");
                    System.out.println(": party report           Generate a report for a party in the database");
                    System.out.println(": menu report            Generate a report for a menu in the database");
                    System.out.println(": insert party           add a new party to the database");
                    System.out.println(": quit                   exit the program");
                }
                else if(user.toLowerCase().equals("quit")){
                    // Break the loop to end the program
                    break;
                }
                else if(user.toLowerCase().equals("insert party")){
                    int mid = 0;
                    int vid = 0;
                    int eid = 0;
                    int price = 0;
                    int numberofguests = 0;

                    // Read in all required values
                    System.out.print("Enter name of party: ");
                    String name = scan.nextLine();
                    System.out.print("Enter id of menu: ");
                    // try statement for if user enters anything other than an int
                    try {
                        mid = scan.nextInt();
                        scan.nextLine();
                        System.out.print("Enter id of venue: ");

                        vid = scan.nextInt();
                        scan.nextLine();
                        System.out.print("Enter id of entertainment: ");

                        eid = scan.nextInt();
                        scan.nextLine();
                        System.out.print("Enter the quoted price: ");
                    }
                    catch (IllegalArgumentException ex){
                        System.out.println("The entered ID is invalid");
                        System.out.println("Terminating party insert");
                        continue;
                    }
                    try {
                        price = scan.nextInt();
                    }
                    catch (IllegalArgumentException ex){
                        System.out.println("This is not a valid price");
                        System.out.println("Terminating party insert");
                        continue;
                    }
                    scan.nextLine();
                    System.out.print("Enter time and date of party in the format yyyy-mm-dd hh:mm:ss: ");
                    String timingS = scan.nextLine();
                    Timestamp timing = null;
                    // try statement as since using Timestamp data type error is detected before DB call
                    try {
                        timing = Timestamp.valueOf(timingS);
                    }
                    catch (IllegalArgumentException ex){
                        System.out.println("The entered time and date is in an incorrect format");
                        System.out.println("Terminating party insert");
                        continue;
                    }
                    System.out.print("Enter number of guests attending: ");
                    try {
                        numberofguests = scan.nextInt();
                    }
                    catch(IllegalArgumentException ex){
                        System.out.println("Number of guests is invalid");
                        System.out.println("Terminating party insert");
                        continue;
                    }
                    scan.nextLine();
                    // call to insert into DB
                    insertParty(conn, name, mid, vid, eid, price, timing, numberofguests);
                }
                else{
                    System.out.println("Invalid user request, please try again");
                }
            }
            // Final closing of the database on exit
            conn.close();
            System.out.println("Connection closed");
        } catch (SQLException ex) {
            System.out.println("Connection to database failed");
            ex.printStackTrace();
        }
    }

    public static boolean insertParty(Connection conn, String name, int mid, int vid, int eid, int pricing, Timestamp timing, int numberofguests){
        try {
            PreparedStatement insertPartyTableStmt = conn.prepareStatement("" +
                    "INSERT INTO Party (name, mid, vid, eid, price, timing, numberofguests) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?)");
            // Set variables
            insertPartyTableStmt.setString(1, name);
            insertPartyTableStmt.setInt(2, mid);
            insertPartyTableStmt.setInt(3, vid);
            insertPartyTableStmt.setInt(4, eid);
            insertPartyTableStmt.setInt(5, pricing);
            insertPartyTableStmt.setTimestamp(6, timing);
            insertPartyTableStmt.setInt(7, numberofguests);

            // Call the query
            insertPartyTableStmt.executeUpdate();
            System.out.println("Party successfully added");
            return true;
        } catch (SQLException e) {
            String error = e.getMessage();
            System.out.println(error);
            String IDPattern = ".*\\((\\d+)\\).*\"(\\w+)\"";
            Pattern IDre = Pattern.compile(IDPattern);
            Matcher IDmatch = IDre.matcher(error);
            // if match first regex the error is for invalid IDs
            if(IDmatch.find()){
                System.out.println("ID " + IDmatch.group(1) + "does not exist in " + IDmatch.group(2) +" table.");
                System.out.println("Terminating party insert");
            }
            // If not first regex  it must be for negative values in either price or numberofguests
            else{
                String negativeInputPattern = "(price|numberofguests)";
                Pattern negativere = Pattern.compile(negativeInputPattern);
                Matcher negativematch = negativere.matcher(error);
                if(negativematch.find()) {
                    System.out.println(negativematch.group(0) + " should not have a negative value.");
                    System.out.println("Terminating party insert");
                }
            }
        }
        return false;
    }
}
