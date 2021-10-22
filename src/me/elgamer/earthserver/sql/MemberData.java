package me.elgamer.earthserver.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.sql.DataSource;

import me.elgamer.earthserver.utils.Time;

public class MemberData {

	DataSource dataSource;

	public MemberData(DataSource dataSource) {

		this.dataSource = dataSource;

	}

	private Connection conn() throws SQLException {
		return dataSource.getConnection();
	}
	
	public boolean addMembers(HashMap<String, String> members) {

		long currentTime = Time.currentTime();
		int i = 0;
		
		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"INSERT INTO region_members(region, uuid, last_enter) VALUES(?, ?, ?);"
				)){

			for (Entry<String, String> e : members.entrySet()) {

				statement.setString(1, e.getKey());
				statement.setString(2, e.getValue());
				statement.setLong(3, currentTime);

				statement.addBatch();
				i++;
				
				if (i % 1000 == 0 || i == members.size()) {
					statement.executeBatch();
				}				
			}

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	//Check is player has instance in table
	public HashMap<String, String> getInactiveMembers(long inactive) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT region, uuid FROM region_members WHERE last_enter <= ?;"
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

	public void removeInactiveMembers(long inactive) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"DELETE FROM region_members WHERE last_enter <= ?;"
				)){

			statement.setLong(1, inactive);
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}


	}

	public boolean isMember(String uuid, String region) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT uuid FROM region_members WHERE region = ? AND uuid = ?;"
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

	public int count(String uuid) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT COUNT(uuid) FROM region_members WHERE uuid = ?;"
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

	public void addMember(String region, String uuid) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"INSERT INTO region_members(region, uuid, last_enter) VALUES (?, ?, ?);"
				)){

			statement.setString(1, region);
			statement.setString(2, uuid);
			statement.setLong(3, Time.currentTime());

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void updateTime(String uuid, String region) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"UPDATE region_members SET last_enter = ? WHERE region = ? AND uuid = ?;"
				)){

			statement.setLong(1, Time.currentTime());
			statement.setString(2, region);
			statement.setString(3, uuid);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}	

	}

	public boolean hasMember(String region) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT uuid FROM region_members WHERE region = ?;"
				)){

			statement.setString(1, region);

			ResultSet results = statement.executeQuery();

			return (results.next());

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public long lastEnter(String region, String uuid) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT last_enter FROM region_members WHERE region = ? AND uuid = ?;"
				)){

			statement.setString(1, region);
			statement.setString(2, uuid);

			ResultSet results = statement.executeQuery();
			results.next();
			
			return (results.getLong("last_enter"));

		} catch (SQLException e) {
			e.printStackTrace();
			return 1;
		}
	}

	public String latestMember(String region) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT uuid FROM region_members WHERE region = ? ORDER BY last_enter DESC;"
				)){

			statement.setString(1, region);

			ResultSet results = statement.executeQuery();
			results.next();

			return (results.getString("uuid"));

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	public void removeMember(String region, String uuid) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"DELETE FROM region_members WHERE region = ? AND uuid = ?;"
				)){
			//Delete all inactive members from member table
			statement.setString(1, region);
			statement.setString(2, uuid);
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public int countMembers(String region) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT COUNT(uuid) FROM region_members WHERE region = ?;"
				)){
			
			statement.setString(1, region);

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

	public ArrayList<String> getMembers(String region) {

		ArrayList<String> members = new ArrayList<String>();
		
		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT uuid FROM region_members WHERE region = ?;"
				)){

			statement.setString(1, region);

			ResultSet results = statement.executeQuery();
			
			while (results.next()) {
				members.add(results.getString("uuid"));
			}

			return members;

		} catch (SQLException e) {
			e.printStackTrace();
			return members;
		}	

	}

	public ArrayList<String> getRegions(String uuid) {

		ArrayList<String> regions = new ArrayList<String>();
		
		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT region FROM region_members WHERE uuid = ?;"
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
}
