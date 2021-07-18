package me.elgamer.earthserver.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.sql.RegionData;
import me.elgamer.earthserver.utils.Permissions;
import me.elgamer.earthserver.utils.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class TeleportEvent implements Listener {
	
	public TeleportEvent(Main plugin) {

		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {

		Player p = e.getPlayer();
		User u = Main.getUser(p);
		Location l = e.getTo();
		
		if (!(l.getWorld().equals(u.current_world))) {
			u.current_world = l.getWorld();
			if (!(u.current_world.getName().equals(Main.getInstance().getConfig().getString("World_Name")))) {
				Permissions.removeWorldedit(u.uuid);
				u.hasWorldEdit = false;
				if (!(u.current_region.equals("buildhub"))) {
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "You have entered buildhub and left " + u.current_region));
					u.current_region = "buildhub";
				}
				return;
			}
		}

		if (!(u.current_world.getName().equals(Main.getInstance().getConfig().getString("World_Name")))) {
			return;
		}

		if (!(u.current_region.equals(User.getRegion(l)))) {

			if (u.builder_role.equals("builder") || u.builder_role.equals("jrbuilder")) {
				u.hasWorldEdit = User.updatePerms(u, User.getRegion(l));	
			}
			
			if (u.builder_role.equals("apprentice") && u.builder_role.equals("guest")) {
				if (!(RegionData.regionExists(User.getRegion(l)))) {
					e.setCancelled(true);
					return;
				}
			}
			
			p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "You have entered " + User.getRegion(l) + " and left " + u.current_region));
			u.current_region = User.getRegion(p);

		}

	}

}
