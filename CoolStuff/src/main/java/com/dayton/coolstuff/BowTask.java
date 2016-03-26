package com.dayton.coolstuff;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BowTask extends BukkitRunnable {

	private static final double MaxRotationAngle = HomingBow.rotate;
	Entity arrow;
	LivingEntity target;
	private LivingEntity shooter;

	public BowTask(LivingEntity shooter, Entity arrow, LivingEntity target) {
		this.arrow = arrow;
		this.target = target;
		this.shooter = shooter;
		runTaskTimer(CoolStuff.plugin, 0, 1);
	}

	public void run() {
		double speed = this.arrow.getVelocity().length();
		if ((this.arrow.isOnGround()) || (this.arrow.isDead()) || (this.target.isDead()) || target == shooter || target.getName() == "ChloeMoretz") {
			cancel();
			return;
		}
		Vector toTarget = this.target.getLocation().clone().add(new Vector(0.0D, 0.5D, 0.0D)).subtract(this.arrow.getLocation()).toVector();

		Vector dirVelocity = this.arrow.getVelocity().clone().normalize();
		Vector dirToTarget = toTarget.clone().normalize();
		double angle = dirVelocity.angle(dirToTarget);

		double newSpeed = 0.9D * speed + 0.14D;
		if ((this.target instanceof Player)) {
			if (this.arrow.getLocation().distance(this.target.getLocation()) < 8.0D) {
				Player player = (Player) this.target;
				if (player.isBlocking()) {
					newSpeed = speed * 0.6D;
				}
			}
		}
		Vector newVelocity;
		if (angle < 0.12D) {
			newVelocity = dirVelocity.clone().multiply(newSpeed);
		} else {
			Vector newDir = dirVelocity.clone().multiply((angle - MaxRotationAngle) / angle).add(dirToTarget.clone().multiply(MaxRotationAngle / angle));
			newDir.normalize();
			newVelocity = newDir.clone().multiply(newSpeed);
		}
		this.arrow.setVelocity(newVelocity.add(new Vector(0.0D, 0.06D, 0.0D)));
	}
}
