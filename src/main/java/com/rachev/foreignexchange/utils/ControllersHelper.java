package com.rachev.foreignexchange.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public final class ControllersHelper {

  private ControllersHelper() {
  }

  public static boolean isDoubleParsable(final String doubleString) {
    try {
      Double.parseDouble(doubleString);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  public static boolean isDateParsable(final String date) {
    try {
      new SimpleDateFormat("yyyy-MM-dd").parse(date);
      return true;
    } catch (ParseException e) {
      return false;
    }
  }

  public static String getPreviousDayAsString(final String date) {
    try {
      isDateParsable(date);
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
      Instant instant = simpleDateFormat.parse(date).toInstant();
      Date prevDate = Date.from(instant.minus(1, ChronoUnit.DAYS));
      return simpleDateFormat.format(prevDate);
    } catch (ParseException e) {
      return null;
    }
  }
}
