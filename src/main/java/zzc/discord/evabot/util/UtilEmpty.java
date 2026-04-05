package zzc.discord.evabot.util;

import java.util.Collection;
import java.util.Map;

public final class UtilEmpty {
	private UtilEmpty() {}
	
	public static boolean isEmptyOrNull(String string) {
		return string == null || string.isEmpty();
	}
	
	public static <E> boolean isEmptyOrNull(Collection<E> col) {
		return col == null || col.isEmpty();
	}
	
	public static <A, B> boolean isEmptyOrNull(Map<A, B> col) {
		return col == null || col.isEmpty();
	}
}
