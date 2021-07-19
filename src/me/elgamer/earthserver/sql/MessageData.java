package me.elgamer.earthserver.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.elgamer.earthserver.Main;

public class MessageData {

	private static int getNewID() {

		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.messageData);

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
	
	public static void newMessage(String uuid, String message, String colour) {
		
		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("INSERT INTO " + instance.messageData + " (ID,UUID,MESSAGE,COLOUR) VALUE (?,?,?,?)");
			
			statement.setInt(1, getNewID());
			
			statement.setString(2, uuid);
			statement.setString(3, message);			
			statement.setString(4, colour);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	
	}
	
	public static boolean hasMessage(String uuid) {
		
		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.messageData + " WHERE UUID=?");
			statement.setString(1, uuid);
			
			ResultSet results = statement.executeQuery();
			
			return (results.next());
					
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public static ResultSet getMessages(String uuid) {
		
		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.messageData + " WHERE UUID=?");
			statement.setString(1, uuid);
			
			return statement.executeQuery();
					
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static void removeMessages(String uuid) {
		
		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("DELETE FROM " + instance.messageData + " WHERE UUID=?");
			statement.setString(1, uuid);
			
			statement.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
