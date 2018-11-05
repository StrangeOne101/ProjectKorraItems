package com.projectkorra.items.utils;

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
}
