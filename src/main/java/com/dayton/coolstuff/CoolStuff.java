package com.dayton.coolstuff;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.earth2me.essentials.Essentials;
import com.oracle.jrockit.jfr.EventDefinition;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.material.SpawnEgg;
import org.bukkit.plugin.java.JavaPlugin;

import com.dayton.coolstuff.message.MessageCommand;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class CoolStuff extends JavaPlugin implements Listener {

    public static CoolStuff plugin;
    public static Essentials ess;

    private List<String> socialspy = new ArrayList<>();

    private static File warps;
    private static FileConfiguration warpsConfig;

    private static File homes;
    private static FileConfiguration homesConfig;

    public void onEnable() {
        plugin = this;
        ess = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new HomingBow(), this);
        getServer().getPluginManager().registerEvents(this, this);

        warps = new File(getDataFolder(), "Warps.yml");
        if (!warps.exists()) {
            try {
                warps.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        warpsConfig = YamlConfiguration.loadConfiguration(warps);

        homes = new File(getDataFolder(), "Homes.yml");
        if (!homes.exists()) {
            try {
                homes.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        homesConfig = YamlConfiguration.loadConfiguration(homes);

        for (Player p : Bukkit.getOnlinePlayers()) {
            Home.loadHomes(p);
        }
        Warp.loadWarps();

        registerCommands();
    }

    public void registerCommands() {
        getCommand("message").setExecutor(new MessageCommand());
        getCommand("reply").setExecutor(new MessageCommand());
    }

    public static FileConfiguration getWarps() {
        return warpsConfig;
    }

    public static void saveWarps() {
        try {
            warpsConfig.save(warps);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FileConfiguration getHomes() {
        return homesConfig;
    }

    public static void saveHomes() {
        try {
            homesConfig.save(homes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("home")) {
            if (args.length == 0) {
                if (Home.getHomes(p) == null || Home.getHomes(p).size() == 0) {
                    p.sendMessage("§cYou do not have any homes.");
                    return true;
                }
                p.sendMessage("§7Homes:");
                for (Home home : Home.getHomes(p)) {
                    p.sendMessage("§7- " + home.getName());
                }
                return true;
            }
            if (Home.getHomes(p) == null || Home.getHomes(p).size() == 0) {
                p.sendMessage("§cYou do not have any homes.");
                return true;
            }
            if (Home.getHome(p, args[0]) == null) {
                p.sendMessage("§cNo home exists with that name.");
                return true;
            }
            p.teleport(Home.getHome(p, args[0]).getLocation());
            p.sendMessage("§aTeleported to home: " + args[0]);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("sethome")) {
            if (args.length == 0) {
                p.sendMessage("§cUsage: /sethome (home)");
            }
            if (Home.getHomes(p) == null || Home.getHomes(p).size() == 0) {
                String key = "Homes." + p.getUniqueId().toString() + "." + args[0];
                getHomes().set(key + ".World", p.getWorld().getName());
                getHomes().set(key + ".x", p.getLocation().getX());
                getHomes().set(key + ".y", p.getLocation().getY());
                getHomes().set(key + ".z", p.getLocation().getZ());
                getHomes().set(key + ".yaw", p.getLocation().getYaw());
                getHomes().set(key + ".pitch", p.getLocation().getPitch());
                saveHomes();
                Home.loadHomes(p);
                p.sendMessage("§aCreated home: " + args[0]);
                return true;
            }
            for (Home home : Home.getHomes(p)) {
                if (!args[0].equalsIgnoreCase(home.getName())) {
                    String key = "Homes." + p.getUniqueId().toString() + "." + args[0];
                    getHomes().set(key + ".World", p.getWorld().getName());
                    getHomes().set(key + ".x", p.getLocation().getX());
                    getHomes().set(key + ".y", p.getLocation().getY());
                    getHomes().set(key + ".z", p.getLocation().getZ());
                    getHomes().set(key + ".yaw", p.getLocation().getYaw());
                    getHomes().set(key + ".pitch", p.getLocation().getPitch());
                    saveHomes();
                    Home.loadHomes(p);
                    p.sendMessage("§aCreated home: " + args[0]);
                    return true;
                } else {
                    p.sendMessage("§cThat home already exists.");
                }
            }
        }

        if (cmd.getName().equalsIgnoreCase("delhome")) {
            if (args.length == 0) {
                p.sendMessage("§cUsage: /delhome (home)");
            }
            if (args.length == 1) {
                if (Home.getHomes(p) == null || Home.getHomes(p).size() == 0) {
                    p.sendMessage("§cYou do not have any homes.");
                    return true;
                }

                for (Home home : Home.getHomes(p)) {
                    if (args[0].equalsIgnoreCase(home.getName())) {
                        getHomes().set("Homes." + p.getUniqueId().toString() + "." + args[0], null);
                        saveHomes();
                        Home.loadHomes(p);
                        p.sendMessage("§aDeleted home: " + args[0]);
                        return true;
                    } else {
                        p.sendMessage("§cNo home exists with that name.");
                    }
                }
            }
        }

        // WARPS SECTION
        if (cmd.getName().equalsIgnoreCase("warp")) {
            if (args.length == 0) {
                if (Warp.getWarps().size() == 0) {
                    p.sendMessage("§cThere are no warps set.");
                    return true;
                }
                StringBuilder builder = new StringBuilder();
                builder.append("§6Warps:");
                for (Warp warp : Warp.getWarps()) {
                    if (builder.length() > 1) {
                        builder.append("§7, ");
                    }
                    builder.append("§7" + warp.getName());
                }
                p.sendMessage(builder.toString().replaceFirst(",", ""));
                return true;
            }
            if (Warp.getWarps().size() == 0) {
                p.sendMessage("§cThere are no warps set.");
                return true;
            }
            if (Warp.getWarp(args[0]) == null) {
                p.sendMessage("§cNo warp exists with that name.");
                return true;
            }
            p.teleport(Warp.getWarp(args[0]).getLocation());
            p.sendMessage("§aTeleported to warp: " + args[0]);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("setwarp")) {
            if (args.length == 0) {
                p.sendMessage("§cUsage: /setwarp (warp)");
                return true;
            }
            if (Warp.getWarps().size() == 0) {
                String key = "Warps." + args[0];
                getWarps().set(key + ".World", p.getWorld().getName());
                getWarps().set(key + ".x", p.getLocation().getX());
                getWarps().set(key + ".y", p.getLocation().getY());
                getWarps().set(key + ".z", p.getLocation().getZ());
                getWarps().set(key + ".yaw", p.getLocation().getYaw());
                getWarps().set(key + ".pitch", p.getLocation().getPitch());
                saveWarps();
                Warp.loadWarps();
                p.sendMessage("§aCreated warp: " + args[0]);
                return true;
            }
            if (Warp.getWarp(args[0]) == null) {
                String key = "Warps." + args[0];
                getWarps().set(key + ".World", p.getWorld().getName());
                getWarps().set(key + ".x", p.getLocation().getX());
                getWarps().set(key + ".y", p.getLocation().getY());
                getWarps().set(key + ".z", p.getLocation().getZ());
                getWarps().set(key + ".yaw", p.getLocation().getYaw());
                getWarps().set(key + ".pitch", p.getLocation().getPitch());
                saveWarps();
                Warp.loadWarps();
                p.sendMessage("§aCreated warp: " + args[0]);
                return true;
            } else {
                p.sendMessage("§cThat warp already exists.");
            }
        }

        if (cmd.getName().equalsIgnoreCase("delwarp")) {
            if (args.length == 0) {
                p.sendMessage("§cUsage: /delwarp (warp)");
            }
            if (args.length == 1) {
                if (Warp.getWarps().size() == 0) {
                    p.sendMessage("§cThere are no warps set.");
                    return true;
                }

                for (Warp warp : Warp.getWarps()) {
                    if (args[0].equalsIgnoreCase(warp.getName())) {
                        getWarps().set("Warps." + args[0], null);
                        saveWarps();
                        Warp.loadWarps();
                        p.sendMessage("§aDeleted warp: " + args[0]);
                        return true;
                    } else {
                        p.sendMessage("§cNo warp exists with that name.");
                    }
                }
            }
        }

        if (cmd.getName().equalsIgnoreCase("socialspy")) {
            if (socialspy.contains(p.getName())) {
                socialspy.remove(p.getName());
                p.sendMessage("§aSocial Spy turned off.");
            } else {
                socialspy.add(p.getName());
                p.sendMessage("§aSocial Spy turned on.");
            }
        }

        if (cmd.getName().equalsIgnoreCase("top")) {
            Block top = p.getWorld().getHighestBlockAt(p.getLocation());
            if (top.getLocation().getY() == p.getLocation().getY()) {
                p.sendMessage("§cYou are already at the highest block.");
                return true;
            }
            p.teleport(
                    new Location(p.getWorld(), top.getLocation().getX(), top.getLocation().getY(), top.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch()));
        }

        if (cmd.getName().equalsIgnoreCase("near")) {
            int maxDist = 200;
            StringBuilder b = new StringBuilder();
            DecimalFormat f = new DecimalFormat("###");
            b.append("§6Players nearby: ");
            int count = 0;
            for (Entity e : p.getWorld().getEntities()) {
                if (p.getLocation().distance(e.getLocation()) <= maxDist) {
                    if (e instanceof Creeper) {
                        if (e.isDead() || e.getName() == p.getName())
                            continue;
                        double dist = p.getLocation().distance(e.getLocation());
                        b.append("§r" + e.getName() + "§7(§6" + f.format(dist) + "m§7)").append("§7, ");
                        count++;
                    }
                }
            }
            if (count == 0) {
                b.append("§rnone");
            } else {
                b.deleteCharAt(b.lastIndexOf(","));
            }
            p.sendMessage(b.toString());
        }
        return false;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Home.loadHomes(e.getPlayer());
    }

    @EventHandler
    public void preCommand(PlayerCommandPreprocessEvent e) {
        for (String s : getConfig().getStringList("SocialSpy")) {
            if (e.getMessage().startsWith("/" + s.toLowerCase())) {
                String[] args = e.getMessage().split(" ");
                for (String sp : socialspy) {
                    Player player = Bukkit.getPlayer(sp);
                    String msg = "§6" + e.getPlayer().getName() + "§7: §r/" + s + " ";
                    for (int i = 1; i < args.length; i++) {
                        msg += args[i] + " ";
                    }
                    player.sendMessage(msg);
                }
            }
        }
    }

    @EventHandler
    public void onEggClick(final PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR) {
            if (e.getItem().getType() == Material.MONSTER_EGG) {
                final String type = Util.getFriendlyName(e.getItem(), true).toUpperCase();
                final ItemStack item = e.getItem().clone();
                item.setAmount(1);

                final Item egg = e.getPlayer().getWorld().dropItem(e.getPlayer().getEyeLocation(), item);
                egg.setVelocity(e.getPlayer().getEyeLocation().getDirection().multiply(2));
                if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                    if (e.getItem().getAmount() > 1) {
                        e.getItem().setAmount(e.getItem().getAmount() - 1);
                    } else {
                        e.getPlayer().getInventory().remove(e.getItem());
                    }
                    e.getPlayer().updateInventory();
                }

                new BukkitRunnable() {
                    public void run() {
                        if (egg.isOnGround()) {
                            e.getPlayer().getWorld().spawnEntity(egg.getLocation(), EntityType.valueOf(type.split(" ")[1]));
                            egg.remove();
                            cancel();
                            // Testing
                        }
                    }
                }.runTaskTimer(this, 0, 3);
            }
        }
    }

}
