package coffee.laeven.expocool.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import coffee.laeven.expocool.ExpoCool;
import coffee.laeven.expocool.combat.CombatCtrl;
import coffee.laeven.expocool.config.item.ConfigItem;
import coffee.laeven.expocool.cooldown.CooldownCtrl;
import coffee.laeven.expocool.utils.FUtils;
import coffee.laeven.expocool.utils.Logg;
import coffee.laeven.expocool.utils.TimeUtils;

/**
 * 
 * @author Laeven
 * Implements YAML manually to allow for comments
 */
public class YamlConfig implements PluginConfig
{
	private FileConfiguration config;
	private List<ConfigItem<?>> defaults;
	private Path configFile;
	private List<String> header;
	private boolean loaded;
	
	private int maxCorruptReplaceAttempts = 5;
	private int corruptReplaceAttempts = 0;
	
	private static final String CORRUPT_CONFIG_DIR = "corrupt_configs";
	
	/**
	 * Create a new configuration file object
	 * @param configFile Configuration file
	 * @param defaults Default values for this configuration file
	 * @param header The header comment of the configuration file
	 */
	public YamlConfig(Path configFile,List<ConfigItem<?>> defaults)
	{
		this.configFile = configFile;
		this.defaults = defaults;
		this.header = getHeader();
		
		if(Files.exists(configFile))
		{ 
			load();
			setDefaults();
			save();
			return;
		}
		
		firstTimeSetup();
	}
	
	public void dddd()
	{
		//config.comm
	}
	
	/**
	 * Loads configuration file that already exists
	 */
	public void load()
	{
		if(this.configFile == null)
		{
			Logg.fatal("Cannot load configuration file as path is null!");
			ExpoCool.forceShutdown();
		}
		
		this.config = YamlConfiguration.loadConfiguration(configFile.toFile());
		
		/**
		 * MD_5 back at it again with soft silencing an exception internally instead of
		 * letting plugin devs handle it smh.
		 * 
		 * This checks if a blank YAML was returned which is what happens if loading fails.
		 */
		if(this.config.saveToString().replace('\n',' ').replace(" ","").length() == 0)
		{
			Logg.error("Error! Configuration file could not be parsed/read correctly!");
			replaceCorruptedConfig();
		}
		else
		{
			this.loaded = true;
		}
	}
	
	/**
	 * In the event the configuration file cannot be read, it is moved to a corrupt files directory (if the user later wishes to consult it).
	 * A new configuration file is generated in its place to allow the server to continue functioning
	 */
	private void replaceCorruptedConfig()
	{
		// Prevents the server from continually attempting to replace the corrupt file in the event it keeps failing
		if((corruptReplaceAttempts++) >= maxCorruptReplaceAttempts)
		{
			Logg.fatal("Maximum corrupt configuration file replacement attempts reached!");
			ExpoCool.forceShutdown();
			return;
		}
		
		corruptReplaceAttempts++;
		
		String fileName = "corrupt_dape_config_" + TimeUtils.getDateFormat(TimeUtils.PATTERN_DASH_dd_MM_yy);
		Path corruptConfigPath = ExpoCool.internalFilePath(CORRUPT_CONFIG_DIR);
		
		FUtils.createDirectories(corruptConfigPath);
		
		int extraNumber = 1;
		
		// In the event (somehow) more than configuration file corrupts in the same second.
		while(Files.exists(Paths.get(corruptConfigPath.toAbsolutePath().toString() + File.separator + fileName)))
		{
			fileName = "corrupt_dape_config_" + TimeUtils.getDateFormat(TimeUtils.PATTERN_DASH_dd_MM_yy) + " (" + extraNumber + ")";
			extraNumber++;
		}
		
		Path finalPath = ExpoCool.internalFilePath(CORRUPT_CONFIG_DIR + File.separator + fileName + ".yml");
		FUtils.copyFile(configFile,finalPath);
		
		if(!Files.exists(finalPath))
		{
			Logg.fatal("Corrupted configuration file '" + configFile.toString() + "' could not be moved to directory '" + corruptConfigPath.toAbsolutePath().toString() + "'!");
			ExpoCool.forceShutdown();
			return;
		}
		
		FUtils.delete(configFile);
		
		if(Files.exists(configFile))
		{
			Logg.fatal("Corrupted configuration file '" + configFile.toString() + "' could not be deleted from original location!");
			ExpoCool.forceShutdown();
			return;
		}
		
		Logg.warn("New configuration file has been created. Old configuration file moved to " + finalPath.toString());
		
		firstTimeSetup();
	}
	
	/**
	 * Saves configuration file
	 */
	public void save()
	{
		if(this.configFile == null) { Logg.error("The configuration file location is null"); return; }
		if(this.config == null) { Logg.error("The FileConfiguration Object is null"); return; }
		
		try
		{
			this.config.save(this.configFile.toFile());
		}
		catch (IOException e)
		{
			Logg.error("Failed to save configuration file!",e);
		}
	}
	
	/**
	 * Sets the defaults for the configuration file
	 * 
	 * <p>New configuration files have this done automatically
	 */
	public void setDefaults()
	{
		for(ConfigItem<?> key : this.defaults)
		{
			if(this.config.contains(key.getKey())) { continue; }
			this.config.set(key.getKey(),key.getDefaultValue());
			this.config.setComments(key.getKey(),key.getDescriptionChopped(64));
		}
	}
	
	public void reset()
	{
		FUtils.delete(configFile);
		firstTimeSetup();
	}
	
	/**
	 * Gets the configuration
	 * @return FileConfiguration
	 */
	public FileConfiguration get()
	{
		return this.config;
	}
	
	/**
	 * Setup for new configuration files
	 */
	private void firstTimeSetup()
	{
		if(this.configFile == null)
		{
			Logg.fatal("Cannot load configuration file as path is null!");
			ExpoCool.forceShutdown();
			return;
		}
		
		FUtils.createFile(configFile);
		this.config = YamlConfiguration.loadConfiguration(configFile.toFile());
		setDefaults();
		this.config.options().setHeader(this.header);
		save();
	}

	public boolean isLoaded()
	{
		return loaded;
	}

	@Override
	public void saveConfig()
	{
		this.save();
	}

	@Override
	public void reloadConfig()
	{
		CooldownCtrl.clearInstances();
		CombatCtrl.clearInstances();
		
		this.loaded = false;
		this.load();
	}
	
	@Override
	public Object getObject(String key)
	{
		return get().get(key);
	}
	
	@Override
	public boolean hasKey(String key)
	{
		return get().contains(key);
	}

	@Override
	public String getString(String key)
	{
		return get().getString(key);
	}

	@Override
	public boolean getBoolean(String key)
	{
		return get().getBoolean(key);
	}

	@Override
	public int getInt(String key)
	{
		return get().getInt(key);
	}

	@Override
	public long getLong(String key)
	{
		return get().getLong(key);
	}

	@Override
	public float getFloat(String key)
	{
		// No option to directly get a value as float... bruh
		return (float) get().getDouble(key);
	}

	@Override
	public double getDouble(String key)
	{
		return get().getDouble(key);
	}
	
	@Override
	public List<?> getList(String key)
	{
		return get().getList(key);
	}

	@Override
	public List<String> getStringList(String key)
	{
		return get().getStringList(key);
	}

	@Override
	public List<Boolean> getBooleanList(String key)
	{
		return get().getBooleanList(key);
	}

	@Override
	public List<Integer> getIntList(String key)
	{
		return get().getIntegerList(key);
	}

	@Override
	public List<Long> getLongList(String key)
	{
		return get().getLongList(key);
	}

	@Override
	public List<Float> getFloatList(String key)
	{
		return get().getFloatList(key);
	}

	@Override
	public List<Double> getDoubleList(String key)
	{
		return get().getDoubleList(key);
	}

	@Override
	public void set(String key,Object value)
	{
		get().set(key,value);
	}
	
	private List<String> getHeader()
	{
		return List.of
		(
			"___________                     _________               .__   ",
			"\\_   _____/__  _________   ____ \\_   ___ \\  ____   ____ |  |  ",
			" |    __)_\\  \\/  /\\____ \\ /  _ \\/    \\  \\/ /  _ \\ /  _ \\|  |  ",
			" |        \\>    < |  |_> >  <_> )     \\___(  <_> |  <_> )  |__",
			"/_______  /__/\\_ \\|   __/ \\____/ \\______  /\\____/ \\____/|____/",
			"        \\/      \\/|__|                  \\/                    ",
			"==================================================================",
			"A plugin by Laeven"
		);
	}
}
