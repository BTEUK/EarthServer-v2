package me.elgamer.earthserver.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.utils.OldClaim;
import me.elgamer.earthserver.utils.Time;

public class OwnerData {

	//Check is player has instance in table
	public static HashMap<String, String> getInactiveOwners(long inactive) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.ownerData + " WHERE LAST_ENTER<=?");
			statement.setLong(1, inactive);

			ResultSet results = statement.executeQuery();
			HashMap<String, String> inactives = new HashMap<String, String>();

			while (results.next()) {
				inactives.put(results.getString("REGION_ID"), results.getString("UUID"));
			}

			return inactives;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static void removeInactiveOwners(long inactive) {

		Main instance = Main.getInstance();
		PreparedStatement statement;

		try {

			//Get all inactive owners
			statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.ownerData + " WHERE LAST_ENTER<=?");
			statement.setLong(1, inactive); 

			ResultSet results = statement.executeQuery();

			//Close all logs for inactive owners
			while (results.next()) {

				RegionLogs.closeLog(results.getString("REGION_ID"), results.getString("UUID"));

			}

			//Delete all inactive owners from owner table
			statement = instance.getConnection().prepareStatement
					("DELETE FROM " + instance.ownerData + " WHERE LAST_ENTER<=?");
			statement.setLong(1, inactive);
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}


	}

	public static void convertOwners(ArrayList<OldClaim> claims) {

		Main instance = Main.getInstance();

		PreparedStatement statement;

		try {

			for (OldClaim claim : claims) {

				statement = instance.getConnection().prepareStatement
						("INSERT INTO " + instance.ownerData + " (REGION_ID,UUID,LAST_ENTER) VALUE (?,?,?)");
				statement.setString(1, claim.region);
				statement.setString(2, claim.owner);
				statement.setLong(3, Time.currentTime());

				statement.executeUpdate();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static boolean isOwner(String uuid, String region) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.ownerData + " WHERE REGION_ID=? AND UUID=?");
			statement.setString(1, region);
			statement.setString(2, uuid);

			ResultSet results = statement.executeQuery();

			return (results.next());

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean hasOwner(String region) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.ownerData + " WHERE REGION_ID=?");
			statement.setString(1, region);

			ResultSet results = statement.executeQuery();

			return (results.next());

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}	

	}

	public static int count(String uuid) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT COUNT(*) FROM " + instance.ownerData + " WHERE UUID=?");
			statement.setString(1, uuid);

			ResultSet results = statement.executeQuery();

			if (results.next()) {
				return (results.getInt("1"));
			} else {
				return 0;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}	

	}

	public static void addOwner(String region, String uuid) {

		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("INSERT INTO " + instance.ownerData + " (REGION_ID,UUID,LAST_ENTER) VALUE (?,?,?)");
			statement.setString(1, region);
			statement.setString(2, uuid);
			statement.setLong(3, Time.currentTime());

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static String getOwner(String region) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.ownerData + " WHERE REGION_ID=?");
			statement.setString(1, region);

			ResultSet results = statement.executeQuery();

			if (results.next()) {

				return (results.getString("UUID"));

			} else {
				return "false";
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return "false";
		}	

	}

	public static void updateTime(String uuid, String region) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("UPDATE " + instance.ownerData + " SET LAST_ENTER=? WHERE REGION_ID=? AND UUID=?");
			statement.setLong(1, Time.currentTime());
			statement.setString(2, region);
			statement.setString(3, uuid);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}	

	}

	public static void addNewOwners(HashMap<String, String> regions) {

		String uuid;

		for (String s : regions.keySet()) {

			if (MemberData.hasMember(s)) {

				uuid = MemberData.latestMember(s);

				OwnerData.addOwner(s, uuid);
				MemberData.removeMember(s, uuid);
				RegionLogs.closeLog(s, uuid);
				RegionLogs.newLog(s, uuid, "owner");
				RequestData.updateRegionOwner(s, uuid);

			}

		}


	}

	public static void addNewOwner(String region) {

		if (MemberData.hasMember(region)) {

			String uuid = MemberData.latestMember(region);

			OwnerData.addOwner(region, uuid);
			MemberData.removeMember(region, uuid);
			RegionLogs.closeLog(region, uuid);
			RegionLogs.newLog(region, uuid, "owner");
			RequestData.updateRegionOwner(region, uuid);

		}

	}

	public static ResultSet getRegions(String uuid) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.ownerData + " WHERE UUID=?");
			statement.setString(1, uuid);

			ResultSet results = statement.executeQuery();

			return results;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static void removeOwner(String uuid, String region) {

		Main instance = Main.getInstance();
		PreparedStatement statement;

		try {

			statement = instance.getConnection().prepareStatement
					("DELETE FROM " + instance.ownerData + " WHERE UUID=? AND REGION_ID=?");
			statement.setString(1, uuid);
			statement.setString(2, region);
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}


	}
	
	public static ResultSet getOwners() {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.ownerData);

			ResultSet results = statement.executeQuery();

			return results;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

}
