package coffee.laeven.expocool;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.codehaus.plexus.interpolation.util.StringUtils;

import coffee.laeven.expocool.combat.CombatCtrl;
import coffee.laeven.expocool.commands.ExpoCoolCommand;
import coffee.laeven.expocool.config.Configurable;
import coffee.laeven.expocool.config.PluginConfig;
import coffee.laeven.expocool.config.YamlConfig;
import coffee.laeven.expocool.config.item.ConfigItem;
import coffee.laeven.expocool.cooldown.CooldownCtrl;
import coffee.laeven.expocool.utils.ColourUtils;
import coffee.laeven.expocool.utils.Logg;
import coffee.laeven.expocool.utils.structs.Namespace;
import coffee.laeven.expocool.utils.tools.ClasspathCollector;

/**
 * Exponential Cooldown
 * 
 * @author Laeven
 */
public class ExpoCool extends JavaPlugin
{
	private static ExpoCool INSTANCE;
	private static Path PLUGIN_PATH;
	private static final String NAMESPACE_NAME = "EXPO_COOL";
	
	private static PluginConfig config = null;
	
	private static long initStartTime = 0;
	private static long initEndTime = 0;
	
	/* =================== *
	 *   Pre-Bukkit Load
	 * =================== */
	
	@Override
	public void onEnable()
	{
		initStartTime = System.currentTimeMillis();
		INSTANCE = this;
		PLUGIN_PATH = Path.of(getFile().getAbsolutePath());
		createConfig();
		printHeader();
		
		configureLogger();
		
		ExpoCoolCommand expoCommand = new ExpoCoolCommand();
		
		this.getCommand("expocool").setExecutor(expoCommand);
		this.getCommand("expocool").setTabCompleter(expoCommand);
		
		initEndTime = System.currentTimeMillis();
		
		Logg.info("&f&eExpoCool Pre-Bukkit load initialised in " + ((initEndTime - initStartTime) / 1000F) + " seconds");
		
		Bukkit.getPluginManager().registerEvents(new CooldownCtrl(),this);
		Bukkit.getPluginManager().registerEvents(new CombatCtrl(),this);
	}
	
	@Override
	public void onDisable()
	{
		CooldownCtrl.clearInstances();
		CombatCtrl.clearInstances();
	}
	
	private void createConfig()
	{
		List<ConfigItem<?>> defaults = new ArrayList<>();
		
		Logg.title("Collecting Default Configuration Values...");
		
		try
		{
			ClasspathCollector collector = new ClasspathCollector(PLUGIN_PATH,ExpoCool.class.getClassLoader());
			Set<String> configurableClasses = collector.getClasspathsAssignableFrom(Configurable.class);
			
			for(String clazz : configurableClasses)
			{
				Class<?> configurableClass = Class.forName(clazz,false,ExpoCool.class.getClassLoader());
				String className;
				
				/**
				 * It's common practice of config items going in a nested 'Config' class.
				 * 
				 * This makes it easier to access from an IDE when typing and this method spends less
				 * time combing through fields that are not of ConfigItem type.
				 * 
				 * Unfortunately it's not useful to have every configuration class called 'config' in the logs.
				 * 
				 * So we attempt to get the nested class name instead.
				 */
				if(configurableClass.getSimpleName().equals("Config"))
				{
					String[] canonicalNameSplit = configurableClass.getCanonicalName().split("[.]");
					className = StringUtils.capitalizeFirstLetter(canonicalNameSplit[canonicalNameSplit.length - 2]);
				}
				else
				{
					className = configurableClass.getSimpleName() + " S I M P L E";
				}
				
				for(Field field : configurableClass.getDeclaredFields())
				{
					// Ignore fields that are not of ConfigItem
					if(!field.getType().equals(ConfigItem.class)) { continue; }
					
					try
					{
						ConfigItem<?> configItem = (ConfigItem<?>) field.get(null);
						defaults.add(configItem);
						Logg.Common.printOk(Logg.Common.Component.CONFIG,"Collecting","(" + className + ") " + configItem.getKey());
					}
					catch(Exception e)
					{
						Logg.error("Configurable class " + configurableClass.getSimpleName());
						Logg.Common.printFail(Logg.Common.Component.CONFIG,"Collecting","(" + className + ") " + "???");
					}
				}
			}
		}
		catch (Exception e)
		{
			Logg.fatal("Configurables could not be initialised!",e);
		}
		
		config = new YamlConfig(internalFilePath("config.yml"),defaults);
	}
	
	private void configureLogger()
	{
		try
		{
			// Register all verbose groups
			for(Field f : Logg.VerbGroup.class.getDeclaredFields())
			{
				Namespace verboseGroup = (Namespace) f.get(null);
				Logg.registerVerboseLogGroup(verboseGroup);
			}
		}
		catch(Exception e)
		{
			Logg.error("Error occured attempting to register logger verbose groups!",e);
		}
		
		Logg.setHideVerbose(Logg.Config.HIDE_VERBOSE.get());
		Logg.setHideWarnings(Logg.Config.HIDE_WARNINGS.get());
		Logg.setHideErrors(Logg.Config.HIDE_ERRORS.get());
		Logg.setHideFatals(Logg.Config.HIDE_FATALS.get());
	}
	
	/**
	 * Used to shutdown the server in times when the server is left in a state that cannot be recovered
	 * Shutting down the server prevents further data degradation and unpredictable server states
	 */
	public static void forceShutdown()
	{
		Logg.error("A fatal error has occured. The server will be forcefully shutdown to prevent further damage.");
		Bukkit.getServer().shutdown();
	}
	
	/**
	 * Used to shutdown the server in times when the server is left in a state that cannot be recovered
	 * Shutting down the server prevents further data degradation and unpredictable server states
	 */
	public static void forceShutdown(String reason)
	{
		Logg.error("The server is being forcefully shutdown. Reason: " + reason);
		Bukkit.getServer().shutdown();
	}
	
	/**
	 * Prints header
	 */
	private final void printHeader()
	{
		// String builder necessary to create a single string otherwise the logger prints the time for each line
		StringBuilder sb = new StringBuilder();
		
		sb.append("\r" + String.format("%" + 400 + "s", "") + "\n");
		
		for(String s : logo)
		{
			sb.append(s);
		}
		
		sb.append("\n");
		
		Logg.raw(ColourUtils.translate(sb.toString()));
	}
	
	public static final ExpoCool instance()
	{
		return INSTANCE;
	}
	
	/**
	 * Returns an internal file path for dapes plugin data folder with an appended directory path
	 * @param path Appended directory path starting from ./plugins/Dape/
	 * @return Path of directory or file internal to dapes plugin data directory
	 */
	public static Path internalFilePath(String path)
	{
		Path p = Paths.get(ExpoCool.instance().getDataFolder().getPath() + File.separator + path);		
		return p; 
	}
	
	public static final Path getPluginPath()
	{
		return PLUGIN_PATH;
	}
	
	public static String getNamespaceName()
	{
		return NAMESPACE_NAME;
	}
	
	public static NamespacedKey getNamespacedKey()
	{
		return new NamespacedKey(ExpoCool.instance(),getNamespaceName());
	}

	public static int getMajorVersion()
	{
		String major = INSTANCE.getDescription().getVersion().split("[.]")[0];
		return major.length() == 0 ? 0 : Integer.parseInt(major);
	}

	public static int getMinorVersion()
	{
		String minor = INSTANCE.getDescription().getVersion().split("[.]")[1];
		return minor.length() == 0 ? 0 : Integer.parseInt(minor);
	}

	public static int getRevision()
	{
		String patch = INSTANCE.getDescription().getVersion().split("[.]")[2];
		return patch.length() == 0 ? 0 : Integer.parseInt(patch);
	}
	
	public static int getHotfix()
	{
		String hotfix = INSTANCE.getDescription().getVersion().split("[.]")[3];
		return hotfix.length() == 0 ? 0 : Integer.parseInt(hotfix);
	}

	public static String getVersion()
	{
		return INSTANCE.getDescription().getVersion();
	}
	
	public static PluginConfig getConfigFile()
	{
		return config;
	}
	
	@Override
	public void saveConfig()
	{
		config.saveConfig();
	}
	
	public void reloadConfig()
	{		
		config.reloadConfig();
	}
	
	@Override
	public void saveDefaultConfig()
	{
		saveConfig();
	}
	
	@Override
	public FileConfiguration getConfig()
	{
		throw new UnsupportedOperationException("The default configuration is not supported! Please use '" + this.getClass().getSimpleName() + ".getConfigFile()");
	}

	private final String[] logo = new String[]
	{
		"&r\r\n",
		"          &8[" + ColourUtils.applyColour("Exponential Cooldown",ColourUtils.TEXT) + "&8]&r\r\n",
		"&r\r\n",
		"          &9Version &8> &e" + getDescription().getVersion() + "&r\r\n",
		"          &9Message Prefix &8> " + ColourUtils.applyColour(Logg.PLUGIN_PREFIX,ColourUtils.TEXT) + "&r\r\n",
		"          &9Spigot API &8> &e" + getDescription().getAPIVersion() + "&r\r\n",
		"          &9Contributors &8> &8[&e" + String.join(",",getDescription().getAuthors()) + "&8]&r\r\n",
		"          &9Bukkit Ver &8> &e" + Bukkit.getBukkitVersion() + "&r\r\n",
		"&r\r\n"
	};	
}
