package me.elgamer.earthserver.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.DataSource;

//import org.bukkit.Bukkit;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.utils.Time;

public class OwnerData {

	DataSource dataSource;

	public OwnerData(DataSource dataSource) {

		this.dataSource = dataSource;

	}

	private Connection conn() throws SQLException {
		return dataSource.getConnection();
	}
	
	//Check is player has instance in table
	public HashMap<String, String> getInactiveOwners(long inactive) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT region, uuid FROM region_owners WHERE last_enter <= ?;"
				)){

			statement.setLong(1, inactive);

			ResultSet results = statement.executeQuery();
			HashMap<String, String> inactives = new HashMap<String, String>();

			while (results.next()) {
				inactives.put(results.getString("region"), results.getString("uuid"));
			}

			return inactives;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void removeInactiveOwners(long inactive) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"DELETE FROM region_owners WHERE last_enter <= ?;"
				)){

			statement.setLong(1, inactive);
			statement.executeUpdate();
			//Bukkit.broadcastMessage("Removed Owners");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean isOwner(String uuid, String region) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT uuid FROM region_owners WHERE region = ? AND uuid = ?;"
				)){

			statement.setString(1, region);
			statement.setString(2, uuid);

			ResultSet results = statement.executeQuery();

			return (results.next());

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean hasOwner(String region) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT uuid FROM region_owners WHERE region = ?;"
				)){

			statement.setString(1, region);

			ResultSet results = statement.executeQuery();

			return (results.next());

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}	

	}

	public int count(String uuid) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT COUNT(region) FROM region_owners WHERE uuid = ?;"
				)){

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

	public void addOwner(String region, String uuid) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"INSERT INTO region_owners(region, uuid, last_enter) VALUES (?, ?, ?);"
				)){

			statement.setString(1, region);
			statement.setString(2, uuid);
			statement.setLong(3, Time.currentTime());

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public String getOwner(String region) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT uuid FROM region_owners WHERE region = ?;"
				)){

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

	public void updateTime(String uuid, String region) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"UPDATE region_owners SET last_enter = ? WHERE region = ? AND uuid = ?;"
				)){

			statement.setLong(1, Time.currentTime());
			statement.setString(2, region);
			statement.setString(3, uuid);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}	

	}

	public void addNewOwners(HashMap<String, String> regions) {

		String uuid;
		MemberData memberData = Main.getInstance().memberData;
		RegionLogs regionLogs = Main.getInstance().regionLogs;
		RequestData requestData = Main.getInstance().requestData;

		for (String s : regions.keySet()) {

			if (memberData.hasMember(s)) {

				uuid = memberData.latestMember(s);

				addOwner(s, uuid);
				memberData.removeMember(s, uuid);
				regionLogs.closeLog(s, uuid);
				regionLogs.newLog(s, uuid, "owner");
				requestData.updateRegionOwner(s, uuid);

			}

		}


	}

	public void addNewOwner(String region) {

		MemberData memberData = Main.getInstance().memberData;
		RegionLogs regionLogs = Main.getInstance().regionLogs;
		RequestData requestData = Main.getInstance().requestData;
		if (memberData.hasMember(region)) {

			String uuid = memberData.latestMember(region);

			addOwner(region, uuid);
			memberData.removeMember(region, uuid);
			regionLogs.closeLog(region, uuid);
			regionLogs.newLog(region, uuid, "owner");
			requestData.updateRegionOwner(region, uuid);

		}

	}

	public ArrayList<String> getRegions(String uuid) {

		ArrayList<String> regions = new ArrayList<String>();
		
		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT region FROM region_owners WHERE uuid = ?;"
				)){

			statement.setString(1, uuid);

			ResultSet results = statement.executeQuery();
			while (results.next()) {
				regions.add(results.getString("region"));
			}

			return regions;

		} catch (SQLException e) {
			e.printStackTrace();
			return regions;
		}

	}

	public void removeOwner(String uuid, String region) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"DELETE FROM region_owners WHERE region = ? AND uuid = ?;"
				)){

			statement.setString(1, region);
			statement.setString(2, uuid);
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}


	}
}
