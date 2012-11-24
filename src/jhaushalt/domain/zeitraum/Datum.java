package jhaushalt.domain.zeitraum;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.logging.Logger;

public class Datum implements Comparable<Datum>, Cloneable {
	final DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY);

	private final Logger logger = Logger.getLogger(Datum.class.getName());

	private Locale locale = Locale.GERMANY;
	private GregorianCalendar wert;

	public Datum() {
		this.wert = new GregorianCalendar();
	}

	public Datum(final int tag, final int monat, final int jahr) {
		this.wert = new GregorianCalendar(jahr, monat - 1, tag);
	}

	public Datum(String datumString) {
		this.wert = new GregorianCalendar();
		final String heute = df.format(this.wert.getTime());
		if (locale.getLanguage().equals("de") && (datumString.length() < heute.length())) {
			datumString = datumString + heute.substring(datumString.length());
		}
		try {
			this.wert.setTime(df.parse(datumString));
		}
		catch (final java.text.ParseException e) {}
	}

	@Override
	public String toString() {
		final DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		return df.format(this.wert.getTime());
	}

	public void setTime(Date time) {
		wert.setTime(time);
	}

	public Date getTime() {
		return wert.getTime();	
	}

	public long sub(final Datum datum) {
		return (this.wert.getTimeInMillis() - datum.wert.getTimeInMillis()) / 86400000L;
		// 1 Tag = 24 * 60 * 60 * 1000 = 86400000 ms
	}

	public void addiereTage(final int tage) {
		this.wert.add(Calendar.DAY_OF_MONTH, tage);
	}

	public int getTag() {
		return this.wert.get(Calendar.DAY_OF_MONTH);
	}

	public int getMonat() {
		return this.wert.get(Calendar.MONTH) + 1;
	}

	public int getJahr() {
		return this.wert.get(Calendar.YEAR);
	}

	// -- Methoden fuer Interface: Cloneable --------------------

	@Override
	public final Object clone() {
		Datum datum = new Datum();
		try {
			datum = (Datum) super.clone();
		} catch (final CloneNotSupportedException e) {
			logger.warning("Clone not works. This should never happen!");
		}
		datum.wert = (GregorianCalendar) this.wert.clone();
		return datum;
	}

	// -- Methoden fuer Interface: Comparable -------------------

	public int compareTo(final Datum datum) { 
		return this.wert.compareTo(datum.wert);
	}

	public boolean istImZeitraum(Zeitraum zeitraum) {
		boolean endCheck = zeitraum.getEndDatum().compareTo(this) > 0;
		boolean startCheck = zeitraum.getStartDatum().compareTo(this) <= 0;
		return  startCheck && endCheck;
	}

}