package jhaushalt.service.factories;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import jhaushalt.domain.zeitraum.Datum;

public class DatumFactory {
	private static final DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMAN);
	
	public static Datum getInstance(DataSourceHolder in) throws IOException, ParseException {
		Datum datum = new Datum();
		datum.setTime(loadDateFromInputStream(in));
		return datum;
	}
	
	private static Date loadDateFromInputStream(final DataSourceHolder in) throws IOException, ParseException {
		return df.parse(in.getDataString());
	}
}
