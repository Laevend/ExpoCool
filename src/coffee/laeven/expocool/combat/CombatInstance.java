package coffee.laeven.expocool.combat;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import coffee.laeven.expocool.cooldown.CooldownCtrl;
import coffee.laeven.expocool.utils.MathUtils;
import coffee.laeven.expocool.utils.PrintUtils;
import coffee.laeven.expocool.utils.clocks.RefillableIntervalClock;
import coffee.laeven.expocool.utils.clocks.RepeatingClock;

public class CombatInstance
{
	private Player playerInCombat;
	private UUID playerUUID;
	private String name;
	private CombatClock combatClock;
	private DebugClock debugClock = null;
	
	public CombatInstance(Player p)
	{
		Objects.requireNonNull(p,"Player cannot be null!");
		this.playerInCombat = p;
		this.playerUUID = p.getUniqueId();
		this.name = p.getName();
		this.combatClock = new CombatClock();
		this.combatClock.start();
	}
	
	public void dispose()
	{
		if(combatClock.isEnabled()) { combatClock.stop(); }
		stopDebugClock();
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
	
	public CombatClock getCombatClock()
	{
		return combatClock;
	}

	public DebugClock getDebugClock()
	{
		return debugClock;
	}
	
	/**
	 * Set owner of this cooldown instance
	 * <p>Required when player leaves and re-joins the server.
	 * <p>Rejoining the server desyncs their player interface
	 * @param owner
	 */
	public void resync(Player owner)
	{
		this.playerInCombat = owner;
	}

	public class CombatClock extends RefillableIntervalClock
	{
		public CombatClock()
		{
			super(name + "_combat_clock",(long) (CombatCtrl.Config.IN_COMBAT_ELAPSED_TIME_TO_LEAVE_COMBAT.get() * 20));
			
			if(CooldownCtrl.isInDebugMode(playerInCombat))
			{
				PrintUtils.actionBar(playerInCombat,"&cYou are now in combat!");
			}
		}

		@Override
		public void execute() throws Exception
		{
			CombatCtrl.tagPlayerAsOutOfCombat(playerUUID);
		}

		public Player getPlayerInCombat()
		{
			return playerInCombat;
		}
	}
	
	private class DebugClock extends RepeatingClock 
	{
		DecimalFormat df = new DecimalFormat("0.00");
		float timeUntillCombatExpires = CombatCtrl.Config.IN_COMBAT_ELAPSED_TIME_TO_LEAVE_COMBAT.get();
		BossBar combatLeaveCountdown;
		BossBar combatTime;
		
		public DebugClock()
		{
			super(name + "_combat_debug_clock",1);
		}
		
		@Override
		public void start()
		{
			if(clock != null && !clock.isCancelled()) { return; }
			run();
			
			combatLeaveCountdown = Bukkit.createBossBar("Time combat expires in (" + df.format(((float) combatClock.getInterval()) / 20f) + ")",BarColor.RED,BarStyle.SOLID);
			combatTime = Bukkit.createBossBar("Time in combat (" + df.format(((float) combatClock.getElapsedInterval()) / 20f) + ")",BarColor.YELLOW,BarStyle.SOLID);
			combatTime.setProgress(1.0d);
			
			combatLeaveCountdown.setVisible(true);
			combatLeaveCountdown.addPlayer(playerInCombat);
			combatTime.setVisible(true);
			combatTime.addPlayer(playerInCombat);
		}
		
		@Override
		public void stop()
		{
			if(clock == null || clock.isCancelled()) { return; }
			clock.cancel();
			clock = null;
			
			combatLeaveCountdown.removeAll();
			combatLeaveCountdown.setVisible(false);
			combatTime.removeAll();
			combatTime.setVisible(false);
		}
		
		@Override
		public void execute() throws Exception
		{
			combatLeaveCountdown.setTitle("Time combat expires in (" + df.format(((float) combatClock.getInterval()) / 20f) + ")");
			combatTime.setTitle("Time in combat (" + df.format(((float) combatClock.getElapsedInterval()) / 20f) + ")");
			
			float maxCombatTimeInTicks = timeUntillCombatExpires * 20;
			
			combatLeaveCountdown.setProgress(MathUtils.clamp(0.0,1.0,(1f / maxCombatTimeInTicks) * ((float) combatClock.getInterval())));
		}
	}
}
