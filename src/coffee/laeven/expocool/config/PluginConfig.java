package coffee.laeven.expocool.config;

import java.util.List;

public interface PluginConfig
{
	public void saveConfig();
	
	public void reloadConfig();
	
	public boolean hasKey(String key);
	
	public Object getObject(String key);
	
	public String getString(String key);
	
	public boolean getBoolean(String key);
	
	public int getInt(String key);
	
	public long getLong(String key);
	
	public float getFloat(String key);
	
	public double getDouble(String key);
	
	public List<?> getList(String key);
	
	public List<String> getStringList(String key);
	
	public List<Boolean> getBooleanList(String key);
	
	public List<Integer> getIntList(String key);
	
	public List<Long> getLongList(String key);
	
	public List<Float> getFloatList(String key);
	
	public List<Double> getDoubleList(String key);
	
	public void set(String key,Object value);
}
