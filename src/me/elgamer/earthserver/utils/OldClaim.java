package me.elgamer.earthserver.utils;

import java.util.List;

public class OldClaim {
	
	public String region;
	public String owner;
	public List<String> members;
	public boolean public_private;
	
	
	public OldClaim(String region, String owner, List<String> members, boolean public_private) {
		
		this.region = region;
		this.owner = owner;
		this.members = members;
		this.public_private = public_private;
		
	}

}
