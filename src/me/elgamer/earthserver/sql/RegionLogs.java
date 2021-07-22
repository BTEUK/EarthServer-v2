package me.elgamer.earthserver.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.utils.Time;

public class RegionLogs {

	public static void newLog(String region, String uuid, String role) {

		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("INSERT INTO " + instance.regionLogs + " (ID,REGION_ID,UUID,ROLE,START_TIME,END_TIME) VALUE (?,?,?,?,?,?)");

			statement.setInt(1, getNewID());

			statement.setString(2, region);
			statement.setString(3, uuid);

			statement.setString(4, role);

			statement.setLong(5, Time.currentTime());
			statement.setLong(6, 0);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void closeLog(String region, String uuid) {

		Main instance = Main.getInstance();

		PreparedStatement statement;

		try {
			statement = instance.getConnection().prepareStatement
					("UPDATE " + instance.regionLogs + " SET END_TIME=? WHERE REGION_ID=? AND UUID=? AND END_TIME=?");

			statement.setString(2, region);
			statement.setString(3, uuid);

			statement.setLong(4, 0);

			statement.setLong(1, Time.currentTime());

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private static int getNewID() {

		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.regionLogs);

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

	public static void closeLogs(HashMap<String, String> data) {

		for (Entry<String, String> entry : data.entrySet()) {

			closeLog(entry.getKey(), entry.getValue());

		}

	}

	public static void newLogs(HashMap<String, String> data, String role) {

		for (Entry<String, String> entry : data.entrySet()) {

			newLog(entry.getKey(), entry.getValue(), role);

		}

	}

	public static void startLogs() {

		ResultSet owners = OwnerData.getOwners();
		ResultSet members = MemberData.getMembers();

		Main instance = Main.getInstance();

		PreparedStatement statement;

		try {
			while (owners.next()) {

				statement = instance.getConnection().prepareStatement
						("INSERT INTO " + instance.regionLogs + " (ID,REGION_ID,UUID,ROLE,START_TIME,END_TIME) VALUE (?,?,?,?,?,?)");

				statement.setInt(1, getNewID());

				statement.setString(2, owners.getString("REGION_ID"));
				statement.setString(3, owners.getString("UUID"));

				statement.setString(4, "owner");

				statement.setLong(5, Time.currentTime());
				statement.setLong(6, 0);

				statement.executeUpdate();

			}

			while (members.next()) {

				statement = instance.getConnection().prepareStatement
						("INSERT INTO " + instance.regionLogs + " (ID,REGION_ID,UUID,ROLE,START_TIME,END_TIME) VALUE (?,?,?,?,?,?)");

				statement.setInt(1, getNewID());

				statement.setString(2, members.getString("REGION_ID"));
				statement.setString(3, members.getString("UUID"));

				statement.setString(4, "member");

				statement.setLong(5, Time.currentTime());
				statement.setLong(6, 0);

				statement.executeUpdate();

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public static ResultSet getLogsAfter(long time) {
		
		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.regionLogs + " WHERE END_TIME>=? ORDER BY END_TIME ASC");
			statement.setLong(1, time);
			
			return statement.executeQuery();

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}	
		
	}

}
