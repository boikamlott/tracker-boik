package com.trackerboik.util;


public class BDDUtil {

	public static String getEnumValuesToStringForBDD(Object[] elems) {
		String res = "";
		
		for(Object o : elems) {
			res += "'" + o.toString() + "',";
		}
		
		if(!res.isEmpty()) {
			res = res.substring(0, res.length() - 1);
		}
		
		return res;
	}
}
