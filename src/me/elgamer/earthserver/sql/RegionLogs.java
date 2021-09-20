package me.elgamer.earthserver.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.sql.DataSource;

import me.elgamer.earthserver.utils.Time;

public class RegionLogs {

	DataSource dataSource;

	public RegionLogs(DataSource dataSource) {

		this.dataSource = dataSource;

	}

	private Connection conn() throws SQLException {
		return dataSource.getConnection();
	}
	
	public void newLog(String region, String uuid, String role) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"INSERT INTO logs(region, uuid, role, start_time, end_time) VALUES(?, ?, ?, ?, ?);"
				)){

			statement.setString(1, region);
			statement.setString(2, uuid);

			statement.setString(3, role);

			statement.setLong(4, Time.currentTime());
			statement.setLong(5, 0);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void closeLog(String region, String uuid) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"UPDATE logs SET end_time = ? WHERE region = ? AND uuid = ? AND end_time = ?;"
				)){

			statement.setString(2, region);
			statement.setString(3, uuid);

			statement.setLong(4, 0);

			statement.setLong(1, Time.currentTime());

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void closeLogs(HashMap<String, String> data) {

		for (Entry<String, String> entry : data.entrySet()) {

			closeLog(entry.getKey(), entry.getValue());

		}

	}

	public void newLogs(HashMap<String, String> data, String role) {

		for (Entry<String, String> entry : data.entrySet()) {

			newLog(entry.getKey(), entry.getValue(), role);

		}

	}
}
