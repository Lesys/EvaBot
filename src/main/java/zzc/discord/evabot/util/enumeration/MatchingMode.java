package zzc.discord.evabot.util.enumeration;

/**
 * The matchingMode of an ER game
 * 1 ? (9 on API PDF) == lone wolf
 * 2 == normal
 * 3 == ranked
 * 6 (4 on API PDF) == cobalt
 * 8 == union
 */
public enum MatchingMode {
	LONE_WOLF(1), NORMAL(2), RANKED(3), COBALT(6), UNION(8);
	
	protected int value;
	
	MatchingMode(int value) {
		this.value = value;
	}
	
	public static MatchingMode getMatchingModeByValue(Integer value) {
		MatchingMode result = null;
		if (value != null) {		
			if (value.equals(LONE_WOLF.value)) {
				result = MatchingMode.LONE_WOLF;
			} else if (value.equals(NORMAL.value)) {
				result = MatchingMode.NORMAL;
			} else if (value.equals(RANKED.value)) {
				result = MatchingMode.RANKED;
			} else if (value.equals(COBALT.value)) {
				result = MatchingMode.COBALT;
			} else if (value.equals(UNION.value)) {
				result = MatchingMode.UNION;
			}
		}
		
		return result;
	}
}
