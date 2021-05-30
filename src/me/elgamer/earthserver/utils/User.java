package me.elgamer.earthserver.utils;

import org.bukkit.entity.Player;

public class User {
	
	public Player p;
	public String uuid;
	public String name;
	public String builder_role;
	
	public User(Player p) {
		this.p = p;
		uuid = p.getUniqueId().toString();
		name = p.getName();
		
		if (p.hasPermission("group.builder")) {
			builder_role = "builder";
		} else if (p.hasPermission("group.jrbuilder")) {
			builder_role = "jrbuilder";
		} else if (p.hasPermission("group.apprentice")) {
			builder_role = "apprentice";
		} else {
			builder_role = "guest";
		}
		
	}

}
