package com.projectkorra.items.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenericUtil {
	
	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {return false;}
	}
	
	public static boolean isDouble(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (NumberFormatException e) {return false;}
	}

	/**
	 * Convert an unsigned integer to a signed short
	 */
	public static short convertUnsignedShort(int id) {
		return (short) (id - Short.MIN_VALUE);
	}
	
	/**
	 * Convert a signed short to an unsigned integer
	 */
	public static int convertSignedShort(short id) {
		return (int) (id + Short.MAX_VALUE + 1);
	}
	
	/**
	 * Splits a string into multiple lines by the length provided
	 * @param string The string to split
	 * @param length The length of each line (in characters). Recommended is 60.
	 */
	public static List<String> splitString(String string, int length)
	{
		Pattern p = Pattern.compile("\\G\\s*(.{1,"+length+"})(?=\\s|$)", Pattern.DOTALL);
		Matcher m = p.matcher(string);
		List<String> l = new ArrayList<String>();
		char lastColor = 'f';
		while (m.find())
		{
			String s = m.group(1);
			l.add("\u00A7" + lastColor + s);
			if (s.contains("\u00A7")) {
				lastColor = s.charAt(s.lastIndexOf('\u00A7') + 1);
			}
			
		}
		l.set(0, l.get(0).substring(2)); //Take off the extra white color at the front
		return l;
	}
}
