package me.elgamer.earthserver.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Location;

import me.elgamer.earthserver.Main;

public class RequestData {

	//Counts all the requests with a specific owner
	public static int count(String uuid) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT COUNT(*) FROM " + instance.requestData + " WHERE OWNER=? AND OWNER_ACCEPT=?");
			statement.setString(1, uuid);
			statement.setBoolean(2, false);

			ResultSet results = statement.executeQuery();

			if (results.next()) {
				return (results.getInt(1));
			} else {
				return 0;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}	

	}

	//Counts all the requests that need to be accepted by staff
	public static int count() {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT COUNT(*) FROM " + instance.requestData + " WHERE STAFF_ACCEPT=?");
			statement.setBoolean(1, false);

			ResultSet results = statement.executeQuery();

			if (results.next()) {
				return (results.getInt(1));
			} else {
				return 0;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}	

	}

	public static void newRequest(String region, String uuid, boolean staff_accept, boolean owner_accept, Location l) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("INSERT INTO " + instance.requestData + " (ID,REGION_ID,OWNER,UUID,STAFF_ACCEPT,OWNER_ACCEPT,X,Y,Z) VALUE (?,?,?,?,?,?,?,?,?)");

			statement.setInt(1, getNewID());

			statement.setString(2, region);
			statement.setString(4, uuid);

			statement.setString(3, OwnerData.getOwner(region));

			statement.setBoolean(5, staff_accept);
			statement.setBoolean(6, owner_accept);

			statement.setDouble(7, l.getX());
			statement.setDouble(8, l.getY());
			statement.setDouble(9, l.getZ());

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static int getNewID() {

		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.requestData);

			ResultSet results = statement.executeQuery();

			if (results.last()) {

				return (results.getInt("ID") + 1);

			} else {
				return 1;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return 1;
		}		
	}

	//Get all request for a specific owner
	public static ResultSet getRequests(String uuid) {

		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.requestData + " WHERE OWNER=? AND OWNER_ACCEPT=?");
			statement.setString(1, uuid);
			statement.setBoolean(2, false);

			return (statement.executeQuery());

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	//Get all staff requests by jr builders
	public static ResultSet getRequests() {

		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.requestData + " WHERE STAFF_ACCEPT=?");
			statement.setBoolean(1, false);

			return (statement.executeQuery());

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static Location getRequestLocation(String region, String requester) {

		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.requestData + " WHERE REGION_ID=? AND UUID=?");
			statement.setString(1, region);
			statement.setString(2, requester);

			ResultSet results = statement.executeQuery();

			if (results.next()) {
				return new Location(Main.buildWorld, results.getDouble("X"), results.getDouble("Y"), results.getDouble("Z"));
			} else {
				return null;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}


	}

	public static void setOwnerAccept(String region, String requester, boolean value) {

		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("UPDATE " + instance.requestData + " SET OWNER_ACCEPT=? WHERE REGION_ID=? AND UUID=?");
			statement.setBoolean(1, value);
			statement.setString(2, region);
			statement.setString(3, requester);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void setStaffAccept(String region, String requester, boolean value) {

		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("UPDATE " + instance.requestData + " SET STAFF_ACCEPT=? WHERE REGION_ID=? AND UUID=?");
			statement.setBoolean(1, value);
			statement.setString(2, region);
			statement.setString(3, requester);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	//Checks whether the region is already accepted by staff
	public static boolean staffAccept(String region, String requester) {

		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.requestData + " WHERE REGION_ID=? AND UUID=?");
			statement.setString(1, region);
			statement.setString(2, requester);

			ResultSet results = statement.executeQuery();

			if (results.next()) {
				return results.getBoolean("STAFF_ACCEPT");
			} else {
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}		

	}

	//Checks whether the region is already accepted by the owner
	public static boolean ownerAccept(String region, String requester) {

		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.requestData + " WHERE REGION_ID=? AND UUID=?");
			statement.setString(1, region);
			statement.setString(2, requester);

			ResultSet results = statement.executeQuery();

			if (results.next()) {
				return results.getBoolean("OWNER_ACCEPT");
			} else {
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}		

	}

	public static void closeRequest(String region, String requester) {

		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("DELETE FROM " + instance.requestData + " WHERE REGION_ID=? AND UUID=?");
			statement.setString(1, region);
			statement.setString(2, requester);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	//Checks whether the request still exists for the owner
	public static boolean requestExists(String region, String requester) {

		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.requestData + " WHERE REGION_ID=? AND UUID=? AND OWNER_ACCEPT=?");
			statement.setString(1, region);
			statement.setString(2, requester);
			statement.setBoolean(3, false);

			ResultSet results = statement.executeQuery();

			return results.next();

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}		

	}

	//Checks whether the request still exists for staff
	public static boolean requestExists(String region, String requester, boolean staff) {

		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.requestData + " WHERE REGION_ID=? AND UUID=? AND STAFF_ACCEPT=?");
			statement.setString(1, region);
			statement.setString(2, requester);
			statement.setBoolean(3, false);

			ResultSet results = statement.executeQuery();

			return results.next();

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}		

	}

	public static void updateRegionOwner(String region, String newOwner) {

		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("UPDATE " + instance.requestData + " SET OWNER=? WHERE REGION_ID=?");
			statement.setString(1, newOwner);
			statement.setString(2, region);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	//Counts all requests by a specific player.
	public static int countRequests(String uuid) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT COUNT(*) FROM " + instance.requestData + " WHERE UUID=?");
			statement.setString(1, uuid);

			ResultSet results = statement.executeQuery();

			if (results.next()) {
				return (results.getInt(1));
			} else {
				return 0;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}

	}

	public static boolean hasRequest(String uuid) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.requestData + " WHERE UUID=?");
			statement.setString(1, uuid);

			ResultSet results = statement.executeQuery();

			return (results.next());

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	//Get all request for a specific owner
	public static ResultSet getYourRequests(String uuid) {

		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.requestData + " WHERE UUID=?");
			statement.setString(1, uuid);

			return (statement.executeQuery());

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	//Checks whether the request still exists
	public static boolean hasRequested(String region, String requester) {

		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.requestData + " WHERE REGION_ID=? AND UUID=?");
			statement.setString(1, region);
			statement.setString(2, requester);

			ResultSet results = statement.executeQuery();

			return results.next();

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}		

	}

	public static void closeRequests(String region) {

		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("DELETE FROM " + instance.requestData + " WHERE REGION_ID=?");
			statement.setString(1, region);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	//Check whether you have any requests for claims you own
	public static boolean hasRequestOwner(String uuid) {

		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.requestData + " WHERE OWNER=?");
			statement.setString(1, uuid);

			ResultSet results = statement.executeQuery();

			return results.next();

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}	

	}

	//Check whether you have any requests for claims you own
	public static boolean hasRequestStaff() {

		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.requestData + " WHERE STAFF_ACCEPT=?");
			statement.setBoolean(1, false);

			ResultSet results = statement.executeQuery();

			return results.next();

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}	

	}

}
