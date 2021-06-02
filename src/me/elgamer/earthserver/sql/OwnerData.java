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

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
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
					("SELECT * FROM " + instance.ownerData + " WHERE REGION_ID=?,UUID=?");
			statement.setString(1, region);
			statement.setString(2, uuid);
			
			ResultSet results = statement.executeQuery();

			return (results.next());

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
