package me.elgamer.earthserver.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.sql.MemberData;
import me.elgamer.earthserver.sql.OwnerData;
import me.elgamer.earthserver.sql.PlayerData;
import me.elgamer.earthserver.utils.Permissions;
import me.elgamer.earthserver.utils.User;

public class LeaveEvent implements Listener {
	
	public LeaveEvent(Main plugin) {

		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		
		User u = Main.getUser(e.getPlayer());
		Permissions.removeWorldedit(u.uuid);
		
		PlayerData.updatePlayer(u);		
		
		if (OwnerData.isOwner(u.uuid, u.current_region)) {
			OwnerData.updateTime(u.uuid, u.current_region);
		} else if (MemberData.isMember(u.uuid, u.current_region)) {
			MemberData.updateTime(u.uuid, u.current_region);
		}
		
	}

}
