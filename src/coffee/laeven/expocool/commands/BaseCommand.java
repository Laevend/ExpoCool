package coffee.laeven.expocool.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import coffee.laeven.expocool.utils.Logg;
import xdrop.fuzzywuzzy.FuzzySearch;

public abstract class BaseCommand implements CommandExecutor, TabCompleter
{
	// long string state is a string argument that has one or multiple spaces
	// it is set to true when a quote is opened and set to false when a quote is closed
	private boolean longStringState = false;
	
	public String[] cleanArguments(String args[])
	{
		// If not arguments were entered, just return
		if(args.length == 0) { return args; }
		
		String argumentString = String.join(" ",args);
		long numOfQuotes = argumentString.chars().filter(v -> v == '\"').count();
		
		if(numOfQuotes == 0) { return args; }
		
		// Odd number of quotes. See isLongStringState ^
		longStringState = (numOfQuotes % 2 != 0);
		
		String longStringParts[] = argumentString.split("\"");
		List<String> newArgs = new ArrayList<>();
		
		Logg.verb("Parts -> " + Arrays.asList(longStringParts),Logg.VerbGroup.COMMANDS);
		Logg.verb("length -> " + longStringParts.length,Logg.VerbGroup.COMMANDS);
		
		Loop1:
		for(int i = 0; i < longStringParts.length; i++)
		{
			if(longStringParts[i].length() == 0) { continue Loop1; }
			
			if(i % 2 == 0)
			{				
				String tempArgs[] = longStringParts[i].split(" ");
				
				Loop2:
				for(String arg : tempArgs)
				{
					if(arg.length() == 0) { continue Loop2; }
					newArgs.add(arg);
				}
			}
			else
			{				
				newArgs.add(longStringParts[i]);
			}
		}
		
		return newArgs.toArray(new String[newArgs.size()]);
	}
	
	public boolean assertArgument(String arg,String... values)
	{
		for(String value : values)
		{
			if(arg.toLowerCase().equals(value)) { return true; }
		}
		
		return false;
	}
	
	public boolean isLongStringState()
	{
		return longStringState;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
	{
		List<String> suggestions = onTab(sender,cleanArguments(args));
		if(suggestions == null || suggestions.isEmpty()) { return suggestions; }
		if(args[args.length - 1].isEmpty() || args[args.length - 1].isBlank()) { return suggestions; }
		
		return estimate(suggestions,args[args.length - 1]);
	}
	
	public abstract List<String> onTab(CommandSender sender,String[] args);
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		onCommand(sender,cleanArguments(args));
		return true;
	}
	
	public abstract void onCommand(CommandSender sender,String[] args);
	
	public static final int FUZZY_SEARCH_RATIO_CUTOFF = 70;
	
	private static List<String> estimate(List<String> collectedSuggestions,String lastArg)
	{
		List<String> finalSuggestions = new ArrayList<String>();
		TreeMap<Integer,List<String>> ratioMap = new TreeMap<>(Collections.reverseOrder());
		
		for(String suggestion : collectedSuggestions)
		{
			int ratio = FuzzySearch.partialRatio(suggestion,lastArg);
			
			if(ratio < FUZZY_SEARCH_RATIO_CUTOFF) { continue; }
			if(!ratioMap.containsKey(ratio)) { ratioMap.put(ratio,new ArrayList<>()); }
			
			ratioMap.get(ratio).add(suggestion);
		}
		
		for(List<String> sortedSuggestions : ratioMap.values())
		{
			finalSuggestions.addAll(sortedSuggestions);
		}
		
		// Return new estimates
		return finalSuggestions;
	}
}
