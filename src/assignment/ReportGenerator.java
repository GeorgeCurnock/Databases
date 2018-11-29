package assignment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportGenerator {

    public static boolean MenuReportGenerator(Connection conn, int menuID) {
        String menuDescription = "";
        int menuCost = 0;
        int partyNum = 0;
        int guestNum = 0;

        try {
            // statement to select the menu id, description, price
            PreparedStatement menuInfoStmt = conn.prepareStatement("" +
                    "SELECT description, costprice " +
                    "FROM menu " +
                    "WHERE mid = ?");
            menuInfoStmt.setInt(1, menuID);
            ResultSet menuInfoRS = menuInfoStmt.executeQuery();
            if(menuInfoRS.next()) {
                menuDescription = menuInfoRS.getString(1);
                menuCost = menuInfoRS.getInt(2);
            }


            // statement to select the number of parties the menu was used at
            PreparedStatement numOfPartiesStmt = conn.prepareStatement("" +
                    "SELECT COUNT(party.pid) " +
                    "FROM party " +
                    "WHERE party.mid = ?");
            numOfPartiesStmt.setInt(1, menuID);
            ResultSet numOfPartiesRS = numOfPartiesStmt.executeQuery();
            if(numOfPartiesRS.next()) {
                partyNum = numOfPartiesRS.getInt(1);
            }

            // statement to select the total number of guests that used the menu
            PreparedStatement numOfGuestsStmt = conn.prepareStatement("" +
                    "SELECT SUM(party.numberofguests) " +
                    "FROM party " +
                    "WHERE party.pid IN (SELECT party.pid " +
                                        "FROM party " +
                                        "WHERE party.mid = ?)");
            numOfGuestsStmt.setInt(1, menuID);
            ResultSet numOfGuestsRS = numOfGuestsStmt.executeQuery();
            if(numOfGuestsRS.next()) {
                guestNum = numOfGuestsRS.getInt(1);
            }

            // Generate report with variables
            String fileName = "Reports/MenuReports/menu_Report_" + menuID + ".txt";
            FileWriter fw = new FileWriter(fileName);
            BufferedWriter bf = new BufferedWriter(fw);
            bf.write("Menu ID: " + menuID);
            bf.write("\nMenu Description: " + menuDescription);
            bf.write("\nCost per person: " + menuCost);
            bf.write("\nNumber of parties featuring this menu: " + partyNum);
            bf.write("\nNumber of guests that used this menu: " + guestNum);
            bf.close();
            return true;

        } catch (SQLException e) {
            System.out.println("That menu does not exist");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            System.out.println("Error generating report\n Perhaps the report already exists.");
            return false;
        }

    }

    public static boolean PartyReportGenerator(Connection conn, int partyID){
        String partyName = "";
        int eid = 0;
        int vid = 0;
        int mid = 0;
        String venueName = "";
        String menuDescription = "";
        String entertainmentDescription = "";
        int numberOfGuests  = 0;
        int price = 0;
        int totalcost = 0;
        int netProfit;

        try {
            // statement to select the party name, number of guests, price charges
            // as well as ids for further queries
            PreparedStatement partyInfoStmt = conn.prepareStatement("" +
                    "SELECT name, eid, vid, mid, numberofguests, price " +
                    "FROM party " +
                    "WHERE pid = ?");
            partyInfoStmt.setInt(1, partyID);
            ResultSet partyInfoRS = partyInfoStmt.executeQuery();
            if(partyInfoRS.next()) {
                partyName = partyInfoRS.getString(1);
                eid = partyInfoRS.getInt(2);
                vid = partyInfoRS.getInt(3);
                mid = partyInfoRS.getInt(4);
                numberOfGuests = partyInfoRS.getInt(5);
                price = partyInfoRS.getInt(6);
            }

            // statement to select the venue name
            PreparedStatement venueNameStmt = conn.prepareStatement("" +
                    "SELECT venue.name " +
                    "FROM venue " +
                    "WHERE vid=?");
            venueNameStmt.setInt(1, vid);
            ResultSet venueNameRS = venueNameStmt.executeQuery();
            if(venueNameRS.next()){
                venueName = venueNameRS.getString(1);
            }

            // statement to select the menu description
            PreparedStatement menuDescriptionStmt = conn.prepareStatement("" +
                    "SELECT menu.description " +
                    "FROM menu " +
                    "WHERE mid=?");
            menuDescriptionStmt.setInt(1, vid);
            ResultSet menuDescriptionRS = menuDescriptionStmt.executeQuery();
            if(menuDescriptionRS.next()){
                menuDescription = menuDescriptionRS.getString(1);
            }


            // statement to select the description of entertainment
            PreparedStatement entertainmentDescriptionStmt = conn.prepareStatement("" +
                    "SELECT entertainment.description " +
                    "FROM entertainment " +
                    "WHERE eid=?");
            entertainmentDescriptionStmt.setInt(1, vid);
            ResultSet entertainmentDescriptionRS = entertainmentDescriptionStmt.executeQuery();
            if(entertainmentDescriptionRS.next()){
                entertainmentDescription = entertainmentDescriptionRS.getString(1);
            }


            // statement to select the total cost price
            PreparedStatement getPrice = conn.prepareStatement("" +
                    "SELECT SUM((m.costprice * ?) + e.costprice + v.venuecost) " +
                    "FROM Entertainment e, Menu m, Venue v " +
                    "WHERE e.eid=? AND m.mid=? AND v.vid=?");

            getPrice.setInt(1, numberOfGuests);
            getPrice.setInt(2, eid);
            getPrice.setInt(3, mid);
            getPrice.setInt(4, vid);
            ResultSet priceRS = getPrice.executeQuery();
            if(priceRS.next()){
                totalcost = priceRS.getInt(1);
            }

            // using variables we do the party cost-total cost price
            netProfit = price - totalcost;


            // Generate report with variables
                String fileName = "Reports/PartyReports/Party_Report_"+ partyID + ".txt";
                FileWriter fw = new FileWriter(fileName);
                BufferedWriter bf = new BufferedWriter(fw);
                bf.write("Party ID: " + partyID);
                bf.write("\nParty Name: " + partyName);
                bf.write("\nVenue: " + venueName);
                bf.write("\nMenu: " + menuDescription);
                bf.write("\nEntertainment: " + entertainmentDescription);
                bf.write("\nGuest Count: " + numberOfGuests);
                bf.write("\nQuoted party cost: £" + (float)price/100);
                bf.write("\nTotal cost of party: £"+ (float)totalcost/100);
                bf.write("\nNet profit: £" + (float)netProfit/100);
                bf.close();
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("That partyID does not exist");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error generating report");
            return false;
        }
    }
}
