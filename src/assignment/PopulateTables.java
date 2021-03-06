package assignment;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;


public class PopulateTables {

    public static void executePopulation(Connection conn) {
        Random rand = new Random();
        populateEntertainment(rand, conn);
        populateMenu(rand, conn);
        populateVenue(rand, conn);
        populateParty(rand, conn);
    }

    private static void populateEntertainment(Random rand, Connection conn) {
        // List of dummmy entertainments
        String[] descriptions = new String[]{"Clown", "Bouncy Castle", "Circus", "Animals", "Lecture", "Sports", "paintball", "painting", "drawing",  "Video games"};
        String description;
        int price;
        try {
            PreparedStatement populateEntertainmentTableStmt = conn.prepareStatement(
                    "" +
                            "INSERT INTO Entertainment (description, costprice)" +
                            "VALUES(?,?)");
            // Sets the statements variables to random values and creates an entry 100 times
            for (int i = 0; i < 100; i++) {
                description = descriptions[rand.nextInt(descriptions.length)];
                price = rand.nextInt(50000);
                populateEntertainmentTableStmt.setString(1, description);
                populateEntertainmentTableStmt.setInt(2, price);
                populateEntertainmentTableStmt.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error populating table: Entertainment");
        }
    }

    private static void populateMenu(Random rand, Connection conn) {
        String[] descriptions = new String[]{"Roast Dinner", "Fish and Chips", "Chicken Chow Mein", "chicken Tikka Masala", "Salad", "Burger", "Hot Dog", "Pasta Bake", "Sandwich Buffet", "Giant Cake"};
        String description;
        int price;
        try {
            PreparedStatement populateMenuTableStmt = conn.prepareStatement(
                    "" +
                            "INSERT INTO Menu (description, costprice)" +
                            "VALUES(?,?)");
            // Sets the statements variables to random values and creates an entry 100 times
            for (int i = 0; i < 100; i++) {
                description = descriptions[rand.nextInt(descriptions.length)];
                price = rand.nextInt(2000)+ 1000;
                populateMenuTableStmt.setString(1, description);
                populateMenuTableStmt.setInt(2, price);
                populateMenuTableStmt.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error populating table: Menu");
        }
    }

    private static void populateVenue(Random rand, Connection conn) {
        String[] names = new String[]{"Football Stadium", "Office Block", "Concert Hall", "School", "Park", "Swimming Pool", "House", "Restaurant", "Paintball Centre", "Lecture Theatre"};
        String name;
        int venuecost;
        try {
            PreparedStatement populateVenueTableStmt = conn.prepareStatement(
                    "" +
                            "INSERT INTO Venue (name, venuecost)" +
                            "VALUES(?,?)");
            // Sets the statements variables to random values and creates an entry 100 times
            for (int i = 0; i < 100; i++) {
                name = names[rand.nextInt(names.length)];
                venuecost = rand.nextInt(40000) + 10000;
                populateVenueTableStmt.setString(1, name);
                populateVenueTableStmt.setInt(2, venuecost);
                populateVenueTableStmt.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error populating table: Venue");
        }
    }

    private static void populateParty(Random rand, Connection conn) {
        // Make calls to other tables to get random values for party population
        String[] names = new String[]{"George's", "James's", "Adam's", "Rory's", "Louise's", "Yasmin's", "Megan's", "Robert's", "Andrew's", "Grace's"};
        String name;
        int mid=0;
        int vid=0;
        int eid=0;
        ResultSet priceRS;
        int price=0;
        Timestamp timing;
        int numberOfGuests;
        try {
            // Selects random IDs from each table to be used in the party entries
            PreparedStatement randomMIDStmt = conn.prepareStatement("" +
                    "SELECT mid FROM Menu " +
                    "ORDER BY random() " +
                    "LIMIT 1");

            PreparedStatement randomVIDStmt = conn.prepareStatement("" +
                    "SELECT vid FROM Venue " +
                    "ORDER BY random() " +
                    "LIMIT 1");

            PreparedStatement randomEIDStmt = conn.prepareStatement("" +
                    "SELECT eid FROM Entertainment " +
                    "ORDER BY random() " +
                    "LIMIT 1");

            // Gets the total price of all IDs selected
            PreparedStatement getPriceStmt = conn.prepareStatement("" +
                    "SELECT SUM((m.costprice * ?) + e.costprice + v.venuecost) " +
                    "FROM Entertainment e, Menu m, Venue v " +
                    "WHERE e.eid=? AND m.mid=? AND v.vid=?");

            // Call to populate the party table
            PreparedStatement populatePartyTableStmt = conn.prepareStatement("" +
                    "INSERT INTO Party (name, mid, vid, eid, price, timing, numberofguests)" +
                    "VALUES(?, ?, ?, ?, ?, ?, ?)");

            // Sets the statements variables to random values and creates an entry 1000 times
            for (int i = 0; i < 1000; i++) {
                // Determine values for party
                name = names[rand.nextInt(names.length)];
                ResultSet midRS = randomMIDStmt.executeQuery();
                if (midRS.next()) {
                    mid = midRS.getInt(1);
                }
                ResultSet vidRS = randomVIDStmt.executeQuery();
                if (vidRS.next()) {
                    vid = vidRS.getInt(1);
                }
                ResultSet eidRS = randomEIDStmt.executeQuery();
                if (eidRS.next()) {
                    eid = eidRS.getInt(1);
                }

                numberOfGuests = rand.nextInt(1000);
                // Sets variables for price query
                getPriceStmt.setInt(1, numberOfGuests);
                getPriceStmt.setInt(2, eid);
                getPriceStmt.setInt(3, mid);
                getPriceStmt.setInt(4, vid);
                priceRS = getPriceStmt.executeQuery();
                if(priceRS.next()){
                    price = priceRS.getInt(1);
                    // adds a random value to the total to act as 'profit'
                    price = price + rand.nextInt(10000);
                }
                // Create a randome Timestamp
                timing = createTime(rand);
                // Assign variables in statement
                populatePartyTableStmt.setString(1, name);
                populatePartyTableStmt.setInt(2, mid);
                populatePartyTableStmt.setInt(3, vid);
                populatePartyTableStmt.setInt(4, eid);
                populatePartyTableStmt.setInt(5, price);
                populatePartyTableStmt.setTimestamp(6, timing);
                populatePartyTableStmt.setInt(7, numberOfGuests);
                populatePartyTableStmt.executeUpdate();
            }
        }
        catch(SQLException e){
                e.printStackTrace();
                System.out.println("Error populating table: Party");
            }
    }

    private static Timestamp createTime(Random rand){
        // Offset is the lowest value of time
        long offset = Timestamp.valueOf("1900-01-01 00:00:00").getTime();
        // end is the maximum value of time
        long end = Timestamp.valueOf("2100-01-01 00:00:00").getTime();
        // diff is the range of times we can have
        long diff = end - offset + 1;
        // randomTime = lowestTime + a random amount of difference
        Timestamp randomTime = new Timestamp(offset + (long)(Math.random() * diff));
        return randomTime;
    }
}
