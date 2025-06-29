package zzc.discord.evabot;

import java.util.stream.Stream;

public enum Priority {
	HIGH(1), NEUTRAL(0), LOW(-1);
	
	protected int value;
	
	Priority(int value) {
		this.value = value;
	}
	
	public static Priority getPriorityByName(String priorityString) {
		Priority priority = null;
		if (priorityString.equalsIgnoreCase(Priority.HIGH.toString())) {
			priority = Priority.HIGH;
		} else if (priorityString.equalsIgnoreCase(Priority.NEUTRAL.toString())) {
			priority = Priority.NEUTRAL;
		} else if (priorityString.equalsIgnoreCase(Priority.LOW.toString())) {
			priority = Priority.LOW;
		}
		
		return priority;
	}
	
	public static boolean equals(Priority priority, String priorityString) {
		return priorityString != null && priorityString.equalsIgnoreCase(priority.toString());
	}
	
	public static String valuesToString() {
		String[] stringValues = Stream.of(Priority.values()).map(p -> p.toString()).toList().toArray(String[]::new);
		return String.join(", ", stringValues);
	}
}
