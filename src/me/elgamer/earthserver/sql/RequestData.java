package me.elgamer.earthserver.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.bukkit.Location;

import me.elgamer.earthserver.Main;

public class RequestData {

	DataSource dataSource;

	public RequestData(DataSource dataSource) {

		this.dataSource = dataSource;

	}

	private Connection conn() throws SQLException {
		return dataSource.getConnection();
	}

	public boolean insert(int plot, String uuid, String reviewer, int feedback, int size, int accuracy, int quality, int points) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"INSERT INTO accept_data(plot, uuid, reviewer, feedback, size, accuracy, quality, points, time) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);"
				)){
			statement.setInt(1, plot);

			statement.executeUpdate();

			return true;

		} catch (SQLException sql) {
			sql.printStackTrace();
			return false;
		}

	}

	//Counts all the requests with a specific owner
	public int count(String uuid) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT count(id) FROM requests WHERE owner = ? AND owner_ac = ?;"
				)){

			statement.setString(1, uuid);
			statement.setBoolean(2, false);

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

	//Counts all the requests that need to be accepted by staff
	public int count() {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT count(id) FROM requests WHERE staff_ac = ?;"
				)){

			statement.setBoolean(1, false);

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

	public void newRequest(String region, String uuid, boolean staff_accept, boolean owner_accept, Location l) {

		OwnerData ownerData = Main.getInstance().ownerData;

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"INSERT INTO requests(region, owner, uuid, staff_ac, owner_ac, x, y, z) VALUES(?, ?, ?, ?, ?, ?, ?, ?);"
				)){

			statement.setString(1, region);
			statement.setString(3, uuid);

			statement.setString(2, ownerData.getOwner(region));

			statement.setBoolean(4, staff_accept);
			statement.setBoolean(5, owner_accept);

			statement.setDouble(6, l.getX());
			statement.setDouble(7, l.getY());
			statement.setDouble(8, l.getZ());

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	//Get all request for a specific owner
	public ArrayList<String> getRequests(String uuid) {

		PlayerData playerData = Main.getInstance().playerData;
		ArrayList<String> requests = new ArrayList<String>();

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT region, uuid FROM requests WHERE owner = ? AND owner_ac = ?;"
				)){

			statement.setString(1, uuid);
			statement.setBoolean(2, false);

			ResultSet results = statement.executeQuery();

			while (results.next()) {
				requests.add(playerData.getName(results.getString("uuid")) + ", " + results.getString("region"));
			}

			return requests;

		} catch (SQLException e) {
			e.printStackTrace();
			return requests;
		}

	}

	//Get all staff requests by jr builders
	public ArrayList<String> getRequests() {

		PlayerData playerData = Main.getInstance().playerData;
		ArrayList<String> requests = new ArrayList<String>();

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT region, uuid FROM requests WHERE staff_ac = ?;"
				)){

			statement.setBoolean(1, false);

			ResultSet results = statement.executeQuery();

			while (results.next()) {
				requests.add(playerData.getName(results.getString("uuid")) + ", " + results.getString("region"));
			}

			return requests;

		} catch (SQLException e) {
			e.printStackTrace();
			return requests;
		}

	}

	public Location getRequestLocation(String region, String requester) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT x, y, z FROM requests WHERE region = ? AND uuid = ?;"
				)){

			statement.setString(1, region);
			statement.setString(2, requester);

			ResultSet results = statement.executeQuery();

			if (results.next()) {
				return new Location(Main.buildWorld, results.getDouble("x"), results.getDouble("y"), results.getDouble("z"));
			} else {
				return null;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}


	}

	public void setOwnerAccept(String region, String requester, boolean value) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"UPDATE requests SET owner_ac = ? WHERE region = ? AND uuid = ?;"
				)){

			statement.setBoolean(1, value);
			statement.setString(2, region);
			statement.setString(3, requester);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void setStaffAccept(String region, String requester, boolean value) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"UPDATE requests SET staff_ac = ? WHERE region = ? AND uuid = ?;"
				)){

			statement.setBoolean(1, value);
			statement.setString(2, region);
			statement.setString(3, requester);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	//Checks whether the region is already accepted by staff
	public boolean staffAccept(String region, String requester) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT staff_ac FROM requests WHERE region = ? AND uuid = ?;"
				)){

			statement.setString(1, region);
			statement.setString(2, requester);

			ResultSet results = statement.executeQuery();

			if (results.next()) {
				return results.getBoolean("staff_ac");
			} else {
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}		

	}

	//Checks whether the region is already accepted by the owner
	public boolean ownerAccept(String region, String requester) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT owner_ac FROM requests WHERE region = ? AND uuid = ?;"
				)){

			statement.setString(1, region);
			statement.setString(2, requester);

			ResultSet results = statement.executeQuery();

			if (results.next()) {
				return results.getBoolean("owner_ac");
			} else {
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}		

	}

	public void closeRequest(String region, String requester) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"DELETE FROM requests WHERE region = ? AND uuid = ?;"
				)){

			statement.setString(1, region);
			statement.setString(2, requester);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	//Checks whether the request still exists for the owner
	public boolean requestExists(String region, String requester) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT id FROM requests WHERE region = ? AND uuid = ? AND owner_ac = ?;"
				)){

			statement.setString(1, region);
			statement.setString(2, requester);
			statement.setBoolean(3, false);

			ResultSet results = statement.executeQuery();

			return results.next();

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}		

	}

	//Checks whether the request still exists for staff
	public boolean requestExists(String region, String requester, boolean staff) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT id FROM requests WHERE region = ? AND uuid = ? AND staff_ac = ?;"
				)){

			statement.setString(1, region);
			statement.setString(2, requester);
			statement.setBoolean(3, false);

			ResultSet results = statement.executeQuery();

			return results.next();

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}		

	}

	public void updateRegionOwner(String region, String newOwner) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"UPDATE requests SET owner = ? WHERE region = ?;"
				)){

			statement.setString(1, newOwner);
			statement.setString(2, region);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	//Counts all requests by a specific player.
	public int countRequests(String uuid) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT count(id) FROM requests WHERE uuid = ?;"
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

	public boolean hasRequest(String uuid) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT id FROM requests WHERE uuid = ?;"
				)){

			statement.setString(1, uuid);

			ResultSet results = statement.executeQuery();

			return (results.next());

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	//Get all request for a specific owner
	public ArrayList<String> getYourRequests(String uuid) {

		ArrayList<String> requests = new ArrayList<String>();
		
		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT region FROM requests WHERE uuid = ?;"
				)){

			statement.setString(1, uuid);

			ResultSet results = statement.executeQuery();
			
			while (results.next()) {
				requests.add(results.getString("region"));
			}
			
			return requests;

		} catch (SQLException e) {
			e.printStackTrace();
			return requests;
		}

	}

	//Checks whether the request still exists
	public boolean hasRequested(String region, String requester) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT id FROM requests WHERE region = ? AND uuid = ?;"
				)){

			statement.setString(1, region);
			statement.setString(2, requester);

			ResultSet results = statement.executeQuery();

			return results.next();

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}		

	}

	public void closeRequests(String region) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"DELETE FROM requests WHERE region = ?;"
				)){

			statement.setString(1, region);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	//Check whether you have any requests for claims you own
	public boolean hasRequestOwner(String uuid) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT id FROM requests WHERE owner = ?;"
				)){

			statement.setString(1, uuid);

			ResultSet results = statement.executeQuery();

			return results.next();

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}	

	}

	//Check whether you have any requests for claims you own
	public boolean hasRequestStaff() {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT id FROM requests WHERE staff_ac = ?;"
				)){

			statement.setBoolean(1, false);

			ResultSet results = statement.executeQuery();

			return results.next();

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}	

	}

}
