package me.elgamer.earthserver.utils;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

public class Permissions {
	
	public static void removePermissions(ArrayList<OldClaim> claims) {
		
		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		
		for (OldClaim claim : claims) {
			
			Bukkit.dispatchCommand(console, "lp user " + claim.owner + " permission unset worldedit.* wg-region=" + claim.region);
			
			if (claim.public_private) {
				Bukkit.dispatchCommand(console, "lp group builder permission unset worldedit.* wg-region=" + claim.region);
			}
			
			if (claim.members != null) {
				
				for (String member : claim.members) {
					
					Bukkit.dispatchCommand(console, "lp user " + member + " permission unset worldedit.* wg-region=" + claim.region);
					
				}
				
			}
			
		}
		
		
	}
	
	public static void removeWorldedit(String uuid) {
		
		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		Bukkit.dispatchCommand(console, "lp user " + uuid + " permission unset worldedit.* server=cubic");
		
	}
	
	public static void giveWorldedit(String uuid) {
		
		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		Bukkit.dispatchCommand(console, "lp user " + uuid + " permission set worldedit.* server=cubic");
		
	}

}
