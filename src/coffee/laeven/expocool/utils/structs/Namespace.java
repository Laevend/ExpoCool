package coffee.laeven.expocool.utils.structs;

import java.util.Objects;
import java.util.regex.Pattern;

import org.bukkit.NamespacedKey;

/**
 * 
 * @author Laeven
 * Defines a key inside a namespace without requiring the namespace itself to be a plugin
 */
public final class Namespace
{
	private final String name;
	private final String key;
	private final String toString;
	public static final Pattern namespaceStringPattern = Pattern.compile("^\\[Namespace:.+,Key:.+\\]$");
	public static final Pattern namespaceSimpleStringPattern = Pattern.compile("^.+:.+$");
	
	/**
	 * Creates a new namespace
	 * @param namespaceName Name of this namespace
	 * @param key The key of this namespace
	 * @return Namespace object
	 */
	public static final Namespace of(String namespaceName,String namespaceKey)
	{
		return new Namespace(namespaceName,namespaceKey);
	}
	
	public static final Namespace fromString(String namespaceString)
	{
		Objects.requireNonNull(namespaceString,"Namespace string name cannot be null!");
		
		if(namespaceStringPattern.matcher(namespaceString).matches())
		{
			String[] namespaceAndKey = namespaceString.split(",");
			String namespace = namespaceAndKey[0].split(":")[1];
			String key = namespaceAndKey[1].split(":")[1];
			return Namespace.of(namespace,key);
		}
		
		if(namespaceSimpleStringPattern.matcher(namespaceString).matches())
		{
			String[] namespaceAndKey = namespaceString.split(":");
			String namespace = namespaceAndKey[0];
			String key = namespaceAndKey[1];
			return Namespace.of(namespace,key);
		}
		
		throw new IllegalArgumentException("Namespace string '" + namespaceString + "' could not be converted to a namespace as it is not correctly formatted!");
	}
	
	public static final Namespace fromNamespacedKey(NamespacedKey key)
	{
		Objects.requireNonNull(key,"NamespacedKey string name cannot be null!");
		return new Namespace(key.getNamespace(),key.getKey());
	}
	
	private Namespace(String namespaceName,String namespaceKey)
	{
		Objects.requireNonNull(namespaceName,"Namespace name cannot be null!");
		Objects.requireNonNull(namespaceKey,"Namespace key cannot be null!");
		
		name = namespaceName;
		key = namespaceKey;
		toString = "[Namespace:\"" + name + "\",Key:\"" + key + "\"]";
	}
	
	/**
	 * Returns the name of this namespace
	 * @return
	 */
	public final String getName()
	{
		return name;
	}
	
	/**
	 * Returns the key of this namespace
	 * @return
	 */
	public final String getKey()
	{
		return key;
	}
	
	@Override
	public String toString()
	{
		return toString;
	}
	
	public String toSimpleString()
	{
		return name + ":" + key;
	}
	
	/**
	 * Convenient method to convert a namespace to a namespaced key for compatibility
	 * @return NamespacedKey
	 */
	public NamespacedKey toNamespacedKey()
	{
		return new NamespacedKey(name,key);
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof Namespace namespace)) { return false; }
		return this.toString.equals(namespace.toString());
	}
	
	@Override
	public int hashCode()
	{
		return this.toString.hashCode();
	}
}
