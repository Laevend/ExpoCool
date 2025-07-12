package coffee.laeven.expocool.cooldown;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import coffee.laeven.expocool.utils.DelayUtils;
import coffee.laeven.expocool.utils.Logg;

/**
 * Represents an instance of an exponential enderpearl cooldown
 */
public class EnderpearlCooldownInstance extends CooldownInstance
{
	public EnderpearlCooldownInstance(Player p)
	{
		super(p,CooldownType.ENDERPEARL);
		
		baseMultiplier = CooldownCtrl.Config.ENDERPEARL_BASE_MULTIPLIER.get();
		baseCooldown = CooldownCtrl.Config.ENDERPEARL_BASE_COOLDOWN.get();
		basePower = CooldownCtrl.Config.ENDERPEARL_BASE_POWER.get();
		
		powerModifierAmount = CooldownCtrl.Config.ENDERPEARL_POWER_INCREMENT.get();
		minCooldown = CooldownCtrl.Config.ENDERPEARL_MIN_COOLDOWN.get();
		maxCooldown = CooldownCtrl.Config.ENDERPEARL_MAX_COOLDOWN.get();
		
		deductDelay = CooldownCtrl.Config.ENDERPEARL_COOLDOWN_REDUCTION_DELAY.get();
		deductAmount = CooldownCtrl.Config.ENDERPEARL_COOLDOWN_REDUCTION_AMOUNT.get();
		
		long deductDelayInTicks = (long) (deductDelay * 20f);
		cooloffClock = new CooloffClock(deductDelayInTicks);
		cooloffClock.start();
		
		heldCooldown = 1;
	}

	@Override
	public void setNewCooldown(int newCooldownInTicks)
	{
		// Delay setting cooldown by 1 tick (50ms) to stop vanilla Minecraft overriding our custom cooldown
		DelayUtils.executeDelayedTask(() ->
		{
			owner.setCooldown(Material.ENDER_PEARL,newCooldownInTicks);
		});
	}

	@Override
	public void removeIfPlayerIsOffline()
	{
		Logg.verb("Is player offline?",Logg.VerbGroup.COOLDOWN_INSTANCE);
		if(owner.isOnline()) { return; }
		CooldownCtrl.removeEnderpearlCooldownInstance(owner.getUniqueId());
		Logg.verb("Player is offline, removing instance...",Logg.VerbGroup.COOLDOWN_INSTANCE);
	}

	@Override
	public void holdCooldown()
	{
		this.heldCooldown = owner.getCooldown(Material.ENDER_PEARL);
	}

	@Override
	public void applyHeldCooldown()
	{
		DelayUtils.executeDelayedTask(() ->
		{
			owner.setCooldown(Material.ENDER_PEARL,heldCooldown);
			heldCooldown = 1;
		});
	}
}
