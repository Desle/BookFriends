package com.desle.bookfriends;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.server.v1_11_R1.IChatBaseComponent;
import net.minecraft.server.v1_11_R1.IChatBaseComponent.ChatSerializer;

public class FriendManager {
	
	private static FriendManager instance;
	public static FriendManager getInstance() {
		if (instance == null)
			instance = new FriendManager();
		
		return instance;
	}
	
	public enum friendStatus {
		ONLINE("§2  ➥" + " Online", "a"),
		PENDING("§7  ➥" + " Request pending", "b"),
		OFFLINE("§4  ➥" + " Offline", "c");
		public String display;
		public String sort;
		
		friendStatus(String display, String sort) {
			this.display = display;
			this.sort = sort;
		}
	}
	
	public String getFriendStringComponent(UUID uuid, friendStatus friendstatus) {
		
		boolean shouldremove = true;
		
		if (Friend.list.containsKey(uuid))
			shouldremove = false;
		
		String name = Friend.get(uuid).getName();
		
		String line1 = "  " + "§8" + name;
		String line2 = friendstatus.display;
		String hover = "Click to show options";
		//String command = "/gamemode 1";

		if (shouldremove)
			Friend.get(uuid).destroy();
		
		return "{"
				+ "\"text\" : \"" + line1 + "\n" + line2 + "\n\n" + "\","
				+ "\"hoverEvent\" : "
				+ "{"
				+ "\"action\" : \"show_text\","
				+ "\"value\" : \"" + hover + "\""
				+ "}"
				+ "},";
	}
	
	public List<IChatBaseComponent> getFriendPages(UUID playeruuid) {
		List<IChatBaseComponent> pages = new ArrayList<IChatBaseComponent>();
		
		Friend friend = Friend.get(playeruuid);
		int count = 0;
		String page = "{" 
				+ "\"text\" : \"§n          Friends         \n\n\","
				+ "\"extra\" : "
				+ "[";
		
		Map<UUID, friendStatus> friends = new HashMap<UUID, friendStatus>();

		for (UUID uuid : friend.getOnlineFriends()) {
			friends.put(uuid, friendStatus.ONLINE);
		}
		
		for (UUID uuid : friend.getPendingFriends()) {
			friends.put(uuid, friendStatus.PENDING);
		}
		
		for (UUID uuid : friend.getOfflineFriends()) {
			friends.put(uuid, friendStatus.OFFLINE);
		}
		
		if (friends.isEmpty()) {
			page += "{"
					+ "\"text\" : \"You currently have no friends.\nUse /friends add <name>\nto add someone.\""
					+ "}";
			pages.add(ChatSerializer.a(page + "]"+ "}"));
			
			return pages;
		}
		
		List<UUID> sortedfriends = new ArrayList<UUID>(friends.keySet());
		
		Collections.sort(sortedfriends, new Comparator<UUID>() {

			@Override
			public int compare(UUID o1, UUID o2) {
				return friends.get(o1).sort.compareTo(friends.get(o2).sort);
			}
			
		});
		
		for (UUID uuid : sortedfriends) {
			String friendcomponent = getFriendStringComponent(uuid, friends.get(uuid));
			
			if (count == 4) {
				
				page = page.substring(0, page.length()-1);
				pages.add(ChatSerializer.a(page + "]"+ "}"));
				count = 0;
				page = "{" 
						+ "\"text\" : \"§n          Friends         \n\n\","
						+ "\"extra\" : "
						+ "[";
			}

			page += friendcomponent;
			count++;
		}

		page = page.substring(0, page.length()-1);
		pages.add(ChatSerializer.a(page + "]"+ "}"));
		
		return pages;
	}
	
	
	public boolean addFriend(UUID uuidplayer, UUID uuidfriend) {
		Friend playerfriend = Friend.get(uuidplayer);
		
		if (playerfriend.getFriends().containsKey(uuidfriend))
			return false;
		
		boolean shoulddelete = true;
		
		if (Friend.list.containsKey(uuidfriend))
			shoulddelete = false;
		
		Friend otherfriend = Friend.get(uuidfriend);
		
		if (otherfriend.getPendingFriends().contains(uuidplayer)) {
			otherfriend.getFriends().put(uuidplayer, true);
			playerfriend.getFriends().put(uuidfriend, true);
		} else {
			playerfriend.getFriends().put(uuidfriend, false);
		}
		
		if (shoulddelete)
			otherfriend.destroy();
		
		return true;
	}
	
	public boolean removeFriend(UUID uuidplayer, UUID uuidfriend) {
		Friend playerfriend = Friend.get(uuidplayer);
		
		if (!playerfriend.getFriends().containsKey(uuidfriend))
			return false;
		
		boolean shoulddelete = true;
		
		if (Friend.list.containsKey(uuidfriend))
			shoulddelete = false;
		
		Friend otherfriend = Friend.get(uuidfriend);
		
		if (otherfriend.getFriends().keySet().contains(uuidplayer))
			otherfriend.getFriends().remove(uuidplayer);
		
		playerfriend.getFriends().remove(uuidfriend);
		
		if (shoulddelete)
			otherfriend.destroy();
		
		return true;
	}
}
