package me.elgamer.earthserver.utils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;


public class Time {

	public static long currentTime() {	

		return System.currentTimeMillis();
	}

	public static String getDate(long time) {

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
		TimeZone timeZone = TimeZone.getTimeZone("Europe/London");
		formatter.setTimeZone(timeZone);
		
		Date date = new Date(time);
		return formatter.format(date);
	}

}