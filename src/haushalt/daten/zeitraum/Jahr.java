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

package haushalt.daten.zeitraum;

import haushalt.daten.Datum;

/**
 * @author Dr. Lars H. Hahn
 * @version 2.5.1/2008.03.10
 * @since 2.0
 */

/*
 * 2008.03.10 BugFix: Falsches Start/End-Datum im Rahmen der
 * Internationalisierung
 * 2004.08.22 Erste Version
 */

public class Jahr
		extends AbstractZeitraum {

	private int jahr = 2000;

	public Jahr(final int jahr) {
		this.jahr = jahr;
	}

	public Jahr(final String jahr) {
		try {
			this.jahr = Integer.parseInt(jahr);
		}
		catch (final Exception e) {
			// Fehler! -> Standardwerte.
		}
	}

	@Override
	public Datum getStartDatum() {
		return new Datum(1, 1, this.jahr);
	}

	@Override
	public Datum getEndDatum() {
		return new Datum(1, 1, this.jahr + 1);
	}

	@Override
	public AbstractZeitraum folgeZeitraum() {
		return new Jahr(this.jahr + 1);
	}

	@Override
	public String toString() {
		return "" + this.jahr;
	}

}