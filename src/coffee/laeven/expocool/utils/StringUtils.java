package coffee.laeven.expocool.utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtils
{
	public static List<String> chop(String s,int length)
	{
		List<String> choppedString = new ArrayList<>();
		
		if(s.length() <= length) { choppedString.add(s); return choppedString; }
		
		int chopFrom = 0;
		int chopTo = length;
		
		while(true)
		{
			if(s.charAt(chopTo) == ' ')
			{
				choppedString.add(s.substring(chopFrom,chopTo));
				chopFrom = chopTo + 1;
				chopTo = chopTo + length;
			}
			
			if(chopTo >= s.length())
			{
				choppedString.add(s.substring(chopFrom,s.length()));
				break;
			}
			
			chopTo++;
		}
		
		return choppedString;
	}
}
