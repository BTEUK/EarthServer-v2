package me.elgamer.earthserver.utils;

import org.bukkit.configuration.file.FileConfiguration;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.sql.MemberData;
import me.elgamer.earthserver.sql.OwnerData;
import me.elgamer.earthserver.sql.RequestData;

public class ClaimLimit {
	
	
	private static int limit(String role) {
		
		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();
		
		if (role.equals("jrbuilder")) {
			
			return config.getInt("Limit.JrBuilder");
			
		} else if (role.equals("builder")) {
			
			return config.getInt("Limit.Builder");
			
		} else {return 0;}
		
		
	}	
	
	public static boolean limitReached(User u) {
		
		OwnerData ownerData = Main.getInstance().ownerData;
		MemberData memberData = Main.getInstance().memberData;
		RequestData requestData = Main.getInstance().requestData;
		
		//Count all regions the player is in plus all active region requests.
		int regionNumber = ownerData.count(u.uuid) + memberData.count(u.uuid) + requestData.countRequests(u.uuid);
		
		if (regionNumber >= limit(u.builder_role)) {
			return true;
		} else {return false;}			
				
	}
	

}
