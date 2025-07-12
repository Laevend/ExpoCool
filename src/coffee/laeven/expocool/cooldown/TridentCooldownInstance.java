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
		
		baseMultiplier = CooldownCtrl.Config.TRIDENT_BASE_MULTIPLIER.get();
		baseCooldown = CooldownCtrl.Config.TRIDENT_BASE_COOLDOWN.get();
		basePower = CooldownCtrl.Config.TRIDENT_BASE_POWER.get();
		
		powerModifierAmount = CooldownCtrl.Config.TRIDENT_POWER_INCREMENT.get();
		minCooldown = CooldownCtrl.Config.TRIDENT_MIN_COOLDOWN.get();
		maxCooldown = CooldownCtrl.Config.TRIDENT_MAX_COOLDOWN.get();
		
		deductDelay = CooldownCtrl.Config.TRIDENT_COOLDOWN_REDUCTION_DELAY.get();
		deductAmount = CooldownCtrl.Config.TRIDENT_COOLDOWN_REDUCTION_AMOUNT.get();
		
		long deductDelayInTicks = (long) (deductDelay * 20f);
		cooloffClock = new CooloffClock(deductDelayInTicks);
		cooloffClock.start();
		
		heldCooldown = 0;
	}

	@Override
	public void setNewCooldown(int newCooldownInTicks)
	{
		// Delay setting cooldown by 1 tick (50ms) to stop vanilla Minecraft overriding our custom cooldown
		DelayUtils.executeDelayedTask(() ->
		{
			owner.setCooldown(Material.TRIDENT,newCooldownInTicks);
		});
	}
	
	@Override
	public void removeIfPlayerIsOffline()
	{
		Logg.verb("Is player offline?",Logg.VerbGroup.COOLDOWN_INSTANCE);
		if(owner.isOnline()) { return; }
		CooldownCtrl.removeTridentCooldownInstance(owner.getUniqueId());
		Logg.verb("Player is offline, removing instance...",Logg.VerbGroup.COOLDOWN_INSTANCE);
	}
	
	@Override
	public void holdCooldown()
	{
		this.heldCooldown = owner.getCooldown(Material.TRIDENT);
	}

	@Override
	public void applyHeldCooldown()
	{
		DelayUtils.executeDelayedTask(() ->
		{
			owner.setCooldown(Material.TRIDENT,heldCooldown);
			heldCooldown = 0;
		});
	}
}
