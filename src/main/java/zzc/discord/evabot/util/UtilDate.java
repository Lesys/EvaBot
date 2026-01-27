package zzc.discord.evabot.util;

import java.util.Calendar;
import java.util.Date;

public final class UtilDate {
	public static boolean isSameDate(Date date1, Date date2, boolean withTime) {
		return withTime ? isSameDateWithTime(date1, date2) : isSameDateWithoutTime(date1, date2);
	}
	
	private static boolean isSameDateWithoutTime(Date date1, Date date2) {
		boolean result = false;
		
		// Returns false if any date is null except if both are null (which means they are the same)
		if (date1 == null || date2 == null) {
			if (date1 == date2) {
				result = true;
			}
		} else {
			Calendar cal1 = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();
			cal1.setTime(date1);
			cal2.setTime(date2);
			
			result = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
					&& cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);			
		}
		
		return result;
	}

	private static boolean isSameDateWithTime(Date date1, Date date2) {
		boolean result = false;
		
		// Returns false if any date is null except if both are null (which means they are the same)
		if (date1 == null || date2 == null) {
			if (date1 == date2) {
				result = true;
			}
		} else {
			result = date1.getTime() == date2.getTime();
		}
		
		return result;
	}
	
	public static boolean isSameDay(Date date1, Date date2) {
		boolean result = false;
		
		// Returns false if any date is null except if both are null (which means they are the same)
		if (date1 == null || date2 == null) {
			if (date1 == date2) {
				result = true;
			}
		} else {
			Calendar cal1 = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();
			cal1.setTime(date1);
			cal2.setTime(date2);
			
			result = cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE);
		}
		
		return result;
	}
	
	public static boolean isSameMonth(Date date1, Date date2) {
		boolean result = false;
		
		// Returns false if any date is null except if both are null (which means they are the same)
		if (date1 == null || date2 == null) {
			if (date1 == date2) {
				result = true;
			}
		} else {
			Calendar cal1 = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();
			cal1.setTime(date1);
			cal2.setTime(date2);
			
			result = cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
		}
		
		return result;
	}
	
	public static boolean isSameYear(Date date1, Date date2) {
		boolean result = false;
		
		// Returns false if any date is null except if both are null (which means they are the same)
		if (date1 == null || date2 == null) {
			if (date1 == date2) {
				result = true;
			}
		} else {
			Calendar cal1 = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();
			cal1.setTime(date1);
			cal2.setTime(date2);
			
			result = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
		}
		
		return result;
	}
}
