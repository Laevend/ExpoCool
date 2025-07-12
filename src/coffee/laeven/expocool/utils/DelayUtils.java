package coffee.laeven.expocool.utils;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import coffee.laeven.expocool.ExpoCool;

/**
 * @author Laeven
 */
public class DelayUtils
{
	/**
	 * Executes a delayed task
	 * @param runn The runnable object to execute
	 */
	public static void executeDelayedTask(Runnable runn)
	{
		executeDelayedTask(runn,1L);
	}
	
	/**
	 * Executes a delayed task
	 * @param runn The runnable object to execute
	 * @param ticksToWait Number of ticks to wait before executing this runnable object
	 * @return task id
	 */
	public static int executeDelayedTask(Runnable runn,long ticksToWait)
	{
        return Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ExpoCool.instance(),runn,ticksToWait);
	}
	
	/**
	 * Executes a delayed bukkit task
	 * @param runn The runnable object to execute
	 * @param ticksToWait Number of ticks to wait before executing this runnable object
	 * @return BukkitTask
	 */
	public static BukkitTask executeDelayedBukkitTask(Runnable runn,long ticksToWait)
	{
        return Bukkit.getServer().getScheduler().runTaskLater(ExpoCool.instance(),runn,ticksToWait);
	}
	
	/**
	 * Executes a task asynchronously 
	 * @param runn The runnable object to execute
	 * @return task id
	 */
	public static BukkitTask executeTaskAsynchronously(Runnable runn)
	{
		return Bukkit.getServer().getScheduler().runTaskAsynchronously(ExpoCool.instance(), runn);
	}
	
	/**
	 * Cancels a scheduled task
	 * @param taskId
	 */
	public static void cancelTask(int taskId)
	{
		Bukkit.getServer().getScheduler().cancelTask(taskId);
	}
}
