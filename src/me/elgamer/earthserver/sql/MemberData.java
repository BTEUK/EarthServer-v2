package me.elgamer.earthserver.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.utils.OldClaim;
import me.elgamer.earthserver.utils.Time;

public class MemberData {

	public static boolean addMembers(HashMap<String, String> members) {

		Main instance = Main.getInstance();
		long currentTime = Time.currentTime();

		PreparedStatement statement;

		try {
			for (Entry<String, String> e : members.entrySet()) {

				statement = instance.getConnection().prepareStatement
						("INSERT INTO " + instance.memberData + " (REGION_ID,UUID,LAST_ENTER) VALUE (?,?,?)");
				statement.setString(1, e.getKey());
				statement.setString(2, e.getValue());
				statement.setLong(3, currentTime);

				statement.executeUpdate();

			}

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	//Check is player has instance in table
	public static HashMap<String, String> getInactiveMembers(long inactive) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.memberData + " WHERE LAST_ENTER<=?");
			statement.setLong(1, inactive);

			ResultSet results = statement.executeQuery();
			HashMap<String, String> inactives = new HashMap<String, String>();

			while (results.next()) {
				inactives.put(results.getString("REGION_ID"), results.getString("UUID"));
			}

			return inactives;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static void removeInactiveMembers(long inactive) {

		Main instance = Main.getInstance();
		PreparedStatement statement;
		try {

			//Delete all inactive members from member table
			statement = instance.getConnection().prepareStatement
					("DELETE FROM " + instance.memberData + " WHERE LAST_ENTER<=?");
			statement.setLong(1, inactive);
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}


	}

	public static void convertMembers(ArrayList<OldClaim> claims) {

		Main instance = Main.getInstance();

		PreparedStatement statement;

		try {

			for (OldClaim claim : claims) {

				if (claim.members == null) {
					continue;
				}

				if (claim.public_private) {
					continue;
				}

				for (String member : claim.members) {

					if (OwnerData.isOwner(member, claim.region)) {
						continue;
					}
					
					statement = instance.getConnection().prepareStatement
							("INSERT INTO " + instance.memberData + " (REGION_ID,UUID,LAST_ENTER) VALUE (?,?,?)");
					statement.setString(1, claim.region);
					statement.setString(2, member);
					statement.setLong(3, Time.currentTime());

					statement.executeUpdate();

				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static boolean isMember(String uuid, String region) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.memberData + " WHERE REGION_ID=? AND UUID=?");
			statement.setString(1, region);
			statement.setString(2, uuid);

			ResultSet results = statement.executeQuery();

			return (results.next());

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static int count(String uuid) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT COUNT(*) FROM " + instance.memberData + " WHERE UUID=?");
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

	public static void addMember(String region, String uuid) {

		Main instance = Main.getInstance();

		PreparedStatement statement;
		try {
			statement = instance.getConnection().prepareStatement
					("INSERT INTO " + instance.memberData + " (REGION_ID,UUID,LAST_ENTER) VALUE (?,?,?)");
			statement.setString(1, region);
			statement.setString(2, uuid);
			statement.setLong(3, Time.currentTime());

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void updateTime(String uuid, String region) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("UPDATE " + instance.memberData + " SET LAST_ENTER=? WHERE REGION_ID=? AND UUID=?");
			statement.setLong(1, Time.currentTime());
			statement.setString(2, region);
			statement.setString(3, uuid);

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}	

	}

	public static boolean hasMember(String region) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.memberData + " WHERE REGION_ID=?");
			statement.setString(1, region);

			ResultSet results = statement.executeQuery();

			return (results.next());

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String latestMember(String region) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.memberData + " WHERE REGION_ID=? ORDER BY LAST_ENTER DESC");
			statement.setString(1, region);

			ResultSet results = statement.executeQuery();
			results.next();

			return (results.getString("UUID"));

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static void removeMember(String region, String uuid) {

		Main instance = Main.getInstance();
		PreparedStatement statement;
		try {

			//Delete all inactive members from member table
			statement = instance.getConnection().prepareStatement
					("DELETE FROM " + instance.memberData + " WHERE REGION_ID=? AND UUID=?");
			statement.setString(1, region);
			statement.setString(2, uuid);
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static int countMembers(String region) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT COUNT(*) FROM " + instance.memberData + " WHERE REGION_ID=?");
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

	public static ResultSet getMembers(String region) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.memberData + " WHERE REGION_ID=?");
			statement.setString(1, region);

			ResultSet results = statement.executeQuery();

			return results;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}	

	}

	public static ResultSet getRegions(String uuid) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.memberData + " WHERE UUID=?");
			statement.setString(1, uuid);

			ResultSet results = statement.executeQuery();

			return results;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}
	
	public static ResultSet getMembers() {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.memberData);

			ResultSet results = statement.executeQuery();

			return results;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

}
