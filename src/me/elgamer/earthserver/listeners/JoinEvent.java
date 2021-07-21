package me.elgamer.earthserver.listeners;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.sql.LocationSQL;
import me.elgamer.earthserver.sql.MessageData;
import me.elgamer.earthserver.sql.PlayerData;
import me.elgamer.earthserver.sql.RequestData;
import me.elgamer.earthserver.utils.User;

public class JoinEvent implements Listener {

	public JoinEvent(Main plugin) {

		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {

		Player p = e.getPlayer();
		User u = Main.addUser(p);
		
		Bukkit.broadcastMessage(ChatColor.GREEN + "Added user " + u.name + " to the List");

		if (PlayerData.hasPlayer(u)) {
			PlayerData.updatePlayer(u);
		} else {
			PlayerData.addPlayer(u);
		}
		
		if (p.hasPermission("earthserver.location.add")) {
			
			if (LocationSQL.requestExists()) {
				Bukkit.getScheduler().runTaskLater (Main.getInstance(), () -> p.sendMessage(ChatColor.GREEN + "There is a new location request, check with /requests"), 20); //20 ticks equal 1 second
			}
			
		}
		
		if (RequestData.hasRequestOwner(u.uuid)) {
			Bukkit.getScheduler().runTaskLater (Main.getInstance(), () -> p.sendMessage(ChatColor.GREEN + "You have join requests for your regions, check the gui with /claim."), 20); //20 ticks equal 1 second
		}
		
		if (RequestData.hasRequestStaff() && u.p.hasPermission("earthserver.admin.review")) {
			Bukkit.getScheduler().runTaskLater (Main.getInstance(), () -> p.sendMessage(ChatColor.GREEN + "There are region join requests by Jr.Builders, check the gui with /claim"), 20); //20 ticks equal 1 second		
		}
		
		if (MessageData.hasMessage(u.uuid)) {
			
			ResultSet results = MessageData.getMessages(u.uuid);
			MessageData.removeMessages(u.uuid);
			
			try {
				while (results.next()) {
					Bukkit.getScheduler().runTaskLater (Main.getInstance(), () -> {
						try {
							u.p.sendMessage(ChatColor.valueOf(results.getString("COLOUR"))  + results.getString("MESSAGE"));
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}, 20); //20 ticks equal 1 second
				}
			} catch (IllegalArgumentException | SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}

	}

}
