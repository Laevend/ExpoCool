package coffee.laeven.expocool.cooldown;

import java.text.DecimalFormat;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import coffee.laeven.expocool.utils.Logg;
import coffee.laeven.expocool.utils.MathUtils;
import coffee.laeven.expocool.utils.clocks.RefillableIntervalClock;
import coffee.laeven.expocool.utils.clocks.RepeatingClock;

/**
 * Represents an instance of an exponential cooldown
 */
public abstract class CooldownInstance
{
	protected Player owner;
	protected String name;
	
	protected float baseMultiplier = 1.2f;					// Base multiplier {cooldown.enderpearl.base_multiplier}
	protected float baseCooldown = 0.5f;					// Base cooldown. {cooldown.enderpearl.base_cooldown}
	protected float basePower = 1f;							// Base power. {cooldown.enderpearl.base_multiplier_power}
	
	protected float powerModifier = 0f;						// Modifies base power.
	protected float powerModifierAmount = 0f;				// Amount to increment power modifier by. {cooldown.enderpearl.power_increment}
	protected float multiplier = 0f;						// Multiplier calculated from (baseMultiplier ^ (baseMultiplier + powerModifier)).
	
	protected float minCooldown = 1;						// Minimum cooldown (clamped). {cooldown.enderpearl.min_cooldown}
	protected float maxCooldown = 15;						// Maximum cooldown (clamped). {cooldown.enderpearl.max_cooldown}
	
	protected float deductDelay = 0f;						// Delay before a deduction in the cooldown. {cooldown.enderpearl.cooldown_deduct_delay}
	protected float deductAmount = 0f;						// Deduct amount. {cooldown.enderpearl.cooldown_deduct_amount}
	protected float totalDeductAmount = 0f;					// Total amount of deduction (equal to deductAmount accumulated overtime when not using pearl or trident)
	
	protected CooloffClock cooloffClock = null;
	protected DebugClock debugClock = null;
	
	protected float lastCooldown = 0f;						// Used for debug viewing
	protected int heldCooldown = 0;							/** Cooldown held if player disconnects. {@link #holdCooldown()} */
	
	protected CooldownType type;
	
	public CooldownInstance(Player p,CooldownType type)
	{
		Objects.requireNonNull(p,"Player cannot be null!");
		Objects.requireNonNull(type,"CooldownType cannot be null!");
		this.owner = p;
		this.name = p.getName();
		this.type = type;
	}
	
	/**
	 * Called when an enderpearl or a riptide trident is used
	 */
	public void triggerItemUse()
	{
		// Unlikely to need a power higher than 9999
		powerModifier = MathUtils.clamp(0,9999f,(powerModifier + powerModifierAmount));
		calculateNewCooldown();
		cooloffClock.refill();
		totalDeductAmount = 0f;
	}
	
	/**
	 * Calculates new cooldown result
	 */
	public void calculateNewCooldown()
	{
		// Create multiplier
		float newCooldownMultiplier = (float) Math.pow(baseMultiplier,(basePower + powerModifier));
		
		// Create cooldown (in seconds)
		float newCooldown = MathUtils.clamp(minCooldown,maxCooldown,(newCooldownMultiplier * baseCooldown));
		
		// Deduct cooldown
		newCooldown = MathUtils.clamp(minCooldown,maxCooldown,(newCooldown - totalDeductAmount));
		
		lastCooldown = newCooldown;
		
		// Convert cooldown in seconds to game ticks
		int newCooldownInTicks = (int) (newCooldown * 20);
		setNewCooldown(newCooldownInTicks);
		
		Logg.verb("(" + name + ") [" + type.toString().toLowerCase() + "] New multiplier > " + newCooldownMultiplier,Logg.VerbGroup.COOLDOWN_INSTANCE);
		Logg.verb("(" + name + ") [" + type.toString().toLowerCase() + "] New cooldown > " + newCooldown,Logg.VerbGroup.COOLDOWN_INSTANCE);
		Logg.verb("(" + name + ") [" + type.toString().toLowerCase() + "] New cooldown (in ticks) > " + newCooldownInTicks,Logg.VerbGroup.COOLDOWN_INSTANCE);
	}
	
	/**
	 * Apply the cooldown to the pearl or trident
	 * @param newCooldownInTicks New cooldown to set (in ticks)
	 */
	public abstract void setNewCooldown(int newCooldownInTicks);
	
	/**
	 * Reset the cooldown back to stock settings
	 */
	public void resetCooldown()
	{
		powerModifier = 0f;
		totalDeductAmount = 0f;
		lastCooldown = 1f;
		removeIfPlayerIsOffline();
	}
	
	public abstract void removeIfPlayerIsOffline();
	
	/**
	 * Cooloff clock that reduces a players pearl or trident cooldown by 'deductAmount' every 'deductDelay' seconds
	 */
	protected class CooloffClock extends RefillableIntervalClock
	{
		public CooloffClock(long deductDelay)
		{
			super(type.toString().toLowerCase() + "_cooloff_clock",deductDelay);
		}

		@Override
		public void execute() throws Exception
		{
			// Refill clock to begin counting down again
			refill();
			
			// No power modifier? No cooldown to deduct
			if(powerModifier == 0f) { return; }
			
			powerModifier = MathUtils.clamp(0,9999f,(powerModifier - powerModifierAmount));
			
			// No point deducting more than the max cooldown
			totalDeductAmount = MathUtils.clamp(0,maxCooldown,(totalDeductAmount + deductAmount));
			
			Logg.verb("(" + name + ") [" + type.toString().toLowerCase() + "] Cooling off, power modifier > " + powerModifier,Logg.VerbGroup.COOLDOWN_INSTANCE);
			Logg.verb("(" + name + ") [" + type.toString().toLowerCase() + "] Cooling off, totalDeductAmount > " + totalDeductAmount,Logg.VerbGroup.COOLDOWN_INSTANCE);
			
			// If power modifier ever reaches 0 or the cooldown reaches 0, reset cooldown back to 0
			if(powerModifier == 0f || lastCooldown == 0f)
			{
				resetCooldown();
			}
		}
	}
	
	/**
	 * Create debug clock to view cooldowns on the players action bar
	 */
	public void startDebugClock()
	{
		if(debugClock == null) { debugClock = new DebugClock(); }
		if(debugClock.isEnabled()) { return; }
		debugClock.start();
	}
	
	public void stopDebugClock()
	{
		if(debugClock == null || !debugClock.isEnabled()) { return; }
		debugClock.stop();
	}
	
	/**
	 * <p>Debug clock which is only in use when in debug mode
	 * 
	 * <p>Displays the current cooldown of the pearl/trident in seconds
	 */
	private class DebugClock extends RepeatingClock 
	{
		DecimalFormat df = new DecimalFormat("0.00");
		BossBar debugBar;
		
		public DebugClock()
		{
			super(type.toString().toLowerCase() + "_debug_clock",1);
		}
		
		@Override
		public void start()
		{
			if(clock != null && !clock.isCancelled()) { return; }
			run();
			
			if(type == CooldownType.ENDERPEARL)
			{
				debugBar = Bukkit.createBossBar("Enderpearl cooldown remaining (0.00)",BarColor.PURPLE,BarStyle.SOLID);
			}
			else if(type == CooldownType.TRIDENT)
			{
				debugBar = Bukkit.createBossBar("Trident cooldown remaining (0.00)",BarColor.BLUE,BarStyle.SOLID);
			}
			else
			{
				Logg.error("Unknown cooldown type! -> " + type.toString());
				return;
			}
			
			debugBar.setVisible(true);
			debugBar.addPlayer(owner);
		}
		
		@Override
		public void stop()
		{
			if(clock == null || clock.isCancelled()) { return; }
			clock.cancel();
			clock = null;
			debugBar.removeAll();
			debugBar.setVisible(false);
		}
		
		@Override
		public void execute() throws Exception
		{
			if(type == CooldownType.ENDERPEARL)
			{
				debugBar.setTitle("Enderpearl cooldown remaining (" + df.format(((float) owner.getCooldown(Material.ENDER_PEARL) / 20f)) + ")");
				debugBar.setProgress(MathUtils.clamp(0f,1f,(1f / lastCooldown) * ((float) owner.getCooldown(Material.ENDER_PEARL) / 20f)));
			}
			else if(type == CooldownType.TRIDENT)
			{
				debugBar.setTitle("Trident cooldown remaining (" + df.format(((float) owner.getCooldown(Material.TRIDENT) / 20f)) + ")");
				debugBar.setProgress(MathUtils.clamp(0f,1f,(1f / lastCooldown) * ((float) owner.getCooldown(Material.TRIDENT) / 20f)));
			}
		}
	}
	
	/**
	 * Set owner of this cooldown instance
	 * <p>Required when player leaves and re-joins the server.
	 * <p>Rejoining the server desyncs their player interface
	 * @param owner
	 */
	public void resync(Player owner)
	{
		this.owner = owner;
	}
	
	public float getPowerModifier()
	{
		return powerModifier;
	}

	public float getLastCooldown()
	{
		return lastCooldown;
	}
	
	/**
	 * <p>Should a player disconnect from the server, their cooldown for
	 * the enderpearl or trident will be reset to the vanilla default
	 * when they rejoin.
	 *
	 *<p>To prevent them bypassing this, the cooldown remaining is 'held'
	 * until they return or the combat encounter expires.
	 * 
	 * <p>If they return during the combat encounter, this held cooldown
	 * gets re-applied
	 */
	public abstract void holdCooldown();
	
	/**
	 * Applies the held cooldown
	 */
	public abstract void applyHeldCooldown();

	/**
	 * Dispose of this cooldown instance
	 */
	public void dispose()
	{
		if(cooloffClock != null && cooloffClock.isEnabled())
		{
			cooloffClock.stop();
		}
		
		if(debugClock != null && debugClock.isEnabled())
		{
			debugClock.stop();
		}
	}
	
	public enum CooldownType
	{
		ENDERPEARL,
		TRIDENT
	}
}
