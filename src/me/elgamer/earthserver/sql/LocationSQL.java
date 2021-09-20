package me.elgamer.earthserver.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.DataSource;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import me.elgamer.earthserver.Main;

public class LocationSQL {

	DataSource dataSource;

	public LocationSQL(DataSource dataSource) {

		this.dataSource = dataSource;

	}

	private Connection conn() throws SQLException {
		return dataSource.getConnection();
	}

	//Returns whether the location is already requested
	public boolean requestExists(String loc) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT location FROM location_request_data WHERE location = ?;"
				)){

			statement.setString(1, loc);
			ResultSet results = statement.executeQuery();

			if (results.next()) {
				return true;
			}

			return false;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

	//Returns true if there is a new location request
	public boolean requestExists() {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT location FROM location_request_data;"
				)){
			ResultSet results = statement.executeQuery();

			if (results.next()) {
				return true;
			}

			return false;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

	//Creates a location request
	public boolean addRequest(String loc, Location l) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"INSERT INTO location_request_data(location, x, y, z, pitch, yaw) VALUES(?, ?, ?, ?, ?, ?);"
				)){
			statement.setString(1, loc);
			statement.setDouble(2, l.getX());
			statement.setDouble(3, l.getY());
			statement.setDouble(4, l.getZ());
			statement.setFloat(5, l.getPitch());
			statement.setFloat(6, l.getYaw());

			statement.executeUpdate();

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}


	}

	//Creates a location from request
	public boolean addLocation(String loc, String cat, String subcat, Location l) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"INSERT INTO location_data(location, category, subcategory, x, y, z, pitch, yaw) VALUES (?, ?, ?, ?, ?, ?, ?, ?);"
				)){

			statement.setString(1, loc);
			statement.setString(2, cat);
			statement.setString(3, subcat);
			statement.setDouble(4, l.getX());
			statement.setDouble(5, l.getY());
			statement.setDouble(6, l.getZ());
			statement.setFloat(7, l.getPitch());
			statement.setFloat(8, l.getYaw());

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;


	}

	//Returns whether the location is already requested
	public boolean locationExists(String loc) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT location FROM location_data WHERE location = ?;"
				)){

			statement.setString(1, loc);
			ResultSet results = statement.executeQuery();

			if (results.next()) {
				return true;
			}

			return false;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

	//Return the location of a request and deletes it subsequently
	public Location getRequestLocation(String loc) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT * FROM location_request_data WHERE location = ?;"
				)){

			statement.setString(1, loc);
			ResultSet results = statement.executeQuery();
			results.next();

			Location l = new Location(Bukkit.getWorld("world"), results.getDouble("x"), results.getDouble("y"), results.getDouble("z"), results.getFloat("yaw"), results.getFloat("pitch"));
			removeRequest(loc);

			return (l);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	//Remove a location request
	public boolean removeRequest(String loc) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"DELETE FROM location_request_data WHERE location = ?;"
				)){

			statement.setString(1, loc);
			statement.executeUpdate();

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

	//Return the location of a request and deletes it subsequently
	public Location toRequestLocation(String loc) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT * FROM location_request_data WHERE location = ?;"
				)){

			statement.setString(1, loc);
			ResultSet results = statement.executeQuery();
			results.next();

			return (new Location(Bukkit.getWorld("world"), results.getDouble("x"), results.getDouble("y"), results.getDouble("z"), results.getFloat("yaw"), results.getFloat("pitch")));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	//Remove location
	public boolean removeLocation(String loc) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"DELETE FROM location_data WHERE location = ?;"
				)){

			statement.setString(1, loc);
			statement.executeUpdate();

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

	public HashMap<String, Location> getRequests(){

		HashMap<String, Location> requests = new HashMap<String, Location>();

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT * FROM location_request_data;"
				)){

			ResultSet results = statement.executeQuery();

			while (results.next()) {

				requests.put(results.getString("location"), new Location(Bukkit.getWorld("world"), results.getDouble("x"), results.getDouble("y"), results.getDouble("z"), results.getFloat("pitch"), results.getFloat("yaw")));

			}

			return requests;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int CategoryCount(String cat) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT COUNT(location) FROM location_data WHERE category = ?;"
				)){

			statement.setString(1, cat);
			ResultSet results = statement.executeQuery();

			results.next();

			return (results.getInt(1));

		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}

	}

	public ArrayList<String[]> getLocations(String cat){

		ArrayList<String[]> locations = new ArrayList<String[]>();
		
		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT location, subcategory FROM location_data WHERE category = ?;"
				)){

			statement.setString(1, cat);
			ResultSet results = statement.executeQuery();

			while (results.next()) {

				locations.add(new String[] {results.getString("location"), results.getString("subcategory")});

			}

			return locations;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	public Location getLocation(String name) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT * FROM location_data WHERE location = ?;"
				)){

			statement.setString(1, name);
			ResultSet results = statement.executeQuery();

			results.next();

			return (new Location(Bukkit.getWorld(Main.getInstance().getConfig().getString("World_Name")),
					results.getDouble("x"),
					results.getDouble("y"),
					results.getDouble("z"),
					results.getFloat("yaw"),
					results.getFloat("pitch")));

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}


	}

}
