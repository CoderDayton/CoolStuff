package com.dayton.coolstuff.message;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.dayton.coolstuff.CoolStuff;

public class Message {

	private String toFormat;
	private String fromFormat;
	private Map<String, String> replies;
	
	public Message() {
		this.toFormat = CoolStuff.plugin.getConfig().getString("Messages.ToFormat");
		this.fromFormat = CoolStuff.plugin.getConfig().getString("Messages.FromFormat");
		replies = new HashMap<>();
	}
	
	public void sendMessage(Player from, Player to, String message) {
		if (to == null) {
			from.sendMessage("§cThat player isn't online.");
			return;
		}
		if (from == to) {
			from.sendMessage("§cYou cannot send a message to yourself.");
			return;
		}
		
		String fromMessage = fromFormat;
		String toMessage = toFormat;
		if (fromMessage.contains("{to}")) {
			fromMessage = fromMessage.replace("{to}", to.getName());
		} else if (fromMessage.contains("{to_displayname}")) {
			fromMessage = fromMessage.replace("{to_displayname}", to.getDisplayName());
		}
		if (fromMessage.contains("{from}")) {
			fromMessage = fromMessage.replace("{from}", from.getName());
		} else if (fromMessage.contains("{from_displayname}")) {
			fromMessage = fromMessage.replace("{from_displayname}", from.getDisplayName());
		}
		if (fromMessage.contains("{message}")) {
			fromMessage = fromMessage.replace("{message}", message);
		}
		
		if (toMessage.contains("{to}")) {
			toMessage = toMessage.replace("{to}", to.getName());
		} else if (fromMessage.contains("{to_displayname}")) {
			toMessage = toMessage.replace("{to_displayname}", to.getDisplayName());
		}
		if (toMessage.contains("{from}")) {
			toMessage = toMessage.replace("{from}", from.getName());
		} else if (toMessage.contains("{from_displayname}")) {
			toMessage = toMessage.replace("{from_displayname}", from.getDisplayName());
		}
		if (toMessage.contains("{message}")) {
			toMessage = toMessage.replace("{message}", message);
		}
		
		replies.put(to.getName(), from.getName());
		from.sendMessage(fromMessage.replaceAll("&", "§"));
		to.sendMessage(toMessage.replaceAll("&", "§"));
	}
	
	public void reply(Player from, String message) {
		System.out.println(replies.size());
		if (!replies.containsKey(from.getName()) && !replies.containsValue(from.getName())) {
			from.sendMessage("§cYou do not have anyone to reply to.");
			return;
		}
		if (replies.containsKey(from.getName())) {
			Player to = Bukkit.getPlayer(replies.get(from.getName()));
			if (to == null) {
				from.sendMessage("§cThis person has logged out.");
				replies.remove(from.getName());
				return;
			}
			replies.remove(from.getName());
			sendMessage(from, to, message);
			return;
		}
		
		if (replies.containsValue(from.getName())) {
			Player to = getReplyFromValue(from);
			if (to == null) {
				from.sendMessage("§cThis person has logged out.");
				removeReplyWithValue(from.getName());
				return;
			}
			removeReplyWithValue(from.getName());
			sendMessage(from, to, message);
			return;
		}
		return;
	}
	
	public Player getReplyFromValue(Player p) {
		for (Entry<String, String> entry : replies.entrySet()) {
			if (entry.getValue().equals(p.getName())) {
				Player returnee =  Bukkit.getPlayer(entry.getKey());
				return returnee != null ? returnee : null;
			}
		}
		return null;
	}
	
	public void removeReplyWithValue(String value) {
		for (Entry<String, String> entry : replies.entrySet()) {
			if (entry.getValue().equals(value)) {
				replies.remove(entry.getKey());
			}
		}
	}
}
