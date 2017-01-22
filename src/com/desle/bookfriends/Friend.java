package com.desle.bookfriends;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Friend {

	public static Map<UUID, Friend> list = new HashMap<UUID, Friend>();
	
	public static Friend get(UUID uuid) {
		if (list.get(uuid) == null) {
			FriendLoader.getInstance().loadFriend(uuid);
		}
		return list.get(uuid);
	}
	
	private UUID uuid;
	private Map<UUID, Boolean> friends;
	private String name;
	
	public Friend(UUID uuid, Map<UUID, Boolean> friends, String name) {
		
		this.uuid = uuid;
		this.friends = friends;
		this.name = name;
		
		list.put(uuid, this);
	}
	
	public List<UUID> getOnlineFriends() {
		List<UUID> friends = new ArrayList<UUID>();
		
		for (UUID uuid : this.friends.keySet()) {			
			if (this.friends.get(uuid) == true && list.containsKey(uuid))
				friends.add(uuid);
		}
		
		return friends;
	}
	
	public List<UUID> getOfflineFriends() {
		List<UUID> friends = new ArrayList<UUID>();
		
		for (UUID uuid : this.friends.keySet()) {
			if (this.friends.get(uuid) == true && !list.containsKey(uuid))
				friends.add(uuid);
		}
		
		return friends;
	}
	
	public List<UUID> getPendingFriends() {
		List<UUID> friends = new ArrayList<UUID>();
		
		for (UUID uuid : this.friends.keySet()) {
			if (this.friends.get(uuid) == false)
				friends.add(uuid);
		}
		
		return friends;
	}
	
	public Map<UUID, Boolean> getFriends() {
		return this.friends;
	}
	
	public UUID getUniqueId() {
		return this.uuid;
	}
	
	public void destroy() {
		list.remove(this.uuid);
		
		this.friends = null;
	}
	
	public String getName() {
		return this.name;
	}
}
