package zzc.discord.evabot.util;

import java.util.ArrayList;
import java.util.List;


public final class UtilConvert {
	public static <T> List<T> toList(Iterable<T> iterable) {
		List<T> list = new ArrayList<T>();
		
		for (T item : iterable) {
			list.add(item);
		}
		
		return list;
	}
}
