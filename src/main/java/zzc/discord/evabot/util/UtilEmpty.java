package zzc.discord.evabot.util;

import java.util.Collection;

public final class UtilEmpty {
	private UtilEmpty() {}
	
	public static boolean isEmptyOrNull(String string) {
		return string == null || string.isEmpty();
	}
	
	public static <E> boolean isEmptyOrNull(Collection<E> col) {
		return col == null || col.isEmpty();
	}
}
