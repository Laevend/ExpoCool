package coffee.laeven.expocool.cooldown;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import coffee.laeven.expocool.utils.DelayUtils;
import coffee.laeven.expocool.utils.Logg;

/**
 * Represents an instance of an exponential enderpearl cooldown
 */
public class TridentCooldownInstance extends CooldownInstance
{
	public TridentCooldownInstance(Player p)
	{
		super(p,CooldownType.TRIDENT);
		
		multiplier = CooldownCtrl.Config.TRIDENT_MULTIPLIER.get();
		base = CooldownCtrl.Config.TRIDENT_BASE.get();
		
		minCooldown = CooldownCtrl.Config.ENDERPEARL_MIN_COOLDOWN.get();
		maxCooldown = CooldownCtrl.Config.ENDERPEARL_MAX_COOLDOWN.get();
		
		deductDelay = CooldownCtrl.Config.TRIDENT_COOLDOWN_REDUCTION_DELAY.get();
		deductAmount = CooldownCtrl.Config.TRIDENT_COOLDOWN_REDUCTION_AMOUNT.get();
		
		long deductDelayInTicks = (long) (deductDelay * 20f);
		cooloffClock = new CooloffClock(deductDelayInTicks);
		cooloffClock.start();
		
		heldCooldownInTicks = 1;
		nextCooldown = minCooldown;
	}

	@Override
	public void setNewCooldown(int newCooldownInTicks)
	{
		// Delay setting cooldown by 1 tick (50ms) to stop vanilla Minecraft overriding our custom cooldown
		DelayUtils.executeDelayedTask(() ->
		{
			getPlayer().setCooldown(Material.TRIDENT,newCooldownInTicks);
		});
	}
	
	@Override
	public void removeIfPlayerIsOffline()
	{
		Logg.verb("Is player offline?",Logg.VerbGroup.COOLDOWN_INSTANCE);
		if(isOnline()) { return; }
		CooldownCtrl.removeTridentCooldownInstance(owner);
		Logg.verb("Player is offline, removing instance...",Logg.VerbGroup.COOLDOWN_INSTANCE);
	}
	
	@Override
	public void holdCooldown()
	{
		this.heldCooldownInTicks = getPlayer().getCooldown(Material.TRIDENT);
	}

	@Override
	public void applyHeldCooldown()
	{
		DelayUtils.executeDelayedTask(() ->
		{
			getPlayer().setCooldown(Material.TRIDENT,heldCooldownInTicks);
			heldCooldownInTicks = 0;
		});
	}
}
