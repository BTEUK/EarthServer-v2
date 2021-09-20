package me.elgamer.earthserver.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import me.elgamer.earthserver.utils.WorldGuardFunctions;

public class RegionData {

	DataSource dataSource;

	public RegionData(DataSource dataSource) {

		this.dataSource = dataSource;

	}

	private Connection conn() throws SQLException {
		return dataSource.getConnection();
	}
	
	public boolean hasEntry() {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT region FROM regions;"
				)){

			ResultSet results = statement.executeQuery();

			return (results.next());

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}			
	}

	public boolean isOpen(String region) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT region FROM regions WHERE region = ? AND open = ?;"
				)){
			
			statement.setString(1, region);
			statement.setBoolean(2, true);

			ResultSet results = statement.executeQuery();

			return (results.next());

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}	

	}

	public boolean isPublic(String region) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT region FROM regions WHERE region = ? AND public = ?;"
				)){
			
			statement.setString(1, region);
			statement.setBoolean(2, true);

			ResultSet results = statement.executeQuery();

			return (results.next());

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}	

	}

	public boolean isLocked(String region) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT region FROM regions WHERE region = ? AND locked = ?;"
				)){

			statement.setString(1, region);
			statement.setBoolean(2, true);

			ResultSet results = statement.executeQuery();

			return (results.next());

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}	

	}

	public void setOpen(String region) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"UPDATE regions SET open = ? WHERE region = ?;"
				)){
			
			statement.setBoolean(1, true);

			statement.setString(2, region);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}	

	}

	public void setClosed(String region) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"UPDATE regions SET open = ? WHERE region = ?;"
				)){

			statement.setBoolean(1, false);

			statement.setString(2, region);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}	

	}

	public void setLocked(String region) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"UPDATE regions SET locked = ? WHERE region = ?;"
				)){

			statement.setBoolean(1, true);

			statement.setString(2, region);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}	

	}

	public void setUnlocked(String region) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"UPDATE regions SET locked = ? WHERE region = ?;"
				)){

			statement.setBoolean(1, false);

			statement.setString(2, region);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}	

	}

	public void setPrivate(String region) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"UPDATE regions SET public = ? WHERE region = ?;"
				)){

			statement.setBoolean(1, false);

			statement.setString(2, region);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}	

	}

	public void setPublic(String region) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"UPDATE regions SET public = ? WHERE region = ?;"
				)){

			statement.setBoolean(1, true);

			statement.setString(2, region);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}	

	}

	public void createRegionIfNotExists(String region) {

		if (regionExists(region)) {
			return;
		}
		
		if (region.equalsIgnoreCase("BuildHub")) {
			return;
		}

		String[] xz = region.split(",");

		int x = Integer.parseInt(xz[0]);
		int z = Integer.parseInt(xz[1]);
		
		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"INSERT INTO regions(region, region_x, region_z, public, locked, open) VALUES(?, ?, ?, ?, ?, ?);"
				)){
			
			statement.setString(1, region);

			statement.setInt(2, x);
			statement.setInt(3, z);

			statement.setBoolean(4, false);
			statement.setBoolean(5, false);
			statement.setBoolean(6, false);

			statement.executeUpdate();
			
			WorldGuardFunctions.createRegion(region, x, z);

		} catch (SQLException e) {
			e.printStackTrace();
		}	

	}
	
	public boolean regionExists(String region) {
		
		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT region FROM regions WHERE region = ?;"
				)){

			statement.setString(1, region);

			ResultSet results = statement.executeQuery();

			return (results.next());

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
			
		}
	}

}
