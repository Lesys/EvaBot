package zzc.discord.evabot.util;

import java.util.function.Supplier;

import zzc.discord.evabot.dto.ERPlayerDTO;

public final class UtilERPlayer {
	
	/**
	 * Returns true if both players have the same discord name (ignoring case, excluding null)
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static boolean hasSameDiscordName(ERPlayerDTO p1, ERPlayerDTO p2) {
		if (p1 == null || p2 == null || p1.getDiscordName() == null || p2.getDiscordName() == null) return false;
		
		return p1.getDiscordName().equalsIgnoreCase(p2.getDiscordName());
	}


	/**
	 * Retrieves the player name without any special character
	 * @param getString
	 * @return
	 */
	public static String getNameWithoutSpecialChar(Supplier<String> getString) {
		if (getString == null || getString.get() == null) return null;
		return getString.get().replaceAll("[*_]", "");
	}	
}
