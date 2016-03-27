package com.dayton.coolstuff;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Warp {

    public static List<Warp> warps = new ArrayList<>();

    public static Warp getWarp(String warp) {
        for (Warp entry : warps) {
            if (entry.getName().equals(warp)) {
                return entry;
            }
        }
        return null;
    }

    public static List<Warp> getWarps() {
        return warps;
    }

    public static void loadWarps() {
        warps.clear();
        FileConfiguration config = CoolStuff.getWarps();
        if (config.contains("Warps")) {
            for (String s : config.getConfigurationSection("Warps").getKeys(false)) {
                Warp warp = new Warp(s, config.getConfigurationSection("Warps." + s));
                warps.add(warp);
            }
        }
    }

    private String name;
    private Location location;

    public Warp(String name, ConfigurationSection section) {
        this.name = name;
        World w = Bukkit.getWorld(section.getString("World"));
        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        float yaw = (float) section.getInt("yaw");
        float pitch = (float) section.getInt("pitch");
        this.location = new Location(w, x, y, z, yaw, pitch);
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

}
