package me.elgamer.earthserver.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

}
