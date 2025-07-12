package coffee.laeven.expocool.utils;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

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
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR,TextComponent.fromLegacy(ColourUtils.translate(message)));
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
		player.spigot().sendMessage(
				ChatMessageType.ACTION_BAR,
				TextComponent.fromLegacy(
						ColourUtils.translate(Logg.PLUGIN_PREFIX + "&r&c You lack permissions for this!")));
	}
	
	/**
	 * Send a text component to a HumanEntity
	 * @param entity Entity to send message to
	 * @param comp Components to send
	 */
	public static void sendComp(HumanEntity entity,BaseComponent... comp)
	{
		entity.spigot().sendMessage(comp);
	}
	
	/**
	 * Send a text component to a CommandSender
	 * 
	 * <p>Messages being sent to ConsoleCommandSender will not appear!</p>
	 * @param sender CommandSender to send message to
	 * @param comp Components to send
	 */
	public static void sendComp(CommandSender sender,BaseComponent... comp)
	{
		if(!(sender instanceof Player p)) { return; }
		sendPermErr(p);
	}
	
	/**
	 * Send a text component error to a player
	 * @param player Player to send message to
	 * @param comp Components to send
	 */
	public static void sendComp(Player player,BaseComponent... comp)
	{
		player.spigot().sendMessage(comp);
	}
	
	/**
	 * Send a text component to all players on the server
	 * @param comp Components to send
	 */
	public static void sendCompAll(BaseComponent... comp)
	{
		Bukkit.getOnlinePlayers().forEach(player -> player.spigot().sendMessage(comp));
	}
	
	/**
	 * Sends a title to the player using human entity
	 * @param entity The player
	 * @param title The title to send
	 * @param sub The sub title
	 */
	public static void sendTitle(HumanEntity entity,String title,String sub)
	{
		if(!(entity instanceof Player p)) { return; }
		sendTitle(p,title,sub);
	}
	
	/**
	 * Sends a title to the player using a command sender
	 * @param sender The player
	 * @param title The title to send
	 * @param sub The sub title
	 */
	public static void sendTitle(CommandSender sender,String title,String sub)
	{
		if(!(sender instanceof Player p)) { return; }
		sendTitle(p,title,sub);
	}
	
	/**
	 * Sends a title to the player using the default values for fadeIn, stay, and fadeOut as per documentation
	 * @param player The player
	 * @param title The title to send
	 * @param sub The sub title
	 */
	public static void sendTitle(Player player,String title,String sub)
	{
		player.sendTitle(ColourUtils.translate(title),ColourUtils.translate(sub),10,70,20);
	}
	
	/**
	 * Sends a title to all players on the server
	 * @param title The title to send
	 * @param sub The sub title
	 */
	public static void sendTitleToAll(String title,String sub)
	{
		Bukkit.getOnlinePlayers().forEach(player -> sendTitle(player,title,sub));
	}
	
	/**
	 * Sends a title to the player using human entity with fade values
	 * @param entity The player
	 * @param title The title to send
	 * @param sub The sub title
	 * @param fadeIn time in ticks for titles to fade in
	 * @param stay time in ticks for titles to stay
	 * @param fadeOut time in ticks for titles to fade out
	 */
	public static void sendTitle(HumanEntity entity,String title,String sub,int fadeIn,int stay,int fadeOut)
	{
		if(!(entity instanceof Player p)) { return; }
		sendTitle(p,title,sub,fadeIn,stay,fadeOut);
	}
	
	/**
	 * Sends a title to the player using a command sender with fade values
	 * @param sender The player
	 * @param title The title to send
	 * @param sub The sub title
	 * @param fadeIn time in ticks for titles to fade in
	 * @param stay time in ticks for titles to stay
	 * @param fadeOut time in ticks for titles to fade out
	 */
	public static void sendTitle(CommandSender sender,String title,String sub,int fadeIn,int stay,int fadeOut)
	{
		if(!(sender instanceof Player p)) { return; }
		sendTitle(p,title,sub,fadeIn,stay,fadeOut);
	}
	
	/**
	 * Sends a title to the player with fade values
	 * @param player The player
	 * @param title The title to send
	 * @param sub The sub title
	 * @param fadeIn time in ticks for titles to fade in
	 * @param stay time in ticks for titles to stay
	 * @param fadeOut time in ticks for titles to fade out
	 */
	public static void sendTitle(Player player,String title,String sub,int fadeIn,int stay,int fadeOut)
	{
		player.sendTitle(ColourUtils.translate(title),ColourUtils.translate(sub),fadeIn,stay,fadeOut);
	}
	
	/**
	 * Sends a title to all players on the server with fade values
	 * @param title The title to send
	 * @param sub The sub title
	 * @param fadeIn time in ticks for titles to fade in
	 * @param stay time in ticks for titles to stay
	 * @param fadeOut time in ticks for titles to fade out
	 */
	public static void sendTitleToAll(String title,String sub,int fadeIn,int stay,int fadeOut)
	{
		Bukkit.getOnlinePlayers().forEach(player -> sendTitle(player,title,sub,fadeIn,stay,fadeOut));
	}
	
	/**
	 * Clears the title being displayed for a player using human entity
	 * @param entity The player
	 */
	public static void clearTitle(HumanEntity entity)
	{
		if(!(entity instanceof Player p)) { return; }
		clearTitle(p);
	}
	
	/**
	 * Clears the title being displayed for a player using a command sender
	 * @param sender The player
	 */
	public static void clearTitle(CommandSender sender)
	{
		if(!(sender instanceof Player p)) { return; }
		clearTitle(p);
	}
	
	/**
	 * Clears the title being displayed for a player
	 * @param player The player
	 */
	public static void clearTitle(Player player)
	{
		player.resetTitle();
	}
	
	/**
	 * Clears the title being displayed for all players
	 */
	public static void clearTitleAll()
	{
		Bukkit.getOnlinePlayers().forEach(player -> clearTitle(player));
	}
	
	/**
	 * Based on how many 2's you can fit in 1 line of the chat bar (53) and then cutting a few off (50)
	 * This method will return a string that account for a titles space and display a title in chat
	 * neatly
	 * @param title The title
	 * @return A title string
	 */
	public static String getChatTitle(String title)
	{
		// New string builder
		StringBuilder sb = new StringBuilder();
		
		// Number of characters we have to use
		int space = 52;
		
		// the space we have left (-4 for the brackets and the space)
		int remainingSpace = (space - 4) - title.length();
		
		// The space at the sides of the title
		int spaceAtSides = remainingSpace / 2;
		
		for(int i = 0; i < spaceAtSides; i++)
		{
			sb.append(" ");
		}
		
		return ColourUtils.translate("&8&m" + sb.toString() + "&r&8< &6" + title + " &8>&8&m" + sb.toString() + "&r");
	}
	
	/**
	 * Builds a title which will float to the left of chat
	 * @param title The title
	 */
	public static void printChatTitleFloatLeft(CommandSender sender,String title)
	{		
		String titleCap = Character.toUpperCase(title.charAt(0)) + title.substring(1,title.length());
		
		raw(sender,"");
		raw(sender,ColourUtils.translate("&a" + titleCap + "&r"));
		raw(sender,getDivider());
	}
	
	/**
	 * Builds a title which will float to the left of chat
	 * @param title The title
	 */
	public static void printChatTitleFloatLeft(Player p,String title)
	{		
		String titleCap = Character.toUpperCase(title.charAt(0)) + title.substring(1,title.length());
		
		raw(p,"");
		raw(p,ColourUtils.translate("&a" + titleCap + "&r"));
		raw(p,getDivider());
	}
	
	/**
	 * Gets a black line that is used as a chat divider
	 * @return Chat divider
	 */
	public static String getDivider()
	{
		StringBuilder sb = new StringBuilder();
		
		// Number of characters we have to use (50 with character, 75 with no characters)
		int space = 75;
		
		sb.append("&8&m");
		
		for(int i = 0; i < space; i++)
		{
			sb.append(" ");
		}
		
		return ColourUtils.translate(sb.toString());
	}
	
	// Default chat width
	private final static int CENTER_PX = 154;
	private final static int CENTER_PX_MOTD = 135;
	
	/**
	 * Sends a message to a player that is center
	 * @param player Player to send message to
	 * @param message Msg
	 */
	public static void sendCenteredMessage(Player player,String message)
	{
        if(message == null || message.equals("")) { player.sendMessage(""); }
        
		message = ColourUtils.translate(message);
		
		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;
		
		for(char c : message.toCharArray())
		{
			if(c == ((char) 167))
			{
				previousCode = true;
				continue;
			}
			else if(previousCode == true)
			{
				previousCode = false;
				
				if(c == 'l' || c == 'L')
				{
	                isBold = true;
	                continue;
                }
				else
				{
					isBold = false;
				}
            }
			else
			{
	            DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
	            messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
	            messagePxSize++;
            }
		}
 
        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
                
        StringBuilder sb = new StringBuilder();
                
        while(compensated < toCompensate)
        {
        	sb.append(" ");
        	compensated += spaceLength;
        }

        player.sendMessage(sb.toString() + message);
	}
	
	public static String centerMotdMessage(String message)
	{
        if(message == null || message.equals("")) { return ""; }
        
		message = ColourUtils.translate(message);
		
		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;
		
		for(char c : message.toCharArray())
		{
			if(c == ((char) 167))
			{
				previousCode = true;
				continue;
			}
			else if(previousCode == true)
			{
				previousCode = false;
				
				if(c == 'l' || c == 'L')
				{
	                isBold = true;
	                continue;
                }
				else
				{
					isBold = false;
				}
            }
			else
			{
	            DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
	            messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
	            messagePxSize++;
            }
		}
 
        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX_MOTD - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
                
        StringBuilder sb = new StringBuilder();
                
        while(compensated < toCompensate)
        {
        	sb.append(" ");
        	compensated += spaceLength;
        }
        
        return sb.toString() + message + sb.toString();
	}
	
	public enum DefaultFontInfo
	{
		A('A', 5),
		a('a', 5),
		B('B', 5),
		b('b', 5),
		C('C', 5),
		c('c', 5),
		D('D', 5),
		d('d', 5),
		E('E', 5),
		e('e', 5),
		F('F', 5),
		f('f', 4),
		G('G', 5),
		g('g', 5),
		H('H', 5),
		h('h', 5),
		I('I', 3),
		i('i', 1),
		J('J', 5),
		j('j', 5),
		K('K', 5),
		k('k', 4),
		L('L', 5),
		l('l', 1),
		M('M', 5),
		m('m', 5),
		N('N', 5),
		n('n', 5),
		O('O', 5),
		o('o', 5),
		P('P', 5),
		p('p', 5),
		Q('Q', 5),
		q('q', 5),
		R('R', 5),
		r('r', 5),
		S('S', 5),
		s('s', 5),
		T('T', 5),
		t('t', 4),
		U('U', 5),
		u('u', 5),
		V('V', 5),
		v('v', 5),
		W('W', 5),
		w('w', 5),
		X('X', 5),
		x('x', 5),
		Y('Y', 5),
		y('y', 5),
		Z('Z', 5),
		z('z', 5),
		NUM_1('1', 5),
		NUM_2('2', 5),
		NUM_3('3', 5),
		NUM_4('4', 5),
		NUM_5('5', 5),
		NUM_6('6', 5),
		NUM_7('7', 5),
		NUM_8('8', 5),
		NUM_9('9', 5),
		NUM_0('0', 5),
		EXCLAMATION_POINT('!', 1),
		AT_SYMBOL('@', 6),
		NUM_SIGN('#', 5),
		DOLLAR_SIGN('$', 5),
		PERCENT('%', 5),
		UP_ARROW('^', 5),
		AMPERSAND('&', 5),
		ASTERISK('*', 5),
		LEFT_PARENTHESIS('(', 4),
		RIGHT_PERENTHESIS(')', 4),
		MINUS('-', 5),
		UNDERSCORE('_', 5),
		PLUS_SIGN('+', 5),
		EQUALS_SIGN('=', 5),
		LEFT_CURL_BRACE('{', 4),
		RIGHT_CURL_BRACE('}', 4),
		LEFT_BRACKET('[', 3),
		RIGHT_BRACKET(']', 3),
		COLON(':', 1),
		SEMI_COLON(';', 1),
		DOUBLE_QUOTE('"', 3),
		SINGLE_QUOTE('\'', 1),
		LEFT_ARROW('<', 4),
		RIGHT_ARROW('>', 4),
		QUESTION_MARK('?', 5),
		SLASH('/', 5),
		BACK_SLASH('\\', 5),
		LINE('|', 1),
		TILDE('~', 5),
		TICK('`', 2),
		PERIOD('.', 1),
		COMMA(',', 1),
		SPACE(' ', 3),
		DEFAULT('a', 4);
	 
		private char character;
		private int length;
	 
		DefaultFontInfo(char character, int length)
		{
			this.character = character;
			this.length = length;
		}
	 
		public char getCharacter()
		{
			return this.character;
		}
	 
		public int getLength()
		{
			return this.length;
		}
	 
		public int getBoldLength()
		{
			if(this == DefaultFontInfo.SPACE) { return this.getLength(); }
			return this.length + 1;
		}
	 
		public static DefaultFontInfo getDefaultFontInfo(char c)
		{
			for(DefaultFontInfo dfi : DefaultFontInfo.values())
			{
				if(dfi.getCharacter() == c) { return dfi; }
			}
			
			return DefaultFontInfo.DEFAULT;
		}
	}
	
	public static void sendAlert(Player player,String message)
	{
		printAlertDivider(player);
		PrintUtils.raw(player,ColourUtils.translate("&c" + message));
		printAlertDivider(player);
		player.playSound(player.getLocation(),Sound.ENTITY_BREEZE_DEATH,1.0f,1.0f);
	}
	
	public static void printAlertDivider(Player p)
	{
		PrintUtils.sendCenteredMessage(p,
				ColourUtils.translate("&4--------------------") +
				ColourUtils.translate("&r &8[") + 
				ColourUtils.translate("&c!") + 
				ColourUtils.translate("&8] &r") +
				ColourUtils.translate("&4--------------------"));
	}
}