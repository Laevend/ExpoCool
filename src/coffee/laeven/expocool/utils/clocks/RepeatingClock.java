package coffee.laeven.expocool.utils.clocks;

import org.bukkit.scheduler.BukkitRunnable;

import coffee.laeven.expocool.ExpoCool;
import coffee.laeven.expocool.utils.Logg;
import coffee.laeven.expocool.utils.clocks.absclocks.BukkitClock;

/**
 * 
 * @author Laeven
 * A clock that will call {@link #execute()} after a specified interval until manually cancelled
 */
public abstract class RepeatingClock extends BukkitClock
{
	/**
	 * Creates a repeating clock
	 * @param clockName Name of clock
	 * @param intervalInTicks Interval before {@link #execute()} is called
	 */
	public RepeatingClock(String clockName,long durationInTicks)
	{
		super(clockName,durationInTicks);
	}
	
	@Override
	protected void run()
	{
		clock = new BukkitRunnable()
		{
			@Override
			public void run() 
		    {
				if(clock.isCancelled()) { return; }
				
				if(attempts > continueAttempts)
		    	{
		    		cancel();
		    		Logg.fatal("Clock " + clockName + " was canceled due to failing > " + continueAttempts + " times");
		    		return;
		    	}
				
				try
		    	{
		    		execute();
		    		attempts = 1;
		    		
		    		// If duration was not refilled in the execute()
		    		if(interval <= 0)
		    		{
		    			cancel();
		    			return;
		    		}
		    	}
		    	catch(Exception e)
		    	{
		    		Logg.error(clockName + " tripped and threw an exception!",e);
		    		attempts++;
		    	}
		    }
		}.runTaskTimer(ExpoCool.instance(),0L,interval);
	}
	
	public abstract void execute() throws Exception;
}