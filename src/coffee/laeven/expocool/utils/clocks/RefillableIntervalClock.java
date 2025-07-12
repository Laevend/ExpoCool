package coffee.laeven.expocool.utils.clocks;

import org.bukkit.scheduler.BukkitRunnable;

import coffee.laeven.expocool.ExpoCool;
import coffee.laeven.expocool.utils.Logg;
import coffee.laeven.expocool.utils.MathUtils;
import coffee.laeven.expocool.utils.clocks.absclocks.BukkitClock;


/**
 * 
 * @author Laeven
 * A clock that will automatically stop when the interval hits 0.
 * This clocks interval can be refilled before and during {@link #execute()}
 */
public abstract class RefillableIntervalClock extends BukkitClock
{
	private long maxDuration;
	private long elapsedTime = 0;
	
	/**
	 * Creates a refillable interval clock
	 * @param clockName Name of clock
	 * @param intervalInTicks Interval before {@link #execute()} is called and clock is stopped
	 */
	public RefillableIntervalClock(String clockName,long durationInTicks)
	{
		super(clockName,durationInTicks);
		this.maxDuration = durationInTicks;
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
				
				if(interval <= 0)
				{
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
				else
				{
					interval--;
					elapsedTime++;
				}
		    }
		}.runTaskTimer(ExpoCool.instance(),0L,1L);
	}
	
	public abstract void execute() throws Exception;
	
	@Override
	public void start()
	{
		if(clock != null && !clock.isCancelled()) { return; }
		refill();
		run();
	}
	
	public void refill()
	{
		interval = this.maxDuration;
	}
	
	public void refill(int amount)
	{
		interval += MathUtils.clamp(0,this.maxDuration,amount);
	}
	
	public void setMaxDuration(int maxDuration)
	{
		this.maxDuration = maxDuration;
	}
	
	public long getElapsedInterval()
	{
		return elapsedTime;
	}
}