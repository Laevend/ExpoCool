package coffee.laeven.expocool.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtils
{
	private static SimpleDateFormat sdFormat;
	public static final String PATTERN_DASH_dd_MM = "dd-MM";
	public static final String PATTERN_DASH_dd_MM_yy = "dd-MM-yy";
	public static final String PATTERN_DASH_dd_MM_yyyy = "dd-MM-yyyy";
	public static final String PATTERN_DASH_HH_mm_ss = "HH-mm-ss";
	public static final String PATTERN_DASH_mm_ss = "mm-ss";
	
	public static final String PATTERN_SLASH_dd_MM = "dd/MM";
	public static final String PATTERN_SLASH_dd_MM_yy = "dd/MM/yy";
	public static final String PATTERN_SLASH_dd_MM_yyyy = "dd/MM/yyyy";
	
	public static final String PATTERN_COLON_HH_mm_ss = "HH:mm:ss";
	public static final String PATTERN_COLON_mm_ss = "mm:ss";
	
	public static final String PATTERN_UNDERLINE_dd_MM = "dd_MM";
	public static final String PATTERN_UNDERLINE_dd_MM_yy = "dd_MM_yy";
	public static final String PATTERN_UNDERLINE_dd_MM_yyyy = "dd_MM_yyyy";
	public static final String PATTERN_UNDERLINE_HH_mm_ss = "HH_mm_ss";
	public static final String PATTERN_UNDERLINE_mm_ss = "mm_ss";
	
	/**
	 * Get a time format of a specified time
	 * @param seconds The time in ticks
	 * @param pattern
	 * @return
	 */
	public static String getDateFormat(int ticks,String pattern)
	{
		int days = (int) TimeUnit.SECONDS.toDays(ticks / 20);
		int hours = (int) (TimeUnit.SECONDS.toHours(ticks / 20) - TimeUnit.DAYS.toHours(days));
		int minutes = (int) (TimeUnit.SECONDS.toMinutes(ticks / 20) - TimeUnit.HOURS.toMinutes(hours) - TimeUnit.DAYS.toMinutes(days));
		int seconds = (int) (TimeUnit.SECONDS.toSeconds(ticks / 20) - TimeUnit.MINUTES.toSeconds(minutes));
		
		Logg.verb("Converted " + ticks + " ticks into " + hours + " hour(s) " + minutes + " minute(s) " + seconds + " second(s)",Logg.VerbGroup.TIME_UTILS);
		return DateTimeFormatter.ofPattern(pattern).format(LocalTime.of(hours,minutes,seconds));
	}
	
	/**
	 * Get a time format of a specified time
	 * @param time
	 * @param pattern
	 * @return
	 */
	public static String getDateFormat(LocalTime time,String pattern)
	{
		return DateTimeFormatter.ofPattern(pattern).format(time);
	}
	
	/**
	 * Get a date and time format of a specified date
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String getDateFormat(LocalDateTime date,String pattern)
	{
		return DateTimeFormatter.ofPattern(pattern).format(date);
	}
	
	/**
	 * Get a current date and time format
	 * @param pattern
	 * @return
	 */
	public static String getDateFormat(String pattern)
	{
		return DateTimeFormatter.ofPattern(pattern).format(LocalDateTime.now());
	}
	
	/**
	 * Get a date and time format of a specified date
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String getDateFormat(Date date,String pattern)
	{
		sdFormat = new SimpleDateFormat(pattern);
		return sdFormat.format(date);
	}
	
	public static int hoursToTicks(int hour)
	{
		return minutesToTicks(hour * 60);
	}
	
	public static int minutesToTicks(int minutes)
	{
		return secondsToTicks(minutes * 60);
	}
	
	public static int secondsToTicks(int seconds)
	{
		return seconds * 20;
	}
	
	public static long getMilisecondsAtStartOfDay()
	{
		LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
		return ZonedDateTime.of(startOfDay,ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
	
	public static long getMilisecondsSinceStartOfDay()
	{
		return System.currentTimeMillis() - getMilisecondsAtStartOfDay();
	}
	
	public static final int MILI_IN_DAY = 86_400_000;
	
	public static long getMilisecondsLeftInDay()
	{
		return MILI_IN_DAY - getMilisecondsSinceStartOfDay();
	}
	
	public static String getLongDateFromMili(long mili)
	{
		return new SimpleDateFormat("EEEE d MMMM YYYY, HH:mm:ss").format(new Date(mili));
	}
	
	public static long getMilliFromLocalDateTime(LocalDateTime ldt)
	{
		return ZonedDateTime.of(ldt,ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
}
