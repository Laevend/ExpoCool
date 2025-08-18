package coffee.laeven.expocool.cooldown;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.UUID;

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
	protected UUID owner;
	protected String name;
	
	protected float multiplier = 1.2f;						// Base multiplier {cooldown.enderpearl.multiplier}
	protected float base = 0.5f;							// Base cooldown. {cooldown.enderpearl.base}
	
	protected float minCooldown = 1;						// Minimum cooldown (clamped). {cooldown.enderpearl.min_cooldown}
	protected float maxCooldown = 15;						// Maximum cooldown (clamped). {cooldown.enderpearl.max_cooldown}
	
	protected float deductDelay = 0f;						// Delay before a deduction in the cooldown. {cooldown.enderpearl.cooldown_deduct_delay}
	protected float deductAmount = 0f;						// Deduct amount. {cooldown.enderpearl.cooldown_deduct_amount}
	private boolean cooloffTriggered = false;				// When a player waits the alloted time for a reduction in cooldown this is set to true and prevents a multiply for the next cooldown
	
	protected CooloffClock cooloffClock = null;
	protected DebugClock debugClock = null;
	
	protected float lastCooldown = 0f;						// Used for debug viewing
	protected float nextCooldown = 0f;						// Next cooldown to be set
	protected int heldCooldownInTicks = 0;					/** Cooldown held if player disconnects. {@link #holdCooldown()} */
	
	protected CooldownType type;
	
	public CooldownInstance(Player p,CooldownType type)
	{
		Objects.requireNonNull(p,"Player cannot be null!");
		Objects.requireNonNull(type,"CooldownType cannot be null!");
		this.owner = p.getUniqueId();
		this.name = p.getName();
		this.type = type;
	}
	
	/**
	 * Called when an enderpearl or a riptide trident is used
	 */
	public void triggerItemUse()
	{
		calculateNewCooldown();
		cooloffClock.refill();
	}
	
	/**
	 * Calculates new cooldown result
	 */
	public void calculateNewCooldown()
	{
		// Multiply last cooldown by multiply amount (unless a cooloff was triggered)
		float newCooldown = MathUtils.clamp(minCooldown,maxCooldown,(cooloffTriggered ? nextCooldown * 1f : nextCooldown * multiplier));
		cooloffTriggered = false;
		
		// Convert cooldown in seconds to game ticks
		int newCooldownInTicks = (int) (newCooldown * 20);
		setNewCooldown(newCooldownInTicks);
		
		Logg.verb("(" + name + ") [" + type.toString().toLowerCase() + "] Last cooldown > " + lastCooldown,Logg.VerbGroup.COOLDOWN_INSTANCE);
		Logg.verb("(" + name + ") [" + type.toString().toLowerCase() + "] New cooldown > " + newCooldown,Logg.VerbGroup.COOLDOWN_INSTANCE);
		Logg.verb("(" + name + ") [" + type.toString().toLowerCase() + "] New cooldown (in ticks) > " + newCooldownInTicks,Logg.VerbGroup.COOLDOWN_INSTANCE);
		
		lastCooldown = newCooldown;
		nextCooldown = lastCooldown;
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
		nextCooldown = base;
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
			
			Logg.verb("(" + name + ") [" + type.toString().toLowerCase() + "] Cooling off, Delay (in secs) > " + deductAmount,Logg.VerbGroup.COOLDOWN_INSTANCE);
			Logg.verb("(" + name + ") [" + type.toString().toLowerCase() + "] Cooling off, Delay (in ticks) > " + deductDelay,Logg.VerbGroup.COOLDOWN_INSTANCE);
		}

		@Override
		public void execute() throws Exception
		{
			// Refill clock to begin counting down again
			refill();
			
			// No point deducting more than the max cooldown
			nextCooldown = MathUtils.clamp(0,maxCooldown,(nextCooldown - deductAmount));
			cooloffTriggered = true;
			
			Logg.verb("(" + name + ") [" + type.toString().toLowerCase() + "] Cooling off, Next cooldown > " + nextCooldown,Logg.VerbGroup.COOLDOWN_INSTANCE);
			Logg.verb("(" + name + ") [" + type.toString().toLowerCase() + "] Cooling off, Deduct amount > " + deductAmount,Logg.VerbGroup.COOLDOWN_INSTANCE);
			
			// If power modifier ever reaches 0 or the cooldown reaches 0, reset cooldown back to 0
			if(nextCooldown == 0f)
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
			debugBar.addPlayer(getPlayer());
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
				debugBar.setTitle("Enderpearl cooldown remaining (" + df.format(((float) getPlayer().getCooldown(Material.ENDER_PEARL) / 20f)) + ")");
				debugBar.setProgress(MathUtils.clamp(0f,1f,(1f / lastCooldown) * ((float) getPlayer().getCooldown(Material.ENDER_PEARL) / 20f)));
			}
			else if(type == CooldownType.TRIDENT)
			{
				debugBar.setTitle("Trident cooldown remaining (" + df.format(((float) getPlayer().getCooldown(Material.TRIDENT) / 20f)) + ")");
				debugBar.setProgress(MathUtils.clamp(0f,1f,(1f / lastCooldown) * ((float) getPlayer().getCooldown(Material.TRIDENT) / 20f)));
			}
		}
	}
	
	public Player getPlayer()
	{
		return Bukkit.getPlayer(owner);
	}
	
	public boolean isOnline()
	{
		return Bukkit.getPlayer(owner) != null;
	}

	public float getLastCooldown()
	{
		return lastCooldown;
	}
	
	public float getNextCooldown()
	{
		return nextCooldown;
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
			cooloffClock = null;
		}
		
		if(debugClock != null && debugClock.isEnabled())
		{
			debugClock.stop();
			debugClock = null;
		}
	}
	
	public enum CooldownType
	{
		ENDERPEARL,
		TRIDENT
	}
}
