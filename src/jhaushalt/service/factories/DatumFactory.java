package jhaushalt.service.factories;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import jhaushalt.domain.zeitraum.Datum;
import jhaushalt.service.factories.io.DataInputFacade;

public class DatumFactory {
	private static final DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMAN);
	
	public static Datum getInstance(DataInputFacade in) throws IOException, ParseException {
		Datum datum = new Datum();
		datum.setTime(loadDateFromInputStream(in));
		return datum;
	}
	
	private static Date loadDateFromInputStream(final DataInputFacade in) throws IOException, ParseException {
		return df.parse(in.getDataString());
	}
}
