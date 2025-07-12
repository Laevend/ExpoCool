package coffee.laeven.expocool.cooldown;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import coffee.laeven.expocool.combat.CombatCtrl;
import coffee.laeven.expocool.config.Configurable;
import coffee.laeven.expocool.config.item.ConfigItem;
import coffee.laeven.expocool.utils.Logg;

public class CooldownCtrl implements Listener
{
	private static Map<UUID,EnderpearlCooldownInstance> pearlCooldownMap = new HashMap<>();
	private static Map<UUID,TridentCooldownInstance> tridentCooldownMap = new HashMap<>();
	private static Set<UUID> playersInDebugMode = new HashSet<>();
	
	public static void removeEnderpearlCooldownInstance(UUID uuid)
	{
		Objects.requireNonNull(uuid,"UUID cannot be null!");
		
		if(!pearlCooldownMap.containsKey(uuid)) { return; }
		pearlCooldownMap.get(uuid).dispose();
		pearlCooldownMap.remove(uuid);
	}
	
	public static void removeTridentCooldownInstance(UUID uuid)
	{
		Objects.requireNonNull(uuid,"UUID cannot be null!");
		
		if(!tridentCooldownMap.containsKey(uuid)) { return; }
		tridentCooldownMap.get(uuid).dispose();
		tridentCooldownMap.remove(uuid);
	}
	
	/**
	 * Spigot calls this method when a player leaves the server
	 * @param e PlayerQuitEvent
	 */
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e)
	{
		if(pearlCooldownMap.containsKey(e.getPlayer().getUniqueId()))
		{
			EnderpearlCooldownInstance eci = pearlCooldownMap.get(e.getPlayer().getUniqueId());
			
			// If their cooldown is back to normal, no point keeping it in memory when they leave
			if(eci.getPowerModifier() == 0f || eci.getLastCooldown() == 0f)
			{
				removeEnderpearlCooldownInstance(e.getPlayer().getUniqueId());
			}
			
			eci.stopDebugClock();
			eci.holdCooldown();
		}
		
		if(tridentCooldownMap.containsKey(e.getPlayer().getUniqueId()))
		{
			TridentCooldownInstance tci = tridentCooldownMap.get(e.getPlayer().getUniqueId());
			
			// If their cooldown is back to normal, no point keeping it in memory when they leave
			if(tci.getPowerModifier() == 0f || tci.getLastCooldown() == 0f)
			{
				removeTridentCooldownInstance(e.getPlayer().getUniqueId());
			}
			
			tci.stopDebugClock();
			tci.holdCooldown();
		}
	}
	
	/**
	 * Spigot calls this method when a player joins the server
	 * @param e PlayerQuitEvent
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		if(pearlCooldownMap.containsKey(e.getPlayer().getUniqueId()))
		{
			EnderpearlCooldownInstance eci = pearlCooldownMap.get(e.getPlayer().getUniqueId());
			eci.resync(e.getPlayer());
			eci.applyHeldCooldown();
			
			if(playersInDebugMode.contains(e.getPlayer().getUniqueId()))
			{
				pearlCooldownMap.get(e.getPlayer().getUniqueId()).startDebugClock();
			}
		}
		
		if(tridentCooldownMap.containsKey(e.getPlayer().getUniqueId()))
		{
			TridentCooldownInstance tci = tridentCooldownMap.get(e.getPlayer().getUniqueId());
			tci.resync(e.getPlayer());
			tci.applyHeldCooldown();
			
			if(playersInDebugMode.contains(e.getPlayer().getUniqueId()))
			{
				tridentCooldownMap.get(e.getPlayer().getUniqueId()).startDebugClock();
			}
		}
	}
	
	/**
	 * Spigot calls this method when a pearl is thrown
	 * @param e ProjectileLaunchEvent
	 */
	@EventHandler
	public void onPearlThrow(ProjectileLaunchEvent e)
	{
		if(!(e.getEntity() instanceof EnderPearl)) { return; }
		if(!(e.getEntity().getShooter() instanceof Player p)) { return; }
		
		// Check if player is in combat for some time
		if(!CombatCtrl.hasCombatElapsedMoreThan(p,(long) (CombatCtrl.Config.IN_COMBAT_ELAPSED_TIME_TO_ENABLE_COOLDOWN.get() * 20))) { return; }
		
		if(!pearlCooldownMap.containsKey(p.getUniqueId()))
		{
			pearlCooldownMap.put(p.getUniqueId(),new EnderpearlCooldownInstance(p));
			
			if(playersInDebugMode.contains(p.getUniqueId()))
			{
				pearlCooldownMap.get(p.getUniqueId()).startDebugClock();
			}
		}
		
		pearlCooldownMap.get(p.getUniqueId()).triggerItemUse();
	}
	
	/**
	 * Spigot calls this method when a player performs the riptide action
	 * @param e PlayerRiptideEvent
	 */
	@EventHandler
	public void onRiptide(PlayerRiptideEvent e)
	{
		// Check if player is in combat for some time
		if(!CombatCtrl.hasCombatElapsedMoreThan(e.getPlayer(),(long) (CombatCtrl.Config.IN_COMBAT_ELAPSED_TIME_TO_ENABLE_COOLDOWN.get() * 20))) { return; }
		
		if(e.getItem().getType() != Material.TRIDENT)
		{
			Logg.warn("Player " + e.getPlayer().getName() + " is riptiding without a trident?");
			return;
		}
		
		UUID playerUUID = e.getPlayer().getUniqueId();
		ItemStack trident = e.getItem();
		ItemMeta meta = trident.getItemMeta();
		
		if(!meta.hasEnchants())
		{
			Logg.warn("Player " + e.getPlayer().getName() + " is riptiding with no enchantments?");
			return;
		}
		
		int riptideLevel = meta.getEnchantLevel(Enchantment.RIPTIDE);
		
		if(riptideLevel <= 0)
		{
			Logg.warn("Player " + e.getPlayer().getName() + " is riptiding without a riptide enchant?");
			return;
		}
		
		if(!tridentCooldownMap.containsKey(playerUUID))
		{
			tridentCooldownMap.put(playerUUID,new TridentCooldownInstance(e.getPlayer()));
			
			if(playersInDebugMode.contains(playerUUID))
			{
				tridentCooldownMap.get(playerUUID).startDebugClock();
			}
		}
		
		tridentCooldownMap.get(playerUUID).triggerItemUse();
	}
	
	/**
	 * Clears all current cooldown instances
	 */
	public static void clearInstances()
	{
		pearlCooldownMap.forEach((k,v) -> v.dispose());
		tridentCooldownMap.forEach((k,v) -> v.dispose());
		pearlCooldownMap.clear();
		tridentCooldownMap.clear();
	}
	
	public static void clearInstance(Player p)
	{
		Objects.requireNonNull(p,"Player cannot be null!");
		clearInstance(p.getUniqueId());
	}
	
	/**
	 * Clears cooldown instance for a player
	 */
	public static void clearInstance(UUID playerUUID)
	{
		Objects.requireNonNull(playerUUID,"Player uuid cannot be null!");
		
		if(pearlCooldownMap.containsKey(playerUUID))
		{
			pearlCooldownMap.get(playerUUID).dispose();
		}
		
		if(tridentCooldownMap.containsKey(playerUUID))
		{
			tridentCooldownMap.get(playerUUID).dispose();
		}
	}
	
	/**
	 * Resets cooldown instance for a player
	 */
	public static void resetCooldown(UUID playerUUID)
	{
		Objects.requireNonNull(playerUUID,"Player cannot be null!");
		
		if(pearlCooldownMap.containsKey(playerUUID))
		{
			pearlCooldownMap.get(playerUUID).resetCooldown();
		}
		
		if(tridentCooldownMap.containsKey(playerUUID))
		{
			tridentCooldownMap.get(playerUUID).resetCooldown();
		}
	}
	
	/**
	 * Enable or disable debug for this player viewing cooldowns
	 * @param p Player to set debug state for
	 * @param debugState Debug state, true to enable, false to disable
	 */
	public static void setDebugMode(Player p,boolean debugState)
	{
		Objects.requireNonNull(p,"Player cannot be null!");
		Objects.requireNonNull(debugState,"Debug state cannot be null!");
		
		if(debugState)
		{
			playersInDebugMode.add(p.getUniqueId());
			
			if(pearlCooldownMap.containsKey(p.getUniqueId()))
			{
				pearlCooldownMap.get(p.getUniqueId()).startDebugClock();
			}
			
			if(tridentCooldownMap.containsKey(p.getUniqueId()))
			{
				tridentCooldownMap.get(p.getUniqueId()).startDebugClock();
			}
			
			if(CombatCtrl.isInCombat(p))
			{
				CombatCtrl.getCombatInstance(p).startDebugClock();
			}
			
			return;
		}
		
		playersInDebugMode.remove(p.getUniqueId());
		
		if(pearlCooldownMap.containsKey(p.getUniqueId()))
		{
			pearlCooldownMap.get(p.getUniqueId()).stopDebugClock();
		}
		
		if(tridentCooldownMap.containsKey(p.getUniqueId()))
		{
			tridentCooldownMap.get(p.getUniqueId()).stopDebugClock();
		}
		
		if(CombatCtrl.isInCombat(p))
		{
			CombatCtrl.getCombatInstance(p).stopDebugClock();
		}
	}
	
	/**
	 * Checks if a player is in debug mode
	 * @param p Player to check
	 * @return True if this player is in debug mode, false otherwise
	 */
	public static boolean isInDebugMode(Player p)
	{
		Objects.requireNonNull(p,"Player cannot be null!");
		return playersInDebugMode.contains(p.getUniqueId());
	}
	
	/**
	 * Configurable values for pearl and riptide trident cooldown instances
	 */
	public static class Config implements Configurable
	{
		public static final ConfigItem<Float> ENDERPEARL_BASE_MULTIPLIER = new ConfigItem<>("cooldown.enderpearl.base_multiplier",1.2f,"The base multiplier that when combined with the multiplier power acts as a multiplier to the players enderpearl cooldown (in real seconds).");
		public static final ConfigItem<Float> ENDERPEARL_BASE_COOLDOWN = new ConfigItem<>("cooldown.enderpearl.base_cooldown",0.5f,"The starting value of an enderpearls cooldown (must be lower than min_cooldown) (in real seconds).");
		public static final ConfigItem<Float> ENDERPEARL_BASE_POWER = new ConfigItem<>("cooldown.enderpearl.base_multiplier_power",1f,"The power that the base multiplier will be raised to (base_multiplier ^ base_multiplier_power).");
		public static final ConfigItem<Float> ENDERPEARL_POWER_INCREMENT = new ConfigItem<>("cooldown.enderpearl.power_increment",1f,"How much to increase the base_multiplier_power by everytime a pearl is thrown.");
		public static final ConfigItem<Float> ENDERPEARL_MIN_COOLDOWN = new ConfigItem<>("cooldown.enderpearl.min_cooldown",1f,"The lowest value a enderpearls cooldown can be (cooldown is clamped to this value if cooldown falls below this value) (must be higher than base_cooldown) (in real seconds).");
		public static final ConfigItem<Float> ENDERPEARL_MAX_COOLDOWN = new ConfigItem<>("cooldown.enderpearl.max_cooldown",15f,"The highest value an enderpearls cooldown can be (cooldown is clamped to this value if cooldown rises above this value) (must be higher than base_cooldown & min_cooldown) (in real seconds).");
		public static final ConfigItem<Float> ENDERPEARL_COOLDOWN_REDUCTION_DELAY = new ConfigItem<>("cooldown.enderpearl.cooldown_deduct_delay",10f,"They delay (in real seconds) that need to elapse without throwing a pearl for a cooldown reduction.");
		public static final ConfigItem<Float> ENDERPEARL_COOLDOWN_REDUCTION_AMOUNT = new ConfigItem<>("cooldown.enderpearl.cooldown_deduct_amount",1f,"The amount (in real seconds) to deduct from the players current enderpearl cooldown.");
		
		public static final ConfigItem<Float> TRIDENT_BASE_MULTIPLIER = new ConfigItem<>("cooldown.trident.base_multiplier",1.2f,"The base multiplier that when combined with the multiplier power acts as a multiplier to the players enderpearl cooldown (in real seconds).");
		public static final ConfigItem<Float> TRIDENT_BASE_COOLDOWN = new ConfigItem<>("cooldown.trident.base_cooldown",0.5f,"The starting value of an tridents cooldown (must be lower than min_cooldown) (in real seconds).");
		public static final ConfigItem<Float> TRIDENT_BASE_POWER = new ConfigItem<>("cooldown.trident.base_multiplier_power",1f,"The power that the base multiplier will be raised to (base_multiplier ^ base_multiplier_power).");
		public static final ConfigItem<Float> TRIDENT_POWER_INCREMENT = new ConfigItem<>("cooldown.trident.power_increment",1f,"How much to increase the base_multiplier_power by everytime a pearl is thrown.");
		public static final ConfigItem<Float> TRIDENT_MIN_COOLDOWN = new ConfigItem<>("cooldown.trident.min_cooldown",1f,"The lowest value a tridents cooldown can be (cooldown is clamped to this value if cooldown falls below this value) (must be higher than base_cooldown) (in real seconds).");
		public static final ConfigItem<Float> TRIDENT_MAX_COOLDOWN = new ConfigItem<>("cooldown.trident.max_cooldown",15f,"The highest value a tridents cooldown can be (cooldown is clamped to this value if cooldown rises above this value) (must be higher than base_cooldown & min_cooldown) (in real seconds).");
		public static final ConfigItem<Float> TRIDENT_COOLDOWN_REDUCTION_DELAY = new ConfigItem<>("cooldown.trident.cooldown_deduct_delay",10f,"They delay (in real seconds) that need to elapse without throwing a pearl for a cooldown reduction.");
		public static final ConfigItem<Float> TRIDENT_COOLDOWN_REDUCTION_AMOUNT = new ConfigItem<>("cooldown.trident.cooldown_deduct_amount",1f,"The amount (in real seconds) to deduct from the players current enderpearl cooldown.");
	}
}
