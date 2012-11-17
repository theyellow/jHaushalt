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
 * @version 2.5.1/2008.03.10
 * @since 2.0
 */

/*
 * 2008.03.10 BugFix: Falsches Start/End-Datum im Rahmen der
 * Internationalisierung
 * 2004.08.22 Erste Version
 */

public class Quartal extends Zeitraum {

	private int quartal = 1;
	private int jahr = 2000;

	public Quartal(final int quartal, final int jahr) {
		this.quartal = quartal;
		this.jahr = jahr;
	}

	public Quartal(final String text) {
		try {
			final StringTokenizer st = new StringTokenizer(text, "/");
			this.quartal = Integer.parseInt(st.nextToken());
			this.jahr = Integer.parseInt(st.nextToken());
			if (this.quartal > 4) {
				this.quartal = 4;
			}
		} catch (final Exception e) {
			// Fehler! -> Standardwerte.
		}
	}

	@Override
	public Datum getStartDatum() {
		return getQuartalsBeginn(this.quartal, this.jahr);
	}

	@Override
	public Datum getEndDatum() {
		if (this.quartal == 4) {
			return getQuartalsBeginn(1, this.jahr + 1);
		}
		return getQuartalsBeginn(this.quartal + 1, this.jahr);
	}

	@Override
	public Zeitraum folgeZeitraum() {
		if (this.quartal == 4) {
			return new Quartal(1, this.jahr + 1);
		}
		return new Quartal(this.quartal + 1, this.jahr);
	}

	private static Datum getQuartalsBeginn(final int quartal, final int jahr) {
		int monat = 1;
		switch (quartal) {
			case 1:
				monat = 1;
				break;
			case 2:
				monat = 4;
				break;
			case 3:
				monat = 7;
				break;
			case 4:
				monat = 10;
				break;
			default:
				break;
		}
		return new Datum(1, monat, jahr);
	}

	@Override
	public String toString() {
		return "" + this.quartal + "/" + this.jahr;
	}

}
