package me.elgamer.earthserver.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.utils.User;
import net.md_5.bungee.api.ChatColor;

public class CommandListener {
	
	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {

		Player p = e.getPlayer();
		User u = Main.getUser(p);
		
		if (e.getMessage().startsWith("//schbr")) {
			if (!u.hasWorldEdit) {
				u.p.sendMessage(ChatColor.RED + "You may only use this in regions you can build in.");
				e.setCancelled(true);
			}
		}
		
	}
}
