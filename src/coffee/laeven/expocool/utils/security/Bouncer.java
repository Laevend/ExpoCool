package coffee.laeven.expocool.utils.security;

import java.util.Collection;
import java.util.Objects;

import coffee.laeven.expocool.utils.Logg;
import coffee.laeven.expocool.utils.MathUtils;

/**
 * @author Laeven
 * Acts as a way to block method calls from classes/objects that shouldn't be calling a method
 */
public final class Bouncer
{
	/**
	 * Probes the stack trace where {@link Bouncer#probe()} is called showing what where the last call was from
	 */
	public static final void probeLastCall()
	{
		StackTraceElement ele = Thread.currentThread().getStackTrace()[3];
		Logg.info("Bouncer Probe -> \n" + 
				  "    Class: " + ele.getClassName() + "\n" +
				  "    Method: " + ele.getMethodName() + "\n" +
				  "    Line: " + ele.getLineNumber());
	}
	
	
	/**
	 * Probes the stack trace where {@link Bouncer#probe()} is called showing what where the last call was from
	 */
	public static final void probe()
	{
		char arrowLeftUp = '\u2514';
		StringBuilder sb = new StringBuilder();
		StackTraceElement ele = Thread.currentThread().getStackTrace()[3];
		
		sb.append("Bouncer Probe -> \n");
		sb.append("&a" + ele.getClassName() + " &f| &9" + ele.getMethodName() + " &f| &e" + ele.getLineNumber() + "\n");
		ele = Thread.currentThread().getStackTrace()[4];
		sb.append("&f" + arrowLeftUp + " &a" + ele.getClassName() + " &f| &9" + ele.getMethodName() + " &f| &e" + ele.getLineNumber() + "\n");
		
		for(int i = 5; i < Thread.currentThread().getStackTrace().length; i++)
		{
			ele = Thread.currentThread().getStackTrace()[i];
			sb.append("  &a" + ele.getClassName() + " &f| &9" + ele.getMethodName() + " &f| &e" + ele.getLineNumber() + "\n");
		}
		
		Logg.info(sb.toString());
	}
	
	/**
	 * Checks a string is not null, have a length of 0, is not empty or blank
	 * @param string String to check
	 * @param message Message to display should string not meet these conditions
	 */
	public static final void requireNotNullOrEmpty(String string,String message)
	{
		Objects.requireNonNull(string,message);
		if(string.length() == 0 || string.isEmpty() || string.isBlank())
		{
			Logg.throwIllegalArgumentError(message);
			return;
		}
	}
	
	/**
	 * Checks an array is not null, has a length greater than 0, and checks the first array index if a value exists
	 * @param array The array object to check
	 * @param message Message to display should string not meet these conditions
	 */
	public static final void requireNotNullOrEmpty(Object[] array,String message)
	{
		Objects.requireNonNull(array,message);
		if(array.length == 0)
		{
			Logg.throwIllegalArgumentError(message);
			return;
		}
		
		Objects.requireNonNull(array[0],message);
	}
	
	/**
	 * Checks an array is not null, has a length greater than 0, and checks the first array index if a value exists
	 * @param array The array object to check
	 * @param message Message to display should string not meet these conditions
	 */
	public static final void requireNotNullOrEmpty(byte[] array,String message)
	{
		Objects.requireNonNull(array,message);
		if(array.length == 0)
		{
			Logg.throwIllegalArgumentError(message);
			return;
		}
		
		Objects.requireNonNull(array[0],message);
	}
	
	/**
	 * Checks an array is not null, has a length greater than 0, and checks the first array index if a value exists
	 * @param array The array object to check
	 * @param message Message to display should string not meet these conditions
	 */
	public static final void requireNotNullOrEmpty(short[] array,String message)
	{
		Objects.requireNonNull(array,message);
		if(array.length == 0)
		{
			Logg.throwIllegalArgumentError(message);
			return;
		}
		
		Objects.requireNonNull(array[0],message);
	}
	
	/**
	 * Checks an array is not null, has a length greater than 0, and checks the first array index if a value exists
	 * @param array The array object to check
	 * @param message Message to display should string not meet these conditions
	 */
	public static final void requireNotNullOrEmpty(int[] array,String message)
	{
		Objects.requireNonNull(array,message);
		if(array.length == 0)
		{
			Logg.throwIllegalArgumentError(message);
			return;
		}
		
		Objects.requireNonNull(array[0],message);
	}
	
	/**
	 * Checks an array is not null, has a length greater than 0, and checks the first array index if a value exists
	 * @param array The array object to check
	 * @param message Message to display should string not meet these conditions
	 */
	public static final void requireNotNullOrEmpty(long[] array,String message)
	{
		Objects.requireNonNull(array,message);
		if(array.length == 0)
		{
			Logg.throwIllegalArgumentError(message);
			return;
		}
		
		Objects.requireNonNull(array[0],message);
	}
	
	/**
	 * Checks an array is not null, has a length greater than 0, and checks the first array index if a value exists
	 * @param array The array object to check
	 * @param message Message to display should string not meet these conditions
	 */
	public static final void requireNotNullOrEmpty(float[] array,String message)
	{
		Objects.requireNonNull(array,message);
		if(array.length == 0)
		{
			Logg.throwIllegalArgumentError(message);
			return;
		}
		
		Objects.requireNonNull(array[0],message);
	}
	
	/**
	 * Checks an array is not null, has a length greater than 0, and checks the first array index if a value exists
	 * @param array The array object to check
	 * @param message Message to display should string not meet these conditions
	 */
	public static final void requireNotNullOrEmpty(double[] array,String message)
	{
		Objects.requireNonNull(array,message);
		if(array.length == 0)
		{
			Logg.throwIllegalArgumentError(message);
			return;
		}
		
		Objects.requireNonNull(array[0],message);
	}
	
	/**
	 * Checks an array is not null, has a length greater than 0, and checks the first array index if a value exists
	 * @param array The array object to check
	 * @param message Message to display should string not meet these conditions
	 */
	public static final void requireNotNullOrEmpty(boolean[] array,String message)
	{
		Objects.requireNonNull(array,message);
		if(array.length == 0)
		{
			Logg.throwIllegalArgumentError(message);
			return;
		}
		
		Objects.requireNonNull(array[0],message);
	}
	
	/**
	 * Checks an array is not null, has a length greater than 0, and checks the first array index if a value exists
	 * @param array The array object to check
	 * @param message Message to display should string not meet these conditions
	 */
	public static final void requireNotNullOrEmpty(char[] array,String message)
	{
		Objects.requireNonNull(array,message);
		if(array.length == 0)
		{
			Logg.throwIllegalArgumentError(message);
			return;
		}
		
		Objects.requireNonNull(array[0],message);
	}
	
	/**
	 * Checks a collection is not null and is not empty
	 * @param collection The Collection object to check
	 * @param message Message to display should string not meet these conditions
	 */
	public static final void requireNotNullOrEmpty(Collection<?> collection,String message)
	{
		Objects.requireNonNull(collection,message);
		if(collection.isEmpty())
		{
			Logg.throwIllegalArgumentError(message);
			return;
		}
	}
	
	/**
	 * Checks a value is not null and is within a range
	 * @param value The value to check
	 * @param message Message to display should string not meet these conditions
	 */
	public static final void requireNotNullAndInRange(int value,int lowerBound,int upperBound,String message)
	{
		Objects.requireNonNull(value,message);
		if(!MathUtils.inclusiveRange(lowerBound,upperBound,value))
		{
			Logg.throwIllegalArgumentError(message);
			return;
		}
	}
	
	/**
	 * Checks a value is not null and is within a range
	 * @param value The value to check
	 * @param message Message to display should string not meet these conditions
	 */
	public static final void requireNotNullAndInRange(long value,long lowerBound,long upperBound,String message)
	{
		Objects.requireNonNull(value,message);
		if(!MathUtils.inclusiveRange(lowerBound,upperBound,value))
		{
			Logg.throwIllegalArgumentError(message);
			return;
		}
	}
}
