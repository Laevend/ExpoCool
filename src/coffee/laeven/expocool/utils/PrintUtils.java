package coffee.laeven.expocool.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;

/**
 * 
 * @author Laeven
 * Handles printing messages to the player via chat, action bar, titles
 */
public class PrintUtils
{
	/**
	 * Send a raw message to a HumanEntity
	 * 
	 * <p>Raw messages have no colour translating, formatting, or MPOA message prefix</p>
	 * @param entity Entity to send message to
	 * @param message Message to send
	 */
	public static void raw(HumanEntity entity,String message)
	{
		entity.sendMessage(message);
	}
	
	/**
	 * Send a raw message to a CommandSender
	 * 
	 * <p>Raw messages have no colour translating, formatting, or MPOA message prefix</p>
	 * <p>Messages being sent to ConsoleCommandSender will appear as a raw log message</p>
	 * @param sender CommandSender to send message to
	 * @param message Message to send
	 */
	public static void raw(CommandSender sender,String message)
	{
		if(!(sender instanceof Player p)) { Logg.raw(message); return; }
		raw(p,message);
	}
	
	/**
	 * Send a raw message to a player
	 * 
	 * <p>Raw messages have no colour translating, formatting, or MPOA message prefix</p>
	 * @param player Player to send message to
	 * @param message Message to send
	 */
	public static void raw(Player player,String message)
	{
		player.sendMessage(message);
	}
	
	/**
	 * Send a raw message to all players on the server
	 * 
	 * <p>Raw messages have no colour translating, formatting, or MPOA message prefix</p>
	 * @param message Message to send
	 */
	public static void rawAll(String message)
	{
		Bukkit.getOnlinePlayers().forEach(player -> raw(player,message));
	}
	
	// # ========== #
	// # Info
	// # ========== #
	
	/**
	 * Send a message to a HumanEntity
	 * @param entity Entity to send message to
	 * @param message Message to send
	 */
	public static void info(HumanEntity entity,String message)
	{
		entity.sendMessage(ColourUtils.translate(Logg.PLUGIN_PREFIX + "&r " + message));
	}
	
	/**
	 * Send a message to a CommandSender
	 * 
	 * <p>Messages being sent to ConsoleCommandSender will appear as an INFO log</p>
	 * @param sender CommandSender to send message to
	 * @param message Message to send
	 */
	public static void info(CommandSender sender,String message)
	{
		if(!(sender instanceof Player p)) { Logg.info(message); return; }
		info(p,message);
	}
	
	/**
	 * Send a message to a player
	 * @param player Player to send message to
	 * @param message Message to send
	 */
	public static void info(Player player,String message)
	{
		player.sendMessage(ColourUtils.translate(Logg.PLUGIN_PREFIX + "&r&f " + message));
	}
	
	/**
	 * Send a message to all players on the server
	 * @param message Message to send
	 */
	public static void infoAll(String message)
	{
		Bukkit.getOnlinePlayers().forEach(player -> info(player,message));
	}
	
	// # ========== #
	// # Warn
	// # ========== #
	
	/**
	 * Send a message to a HumanEntity
	 * @param entity Entity to send message to
	 * @param message Message to send
	 */
	public static void warn(HumanEntity entity,String message)
	{
		entity.sendMessage(ColourUtils.translate(Logg.PLUGIN_PREFIX + "&r " + message));
	}
	
	/**
	 * Send a message to a CommandSender
	 * 
	 * <p>Messages being sent to ConsoleCommandSender will appear as an WARN log</p>
	 * @param sender CommandSender to send message to
	 * @param message Message to send
	 */
	public static void warn(CommandSender sender,String message)
	{
		if(!(sender instanceof Player p)) { Logg.warn(message); return; }
		warn(p,message);
	}
	
	/**
	 * Send a message to a player
	 * @param player Player to send message to
	 * @param message Message to send
	 */
	public static void warn(Player player,String message)
	{
		player.sendMessage(ColourUtils.translate(Logg.PLUGIN_PREFIX + "&r&e " + message));
	}
	
	/**
	 * Send a message to all players on the server
	 * @param message Message to send
	 */
	public static void warnAll(String message)
	{
		Bukkit.getOnlinePlayers().forEach(player -> warn(player,message));
	}
	
	// # ========== #
	// # Error
	// # ========== #
	
	/**
	 * Send a message to a HumanEntity
	 * @param entity Entity to send message to
	 * @param message Message to send
	 */
	public static void error(HumanEntity entity,String message)
	{
		entity.sendMessage(ColourUtils.translate(Logg.PLUGIN_PREFIX + "&r " + message));
	}
	
	/**
	 * Send a message to a CommandSender
	 * 
	 * <p>Messages being sent to ConsoleCommandSender will appear as an ERROR log</p>
	 * @param sender CommandSender to send message to
	 * @param message Message to send
	 */
	public static void error(CommandSender sender,String message)
	{
		if(!(sender instanceof Player p)) { Logg.error(message); return; }
		error(p,message);
	}
	
	/**
	 * Send a message to a player
	 * @param player Player to send message to
	 * @param message Message to send
	 */
	public static void error(Player player,String message)
	{
		player.sendMessage(ColourUtils.translate(Logg.PLUGIN_PREFIX + "&r&c " + message));
	}
	
	/**
	 * Send a message to all players on the server
	 * @param message Message to send
	 */
	public static void errorAll(String message)
	{
		Bukkit.getOnlinePlayers().forEach(player -> warn(player,message));
	}
	
	// # ========== #
	// # Success
	// # ========== #
	
	/**
	 * Send a message to a HumanEntity
	 * @param entity Entity to send message to
	 * @param message Message to send
	 */
	public static void success(HumanEntity entity,String message)
	{
		entity.sendMessage(ColourUtils.translate(Logg.PLUGIN_PREFIX + "&r " + message));
	}
	
	/**
	 * Send a message to a CommandSender
	 * 
	 * <p>Messages being sent to ConsoleCommandSender will appear as an SUCCESS log</p>
	 * @param sender CommandSender to send message to
	 * @param message Message to send
	 */
	public static void success(CommandSender sender,String message)
	{
		if(!(sender instanceof Player p)) { Logg.success(message); return; }
		success(p,message);
	}
	
	/**
	 * Send a message to a player
	 * @param player Player to send message to
	 * @param message Message to send
	 */
	public static void success(Player player,String message)
	{
		player.sendMessage(ColourUtils.translate(Logg.PLUGIN_PREFIX + "&r&a " + message));
	}
	
	/**
	 * Send a message to all players on the server
	 * @param message Message to send
	 */
	public static void successAll(String message)
	{
		Bukkit.getOnlinePlayers().forEach(player -> success(player,message));
	}
	
	// # ========== #
	// # Action Bar
	// # ========== #
	
	/**
	 * Send an action bar message to a HumanEntity (assuming they're a player otherwise nothing will send)
	 * @param entity Player to send message to
	 * @param message Message to send
	 */
	public static void actionBar(HumanEntity entity,String message)
	{
		if(!(entity instanceof Player p)) { return; }
		actionBar(p,message);
	}
	
	/**
	 * Send an action bar message to a CommandSender
	 * 
	 * <p>Messages being sent to ConsoleCommandSender will NOT appear! Use {@linkplain #sendMsg(CommandSender, String, MsgType)} instead
	 * @param sender CommandSender to send message to
	 * @param message Message to send
	 */
	public static void actionBar(CommandSender sender,String message)
	{
		if(!(sender instanceof Player p)) { return; }
		actionBar(p,message);
	}
	
	/**
	 * Send an action bar message to a player
	 * @param player Player to send message to
	 * @param message Message to send
	 */
	public static void actionBar(Player player,String message)
	{
		player.sendActionBar(Component.text(ColourUtils.translate(message)));
	}
	
	/**
	 * Send an action bar message to all players on the server
	 * @param message Message to send
	 */
	public static void actionBar(String message)
	{
		Bukkit.getOnlinePlayers().forEach(player -> actionBar(player,message));
	}
	
	/**
	 * Send a permission error message to a HumanEntity
	 * @param entity Entity to send message to
	 */
	public static void sendPermErr(HumanEntity entity)
	{
		if(!(entity instanceof Player p)) { return; }
		sendPermErr(p);
	}
	
	/**
	 * Send a permission error to a CommandSender
	 * 
	 * <p>Messages being sent to ConsoleCommandSender will not appear!</p>
	 * @param sender CommandSender to send message to
	 */
	public static void sendPermErr(CommandSender sender)
	{
		if(!(sender instanceof Player p)) { return; }
		sendPermErr(p);
	}
	
	/**
	 * Send a permission error to a player
	 * @param player Player to send message to
	 */
	public static void sendPermErr(Player player)
	{
		player.sendActionBar(Component.text(ColourUtils.translate(Logg.PLUGIN_PREFIX + "&r&c You lack permissions for this!")));
	}

}