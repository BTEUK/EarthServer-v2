package me.elgamer.earthserver.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.utils.OldClaim;

public class OldClaimSQL {
	
	public static ArrayList<OldClaim> getAllClaims() {
		
		Main instance = Main.getInstance();
		
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.claimData);

			ResultSet results = statement.executeQuery();
			ArrayList<OldClaim> claims = new ArrayList<OldClaim>();
			List<String> members = new ArrayList<String>();
			boolean b;

			while (results.next()) {
				
				if (results.getString("IS_PUBLIC").equals("true")) {
					b = true;
				} else {
					b = false;
				}
				
				if (results.getString("MEMBERS") == null) {
					members = null;
				} else {
					members = Arrays.asList(results.getString("MEMBERS").split(","));
				}
				
				
				claims.add(new OldClaim(results.getString("REGION_ID"), results.getString("REGION_OWNER"), members, b));
			}

			return claims;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}	
		
		
	}

}
