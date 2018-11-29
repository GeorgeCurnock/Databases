import java.sql.*;
import javax.sql.*;


public class Main {

    public static final String DB_NAME = "jdbc:postgresql://mod-intro-databases.cs.bham.ac.uk/";
    public static final String UNAME = "gxc712";
    public static final String PWORD = "qcsprzhm2i";

    public static void main(String[] args){
        // Attempts to load required drivers
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            System.out.println("Driver not found");
        }
        // If successful prints so
        System.out.println("PostgreSQL driver registered.");

        // Attempt to make a connection to the DB
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_NAME, UNAME, PWORD);


            try{
                // Used to ensure that the catch statement does not throw an errror
                // Insert code here
                PreparedStatement stmt = conn.prepareStatement("");
                ResultSet rs = stmt.executeQuery();

            }
            catch (SQLDataException ex){
                System.out.println("Error in Statement");
            }
            finally {
                conn.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if (conn != null) {
            System.out.println("Database accessed!");
        } else {
            System.out.println("Failed to make connection");
            System.exit(0);
        }
    }
}

