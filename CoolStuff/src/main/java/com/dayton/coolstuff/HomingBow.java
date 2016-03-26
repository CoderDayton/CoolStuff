package com.dayton.coolstuff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class HomingBow implements Listener {

	public static String itemName = "§6Homing Bow";
	public static Map<String, Entity> arrowTracking = new HashMap<>();
	public static double rotate = 0.12;

	public static ItemStack getBow() {
		ItemStack is = new ItemStack(Material.BOW);
		ItemMeta im = is.getItemMeta();

		im.setDisplayName(itemName);
		is.setItemMeta(im);
		return is;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onShoot(EntityShootBowEvent e) {
		if (e.getProjectile() instanceof Arrow) {
			if (e.getEntity() instanceof Player) {
				Player p = (Player) e.getEntity();
				if (e.getBow().hasItemMeta()) {
					if (e.getBow().getItemMeta().hasDisplayName()) {
						if (e.getBow().getItemMeta().getDisplayName().equals(itemName)) {
							double minimumAngle = 18.0;
							Entity minimumEntity = null;
							e.getProjectile().setMetadata("homingarrow", new FixedMetadataValue(CoolStuff.plugin, "homingarrow_0"));
							if (!arrowTracking.containsKey(p.getName())) {
								for (Entity ent : getNearbyEntities(p.getTargetBlock((HashSet<Byte>) null, 64).getLocation(), 1000)) {
									if (ent instanceof Player) {
										if (p.hasLineOfSight(ent) && !ent.isDead()) {
											Vector vec = ent.getLocation().toVector().clone().subtract(p.getLocation().toVector());
											double angle = e.getProjectile().getVelocity().angle(vec);
											if (angle < minimumAngle) {
												minimumAngle = angle;
												minimumEntity = ent;
												arrowTracking.put(p.getName(), ent);
											}
										}
									}
								}
							}
							if (minimumEntity != null && !minimumEntity.isDead()) {
								Arrow arrow = (Arrow) e.getProjectile();
								new BowTask(p, arrow, (LivingEntity) minimumEntity);
							} else {
								arrowTracking.remove(p.getName());
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void switcher(PlayerItemHeldEvent e) {
		if (arrowTracking.containsKey(e.getPlayer()) && (e.getPlayer().getInventory().getItemInMainHand().getType() != Material.BOW)) {
			arrowTracking.remove(e.getPlayer());
		}
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		if (!(e.getEntity() instanceof Arrow)) {
			return;
		}
		Arrow arrow = (Arrow) e.getEntity();
		if (!e.getEntity().hasMetadata("homingarrow")) {
			return;
		}
		for (Entity entity : arrow.getNearbyEntities(1.0D, 1.0D, 1.0D)) {
			if (!(entity instanceof Player)) {
				return;
			}
			arrow.getWorld().playSound(arrow.getLocation(), Sound.ENTITY_ENDERDRAGON_SHOOT, 2.0F, 2.0F);
		}
		arrowTracking.remove(((Player) e.getEntity().getShooter()).getName());
		e.getEntity().remove();
	}

	public List<Entity> getNearbyEntities(Location loc, int radius) {
		List<Entity> list = new ArrayList<>();
		for (Entity e : loc.getWorld().getEntities()) {
			if (loc.distance(e.getLocation()) <= radius) {
				list.add(e);
			}
		}
		return list;
	}

}
