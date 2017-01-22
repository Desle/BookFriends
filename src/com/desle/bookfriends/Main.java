package com.desle.bookfriends;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.desle.bookmanager.BookManager;

public class Main extends JavaPlugin implements Listener {
	
	public static Main getMain() {
		return (Main) Bukkit.getPluginManager().getPlugin("BookFriends");
	}
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		FriendLoader.getInstance().saveDefaultFriendsFile();
	}
	
	@Override
	public void onDisable() {
		FriendLoader.getInstance().saveFriends();
	}
	
	@EventHandler
	public void onLogout(PlayerQuitEvent e) {
		Friend.get(e.getPlayer().getUniqueId()).destroy();
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!label.equalsIgnoreCase("friends") && !label.equalsIgnoreCase("friend"))
			return false;
		
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		FriendManager fm = FriendManager.getInstance();
		
		if (args.length == 0) {
			
			BookManager.get(player).setJsonPages(fm.getFriendPages(player.getUniqueId()));
			BookManager.get(player).openBook();
			
			return false;
		}
		
		if (args[0].equalsIgnoreCase("add")) {
			if (args.length != 2) {
				player.sendMessage("Invalid arguments!");
				player.sendMessage("/add <player>");
				return false;
			}
			
			UUID uuid = null;
			
			if (Bukkit.getOfflinePlayer(args[1]) != null)
				uuid = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
			
			if (Bukkit.getPlayerExact(args[1]) != null)
				uuid = Bukkit.getPlayerExact(args[1]).getUniqueId();

			if (uuid == null) {
				player.sendMessage("That player could not be found");
				return false;
			}
			
			if (uuid.equals(player.getUniqueId())) {
				player.sendMessage("You can't add yourself as a friend");
				return false;
			}
			
			
			if (fm.addFriend(player.getUniqueId(), uuid)) {
				player.sendMessage("Successfully added!");
			} else {
				player.sendMessage("You already have this player as a friend");
			}
			
			return false;
		}
		
		
		if (args[0].equalsIgnoreCase("remove")) {
			if (args.length != 2) {
				player.sendMessage("Invalid arguments!");
				player.sendMessage("/remove <player>");
				return false;
			}
			
			UUID uuid = null;
			
			if (Bukkit.getOfflinePlayer(args[1]) != null)
				uuid = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
			
			if (Bukkit.getPlayerExact(args[1]) != null)
				uuid = Bukkit.getPlayerExact(args[1]).getUniqueId();
			
			if (uuid == null) {
				player.sendMessage("That player could not be found");
				return false;
			}
			
			if (fm.removeFriend(player.getUniqueId(), uuid)) {
				player.sendMessage("Successfully removed!");
			} else {
				player.sendMessage("You do not have this player as a friend");
			}
			
			return false;
		}
		
		return false;
	}
}
