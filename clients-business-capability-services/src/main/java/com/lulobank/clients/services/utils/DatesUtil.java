package com.lulobank.clients.services.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class DatesUtil {
  private static final String DATE_FORMATTER_dd_mm_yy = "d/MM/yyyy";
  private static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
  private static final String GMT ="GMT-5";
  private static final String UPPERCASE_AM = "AM";
  private static final String UPPERCASE_PM = "PM";
  private static final String LOWERCASE_AM = "a.m.";
  private static final String LOWERCASE_PM = "p.m.";

  private DatesUtil() {
    throw new IllegalStateException();
  }

  public static LocalDate convertString_yy_mm_dd_ToLocalDateTime(String date) {
    return convertStringToLocalDateTime(date, DATE_FORMATTER_dd_mm_yy);
  }

  public static LocalDate convertStringYYYYmmDDToLocalDateTime(String date) {
    return convertStringToLocalDateTime(date, DATE_FORMAT_YYYY_MM_DD);
  }

  private static LocalDate convertStringToLocalDateTime(String date, String formatter) {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(formatter);
    return LocalDate.parse(date, dateFormatter);
  }

  public static LocalDateTime getLocalDateGMT5(){
    return LocalDateTime.now(ZoneId.of(GMT));
  }

  public static LocalDateTime getLocalDateTimeGMT5FromTimestamp(long date) {
    return LocalDateTime.ofEpochSecond(date,0, ZoneOffset.UTC)
            .atZone(ZoneId.of("GMT"))
            .withZoneSameInstant(ZoneId.of(GMT))
            .toLocalDateTime();
  }

  public static String getFormattedDate(LocalDateTime date) {
    return date.withNano(0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
  }


  public static String get12HourFormatTime(LocalDateTime date){
    DateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    return dateFormat.format(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()))
            .replace(UPPERCASE_AM, LOWERCASE_AM)
            .replace(UPPERCASE_PM, LOWERCASE_PM);
  }

  public static String getFormatLocalDate(LocalDateTime date){
    DateFormat dateFormat = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.getDefault());
    return dateFormat.format(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));
  }
}
