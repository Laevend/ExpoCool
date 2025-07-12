package coffee.laeven.expocool.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import coffee.laeven.expocool.ExpoCool;
import coffee.laeven.expocool.cooldown.CooldownCtrl;
import coffee.laeven.expocool.utils.Logg;
import coffee.laeven.expocool.utils.PrintUtils;
import coffee.laeven.expocool.utils.structs.TabTree;
import coffee.laeven.expocool.utils.structs.TabTree.Node;

public class ExpoCoolCommand extends BaseCommand
{
	private static final String TOGGLE_VERBOSE_MESSAGES = "toggle-verbose-messages";
	private static final String TOGGLE_DEBUG = "toggle-debug";
	private static final String RELOAD_CONFIG = "reload-config";

	// ec toggle-verbose-messages
	
	// ec toggle-cooldown-viewing
	
	// ec reload-config
	
	public void onCommand(CommandSender sender,String[] args)
	{
		if(args.length < 1)
		{
			PrintUtils.error(sender,"Not enough arguments!");
			return;
		}
		
		if(!assertArgument(args[0],TOGGLE_VERBOSE_MESSAGES,TOGGLE_DEBUG))
		{
			PrintUtils.error(sender,"Bad arguments!");
			return;
		}
		
		switch(args[0])
		{
			case TOGGLE_VERBOSE_MESSAGES -> toggleVerboseMessages(sender);
			case TOGGLE_DEBUG -> toggleDebug(sender);
			case RELOAD_CONFIG -> reloadConfig(sender);
		}
	}
	
	private void toggleVerboseMessages(CommandSender sender)
	{
		Logg.setHideVerbose(Logg.isHideVerbose() ? false : true);
		
		Logg.Config.HIDE_VERBOSE.set(Logg.isHideVerbose());
		ExpoCool.getConfigFile().saveConfig();
		
		if(Logg.isHideVerbose())
		{
			PrintUtils.info(sender,"Verbose messages are now hidden.");
			return;
		}
		
		PrintUtils.info(sender,"Verbose messages are now shown.");
	}
	
	private void toggleDebug(CommandSender sender)
	{
		if(!(sender instanceof Player p)) { PrintUtils.error(sender,"Cannot call this command from console or command block!"); return; }
		CooldownCtrl.setDebugMode(p,CooldownCtrl.isInDebugMode(p) ? false : true);
		
		if(CooldownCtrl.isInDebugMode(p))
		{
			PrintUtils.info(sender,"You are now in debug mode.");
			return;
		}
		
		PrintUtils.info(sender,"You are no longer in debug mode.");
	}
	
	private void reloadConfig(CommandSender sender)
	{
		PrintUtils.info(sender,"Reloading config...");
		ExpoCool.instance().reloadConfig();
	}
	
	private static TabTree tree = new TabTree();
	
	public ExpoCoolCommand()
	{
		tree.getRoot().addBranch(TOGGLE_VERBOSE_MESSAGES);
		tree.getRoot().addBranch(TOGGLE_DEBUG);
		tree.getRoot().addBranch(RELOAD_CONFIG);
	}
	
	@Override
	public List<String> onTab(CommandSender sender,String[] args)
	{
		Node nextNode = tree.getRoot();
		if(args == null || args.length == 1) { return new ArrayList<>(nextNode.branches.keySet()); }
		return Collections.emptyList();
	}
}
