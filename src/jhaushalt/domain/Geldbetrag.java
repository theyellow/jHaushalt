/*
 * This file is part of jHaushalt.
 * jHaushalt is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * jHaushalt is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with jHaushalt; if not, see <http://www.gnu.org/licenses/>.
 * (C)opyright 2002-2010 Dr. Lars H. Hahn
 */

package jhaushalt.domain;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Repräsentiert einen Geldbetrag.
 * Die Währung des Betrags ist in der Regel Euro, das Währungssymbol kann aber
 * geändert werden.
 * 
 * @author Dr. Lars H. Hahn
 * @version 2.6/2009.05.17
 */
public class Geldbetrag implements Cloneable, Comparable<Geldbetrag> {

	public static final Geldbetrag NULL_EURO = new Geldbetrag();
	private static final Logger LOGGER = Logger.getLogger(Geldbetrag.class.getName());
	final NumberFormat nf = setNumberFormat(Locale.GERMANY);

	private static String symbol = "€";
	private long wert = 0L;

	public Geldbetrag() { }

	public Geldbetrag(final double wert) {
		setWert(wert);
	}

	public Geldbetrag(String wert) {
		convertStringIntoLongValue(wert);
	}

	public long getBetrag() {
		return wert;
	}

	public double toDouble() {
		return this.wert / 100.0D;
	}

	private void setWert(final double wert) {
		final double help = wert * 100.0D;
		if (help < 0) {
			this.wert = (long) (help - 0.5D);
		}
		else {
			this.wert = (long) (help + 0.5D);
		}
	}

	public void umrechnenVonDM() { // Wenn der Wert in DM vorliegt -> umrechnen
									// in EURO
		if (this.wert < 0) {
			this.wert = (long) (this.wert / 1.95583D - 0.5D);
		}
		else {
			this.wert = (long) (this.wert / 1.95583D + 0.5D);
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj.getClass() != Geldbetrag.class) {
			return false;
		}
		return ((Geldbetrag) obj).wert == this.wert;
	}

	@Override
	public int hashCode() {
		assert false : "hashCode not designed";
		return 0;
	}

	/**
	 * Bildet die Summe aus diesem Euro-Objekt und dem Parameter.
	 * Die Summe wird einem neuen Objekt zu gewiesen. Um aufzusummieren sollte
	 * deshalb die Funktion <code>sum(Euro)</code> verwendet werden.
	 * 
	 * @see Geldbetrag#sum(Geldbetrag)
	 * @param euro
	 * @return Neues Objekt mit der Summe
	 */
	public Geldbetrag add(final Geldbetrag euro) {
		final Geldbetrag ergebnis = new Geldbetrag();
		ergebnis.wert = this.wert + euro.wert;
		return ergebnis;
	}

	public Geldbetrag sub(final Geldbetrag euro) {
		final Geldbetrag ergebnis = new Geldbetrag();
		ergebnis.wert = this.wert - euro.wert;
		return ergebnis;
	}

	public Geldbetrag durch(final int zahl) {
		final Geldbetrag ergebnis = new Geldbetrag();
		ergebnis.wert = this.wert / zahl;
		return ergebnis;
	}

	public Geldbetrag mal(final double zahl) {
		final Geldbetrag ergebnis = new Geldbetrag();
		ergebnis.setWert(toDouble() * zahl);
		return ergebnis;
	}

	public void sum(final Geldbetrag euro) {
		this.wert += euro.wert;
	}

	// -- Methoden fuer Interface: Cloneable --------------------

	@Override
	public final Object clone() {
		Geldbetrag kopie = new Geldbetrag();
		try {
			kopie = (Geldbetrag) super.clone();
		} catch (final CloneNotSupportedException e) {
			LOGGER.warning("Cloning error. This should never happen.");
		}
		kopie.wert = this.wert;
		return kopie;
	}

	// -- Methoden fuer Interface: Comparable -------------------
	public int compareTo(final Geldbetrag euro) {
		if (this.wert < euro.wert) {
			return -1;
		}
		if (this.wert > euro.wert) {
			return 1;
		}
		return 0;
	}
	
	@Override
	public String toString() {
		return nf.format(this.wert / 100.0D) + " " + symbol;
	}

	private void convertStringIntoLongValue(String wert) {
		if (!"".equals(wert)) {
			try {
				if (wert.trim().startsWith("+")) {
					wert = wert.trim().substring(1).trim();
				}
				setWert(nf.parse(wert).doubleValue());
			}
			catch (final ParseException e) {
				LOGGER.warning("Error while parsing string: " + wert);
			}
		}
		else {
			this.wert = 0L;
		}
	}

	private NumberFormat setNumberFormat(final Locale locale) {
		final NumberFormat nf = NumberFormat.getInstance(locale);
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		return nf;
	}

}