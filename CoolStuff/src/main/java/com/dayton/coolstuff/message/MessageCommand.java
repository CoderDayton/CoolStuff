package com.dayton.coolstuff.message;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class MessageCommand implements CommandExecutor {

	public static Message messageHandler;
	
	public MessageCommand() {
		messageHandler = new Message();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof ConsoleCommandSender) { sender.sendMessage("The console cannot send messages."); return true; }
		Player p = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("message")) {
			if (args.length >= 2) {
				Player to = Bukkit.getPlayer(args[0]);
				String message = "";
				for (int i = 1; i < args.length; i++) {
					message += args[i] + " ";
				}
				messageHandler.sendMessage(p, to, message);
			} else {
				p.sendMessage("§cNot enough arguments. Try /msg (player) (message)");
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("reply")) {
			if (args.length >= 1) {
				String message = "";
				for (int i = 0; i < args.length; i++) {
					message += args[i] + " ";
				}
				messageHandler.reply(p, message);
			} else {
				p.sendMessage("§cNot enough arguments. Try /r (message)");
			}
		}
		return false;
	}
	
}
