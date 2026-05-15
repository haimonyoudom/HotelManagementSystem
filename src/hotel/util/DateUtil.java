package hotel.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class DateUtil {

	private DateUtil() {
	}

	public static int calculateNights(String checkIn, String checkOut) {
		LocalDate in = LocalDate.parse(checkIn);
		LocalDate out = LocalDate.parse(checkOut);
		return (int) ChronoUnit.DAYS.between(in, out);
	}

	public static boolean isValidDate(String date) {
		try {
			LocalDate.parse(date);
			return true;
		} catch (DateTimeParseException | NullPointerException e) {
			return false;
		}
	}

	public static boolean isBefore(String date1, String date2) {
		LocalDate first = LocalDate.parse(date1);
		LocalDate second = LocalDate.parse(date2);
		return first.isBefore(second);
	}

	public static String today() {
		return LocalDate.now().toString();
	}
}
