package coffee.laeven.expocool.utils;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Chunk;
import org.bukkit.util.Vector;

/**
 * @author Laeven
 * @since 1.0.0
 */
public class MathUtils
{
	private static Random random = new Random();
	
	public static Random rand()
	{
		return random;
	}
	
	public static int max(int num1,int num2)
	{
		if(num1 >= num2) { return num1; }
		return num2;
	}
	
	public static int min(int num1,int num2)
	{
		if(num1 <= num2) { return num1; }
		return num2;
	}
	
	public static float max(float num1,float num2)
	{
		if(num1 >= num2) { return num1; }
		return num2;
	}
	
	public static float min(float num1,float num2)
	{
		if(num1 <= num2) { return num1; }
		return num2;
	}
	
	/**
	 * Returns a random integer from a range 
	 * @param low Lowest number it can be, including this number
	 * @param high Highest number it can be, including this number
	 * @return Integer
	 */
	public static int getRandom(int low,int high)
	{
		if(low == high) { return low; }
		return random.nextInt(low,high + 1);
	}
	
	/**
	 * Returns a random integer from a range 
	 * @param <T>
	 * @return element
	 */
	public static <T> T getRandom(List<T> list)
	{		
		return list.get(getRandom(0,list.size() - 1));
	}
	
	/**
	 * Returns a random integer from a range 
	 * @param <T>
	 * @return element
	 */
	public static <T extends Enum<?>> T getRandomEnum(Class<T> clazz)
	{		
		int randomIndex = random.nextInt(clazz.getEnumConstants().length);
		return clazz.getEnumConstants()[randomIndex];
	}
	
	/**
	 * Returns a random integer from a collection 
	 * @param <T>
	 * @return element
	 */
	public static <E> Optional<E> getRandom(Collection<E> coll)
	{		
		return coll.stream()
	            .skip((int) (coll.size() * Math.random()))
	            .findFirst();
	}
	
	/**
	 * Returns a random float from a range 
	 * @param low Lowest number it can be, including this number
	 * @param high Highest number it can be, including this number
	 * @return Float
	 */
	public static float getRandom(float low,float high)
	{		
		if(low == high) { return low; }
		Random r = new Random();
		float random = low + r.nextFloat() * (high - low);
		return random;
	}
	
	/**
	 * Returns a random double from a range 
	 * @param low Lowest number it can be, including this number
	 * @param high Highest number it can be, including this number
	 * @return Double
	 */
	public static double getRandom(double low,double high)
	{		
		if(low == high) { return low; }
		Random r = new Random();
		double random = low + r.nextDouble() * (high - low);
		return random;
	}
	
	/**
	 * Returns a random boolean
	 * @return Boolean
	 */
	public static boolean getRandom()
	{		
		return ThreadLocalRandom.current().nextBoolean();
	}
	
	/**
	 * Gets a random string of integers
	 * @param numberOfInts Number of Ints to generate
	 * @return String of numbers
	 */
	public static String getRandomIntString(int numberOfInts)
	{
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < numberOfInts; i++)
		{
			sb.append(getRandom(0,9));
		}
		
		return sb.toString();
	}
	
	/**
	 * Gets a random string of integers using secure random
	 * @param numberOfInts Number of Ints to generate
	 * @return String of numbers
	 */
	public static String getSecureRandomIntString(int numberOfInts)
	{
		StringBuilder sb = new StringBuilder();
		SecureRandom sr = new SecureRandom();
		
		for(int i = 0; i < numberOfInts; i++)
		{
			sb.append(sr.nextInt(0,10));
		}
		
		return sb.toString();
	}
	
	/**
	 * Checks if a value exists in-between 2 numbers including those numbers
	 * @param low Lowest number including this number
	 * @param high Highest number including this number
	 * @param value The value being checked
	 * @return True/False
	 */
	public static boolean inclusiveRange(int low,int high,int value)
	{
		if(value >= low && value <= high) { return true; }
		
		return false;
	}
	
	/**
	 * Checks if a value exists in-between 2 numbers including those numbers
	 * @param low Lowest number including this number
	 * @param high Highest number including this number
	 * @param value The value being checked
	 * @return True/False
	 */
	public static boolean inclusiveRange(float low,float high,float value)
	{
		if(value >= low && value <= high) { return true; }
		
		return false;
	}
	
	/**
	 * Checks if a value exists in-between 2 numbers including those numbers
	 * @param low Lowest number including this number
	 * @param high Highest number including this number
	 * @param value The value being checked
	 * @return True/False
	 */
	public static boolean inclusiveRange(double low,double high,double value)
	{
		if(value >= low && value <= high) { return true; }
		
		return false;
	}
	
	/**
	 * Checks if a value exists in-between 2 numbers NOT including those numbers
	 * @param low Lowest number NOT including this number
	 * @param high Highest number NOT including this number
	 * @param value The value being checked
	 * @return True/False
	 */
	public static boolean exclusiveRange(int low,int high,int value)
	{
		if(value > low && value < high) { return true; }
		
		return false;
	}
	
	/**
	 * Checks if a value exists in-between 2 numbers NOT including those numbers
	 * @param low Lowest number NOT including this number
	 * @param high Highest number NOT including this number
	 * @param value The value being checked
	 * @return True/False
	 */
	public static boolean exclusiveRange(double low,double high,double value)
	{
		if(value > low && value < high) { return true; }
		
		return false;
	}
	
	/**
	 * Clamps a value inside a range
	 * @param low Lowest this value can be
	 * @param high Highest this value can be
	 * @param value Value to clamp
	 * @return valued clamped inside the range provided
	 */
	public static int clamp(int low,int high,int value)
	{
		int newValue = value;
		if(newValue > high) { newValue = high; return newValue; }
		if(newValue < low) { newValue = low; return newValue; }
		return newValue;
	}
	
	/**
	 * Clamps a value inside a range
	 * @param low Lowest this value can be
	 * @param high Highest this value can be
	 * @param value Value to clamp
	 * @return valued clamped inside the range provided
	 */
	public static double clamp(double low,double high,double value)
	{
		double newValue = value;
		if(newValue > high) { newValue = high; return newValue; }
		if(newValue < low) { newValue = low; return newValue; }
		return newValue;
	}
	
	/**
	 * Clamps a value inside a range
	 * @param low Lowest this value can be
	 * @param high Highest this value can be
	 * @param value Value to clamp
	 * @return valued clamped inside the range provided
	 */
	public static float clamp(float low,float high,float value)
	{
		float newValue = value;
		if(newValue > high) { newValue = high; return newValue; }
		if(newValue < low) { newValue = low; return newValue; }
		return newValue;
	}
	
	/**
	 * Clamps a value inside a range
	 * @param low Lowest this value can be
	 * @param high Highest this value can be
	 * @param value Value to clamp
	 * @return valued clamped inside the range provided
	 */
	public static long clamp(long low,long high,long value)
	{
		long newValue = value;
		if(newValue > high) { newValue = high; return newValue; }
		if(newValue < low) { newValue = low; return newValue; }
		return newValue;
	}
	
	/**
	 * Clamps and rolls a value.
	 * <p>If a value is outside the clamp range it will roll around to the other boundary of the clamp.
	 * 
	 * <p>Example, range of 1 to 10, an input of 31, and an initial value of 2 will have the value roll back to 1 if it exceeds 10.
	 * <p>In this example it would roll around to 1 and count up again 3 times and land on 2.
	 * @param low
	 * @param high
	 * @param value
	 * @return
	 */
	public static int clampAndRoll(int low,int high,int initialValue,int value)
	{
		int clampedInitialVal = clamp(low,high,initialValue);
		int rangeSize = high - (low - 1);
		int remain = value % rangeSize;
		int finalRoll = clampedInitialVal + remain;
		
		if(finalRoll > high)
		{
			remain = finalRoll - high;
			finalRoll = (low - 1) + remain;
		}
		
		if(finalRoll < low)
		{
			remain = low - finalRoll;
			finalRoll = (high + 1) - remain;
		}
		
		return finalRoll;
	}
	
	public static double distanceToMagnitude(double distance)
	{
        return ((distance + 1.5)/5d);
    }

	public static Vector setMag(Vector vector, double mag){
        double x = vector.getX();
        double y = vector.getY();
        double z = vector.getZ();
        double denominator = Math.sqrt(x*x + y*y + z*z);
        if(denominator != 0 ){
            return vector.multiply(mag/denominator);
        }else{
            return vector;
        }
    }
	
	public static Vector rotateAroundAxisX(Vector v, double angle)
	{
		angle = Math.toRadians(angle);
		double y, z, cos, sin;
		cos = Math.cos(angle);
		sin = Math.sin(angle);
		y = v.getY() * cos - v.getZ() * sin;
		z = v.getY() * sin + v.getZ() * cos;
    	y = Math.round(y);
    	z = Math.round(z);
		return v.setY(y).setZ(z);
    }

	public static Vector rotateAroundAxisY(Vector v, double angle)
    {
    	angle = -angle;
    	angle = Math.toRadians(angle);
    	double x, z, cos, sin;
    	cos = Math.cos(angle);
    	sin = Math.sin(angle);
    	x = v.getX() * cos + v.getZ() * sin;
    	z = v.getX() * -sin + v.getZ() * cos;
    	x = Math.round(x);
    	z = Math.round(z);
    	return v.setX(x).setZ(z);
    }

	public static Vector rotateAroundAxisZ(Vector v, double angle)
    {
    	angle = Math.toRadians(angle);
    	double x, y, cos, sin;
    	cos = Math.cos(angle);
    	sin = Math.sin(angle);
    	x = v.getX() * cos - v.getY() * sin;
    	y = v.getX() * sin + v.getY() * cos;
    	x = Math.round(x);
    	y = Math.round(y);
    	return v.setX(x).setY(y);
    }
	
	public static Vector mirrorAxisX(Vector v)
    {
		int x = (int) (v.getX() * -1);
    	return v.setX(x);
    }
	
	public static Vector mirrorAxisZ(Vector v)
    {
		int z = (int) (v.getZ() * -1);
    	return v.setZ(z);
    }
	
	public static double getDistance(double x1,double x2,double z1,double z2)
	{
		return Math.sqrt((z2 - z1) * (z2 - z1) + (x2 - x1) * (x2 - x1));
	}
	
	public static double getDistance(int x1,int x2,int z1,int z2)
	{
		return Math.sqrt((z2 - z1) * (z2 - z1) + (x2 - x1) * (x2 - x1));
	}
	
	public static double getDistance(Chunk chunkA,Chunk chunkB)
	{
		int x1 = chunkA.getX();
		int x2 = chunkB.getX();
		int z1 = chunkA.getZ();
		int z2 = chunkB.getZ();
		
		return Math.sqrt((z2 - z1) * (z2 - z1) + (x2 - x1) * (x2 - x1));
	}
	
	public static Set<Integer> getSetOfNumbers(int start,int end)
	{
		Set<Integer> nums = new HashSet<>();
		
		for(int i = start; i <= end; i++)
		{
			nums.add(i);
		}
		
		return nums;
	}
	
	public static Set<String> getSetOfNumbersAsString(int start,int end)
	{
		Set<String> nums = new HashSet<>();
		
		for(int i = start; i <= end; i++)
		{
			nums.add(String.valueOf(i));
		}
		
		return nums;
	}
}