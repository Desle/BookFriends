package com.desle.bookfriends;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FriendLoader {
	
	private static FriendLoader instance;
	public static FriendLoader getInstance() {
		if (instance == null)
			instance = new FriendLoader();
		
		return instance;
	}
	
	
	public void loadFriend(UUID puuid) {
		FileConfiguration f = getFriendsFile();
		
		String name = f.getString("friends." + puuid.toString() + ".name");
		
		Map<UUID, Boolean> friends = new HashMap<UUID, Boolean>();
		
		for (String rawfriend : f.getStringList("friends." + puuid.toString() + ".friends")) {
			String[] splitfriend = rawfriend.split(":");
			
			UUID uuid = UUID.fromString(splitfriend[0]);
			Boolean friended = Boolean.parseBoolean(splitfriend[1]);
			
			friends.put(uuid, friended);
		}
		
		if (name == null) {
			if (Bukkit.getPlayer(puuid) != null) {
				name = Bukkit.getPlayer(puuid).getName();
			} else {
				name = Bukkit.getOfflinePlayer(puuid).getName();
			}			
		}
			
			
		new Friend(puuid, friends, name);
	}
	
	public void saveFriend(Friend friend) {
		FileConfiguration f = getFriendsFile();
		
		List<String> friends = new ArrayList<String>();
		
		for (UUID uuid : friend.getFriends().keySet()) {
			boolean friended = friend.getFriends().get(uuid);
			String frienduid = uuid.toString();
			
			friends.add(frienduid + ":" + friended);
		}

		f.set("friends." + friend.getUniqueId() + ".name", friend.getName());
		f.set("friends." + friend.getUniqueId() + ".friends", friends);
		
		saveFriendsFile();
	}
	
	public void saveFriends() {
		for (Friend friend: Friend.list.values()) {
			saveFriend(friend);
		}
	}
	
	
	
	/*
	 * 
	 * 
	 *  Friends configuration file
	 * 
	 * 
	 * 
	 * */
	
	
	
	  private FileConfiguration friendsFileConfiguration = null;
	  private File friendsFile = null;
	
	
	  public void reloadFriendsFile()
	  {
	    if (this.friendsFile == null) {
	      this.friendsFile = new File(Main.getMain().getDataFolder(), "friends.yml");
	    }
	    this.friendsFileConfiguration = YamlConfiguration.loadConfiguration(this.friendsFile);

	    InputStream defConfigStream = Main.getMain().getResource("friends.yml");
	    if (defConfigStream != null) {
	      @SuppressWarnings("deprecation")
		YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	      this.friendsFileConfiguration.setDefaults(defConfig);
	    }
	  }
	  
	  

	  public FileConfiguration getFriendsFile() {
	    if (this.friendsFileConfiguration == null) {
	    	reloadFriendsFile();
	    }
	    return this.friendsFileConfiguration;
	  }

	  
	  
	  public void saveFriendsFile() {
	    if ((this.friendsFileConfiguration == null) || (this.friendsFile == null))
	      return;
	    try
	    {
	    	getFriendsFile().save(this.friendsFile);
	    } catch (IOException ex) {
	    	Bukkit.getLogger().log(Level.SEVERE, "Could not save config to " + this.friendsFile, ex);
	    }
	  }

	  
	  
	  public void saveDefaultFriendsFile() {
	    if (this.friendsFile == null) {
	      this.friendsFile = new File(Main.getMain().getDataFolder(), "friends.yml");
	    }
	    if (!this.friendsFile.exists())
	    	Main.getMain().saveResource("friends.yml", false);
	  }
}
