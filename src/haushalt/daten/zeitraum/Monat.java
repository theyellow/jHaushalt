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

import java.util.StringTokenizer;

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

public class Monat extends AbstractZeitraum {

	private int monat = 1;
	private int jahr = 2000;

	public Monat(final int monat, final int jahr) {
		this.monat = monat;
		this.jahr = jahr;
	}

	public Monat(final String text) {
		try {
			final StringTokenizer st = new StringTokenizer(text, "/");
			this.monat = Integer.parseInt(st.nextToken());
			this.jahr = Integer.parseInt(st.nextToken());
			if (this.monat > 12) {
				this.monat = 12;
			}
		}
		catch (final Exception e) {
			// Fehler! -> Standardwerte.
		}
	}

	@Override
	public Datum getStartDatum() {
		return getMonatsBeginn(this.monat, this.jahr);
	}

	@Override
	public Datum getEndDatum() {
		if (this.monat == 12) {
			return getMonatsBeginn(1, this.jahr + 1);
		}
		return getMonatsBeginn(this.monat + 1, this.jahr);
	}

	@Override
	public AbstractZeitraum folgeZeitraum() {
		if (this.monat == 12) {
			return new Monat(1, this.jahr + 1);
		}
		return new Monat(this.monat + 1, this.jahr);
	}

	private static Datum getMonatsBeginn(final int monat, final int jahr) {
		return new Datum(1, monat, jahr);
	}

	@Override
	public String toString() {
		return "" + this.monat + "/" + this.jahr;
	}

}