package assignment;
import java.sql.*;
import java.util.Scanner;


public class main {
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
        }


        // Attempt to make a connection to the DB
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_NAME, UNAME, PWORD);

            if (conn != null) {
                System.out.println("Database accessed!");
            } else {
                System.out.println("Failed to make connection");
                System.exit(0);
            }
            CreateTables.executeStartup(conn);
            System.out.println("Tables created");
            PopulateTables.executePopulation(conn);
            System.out.println("Tables populated");
            // Setup the scanner
            // Setup the while loop
            // Check for cases
            scan = new Scanner(System.in);
            System.out.println("Welcome to the database UI");
            System.out.println("Type help for available commands");
            String user = "";
            int id = 0;
            while(true){
                System.out.print("Ready for user request: ");
                Thread.sleep(500);
                user = scan.nextLine();
                if(user.toLowerCase().equals("party report")){
                    System.out.print("Please enter the id of the party: ");
                    id = scan.nextInt();
                    scan.nextLine();
                    if(ReportGenerator.PartyReportGenerator(conn, id)){
                        System.out.println("Party_report_" + id + " has been generated");
                    }
                    else {
                        System.out.println("Report generation failed");
                    }
                }
                else if(user.toLowerCase().equals("menu report")){
                    System.out.print("Please enter the id of the menu: ");
                    id = scan.nextInt();
                    scan.nextLine();
                    if(ReportGenerator.MenuReportGenerator(conn, id)){
                        System.out.println("Menu_report_" + id + " has been generated");
                    }
                    else{
                        System.out.println("Report generation failed");
                    }
                }
                else if(user.toLowerCase().equals("help")){
                    System.out.println("Available commands:");
                    System.out.println(": party report           Generate a report for a party in the database");
                    System.out.println(": menu report            Generate a report for a menu in the database");
                    System.out.println(": insert party           add a new party to the database");
                    System.out.println(": quit                   exit the program");
                }
                else if(user.toLowerCase().equals("quit")){
                    break;
                }
                else if(user.toLowerCase().equals("insert party")){
                    System.out.print("Enter name of party: ");
                    String name = scan.nextLine();
                    System.out.print("Enter id of menu: ");
                    int mid = scan.nextInt();
                    scan.nextLine();
                    System.out.print("Enter id of venue: ");
                    int vid = scan.nextInt();
                    scan.nextLine();
                    System.out.print("Enter id of entertainment: ");
                    int eid = scan.nextInt();
                    scan.nextLine();
                    System.out.print("Enter the quoted price: ");
                    int price = scan.nextInt();
                    scan.nextLine();
                    System.out.print("Enter time and date of party in the format yyyy-mm-dd hh:mm:ss: ");
                    String timingS = scan.nextLine();
                    Timestamp timing = Timestamp.valueOf(timingS);
                    System.out.print("Enter number of guests attending: ");
                    int numberofguests = scan.nextInt();
                    scan.nextLine();
                    try {
                        insertParty(conn, name, mid, vid, eid, price, timing, numberofguests);
                        System.out.println("Party successfully added");
                    }catch(IllegalArgumentException ex){
                        System.out.println("Incorrect input, cancelling insert request");
                        ex.printStackTrace();
                    }
                }
                else{
                    System.out.println("Invalid user request, please try again");
                }

            }
            conn.close();
            System.out.println("Connection closed");
        } catch (SQLException ex) {
            System.out.println("Connection to database failed");
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean insertParty(Connection conn, String name, int mid, int vid, int eid, int pricing, Timestamp timing, int numberofguests){
        try {

            // insert the entry into the table
            PreparedStatement populatePartyTableStmt = conn.prepareStatement("" +
                    "INSERT INTO Party (name, mid, vid, eid, price, timing, numberofguests) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?)");
            // Set variables
            populatePartyTableStmt.setString(1, name);
            populatePartyTableStmt.setInt(2, mid);
            populatePartyTableStmt.setInt(3, vid);
            populatePartyTableStmt.setInt(4, eid);
            populatePartyTableStmt.setInt(5, pricing);
            populatePartyTableStmt.setTimestamp(6, timing);
            populatePartyTableStmt.setInt(7, numberofguests);

            // Call the query
            populatePartyTableStmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
