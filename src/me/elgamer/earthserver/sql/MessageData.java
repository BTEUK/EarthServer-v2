package me.elgamer.earthserver.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.bukkit.ChatColor;

public class MessageData {

	DataSource dataSource;

	public MessageData(DataSource dataSource) {

		this.dataSource = dataSource;

	}

	private Connection conn() throws SQLException {
		return dataSource.getConnection();
	}

	public void newMessage(String uuid, String message, String colour) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"INSERT INTO messages(uuid, message, colour) VALUES(?, ?, ?);"
				)){

			statement.setString(1, uuid);
			statement.setString(2, message);			
			statement.setString(3, colour);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public boolean hasMessage(String uuid) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT uuid FROM messages WHERE uuid = ?;"
				)){

			statement.setString(1, uuid);

			ResultSet results = statement.executeQuery();

			return (results.next());

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public ArrayList<String> getMessages(String uuid) {

		ArrayList<String> messages = new ArrayList<String>();

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT message, colour FROM messages WHERE uuid = ?;"
				)){

			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();

			while (results.next()) {
				messages.add(ChatColor.valueOf(results.getString("colour")) + results.getString("message"));
			}

			return messages;

		} catch (SQLException e) {
			e.printStackTrace();
			return messages;
		}

	}

	public void removeMessages(String uuid) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"DELETE FROM messages WHERE uuid = ?;"
				)){

			statement.setString(1, uuid);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
