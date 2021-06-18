package me.elgamer.earthserver.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.elgamer.earthserver.Main;

public class RequestData {

	public static int count(String uuid) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT COUNT(*) FROM " + instance.requestData + " WHERE OWNER=?");
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

	public static void newRequest(String region, String uuid, boolean staff_accept, boolean owner_accept) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("INSERT INTO " + instance.requestData + " (ID,REGION_ID,OWNER,UUID,STAFF_ACCEPT,OWNER_ACCEPT) VALUE (?,?,?,?,?,?)");
			
			statement.setInt(1, getNewID());
			
			statement.setString(2, region);
			statement.setString(4, uuid);
			
			statement.setString(3, OwnerData.getOwner(region));
			
			statement.setBoolean(5, staff_accept);
			statement.setBoolean(6, owner_accept);
			
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


}
