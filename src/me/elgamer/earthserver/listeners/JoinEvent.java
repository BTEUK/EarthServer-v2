package me.elgamer.earthserver.listeners;

import java.util.ArrayList;

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

	MessageData messageData;
	PlayerData playerData;
	RequestData requestData;

	public JoinEvent(Main plugin, MessageData messageData, PlayerData playerData, RequestData requestData) {

		this.messageData = messageData;
		this.playerData = playerData;
		this.requestData = requestData;
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {

		Player p = e.getPlayer();
		User u = Main.addUser(p);

		Bukkit.broadcastMessage(ChatColor.GREEN + "Added user " + u.name + " to the List");

		if (playerData.hasPlayer(u)) {
			playerData.updatePlayer(u);
		} else {
			playerData.addPlayer(u);
		}

		if (p.hasPermission("earthserver.location.add")) {

			LocationSQL locationSQL = Main.getInstance().locationData;

			if (locationSQL.requestExists()) {
				Bukkit.getScheduler().runTaskLater (Main.getInstance(), () -> p.sendMessage(ChatColor.GREEN + "There is a new location request, check with /requests"), 20); //20 ticks equal 1 second
			}

		}

		if (requestData.hasRequestOwner(u.uuid)) {
			Bukkit.getScheduler().runTaskLater (Main.getInstance(), () -> p.sendMessage(ChatColor.GREEN + "You have join requests for your regions, check the gui with /claim."), 20); //20 ticks equal 1 second
		}

		if (requestData.hasRequestStaff() && u.p.hasPermission("earthserver.admin.review")) {
			Bukkit.getScheduler().runTaskLater (Main.getInstance(), () -> p.sendMessage(ChatColor.GREEN + "There are region join requests by Jr.Builders, check the gui with /claim"), 20); //20 ticks equal 1 second		
		}

		if (messageData.hasMessage(u.uuid)) {

			Bukkit.getScheduler().runTaskLater (Main.getInstance(), () -> {
				ArrayList<String> messages = messageData.getMessages(u.uuid);
				messageData.removeMessages(u.uuid);

				for (String message : messages) {
					u.p.sendMessage(message);
				}
			}, 20);

		}

	}

}
