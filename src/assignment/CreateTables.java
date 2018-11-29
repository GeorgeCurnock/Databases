package assignment;
import java.sql.*;

public class CreateTables {

    private static Connection conn;

    public static void executeStartup(Connection _conn){
        conn = _conn;
        if(dropTables()) {
            createEntertainment();
            createMenu();
            createVenue();
            createParty();
        }
        else{
            System.out.println("Error creating tables");
        }
    }

    private static boolean dropTables() {
        try {
            // Drops the schema that holds all the tables
            PreparedStatement dropSchema = conn.prepareStatement(
                    "DROP SCHEMA public CASCADE"
            );
            // Executes the update
            dropSchema.executeUpdate();
            // Creates a new schema in the place of the previous
            PreparedStatement createScheme = conn.prepareStatement(
                    "CREATE SCHEMA public"
            );
            // Executes the update
            createScheme.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error dropping tables");
        }
        return false;
    }

    private static boolean createEntertainment() {
        try {
            PreparedStatement createEntertainmentTable = conn.prepareStatement(
                    "" +
                            "CREATE TABLE Entertainment(" +
                            "eid            SERIAL," +
                            "description    VARCHAR," +
                            "costprice      INTEGER," +
                            "PRIMARY KEY (eid)," +
                            "CHECK (costprice >= 0)" +
                            ")"
            );
            createEntertainmentTable.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error creating Table: Entertainment");
        }
        return false;
    }

    private static boolean createMenu() {
        try {
            PreparedStatement createMenuTable = conn.prepareStatement(
                    "" +
                            "CREATE TABLE Menu(" +
                            "mid            SERIAL," +
                            "description    VARCHAR," +
                            "costprice      INTEGER," +
                            "PRIMARY KEY (mid)," +
                            "CHECK (costprice >= 0)" +
                            ")"
            );
            createMenuTable.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error creating Table: Menu");
        }
        return false;
    }

    private static boolean createVenue() {
        try {
            PreparedStatement createVenueTable = conn.prepareStatement(
                    "" +
                            "CREATE TABLE Venue(" +
                            "vid            SERIAL," +
                            "name    VARCHAR(50)," +
                            "venuecost      INTEGER," +
                            "PRIMARY KEY (vid)," +
                            "CHECK (venuecost >= 0)" +
                            ")"
            );
            createVenueTable.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error creating Table: Venue");
        }
        return false;
    }

    private static boolean createParty() {
        try {
            PreparedStatement createPartyTable = conn.prepareStatement(
                    "" +
                            "CREATE TABLE Party(" +
                            "pid            SERIAL," +
                            "name    VARCHAR(50)," +
                            "mid            INTEGER," +
                            "vid            INTEGER," +
                            "eid            INTEGER," +
                            "price      INTEGER," +
                            "timing      TIMESTAMP," +
                            "numberofguests      INTEGER," +
                            "PRIMARY KEY (pid)," +
                            "CHECK (price >= 0)," +
                            "CHECK (numberofguests >= 0)," +
                            "FOREIGN KEY (mid) REFERENCES Menu(mid)" +
                            "ON DELETE CASCADE " +
                            "ON UPDATE CASCADE," +
                            "FOREIGN KEY (vid) REFERENCES Venue(vid)" +
                            "ON DELETE CASCADE " +
                            "ON UPDATE CASCADE," +
                            "FOREIGN KEY (eid) REFERENCES Entertainment(eid)" +
                            "ON DELETE CASCADE " +
                            "ON UPDATE CASCADE" +
                            ")"
            );
            createPartyTable.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error creating Table: Party");
        }
        return false;
    }
}
