package me.elgamer.earthserver.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import javax.sql.DataSource;

import org.bukkit.Bukkit;

import me.elgamer.earthserver.utils.Time;
import me.elgamer.earthserver.utils.User;

public class PlayerData {

	DataSource dataSource;

	public PlayerData(DataSource dataSource) {

		this.dataSource = dataSource;

	}

	private Connection conn() throws SQLException {
		return dataSource.getConnection();
	}

	//Create player instance
	public boolean addPlayer(User u) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"INSERT INTO players(uuid, name, role, last_join) VALUES(?, ?, ?, ?);"
				)){

			statement.setString(1, u.uuid);
			statement.setString(2, u.name);
			statement.setString(3, u.builder_role);
			statement.setLong(4, Time.currentTime());

			statement.executeUpdate();

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	//Check is player has instance in table
	public boolean hasPlayer(User u) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT uuid FROM players WHERE uuid = ?;"
				)){

			statement.setString(1, u.uuid);

			ResultSet results = statement.executeQuery();

			if (results.next()) {
				return true;
			} else {
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

	//Update player data
	public void updatePlayer(User u) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"UPDATE players SET name = ?, role = ?, last_join = ? WHERE uuid = ?;"
				)){

			statement.setString(1, u.name);
			statement.setString(2, u.builder_role);
			statement.setLong(3, Time.currentTime());

			//Rather than use buildingTime from here, use the point plugin instead.
			//statement.setInt(4, u.buildingTime);
			me.elgamer.btepoints.utils.PlayerData.setBuildTime(u.uuid, u.buildingTime);


			statement.setString(4, u.uuid);
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public String getName(String uuid) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT name FROM players WHERE uuid = ?;"
				)){

			statement.setString(1, uuid);

			ResultSet results = statement.executeQuery();

			if (results.next()) {
				return (results.getString("name"));
			} else {
				if (Bukkit.getPlayer(uuid) != null) {
					return (Bukkit.getPlayer(UUID.fromString(uuid)).getName());
				} else {
					return (Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}		

	}

	public String getUUID(String uuid) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT uuid FROM players WHERE name = ?;"
				)){

			statement.setString(1, uuid);

			ResultSet results = statement.executeQuery();

			if (results.next()) {
				return (results.getString("uuid"));
			} else {
				return null;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}		

	}

	//Create player instance if not connected to server
	public boolean addPlayer(String uuid, String name) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"INSERT INTO players(uuid, name, role, last_join) VALUES(?, ?, ?, ?);"
				)){

			statement.setString(1, uuid);
			statement.setString(2, name);
			statement.setString(3, "builder");
			statement.setLong(4, Time.currentTime());

			statement.executeUpdate();

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
