package com.dayton.coolstuff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Home {

	public static Map<String, List<Home>> homes = new HashMap<>();
	
	public static Home getHome(Player p, String home) {
		for (Entry<String, List<Home>> entry : homes.entrySet()) {
			if (entry.getKey().equals(p.getName())) {
				for (Home h : entry.getValue()) {
					if (h.getName().equals(home)) {
						return h;
					}
				}
			}
		}
		return null;
	}
	
	public static List<Home> getHomes(Player p) {
		for (Entry<String, List<Home>> entry : homes.entrySet()) {
			if (entry.getKey().equals(p.getName())) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	public static void loadHomes(Player p) {
		FileConfiguration config = CoolStuff.plugin.getConfig();
		List<Home> list = new ArrayList<>();
		if (config.contains("Homes." + p.getUniqueId().toString())) {
			for (String s : config.getConfigurationSection("Homes." + p.getUniqueId().toString()).getKeys(false)) {
				Home home = new Home(s, config.getConfigurationSection("Homes." + p.getUniqueId().toString() + "." + s));
				list.add(home);
			}
			homes.put(p.getName(), list);
		} else {
			System.out.println("[Homes] That player doesn't have any homes.");
		}
	}
	
	private String name;
	private Location location;
	
	public Home(String name, ConfigurationSection section) {
		this.name = name;
		World w = Bukkit.getWorld(section.getString("World"));
		double x = section.getDouble("x");
		double y = section.getDouble("y");
		double z = section.getDouble("z");
		float yaw = (float) section.getInt("yaw");
		float pitch = (float) section.getInt("pitch");
		System.out.println(w + " " + x + " " + y + " " + z + " ");
		this.location = new Location(w, x, y, z, yaw, pitch);
	}
	
	public String getName() {
		return name;
	}
	
	public Location getLocation() {
		return location;
	}
	
}
