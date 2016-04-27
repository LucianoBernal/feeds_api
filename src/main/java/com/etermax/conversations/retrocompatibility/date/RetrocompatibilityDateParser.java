package com.etermax.conversations.retrocompatibility.date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class RetrocompatibilityDateParser {

	private static DateTimeFormatter format = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss z")
															.withZone(DateTimeZone.forID("EST"));

	public static String parseDate(Date date) {
		DateTime dateTime = new DateTime(date.getTime());
		return dateTime.toString(format);
	}

	public static String parseDate(Long date) {
		return parseDate(new Date(date));
	}

}
