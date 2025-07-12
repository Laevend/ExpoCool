package coffee.laeven.expocool.config.item;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import coffee.laeven.expocool.ExpoCool;
import coffee.laeven.expocool.utils.Logg;
import coffee.laeven.expocool.utils.StringUtils;
import coffee.laeven.expocool.utils.structs.Pair;

public class ConfigItem<V>
{
	private final String key;
	private String description = null;
	private Class<V> type;
	private final V defaultValue;
	private SortedSet<V> possibleValues = null;
	
	@SuppressWarnings("unchecked")
	public ConfigItem(String key,V defaultValue)
	{
		Objects.requireNonNull(key,"Config key cannot be null!");
		Objects.requireNonNull(defaultValue,"Default value cannot be null!");
		this.key = key;
		this.defaultValue = defaultValue;
		this.type = (Class<V>) defaultValue.getClass();
	}
	
	public ConfigItem(String key,V defaultValue,String description)
	{
		this(key,defaultValue);
		Objects.requireNonNull(description,"Description cannot be null!");
		this.description = description;
	}
	
	public ConfigItem(String key,V defaultValue,String description,V[] possibleValues)
	{
		this(key,defaultValue);
		this.description = description;
		this.possibleValues = new TreeSet<>();
		this.possibleValues.addAll(Arrays.asList(possibleValues));
	}
	
	public final boolean has()
	{
		return ExpoCool.getConfigFile().hasKey(key);
	}
	
	@SuppressWarnings("unchecked")
	public final V get()
	{
		if(!has())
		{
			set(defaultValue);
			return defaultValue;
		}
		
		V value = null;
		
		try
		{
			if(defaultValue instanceof List)
			{
				value = clamp(type.cast(ExpoCool.getConfigFile().getList(key)));
			}
			else
			{
				if(type.equals(String.class)) { value = (V) ExpoCool.getConfigFile().getString(key); }
				else if(type.equals(Integer.class)) { value = (V) Integer.valueOf(ExpoCool.getConfigFile().getInt(key)); }
				else if(type.equals(Double.class)) { value = (V) Double.valueOf(ExpoCool.getConfigFile().getDouble(key)); }
				else if(type.equals(Float.class)) { value = (V) Float.valueOf(ExpoCool.getConfigFile().getFloat(key)); }
				else if(type.equals(Long.class)) { value = (V) Long.valueOf(ExpoCool.getConfigFile().getLong(key)); }
				else if(type.equals(Boolean.class)) { value = (V) Boolean.valueOf(ExpoCool.getConfigFile().getBoolean(key)); }
			}
			
			if(value == null)
			{
				throw new IllegalArgumentException("Unsupported type " + type.getTypeName());
			}
		}
		catch(Exception e)
		{
			Logg.error("Config key " + key + " cannot be retrieved using unsupported type " + type.getTypeName(),e);
			return null;
		}
		
		return value;
	}
	
	public final void set(V value)
	{
		ExpoCool.getConfigFile().set(key,value);
	}
	
	/**
	 * Clamps the config value to an acceptable value if the one given is not acceptable.
	 * Must be overridden upon initialisation
	 * <p>
	 * By default it will clamp a value to the first possible value in {@link #possibleValues} unless it is null
	 */
	public V clamp(V unclampedValue)
	{
		if(this.possibleValues == null) { return unclampedValue; }
		if(this.possibleValues.contains(unclampedValue)) { return unclampedValue; }
		return this.possibleValues.first();
	}

	public final String getKey()
	{
		return key;
	}
	
	public final String getDescription()
	{
		return description;
	}
	
	public final List<String> getDescriptionChopped(int lineLength)
	{
		List<String> chopped = StringUtils.chop(description,lineLength);
		chopped.addFirst(null);
		return chopped;
	}

	public final Set<V> getPossibleValues()
	{
		return possibleValues;
	}

	public final V getDefaultValue()
	{
		return defaultValue;
	}
	
	public Pair<String,Object> getDefaultsPair()
	{
		return new Pair<>(key,defaultValue);
	}
}
