package com.barrans.master.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class InternalDateUtil {

	static final long ONE_MINUTE_IN_MILLIS=60000;
	
	public static final String ISO_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
	public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_PATTERN = "yyyy-MM-dd";

    public static Date convertFromStringToDate(String dateFrom) throws Exception {
    	SimpleDateFormat genericFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return genericFormat.parse(dateFrom);
    }
    
    public static LocalDateTime convertFromStringToLocalDateTime(String dateFrom) throws ParseException {
    	SimpleDateFormat genericFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	return genericFormat.parse(dateFrom).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    
    public static String convertLocalDateTimeToString(LocalDateTime dateFrom) {
		LocalDateTime dt = dateFrom;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	return sdf.format(Date.from(dt.atZone(ZoneId.systemDefault()).toInstant()));
    }
    
    public static LocalDateTime convertFromStringYYYYMMDDToLocalDateTime(String dateFrom) throws ParseException {
    	DateTimeFormatter dtfYYYYMMDD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    	return LocalDate.parse(dateFrom, dtfYYYYMMDD).atStartOfDay();
    }
    
    public static Date addMinutesToDate(Date date, long additionalMinutes) throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        long t = cal.getTimeInMillis();
        Date afterAddingAdditionalMinutes = new Date(t + (additionalMinutes * ONE_MINUTE_IN_MILLIS));
        return afterAddingAdditionalMinutes;
    }
    
    public static boolean isSameDay(LocalDateTime date1, LocalDateTime date2) {
        return date1.toLocalDate().isEqual(date2.toLocalDate());
    }

    public static String getIndonesianDate(LocalDateTime date){
        String[] month = new String[]{"","Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus",
                "September", "Oktober", "November", "Desember"};

        return "" + date.getDayOfMonth() + " " + month[date.getMonthValue()] + " " + date.getYear();
    }

	public static String convertLocalDateTimeToOffsetDateTimeToString(LocalDateTime datetime, String format) {
    	ZoneOffset offset = ZoneId.systemDefault().getRules().getOffset(datetime);
        OffsetDateTime offDateTime = datetime.atOffset(offset);

    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
    	return offDateTime.format(dtf);
    }
	
	public static String convertCurrentDateTimeToString(String format) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return formatter.format(Instant.now().atZone(ZoneId.systemDefault()));
    }

	public static String convertLocalDateTimeToToString(LocalDateTime datetime, String format) {
		LocalDateTime dt = datetime;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
    	return sdf.format(Date.from(dt.atZone(ZoneId.systemDefault()).toInstant()));
    }

    public static String convertLocalDateTimeToToString(LocalDateTime datetime, String format, ZoneId zoneId, Locale locale) {
		LocalDateTime zoneDateTime = LocalDateTime.from(datetime.atZone(ZoneId.systemDefault()).toInstant().atZone(zoneId));
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format).localizedBy(locale);
		return zoneDateTime.format(dtf);
	}

    public static String convertLocalDateTimeToJakartaString(LocalDateTime datetime, String format) {
		return convertLocalDateTimeToToString(datetime, format, ZoneId.of("Asia/Jakarta"), new Locale("id"));
	}

	public static String convertLocalDateToToString(LocalDate date, String format) {
		if (null == date) {
			return "";
		}
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		String formattedString = date.format(formatter);
		return formattedString;
    }

    public static LocalDateTime convertFromStringToLocalDateTime(String dateFrom, String format) throws ParseException {
    	SimpleDateFormat genericFormat = new SimpleDateFormat(format);
    	return genericFormat.parse(dateFrom).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    
    public static OffsetDateTime convertFromStringToOffsteDateTime(String dateFrom, String format) throws ParseException {
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
    	return OffsetDateTime.parse(dateFrom, dtf);
    }

}
