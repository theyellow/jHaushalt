/*
 * This file is part of jHaushalt.
 * jHaushalt is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * jHaushalt is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with jHaushalt; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * (C)opyright 2002-2010 Dr. Lars H. Hahn
 */
package jhaushalt.domain.zeitraum;

import java.util.StringTokenizer;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.6/2009.08.05
 */

/*
 * 2009.08.05 BugFix: Korrekter Folgezeitraum
 * 2004.08.22 Version 2.0
 */
public class FreierZeitraum
		extends Zeitraum {

	private Datum startDatum = new Datum();
	private Datum endDatum = new Datum();

	public FreierZeitraum(final int tag1, final int monat1, final int jahr1, final int tag2, final int monat2,
			final int jahr2) {
		this.startDatum = new Datum(tag1, monat1, jahr1);
		this.endDatum = new Datum(tag2, monat2, jahr2);
	}

	public FreierZeitraum(final Datum startDatum, final Datum endDatum) {
		this.startDatum = startDatum;
		this.endDatum = endDatum;
	}

	public FreierZeitraum(final String text) {
		try {
			final StringTokenizer st = new StringTokenizer(text, "-");
			this.startDatum = new Datum(st.nextToken());
			this.endDatum = new Datum(st.nextToken());
		}
		catch (final Exception e) {
			// Fehler! -> Standardwerte.
		}
	}

	/**
	 * @see haushalt.daten.zeitraum.AbstractZeitraum#getStartDatum()
	 */
	@Override
	public Datum getStartDatum() {
		return this.startDatum;
	}

	/**
	 * @see haushalt.daten.zeitraum.AbstractZeitraum#getEndDatum()
	 */
	@Override
	public Datum getEndDatum() {
		return this.endDatum;
	}

	/**
	 * @see haushalt.daten.zeitraum.AbstractZeitraum#folgeZeitraum()
	 */
	@Override
	public Zeitraum folgeZeitraum() {
		final Datum neuesStartDatum = (Datum) this.endDatum.clone();
		final Datum neuesEndDatum = (Datum) this.endDatum.clone();
		neuesEndDatum.addiereTage(getAnzahlTage());
		return new FreierZeitraum(neuesStartDatum, neuesEndDatum);
	}

	@Override
	public String toString() {
		return this.startDatum + "-" + this.endDatum;
	}

}