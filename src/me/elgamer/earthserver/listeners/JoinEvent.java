package me.elgamer.earthserver.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.sql.LocationSQL;
import me.elgamer.earthserver.sql.PlayerData;
import me.elgamer.earthserver.utils.User;

public class JoinEvent implements Listener {

	public JoinEvent(Main plugin) {

		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {

		Player p = e.getPlayer();
		User u = Main.addUser(p);
		
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

	}

}
