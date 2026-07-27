package zzc.discord.evabot.util;

import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.function.BiFunction;
import java.util.function.Function;

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
	
	public static Integer compareDate(Date date1, Date date2) {
		return compareDate(date1, date2, false);
	}
	
	/**
	 * Returns  negative integer, zero, or a positive integer as this objectis less than, equal to, or greater than the specified object.
	 * @param date1
	 * @param date2
	 * @param withTime	Takes the time in the comparison or not
	 * @return Returns negative integer if date1 < date2, 0 if both equal, positive integer if date1 > date2, and null if any of the dates are null
	 */
	public static Integer compareDate(Date date1, Date date2, boolean withTime) {
		// No date can be null in this compare
		if (date1 == null || date2 == null) return null;
		
		// If same date (with or without time), returns 0 (prevents, in case of comparing without time, to have a difference because of it)
		if (isSameDate(date1, date2, withTime)) return 0;
		
		return date1.compareTo(date2);
	}
	
	/**
	 * Compares if date1 is after date2, aka date1 > date2
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isAfter(Date date1, Date date2) {
		return compareDate(date1, date2) > 0;
	}
	
	public static boolean isAfter(LocalDateTime date1, LocalDateTime date2) {
		return isAfter(localDateTimeToDate(date1), localDateTimeToDate(date2));
	}

	/**
	 * Compares if date1 is before date2, aka date1 < date2
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isBefore(Date date1, Date date2) {
		return compareDate(date1, date2) < 0;
	}
	
	public static boolean isBefore(LocalDateTime date1, LocalDateTime date2) {
		return isBefore(localDateTimeToDate(date1), localDateTimeToDate(date2));
	}

	/**
	 * Changes the format of date retrieved from ER API to a LocalDateTime Object
	 * 
	 * @param dateString The String with the date formatted to ER API format
	 * @return The LocalDateTime Object initialized to the dateString time
	 */
	public static LocalDateTime getLocalDateTime(String dateString) {
		return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
	}
	
	/*
	 *	=============== CONVERSIONS ===============
	 */
	
	public static Date localDateTimeToDate(LocalDateTime ldt) {
		if (ldt == null) return null;
		return Timestamp.valueOf(ldt);
	}
	
	/*
	 *	=============== DISPLAY ===============
	 */
	
	public static String dateToString(Date date) {
		return dateToString(date, "yyyy-MMMM-dd HH:mm:ss Z");
	}
	
	public static String dateToString(Date date, String pattern) {
		if (date == null) return null;
		
		Format formatter = new SimpleDateFormat("yyyy-MMMM-dd HH:mm:ss Z");
		return formatter.format(date);
	}
	
	/*
	 *	=============== CHANGE DATE TIME ===============
	 */
	
	public static Date addSeconds(Date date, int seconds) {
		return addFunction(date, seconds, LocalDateTime::plusSeconds);		
	}

	public static Date addMinutes(Date date, int minutes) {
		return addFunction(date, minutes, LocalDateTime::plusMinutes);	
	}

	public static Date addHours(Date date, int hours) {		
		return addFunction(date, hours, LocalDateTime::plusHours);
	}
	
	private static Date addFunction(Date date, int numberToAdd, BiFunction<LocalDateTime, Integer, LocalDateTime> function) {
		if (date == null) return null;
		
		LocalDateTime ldt = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		
		return localDateTimeToDate(function.apply(ldt, numberToAdd));	
	}
}
