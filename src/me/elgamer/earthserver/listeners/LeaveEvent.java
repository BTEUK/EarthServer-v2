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
import net.md_5.bungee.api.ChatColor;

public class LeaveEvent implements Listener {
	
	MemberData memberData;
	OwnerData ownerData;
	PlayerData playerData;
	
	public LeaveEvent(Main plugin, MemberData memberData, OwnerData ownerData, PlayerData playerData) {

		this.memberData = memberData;
		this.ownerData = ownerData;
		this.playerData = playerData;
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		
		User u = Main.getUser(e.getPlayer());
		
		if (u.hasWorldEdit) {
			Permissions.removeWorldedit(u.uuid);
		}
		
		playerData.updatePlayer(u);		
		
		if (ownerData.isOwner(u.uuid, u.current_region)) {
			ownerData.updateTime(u.uuid, u.current_region);
		} else if (memberData.isMember(u.uuid, u.current_region)) {
			memberData.updateTime(u.uuid, u.current_region);
		}
		
		Bukkit.broadcastMessage(ChatColor.RED + "Removed user " + u.name + " from the List");
		Main.removeUser(u);
		
	}

}
