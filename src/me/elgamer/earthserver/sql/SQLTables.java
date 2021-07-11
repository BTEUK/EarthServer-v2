package me.elgamer.earthserver.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import me.elgamer.earthserver.Main;

public class SQLTables {



	//Tables for the navigation menu
	public static void location(Main instance, String table) {

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("CREATE TABLE IF NOT EXISTS " + table
							+ " (LOCATION VARCHAR(128) NOT NULL , CATEGORY VARCHAR(128) NOT NULL , SUBCATEGORY VARCHAR(128) NOT NULL , X DOUBLE NOT NULL , Y DOUBLE NOT NULL , Z DOUBLE NOT NULL , PITCH FLOAT NOT NULL , YAW FLOAT NOT NULL , UNIQUE (LOCATION))");
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void locationRequest(Main instance, String table) {

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("CREATE TABLE IF NOT EXISTS " + table
							+ " (LOCATION VARCHAR(128) NOT NULL , X DOUBLE NOT NULL , Y DOUBLE NOT NULL , Z DOUBLE NOT NULL , PITCH FLOAT NOT NULL , YAW FLOAT NOT NULL , UNIQUE (LOCATION))");
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//Tables for claiming
	public static void region(Main instance, String table) {

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("CREATE TABLE IF NOT EXISTS " + table
							+ " (REGION_ID VARCHAR(15) NOT NULL , REGION_X INT NOT NULL , REGION_Z INT NOT NULL , PUBLIC TINYINT(1) NOT NULL , LOCKED TINYINT(1) NOT NULL , OPEN TINYINT(1) NOT NULL , UNIQUE (REGION_ID))");
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void owner(Main instance, String table) {

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("CREATE TABLE IF NOT EXISTS " + table
							+ " (REGION_ID VARCHAR(15) NOT NULL , UUID VARCHAR(36) NOT NULL , LAST_ENTER BIGINT NOT NULL , UNIQUE (REGION_ID,UUID))");
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void member(Main instance, String table) {

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("CREATE TABLE IF NOT EXISTS " + table
							+ " (REGION_ID VARCHAR(15) NOT NULL , UUID VARCHAR(36) NOT NULL , LAST_ENTER BIGINT NOT NULL , UNIQUE (REGION_ID,UUID))");
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void player(Main instance, String table) {

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("CREATE TABLE IF NOT EXISTS " + table
							+ " (UUID VARCHAR(36) NOT NULL , NAME VARCHAR(17) NOT NULL , BUILDER_ROLE VARCHAR(32) NOT NULL , LAST_ONLINE BIGINT NOT NULL , BUILDING_TIME INT NOT NULL , UNIQUE (UUID))");
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void request(Main instance, String table) {

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("CREATE TABLE IF NOT EXISTS " + table
							+ " (ID INT NOT NULL , REGION_ID VARCHAR(15) NOT NULL , OWNER VARCHAR(36) NOT NULL , UUID VARCHAR(36) NOT NULL , STAFF_ACCEPT TINYINT(1) NOT NULL , OWNER_ACCEPT TINYINT(1) NOT NULL , X DOUBLE NOT NULL , Y DOUBLE NOT NULL , Z DOUBLE NOT NULL , UNIQUE (ID))");
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void messages(Main instance, String table) {

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("CREATE TABLE IF NOT EXISTS " + table
							+ " (ID INT NOT NULL , UUID VARCHAR(36) NOT NULL , MESSAGE TEXT NOT NULL , COLOUR TEXT NOT NULL , UNIQUE (ID))");
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void logs(Main instance, String table) {
		
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("CREATE TABLE IF NOT EXISTS " + table
							+ " (ID INT NOT NULL , REGION_ID VARCHAR(15) NOT NULL , UUID VARCHAR(36) NOT NULL , ROLE VARCHAR(16) NOT NULL , START_TIME BIGINT NOT NULL , END_TIME BIGINT NOT NULL , UNIQUE (ID))");
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
