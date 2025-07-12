package coffee.laeven.expocool.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import coffee.laeven.expocool.config.Configurable;
import coffee.laeven.expocool.config.item.ConfigItem;
import coffee.laeven.expocool.cooldown.CooldownCtrl;
import coffee.laeven.expocool.utils.DelayUtils;
import coffee.laeven.expocool.utils.Logg;
import coffee.laeven.expocool.utils.PrintUtils;

public class CombatCtrl implements Listener
{
	private static Map<UUID,CombatInstance> playersInCombat = new HashMap<>();
	
	@EventHandler(priority = EventPriority.HIGH)
	public void handle(EntityDamageByEntityEvent e)
	{
		if(!(e.getDamager() instanceof Player attacker)) { return; }
		if(!(e.getEntity() instanceof Player victim)) { return; }
		
		Logg.verb("(" + attacker.getName() + ") v (" + victim.getName() + ") Melee damage: " + e.getDamage(),Logg.VerbGroup.IN_COMBAT);
		attemptCombat(attacker,victim,e.getDamage());
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void handleProjectile(ProjectileHitEvent e)
	{
		if(!(e.getEntity().getShooter() instanceof Player attacker)) { return; }
		if(!(e.getHitEntity() instanceof Player victim)) { return; }
		
		// Delayed by 1 tick so that getLastDamage() becomes the damage the victim received just now
		DelayUtils.executeDelayedTask(() ->
		{
			Logg.verb("(" + attacker.getName() + ") v (" + victim.getName() + ") Projectile damage: " + victim.getLastDamage(),Logg.VerbGroup.IN_COMBAT);
			attemptCombat(attacker,victim,victim.getLastDamage());
		});
	}
	
	private void attemptCombat(Player attacker,Player defender,double damage)
	{
		// Can't attack yourself
		if(attacker.getUniqueId().equals(defender.getUniqueId())) { return; }
		
		// Most both be in survival mode
		if(attacker.getGameMode() != GameMode.SURVIVAL) { return; }
		if(defender.getGameMode() != GameMode.SURVIVAL) { return; }
		
		// No damage is no combat
		if(damage <= 0) { return; }
		
		tagPlayerAsInCombat(attacker);
		tagPlayerAsInCombat(defender);
		return;
	}
	
	public static boolean isInCombat(Player p)
	{
		Objects.requireNonNull(p,"Player cannot be null!");
		return playersInCombat.containsKey(p.getUniqueId());
	}
	
	public static boolean hasCombatElapsedMoreThan(Player p,long combatTimeElapsedInTicks)
	{
		Objects.requireNonNull(p,"Player cannot be null!");
		Objects.requireNonNull(combatTimeElapsedInTicks,"Combat time elapsed cannot be null!");
		
		if(!isInCombat(p)) { return false; }
		return playersInCombat.get(p.getUniqueId()).getCombatClock().getElapsedInterval() >= combatTimeElapsedInTicks;
	}
	
	public static void tagPlayerAsInCombat(Player p)
	{
		Objects.requireNonNull(p,"Player cannot be null!");
		
		if(playersInCombat.containsKey(p.getUniqueId()))
		{
			playersInCombat.get(p.getUniqueId()).getCombatClock().refill();
			return;
		}
		
		playersInCombat.put(p.getUniqueId(),new CombatInstance(p));
	
		if(CooldownCtrl.isInDebugMode(p))
		{
			playersInCombat.get(p.getUniqueId()).startDebugClock();
		}
	}
	
	public static void tagPlayerAsOutOfCombat(Player p)
	{
		Objects.requireNonNull(p,"Player cannot be null!");
		
		if(!playersInCombat.containsKey(p.getUniqueId())) { return; }
		
		playersInCombat.remove(p.getUniqueId()).dispose();
		CooldownCtrl.resetCooldown(p);
		
		if(CooldownCtrl.isInDebugMode(p))
		{
			PrintUtils.actionBar(p,"&aYou are now out of combat");
		}
	}
	
	public static CombatInstance getCombatInstance(Player p)
	{
		Objects.requireNonNull(p,"Player cannot be null!");
		return playersInCombat.get(p.getUniqueId());
	}
	
	public static class Config implements Configurable
	{
		public static final ConfigItem<Float> IN_COMBAT_ELAPSED_TIME_TO_ENABLE_COOLDOWN = new ConfigItem<>("combat.elapsed_time_to_enable_cooldown",15f,"The number of seconds that a player must be in combat for the cooldown to enderpearls and tridents to take effect.");
		public static final ConfigItem<Float> IN_COMBAT_ELAPSED_TIME_TO_LEAVE_COMBAT = new ConfigItem<>("combat.elapsed_time_to_leave_combat",30f,"The number of seconds that a player must be in combat for without taking a hit to leave combat.");
	}
}