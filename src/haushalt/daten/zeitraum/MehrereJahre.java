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
 * @since 2.0.2
 */

/*
 * 2008.03.10 BugFix: Falsches Start/End-Datum im Rahmen der
 * Internationalisierung
 * 2005.03.10 Erste Version
 */

public class MehrereJahre
		extends AbstractZeitraum {

	private int startJahr = 2000;
	private int endJahr = 2004;

	public MehrereJahre(final int jahr1, final int jahr2) {
		this.startJahr = jahr1;
		this.endJahr = jahr2;
	}

	public MehrereJahre(final String text) {
		try {
			final StringTokenizer st = new StringTokenizer(text, "-");
			this.startJahr = Integer.parseInt(st.nextToken());
			this.endJahr = Integer.parseInt(st.nextToken());
		}
		catch (final Exception e) {
			// Fehler! -> Standardwerte.
		}
	}

	@Override
	public Datum getStartDatum() {
		return new Datum(1, 1, this.startJahr);
	}

	@Override
	public Datum getEndDatum() {
		return new Datum(31, 12, this.endJahr);
	}

	@Override
	public AbstractZeitraum folgeZeitraum() {
		final int delta = this.endJahr - this.startJahr + 1;
		return new MehrereJahre(this.startJahr + delta, this.endJahr + delta);
	}

	@Override
	public String toString() {
		return "" + this.startJahr + "-" + this.endJahr;
	}

}